/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2012 11:08:52
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.tools.tal.actions.OpenDocumentAction;
import de.augustakom.hurrican.gui.tools.tal.actions.OpenDocumentPanel;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.service.exmodules.archive.ArchiveService;

/**
 * Dialog zum Auswahlen von Datei-Anhaengen aus Scanview fuer die Tal-Bestellung
 */
public class SelectAttachmentDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, OpenDocumentPanel,
        AKObjectSelectionListener {

    private static final long serialVersionUID = -2047790242741011117L;

    private static final Logger LOGGER = Logger.getLogger(SelectAttachmentDialog.class);

    private static final String SUB_TITLE = "sub.title";
    private static final String TITLE = "title";

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/resources/SelectAttachmentDialog.xml";

    private final Set<ArchiveDocumentDto> selectedArchiveDocumentDtos;
    private final CBVorgangSubOrder auftragDetails;

    private final ArchiveService archiveService;

    private AKTableModelXML<ArchiveDocumentDto> tbMdlDocs;
    private AKJTable tbDocs;
    private final List<ArchiveDocumentDto> archiveDocumentDtos;

    private OpenDocumentAction openDocumentAction;

    public SelectAttachmentDialog(CBVorgangSubOrder auftragDetails, List<ArchiveDocumentDto> archiveDocumentDtos, Set<ArchiveDocumentDto> selectedArchiveDocumentDtos,
            ArchiveService archiveService) {
        super(RESOURCE);
        this.auftragDetails = auftragDetails;
        this.selectedArchiveDocumentDtos = selectedArchiveDocumentDtos;
        this.archiveService = archiveService;
        this.archiveDocumentDtos = archiveDocumentDtos;
        try {
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        openDocumentAction = new OpenDocumentAction(this);

        setTitle(getSwingFactory().getText(TITLE));
        AKJLabel lblSubTitle = getSwingFactory().createLabel(SUB_TITLE, SwingConstants.LEFT, Font.BOLD);

        tbMdlDocs = new AKTableModelXML<ArchiveDocumentDto>(
                "de/augustakom/hurrican/gui/tools/tal/resources/SelectAttachmentTable.xml");
        tbDocs = new AKJTable(tbMdlDocs, JTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbDocs.fitTable(tbMdlDocs.getFitList());
        tbDocs.addPopupAction(openDocumentAction);
        tbDocs.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spTable = new AKJScrollPane(tbDocs, new Dimension(600, 110));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        // @formatter:off
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblSubTitle   , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spTable       , GBCFactory.createGBC(100,100, 1, 2, 1, 1, GridBagConstraints.BOTH, 5));
        // @formatter:on
    }

    @Override
    public final void loadData() {
        try {
            if (CollectionTools.hasExpectedSize(archiveDocumentDtos, 1)) {
                archiveDocumentDtos.get(0).setSelected(Boolean.TRUE);
            }
            for (ArchiveDocumentDto archiveDocumentDto : archiveDocumentDtos) {
                int index = archiveDocumentDtos.indexOf(archiveDocumentDto);
                for (ArchiveDocumentDto selected : selectedArchiveDocumentDtos) {
                    if (selected.getKey().equals(archiveDocumentDto.getKey())) {
                        archiveDocumentDto.setSelected(true);
                        archiveDocumentDtos.set(index, archiveDocumentDto);
                    }
                }
            }
            tbMdlDocs.setData(archiveDocumentDtos);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    @Override
    public ArchiveDocumentDto getSelectedDocument() throws HurricanGUIException {
        if (tbDocs.getSelectedRowCount() > 0) {
            @SuppressWarnings("unchecked")
            AKMutableTableModel<ArchiveDocumentDto> tbMdl = (AKMutableTableModel<ArchiveDocumentDto>) tbDocs.getModel();
            return tbMdl.getDataAtRow(tbDocs.getSelectedRow());
        }
        return null;
    }

    @Override
    public ArchiveService getArchiveService() {
        return archiveService;
    }

    @Override
    protected void doSave() {
        if (!selectedArchiveDocumentDtos.isEmpty()) {
            selectedArchiveDocumentDtos.clear();
        }
        if (CollectionTools.isNotEmpty(archiveDocumentDtos)) {
            for (ArchiveDocumentDto archiveDoc : archiveDocumentDtos) {
                if (BooleanTools.nullToFalse(archiveDoc.getSelected())) {
                    selectedArchiveDocumentDtos.add(archiveDoc);
                }
            }
        }

        auftragDetails.setAnzahlSelectedAnlagen(selectedArchiveDocumentDtos.size());
        prepare4Close();
        setValue(selectedArchiveDocumentDtos);
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void objectSelected(Object selection) {
        openDocumentAction.actionPerformed(null);
    }

}
