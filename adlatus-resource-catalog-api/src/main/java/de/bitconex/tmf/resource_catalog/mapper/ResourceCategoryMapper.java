package de.bitconex.tmf.resource_catalog.mapper;

import de.bitconex.tmf.resource_catalog.model.ResourceCategory;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryRef;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryUpdate;
import org.mapstruct.Mapper;

@Mapper
public interface ResourceCategoryMapper {

    ResourceCategoryRef toResourceCategoryRef(ResourceCategory resourceCategory);

    ResourceCategory toResourceCategory(ResourceCategoryCreate resourceCategoryCreate);

    ResourceCategory toResourceCategory(ResourceCategoryRef resourceCategoryRef);

    ResourceCategory toResourceCategory(ResourceCategoryUpdate resourceCategoryUpdate);

}
