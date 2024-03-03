package de.bitconex.tmf.resource_catalog.service.impl;

import de.bitconex.tmf.resource_catalog.mapper.ResourceSpecificationMapper;
import de.bitconex.tmf.resource_catalog.mapper.ResourceSpecificationMapperImpl;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecification;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationRef;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationUpdate;
import de.bitconex.tmf.resource_catalog.repository.ResourceSpecificationRepository;
import de.bitconex.tmf.resource_catalog.service.ResourceSpecificationService;
import de.bitconex.tmf.resource_catalog.utility.HrefTypes;
import de.bitconex.tmf.resource_catalog.utility.ModelUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ResourceSpecificationServiceImpl implements ResourceSpecificationService {
    private final ResourceSpecificationRepository resourceSpecificationRepository;
    private final ResourceSpecificationMapper resourceSpecificationMapper = new ResourceSpecificationMapperImpl();

    public ResourceSpecificationServiceImpl(ResourceSpecificationRepository resourceSpecificationRepository) {
        this.resourceSpecificationRepository = resourceSpecificationRepository;
    }

    @Override
    public ResourceSpecification createResourceSpecification(ResourceSpecificationCreate resourceSpecification) {
        var resourceSpecificationEntity = resourceSpecificationMapper.toResourceSpecification(resourceSpecification);
        return create(resourceSpecificationEntity);
    }

    @Override
    public ResourceSpecification createResourceSpecification(ResourceSpecificationRef resourceSpecification) {
        var resourceSpecificationEntity = resourceSpecificationMapper.toResourceSpecification(resourceSpecification);
        return create(resourceSpecificationEntity);
    }

    @Override
    public Boolean deleteResourceSpecification(String id) {
        var resourceSpecification = resourceSpecificationRepository.findById(id).orElse(null);
        if (resourceSpecification == null)
            return false;
        resourceSpecificationRepository.delete(resourceSpecification);
        return true;
    }

    @Override
    public List<ResourceSpecification> listResourceSpecification(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ModelUtils.getCollectionItems(ResourceSpecification.class, fields, offset, limit, allParams);
    }

    @Override
    public ResourceSpecification patchResourceSpecification(String id, ResourceSpecificationUpdate resourceSpecification) {
        var resourceSpecificationEntity = resourceSpecificationRepository.findById(id).orElse(null);
        if (resourceSpecificationEntity == null)
            return null;
        var updatedResourceSpecification = resourceSpecificationMapper.toResourceSpecification(resourceSpecification);
        ModelUtils.patchEntity(resourceSpecificationEntity, updatedResourceSpecification);
        resourceSpecification.setLastUpdate(OffsetDateTime.now());
        resourceSpecificationRepository.save(updatedResourceSpecification);
        return updatedResourceSpecification;
    }

    @Override
    public ResourceSpecification retrieveResourceSpecification(String id, String fields) {
        return ModelUtils.getCollectionItem(ResourceSpecification.class, id, fields);
    }

    private ResourceSpecification create(ResourceSpecification resourceSpecification) {
        ModelUtils.setIdAndHref(resourceSpecification, HrefTypes.ResourceSpecification.getHrefType());
        resourceSpecification.setLastUpdate(OffsetDateTime.now());
        if (resourceSpecification.getLifecycleStatus() == null)
            resourceSpecification.setLifecycleStatus("Active");
        if (resourceSpecification.getIsBundle() == null)
            resourceSpecification.setIsBundle(false);
        resourceSpecificationRepository.save(resourceSpecification);
        return resourceSpecification;
    }

}
