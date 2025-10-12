package com.coworkproject.controller;

import com.coworkproject.model.RentalShift;
import com.coworkproject.repository.RentalShiftRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rental-shifts")
public class RentalShiftController {

    private final RentalShiftRepository repository;

    public RentalShiftController(RentalShiftRepository repository) {
        this.repository = repository;
    }

    // GET ALL - Buscar todos os turnos
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRentalShifts() {
        List<RentalShift> shifts = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", shifts.isEmpty() ? "Nenhum turno encontrado" : "Turnos recuperados com sucesso");
        response.put("data", shifts);
        response.put("count", shifts.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar turno por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRentalShiftById(@PathVariable Integer id) {
        Optional<RentalShift> shift = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (shift.isPresent()) {
            response.put("success", true);
            response.put("message", "Turno encontrado com sucesso");
            response.put("data", shift.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Turno não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar novo turno
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRentalShift(@RequestBody RentalShift rentalShift) {
        try {
            // Verifica se já existe turno com mesmo nome
            if (repository.existsByNameRentalShifts(rentalShift.getNameRentalShifts())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Já existe um turno com este nome: " + rentalShift.getNameRentalShifts());
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Validação de horários
            if (rentalShift.getStartTimeRentalShifts() != null && rentalShift.getEndTimeRentalShifts() != null &&
                    rentalShift.getStartTimeRentalShifts().isAfter(rentalShift.getEndTimeRentalShifts())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Horário de início não pode ser após o horário de término");
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            RentalShift savedShift = repository.save(rentalShift);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Turno criado com sucesso!");
            response.put("data", savedShift);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao criar turno: " + e.getMessage());
            errorResponse.put("data", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // PUT - Atualizar turno completo
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRentalShift(@PathVariable Integer id, @RequestBody RentalShift shiftDetails) {
        Optional<RentalShift> optionalShift = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (optionalShift.isPresent()) {
            RentalShift shift = optionalShift.get();

            // Verifica se o novo nome já existe em outro turno
            if (!shift.getNameRentalShifts().equals(shiftDetails.getNameRentalShifts()) &&
                    repository.existsByNameRentalShifts(shiftDetails.getNameRentalShifts())) {
                response.put("success", false);
                response.put("message", "Já existe outro turno com este nome: " + shiftDetails.getNameRentalShifts());
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Validação de horários
            if (shiftDetails.getStartTimeRentalShifts() != null && shiftDetails.getEndTimeRentalShifts() != null &&
                    shiftDetails.getStartTimeRentalShifts().isAfter(shiftDetails.getEndTimeRentalShifts())) {
                response.put("success", false);
                response.put("message", "Horário de início não pode ser após o horário de término");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Atualiza todos os campos
            shift.setNameRentalShifts(shiftDetails.getNameRentalShifts());
            shift.setDescriptionRentalShifts(shiftDetails.getDescriptionRentalShifts());
            shift.setStartTimeRentalShifts(shiftDetails.getStartTimeRentalShifts());
            shift.setEndTimeRentalShifts(shiftDetails.getEndTimeRentalShifts());

            RentalShift updatedShift = repository.save(shift);

            response.put("success", true);
            response.put("message", "Turno atualizado com sucesso!");
            response.put("data", updatedShift);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Turno não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // DELETE - Deletar turno
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRentalShift(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            // Aqui você pode adicionar validações (ex: verificar se existem planos usando este turno)

            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Turno deletado com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Turno não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NAME - Buscar turno por nome
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getRentalShiftByName(@PathVariable String name) {
        Optional<RentalShift> shift = repository.findByNameRentalShifts(name);

        Map<String, Object> response = new HashMap<>();

        if (shift.isPresent()) {
            response.put("success", true);
            response.put("message", "Turno encontrado com sucesso");
            response.put("data", shift.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Turno não encontrado com nome: " + name);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}