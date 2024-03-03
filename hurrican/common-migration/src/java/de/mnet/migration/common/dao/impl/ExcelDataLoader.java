/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2010 16:48:44
 */
package de.mnet.migration.common.dao.impl;

import java.util.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import de.mnet.migration.common.dao.DataLoader;
import de.mnet.migration.common.util.excel.ExcelMapper;


/**
 * Dao, das ein Excel-Sheet ausliest. Das Excel-Sheet wird beim Auslesen direkt auf die Zielentity gemapped.<br/>
 * <b>Thread-Safe.</b><br/> <br/> Set the following parameters:<ul> <li>excelFile</li> <li>entityClass</li>
 * <li>sheetNumber (default: 0)</li> <li>captionRow (default: null -> firstRowNow)</li> <li>predicate (default:
 * null)</li> </ul>
 */
public class ExcelDataLoader<T> implements DataLoader<T> {

    private static final Logger LOGGER = Logger.getLogger(ExcelDataLoader.class);

    private Class<T> entityClass;
    private Resource excelFile;
    private Integer sheetNumber = Integer.valueOf(0);
    private Integer captionRow = null;
    private volatile List<T> all;
    private Predicate<T> predicate;

    @Override
    public List<T> getSourceData() {
        ExcelMapper.Config config = new ExcelMapper.Config().setCaptionRow(captionRow).setSheetNumber(sheetNumber);
        all = ExcelMapper.map(excelFile, entityClass, config);
        if (predicate != null) {
            all = Lists.newArrayList(Iterators.filter(all.iterator(), predicate));
        }
        LOGGER.info(all.size() + " rows from " + excelFile + " loaded and mapped to entity " + getSourceObjectName());
        return all;
    }

    @Override
    public String getSourceObjectName() {
        return entityClass.getCanonicalName();
    }

    @Required
    public void setExcelFile(Resource excelFile) {
        this.excelFile = excelFile;
    }

    @Required
    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Injected
     */
    public void setSheetNumber(Integer sheetNumber) {
        this.sheetNumber = sheetNumber;
    }

    /**
     * Injected
     */
    public void setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    /**
     * Injected
     */
    public void setCaptionRow(Integer captionRow) {
        this.captionRow = captionRow;
    }
}
