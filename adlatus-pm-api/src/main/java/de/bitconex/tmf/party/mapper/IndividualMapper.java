package de.bitconex.tmf.party.mapper;


import de.bitconex.tmf.party.models.Individual;
import de.bitconex.tmf.party.models.IndividualCreate;
import de.bitconex.tmf.party.models.IndividualUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IndividualMapper {

    @Mapping(target = "isDeleted", constant = "false")
    Individual toIndividual(IndividualCreate dto);

    Individual toIndividual(IndividualUpdate dto);
}
