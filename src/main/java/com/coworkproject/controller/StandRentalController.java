package com.coworkproject.controller;

import com.coworkproject.dto.StandRentalRequest;
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
@RequestMapping("/api/stand-rentals")
public class StandRentalController {

    private final StandRentalRepository repository;
    private final StandRepository standRepository;
    private final CustomerRepository customerRepository;
    private final RentalPlanRepository rentalPlanRepository;
    private final RentalCategoryRepository rentalCategoryRepository;
    private final RentalShiftRepository rentalShiftRepository;

    public StandRentalController(StandRentalRepository repository,
                                 StandRepository standRepository,
                                 CustomerRepository customerRepository,
                                 RentalPlanRepository rentalPlanRepository,
                                 RentalCategoryRepository rentalCategoryRepository,
                                 RentalShiftRepository rentalShiftRepository) {
        this.repository = repository;
        this.standRepository = standRepository;
        this.customerRepository = customerRepository;
        this.rentalPlanRepository = rentalPlanRepository;
        this.rentalCategoryRepository = rentalCategoryRepository;
        this.rentalShiftRepository = rentalShiftRepository;
    }

    // POST - Criar novo aluguel de stand (MÉTODO CORRIGIDO)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createStandRental(@RequestBody StandRentalRequest rentalRequest) {
        try {
            // CORREÇÃO: Validações obrigatórias mais específicas
            if (rentalRequest.getIdStands() == null) {
                return createErrorResponse("ID do stand é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getIdCustomers() == null) {
                return createErrorResponse("ID do cliente é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getIdRentalPlans() == null) {
                return createErrorResponse("ID do plano de aluguel é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodStandRentals() == null) {
                return createErrorResponse("Data de início é obrigatória", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getTotalPriceStandRentals() == null || rentalRequest.getTotalPriceStandRentals().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("Preço total deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Validações das entidades relacionadas
            Optional<Stand> stand = standRepository.findById(rentalRequest.getIdStands());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());
            Optional<RentalPlan> rentalPlan = rentalPlanRepository.findById(rentalRequest.getIdRentalPlans());

            if (stand.isEmpty()) {
                return createErrorResponse("Stand não encontrado com ID: " + rentalRequest.getIdStands(), HttpStatus.NOT_FOUND);
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
            LocalDateTime startDate = rentalRequest.getStartPeriodStandRentals();

            // Ajustar para o horário final considerando o turno
            LocalTime startTime = rentalPlan.get().getRentalShift().getStartTimeRentalShifts();
            LocalTime endTime = rentalPlan.get().getRentalShift().getEndTimeRentalShifts();

            // CORREÇÃO: Não ajustar o startDate novamente, pois já vem formatado do frontend
            // startDate = startDate.with(startTime);

            // Calcular data final com horário do turno
            LocalDateTime endDate = startDate.plusDays(durationInDays - 1).with(endTime);

            System.out.println("=== VERIFICAÇÃO DE CONFLITOS POR HORÁRIO ===");
            System.out.println("Stand: " + rentalRequest.getIdStands());
            System.out.println("Turno: " + rentalPlan.get().getRentalShift().getNameRentalShifts());
            System.out.println("Horário: " + startTime + " às " + endTime);
            System.out.println("Novo aluguel - Início: " + startDate + " | Fim: " + endDate);

            // VERIFICAÇÃO DE CONFLITOS BASEADA EM HORÁRIOS
            List<StandRental> conflictingRentals = repository.findTimeRangeConflicts(
                    rentalRequest.getIdStands(),
                    startDate,
                    endDate
            );

            if (!conflictingRentals.isEmpty()) {
                System.out.println("CONFLITOS ENCONTRADOS: " + conflictingRentals.size());

                // Verificar se os conflitos são reais (sobreposição de horários)
                for (StandRental conflict : conflictingRentals) {
                    boolean hasRealConflict = checkTimeOverlap(
                            rentalPlan.get().getRentalShift(),
                            conflict.getRentalPlan().getRentalShift()
                    );

                    if (hasRealConflict) {
                        System.out.println("CONFLITO REAL ENCONTRADO:");
                        System.out.println("Aluguel ID: " + conflict.getIdStandRentals());
                        System.out.println("Turno conflitante: " + conflict.getRentalPlan().getRentalShift().getNameRentalShifts());
                        System.out.println("Horário conflitante: " + conflict.getRentalPlan().getRentalShift().getStartTimeRentalShifts() +
                                " às " + conflict.getRentalPlan().getRentalShift().getEndTimeRentalShifts());
                        System.out.println("Período: " + conflict.getStartPeriodStandRentals() + " até " + conflict.getEndPeriodStandRentals());

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

            // Criar o aluguel
            StandRental standRental = new StandRental();
            standRental.setStand(stand.get());
            standRental.setCustomer(customer.get());
            standRental.setRentalPlan(rentalPlan.get());
            standRental.setStartPeriodStandRentals(startDate);
            standRental.setEndPeriodStandRentals(endDate);
            standRental.setTotalPriceStandRentals(rentalRequest.getTotalPriceStandRentals());

            StandRental savedRental = repository.save(standRental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stand alugado com sucesso!");
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
    public ResponseEntity<Map<String, Object>> updateStandRental(@PathVariable Integer id, @RequestBody StandRentalRequest rentalRequest) {
        Optional<StandRental> optionalRental = repository.findById(id);

        if (optionalRental.isEmpty()) {
            return createErrorResponse("Aluguel não encontrado com ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {
            StandRental rental = optionalRental.get();

            // CORREÇÃO: Validações obrigatórias
            if (rentalRequest.getIdStands() == null) {
                return createErrorResponse("ID do stand é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getIdCustomers() == null) {
                return createErrorResponse("ID do cliente é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getIdRentalPlans() == null) {
                return createErrorResponse("ID do plano de aluguel é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodStandRentals() == null) {
                return createErrorResponse("Data de início é obrigatória", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getTotalPriceStandRentals() == null || rentalRequest.getTotalPriceStandRentals().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("Preço total deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Validações das entidades relacionadas
            Optional<Stand> stand = standRepository.findById(rentalRequest.getIdStands());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());
            Optional<RentalPlan> rentalPlan = rentalPlanRepository.findById(rentalRequest.getIdRentalPlans());

            if (stand.isEmpty()) {
                return createErrorResponse("Stand não encontrado com ID: " + rentalRequest.getIdStands(), HttpStatus.NOT_FOUND);
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
            LocalDateTime startDate = rentalRequest.getStartPeriodStandRentals();

            // Ajustar para o horário final considerando o turno
            LocalTime startTime = rentalPlan.get().getRentalShift().getStartTimeRentalShifts();
            LocalTime endTime = rentalPlan.get().getRentalShift().getEndTimeRentalShifts();

            // CORREÇÃO: Não ajustar o startDate novamente
            // startDate = startDate.with(startTime);

            LocalDateTime endDate = startDate.plusDays(durationInDays - 1).with(endTime);

            // Verificar conflitos considerando sobreposição de horários (excluindo o próprio aluguel)
            List<StandRental> conflictingRentals = repository.findTimeRangeConflicts(
                    rentalRequest.getIdStands(),
                    startDate,
                    endDate
            ).stream().filter(r -> !r.getIdStandRentals().equals(id)).toList();

            if (!conflictingRentals.isEmpty()) {
                for (StandRental conflict : conflictingRentals) {
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
            rental.setStand(stand.get());
            rental.setCustomer(customer.get());
            rental.setRentalPlan(rentalPlan.get());
            rental.setStartPeriodStandRentals(startDate);
            rental.setEndPeriodStandRentals(endDate);
            rental.setTotalPriceStandRentals(rentalRequest.getTotalPriceStandRentals());

            StandRental updatedRental = repository.save(rental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aluguel atualizado com sucesso!");
            response.put("data", updatedRental);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao atualizar aluguel: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET ALL - Buscar todos os aluguéis OU filtrar por stand
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStandRentals(@RequestParam(required = false) Integer standId) {
        List<StandRental> rentals;

        if (standId != null) {
            rentals = repository.findByStandIdStands(standId);
        } else {
            rentals = repository.findAll();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ?
                (standId != null ? "Nenhum aluguel encontrado para este stand" : "Nenhum aluguel encontrado")
                : "Aluguéis recuperados com sucesso");
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
    public ResponseEntity<Map<String, Object>> deleteStandRental(@PathVariable Integer id) {
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
    public ResponseEntity<Map<String, Object>> getStandRentalsByCustomer(@PathVariable Integer customerId) {
        List<StandRental> rentals = repository.findByCustomerIdCustomers(customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel encontrado para este cliente" : "Aluguéis do cliente recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET BY STAND - Buscar aluguéis por stand (endpoint alternativo)
    @GetMapping("/stand/{standId}")
    public ResponseEntity<Map<String, Object>> getStandRentalsByStand(@PathVariable Integer standId) {
        List<StandRental> rentals = repository.findByStandIdStands(standId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel encontrado para este stand" : "Aluguéis do stand recuperados com sucesso");
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