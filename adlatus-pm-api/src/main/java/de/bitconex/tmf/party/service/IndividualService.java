package de.bitconex.tmf.party.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import de.bitconex.tmf.party.models.Individual;
import de.bitconex.tmf.party.models.IndividualCreate;
import de.bitconex.tmf.party.models.IndividualUpdate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IndividualService {
    Individual createIndividual(IndividualCreate individualCreate);

    Individual retrieveIndividual(String id, String fields);

    List<Individual> listIndividual(String fields, Integer offset, Integer limit, Map<String, String> allParams);

    Individual patchIndividual(String id, IndividualUpdate individualUpdate);

    Individual patchIndividual(String id, JsonNode operations) throws IOException, JsonPatchException;

    Boolean deleteIndividual(String id);
}
