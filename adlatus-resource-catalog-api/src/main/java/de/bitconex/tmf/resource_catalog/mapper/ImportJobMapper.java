package de.bitconex.tmf.resource_catalog.mapper;

import de.bitconex.tmf.resource_catalog.model.ImportJob;
import de.bitconex.tmf.resource_catalog.model.ImportJobCreate;
import org.mapstruct.Mapper;

@Mapper
public interface ImportJobMapper {
    ImportJob toImportJob(ImportJobCreate importJobCreate);
}
