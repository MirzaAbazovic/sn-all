/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.2006 08:05:15
 */
package de.augustakom.common.gui.swing.table;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.augustakom.common.tools.lang.BooleanTools;


/**
 * TableModel, das seine Definition ueber ein XML-File erhaelt. <br> <br> Aufbau des XML-Files: <br> <code>
 * &lt;table&gt;       <br> &lt;column name="col1" property="prop1" type="java.lang.String" editable="false"/&gt;  <br>
 * &lt;/table&gt;      <br> </code>
 *
 * @param <T> der Typ einer Zeile im Table-Modell
 *
 */
public class AKTableModelXML<T> extends AKTableModel<T> {

    private static final Logger LOGGER = Logger.getLogger(AKTableModelXML.class);

    protected static final String XML_TAG_COLUMN = "column";
    protected static final String XML_COLUMN_ATTRIB_NAME = "name";
    protected static final String XML_COLUMN_ATTRIB_PROPERTY = "property";
    protected static final String XML_COLUMN_ATTRIB_TYPE = "type";
    protected static final String XML_COLUMN_ATTRIB_EDITABLE = "editable";
    protected static final String XML_COLUMN_ATTRIB_WIDTH = "width";
    protected static final String XML_COLUMN_ATTRIB_MAXCHARS = "maxchars";

    private String xmlDef = null;

    private SimpleColumnDefinition[] columnDefs = null;

    private Set<T> modified = new HashSet<T>();

    /**
     * Konstruktor mit Angabe des XML-Files, aus dem der Aufbau des TableModels geladen wird.
     *
     * @param xmlDef
     */
    public AKTableModelXML(String xmlDef) {
        super();
        this.xmlDef = xmlDef;
        initModel();
    }

    /**
     * Initialisiert das TableModel.
     */
    protected void initModel() {
        try {
            readConfiguration();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Liest das XML-File aus.
     */
    protected void readConfiguration() throws JDOMException, IOException {
        SAXBuilder sax = new SAXBuilder();

        URL url = this.getClass().getClassLoader().getResource(xmlDef);
        if (url != null) {
            Document doc = sax.build(url);
            Element root = doc.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> children = root.getChildren(XML_TAG_COLUMN);

            if (children != null) {
                List<SimpleColumnDefinition> definitions = new ArrayList<SimpleColumnDefinition>();
                for (Element child : children) {
                    SimpleColumnDefinition colDef = new SimpleColumnDefinition();
                    colDef.setHeader(child.getAttributeValue(XML_COLUMN_ATTRIB_NAME));
                    colDef.setProperty(child.getAttributeValue(XML_COLUMN_ATTRIB_PROPERTY));
                    colDef.setType(child.getAttributeValue(XML_COLUMN_ATTRIB_TYPE));
                    Boolean editable = StringUtils.equalsIgnoreCase(child.getAttributeValue(XML_COLUMN_ATTRIB_EDITABLE), BooleanTools.DEFAULT_TRUE_STRING);
                    colDef.setEditable(BooleanTools.nullToFalse(editable));
                    colDef.setWidth((child.getAttributeValue(XML_COLUMN_ATTRIB_WIDTH) != null)
                            ? Integer.parseInt(child.getAttributeValue(XML_COLUMN_ATTRIB_WIDTH)) : 50);
                    colDef.setMaxChars((child.getAttributeValue(XML_COLUMN_ATTRIB_MAXCHARS) != null)
                            ? Integer.parseInt(child.getAttributeValue(XML_COLUMN_ATTRIB_MAXCHARS)) : 0);
                    definitions.add(colDef);
                }
                columnDefs = definitions.toArray(new SimpleColumnDefinition[definitions.size()]);
            }
        }
    }

    @Override
    public int getColumnCount() {
        return columnDefs.length;
    }

    @Override
    public String getColumnName(int column) {
        SimpleColumnDefinition colDef = columnDefs[column];
        return colDef.getHeader();
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            SimpleColumnDefinition colDef = columnDefs[column];
            T value = getDataAtRow(row);
            return PropertyUtils.getProperty(value, colDef.getProperty());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        try {
            SimpleColumnDefinition colDef = columnDefs[column];
            T object = getDataAtRow(row);
            PropertyUtils.setProperty(object, colDef.getProperty(), aValue);
            modified.add(object);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void setData(Collection<T> data) {
        super.setData(data);
        modified.clear();
    }

    public Set<T> getModified() {
        return modified;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        SimpleColumnDefinition colDef = columnDefs[column];
        return colDef.isEditable();
    }

    public int getMaxChars(int row, int column) {
        SimpleColumnDefinition colDef = columnDefs[column];
        return colDef.getMaxChars();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        SimpleColumnDefinition colDef = columnDefs[columnIndex];
        return colDef.getTypeAsClass();
    }

    /**
     * Ermittelt die Breite jeder Spalte und gibt eine Liste mit den Breiten zurück
     */
    public int[] getFitList() {
        int[] fitList = null;
        if (null != columnDefs) {
            fitList = new int[columnDefs.length];
            for (int i = 0; i < columnDefs.length; i++) {
                fitList[i] = columnDefs[i].getWidth();
            }
        }
        return fitList;
    }

    /**
     * Modell-Klasse, um die XML-Definitionen zu halten
     */
    private static class SimpleColumnDefinition {
        private String header = null;
        private String property = null;
        private String type = null;
        private boolean editable = false;
        private int width;
        private int maxChars = 0;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * Gibt den angegebenen Typ als Class-Objekt zurueck.
         */
        public Class<?> getTypeAsClass() {
            try {
                return Class.forName(getType());
            }
            catch (Exception e) {
                return String.class;
            }
        }

        public boolean isEditable() {
            return this.editable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        public String getHeader() {
            return this.header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getProperty() {
            return this.property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getMaxChars() {
            return this.maxChars;
        }

        public void setMaxChars(int maxChars) {
            this.maxChars = maxChars;
        }
    }
}
