package com.coworkproject.controller;

import com.coworkproject.model.Stand;
import com.coworkproject.repository.StandRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/stands")
public class StandController {

    private final StandRepository repository;

    public StandController(StandRepository repository) {
        this.repository = repository;
    }

    // GET ALL - Buscar todas as estantes
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStands() {
        List<Stand> stands = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", stands.isEmpty() ? "Nenhuma estante encontrada" : "Estantes recuperadas com sucesso");
        response.put("data", stands);
        response.put("count", stands.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar estante por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStandById(@PathVariable Integer id) {
        Optional<Stand> stand = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (stand.isPresent()) {
            response.put("success", true);
            response.put("message", "Estante encontrada com sucesso");
            response.put("data", stand.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Estante não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar nova estante
    @PostMapping
    public ResponseEntity<Map<String, Object>> createStand(@RequestBody Stand stand) {
        try {
            // Verifica se já existe estante com mesmo número
            if (repository.existsByNumberStands(stand.getNumberStands())) {
                return createErrorResponse("Já existe uma estante com este número: " + stand.getNumberStands(), HttpStatus.CONFLICT);
            }

            // Verifica se já existe estante com mesmo nome
            if (stand.getNameStands() != null && repository.existsByNameStands(stand.getNameStands())) {
                return createErrorResponse("Já existe uma estante com este nome: " + stand.getNameStands(), HttpStatus.CONFLICT);
            }

            // Validação básica
            if (stand.getNumberStands() == null || stand.getNumberStands() <= 0) {
                return createErrorResponse("Número da estante deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            Stand savedStand = repository.save(stand);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estante criada com sucesso!");
            response.put("data", savedStand);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao criar estante: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar estante completa
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStand(@PathVariable Integer id, @RequestBody Stand standDetails) {
        Optional<Stand> optionalStand = repository.findById(id);

        if (optionalStand.isEmpty()) {
            return createErrorResponse("Estante não encontrada com ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {
            Stand stand = optionalStand.get();

            // Verifica se o novo número já existe em outra estante
            if (!stand.getNumberStands().equals(standDetails.getNumberStands()) &&
                    repository.existsByNumberStandsAndIdStandsNot(standDetails.getNumberStands(), id)) {
                return createErrorResponse("Já existe outra estante com este número: " + standDetails.getNumberStands(), HttpStatus.CONFLICT);
            }

            // Verifica se o novo nome já existe em outra estante
            if (standDetails.getNameStands() != null &&
                    !standDetails.getNameStands().equals(stand.getNameStands()) &&
                    repository.existsByNameStandsAndIdStandsNot(standDetails.getNameStands(), id)) {
                return createErrorResponse("Já existe outra estante com este nome: " + standDetails.getNameStands(), HttpStatus.CONFLICT);
            }

            // Validação básica
            if (standDetails.getNumberStands() == null || standDetails.getNumberStands() <= 0) {
                return createErrorResponse("Número da estante deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Atualiza todos os campos
            stand.setNumberStands(standDetails.getNumberStands());
            stand.setNameStands(standDetails.getNameStands());

            Stand updatedStand = repository.save(stand);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estante atualizada com sucesso!");
            response.put("data", updatedStand);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao atualizar estante: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE - Deletar estante
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStand(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Estante deletada com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Estante não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NUMBER - Buscar estante por número
    @GetMapping("/number/{number}")
    public ResponseEntity<Map<String, Object>> getStandByNumber(@PathVariable Integer number) {
        Optional<Stand> stand = repository.findByNumberStands(number);

        Map<String, Object> response = new HashMap<>();

        if (stand.isPresent()) {
            response.put("success", true);
            response.put("message", "Estante encontrada com sucesso");
            response.put("data", stand.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Estante não encontrada com número: " + number);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NAME - Buscar estante por nome
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getStandByName(@PathVariable String name) {
        Optional<Stand> stand = repository.findByNameStands(name);

        Map<String, Object> response = new HashMap<>();

        if (stand.isPresent()) {
            response.put("success", true);
            response.put("message", "Estante encontrada com sucesso");
            response.put("data", stand.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Estante não encontrada com nome: " + name);
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