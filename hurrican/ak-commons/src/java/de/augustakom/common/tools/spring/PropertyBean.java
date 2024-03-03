/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.2012 13:07:40
 */
package de.augustakom.common.tools.spring;

import java.util.*;

import de.augustakom.common.tools.lang.PropertyUtil;

/**
 * Loads properties using the Property Util mechanism implemented in class PropertyUtil
 */
public class PropertyBean {
    private static final String DEFAULT_APPENDIX = "properties";

    private List<String> fileBaseNames;
    private String appendix;
    private boolean debug = false;

    private Properties load(List<String> fileBaseNames, String appendix, boolean useCache) {
        boolean oldDebugValue = PropertyUtil.isDebug();
        PropertyUtil.setDebug(debug);
        Properties props = PropertyUtil.loadPropertyHierarchy(fileBaseNames, appendix, useCache);
        PropertyUtil.setDebug(oldDebugValue);
        return props;
    }

    public Properties getProperties() {
        return load(getFileBaseNames(), getAppendix(), true);
    }

    public String getProperty(String key) {
        return getProperties().getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return getProperties().getProperty(key, defaultValue);
    }

    public void setFileBaseNames(List<String> fileBaseNames) {
        this.fileBaseNames = fileBaseNames;
    }

    public List<String> getFileBaseNames() {
        return fileBaseNames;
    }

    public void setAppendix(String appendix) {
        this.appendix = appendix;
    }

    public String getAppendix() {
        return (appendix != null ? appendix : DEFAULT_APPENDIX);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean getDebug() {
        return debug;
    }
}
