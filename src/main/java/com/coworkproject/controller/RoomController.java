package com.coworkproject.controller;

import com.coworkproject.model.Room;
import com.coworkproject.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository repository;

    public RoomController(RoomRepository repository) {
        this.repository = repository;
    }

    // GET ALL - Buscar todas as salas
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRooms() {
        List<Room> rooms = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rooms.isEmpty() ? "Nenhuma sala encontrada" : "Salas recuperadas com sucesso");
        response.put("data", rooms);
        response.put("count", rooms.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar sala por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoomById(@PathVariable Integer id) {
        Optional<Room> room = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (room.isPresent()) {
            response.put("success", true);
            response.put("message", "Sala encontrada com sucesso");
            response.put("data", room.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Sala não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar nova sala
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Room room) {
        try {
            // Verifica se já existe sala com mesmo número
            if (repository.existsByNumberRooms(room.getNumberRooms())) {
                return createErrorResponse("Já existe uma sala com este número: " + room.getNumberRooms(), HttpStatus.CONFLICT);
            }

            // Verifica se já existe sala com mesmo nome
            if (room.getNameRooms() != null && repository.existsByNameRooms(room.getNameRooms())) {
                return createErrorResponse("Já existe uma sala com este nome: " + room.getNameRooms(), HttpStatus.CONFLICT);
            }

            // Validação básica
            if (room.getNumberRooms() == null || room.getNumberRooms() <= 0) {
                return createErrorResponse("Número da sala deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            Room savedRoom = repository.save(room);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sala criada com sucesso!");
            response.put("data", savedRoom);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao criar sala: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar sala completa
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRoom(@PathVariable Integer id, @RequestBody Room roomDetails) {
        Optional<Room> optionalRoom = repository.findById(id);

        if (optionalRoom.isEmpty()) {
            return createErrorResponse("Sala não encontrada com ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {
            Room room = optionalRoom.get();

            // Verifica se o novo número já existe em outra sala
            if (!room.getNumberRooms().equals(roomDetails.getNumberRooms()) &&
                    repository.existsByNumberRoomsAndIdRoomsNot(roomDetails.getNumberRooms(), id)) {
                return createErrorResponse("Já existe outra sala com este número: " + roomDetails.getNumberRooms(), HttpStatus.CONFLICT);
            }

            // Verifica se o novo nome já existe em outra sala
            if (roomDetails.getNameRooms() != null &&
                    !roomDetails.getNameRooms().equals(room.getNameRooms()) &&
                    repository.existsByNameRoomsAndIdRoomsNot(roomDetails.getNameRooms(), id)) {
                return createErrorResponse("Já existe outra sala com este nome: " + roomDetails.getNameRooms(), HttpStatus.CONFLICT);
            }

            // Validação básica
            if (roomDetails.getNumberRooms() == null || roomDetails.getNumberRooms() <= 0) {
                return createErrorResponse("Número da sala deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Atualiza todos os campos
            room.setNumberRooms(roomDetails.getNumberRooms());
            room.setNameRooms(roomDetails.getNameRooms());

            Room updatedRoom = repository.save(room);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sala atualizada com sucesso!");
            response.put("data", updatedRoom);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao atualizar sala: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE - Deletar sala
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRoom(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Sala deletada com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Sala não encontrada com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NUMBER - Buscar sala por número
    @GetMapping("/number/{number}")
    public ResponseEntity<Map<String, Object>> getRoomByNumber(@PathVariable Integer number) {
        Optional<Room> room = repository.findByNumberRooms(number);

        Map<String, Object> response = new HashMap<>();

        if (room.isPresent()) {
            response.put("success", true);
            response.put("message", "Sala encontrada com sucesso");
            response.put("data", room.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Sala não encontrada com número: " + number);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY NAME - Buscar sala por nome
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getRoomByName(@PathVariable String name) {
        Optional<Room> room = repository.findByNameRooms(name);

        Map<String, Object> response = new HashMap<>();

        if (room.isPresent()) {
            response.put("success", true);
            response.put("message", "Sala encontrada com sucesso");
            response.put("data", room.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Sala não encontrada com nome: " + name);
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