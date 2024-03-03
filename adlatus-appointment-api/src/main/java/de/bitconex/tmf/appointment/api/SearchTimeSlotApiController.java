package de.bitconex.tmf.appointment.api;


import de.bitconex.tmf.appointment.model.SearchTimeSlot;
import de.bitconex.tmf.appointment.model.SearchTimeSlotCreate;
import de.bitconex.tmf.appointment.model.SearchTimeSlotUpdate;
import de.bitconex.tmf.appointment.service.SearchTimeSlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("searchTimeSlot")
public class SearchTimeSlotApiController implements SearchTimeSlotApi {

    private final SearchTimeSlotService searchTimeSlotService;

    public SearchTimeSlotApiController(SearchTimeSlotService searchTimeSlotService) {
        this.searchTimeSlotService = searchTimeSlotService;
    }

    @PostMapping
    public ResponseEntity<SearchTimeSlot> createSearchTimeSlot(@RequestBody SearchTimeSlotCreate searchTimeSlotCreate) {
        SearchTimeSlot createdSearchTimeSlot = searchTimeSlotService.createSearchTimeSlot(searchTimeSlotCreate);
        return ResponseEntity.ok(createdSearchTimeSlot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSearchTimeSlot(@PathVariable String id) {
        SearchTimeSlot searchTimeSlot = searchTimeSlotService.getSearchTimeSlotById(id, Optional.empty());

        if (searchTimeSlot == null) {
            return ResponseEntity.notFound().build();
        }

        searchTimeSlotService.deleteSearchTimeSlot(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SearchTimeSlot>> listSearchTimeSlot(
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit
    ) {
        List<SearchTimeSlot> searchTimeSlots = searchTimeSlotService.getAllSearchTimeSlots(fields, offset, limit);
        return ResponseEntity.ok(searchTimeSlots);
    }

    @PatchMapping("{id}")
    public ResponseEntity<SearchTimeSlot> patchSearchTimeSlot(@PathVariable String id, @RequestBody SearchTimeSlotUpdate searchTimeSlotUpdate) {
        SearchTimeSlot searchTimeSlot = searchTimeSlotService.updateSearchTimeSlot(id, searchTimeSlotUpdate);

        if (searchTimeSlot == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(searchTimeSlot);
    }

    @GetMapping("{id}")
    public ResponseEntity<SearchTimeSlot> retrieveSearchTimeSlot(@PathVariable String id, @RequestParam(required = false) Optional<String> fields) {
        SearchTimeSlot searchTimeSlot = searchTimeSlotService.getSearchTimeSlotById(id, fields);

        if (searchTimeSlot == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(searchTimeSlot);
    }
}
