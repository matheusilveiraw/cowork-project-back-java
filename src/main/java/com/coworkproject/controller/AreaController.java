package com.coworkproject.controller;

import com.coworkproject.model.Area;
import com.coworkproject.repository.AreaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaRepository repository;

    public AreaController(AreaRepository repository) {
        this.repository = repository;
    }

    // GET ALL - Buscar todas as áreas
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAreas() {
        List<Area> areas = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", areas.isEmpty() ? "Nenhuma área encontrada" : "Áreas recuperadas com sucesso");
        response.put("data", areas);
        response.put("count", areas.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar área por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAreaById(@PathVariable Integer id) {
        Optional<Area> area = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (area.isPresent()) {
            response.put("success", true);
            response.put("message", "Área encontrada com sucesso");
            response.put("data", area.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Área não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar nova área
    @PostMapping
    public ResponseEntity<Map<String, Object>> createArea(@RequestBody Area area) {
        try {
            // Verifica se já existe área com mesmo número
            if (repository.existsByNumberAreas(area.getNumberAreas())) {
                return createErrorResponse("Já existe uma área com este número: " + area.getNumberAreas(), HttpStatus.CONFLICT);
            }

            // Verifica se já existe área com mesmo nome
            if (area.getNameAreas() != null && repository.existsByNameAreas(area.getNameAreas())) {
                return createErrorResponse("Já existe uma área com este nome: " + area.getNameAreas(), HttpStatus.CONFLICT);
            }

            // Validação básica
            if (area.getNumberAreas() == null || area.getNumberAreas() <= 0) {
                return createErrorResponse("Número da área deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            Area savedArea = repository.save(area);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Área criada com sucesso!");
            response.put("data", savedArea);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao criar área: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar área completa
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateArea(@PathVariable Integer id, @RequestBody Area areaDetails) {
        Optional<Area> optionalArea = repository.findById(id);

        if (optionalArea.isEmpty()) {
            return createErrorResponse("Área não encontrada com ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {
            Area area = optionalArea.get();

            // Verifica se o novo número já existe em outra área
            if (!area.getNumberAreas().equals(areaDetails.getNumberAreas()) &&
                    repository.existsByNumberAreasAndIdAreasNot(areaDetails.getNumberAreas(), id)) {
                return createErrorResponse("Já existe outra área com este número: " + areaDetails.getNumberAreas(), HttpStatus.CONFLICT);
            }

            // Verifica se o novo nome já existe em outra área
            if (areaDetails.getNameAreas() != null &&
                    !areaDetails.getNameAreas().equals(area.getNameAreas()) &&
                    repository.existsByNameAreasAndIdAreasNot(areaDetails.getNameAreas(), id)) {
                return createErrorResponse("Já existe outra área com este nome: " + areaDetails.getNameAreas(), HttpStatus.CONFLICT);
            }

            // Validação básica
            if (areaDetails.getNumberAreas() == null || areaDetails.getNumberAreas() <= 0) {
                return createErrorResponse("Número da área deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Atualiza todos os campos
            area.setNumberAreas(areaDetails.getNumberAreas());
            area.setNameAreas(areaDetails.getNameAreas());

            Area updatedArea = repository.save(area);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Área atualizada com sucesso!");
            response.put("data", updatedArea);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao atualizar área: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE - Deletar área
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteArea(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Área deletada com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Área não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NUMBER - Buscar área por número
    @GetMapping("/number/{number}")
    public ResponseEntity<Map<String, Object>> getAreaByNumber(@PathVariable Integer number) {
        Optional<Area> area = repository.findByNumberAreas(number);

        Map<String, Object> response = new HashMap<>();

        if (area.isPresent()) {
            response.put("success", true);
            response.put("message", "Área encontrada com sucesso");
            response.put("data", area.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Área não encontrada com número: " + number);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NAME - Buscar área por nome
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getAreaByName(@PathVariable String name) {
        Optional<Area> area = repository.findByNameAreas(name);

        Map<String, Object> response = new HashMap<>();

        if (area.isPresent()) {
            response.put("success", true);
            response.put("message", "Área encontrada com sucesso");
            response.put("data", area.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Área não encontrada com nome: " + name);
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