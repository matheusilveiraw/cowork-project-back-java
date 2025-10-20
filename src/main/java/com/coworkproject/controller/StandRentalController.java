package com.coworkproject.controller;

import com.coworkproject.model.StandRental;
import com.coworkproject.repository.StandRentalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/stand-rentals")
public class StandRentalController {

    private final StandRentalRepository repository;

    public StandRentalController(StandRentalRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStandRentals(@RequestParam(required = false) Integer standId) {
        List<StandRental> rentals;

        if (standId != null) {
            rentals = repository.findByStandIdStands(standId);
        } else {
            rentals = repository.findAll();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel de stand encontrado" : "Aluguéis de stands recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createStandRental(@RequestBody StandRental standRental) {
        try {
            // Validações básicas
            if (standRental.getStand() == null || standRental.getStand().getIdStands() == null) {
                return createErrorResponse("Stand é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (standRental.getCustomer() == null || standRental.getCustomer().getIdCustomers() == null) {
                return createErrorResponse("Cliente é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (standRental.getRentalPlan() == null || standRental.getRentalPlan().getIdRentalPlans() == null) {
                return createErrorResponse("Plano de aluguel é obrigatório", HttpStatus.BAD_REQUEST);
            }

            StandRental savedRental = repository.save(standRental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aluguel de stand criado com sucesso!");
            response.put("data", savedRental);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao criar aluguel de stand: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("data", null);
        return ResponseEntity.status(status).body(errorResponse);
    }
}