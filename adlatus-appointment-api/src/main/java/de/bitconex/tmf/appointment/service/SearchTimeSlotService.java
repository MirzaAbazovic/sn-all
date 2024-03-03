package de.bitconex.tmf.appointment.service;

import de.bitconex.tmf.appointment.model.SearchTimeSlot;
import de.bitconex.tmf.appointment.model.SearchTimeSlotCreate;
import de.bitconex.tmf.appointment.model.SearchTimeSlotUpdate;

import java.util.List;
import java.util.Optional;

public interface SearchTimeSlotService {
    SearchTimeSlot createSearchTimeSlot(SearchTimeSlotCreate searchTimeSlotCreate);

    SearchTimeSlot getSearchTimeSlotById(String id, Optional<String> fields);

    List<SearchTimeSlot> getAllSearchTimeSlots(String fields, Integer offset, Integer limit);

    SearchTimeSlot updateSearchTimeSlot(String id, SearchTimeSlotUpdate searchTimeSlotUpdate);

    void deleteSearchTimeSlot(String id);
}
