package com.coworkproject.controller;

import com.coworkproject.dto.DeskRentalRequest;
import com.coworkproject.model.*;
import com.coworkproject.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final RentalCategoryRepository rentalCategoryRepository;
    private final RentalShiftRepository rentalShiftRepository;

    public DeskRentalController(DeskRentalRepository repository,
                                DeskRepository deskRepository,
                                CustomerRepository customerRepository,
                                RentalPlanRepository rentalPlanRepository,
                                RentalCategoryRepository rentalCategoryRepository,
                                RentalShiftRepository rentalShiftRepository) {
        this.repository = repository;
        this.deskRepository = deskRepository;
        this.customerRepository = customerRepository;
        this.rentalPlanRepository = rentalPlanRepository;
        this.rentalCategoryRepository = rentalCategoryRepository;
        this.rentalShiftRepository = rentalShiftRepository;
    }

    // POST - Criar novo aluguel (MÉTODO COMPLETAMENTE CORRIGIDO)
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

            // Validação de data de início
            if (rentalRequest.getStartPeriodDeskRentals() == null) {
                return createErrorResponse("Data de início é obrigatória", HttpStatus.BAD_REQUEST);
            }

            // Verificar se o plano tem turno
            if (rentalPlan.get().getRentalShift() == null) {
                return createErrorResponse("Plano de aluguel não possui turno definido", HttpStatus.BAD_REQUEST);
            }

            // Buscar a categoria do plano para calcular a data de término
            RentalCategory rentalCategory = rentalPlan.get().getRentalCategory();
            if (rentalCategory == null) {
                return createErrorResponse("Categoria do plano de aluguel não encontrada", HttpStatus.NOT_FOUND);
            }

            // Calcular data de término baseada na duração da categoria
            int durationInDays = rentalCategory.getBaseDurationInDaysRentalCategories();
            LocalDateTime startDate = rentalRequest.getStartPeriodDeskRentals();

            // Ajustar para o horário final considerando o turno
            LocalTime startTime = rentalPlan.get().getRentalShift().getStartTimeRentalShifts();
            LocalTime endTime = rentalPlan.get().getRentalShift().getEndTimeRentalShifts();

            // Aplicar horário de início do turno
            startDate = startDate.with(startTime);
            // Calcular data final com horário do turno
            LocalDateTime endDate = startDate.plusDays(durationInDays - 1).with(endTime);

            System.out.println("=== VERIFICAÇÃO DE CONFLITOS POR HORÁRIO ===");
            System.out.println("Mesa: " + rentalRequest.getIdDesks());
            System.out.println("Turno: " + rentalPlan.get().getRentalShift().getNameRentalShifts());
            System.out.println("Horário: " + startTime + " às " + endTime);
            System.out.println("Novo aluguel - Início: " + startDate + " | Fim: " + endDate);

            // VERIFICAÇÃO DE CONFLITOS BASEADA EM HORÁRIOS
            List<DeskRental> conflictingRentals = repository.findTimeRangeConflicts(
                    rentalRequest.getIdDesks(),
                    startDate,
                    endDate
            );

            if (!conflictingRentals.isEmpty()) {
                System.out.println("CONFLITOS ENCONTRADOS: " + conflictingRentals.size());

                // Verificar se os conflitos são reais (sobreposição de horários)
                for (DeskRental conflict : conflictingRentals) {
                    boolean hasRealConflict = checkTimeOverlap(
                            rentalPlan.get().getRentalShift(),
                            conflict.getRentalPlan().getRentalShift()
                    );

                    if (hasRealConflict) {
                        System.out.println("CONFLITO REAL ENCONTRADO:");
                        System.out.println("Aluguel ID: " + conflict.getIdDeskRentals());
                        System.out.println("Turno conflitante: " + conflict.getRentalPlan().getRentalShift().getNameRentalShifts());
                        System.out.println("Horário conflitante: " + conflict.getRentalPlan().getRentalShift().getStartTimeRentalShifts() +
                                " às " + conflict.getRentalPlan().getRentalShift().getEndTimeRentalShifts());
                        System.out.println("Período: " + conflict.getStartPeriodDeskRentals() + " até " + conflict.getEndPeriodDeskRentals());

                        String conflictMessage = buildConflictMessageByTime(
                                rentalPlan.get().getRentalShift(),
                                conflict.getRentalPlan().getRentalShift()
                        );
                        return createErrorResponse(conflictMessage, HttpStatus.CONFLICT);
                    } else {
                        System.out.println("Conflito de datas mas SEM conflito de horários - PERMITIDO");
                    }
                }
            }

            System.out.println("NENHUM CONFLITO REAL ENCONTRADO - CRIANDO ALUGUEL");

            // Validação de preço
            if (rentalRequest.getTotalPriceDeskRentals() == null || rentalRequest.getTotalPriceDeskRentals().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("Preço total deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Criar o aluguel
            DeskRental deskRental = new DeskRental();
            deskRental.setDesk(desk.get());
            deskRental.setCustomer(customer.get());
            deskRental.setRentalPlan(rentalPlan.get());
            deskRental.setStartPeriodDeskRentals(startDate);
            deskRental.setEndPeriodDeskRentals(endDate);
            deskRental.setTotalPriceDeskRentals(rentalRequest.getTotalPriceDeskRentals());

            DeskRental savedRental = repository.save(deskRental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mesa alugada com sucesso!");
            response.put("data", savedRental);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.out.println("ERRO AO CRIAR ALUGUEL: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("Erro ao criar aluguel: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar aluguel completo (MÉTODO CORRIGIDO)
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

            // Verificar se o plano tem turno
            if (rentalPlan.get().getRentalShift() == null) {
                return createErrorResponse("Plano de aluguel não possui turno definido", HttpStatus.BAD_REQUEST);
            }

            // Buscar a categoria do plano para calcular a data de término
            RentalCategory rentalCategory = rentalPlan.get().getRentalCategory();
            if (rentalCategory == null) {
                return createErrorResponse("Categoria do plano de aluguel não encontrada", HttpStatus.NOT_FOUND);
            }

            // Calcular data de término baseada na duração da categoria
            int durationInDays = rentalCategory.getBaseDurationInDaysRentalCategories();
            LocalDateTime startDate = rentalRequest.getStartPeriodDeskRentals();

            // Ajustar para o horário final considerando o turno
            LocalTime startTime = rentalPlan.get().getRentalShift().getStartTimeRentalShifts();
            LocalTime endTime = rentalPlan.get().getRentalShift().getEndTimeRentalShifts();

            startDate = startDate.with(startTime);
            LocalDateTime endDate = startDate.plusDays(durationInDays - 1).with(endTime);

            // Verificar conflitos considerando sobreposição de horários (excluindo o próprio aluguel)
            List<DeskRental> conflictingRentals = repository.findTimeRangeConflicts(
                    rentalRequest.getIdDesks(),
                    startDate,
                    endDate
            ).stream().filter(r -> !r.getIdDeskRentals().equals(id)).toList();

            if (!conflictingRentals.isEmpty()) {
                for (DeskRental conflict : conflictingRentals) {
                    boolean hasRealConflict = checkTimeOverlap(
                            rentalPlan.get().getRentalShift(),
                            conflict.getRentalPlan().getRentalShift()
                    );

                    if (hasRealConflict) {
                        String conflictMessage = buildConflictMessageByTime(
                                rentalPlan.get().getRentalShift(),
                                conflict.getRentalPlan().getRentalShift()
                        );
                        return createErrorResponse(conflictMessage, HttpStatus.CONFLICT);
                    }
                }
            }

            // Atualizar o aluguel
            rental.setDesk(desk.get());
            rental.setCustomer(customer.get());
            rental.setRentalPlan(rentalPlan.get());
            rental.setStartPeriodDeskRentals(startDate);
            rental.setEndPeriodDeskRentals(endDate);
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

    // GET ALL - Buscar todos os aluguéis OU filtrar por mesa
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDeskRentals(@RequestParam(required = false) Integer deskId) {
        List<DeskRental> rentals;

        if (deskId != null) {
            rentals = repository.findByDeskIdDesks(deskId);
        } else {
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

    // MÉTODO AUXILIAR - Verificar sobreposição de horários
    private boolean checkTimeOverlap(RentalShift shift1, RentalShift shift2) {
        LocalTime start1 = shift1.getStartTimeRentalShifts();
        LocalTime end1 = shift1.getEndTimeRentalShifts();
        LocalTime start2 = shift2.getStartTimeRentalShifts();
        LocalTime end2 = shift2.getEndTimeRentalShifts();

        // Verifica se os horários se sobrepõem
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    // MÉTODO AUXILIAR - Construir mensagem de conflito baseada em horários
    private String buildConflictMessageByTime(RentalShift newShift, RentalShift existingShift) {
        return "Conflito de horários: " +
                newShift.getNameRentalShifts() + " (" + newShift.getStartTimeRentalShifts() + " às " + newShift.getEndTimeRentalShifts() + ") " +
                "conflita com " +
                existingShift.getNameRentalShifts() + " (" + existingShift.getStartTimeRentalShifts() + " às " + existingShift.getEndTimeRentalShifts() + ")";
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