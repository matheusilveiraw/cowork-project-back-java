package com.coworkproject.controller;

import com.coworkproject.dto.RoomRentalRequest;
import com.coworkproject.model.Customer;
import com.coworkproject.model.Room;
import com.coworkproject.model.RoomRental;
import com.coworkproject.repository.CustomerRepository;
import com.coworkproject.repository.RoomRentalRepository;
import com.coworkproject.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/room-rentals")
public class RoomRentalController {

    private final RoomRentalRepository repository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;

    public RoomRentalController(RoomRentalRepository repository,
                                RoomRepository roomRepository,
                                CustomerRepository customerRepository) {
        this.repository = repository;
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
    }

    // GET ALL - Buscar todos os aluguéis OU filtrar por sala
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoomRentals(@RequestParam(required = false) Integer roomId) {
        List<RoomRental> rentals;

        if (roomId != null) {
            // Se roomId foi fornecido, filtra por sala
            rentals = repository.findByRoomIdRooms(roomId);
        } else {
            // Se não, retorna todos os aluguéis
            rentals = repository.findAll();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ?
                (roomId != null ? "Nenhum aluguel encontrado para esta sala" : "Nenhum aluguel encontrado")
                : "Aluguéis de salas recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar aluguel por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoomRentalById(@PathVariable Integer id) {
        Optional<RoomRental> rental = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (rental.isPresent()) {
            response.put("success", true);
            response.put("message", "Aluguel de sala encontrado com sucesso");
            response.put("data", rental.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Aluguel de sala não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar novo aluguel
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRoomRental(@RequestBody RoomRentalRequest rentalRequest) {
        try {
            // Validações das entidades relacionadas
            Optional<Room> room = roomRepository.findById(rentalRequest.getIdRooms());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());

            if (room.isEmpty()) {
                return createErrorResponse("Sala não encontrada com ID: " + rentalRequest.getIdRooms(), HttpStatus.NOT_FOUND);
            }

            if (customer.isEmpty()) {
                return createErrorResponse("Cliente não encontrado com ID: " + rentalRequest.getIdCustomers(), HttpStatus.NOT_FOUND);
            }

            // Validação de datas
            if (rentalRequest.getStartPeriodAreaRentals() == null || rentalRequest.getEndPeriodAreaRentals() == null) {
                return createErrorResponse("Datas de início e término são obrigatórias", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodAreaRentals().isAfter(rentalRequest.getEndPeriodAreaRentals())) {
                return createErrorResponse("Data de início não pode ser após a data de término", HttpStatus.BAD_REQUEST);
            }

            if (rentalRequest.getStartPeriodAreaRentals().isBefore(LocalDateTime.now().toLocalDate().atStartOfDay())) {
                return createErrorResponse("Data de início não pode ser no passado", HttpStatus.BAD_REQUEST);
            }

            // Verificar conflitos de horário
            List<RoomRental> conflictingRentals = repository.findConflictingRentals(
                    rentalRequest.getIdRooms(),
                    rentalRequest.getStartPeriodAreaRentals(),
                    rentalRequest.getEndPeriodAreaRentals()
            );

            if (!conflictingRentals.isEmpty()) {
                return createErrorResponse("A sala já está alugada neste período", HttpStatus.CONFLICT);
            }

            // Validação de preço
            if (rentalRequest.getTotalPriceStandRentals() == null || rentalRequest.getTotalPriceStandRentals().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse("Preço total deve ser maior que zero", HttpStatus.BAD_REQUEST);
            }

            // Criar o aluguel
            RoomRental roomRental = new RoomRental();
            roomRental.setRoom(room.get());
            roomRental.setCustomer(customer.get());
            roomRental.setStartPeriodAreaRentals(rentalRequest.getStartPeriodAreaRentals());
            roomRental.setEndPeriodAreaRentals(rentalRequest.getEndPeriodAreaRentals());
            roomRental.setTotalPriceStandRentals(rentalRequest.getTotalPriceStandRentals());

            RoomRental savedRental = repository.save(roomRental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sala alugada com sucesso!");
            response.put("data", savedRental);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao criar aluguel de sala: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT - Atualizar aluguel completo
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRoomRental(@PathVariable Integer id, @RequestBody RoomRentalRequest rentalRequest) {
        Optional<RoomRental> optionalRental = repository.findById(id);

        if (optionalRental.isEmpty()) {
            return createErrorResponse("Aluguel de sala não encontrado com ID: " + id, HttpStatus.NOT_FOUND);
        }

        try {
            RoomRental rental = optionalRental.get();

            // Validações das entidades relacionadas
            Optional<Room> room = roomRepository.findById(rentalRequest.getIdRooms());
            Optional<Customer> customer = customerRepository.findById(rentalRequest.getIdCustomers());

            if (room.isEmpty()) {
                return createErrorResponse("Sala não encontrada com ID: " + rentalRequest.getIdRooms(), HttpStatus.NOT_FOUND);
            }

            if (customer.isEmpty()) {
                return createErrorResponse("Cliente não encontrado com ID: " + rentalRequest.getIdCustomers(), HttpStatus.NOT_FOUND);
            }

            // Validação de datas
            if (rentalRequest.getStartPeriodAreaRentals().isAfter(rentalRequest.getEndPeriodAreaRentals())) {
                return createErrorResponse("Data de início não pode ser após a data de término", HttpStatus.BAD_REQUEST);
            }

            // Verificar conflitos de horário (excluindo o próprio aluguel)
            List<RoomRental> conflictingRentals = repository.findConflictingRentals(
                    rentalRequest.getIdRooms(),
                    rentalRequest.getStartPeriodAreaRentals(),
                    rentalRequest.getEndPeriodAreaRentals()
            ).stream().filter(r -> !r.getIdRoomRentals().equals(id)).toList();

            if (!conflictingRentals.isEmpty()) {
                return createErrorResponse("A sala já está alugada neste período", HttpStatus.CONFLICT);
            }

            // Atualizar o aluguel
            rental.setRoom(room.get());
            rental.setCustomer(customer.get());
            rental.setStartPeriodAreaRentals(rentalRequest.getStartPeriodAreaRentals());
            rental.setEndPeriodAreaRentals(rentalRequest.getEndPeriodAreaRentals());
            rental.setTotalPriceStandRentals(rentalRequest.getTotalPriceStandRentals());

            RoomRental updatedRental = repository.save(rental);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aluguel de sala atualizado com sucesso!");
            response.put("data", updatedRental);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse("Erro ao atualizar aluguel de sala: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE - Deletar aluguel
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRoomRental(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Aluguel de sala deletado com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Aluguel de sala não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // GET BY CUSTOMER - Buscar aluguéis por cliente
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getRoomRentalsByCustomer(@PathVariable Integer customerId) {
        List<RoomRental> rentals = repository.findByCustomerIdCustomers(customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel de sala encontrado para este cliente" : "Aluguéis de salas do cliente recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ROOM - Buscar aluguéis por sala (endpoint alternativo)
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Map<String, Object>> getRoomRentalsByRoom(@PathVariable Integer roomId) {
        List<RoomRental> rentals = repository.findByRoomIdRooms(roomId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", rentals.isEmpty() ? "Nenhum aluguel encontrado para esta sala" : "Aluguéis da sala recuperados com sucesso");
        response.put("data", rentals);
        response.put("count", rentals.size());

        return ResponseEntity.ok(response);
    }

    // GET ACTIVE - Buscar aluguéis ativos
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveRoomRentals() {
        List<RoomRental> activeRentals = repository.findActiveRentals(LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", activeRentals.isEmpty() ? "Nenhum aluguel de sala ativo no momento" : "Aluguéis de salas ativos recuperados com sucesso");
        response.put("data", activeRentals);
        response.put("count", activeRentals.size());

        return ResponseEntity.ok(response);
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