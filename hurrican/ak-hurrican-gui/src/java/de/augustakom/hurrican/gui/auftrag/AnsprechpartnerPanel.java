/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2009 12:27:40
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.shared.CCAlleAnsprechpartnerDialog;
import de.augustakom.hurrican.gui.shared.CCAnsprechpartnerDialog;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Panel fuer die Anzeige und Bearbeitung der Ansprechpartner zu einem Auftrag.
 *
 *
 */
public class AnsprechpartnerPanel extends AbstractAuftragPanel implements AKObjectSelectionListener {

    private static final long serialVersionUID = 8716814987001642021L;
    private static final Logger LOGGER = Logger.getLogger(AnsprechpartnerPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/AnsprechpartnerPanel.xml";

    private static final String ADD_CONTACT = "add.contact";
    private static final String REMOVE_CONTACT = "remove.contact";
    private static final String COPYPASTE_CONTACT = "copypaste.contact";

    // GUI-Elemente
    private AKJTable tbContacts;
    private AKReferenceAwareTableModel<Ansprechpartner> tbMdlContacts;
    private AKJButton btnAddContact;
    private AKJButton btnRemoveContact;
    private LeitungsnummerPanel leitungsnummernPnl;
    private final List<AKManageableComponent> manageableComponents = new ArrayList<AKManageableComponent>();

    // Modelle
    private CCAuftragModel model;

    // Services
    private AnsprechpartnerService ansprechpartnerService;

    /**
     * Default-Konstruktor.
     */
    public AnsprechpartnerPanel() {
        super(RESOURCE);
        createGUI();
        init();
    }

    @Override
    protected final void createGUI() {
        AKJButton btnCopypasteContact;
        btnAddContact = getSwingFactory().createButton(ADD_CONTACT, getActionListener(), null);
        btnRemoveContact = getSwingFactory().createButton(REMOVE_CONTACT, getActionListener(), null);
        btnCopypasteContact = getSwingFactory().createButton(COPYPASTE_CONTACT, getActionListener(), null);

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddContact, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 10, 2)));
        btnPnl.add(btnRemoveContact, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 10, 2)));
        btnPnl.add(btnCopypasteContact, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 10, 2)));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

        tbMdlContacts = new AKReferenceAwareTableModel<Ansprechpartner>(
                new String[] { "Typ", "Default", "Bemerkung",
                        "Name", "Vorname",
                        "Telefon", "Fax", "Mobil", "eMail" },
                new String[] { Ansprechpartner.TYPE_REF_ID, Ansprechpartner.PREFERRED, Ansprechpartner.TEXT,
                        Ansprechpartner.ADDRESS_NAME, Ansprechpartner.ADDRESS_VORNAME,
                        Ansprechpartner.ADDRESS_TELEFON, Ansprechpartner.ADDRESS_FAX, Ansprechpartner.ADDRESS_HANDY,
                        Ansprechpartner.ADDRESS_EMAIL },
                new Class[] { String.class, Boolean.class, String.class,
                        String.class, String.class,
                        String.class, String.class, String.class, String.class }
        );
        tbContacts = new AKJTable(tbMdlContacts, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);

        addCopyAnsprechpartnerActions();

        tbContacts.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbContacts.attachSorter();
        tbContacts.fitTable(new int[] { 100, 50, 120, 120, 120, 110, 110, 110, 100 });
        AKJScrollPane spContacts = new AKJScrollPane(tbContacts, new Dimension(500, 150));

        leitungsnummernPnl = new LeitungsnummerPanel();

        AKJPanel top = new AKJPanel(new BorderLayout());
        top.setBorder(BorderFactory.createTitledBorder("Ansprechpartner"));
        top.add(btnPnl, BorderLayout.WEST);
        top.add(spContacts, BorderLayout.CENTER);

        this.setLayout(new GridBagLayout());
        this.add(top, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(leitungsnummernPnl, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));

        manageableComponents.add(btnAddContact);
        manageableComponents.add(btnRemoveContact);
        manageGUI(manageableComponents.toArray(new AKManageableComponent[manageableComponents.size()]));
    }

    private void addCopyAnsprechpartnerActions() {
        CopyAnsprechpartnerAction copyAnsprechpartnerAction = new CopyAnsprechpartnerAction(this);
        copyAnsprechpartnerAction.setParentClass(getClass());
        tbContacts.addPopupAction(copyAnsprechpartnerAction);

        CopyAnsprechpartnerActionAs copyAsHotlineService = new CopyAnsprechpartnerActionAs(
                "Als Ansprechpartner Hotline-Service kopieren",
                "Kopiert den Ansprechpartner als Ansprechpartner Hotline-Service",
                "copy.ap.hotlineservice",
                Ansprechpartner.Typ.HOTLINE_SERVICE,
                CCAddress.ADDRESS_TYPE_HOTLINE_SERVICE);
        addCopyAction(copyAsHotlineService);

        CopyAnsprechpartnerActionAs copyAsGeplanteArbeiten = new CopyAnsprechpartnerActionAs(
                "Als Ansprechpartner Geplante-Arbeiten kopieren",
                "Kopiert den Ansprechpartner als Ansprechpartner Geplante-Arbeiten",
                "copy.ap.ga",
                Ansprechpartner.Typ.HOTLINE_GA,
                CCAddress.ADDRESS_TYPE_HOTLINE_GA);
        addCopyAction(copyAsGeplanteArbeiten);

        CopyAnsprechpartnerActionAs copyAsEsA = new CopyAnsprechpartnerActionAs(
                "Als Ansprechpartner Endstelle A kopieren",
                "Kopiert den Ansprechpartner als Ansprechpartner Endstelle A",
                "copy.ap.esa",
                Ansprechpartner.Typ.ENDSTELLE_A,
                CCAddress.ADDRESS_TYPE_CUSTOMER_CONTACT);
        addCopyAction(copyAsEsA);

        CopyAnsprechpartnerActionAs copyAsEsB = new CopyAnsprechpartnerActionAs(
                "Als Ansprechpartner Endstelle B kopieren",
                "Kopiert den Ansprechpartner als Ansprechpartner Endstelle B",
                "copy.ap.esb",
                Ansprechpartner.Typ.ENDSTELLE_B,
                CCAddress.ADDRESS_TYPE_CUSTOMER_CONTACT);
        addCopyAction(copyAsEsB);

        CopyAnsprechpartnerActionAs copyAsPBXEnterpriseAdmin = new CopyAnsprechpartnerActionAs(
                "Als Ansprechpartner PBX Enterprise-Administrator kopieren",
                "Kopiert den Ansprechpartner als Ansprechpartner PBX Enterprise-Administrator",
                "copy.ap.pbx.enterprise.admin",
                Ansprechpartner.Typ.PBX_ENTERPRISE_ADMINISTRATOR,
                CCAddress.ADDRESS_TYPE_CUSTOMER_CONTACT);
        addCopyAction(copyAsPBXEnterpriseAdmin);

        CopyAnsprechpartnerActionAs copyAsPBXSiteAdmin = new CopyAnsprechpartnerActionAs(
                "Als Ansprechpartner PBX Site-Administrator kopieren",
                "Kopiert den Ansprechpartner als Ansprechpartner PBX Site-Administrator",
                "copy.ap.pbx.site.admin",
                Ansprechpartner.Typ.PBX_SITE_ADMINISTRATOR,
                CCAddress.ADDRESS_TYPE_CUSTOMER_CONTACT);
        addCopyAction(copyAsPBXSiteAdmin);
    }

    private void addCopyAction(CopyAnsprechpartnerActionAs copyAction) {
        copyAction.setParentClass(getClass());
        tbContacts.addPopupAction(copyAction);
    }

    /**
     * Init fuer die Services
     */
    private void init() {
        try {
            ansprechpartnerService = getCCService(AnsprechpartnerService.class);
            ReferenceService referenceService = getCCService(ReferenceService.class);

            List<Reference> anspTypes = referenceService.findReferencesByType(Reference.REF_TYPE_ANSPRECHPARTNER, Boolean.FALSE);
            Map<Long, Reference> ansprechpartnerTypeMap = new HashMap<Long, Reference>();
            CollectionMapConverter.convert2Map(anspTypes, ansprechpartnerTypeMap, "getId", null);
            tbMdlContacts.addReference(0, ansprechpartnerTypeMap, "strValue");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Override
    public void setModel(Observable model) throws AKGUIException {
        this.model = null;
        leitungsnummernPnl.setModel(null);

        GuiTools.cleanFields(this);
        if (model instanceof CCAuftragModel) {
            this.model = (CCAuftragModel) model;
            leitungsnummernPnl.setModel(model);

            readModel();
        }
    }


    @Override
    public void readModel() throws AKGUIException {
        if (this.model == null) {
            return;
        }

        enableAnsprechpartnerButtons(false);
        final SwingWorker<List<Ansprechpartner>, Void> worker = new SwingWorker<List<Ansprechpartner>, Void>() {
            final Long auftragId = model.getAuftragId();

            @Override
            protected List<Ansprechpartner> doInBackground() throws Exception {
                List<Ansprechpartner> ansprechpartnerList = ansprechpartnerService.findAnsprechpartner(null, auftragId);
                for (Ansprechpartner ansprechpartner : ansprechpartnerList) {
                    if (ansprechpartner.getAddress() == null) {
                        ansprechpartner.setAddress(CCAddress.getEmptyAddress());
                    }
                }
                return ansprechpartnerList;
            }

            @Override
            public void done() {
                try {
                    List<Ansprechpartner> ansprechpartnerList = get();
                    tbMdlContacts.setData(ansprechpartnerList);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableAnsprechpartnerButtons(true);
                    manageGUI(manageableComponents.toArray(new AKManageableComponent[manageableComponents.size()]));
                }
            }
        };
        worker.execute();
    }


    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof Ansprechpartner) {
            showDialog((Ansprechpartner) selection);
        }
    }


    @Override
    protected void execute(String command) {
        if (ADD_CONTACT.equals(command)) {
            addContact();
        }
        else if (REMOVE_CONTACT.equals(command)) {
            removeContact();
        }
        else if (COPYPASTE_CONTACT.equals(command)) {
            copyAndPaste();
        }
    }


    /**
     * Fuegt einen neuen Ansprechpartner hinzu
     */
    private void addContact() {
        Ansprechpartner ansprechpartner = new Ansprechpartner();
        ansprechpartner.setAuftragId(model.getAuftragId());
        ansprechpartner.setPreferred(Boolean.TRUE);
        showDialog(ansprechpartner);
    }


    /**
     * Entfernt den aktuell selektierten Ansprechpartner
     */
    private void removeContact() {
        try {
            int row = tbContacts.getSelectedRow();
            @SuppressWarnings("unchecked")
            Ansprechpartner selection = ((AKMutableTableModel<Ansprechpartner>) tbContacts.getModel())
                    .getDataAtRow(row);
            if (selection != null) {
                int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                        getSwingFactory().getText("delete.msg"), getSwingFactory().getText("delete.title"));
                if (option == AKJOptionDialog.YES_OPTION) {
                    if ((selection.getAddress() != null)
                            && selection.getAddress().compareCCAddress(CCAddress.getEmptyAddress())
                            && (BooleanTools.nullToFalse(AbstractCCIDModel.IS_TRANSIENT.apply(selection.getAddress())))) {
                        selection.setAddress(null);
                    }
                    ansprechpartnerService.deleteAnsprechpartner(selection);
                    tbMdlContacts.removeObject(selection);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    private void copyAndPaste() {
        CCAlleAnsprechpartnerDialog copydlg = new CCAlleAnsprechpartnerDialog(model.getAuftragId());
        DialogHelper.showDialog(this, copydlg, true, true);
        try {
            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Oeffnet den Adress-Dialog fuer den Ansprechpartner
     */
    private void showDialog(Ansprechpartner ansprechpartner) {
        try {
            boolean isNew = (ansprechpartner.getId() == null) ? true : false;

            CCAnsprechpartnerDialog dlg = new CCAnsprechpartnerDialog(ansprechpartner);
            Object result = DialogHelper.showDialog(this, dlg, true, true);
            if (result instanceof Ansprechpartner) {
                if (isNew) {
                    tbMdlContacts.addObject(ansprechpartner);
                }
                tbMdlContacts.fireTableDataChanged();
            }
            // Da mehrere Instanzen der gleichen Adresse im Detached-Zustand an den
            // Ansprechpartnerobjekten haengen koennen, und eventuell die Adresse
            // eines Ansprechpartners geaendert wurde, ohne dass dies gespeichert
            // werden soll, die geaenderten Daten aber noch genutzt werden, werden
            // die Daten hier neu geladen
            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void enableAnsprechpartnerButtons(boolean enabled) {
        btnAddContact.setEnabled(enabled);
        btnRemoveContact.setEnabled(enabled);
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void saveModel() throws AKGUIException {
        leitungsnummernPnl.saveModel();
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void update(Observable arg0, Object arg1) {
    }


    class CopyAnsprechpartnerAction extends AKAbstractAction {
        private final Component parent;

        public CopyAnsprechpartnerAction(Component parent) {
            this.parent = parent;
            setName("Ansprechpartner kopieren");
            setActionCommand("copy.ap.action");
            setTooltip("Kopiert den Ansprechpartner");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Ansprechpartner source = getSelectedAnsprechpartner();
                if (source != null) {
                    Object resultObject = DialogHelper.showDialog(parent, new CCAnsprechpartnerTypDialog(), true, true);
                    if (resultObject instanceof CCAnsprechpartnerTypDialog.Result) {
                        CCAnsprechpartnerTypDialog.Result result = (CCAnsprechpartnerTypDialog.Result) resultObject;
                        Ansprechpartner newAnsprechpartner = ansprechpartnerService.copyAnsprechpartner(source, result.ansprechpartnerTyp, result.addressType, result.preferred);
                        addAnsprechpartnerToTable(newAnsprechpartner);
                    }
                }
            }
            catch (Exception exception) {
                MessageHelper.showErrorDialog(getMainFrame(), exception);
            }
        }

    }

    class CopyAnsprechpartnerActionAs extends AKAbstractAction {
        private final Ansprechpartner.Typ ansprechpartnerTyp;
        private final Long addressType;

        public CopyAnsprechpartnerActionAs(String name, String tooltip, String actionCommand,
                Ansprechpartner.Typ ansprechpartnerTyp, Long addressType) {
            this.ansprechpartnerTyp = ansprechpartnerTyp;
            this.addressType = addressType;
            setName(name);
            setTooltip(tooltip);
            setActionCommand(actionCommand);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Ansprechpartner source = getSelectedAnsprechpartner();
                if (source != null) {
                    Ansprechpartner newAnsprechpartner = ansprechpartnerService.copyAnsprechpartner(source, ansprechpartnerTyp, addressType, null);
                    addAnsprechpartnerToTable(newAnsprechpartner);
                }
            }
            catch (Exception exception) {
                MessageHelper.showErrorDialog(getMainFrame(), exception);
            }
        }
    }

    private Ansprechpartner getSelectedAnsprechpartner() {
        int selectedRow = tbContacts.getSelectedRow();
        @SuppressWarnings("unchecked")
        Ansprechpartner source = ((AKMutableTableModel<Ansprechpartner>) tbContacts.getModel())
                .getDataAtRow(selectedRow);
        return source;
    }

    private void addAnsprechpartnerToTable(Ansprechpartner newAnsprechpartner) {
        tbMdlContacts.addObject(newAnsprechpartner);
        tbMdlContacts.fireTableDataChanged();
    }

}
