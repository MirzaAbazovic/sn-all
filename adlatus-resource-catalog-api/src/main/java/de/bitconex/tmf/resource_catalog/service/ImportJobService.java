package de.bitconex.tmf.resource_catalog.service;

import de.bitconex.tmf.resource_catalog.api.ImportJobApi;
import de.bitconex.tmf.resource_catalog.model.ImportJob;
import de.bitconex.tmf.resource_catalog.model.ImportJobCreate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ImportJobService {

    ImportJob createImportJob(ImportJobCreate importJob);


    Boolean deleteImportJob(String id);


    List<ImportJob> listImportJob(String fields, Integer offset, Integer limit, Map<String, String> allParams);


    ImportJob retrieveImportJob(String id, String fields);
}
