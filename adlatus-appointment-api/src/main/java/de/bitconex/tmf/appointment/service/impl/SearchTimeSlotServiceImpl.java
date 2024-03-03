package de.bitconex.tmf.appointment.service.impl;

import de.bitconex.tmf.appointment.mapper.SearchTimeSlotMapper;
import de.bitconex.tmf.appointment.mapper.SearchTimeSlotMapperImpl;
import de.bitconex.tmf.appointment.model.SearchTimeSlot;
import de.bitconex.tmf.appointment.model.SearchTimeSlotCreate;
import de.bitconex.tmf.appointment.model.SearchTimeSlotUpdate;
import de.bitconex.tmf.appointment.repository.SearchTimeSlotRepository;
import de.bitconex.tmf.appointment.service.SearchTimeSlotService;
import de.bitconex.tmf.appointment.util.QueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SearchTimeSlotServiceImpl implements SearchTimeSlotService {

    private final SearchTimeSlotRepository searchTimeSlotRepository;

    private final SearchTimeSlotMapper searchTimeSlotMapper = new SearchTimeSlotMapperImpl();

    private final MongoTemplate mongoTemplate;

    private final Logger logger = LoggerFactory.getLogger(SearchTimeSlotService.class);

    public SearchTimeSlotServiceImpl(SearchTimeSlotRepository searchTimeSlotRepository, MongoTemplate mongoTemplate) {
        this.searchTimeSlotRepository = searchTimeSlotRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public SearchTimeSlot createSearchTimeSlot(SearchTimeSlotCreate searchTimeSlotCreate) {
        SearchTimeSlot searchTimeSlot = searchTimeSlotMapper.toSearchTimeSlot(searchTimeSlotCreate);
        return searchTimeSlotRepository.save(searchTimeSlot);
    }

    @Override
    public SearchTimeSlot getSearchTimeSlotById(String id, Optional<String> fields) {
        return searchTimeSlotRepository.findById(id).orElse(null);
    }

    @Override
    public List<SearchTimeSlot> getAllSearchTimeSlots(String fields, Integer offset, Integer limit) {
        Query query = QueryUtil.createQueryWithIncludedFields(fields, SearchTimeSlot.class);

        int page = offset != null && offset >= 0 ? offset : 0;
        int size = limit != null && limit > 0 ? limit : 10;

        query.with(PageRequest.of(page, size));

        return mongoTemplate.find(query, SearchTimeSlot.class);
    }

    @Override
    public SearchTimeSlot updateSearchTimeSlot(String id, SearchTimeSlotUpdate searchTimeSlotUpdate) {
        SearchTimeSlot searchTimeSlot = this.getSearchTimeSlotById(id, Optional.empty());

        if (searchTimeSlot == null) {
            return null;
        }

        if (searchTimeSlotUpdate.getAvailableTimeSlot() != null) {
            searchTimeSlot.setAvailableTimeSlot(searchTimeSlotUpdate.getAvailableTimeSlot());
        }

        if (searchTimeSlotUpdate.getRequestedTimeSlot() != null) {
            searchTimeSlot.setRequestedTimeSlot(searchTimeSlotUpdate.getRequestedTimeSlot());
        }

        return searchTimeSlotRepository.save(searchTimeSlot);
    }

    @Override
    public void deleteSearchTimeSlot(String id) {
        searchTimeSlotRepository.deleteById(id);
    }
}
