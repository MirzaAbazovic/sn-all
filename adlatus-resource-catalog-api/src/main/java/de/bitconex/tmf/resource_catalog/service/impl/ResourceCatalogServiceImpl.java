package de.bitconex.tmf.resource_catalog.service.impl;

import de.bitconex.tmf.resource_catalog.mapper.ResourceCatalogMapper;
import de.bitconex.tmf.resource_catalog.mapper.ResourceCatalogMapperImpl;
import de.bitconex.tmf.resource_catalog.mapper.ResourceCategoryMapper;
import de.bitconex.tmf.resource_catalog.mapper.ResourceCategoryMapperImpl;
import de.bitconex.tmf.resource_catalog.model.ResourceCatalog;
import de.bitconex.tmf.resource_catalog.model.ResourceCatalogCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCatalogUpdate;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryRef;
import de.bitconex.tmf.resource_catalog.repository.ResourceCatalogRepository;
import de.bitconex.tmf.resource_catalog.repository.ResourceCategoryRepository;
import de.bitconex.tmf.resource_catalog.service.ResourceCatalogService;
import de.bitconex.tmf.resource_catalog.service.ResourceCategoryService;
import de.bitconex.tmf.resource_catalog.utility.HrefTypes;
import de.bitconex.tmf.resource_catalog.utility.ModelUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResourceCatalogServiceImpl implements ResourceCatalogService {
    private final ResourceCatalogRepository resourceCatalogRepository;
    private final ResourceCatalogMapper resourceCatalogMapper = new ResourceCatalogMapperImpl();
    private final ResourceCategoryMapper resourceCategoryMapper = new ResourceCategoryMapperImpl();
    private final ResourceCategoryRepository resourceCategoryRepository;
    private final ResourceCategoryService resourceCategoryService;

    public ResourceCatalogServiceImpl(ResourceCatalogRepository resourceCatalogRepository, ResourceCategoryRepository resourceCategoryRepository, ResourceCategoryService resourceCategoryService) {
        this.resourceCatalogRepository = resourceCatalogRepository;
        this.resourceCategoryRepository = resourceCategoryRepository;
        this.resourceCategoryService = resourceCategoryService;
    }

    @Override
    public ResourceCatalog createResourceCatalog(ResourceCatalogCreate resourceCatalog) {
        var resourceCatalogEntity = resourceCatalogMapper.toResourceCatalog(resourceCatalog);
        ModelUtils.setIdAndHref(resourceCatalogEntity, HrefTypes.ResourceCatalog.getHrefType());
        resourceCatalogEntity.setLastUpdate(OffsetDateTime.now());
        if (resourceCatalogEntity.getLifecycleStatus() == null)
            resourceCatalogEntity.setLifecycleStatus("Active");
        addNestedFields(resourceCatalogEntity);
        resourceCatalogRepository.save(resourceCatalogEntity);
        return resourceCatalogEntity;
    }

    @Override
    public Boolean deleteResourceCatalog(String id) {
        var resourceCatalog = resourceCatalogRepository.findById(id).orElse(null);
        if (resourceCatalog == null)
            return false;
        resourceCatalogRepository.delete(resourceCatalog);
        return true;
    }

    @Override
    public List<ResourceCatalog> listResourceCatalog(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ModelUtils.getCollectionItems(ResourceCatalog.class, fields, offset, limit, allParams);
    }

    @Override
    public ResourceCatalog patchResourceCatalog(String id, ResourceCatalogUpdate resourceCatalog) {
        var resourceCatalogEntity = resourceCatalogRepository.findById(id).orElse(null);
        if (resourceCatalogEntity == null)
            return null;
        var updatedResourceCatalog = resourceCatalogMapper.toResourceCatalog(resourceCatalog);
        ModelUtils.patchEntity(resourceCatalogEntity, updatedResourceCatalog);
        updatedResourceCatalog.setLastUpdate(OffsetDateTime.now());
        addNestedFields(updatedResourceCatalog);
        resourceCatalogRepository.save(updatedResourceCatalog);
        return updatedResourceCatalog;
    }

    @Override
    public ResourceCatalog retrieveResourceCatalog(String id, String fields) {
        return ModelUtils.getCollectionItem(ResourceCatalog.class, id, fields);
    }

    private void addNestedFields(ResourceCatalog resourceCatalog) {
        if (resourceCatalog.getCategory() != null) {
            List<ResourceCategoryRef> categories = new ArrayList<>();
            for (var category : resourceCatalog.getCategory()) {
                var resCat = resourceCategoryRepository.findById(category.getId()).orElse(null);
                if (resCat == null)
                    resCat = resourceCategoryService.createResourceCategory(category);
                category = resourceCategoryMapper.toResourceCategoryRef(resCat);
                categories.add(category);
            }
            resourceCatalog.setCategory(categories);
        }
    }
}
