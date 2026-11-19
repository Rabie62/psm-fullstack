package com.sip.psm.controllers;

import com.sip.psm.entities.ETaskStatus;
import com.sip.psm.entities.Tache;
import com.sip.psm.services.TacheService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/taches")
@CrossOrigin(origins = "*")
public class TacheRestController {

    private final TacheService tacheService;

    public TacheRestController(TacheService tacheService) {
        this.tacheService = tacheService;
    }

    @GetMapping({"", "/list"})
    public List<Tache> getAllTaches() {
        return tacheService.findAll();
    }

    @GetMapping("/projet/{projetId}")
    public List<Tache> getTachesByProjet(@PathVariable Long projetId) {
        return tacheService.findByProjetId(projetId);
    }

    @PostMapping("/add/{projetId}")
    public ResponseEntity<Tache> createTache(@PathVariable Long projetId,
                                             @RequestParam(required = false) Long collaborateurId,
                                             @Valid @RequestBody Tache tache) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tacheService.create(projetId, collaborateurId, tache));
    }

    @PatchMapping("/{tacheId}/status")
    public ResponseEntity<Tache> updateTaskStatus(@PathVariable Long tacheId,
                                                  @RequestBody Map<String, Object> payload) {
        ETaskStatus statut = ETaskStatus.valueOf(String.valueOf(payload.getOrDefault("statut", "TODO")));
        int progression = Integer.parseInt(String.valueOf(payload.getOrDefault("progression", 0)));
        return ResponseEntity.ok(tacheService.updateStatusAndProgress(tacheId, statut, progression));
    }

    @DeleteMapping("/{tacheId}")
    public ResponseEntity<?> deleteTache(@PathVariable Long tacheId) {
        tacheService.delete(tacheId);
        return ResponseEntity.ok(Map.of("message", "Tâche supprimée avec succès"));
    }
}
