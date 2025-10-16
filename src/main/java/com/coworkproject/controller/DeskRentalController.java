package com.coworkproject.controller;

import com.coworkproject.dto.DeskRentalRequest;
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
@RequestMapping("/api/desk-rentals")
public class DeskRentalController {

    private final DeskRentalRepository repository;
    private final DeskRepository deskRepository;
    private final CustomerRepository customerRepository;
    private final RentalPlanRepository rentalPlanRepository;

    public DeskRentalController(DeskRentalRepository repository,
                                DeskRepository deskRepository,
                                CustomerRepository customerRepository,
                                RentalPlanRepository rentalPlanRepository) {
        this.repository = repository;
        this.deskRepository = deskRepository;
        this.customerRepository = customerRepository;
        this.rentalPlanRepository = rentalPlanRepository;
    }

    // GET ALL - Buscar todos os aluguéis OU filtrar por mesa
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDeskRentals(@RequestParam(required = false) Integer deskId) {
        List<DeskRental> rentals;

        if (deskId != null) {
            // Se deskId foi fornecido, filtra por mesa
            rentals = repository.findByDeskIdDesks(deskId);
        } else {
            // Se não, retorna todos os aluguéis
            rentals = repository.findAll();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ?
                (deskId != null ? "Nenhum aluguel encontrado para esta mesa" : "Nenhum aluguel encontrado")
                : "Aluguéis recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar aluguel por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDeskRentalById(@PathVariable Integer id) {
        Optional<DeskRental> rental = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (rental.isPresent()) {
            response.put("success", true);
            response.put("message", "Aluguel encontrado com sucesso");
            response.put("data", rental.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Aluguel não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar novo aluguel
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDeskRental(@RequestBody DeskRentalRequest rentalRequest) {
        try {
            // Validações das entidades relacionadas
            Optional<Desk> desk = deskRepository.findById(rentalRequest.getIdDesks());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());
            Optional<RentalPlan> rentalPlan = rentalPlanRepository.findById(rentalRequest.getIdRentalPlans());

            if (desk.isEmpty()) {
                return createErrorResponse("Mesa não encontrada com ID: " + rentalRequest.getIdDesks(), HttpStatus.NOT_FOUND);
            }

            if (customer.isEmpty()) {
                return createErrorResponse("Cliente não encontrado com ID: " + rentalRequest.getIdCustomers(), HttpStatus.NOT_FOUND);
            }

            if (rentalPlan.isEmpty()) {
                return createErrorResponse("Plano de aluguel não encontrado com ID: " + rentalRequest.getIdRentalPlans(), HttpStatus.NOT_FOUND);
            }

            // Validação de datas
            if (rentalRequest.getStartPeriodDeskRentals() == null || rentalRequest.getEndPeriodDeskRentals() == null) {
                return createErrorResponse("Datas de início e término são obrigatórias", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodDeskRentals().isAfter(rentalRequest.getEndPeriodDeskRentals())) {
                return createErrorResponse("Data de início não pode ser após a data de término", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodDeskRentals().isBefore(LocalDateTime.now().toLocalDate().atStartOfDay())) {
                return createErrorResponse("Data de início não pode ser no passado", HttpStatus.BAD_REQUEST);
            }

            // Verificar conflitos de horário
            List<DeskRental> conflictingRentals = repository.findConflictingRentals(
                    rentalRequest.getIdDesks(),
                    rentalRequest.getStartPeriodDeskRentals(),
                    rentalRequest.getEndPeriodDeskRentals()
            );

            if (!conflictingRentals.isEmpty()) {
                return createErrorResponse("A mesa já está alugada neste período", HttpStatus.CONFLICT);
            }

            // Validação de preço
            if (rentalRequest.getTotalPriceDeskRentals() == null || rentalRequest.getTotalPriceDeskRentals().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("Preço total deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Criar o aluguel
            DeskRental deskRental = new DeskRental();
            deskRental.setDesk(desk.get());
            deskRental.setCustomer(customer.get());
            deskRental.setRentalPlan(rentalPlan.get());
            deskRental.setStartPeriodDeskRentals(rentalRequest.getStartPeriodDeskRentals());
            deskRental.setEndPeriodDeskRentals(rentalRequest.getEndPeriodDeskRentals());
            deskRental.setTotalPriceDeskRentals(rentalRequest.getTotalPriceDeskRentals());

            DeskRental savedRental = repository.save(deskRental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mesa alugada com sucesso!");
            response.put("data", savedRental);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao criar aluguel: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar aluguel completo
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDeskRental(@PathVariable Integer id, @RequestBody DeskRentalRequest rentalRequest) {
        Optional<DeskRental> optionalRental = repository.findById(id);

        if (optionalRental.isEmpty()) {
            return createErrorResponse("Aluguel não encontrado com ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {
            DeskRental rental = optionalRental.get();

            // Validações das entidades relacionadas
            Optional<Desk> desk = deskRepository.findById(rentalRequest.getIdDesks());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());
            Optional<RentalPlan> rentalPlan = rentalPlanRepository.findById(rentalRequest.getIdRentalPlans());

            if (desk.isEmpty()) {
                return createErrorResponse("Mesa não encontrada com ID: " + rentalRequest.getIdDesks(), HttpStatus.NOT_FOUND);
            }

            if (customer.isEmpty()) {
                return createErrorResponse("Cliente não encontrado com ID: " + rentalRequest.getIdCustomers(), HttpStatus.NOT_FOUND);
            }

            if (rentalPlan.isEmpty()) {
                return createErrorResponse("Plano de aluguel não encontrado com ID: " + rentalRequest.getIdRentalPlans(), HttpStatus.NOT_FOUND);
            }

            // Validação de datas
            if (rentalRequest.getStartPeriodDeskRentals().isAfter(rentalRequest.getEndPeriodDeskRentals())) {
                return createErrorResponse("Data de início não pode ser após a data de término", HttpStatus.BAD_REQUEST);
            }

            // Verificar conflitos de horário (excluindo o próprio aluguel)
            List<DeskRental> conflictingRentals = repository.findConflictingRentals(
                    rentalRequest.getIdDesks(),
                    rentalRequest.getStartPeriodDeskRentals(),
                    rentalRequest.getEndPeriodDeskRentals()
            ).stream().filter(r -> !r.getIdDeskRentals().equals(id)).toList();

            if (!conflictingRentals.isEmpty()) {
                return createErrorResponse("A mesa já está alugada neste período", HttpStatus.CONFLICT);
            }

            // Atualizar o aluguel
            rental.setDesk(desk.get());
            rental.setCustomer(customer.get());
            rental.setRentalPlan(rentalPlan.get());
            rental.setStartPeriodDeskRentals(rentalRequest.getStartPeriodDeskRentals());
            rental.setEndPeriodDeskRentals(rentalRequest.getEndPeriodDeskRentals());
            rental.setTotalPriceDeskRentals(rentalRequest.getTotalPriceDeskRentals());

            DeskRental updatedRental = repository.save(rental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aluguel atualizado com sucesso!");
            response.put("data", updatedRental);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao atualizar aluguel: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE - Deletar aluguel
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDeskRental(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Aluguel deletado com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Aluguel não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY CUSTOMER - Buscar aluguéis por cliente
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getDeskRentalsByCustomer(@PathVariable Integer customerId) {
        List<DeskRental> rentals = repository.findByCustomerIdCustomers(customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel encontrado para este cliente" : "Aluguéis do cliente recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET BY DESK - Buscar aluguéis por mesa (endpoint alternativo)
    @GetMapping("/desk/{deskId}")
    public ResponseEntity<Map<String, Object>> getDeskRentalsByDesk(@PathVariable Integer deskId) {
        List<DeskRental> rentals = repository.findByDeskIdDesks(deskId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel encontrado para esta mesa" : "Aluguéis da mesa recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET ACTIVE - Buscar aluguéis ativos
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveDeskRentals() {
        List<DeskRental> activeRentals = repository.findActiveRentals(LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", activeRentals.isEmpty() ? "Nenhum aluguel ativo no momento" : "Aluguéis ativos recuperados com sucesso");
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