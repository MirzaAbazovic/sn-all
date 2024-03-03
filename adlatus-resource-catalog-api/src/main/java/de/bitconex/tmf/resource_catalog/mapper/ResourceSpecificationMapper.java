package de.bitconex.tmf.resource_catalog.mapper;

import de.bitconex.tmf.resource_catalog.model.ResourceSpecification;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationRef;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationUpdate;
import org.mapstruct.Mapper;

@Mapper
public interface ResourceSpecificationMapper {

    ResourceSpecificationRef toResourceSpecificationRef(ResourceSpecification resourceSpecification);


    ResourceSpecification toResourceSpecification(ResourceSpecificationCreate dto);

    ResourceSpecification toResourceSpecification(ResourceSpecificationRef dto);

    ResourceSpecification toResourceSpecification(ResourceSpecificationUpdate dto);
}
