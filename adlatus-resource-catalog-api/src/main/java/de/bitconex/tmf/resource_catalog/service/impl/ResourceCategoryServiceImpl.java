package de.bitconex.tmf.resource_catalog.service.impl;

import de.bitconex.tmf.resource_catalog.mapper.ResourceCandidateMapper;
import de.bitconex.tmf.resource_catalog.mapper.ResourceCandidateMapperImpl;
import de.bitconex.tmf.resource_catalog.mapper.ResourceCategoryMapper;
import de.bitconex.tmf.resource_catalog.mapper.ResourceCategoryMapperImpl;
import de.bitconex.tmf.resource_catalog.model.*;
import de.bitconex.tmf.resource_catalog.repository.ResourceCandidateRepository;
import de.bitconex.tmf.resource_catalog.repository.ResourceCategoryRepository;
import de.bitconex.tmf.resource_catalog.service.ResourceCandidateService;
import de.bitconex.tmf.resource_catalog.service.ResourceCategoryService;
import de.bitconex.tmf.resource_catalog.utility.HrefTypes;
import de.bitconex.tmf.resource_catalog.utility.ModelUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResourceCategoryServiceImpl implements ResourceCategoryService {
    private final ResourceCategoryRepository resourceCategoryRepository;
    private final ResourceCategoryMapper resourceCategoryMapper = new ResourceCategoryMapperImpl();
    private final ResourceCandidateMapper resourceCandidateMapper = new ResourceCandidateMapperImpl();
    private final ResourceCandidateRepository resourceCandidateRepository;
    private final ResourceCandidateService resourceCandidateService;

    public ResourceCategoryServiceImpl(ResourceCategoryRepository resourceCategoryRepository, ResourceCandidateRepository resourceCandidateRepository, ResourceCandidateService resourceCandidateService) {
        this.resourceCategoryRepository = resourceCategoryRepository;
        this.resourceCandidateRepository = resourceCandidateRepository;
        this.resourceCandidateService = resourceCandidateService;
    }

    @Override
    public ResourceCategory createResourceCategory(ResourceCategoryCreate resourceCategory) {
        var resourceCategoryEntity = resourceCategoryMapper.toResourceCategory(resourceCategory);
        ModelUtils.setIdAndHref(resourceCategoryEntity, HrefTypes.ResourceCategory.getHrefType());
        resourceCategoryEntity.setLastUpdate(OffsetDateTime.now());
        if (resourceCategoryEntity.getLifecycleStatus() == null)
            resourceCategoryEntity.setLifecycleStatus("Active");
        addNestedFields(resourceCategoryEntity);
        resourceCategoryRepository.save(resourceCategoryEntity);
        return resourceCategoryEntity;
    }

    @Override
    public ResourceCategory createResourceCategory(ResourceCategoryRef resourceCategory) {
        var resourceCategoryEntity = resourceCategoryMapper.toResourceCategory(resourceCategory);
        ModelUtils.setIdAndHref(resourceCategoryEntity, HrefTypes.ResourceCategory.getHrefType());
        resourceCategoryEntity.setLastUpdate(OffsetDateTime.now());
        if (resourceCategoryEntity.getLifecycleStatus() == null)
            resourceCategoryEntity.setLifecycleStatus("Active");
        resourceCategoryRepository.save(resourceCategoryEntity);
        return resourceCategoryEntity;
    }

    @Override
    public Boolean deleteResourceCategory(String id) {
        var resourceCategory = resourceCategoryRepository.findById(id).orElse(null);
        if (resourceCategory == null)
            return false;
        resourceCategoryRepository.delete(resourceCategory);
        return true;
    }

    @Override
    public List<ResourceCategory> listResourceCategory(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ModelUtils.getCollectionItems(ResourceCategory.class, fields, offset, limit, allParams);
    }

    @Override
    public ResourceCategory patchResourceCategory(String id, ResourceCategoryUpdate resourceCategory) {
        var resourceCategoryEntity = resourceCategoryRepository.findById(id).orElse(null);
        if (resourceCategoryEntity == null)
            return null;
        var updatedResourceCategory = resourceCategoryMapper.toResourceCategory(resourceCategory);
        ModelUtils.patchEntity(resourceCategoryEntity, updatedResourceCategory);
        updatedResourceCategory.setLastUpdate(OffsetDateTime.now());
        addNestedFields(updatedResourceCategory);
        resourceCategoryRepository.save(updatedResourceCategory);
        return updatedResourceCategory;
    }

    @Override
    public ResourceCategory retrieveResourceCategory(String id, String fields) {
        return ModelUtils.getCollectionItem(ResourceCategory.class, id, fields);
    }

    private void addNestedFields(ResourceCategory resourceCategory) {
        if (resourceCategory.getResourceCandidate() != null) {
            List<ResourceCandidateRef> candidates = new ArrayList<>();
            for (var candidate : resourceCategory.getResourceCandidate()) {
                var candidateEntity = resourceCandidateRepository.findById(candidate.getId()).orElse(null);
                if (candidateEntity == null)
                    candidateEntity = resourceCandidateService.createResourceCandidate(candidate);
                candidates.add(resourceCandidateMapper.toResourceCandidateRef(candidateEntity));
            }
            resourceCategory.setResourceCandidate(candidates);
        }
        if (resourceCategory.getCategory() != null) {
            List<ResourceCategoryRef> categories = new ArrayList<>();
            for (var category : resourceCategory.getCategory()) {
                var categoryEntity = resourceCategoryRepository.findById(category.getId()).orElse(null);
                if (categoryEntity == null)
                    categoryEntity = createResourceCategory(category);
                categories.add(resourceCategoryMapper.toResourceCategoryRef(categoryEntity));
            }
            resourceCategory.setCategory(categories);
        }
    }
}
