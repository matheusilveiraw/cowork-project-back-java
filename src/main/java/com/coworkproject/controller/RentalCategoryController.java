package com.coworkproject.controller;

import com.coworkproject.model.RentalCategory;
import com.coworkproject.repository.RentalCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rental-categories")
public class RentalCategoryController {

    private final RentalCategoryRepository repository;

    public RentalCategoryController(RentalCategoryRepository repository) {
        this.repository = repository;
    }

    // GET ALL - Buscar todas as categorias
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRentalCategories() {
        List<RentalCategory> categories = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", categories.isEmpty() ? "Nenhuma categoria encontrada" : "Categorias recuperadas com sucesso");
        response.put("data", categories);
        response.put("count", categories.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar categoria por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRentalCategoryById(@PathVariable Integer id) {
        Optional<RentalCategory> category = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (category.isPresent()) {
            response.put("success", true);
            response.put("message", "Categoria encontrada com sucesso");
            response.put("data", category.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Categoria não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar nova categoria
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRentalCategory(@RequestBody RentalCategory rentalCategory) {
        try {
            // Verifica se já existe categoria com mesmo nome
            if (repository.existsByNameRentalCategories(rentalCategory.getNameRentalCategories())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Já existe uma categoria com este nome: " + rentalCategory.getNameRentalCategories());
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            RentalCategory savedCategory = repository.save(rentalCategory);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoria criada com sucesso!");
            response.put("data", savedCategory);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao criar categoria: " + e.getMessage());
            errorResponse.put("data", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // PUT - Atualizar categoria completa
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRentalCategory(@PathVariable Integer id, @RequestBody RentalCategory categoryDetails) {
        Optional<RentalCategory> optionalCategory = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (optionalCategory.isPresent()) {
            RentalCategory category = optionalCategory.get();

            // Verifica se o novo nome já existe em outra categoria
            if (!category.getNameRentalCategories().equals(categoryDetails.getNameRentalCategories()) &&
                    repository.existsByNameRentalCategories(categoryDetails.getNameRentalCategories())) {
                response.put("success", false);
                response.put("message", "Já existe outra categoria com este nome: " + categoryDetails.getNameRentalCategories());
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Atualiza todos os campos
            category.setNameRentalCategories(categoryDetails.getNameRentalCategories());
            category.setBaseDurationInDaysRentalCategories(categoryDetails.getBaseDurationInDaysRentalCategories());

            RentalCategory updatedCategory = repository.save(category);

            response.put("success", true);
            response.put("message", "Categoria atualizada com sucesso!");
            response.put("data", updatedCategory);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Categoria não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // DELETE - Deletar categoria
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRentalCategory(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            // Aqui você pode adicionar validações (ex: verificar se existem planos usando esta categoria)

            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Categoria deletada com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Categoria não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NAME - Buscar categoria por nome (opcional)
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getRentalCategoryByName(@PathVariable String name) {
        Optional<RentalCategory> category = repository.findByNameRentalCategories(name);

        Map<String, Object> response = new HashMap<>();

        if (category.isPresent()) {
            response.put("success", true);
            response.put("message", "Categoria encontrada com sucesso");
            response.put("data", category.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Categoria não encontrada com nome: " + name);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}