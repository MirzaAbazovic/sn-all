/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.2009 11:10:46
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.service.cc.EndgeraeteService;


/**
 *
 */
public class EndgeraetAclEditDialog extends AbstractServiceOptionDialog implements
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(LanIpDialog.class);

    private EndgeraetAcl endgeraetAcl;

    // GUI-Elemente
    private AKJTextField textFieldAcl;
    private AKJTextField textFieldRoutertyp;

    /**
     * Konstruktor mit Angabe des zu editierenden EndgeraetIp-Objekts und des EG2Auftrag Objekts.
     *
     * @param endgeraetIp
     */
    public EndgeraetAclEditDialog(EndgeraetAcl endgeraetAcl) {
        super("de/augustakom/hurrican/gui/auftrag/resources/EndgeraetAclEditDialog.xml");
        if (endgeraetAcl == null) {
            throw new IllegalArgumentException("Es wurde kein Endgeraet-Acl-Objekt angegeben.");
        }

        this.endgeraetAcl = endgeraetAcl;

        createGUI();
        loadData();
        showValues();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel labelAcl = getSwingFactory().createLabel("acl.name");
        AKJLabel labelRoutertyp = getSwingFactory().createLabel("acl.routertyp");

        textFieldAcl = getSwingFactory().createTextField("acl.name");
        textFieldRoutertyp = getSwingFactory().createTextField("acl.routertyp");

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(labelAcl, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(textFieldAcl, GBCFactory.createGBC(0, 0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        child.add(labelRoutertyp, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.VERTICAL));
        child.add(textFieldRoutertyp, GBCFactory.createGBC(0, 0, 2, 2, 2, 1, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     *
     */
    private void showValues() {
        GuiTools.cleanFields(getChildPanel());
        if (endgeraetAcl != null) {
            textFieldAcl.setText(endgeraetAcl.getName());
            textFieldRoutertyp.setText(endgeraetAcl.getRouterTyp());
        }
    }

    /* Speichert die eingetragenen Daten in das EndgeraetIp-Objekt. */
    private void setValues() {
        endgeraetAcl.setName(textFieldAcl.getText());
        endgeraetAcl.setRouterTyp(textFieldRoutertyp.getText());
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            setValues();

            EndgeraeteService es = getCCService(EndgeraeteService.class);
            es.saveEndgeraetAcl(endgeraetAcl);

            prepare4Close();
            setValue(endgeraetAcl);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}
