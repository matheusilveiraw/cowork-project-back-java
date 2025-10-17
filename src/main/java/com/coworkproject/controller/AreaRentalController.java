package com.coworkproject.controller;

import com.coworkproject.dto.AreaRentalRequest;
import com.coworkproject.model.Area;
import com.coworkproject.model.AreaRental;
import com.coworkproject.model.Customer;
import com.coworkproject.repository.AreaRentalRepository;
import com.coworkproject.repository.AreaRepository;
import com.coworkproject.repository.CustomerRepository;
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
@RequestMapping("/api/area-rentals")
public class AreaRentalController {

    private final AreaRentalRepository repository;
    private final AreaRepository areaRepository;
    private final CustomerRepository customerRepository;

    public AreaRentalController(AreaRentalRepository repository,
                                AreaRepository areaRepository,
                                CustomerRepository customerRepository) {
        this.repository = repository;
        this.areaRepository = areaRepository;
        this.customerRepository = customerRepository;
    }

    // GET ALL - Buscar todos os aluguéis OU filtrar por área
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAreaRentals(@RequestParam(required = false) Integer areaId) {
        List<AreaRental> rentals;

        if (areaId != null) {
            // Se areaId foi fornecido, filtra por área
            rentals = repository.findByAreaIdAreas(areaId);
        } else {
            // Se não, retorna todos os aluguéis
            rentals = repository.findAll();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ?
                (areaId != null ? "Nenhum aluguel encontrado para esta área" : "Nenhum aluguel encontrado")
                : "Aluguéis de áreas recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar aluguel por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAreaRentalById(@PathVariable Integer id) {
        Optional<AreaRental> rental = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (rental.isPresent()) {
            response.put("success", true);
            response.put("message", "Aluguel de área encontrado com sucesso");
            response.put("data", rental.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Aluguel de área não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar novo aluguel
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAreaRental(@RequestBody AreaRentalRequest rentalRequest) {
        try {
            // Validações das entidades relacionadas
            Optional<Area> area = areaRepository.findById(rentalRequest.getIdAreas());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());

            if (area.isEmpty()) {
                return createErrorResponse("Área não encontrada com ID: " + rentalRequest.getIdAreas(), HttpStatus.NOT_FOUND);
            }

            if (customer.isEmpty()) {
                return createErrorResponse("Cliente não encontrado com ID: " + rentalRequest.getIdCustomers(), HttpStatus.NOT_FOUND);
            }

            // Validação de datas
            if (rentalRequest.getStartPeriodAreaRentals() == null || rentalRequest.getEndPeriodAreaRentals() == null) {
                return createErrorResponse("Datas de início e término são obrigatórias", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodAreaRentals().isAfter(rentalRequest.getEndPeriodAreaRentals())) {
                return createErrorResponse("Data de início não pode ser após a data de término", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodAreaRentals().isBefore(LocalDateTime.now().toLocalDate().atStartOfDay())) {
                return createErrorResponse("Data de início não pode ser no passado", HttpStatus.BAD_REQUEST);
            }

            // Verificar conflitos de horário
            List<AreaRental> conflictingRentals = repository.findConflictingRentals(
                    rentalRequest.getIdAreas(),
                    rentalRequest.getStartPeriodAreaRentals(),
                    rentalRequest.getEndPeriodAreaRentals()
            );

            if (!conflictingRentals.isEmpty()) {
                return createErrorResponse("A área já está alugada neste período", HttpStatus.CONFLICT);
            }

            // Validação de preço
            if (rentalRequest.getTotalPriceStandRentals() == null || rentalRequest.getTotalPriceStandRentals().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("Preço total deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Criar o aluguel
            AreaRental areaRental = new AreaRental();
            areaRental.setArea(area.get());
            areaRental.setCustomer(customer.get());
            areaRental.setStartPeriodAreaRentals(rentalRequest.getStartPeriodAreaRentals());
            areaRental.setEndPeriodAreaRentals(rentalRequest.getEndPeriodAreaRentals());
            areaRental.setTotalPriceStandRentals(rentalRequest.getTotalPriceStandRentals());

            AreaRental savedRental = repository.save(areaRental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Área alugada com sucesso!");
            response.put("data", savedRental);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao criar aluguel de área: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar aluguel completo
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAreaRental(@PathVariable Integer id, @RequestBody AreaRentalRequest rentalRequest) {
        Optional<AreaRental> optionalRental = repository.findById(id);

        if (optionalRental.isEmpty()) {
            return createErrorResponse("Aluguel de área não encontrado com ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {
            AreaRental rental = optionalRental.get();

            // Validações das entidades relacionadas
            Optional<Area> area = areaRepository.findById(rentalRequest.getIdAreas());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());

            if (area.isEmpty()) {
                return createErrorResponse("Área não encontrada com ID: " + rentalRequest.getIdAreas(), HttpStatus.NOT_FOUND);
            }

            if (customer.isEmpty()) {
                return createErrorResponse("Cliente não encontrado com ID: " + rentalRequest.getIdCustomers(), HttpStatus.NOT_FOUND);
            }

            // Validação de datas
            if (rentalRequest.getStartPeriodAreaRentals().isAfter(rentalRequest.getEndPeriodAreaRentals())) {
                return createErrorResponse("Data de início não pode ser após a data de término", HttpStatus.BAD_REQUEST);
            }

            // Verificar conflitos de horário (excluindo o próprio aluguel)
            List<AreaRental> conflictingRentals = repository.findConflictingRentals(
                    rentalRequest.getIdAreas(),
                    rentalRequest.getStartPeriodAreaRentals(),
                    rentalRequest.getEndPeriodAreaRentals()
            ).stream().filter(r -> !r.getIdAreaRentals().equals(id)).toList();

            if (!conflictingRentals.isEmpty()) {
                return createErrorResponse("A área já está alugada neste período", HttpStatus.CONFLICT);
            }

            // Atualizar o aluguel
            rental.setArea(area.get());
            rental.setCustomer(customer.get());
            rental.setStartPeriodAreaRentals(rentalRequest.getStartPeriodAreaRentals());
            rental.setEndPeriodAreaRentals(rentalRequest.getEndPeriodAreaRentals());
            rental.setTotalPriceStandRentals(rentalRequest.getTotalPriceStandRentals());

            AreaRental updatedRental = repository.save(rental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aluguel de área atualizado com sucesso!");
            response.put("data", updatedRental);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao atualizar aluguel de área: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE - Deletar aluguel
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAreaRental(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Aluguel de área deletado com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Aluguel de área não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY CUSTOMER - Buscar aluguéis por cliente
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getAreaRentalsByCustomer(@PathVariable Integer customerId) {
        List<AreaRental> rentals = repository.findByCustomerIdCustomers(customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel de área encontrado para este cliente" : "Aluguéis de áreas do cliente recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET BY AREA - Buscar aluguéis por área (endpoint alternativo)
    @GetMapping("/area/{areaId}")
    public ResponseEntity<Map<String, Object>> getAreaRentalsByArea(@PathVariable Integer areaId) {
        List<AreaRental> rentals = repository.findByAreaIdAreas(areaId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel encontrado para esta área" : "Aluguéis da área recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET ACTIVE - Buscar aluguéis ativos
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveAreaRentals() {
        List<AreaRental> activeRentals = repository.findActiveRentals(LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", activeRentals.isEmpty() ? "Nenhum aluguel de área ativo no momento" : "Aluguéis de áreas ativos recuperados com sucesso");
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