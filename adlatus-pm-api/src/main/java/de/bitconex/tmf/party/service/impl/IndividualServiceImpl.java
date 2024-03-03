package de.bitconex.tmf.party.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import de.bitconex.tmf.party.mapper.IndividualMapper;
import de.bitconex.tmf.party.mapper.IndividualMapperImpl;
import de.bitconex.tmf.party.models.Individual;
import de.bitconex.tmf.party.models.IndividualCreate;
import de.bitconex.tmf.party.models.IndividualStateType;
import de.bitconex.tmf.party.models.IndividualUpdate;
import de.bitconex.tmf.party.repository.IndividualRepository;
import de.bitconex.tmf.party.service.IndividualService;
import de.bitconex.tmf.party.service.SubscriptionService;
import de.bitconex.tmf.party.utility.HrefTypes;
import de.bitconex.tmf.party.utility.ModelUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class IndividualServiceImpl implements IndividualService {

    private final IndividualRepository individualRepository;
    private final IndividualMapper individualMapper = new IndividualMapperImpl();

    private final SubscriptionService subscriptionService;

    private final ObjectMapper objectMapper;

    public IndividualServiceImpl(IndividualRepository individualRepository, SubscriptionService subscriptionService, ObjectMapper objectMapper) {
        this.individualRepository = individualRepository;
        this.subscriptionService = subscriptionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Individual createIndividual(IndividualCreate individualCreate) {
        var individual = individualMapper.toIndividual(individualCreate);


        ModelUtils.setIdAndHref(individual, HrefTypes.Individual.getHrefType());
        individual.setFullName(String.format("%s %s", individual.getGivenName(), individual.getFamilyName()));
        individual.setStatus(IndividualStateType.INITIALIZED);

        return individualRepository.save(individual);
    }

    @Override
    public Individual retrieveIndividual(String id, String fields) {
        return ModelUtils.getCollectionItem(Individual.class, id, fields);
    }

    @Override
    public List<Individual> listIndividual(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ModelUtils.getCollectionItems(Individual.class, fields, offset, limit, allParams);
    }

    @Override
    public Individual patchIndividual(String id, IndividualUpdate individualUpdate) {
        var individual = individualRepository.findById(id).orElse(null);
        if (individual == null)
            return null;
        var updatedIndividual = individualMapper.toIndividual(individualUpdate);
        ModelUtils.patchEntity(individual, updatedIndividual);
        return individualRepository.save(updatedIndividual);
    }

    @Override
    public Individual patchIndividual(String id, JsonNode operations) throws IOException, JsonPatchException {
        JsonPatch patch = JsonPatch.fromJson(operations);
        Individual individual = individualRepository.findById(id).orElse(null);
        if (individual == null)
            return null;
        JsonNode patchedIndividual = patch.apply(objectMapper.convertValue(individual, JsonNode.class));
        Individual updatedIndividual = objectMapper.treeToValue(patchedIndividual, Individual.class);
        return individualRepository.save(updatedIndividual);
    }

    @Override
    public Boolean deleteIndividual(String id) {
        var individual = individualRepository.findById(id).orElse(null);
        if (individual == null)
            return false;
        individual.setIsDeleted(true);
        individualRepository.save(individual);
        return true;
    }
}
