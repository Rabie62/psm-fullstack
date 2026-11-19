package com.sip.psm.controllers;

import com.sip.psm.entities.Projet;
import com.sip.psm.services.ProjetService;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/projets")
@CrossOrigin(origins = "*")
public class ProjetRestController {

    private final ProjetService projetService;

    public ProjetRestController(ProjetService projetService) {
        this.projetService = projetService;
    }

    @GetMapping({"", "/list"})
    public List<Projet> getAllProjets() {
        return projetService.findAll();
    }

    @GetMapping("/page")
    public Page<Projet> getProjetsPage(@RequestParam(defaultValue = "") String keyword,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return projetService.searchAndPaginate(keyword, page, size);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv() {
        byte[] csvBytes = projetService.exportProjetsCsv().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=projets_psm.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projet> getProjetById(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    public List<Projet> getProjetsByClient(@PathVariable Long clientId) {
        return projetService.findByClientId(clientId);
    }

    @PostMapping({"/{clientId}", "/add/{clientId}"})
    public ResponseEntity<Projet> createProjet(@PathVariable Long clientId,
                                               @Valid @RequestBody Projet projet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetService.create(clientId, projet));
    }

    @PostMapping("/{id}/document")
    public ResponseEntity<Projet> uploadDocument(@PathVariable Long id,
                                                 @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(projetService.uploadCahierCharges(id, file));
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Resource resource = projetService.loadCahierCharges(id);
        String contentType = "application/octet-stream";
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (Exception ignored) {}
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        String filename = resource.getFilename();
        String displayFilename = (filename != null && filename.contains("_"))
                ? filename.substring(filename.indexOf('_') + 1)
                : (filename != null ? filename : "cahier_des_charges.pdf");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + displayFilename + "\"")
                .body(resource);
    }

    @PutMapping({"/update/{clientId}/{projetId}", "/{clientId}/{projetId}"})
    public ResponseEntity<Projet> updateProjet(@PathVariable Long clientId,
                                               @PathVariable Long projetId,
                                               @Valid @RequestBody Projet projetRequest) {
        return ResponseEntity.ok(projetService.update(clientId, projetId, projetRequest));
    }

    @DeleteMapping({"/{projetId}", "/delete/{projetId}"})
    public ResponseEntity<Projet> deleteProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(projetService.delete(projetId));
    }
}
