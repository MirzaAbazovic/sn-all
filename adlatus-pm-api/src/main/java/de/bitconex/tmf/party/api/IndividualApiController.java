package de.bitconex.tmf.party.api;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import de.bitconex.tmf.party.models.Individual;
import de.bitconex.tmf.party.models.IndividualCreate;
import de.bitconex.tmf.party.models.IndividualUpdate;
import de.bitconex.tmf.party.service.IndividualService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/individual")
public class IndividualApiController implements IndividualApi {
    private final IndividualService individualService;

    Logger logger = LoggerFactory.getLogger(IndividualApiController.class);

    @Autowired
    public IndividualApiController(IndividualService individualService) {
        this.individualService = individualService;
    }


    @PostMapping
    public ResponseEntity<Individual> createIndividual(@RequestBody IndividualCreate individualCreate) {
        try {
            Individual createdIndividual = individualService.createIndividual(individualCreate);
            return new ResponseEntity<>(createdIndividual, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> listIndividual(
            String fields,
            Integer offset,
            Integer limit,
            Map<String, String> allParams
    ) {

        List<Individual> individuals = individualService.listIndividual(fields, offset, limit, allParams);
        return ResponseEntity.ok(individuals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieveIndividual(
            @PathVariable String id,
            String fields
    ) {
        Individual individual = individualService.retrieveIndividual(id, fields);
        if (individual == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(individual);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Individual> patchIndividual(@PathVariable String id, IndividualUpdate individual) {
        Individual updatedIndividual = individualService.patchIndividual(id, individual);
        if (updatedIndividual == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedIndividual);
    }

    @PatchMapping(value ="/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Individual> patchIndividual(@PathVariable String id, JsonNode operations) throws IOException, JsonPatchException {
        var updatedIndividual = individualService.patchIndividual(id, operations);
        if (updatedIndividual == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedIndividual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIndividual(@PathVariable String id) {
        var deleted = individualService.deleteIndividual(id);
        if (!deleted)
            return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
