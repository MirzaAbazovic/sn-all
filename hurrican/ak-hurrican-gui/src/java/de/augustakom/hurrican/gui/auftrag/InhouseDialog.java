/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2005 11:14:51
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Inhouse;
import de.augustakom.hurrican.service.cc.PhysikService;

/**
 * Dialog, um ein Inhouse-Objekt zu editieren. <br>
 *
 *
 */
public class InhouseDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(InhouseDialog.class);

    private AKJTextField tfRaumnummer = null;
    private AKJTextField tfVerkabelung = null;
    private AKJTextField tfAnsprech = null;
    private AKJTextArea taBemerkung = null;

    private Inhouse inhouse = null;
    private boolean editable = true;

    /**
     * Konstruktor mit Angabe des zu editierenden <code>Inhouse</code>-Objekts. <br> Ueber das Flag
     * <code>editable</code> wird definiert, ob das Inhouse-Objekt editiert werden darf oder nicht.
     *
     * @param inhouse  Inhouse-Objekt, das editiert bzw. angezeigt werden soll.
     * @param editable Flag, ob das Objekt editiert oder nur angezeigt werden soll.
     * @throws IllegalArgumentException wenn kein Inhouse-Objekt angegeben wird oder die Endstellen-ID des Objekts
     *                                  <code>null</code> ist.
     */
    public InhouseDialog(Inhouse inhouse, boolean editable) {
        super("de/augustakom/hurrican/gui/auftrag/resources/InhouseDialog.xml");
        this.inhouse = inhouse;
        this.editable = editable;
        if (inhouse == null) {
            throw new IllegalArgumentException("Inhouse-Objekt darf nicht <null> sein!");
        }
        else if (inhouse.getEndstelleId() == null) {
            throw new IllegalArgumentException("Das Inhouse-Objekt ist keiner Endstelle zugeordnet!");
        }

        createGUI();
        showValues();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    protected final void createGUI() {
        if (!editable) {
            configureButton(CMD_SAVE, null, null, false, false);
        }

        setTitle("Inhouse-Daten");
        AKJLabel lblRaumnummer = getSwingFactory().createLabel("raumnummer");
        AKJLabel lblVerkabelung = getSwingFactory().createLabel("verkabelung");
        AKJLabel lblAnsprech = getSwingFactory().createLabel("ansprechpartner");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");

        tfRaumnummer = getSwingFactory().createTextField("raumnummer");
        tfVerkabelung = getSwingFactory().createTextField("verkabelung");
        tfAnsprech = getSwingFactory().createTextField("ansprechpartner");
        taBemerkung = getSwingFactory().createTextArea("bemerkung");
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        spBemerkung.setPreferredSize(new Dimension(190, 90));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblRaumnummer, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(tfRaumnummer, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblVerkabelung, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfVerkabelung, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblAnsprech, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfAnsprech, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblBemerkung, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.VERTICAL));
        child.add(spBemerkung, GBCFactory.createGBC(100, 100, 3, 4, 1, 2, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 6, 1, 1, GridBagConstraints.BOTH));
    }

    /* Zeigt die Daten des Inhouse-Objekts an. */
    private void showValues() {
        tfRaumnummer.setText(inhouse.getRaumnummer());
        tfVerkabelung.setText(inhouse.getVerkabelung());
        tfAnsprech.setText(inhouse.getAnsprechpartner());
        taBemerkung.setText(inhouse.getBemerkung());
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    protected void doSave() {
        try {
            PhysikService ps = getCCService(PhysikService.class);
            setValues();
            inhouse = ps.saveInhouse(inhouse, true);

            prepare4Close();
            setValue(inhouse);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /* Uebergibt die angezeigten Werte an das Inhouse-Objekt. */
    private void setValues() {
        inhouse.setRaumnummer(tfRaumnummer.getText(null));
        inhouse.setVerkabelung(tfVerkabelung.getText(null));
        inhouse.setAnsprechpartner(tfAnsprech.getText(null));
        inhouse.setBemerkung(taBemerkung.getText(null));
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


