/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2011 09:50:14
 */
package de.augustakom.hurrican.gui.tools.tal.wizard;

import static de.augustakom.hurrican.gui.tools.tal.wizard.CreateElTALVorgangWizard.*;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceWizardPanel;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;

/**
 * WizardPanel zur Auswahl eines Auftrags,
 */
public class SelectAuftrag4ChangeWizardPanel extends AbstractServiceWizardPanel implements AKDataLoaderComponent {

    private static final long serialVersionUID = 3848846234756121583L;

    private static final Logger LOGGER = Logger.getLogger(SelectAuftrag4ChangeWizardPanel.class);

    private static final String RESOURCE =
            "de/augustakom/hurrican/gui/tools/tal/resources/SelectAuftrag4ChangeWizardPanel.xml";

    private static final String SUB_TITLE = "sub.title";

    private AKReflectionTableModel<AuftragEndstelleView> tbMdlAuftrag = null;
    private AKJTable tbAuftrag = null;

    private AuftragDaten auftragDaten = null;
    private Long geoId = null;

    public SelectAuftrag4ChangeWizardPanel(AKJWizardComponents wizardComponents) {
        super(RESOURCE, wizardComponents);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblSubTitle = getSwingFactory().createLabel(SUB_TITLE, SwingConstants.LEFT, Font.BOLD);
        // @formatter:off
        tbMdlAuftrag = new AKReflectionTableModel<>(
                new String[] { "Auftrags-ID", "Kunde__No", "Name", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Produkt", "Status", "Endstelle", "Endstelle-Name"},
                new String[] { "auftragId", "kundeNo", "name", "vbz", "anschlussart", "auftragStatusText", "endstelle", "endstelleName"},
                new Class[] { Long.class, Long.class, String.class, String.class, String.class, String.class, String.class, String.class});
        // @formatter:on
        tbAuftrag = new AKJTable(tbMdlAuftrag, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAuftrag.attachSorter();
        tbAuftrag.fitTable(new int[] { 70, 75, 100, 80, 80, 80, 100, 100 });
        AKJScrollPane spTable = new AKJScrollPane(tbAuftrag, new Dimension(600, 150));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());

        // @formatter:off
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblSubTitle   , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spTable       , GBCFactory.createGBC(100,100, 1, 2, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    @Override
    public void update() {
        getWizardComponents().removeWizardObject(WIZARD_OBJECT_TRANSFER_ACCOUNT_FROM);
        if (getWizardComponents().getDirection() == AKJWizardComponents.DIRECTION_FORWARD) {
            auftragDaten = (AuftragDaten) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_DATEN);
            loadData();
        }
    }

    @Override
    protected boolean goNext() {
        if (tbAuftrag.getSelectedRowCount() > 0) {
            // selektierten Auftrag ermitteln und in Wizard 'speichern'
            @SuppressWarnings("unchecked")
            AKMutableTableModel<AuftragEndstelleView> tbMdl =
                    (AKMutableTableModel<AuftragEndstelleView>) tbAuftrag.getModel();
            AuftragEndstelleView esView = tbMdl.getDataAtRow(tbAuftrag.getSelectedRow());

            if (CBVorgang.TYP_HVT_KVZ.equals(getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP))) {
                addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_ID_4_HVT_TO_KVZ, esView.getAuftragId());
                return super.goNext();
            }
            else if (isOrderValid4PortChange(esView)) {
                addWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_AUFTRAG_ID_4_PORT_CHANGE, esView.getAuftragId());
                // HUR-8980: Account Übernahme in TAL Wizard
                switch (checkAccountUebernehmen(esView.getAuftragId())) {
                    case JOptionPane.YES_OPTION:
                        addWizardObject(WIZARD_OBJECT_TRANSFER_ACCOUNT_FROM, esView.getAuftragId());
                        break;
                    case JOptionPane.NO_OPTION:
                        break;
                    default:
                        // goNext verhindern, da User Dialog zur Account
                        // Übernahme mit X abgebrochen hat oder eine Exception
                        // geflogen ist
                        return false;
                }
                return super.goNext();
            }
            return false;
        }
        else {
            String selectionMessageKey = "no.selection.msg";
            if (CBVorgang.TYP_HVT_KVZ.equals(getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP))) {
                selectionMessageKey = "hvt_to_kvz.no.selection.msg";
            }
            MessageHelper.showMessageDialog(this, getSwingFactory().getText(selectionMessageKey),
                    getSwingFactory().getText("no.selection.title"), AKJOptionDialog.WARNING_MESSAGE);
            return false;
        }
    }

    /**
     * HUR-8980: Account Übernahme in TAL Wizard
     * 
     * @return NO_OPTION=Account nicht uebernehmen, YES_OPTION=Account uebernehmen, DEFAULT_OPTION=Wizard Vorgang 'Next'
     *         abbrechen
     */
    private int checkAccountUebernehmen(Long auftragIdOld) {
        try {
            AccountService accountService = getCCService(AccountService.class);
            if (!accountService.isAccountMovable(auftragDaten.getAuftragId(), auftragIdOld)) {
                return JOptionPane.NO_OPTION;
            }
            return MessageHelper.showYesNoQuestion(HurricanSystemRegistry.instance().getMainFrame(),
                    getSwingFactory().getText("transfer.account.message"),
                    getSwingFactory().getText("transfer.account.title"));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            return JOptionPane.DEFAULT_OPTION;
        }
    }

    private boolean isOrderValid4PortChange(AuftragEndstelleView esView) {
        if (NumberTools.equal(esView.getAuftragStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT)) {
            int option = MessageHelper.showYesNoQuestion(HurricanSystemRegistry.instance().getMainFrame(),
                    getSwingFactory().getText("order.is.cancelled.message"),
                    getSwingFactory().getText("order.is.cancelled.title"));
            return (option == JOptionPane.YES_OPTION);
        }
        return true;
    }


    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            final EndstellenService endstellenService = getCCService(EndstellenService.class);
            List<Endstelle> endstellen = endstellenService.findEndstellen4Auftrag(auftragDaten.getAuftragId());

            String esTyp = null;
            if (CollectionTools.isNotEmpty(endstellen)) {
                CarrierService carrierService = getCCService(CarrierService.class);
                Carrierbestellung carrierbestellung = carrierService.findCB(
                        (Long) getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CB_ID));

                for (Endstelle es : endstellen) {
                    if (NumberTools.equal(carrierbestellung.getCb2EsId(), es.getCb2EsId())) {
                        esTyp = es.getEndstelleTyp();
                        geoId = es.getGeoId();
                        break;
                    }
                }
            }

            // Auftraege ermitteln und dem TableModel uebergeben
            Collection<AuftragEndstelleView> views = endstellenService.findEndstellen4TalPortAenderung(geoId, esTyp);
            if (CBVorgang.TYP_HVT_KVZ.equals(getWizardObject(CreateElTALVorgangWizard.WIZARD_OBJECT_CBVORGANG_TYP)) &&
                    CollectionUtils.isNotEmpty(views)) {
                final HVTService hvtService = getCCService(HVTService.class);
                views = Collections2.filter(views, new Predicate<AuftragEndstelleView>() {
                    @Override
                    public boolean apply(AuftragEndstelleView auftragEndstelleView) {
                        if (Endstelle.ENDSTELLEN_TYP_B.equals(auftragEndstelleView.getEndstelleTyp())) {
                            HVTStandort hvtStandort = null;
                            try {
                                final Endstelle endstelle = endstellenService.findEndstelle(auftragEndstelleView.getEndstelleId());
                                hvtStandort = hvtService.findHVTStandort(endstelle.getHvtIdStandort());
                            }
                            catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                                MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
                            }
                            return (hvtStandort != null) && hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_HVT);
                        }
                        return false;
                    }
                });
            }
            tbMdlAuftrag.setData(views);
            if (views.isEmpty()) {
                MessageHelper.showMessageDialog(this, getSwingFactory().getText("not.found.msg"),
                        null, AKJOptionDialog.WARNING_MESSAGE);
            }
            else {
                // falls nur ein Auftrag auf "Kuendigung Erfassung" steht, dann diesen selektieren
                int rowWithKuendigungErfassung = -1;
                int row = 0;
                for (AuftragEndstelleView view : views) {
                    if (NumberTools.equal(view.getAuftragStatusId(), AuftragStatus.KUENDIGUNG_ERFASSEN)) {
                        if (rowWithKuendigungErfassung >= 0) {
                            rowWithKuendigungErfassung = -1;
                            break;
                        }
                        rowWithKuendigungErfassung = row;
                    }
                    row++;
                }

                if (rowWithKuendigungErfassung >= 0) {
                    tbAuftrag.selectAndScrollToRow(rowWithKuendigungErfassung);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

}
