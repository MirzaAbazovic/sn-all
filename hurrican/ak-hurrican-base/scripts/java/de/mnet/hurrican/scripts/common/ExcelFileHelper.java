package de.mnet.hurrican.scripts.common;

import java.io.*;
import java.net.*;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.service.base.exceptions.StoreException;

public class ExcelFileHelper {

    private static final Logger LOGGER = Logger.getLogger(ExcelFileHelper.class);

    public Sheet loadExcelFile(String resource) throws StoreException {
        if (StringUtils.isEmpty(resource)) {
            return null;
        }

        try {
            URL importFileResource = this.getClass().getResource(resource);
            if (importFileResource == null) {
                throw new StoreException("Resource nicht gefunden.");
            }
            File source = new File(importFileResource.getFile());
            if (!source.exists()) {
                throw new StoreException("Datei nicht gefunden.");
            }
            Workbook workbook = Workbook.getWorkbook(source);
            Sheet sheet = workbook.getSheet(0);
            return (sheet != null) ? sheet : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage());
        }
    }

}
