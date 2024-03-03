/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2011 12:00:37
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.NullSelectionModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.tools.tal.wita.AnlagenAnzeigePanel;
import de.augustakom.hurrican.gui.tools.tal.wita.TreeTablePopupMouseListener;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.common.service.HistoryService;
import de.mnet.common.service.HistoryService.IoArchiveTreeAndAnlagenList;
import de.mnet.wita.model.IoArchive;

/**
 * Dialog zur Darstellung der WITA-History/des Fortschritts einer Carrierbestellung.
 */
public abstract class HistoryDialog extends AbstractServiceOptionDialog {

    private static final long serialVersionUID = -3671070565495229312L;

    static final String RESOURCE = "de/augustakom/hurrican/gui/tools/tal/ioarchive/resources/HistoryDialog.xml";

    private static final Logger LOGGER = Logger.getLogger(HistoryDialog.class);

    HistoryService historyService;

    private Outline outline;
    private AnlagenAnzeigePanel anlagenAnzeigePanel;

    HistoryDialog(String resource) {
        super(resource, false);
    }

    void initDialog() {
        try {
            initServices();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);

            prepare4Close();
            setValue(null);
        }
    }

    void initServices() throws ServiceNotFoundException {
        historyService = getCCService(HistoryService.class);
    }

    void loadData() {
        SwingWorker<IoArchiveTreeAndAnlagenList, Void> worker = new SwingWorker<IoArchiveTreeAndAnlagenList, Void>() {
            @Override
            protected IoArchiveTreeAndAnlagenList doInBackground() throws Exception {
                return loadIoArchiveTreeAndAnlagenList();
            }

            @Override
            protected void done() {
                try {
                    IoArchiveTreeAndAnlagenList result = get();
                    setOutlineData(result.getFirst());
                    anlagenAnzeigePanel.setAnlagen(result.getSecond());
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                }
            }
        };
        setWaitCursor();
        worker.execute();
    }

    protected abstract IoArchiveTreeAndAnlagenList loadIoArchiveTreeAndAnlagenList() throws FindException;

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, null, null, false, false);
        configureButton(CMD_CANCEL, getSwingFactory().getText("ok"), null, true, true);

        outline = new Outline();
        outline.setSelectionModel(NullSelectionModel.INSTANCE);
        outline.setRootVisible(false);
        setOutlineData(null);
        outline.setRenderDataProvider(new IoArchiveRenderData());
        addActionsToOutline();

        AKJPanel outlinePanel = new AKJPanel(new BorderLayout());
        outlinePanel.add(outline.getTableHeader(), BorderLayout.PAGE_START);
        outlinePanel.add(outline, BorderLayout.CENTER);

        anlagenAnzeigePanel = new AnlagenAnzeigePanel();

        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab(getSwingFactory().getText("title"), new AKJScrollPane(outlinePanel));
        tabbedPane.addTab(getSwingFactory().getText("anlagen"), new AKJScrollPane(anlagenAnzeigePanel));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(tabbedPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(1000, 500));
    }

    private void setOutlineData(List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree) {
        TreeModel treeMdl = new IoArchiveTreeModel(ioArchiveTree);
        OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(treeMdl, new IoArchiveRowModel(), true,
                IoArchiveRowModel.REQUEST_GESCHAEFTSFALL_COLUMN);
        outline.setModel(outlineModel);
        expandAllNodes(ioArchiveTree, treeMdl);
        setColumnWidthsOfOutline();
    }

    private void expandAllNodes(List<Pair<IoArchive, List<IoArchive>>> ioArchiveTree, TreeModel treeMdl) {
        if (ioArchiveTree == null) {
            return;
        }
        TreePath rootPath = new TreePath(treeMdl.getRoot());
        for (Pair<IoArchive, List<IoArchive>> p : ioArchiveTree) {
            TreePath path = rootPath.pathByAddingChild(p.getFirst());
            outline.expandPath(path);
        }
    }

    private void setColumnWidthsOfOutline() {
        outline.getColumn(IoArchiveRowModel.REQUEST_GESCHAEFTSFALL_COLUMN).setPreferredWidth(125);
        outline.getColumn(IoArchiveRowModel.IO_TYPE_COLUMN).setPreferredWidth(25);
        outline.getColumn(IoArchiveRowModel.WITA_EXT_ORDER_NO_COLUMN).setPreferredWidth(50);
        outline.getColumn(IoArchiveRowModel.TIMESTAMP_SENT_COLUMN).setPreferredWidth(100);
        outline.getColumn(IoArchiveRowModel.REQUEST_MELDUNGSCODE_TEXT_COLUMN).setPreferredWidth(200);
        outline.getColumn(IoArchiveRowModel.REQUEST_TIMESTAMP_COLUMN).setPreferredWidth(100);
    }

    private void addActionsToOutline() {
        TreeTablePopupMouseListener popupMouseListener = new TreeTablePopupMouseListener();

        AKAbstractAction createXMLRequestAction = (AKAbstractAction) getSwingFactory().createAction(
                "action.showXMLRequest");
        createXMLRequestAction.setParentClass(HistoryDialog.class);
        popupMouseListener.addAction(createXMLRequestAction);

        AKAbstractAction createCompleteXMLRequestAction = (AKAbstractAction) getSwingFactory().createAction(
                "action.showCompleteXMLRequest");
        createCompleteXMLRequestAction.setParentClass(HistoryDialog.class);
        popupMouseListener.addAction(createCompleteXMLRequestAction);

        outline.addMouseListener(popupMouseListener);

        AKManageableComponent[] managedComponents = new AKManageableComponent[] { createCompleteXMLRequestAction };
        // Ansonsten wird der Name der Subklasse fuer die Verrechtung verwendet
        manageGUI(HistoryDialog.class.getName(), managedComponents);
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void doSave() {
        // not used
    }

}
