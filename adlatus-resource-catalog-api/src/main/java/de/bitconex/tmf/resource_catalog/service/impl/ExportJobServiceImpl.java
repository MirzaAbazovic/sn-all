package de.bitconex.tmf.resource_catalog.service.impl;

import de.bitconex.tmf.resource_catalog.mapper.ExportJobMapper;
import de.bitconex.tmf.resource_catalog.mapper.ExportJobMapperImpl;
import de.bitconex.tmf.resource_catalog.model.ExportJob;
import de.bitconex.tmf.resource_catalog.model.ExportJobCreate;
import de.bitconex.tmf.resource_catalog.repository.ExportJobRepository;
import de.bitconex.tmf.resource_catalog.service.ExportJobService;
import de.bitconex.tmf.resource_catalog.utility.HrefTypes;
import de.bitconex.tmf.resource_catalog.utility.ModelUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExportJobServiceImpl implements ExportJobService {
    private final ExportJobRepository exportJobRepository;
    private final ExportJobMapper exportJobMapper = new ExportJobMapperImpl();

    public ExportJobServiceImpl(ExportJobRepository exportJobRepository) {
        this.exportJobRepository = exportJobRepository;
    }

    @Override
    public ExportJob createExportJob(ExportJobCreate exportJob) {
        var exportJobEntity = exportJobMapper.toExportJob(exportJob);
        ModelUtils.setIdAndHref(exportJobEntity, HrefTypes.ExportJob.getHrefType());
        return exportJobRepository.save(exportJobEntity);
    }

    @Override
    public Boolean deleteExportJob(String id) {
        var exportJobEntity = exportJobRepository.findById(id).orElse(null);
        if (exportJobEntity == null)
            return false;
        exportJobRepository.delete(exportJobEntity);
        return true;
    }

    @Override
    public List<ExportJob> listExportJob(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ModelUtils.getCollectionItems(ExportJob.class, fields, offset, limit, allParams);
    }

    @Override
    public ExportJob retrieveExportJob(String id, String fields) {
        return ModelUtils.getCollectionItem(ExportJob.class, id, fields);
    }
}
