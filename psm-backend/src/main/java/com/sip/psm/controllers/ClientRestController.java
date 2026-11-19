package com.sip.psm.controllers;

import com.sip.psm.entities.Client;
import com.sip.psm.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/clients")
@CrossOrigin(origins = "*")
public class ClientRestController {

    private final ClientService clientService;

    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping({"", "/list"})
    public List<Client> getAllClients() {
        return clientService.findAll();
    }

    @GetMapping("/page")
    public Page<Client> getClientsPage(@RequestParam(defaultValue = "") String keyword,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return clientService.searchAndPaginate(keyword, page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @GetMapping("/{id}/logo")
    public ResponseEntity<Resource> getClientLogo(@PathVariable Long id) {
        try {
            Resource resource = clientService.loadLogo(id);
            String contentType = "image/png";
            try {
                contentType = Files.probeContentType(resource.getFile().toPath());
            } catch (Exception ignored) {}
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping({"", "/add"})
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(client));
    }

    @PostMapping("/{id}/logo")
    public ResponseEntity<Client> uploadClientLogo(@PathVariable Long id,
                                                   @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(clientService.uploadLogo(id, file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @Valid @RequestBody Client clientRequest) {
        return ResponseEntity.ok(clientService.update(id, clientRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Client> deleteClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.delete(id));
    }
}
