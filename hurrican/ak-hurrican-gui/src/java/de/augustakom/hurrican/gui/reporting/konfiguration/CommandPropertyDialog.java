/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2007 10:40:19
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.support.AopUtils;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.reporting.view.CmdProperty;
import de.augustakom.hurrican.service.reporting.impl.command.AbstractReportCommand;


/**
 * Dialog, um die Properties einer Command-Klasse anzuzeigen.
 *
 *
 */
public class CommandPropertyDialog extends AbstractServiceOptionDialog implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(CommandPropertyDialog.class);

    private AKReflectionTableModel<CmdProperty> tbModelProps = null;

    private ServiceCommand cmd = null;

    /**
     * Konstruktor mit Angabe der Command-Klasse Die restlichen Daten werden von dem Dialog selbst geladen.
     */
    public CommandPropertyDialog(ServiceCommand cmd) {
        super(null);
        this.cmd = cmd;
        createGUI();
        read();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Properties für Klasse " + cmd.getClassName());
        setIconURL("de/augustakom/hurrican/gui/images/printer.gif");

        configureButton(CMD_SAVE, "Ok", null, true, true);
        configureButton(CMD_CANCEL, "Abbrechen", null, false, true);

        // Table für Reports
        tbModelProps = new AKReflectionTableModel<CmdProperty>(
                new String[] { "Key", "Value" },
                new String[] { "key", "value" },
                new Class[] { String.class, String.class });

        AKJTable tbProps = new AKJTable(tbModelProps, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbProps.attachSorter();
        tbProps.fitTable(new int[] { 350, 400 });
        AKJScrollPane tableSP = new AKJScrollPane(tbProps, new Dimension(900, 300));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(tableSP, BorderLayout.CENTER);
    }

    /* Laedt die Liste der verfügbaren Reports*/
    private void read() {
        if (cmd != null) {
            try {
                setWaitCursor();

                IServiceLocator sl = ServiceLocatorRegistry.instance().getServiceLocator(
                        IServiceLocatorNames.HURRICAN_REPORT_SERVICE);

                Proxy proxy = (Proxy) sl.getBean(cmd.getClassName());
                Class cmdClass = AopUtils.getTargetClass(proxy);

                AbstractReportCommand command = (AbstractReportCommand) cmdClass.newInstance();
                String filename = command.getPropertyFile();

                // Falls Dateiname != null lese Properties aus
                if (StringUtils.isNotBlank(filename)) {
                    Properties props = new Properties();
                    InputStream inputStream = getClass().getResourceAsStream(filename);
                    try {
                        props.load(inputStream);

                        List<CmdProperty> list = new ArrayList<>();
                        Iterator iter = props.keySet().iterator();

                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            CmdProperty property = new CmdProperty();
                            property.setKey(command.getPropName(key));
                            property.setValue(props.getProperty(key));
                            list.add(property);
                        }

                        tbModelProps.setData(list);
                        tbModelProps.fireTableDataChanged();
                    }
                    finally {
                        FileTools.closeStreamSilent(inputStream);
                    }
                }
                else {
                    MessageHelper.showInfoDialog(getMainFrame(),
                            "Kein Property-File für diese Command-Klasse vorhanden.", null, true);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        // Schließe Dialog
        prepare4Close();
        setValue(AKJOptionDialog.OK_OPTION);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        doSave();
    }


}


