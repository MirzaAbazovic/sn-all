package de.bitconex.tmf.resource_catalog.mapper;

import de.bitconex.tmf.resource_catalog.model.ExportJob;
import de.bitconex.tmf.resource_catalog.model.ExportJobCreate;
import org.mapstruct.Mapper;

@Mapper
public interface ExportJobMapper {
    ExportJob toExportJob(ExportJobCreate exportJobCreate);
}
