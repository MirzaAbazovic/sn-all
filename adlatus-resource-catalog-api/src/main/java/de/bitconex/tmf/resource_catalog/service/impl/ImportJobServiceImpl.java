package de.bitconex.tmf.resource_catalog.service.impl;

import de.bitconex.tmf.resource_catalog.mapper.ImportJobMapper;
import de.bitconex.tmf.resource_catalog.mapper.ImportJobMapperImpl;
import de.bitconex.tmf.resource_catalog.model.ExportJob;
import de.bitconex.tmf.resource_catalog.model.ImportJob;
import de.bitconex.tmf.resource_catalog.model.ImportJobCreate;
import de.bitconex.tmf.resource_catalog.repository.ImportJobRepository;
import de.bitconex.tmf.resource_catalog.service.ImportJobService;
import de.bitconex.tmf.resource_catalog.utility.HrefTypes;
import de.bitconex.tmf.resource_catalog.utility.ModelUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ImportJobServiceImpl implements ImportJobService {
    private final ImportJobRepository importJobRepository;
    private final ImportJobMapper importJobMapper = new ImportJobMapperImpl();

    public ImportJobServiceImpl(ImportJobRepository importJobRepository) {
        this.importJobRepository = importJobRepository;
    }

    @Override
    public ImportJob createImportJob(ImportJobCreate importJob) {
        var importJobEntity = importJobMapper.toImportJob(importJob);
        ModelUtils.setIdAndHref(importJobEntity, HrefTypes.ImportJob.getHrefType());
        return importJobRepository.save(importJobEntity);
    }

    @Override
    public Boolean deleteImportJob(String id) {
        var importJobEntity = importJobRepository.findById(id).orElse(null);
        if (importJobEntity == null)
            return false;
        importJobRepository.delete(importJobEntity);
        return true;
    }

    @Override
    public List<ImportJob> listImportJob(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ModelUtils.getCollectionItems(ImportJob.class, fields, offset, limit, allParams);
    }

    @Override
    public ImportJob retrieveImportJob(String id, String fields) {
        return ModelUtils.getCollectionItem(ImportJob.class, id, fields);
    }
}
