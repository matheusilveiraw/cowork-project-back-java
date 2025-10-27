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

    // GET ALL - Buscar todos os stands
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStands() {
        List<Stand> stands = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", stands.isEmpty() ? "Nenhum stand encontrado" : "Stands recuperados com sucesso");
        response.put("data", stands);
        response.put("count", stands.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar stand por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStandById(@PathVariable Integer id) {
        Optional<Stand> stand = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (stand.isPresent()) {
            response.put("success", true);
            response.put("message", "Stand encontrado com sucesso");
            response.put("data", stand.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Stand não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar novo stand
    @PostMapping
    public ResponseEntity<Map<String, Object>> createStand(@RequestBody Stand stand) {
        try {
            // Verifica se já existe stand com mesmo número
            if (repository.existsByNumberStands(stand.getNumberStands())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Já existe um stand com este número: " + stand.getNumberStands());
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Verifica se já existe stand com mesmo nome
            if (stand.getNameStands() != null && repository.existsByNameStands(stand.getNameStands())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Já existe um stand com este nome: " + stand.getNameStands());
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Validação básica
            if (stand.getNumberStands() == null || stand.getNumberStands() <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Número do stand deve ser maior que zero");
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            Stand savedStand = repository.save(stand);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stand criado com sucesso!");
            response.put("data", savedStand);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao criar stand: " + e.getMessage());
            errorResponse.put("data", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // PUT - Atualizar stand completo
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStand(@PathVariable Integer id, @RequestBody Stand standDetails) {
        Optional<Stand> optionalStand = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (optionalStand.isPresent()) {
            Stand stand = optionalStand.get();

            // Verifica se o novo número já existe em outro stand
            if (!stand.getNumberStands().equals(standDetails.getNumberStands()) &&
                    repository.existsByNumberStands(standDetails.getNumberStands())) {
                response.put("success", false);
                response.put("message", "Já existe outro stand com este número: " + standDetails.getNumberStands());
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Verifica se o novo nome já existe em outro stand
            if (standDetails.getNameStands() != null &&
                    !standDetails.getNameStands().equals(stand.getNameStands()) &&
                    repository.existsByNameStands(standDetails.getNameStands())) {
                response.put("success", false);
                response.put("message", "Já existe outro stand com este nome: " + standDetails.getNameStands());
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Validação básica
            if (standDetails.getNumberStands() == null || standDetails.getNumberStands() <= 0) {
                response.put("success", false);
                response.put("message", "Número do stand deve ser maior que zero");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Atualiza todos os campos
            stand.setNumberStands(standDetails.getNumberStands());
            stand.setNameStands(standDetails.getNameStands());

            Stand updatedStand = repository.save(stand);

            response.put("success", true);
            response.put("message", "Stand atualizado com sucesso!");
            response.put("data", updatedStand);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Stand não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // DELETE - Deletar stand
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStand(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            // Aqui você pode adicionar validações (ex: verificar se existem aluguéis para este stand)

            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Stand deletado com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Stand não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NUMBER - Buscar stand por número
    @GetMapping("/number/{number}")
    public ResponseEntity<Map<String, Object>> getStandByNumber(@PathVariable Integer number) {
        Optional<Stand> stand = repository.findByNumberStands(number);

        Map<String, Object> response = new HashMap<>();

        if (stand.isPresent()) {
            response.put("success", true);
            response.put("message", "Stand encontrado com sucesso");
            response.put("data", stand.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Stand não encontrado com número: " + number);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NAME - Buscar stand por nome
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getStandByName(@PathVariable String name) {
        Optional<Stand> stand = repository.findByNameStands(name);

        Map<String, Object> response = new HashMap<>();

        if (stand.isPresent()) {
            response.put("success", true);
            response.put("message", "Stand encontrado com sucesso");
            response.put("data", stand.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Stand não encontrado com nome: " + name);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}