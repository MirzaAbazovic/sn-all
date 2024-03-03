package de.bitconex.tmf.appointment.mapper;

import de.bitconex.tmf.appointment.model.SearchTimeSlot;
import de.bitconex.tmf.appointment.model.SearchTimeSlotCreate;
import org.mapstruct.Mapper;

@Mapper
public interface SearchTimeSlotMapper {

    SearchTimeSlot toSearchTimeSlot(SearchTimeSlotCreate searchTimeSlotCreate);

    SearchTimeSlotCreate toSearchTimeSlotCreate(SearchTimeSlot searchTimeSlot);
}
