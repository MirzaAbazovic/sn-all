/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.2010 12:10:26
 */

package de.augustakom.hurrican.gui.tools.scheduler.controller;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJAbstractDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;


/**
 * Sehr einfacher Dialog zum anzeigen/editieren von Cron Triggern
 *
 *
 */
public class AKSchedulerControllerCronDialog extends AKJAbstractDialog {

    private static final long serialVersionUID = 5822951590536948987L;
    private static final Logger LOGGER = Logger.getLogger(AKSchedulerControllerCronDialog.class);
    private AKJTextField tfExpression = null;
    private AKJTextField tfFullName = null;
    private CronTrigger cronTrigger = null;
    private boolean saved = false;

    public AKSchedulerControllerCronDialog() {
        super("de/augustakom/hurrican/gui/tools/scheduler/resources/AKSchedulerControllerCronDialog.xml");
        createGUI();
    }

    private void init() {
        if (cronTrigger != null) {
            tfFullName.setText(cronTrigger.getFullName());
            tfExpression.setText(cronTrigger.getCronExpression());
        }
    }

    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* (non-Javadoc)
     * @see de.augustakom.common.gui.swing.AKJAbstractDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("caption"));

        AKJLabel lblFullName = getSwingFactory().createLabel("lbl.fullName", SwingConstants.LEFT, Font.BOLD);
        AKJLabel lblExpressin = getSwingFactory().createLabel("lbl.cronExpression", SwingConstants.LEFT, Font.BOLD);

        tfFullName = getSwingFactory().createTextField("tfn.fullName");
        tfExpression = getSwingFactory().createTextField("tfn.cronExpression");
        AKJButton bSave = getSwingFactory().createButton("btn.save", getActionListener());
        AKJButton bCancel = getSwingFactory().createButton("btn.cancel", getActionListener());

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(lblFullName, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(tfFullName, GBCFactory.createGBC(0, 0, 1, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        center.add(lblExpressin, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(tfExpression, GBCFactory.createGBC(0, 0, 1, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        center.add(bSave, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(bCancel, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);
        setPreferredSize(new Dimension(450, 110));
    }

    /* (non-Javadoc)
     * @see de.augustakom.common.gui.swing.AKJAbstractDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (StringUtils.equals(command, "btn.save")) {
            if (cronTrigger != null) {
                try {
                    List<String> identifiers = AKSchedulerControllerJobModel.splitFullSchedulerName(tfFullName
                            .getText());
                    if ((identifiers != null) && (identifiers.size() > 1)) {
                        cronTrigger.setName(identifiers.get(1));
                        cronTrigger.setGroup(identifiers.get(0));
                        cronTrigger.setCronExpression(tfExpression.getText());
                        saved = true;
                    }
                }
                catch (Exception e) {
                    LOGGER.info(e.getMessage(), e);
                    MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
                }
            }
            this.dispose();
        }
        else if (StringUtils.equals(command, "btn.cancel")) {
            this.dispose();
        }
    }

    /**
     * @param cronTrigger the cronTrigger to set
     */
    public void setCronTrigger(CronTrigger cronTrigger) {
        this.cronTrigger = cronTrigger;
        init();
    }

    /**
     * @return the cronTrigger
     */
    public CronTrigger getCronTrigger() {
        return cronTrigger;
    }

    /**
     * @return the saved
     */
    public boolean isSaved() {
        return saved;
    }

}
