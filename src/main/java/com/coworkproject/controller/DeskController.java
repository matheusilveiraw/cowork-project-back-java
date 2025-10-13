package com.coworkproject.controller;

import com.coworkproject.model.Desk;
import com.coworkproject.repository.DeskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/desks")
public class DeskController {

    private final DeskRepository repository;

    public DeskController(DeskRepository repository) {
        this.repository = repository;
    }

    // GET ALL - Buscar todas as mesas
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDesks() {
        List<Desk> desks = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", desks.isEmpty() ? "Nenhuma mesa encontrada" : "Mesas recuperadas com sucesso");
        response.put("data", desks);
        response.put("count", desks.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar mesa por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDeskById(@PathVariable Integer id) {
        Optional<Desk> desk = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (desk.isPresent()) {
            response.put("success", true);
            response.put("message", "Mesa encontrada com sucesso");
            response.put("data", desk.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Mesa não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar nova mesa
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDesk(@RequestBody Desk desk) {
        try {
            // Verifica se já existe mesa com mesmo número
            if (repository.existsByNumberDesks(desk.getNumberDesks())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Já existe uma mesa com este número: " + desk.getNumberDesks());
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Verifica se já existe mesa com mesmo nome
            if (desk.getNameDesks() != null && repository.existsByNameDesks(desk.getNameDesks())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Já existe uma mesa com este nome: " + desk.getNameDesks());
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Validação básica
            if (desk.getNumberDesks() == null || desk.getNumberDesks() <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Número da mesa deve ser maior que zero");
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            Desk savedDesk = repository.save(desk);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mesa criada com sucesso!");
            response.put("data", savedDesk);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao criar mesa: " + e.getMessage());
            errorResponse.put("data", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // PUT - Atualizar mesa completa
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDesk(@PathVariable Integer id, @RequestBody Desk deskDetails) {
        Optional<Desk> optionalDesk = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (optionalDesk.isPresent()) {
            Desk desk = optionalDesk.get();

            // Verifica se o novo número já existe em outra mesa
            if (!desk.getNumberDesks().equals(deskDetails.getNumberDesks()) &&
                    repository.existsByNumberDesks(deskDetails.getNumberDesks())) {
                response.put("success", false);
                response.put("message", "Já existe outra mesa com este número: " + deskDetails.getNumberDesks());
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Verifica se o novo nome já existe em outra mesa
            if (deskDetails.getNameDesks() != null &&
                    !deskDetails.getNameDesks().equals(desk.getNameDesks()) &&
                    repository.existsByNameDesks(deskDetails.getNameDesks())) {
                response.put("success", false);
                response.put("message", "Já existe outra mesa com este nome: " + deskDetails.getNameDesks());
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // Validação básica
            if (deskDetails.getNumberDesks() == null || deskDetails.getNumberDesks() <= 0) {
                response.put("success", false);
                response.put("message", "Número da mesa deve ser maior que zero");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Atualiza todos os campos
            desk.setNumberDesks(deskDetails.getNumberDesks());
            desk.setNameDesks(deskDetails.getNameDesks());

            Desk updatedDesk = repository.save(desk);

            response.put("success", true);
            response.put("message", "Mesa atualizada com sucesso!");
            response.put("data", updatedDesk);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Mesa não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // DELETE - Deletar mesa
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDesk(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            // Aqui você pode adicionar validações (ex: verificar se existem aluguéis para esta mesa)

            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Mesa deletada com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Mesa não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NUMBER - Buscar mesa por número
    @GetMapping("/number/{number}")
    public ResponseEntity<Map<String, Object>> getDeskByNumber(@PathVariable Integer number) {
        Optional<Desk> desk = repository.findByNumberDesks(number);

        Map<String, Object> response = new HashMap<>();

        if (desk.isPresent()) {
            response.put("success", true);
            response.put("message", "Mesa encontrada com sucesso");
            response.put("data", desk.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Mesa não encontrada com número: " + number);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NAME - Buscar mesa por nome
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getDeskByName(@PathVariable String name) {
        Optional<Desk> desk = repository.findByNameDesks(name);

        Map<String, Object> response = new HashMap<>();

        if (desk.isPresent()) {
            response.put("success", true);
            response.put("message", "Mesa encontrada com sucesso");
            response.put("data", desk.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Mesa não encontrada com nome: " + name);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}