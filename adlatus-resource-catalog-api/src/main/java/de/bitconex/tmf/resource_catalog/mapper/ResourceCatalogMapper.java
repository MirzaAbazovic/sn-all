package de.bitconex.tmf.resource_catalog.mapper;

import de.bitconex.tmf.resource_catalog.model.ResourceCatalog;
import de.bitconex.tmf.resource_catalog.model.ResourceCatalogCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCatalogUpdate;
import org.mapstruct.Mapper;

@Mapper
public interface ResourceCatalogMapper {

    ResourceCatalog toResourceCatalog(ResourceCatalogCreate resourceCatalogCreate);

    ResourceCatalog toResourceCatalog(ResourceCatalogUpdate resourceCatalogUpdate);
}
