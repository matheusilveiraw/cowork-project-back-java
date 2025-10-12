package com.coworkproject.controller;

import com.coworkproject.model.Customer;
import com.coworkproject.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    // GET ALL - Buscar todos os clientes
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCustomers() {
        List<Customer> customers = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", customers.isEmpty() ? "Nenhum cliente encontrado" : "Clientes recuperados com sucesso");
        response.put("data", customers);
        response.put("count", customers.size());

        return ResponseEntity.ok(response);
    }

    // GET BY ID - Buscar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable Integer id) {
        Optional<Customer> customer = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (customer.isPresent()) {
            response.put("success", true);
            response.put("message", "Cliente encontrado com sucesso");
            response.put("data", customer.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Cliente não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // POST - Criar novo cliente
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCustomer(@RequestBody Customer customer) {
        try {
            Customer savedCustomer = repository.save(customer);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cliente criado com sucesso!");
            response.put("data", savedCustomer);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao criar cliente: " + e.getMessage());
            errorResponse.put("data", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // PUT - Atualizar cliente completo
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCustomer(@PathVariable Integer id, @RequestBody Customer customerDetails) {
        Optional<Customer> optionalCustomer = repository.findById(id);

        Map<String, Object> response = new HashMap<>();

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();

            // Atualiza todos os campos
            customer.setNameCustomers(customerDetails.getNameCustomers());
            customer.setEmailCustomers(customerDetails.getEmailCustomers());
            customer.setPhoneCustomers(customerDetails.getPhoneCustomers());
            customer.setAddressCustomers(customerDetails.getAddressCustomers());
            customer.setCpfCustomers(customerDetails.getCpfCustomers());

            Customer updatedCustomer = repository.save(customer);

            response.put("success", true);
            response.put("message", "Cliente atualizado com sucesso!");
            response.put("data", updatedCustomer);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Cliente não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // DELETE - Deletar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);

            response.put("success", true);
            response.put("message", "Cliente deletado com sucesso!");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Cliente não encontrado com ID: " + id);
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}