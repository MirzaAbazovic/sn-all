/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.05.2006 10:52:32
 */
package de.augustakom.hurrican.gui.tools.dbchecks;

import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.DBQueryDef;


/**
 * Dialog fuer die Abfrage von Query-Parametern. <br> Der Dialog wertet die Parameter von einer Query-Definition aus und
 * erstellt entsprechende Eingabe-Felder. Die eingetragenen Werte werden als Object-Array zurueck geliefert.
 *
 *
 */
public class DBQueryParamDialog extends AbstractServiceOptionDialog {

    private DBQueryDef queryDef = null;
    private List<Component> queryParamFields = null;
    private int paramCount = -1;

    /**
     * Konstruktor mit Angabe der Query-Parameter.
     *
     * @param queryDef
     */
    public DBQueryParamDialog(DBQueryDef queryDef) throws HurricanGUIException {
        super(null);
        this.queryDef = queryDef;
        checkParams();
        createGUI();
    }

    /*
     * Erstellt aus den Query-Parametern eine Map. Als Key
     * wird der Parameter-Name, als Value der Parameter-Typ verwendet.
     */
    private void checkParams() throws HurricanGUIException {
        if (StringUtils.isBlank(queryDef.getParams())) {
            throw new HurricanGUIException("Keine gueltigen Query-Parameter definiert.");
        }

        String[] params = StringUtils.split(queryDef.getParams(), DBQueryDef.PARAM_SEPARATOR);
        paramCount = (params != null) ? params.length : -1;
        if (paramCount <= 0) {
            throw new HurricanGUIException("Keine Query-Parameter definiert!");
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        setTitle("Parameter-Definition");
        configureButton(CMD_SAVE, "OK", "Uebernimmt die gesetzten Parameter in die Abfrage", true, true);

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));

        int y = 0;
        queryParamFields = new ArrayList<>();
        String[] paramDefs = StringUtils.split(queryDef.getParams(), DBQueryDef.PARAM_SEPARATOR);
        for (String s : paramDefs) {
            String[] nameType = StringUtils.split(s, DBQueryDef.PARAM_NAME_TYPE_SEPARATOR);

            String paramName = nameType[0];
            AKJLabel lblParam = new AKJLabel(paramName, AKJLabel.LEFT);

            String paramType = nameType[1];
            Component typeComp = null;
            if (StringUtils.equalsIgnoreCase(paramType, DBQueryDef.PARAM_TYPE_STRING)) {
                AKJTextField tf = new AKJTextField();
                tf.setName(paramName);
                tf.setColumns(20);
                typeComp = tf;
            }
            else if (StringUtils.equalsIgnoreCase(paramType, DBQueryDef.PARAM_TYPE_DATE)) {
                AKJDateComponent dc = new AKJDateComponent();
                dc.setName(paramName);
                dc.setColumns(20);
                typeComp = dc;
            }
            else if (StringUtils.equalsIgnoreCase(paramType, DBQueryDef.PARAM_TYPE_INTEGER)) {
                AKJFormattedTextField tf = new AKJFormattedTextField(new DecimalFormat());
                tf.setHorizontalAlignment(AKJFormattedTextField.RIGHT);
                tf.setName(paramName);
                tf.setColumns(20);
                typeComp = tf;
            }

            queryParamFields.add(typeComp);
            getChildPanel().add(lblParam, GBCFactory.createGBC(0, 0, 1, y, 1, 1, GridBagConstraints.HORIZONTAL));
            getChildPanel().add(typeComp, GBCFactory.createGBC(0, 0, 3, y, 1, 1, GridBagConstraints.HORIZONTAL));

            y++;
        }
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, y, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    protected void validateSaveButton() {
        // do nothing
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    protected void doSave() {
        Object[] parameters = new Object[paramCount];
        for (int i = 0; i < queryParamFields.size(); i++) {
            Component comp = queryParamFields.get(i);
            if (comp instanceof AKJFormattedTextField) {
                parameters[i] = ((AKJFormattedTextField) comp).getValue();
            }
            else if (comp instanceof AKJDateComponent) {
                parameters[i] = ((AKJDateComponent) comp).getDate(null);
            }
            else if (comp instanceof AKJTextField) {
                parameters[i] = ((AKJTextField) comp).getText(null);
            }
        }

        prepare4Close();
        setValue(parameters);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}


