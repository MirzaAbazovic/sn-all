/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2011 10:40:34
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.tools.tal.actions.OpenDocumentAction;
import de.augustakom.hurrican.gui.tools.tal.actions.OpenDocumentPanel;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.model.AnlagenDto;

public class AnlagenAnzeigePanel extends AbstractServicePanel implements AKDataLoaderComponent, OpenDocumentPanel,
        AKObjectSelectionListener {

    private static final String COULD_NOT_LOAD_FILE = "Die Datei konnte nicht aus dem Archiv geladen werden!";

    private static final long serialVersionUID = -507072801367563853L;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/wita/resources/AnlagenAnzeigePanel.xml";
    private static final Logger LOGGER = Logger.getLogger(AnlagenAnzeigePanel.class);

    private AKJTable tbAnlagen;
    private AnlagenAnzeigeTableModel tbMdlAnlagen;

    private ArchiveService archiveService;

    private OpenDocumentAction openDocumentAction;

    public AnlagenAnzeigePanel() {
        super(RESOURCE);
        try {
            initServices();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    private void initServices() throws ServiceNotFoundException {
        archiveService = findService(ArchiveService.class.getName(), ArchiveService.class);
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    public final void loadData() {
        // not used; data is shown via setAnlagen
    }

    @Override
    protected final void createGUI() {
        openDocumentAction = new OpenDocumentAction(this);

        tbMdlAnlagen = new AnlagenAnzeigeTableModel();
        tbAnlagen = new AKJTable(tbMdlAnlagen, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);

        tbAnlagen.getColumn(AnlagenAnzeigeTableModel.GESCHAEFTFALL_MELDUNGSTYP).setPreferredWidth(200);
        tbAnlagen.getColumn(AnlagenAnzeigeTableModel.IO_TYP).setPreferredWidth(50);
        tbAnlagen.getColumn(AnlagenAnzeigeTableModel.REF_NR).setPreferredWidth(80);
        tbAnlagen.getColumn(AnlagenAnzeigeTableModel.GESENDET_ZEITSTEMPEL).setPreferredWidth(130);
        tbAnlagen.getColumn(AnlagenAnzeigeTableModel.ANLAGENTYP).setPreferredWidth(130);
        tbAnlagen.getColumn(AnlagenAnzeigeTableModel.DATEINAME).setPreferredWidth(300);

        tbAnlagen.addPopupAction(openDocumentAction);
        tbAnlagen.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spTable = new AKJScrollPane(tbAnlagen, new Dimension(592, 110));

        this.setLayout(new GridBagLayout());
        // @formatter:off
        this.add(spTable       , GBCFactory.createGBC(100,100, 1, 2, 1, 1, GridBagConstraints.BOTH, 0));
        // @formatter:on
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public ArchiveDocumentDto getSelectedDocument() throws HurricanGUIException {
        if (tbAnlagen.getSelectedRowCount() > 0) {
            AnlagenDto anlagenDto = tbMdlAnlagen.getDataAtRow(tbAnlagen.getSelectedRow());
            if (anlagenDto.getArchiveDocumentDto() != null) {
                // Outgoing document
                return anlagenDto.getArchiveDocumentDto();
            }
            Anlage anlage = anlagenDto.getAnlage();
            if (anlage != null) {

                // Incoming document
                if (anlage.getArchivingCancelReason() != null) {
                    throw new HurricanGUIException(COULD_NOT_LOAD_FILE + ": " + anlage.getArchivingCancelReason());
                }
                if (anlage.getArchivSchluessel() != null) {
                    try {
                        ArchiveDocumentDto archiveDocumentDto = archiveService.retrieveDocumentByUuid(anlage
                                .getArchivSchluessel(), HurricanSystemRegistry.instance()
                                .getCurrentLoginName());
                        if (archiveDocumentDto == null) {
                            throw new HurricanGUIException(COULD_NOT_LOAD_FILE);
                        }
                        return archiveDocumentDto;
                    }
                    catch (FindException e) {
                        throw new HurricanGUIException(COULD_NOT_LOAD_FILE, e);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ArchiveService getArchiveService() {
        return archiveService;
    }

    @Override
    public void objectSelected(Object selection) {
        openDocumentAction.actionPerformed(null);
    }

    public void setAnlagen(List<AnlagenDto> anlagenList) {
        tbMdlAnlagen.setData(null);
        if ((anlagenList != null) && (!anlagenList.isEmpty())) {
            tbMdlAnlagen.setData(anlagenList);
        }
    }
}
