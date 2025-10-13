package com.coworkproject.controller;

import com.coworkproject.dto.RentalPlanRequest;
import com.coworkproject.model.RentalCategory;
import com.coworkproject.model.RentalPlan;
import com.coworkproject.model.RentalShift;
import com.coworkproject.repository.RentalCategoryRepository;
import com.coworkproject.repository.RentalPlanRepository;
import com.coworkproject.repository.RentalShiftRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rental-plans")
public class RentalPlanController {

    private final RentalPlanRepository repository;
    private final RentalCategoryRepository categoryRepository;
    private final RentalShiftRepository shiftRepository;

    public RentalPlanController(RentalPlanRepository repository,
                                RentalCategoryRepository categoryRepository,
                                RentalShiftRepository shiftRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.shiftRepository = shiftRepository;
    }

    // GET ALL - Buscar todos os planos
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRentalPlans() {
        List<RentalPlan> plans = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", plans.isEmpty() ? "Nenhum plano encontrado" : "Planos recuperados com sucesso");
        response.put("data", plans);
        response.put("count", plans.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar plano por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRentalPlanById(@PathVariable Integer id) {
        Optional<RentalPlan> plan = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (plan.isPresent()) {
            response.put("success", true);
            response.put("message", "Plano encontrado com sucesso");
            response.put("data", plan.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Plano não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar novo plano
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRentalPlan(@RequestBody RentalPlanRequest planRequest) {
        try {
            // Validações das entidades relacionadas
            Optional<RentalCategory> category = categoryRepository.findById(planRequest.getIdRentalCategories());
            Optional<RentalShift> shift = shiftRepository.findById(planRequest.getIdRentalShifts());

            if (category.isEmpty()) {
                return createErrorResponse("Categoria não encontrada com ID: " + planRequest.getIdRentalCategories(), HttpStatus.NOT_FOUND);
            }

            if (shift.isEmpty()) {
                return createErrorResponse("Turno não encontrado com ID: " + planRequest.getIdRentalShifts(), HttpStatus.NOT_FOUND);
            }

            // Verificar se já existe plano com mesmo nome
            if (repository.existsByPlanNameRentalPlans(planRequest.getPlanNameRentalPlans())) {
                return createErrorResponse("Já existe um plano com este nome: " + planRequest.getPlanNameRentalPlans(), HttpStatus.CONFLICT);
            }

            // Verificar se já existe plano com mesma categoria e turno
            Optional<RentalPlan> existingPlan = repository.findByRentalCategoryIdRentalCategoriesAndRentalShiftIdRentalShifts(
                    planRequest.getIdRentalCategories(), planRequest.getIdRentalShifts());

            if (existingPlan.isPresent()) {
                return createErrorResponse("Já existe um plano para esta categoria e turno", HttpStatus.CONFLICT);
            }

            // Validação de dados
            if (planRequest.getPlanNameRentalPlans() == null || planRequest.getPlanNameRentalPlans().trim().isEmpty()) {
                return createErrorResponse("Nome do plano é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (planRequest.getPriceRentalPlans() == null || planRequest.getPriceRentalPlans().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("Preço deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Criar o plano
            RentalPlan rentalPlan = new RentalPlan();
            rentalPlan.setRentalCategory(category.get());
            rentalPlan.setRentalShift(shift.get());
            rentalPlan.setPlanNameRentalPlans(planRequest.getPlanNameRentalPlans());
            rentalPlan.setPriceRentalPlans(planRequest.getPriceRentalPlans());

            RentalPlan savedPlan = repository.save(rentalPlan);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Plano criado com sucesso!");
            response.put("data", savedPlan);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao criar plano: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar plano completo
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRentalPlan(@PathVariable Integer id, @RequestBody RentalPlanRequest planRequest) {
        Optional<RentalPlan> optionalPlan = repository.findById(id);

        if (optionalPlan.isEmpty()) {
            return createErrorResponse("Plano não encontrado com ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {
            RentalPlan plan = optionalPlan.get();

            // Validações das entidades relacionadas
            Optional<RentalCategory> category = categoryRepository.findById(planRequest.getIdRentalCategories());
            Optional<RentalShift> shift = shiftRepository.findById(planRequest.getIdRentalShifts());

            if (category.isEmpty()) {
                return createErrorResponse("Categoria não encontrada com ID: " + planRequest.getIdRentalCategories(), HttpStatus.NOT_FOUND);
            }

            if (shift.isEmpty()) {
                return createErrorResponse("Turno não encontrado com ID: " + planRequest.getIdRentalShifts(), HttpStatus.NOT_FOUND);
            }

            // Verificar se o novo nome já existe em outro plano
            if (!plan.getPlanNameRentalPlans().equals(planRequest.getPlanNameRentalPlans()) &&
                    repository.existsByPlanNameRentalPlans(planRequest.getPlanNameRentalPlans())) {
                return createErrorResponse("Já existe outro plano com este nome: " + planRequest.getPlanNameRentalPlans(), HttpStatus.CONFLICT);
            }

            // Verificar se a nova combinação categoria/turno já existe em outro plano
            Optional<RentalPlan> existingPlan = repository.findByRentalCategoryIdRentalCategoriesAndRentalShiftIdRentalShifts(
                    planRequest.getIdRentalCategories(), planRequest.getIdRentalShifts());

            if (existingPlan.isPresent() && !existingPlan.get().getIdRentalPlans().equals(id)) {
                return createErrorResponse("Já existe outro plano para esta categoria e turno", HttpStatus.CONFLICT);
            }

            // Validação de dados
            if (planRequest.getPlanNameRentalPlans() == null || planRequest.getPlanNameRentalPlans().trim().isEmpty()) {
                return createErrorResponse("Nome do plano é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (planRequest.getPriceRentalPlans() == null || planRequest.getPriceRentalPlans().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("Preço deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Atualizar o plano
            plan.setRentalCategory(category.get());
            plan.setRentalShift(shift.get());
            plan.setPlanNameRentalPlans(planRequest.getPlanNameRentalPlans());
            plan.setPriceRentalPlans(planRequest.getPriceRentalPlans());

            RentalPlan updatedPlan = repository.save(plan);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Plano atualizado com sucesso!");
            response.put("data", updatedPlan);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao atualizar plano: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE - Deletar plano
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRentalPlan(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            // Aqui você pode adicionar validações (ex: verificar se existem aluguéis usando este plano)

            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Plano deletado com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Plano não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY CATEGORY - Buscar planos por categoria
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getRentalPlansByCategory(@PathVariable Integer categoryId) {
        List<RentalPlan> plans = repository.findByRentalCategoryIdRentalCategories(categoryId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", plans.isEmpty() ? "Nenhum plano encontrado para esta categoria" : "Planos da categoria recuperados com sucesso");
        response.put("data", plans);
        response.put("count", plans.size());

        return ResponseEntity.ok(response);
    }

    // GET BY SHIFT - Buscar planos por turno
    @GetMapping("/shift/{shiftId}")
    public ResponseEntity<Map<String, Object>> getRentalPlansByShift(@PathVariable Integer shiftId) {
        List<RentalPlan> plans = repository.findByRentalShiftIdRentalShifts(shiftId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", plans.isEmpty() ? "Nenhum plano encontrado para este turno" : "Planos do turno recuperados com sucesso");
        response.put("data", plans);
        response.put("count", plans.size());

        return ResponseEntity.ok(response);
    }

    // GET BY PRICE RANGE - Buscar planos por faixa de preço
    @GetMapping("/price-range")
    public ResponseEntity<Map<String, Object>> getRentalPlansByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {

        if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(minPrice) < 0) {
            return createErrorResponse("Faixa de preço inválida", HttpStatus.BAD_REQUEST);
        }

        List<RentalPlan> plans = repository.findByPriceRentalPlansBetween(minPrice, maxPrice);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", plans.isEmpty() ? "Nenhum plano encontrado na faixa de preço especificada" : "Planos na faixa de preço recuperados com sucesso");
        response.put("data", plans);
        response.put("count", plans.size());

        return ResponseEntity.ok(response);
    }

    // GET BY NAME - Buscar plano por nome
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getRentalPlanByName(@PathVariable String name) {
        Optional<RentalPlan> plan = repository.findByPlanNameRentalPlans(name);

        Map<String, Object> response = new HashMap<>();

        if (plan.isPresent()) {
            response.put("success", true);
            response.put("message", "Plano encontrado com sucesso");
            response.put("data", plan.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Plano não encontrado com nome: " + name);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
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