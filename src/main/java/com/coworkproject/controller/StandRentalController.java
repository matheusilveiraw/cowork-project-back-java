package com.coworkproject.controller;

import com.coworkproject.dto.StandRentalRequest;
import com.coworkproject.model.*;
import com.coworkproject.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/stand-rentals")
public class StandRentalController {

    private final StandRentalRepository repository;
    private final StandRepository standRepository;
    private final CustomerRepository customerRepository;
    private final RentalPlanRepository rentalPlanRepository;

    public StandRentalController(StandRentalRepository repository,
                                 StandRepository standRepository,
                                 CustomerRepository customerRepository,
                                 RentalPlanRepository rentalPlanRepository) {
        this.repository = repository;
        this.standRepository = standRepository;
        this.customerRepository = customerRepository;
        this.rentalPlanRepository = rentalPlanRepository;
    }

    // GET ALL - Buscar todos os aluguéis OU filtrar por estante
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStandRentals(@RequestParam(required = false) Integer standId) {
        List<StandRental> rentals;

        if (standId != null) {
            // Se standId foi fornecido, filtra por estante
            rentals = repository.findByStandIdStands(standId);
        } else {
            // Se não, retorna todos os aluguéis
            rentals = repository.findAll();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ?
                (standId != null ? "Nenhum aluguel encontrado para esta estante" : "Nenhum aluguel encontrado")
                : "Aluguéis de estantes recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar aluguel por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStandRentalById(@PathVariable Integer id) {
        Optional<StandRental> rental = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (rental.isPresent()) {
            response.put("success", true);
            response.put("message", "Aluguel de estante encontrado com sucesso");
            response.put("data", rental.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Aluguel de estante não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar novo aluguel
    @PostMapping
    public ResponseEntity<Map<String, Object>> createStandRental(@RequestBody StandRentalRequest rentalRequest) {
        try {
            // Validações das entidades relacionadas
            Optional<Stand> stand = standRepository.findById(rentalRequest.getIdStands());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());
            Optional<RentalPlan> rentalPlan = rentalPlanRepository.findById(rentalRequest.getIdRentalPlans());

            if (stand.isEmpty()) {
                return createErrorResponse("Estante não encontrada com ID: " + rentalRequest.getIdStands(), HttpStatus.NOT_FOUND);
            }

            if (customer.isEmpty()) {
                return createErrorResponse("Cliente não encontrado com ID: " + rentalRequest.getIdCustomers(), HttpStatus.NOT_FOUND);
            }

            if (rentalPlan.isEmpty()) {
                return createErrorResponse("Plano de aluguel não encontrado com ID: " + rentalRequest.getIdRentalPlans(), HttpStatus.NOT_FOUND);
            }

            // Validação de datas
            if (rentalRequest.getStartPeriodStandRentals() == null || rentalRequest.getEndPeriodStandRentals() == null) {
                return createErrorResponse("Datas de início e término são obrigatórias", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodStandRentals().isAfter(rentalRequest.getEndPeriodStandRentals())) {
                return createErrorResponse("Data de início não pode ser após a data de término", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodStandRentals().isBefore(LocalDateTime.now().toLocalDate().atStartOfDay())) {
                return createErrorResponse("Data de início não pode ser no passado", HttpStatus.BAD_REQUEST);
            }

            // Verificar conflitos de horário
            List<StandRental> conflictingRentals = repository.findConflictingRentals(
                    rentalRequest.getIdStands(),
                    rentalRequest.getStartPeriodStandRentals(),
                    rentalRequest.getEndPeriodStandRentals()
            );

            if (!conflictingRentals.isEmpty()) {
                return createErrorResponse("A estante já está alugada neste período", HttpStatus.CONFLICT);
            }

            // Validação de preço
            if (rentalRequest.getTotalPriceStandRentals() == null || rentalRequest.getTotalPriceStandRentals().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("Preço total deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Criar o aluguel
            StandRental standRental = new StandRental();
            standRental.setStand(stand.get());
            standRental.setCustomer(customer.get());
            standRental.setRentalPlan(rentalPlan.get());
            standRental.setStartPeriodStandRentals(rentalRequest.getStartPeriodStandRentals());
            standRental.setEndPeriodStandRentals(rentalRequest.getEndPeriodStandRentals());
            standRental.setTotalPriceStandRentals(rentalRequest.getTotalPriceStandRentals());

            StandRental savedRental = repository.save(standRental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estante alugada com sucesso!");
            response.put("data", savedRental);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao criar aluguel de estante: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar aluguel completo
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStandRental(@PathVariable Integer id, @RequestBody StandRentalRequest rentalRequest) {
        Optional<StandRental> optionalRental = repository.findById(id);

        if (optionalRental.isEmpty()) {
            return createErrorResponse("Aluguel de estante não encontrado com ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {
            StandRental rental = optionalRental.get();

            // Validações das entidades relacionadas
            Optional<Stand> stand = standRepository.findById(rentalRequest.getIdStands());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());
            Optional<RentalPlan> rentalPlan = rentalPlanRepository.findById(rentalRequest.getIdRentalPlans());

            if (stand.isEmpty()) {
                return createErrorResponse("Estante não encontrada com ID: " + rentalRequest.getIdStands(), HttpStatus.NOT_FOUND);
            }

            if (customer.isEmpty()) {
                return createErrorResponse("Cliente não encontrado com ID: " + rentalRequest.getIdCustomers(), HttpStatus.NOT_FOUND);
            }

            if (rentalPlan.isEmpty()) {
                return createErrorResponse("Plano de aluguel não encontrado com ID: " + rentalRequest.getIdRentalPlans(), HttpStatus.NOT_FOUND);
            }

            // Validação de datas
            if (rentalRequest.getStartPeriodStandRentals().isAfter(rentalRequest.getEndPeriodStandRentals())) {
                return createErrorResponse("Data de início não pode ser após a data de término", HttpStatus.BAD_REQUEST);
            }

            // Verificar conflitos de horário (excluindo o próprio aluguel)
            List<StandRental> conflictingRentals = repository.findConflictingRentals(
                    rentalRequest.getIdStands(),
                    rentalRequest.getStartPeriodStandRentals(),
                    rentalRequest.getEndPeriodStandRentals()
            ).stream().filter(r -> !r.getIdStandRentals().equals(id)).toList();

            if (!conflictingRentals.isEmpty()) {
                return createErrorResponse("A estante já está alugada neste período", HttpStatus.CONFLICT);
            }

            // Atualizar o aluguel
            rental.setStand(stand.get());
            rental.setCustomer(customer.get());
            rental.setRentalPlan(rentalPlan.get());
            rental.setStartPeriodStandRentals(rentalRequest.getStartPeriodStandRentals());
            rental.setEndPeriodStandRentals(rentalRequest.getEndPeriodStandRentals());
            rental.setTotalPriceStandRentals(rentalRequest.getTotalPriceStandRentals());

            StandRental updatedRental = repository.save(rental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aluguel de estante atualizado com sucesso!");
            response.put("data", updatedRental);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao atualizar aluguel de estante: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE - Deletar aluguel
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStandRental(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Aluguel de estante deletado com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Aluguel de estante não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY CUSTOMER - Buscar aluguéis por cliente
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getStandRentalsByCustomer(@PathVariable Integer customerId) {
        List<StandRental> rentals = repository.findByCustomerIdCustomers(customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel de estante encontrado para este cliente" : "Aluguéis de estantes do cliente recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET BY STAND - Buscar aluguéis por estante (endpoint alternativo)
    @GetMapping("/stand/{standId}")
    public ResponseEntity<Map<String, Object>> getStandRentalsByStand(@PathVariable Integer standId) {
        List<StandRental> rentals = repository.findByStandIdStands(standId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel encontrado para esta estante" : "Aluguéis da estante recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET ACTIVE - Buscar aluguéis ativos
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveStandRentals() {
        List<StandRental> activeRentals = repository.findActiveRentals(LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", activeRentals.isEmpty() ? "Nenhum aluguel de estante ativo no momento" : "Aluguéis de estantes ativos recuperados com sucesso");
        response.put("data", activeRentals);
        response.put("count", activeRentals.size());

        return ResponseEntity.ok(response);
    }

    // Método auxiliar para criar respostas de erro
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("data", null);
        return ResponseEntity.status(status).body(errorResponse);
    }
}