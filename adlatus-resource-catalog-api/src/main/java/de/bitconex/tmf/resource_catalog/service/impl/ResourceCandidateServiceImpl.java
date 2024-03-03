package de.bitconex.tmf.resource_catalog.service.impl;

import de.bitconex.tmf.resource_catalog.mapper.*;
import de.bitconex.tmf.resource_catalog.model.*;
import de.bitconex.tmf.resource_catalog.repository.ResourceCandidateRepository;
import de.bitconex.tmf.resource_catalog.repository.ResourceCategoryRepository;
import de.bitconex.tmf.resource_catalog.repository.ResourceSpecificationRepository;
import de.bitconex.tmf.resource_catalog.service.ResourceCandidateService;
import de.bitconex.tmf.resource_catalog.service.ResourceCategoryService;
import de.bitconex.tmf.resource_catalog.service.ResourceSpecificationService;
import de.bitconex.tmf.resource_catalog.utility.HrefTypes;
import de.bitconex.tmf.resource_catalog.utility.ModelUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResourceCandidateServiceImpl implements ResourceCandidateService {
    private final ResourceCandidateMapper resourceCandidateMapper = new ResourceCandidateMapperImpl();
    private final ResourceSpecificationMapper resourceSpecificationMapper = new ResourceSpecificationMapperImpl();
    private final ResourceCategoryMapper resourceCategoryMapper = new ResourceCategoryMapperImpl();
    private final ResourceCandidateRepository resourceCandidateRepository;
    private final ResourceSpecificationRepository resourceSpecificationRepository;
    private final ResourceCategoryRepository resourceCategoryRepository;
    private final ResourceSpecificationService resourceSpecificationService;
    private final ResourceCategoryService resourceCategoryService;


    public ResourceCandidateServiceImpl(ResourceCandidateRepository resourceCandidateRepository, ResourceSpecificationRepository resourceSpecificationRepository, ResourceSpecificationService resourceSpecificationService, ResourceCategoryRepository resourceCategoryRepository, @Lazy ResourceCategoryService resourceCategoryService) {
        this.resourceCandidateRepository = resourceCandidateRepository;
        this.resourceSpecificationRepository = resourceSpecificationRepository;
        this.resourceSpecificationService = resourceSpecificationService;
        this.resourceCategoryRepository = resourceCategoryRepository;
        this.resourceCategoryService = resourceCategoryService;
    }

    @Override
    public ResourceCandidate createResourceCandidate(ResourceCandidateCreate resourceCandidateCreate) {
        var resourceCandidate = resourceCandidateMapper.toResourceCandidate(resourceCandidateCreate);
        ModelUtils.setIdAndHref(resourceCandidate, HrefTypes.ResourceCandidate.getHrefType());
        resourceCandidate.setLastUpdate(OffsetDateTime.now());
        if (resourceCandidate.getLifecycleStatus() == null)
            resourceCandidate.setLifecycleStatus("Active");
        addNestedFields(resourceCandidate);
        resourceCandidateRepository.save(resourceCandidate);
        return resourceCandidate;
    }

    @Override
    public ResourceCandidate createResourceCandidate(ResourceCandidateRef resourceCandidate) {
        var resourceCandidateEntity = resourceCandidateMapper.toResourceCandidate(resourceCandidate);
        ModelUtils.setIdAndHref(resourceCandidateEntity, HrefTypes.ResourceCandidate.getHrefType());
        resourceCandidateEntity.setLastUpdate(OffsetDateTime.now());
        if (resourceCandidateEntity.getLifecycleStatus() == null)
            resourceCandidateEntity.setLifecycleStatus("Active");
        resourceCandidateRepository.save(resourceCandidateEntity);
        return resourceCandidateEntity;
    }

    @Override
    public Boolean deleteResourceCandidate(String id) {
        var resourceCandidate = resourceCandidateRepository.findById(id).orElse(null);
        if (resourceCandidate == null)
            return false;
        resourceCandidateRepository.delete(resourceCandidate);
        return true;
    }

    @Override
    public List<ResourceCandidate> listResourceCandidate(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ModelUtils.getCollectionItems(ResourceCandidate.class, fields, offset, limit, allParams);
    }


    @Override
    public ResourceCandidate patchResourceCandidate(String id, ResourceCandidateUpdate resourceCandidate) {
        var resourceCandidateEntity = resourceCandidateRepository.findById(id).orElse(null);
        if (resourceCandidateEntity == null)
            return null;
        var updatedResourceCandidate = resourceCandidateMapper.toResourceCandidate(resourceCandidate);
        ModelUtils.patchEntity(resourceCandidateEntity, updatedResourceCandidate);
        addNestedFields(updatedResourceCandidate);
        resourceCandidateRepository.save(updatedResourceCandidate);
        return updatedResourceCandidate;
    }

    @Override
    public ResourceCandidate retrieveResourceCandidate(String id, String fields) {
        return ModelUtils.getCollectionItem(ResourceCandidate.class, id, fields);
    }

    private void addNestedFields(ResourceCandidate resourceCandidate) {
        if (resourceCandidate.getResourceSpecification() != null) {
            var resSpec = resourceSpecificationRepository.findById(resourceCandidate.getResourceSpecification().getId()).orElse(null);
            if (resSpec == null)
                resSpec = resourceSpecificationService.createResourceSpecification(resourceCandidate.getResourceSpecification());
            resourceCandidate.setResourceSpecification(resourceSpecificationMapper.toResourceSpecificationRef(resSpec));
        }
        if (resourceCandidate.getCategory() != null) {
            List<ResourceCategoryRef> categories = new ArrayList<>();
            for (var category : resourceCandidate.getCategory()) {
                var resCat = resourceCategoryRepository.findById(category.getId()).orElse(null);
                if (resCat == null)
                    resCat = resourceCategoryService.createResourceCategory(category);
                category = resourceCategoryMapper.toResourceCategoryRef(resCat);
                categories.add(category);
            }
            resourceCandidate.setCategory(categories);
        }
    }
}
