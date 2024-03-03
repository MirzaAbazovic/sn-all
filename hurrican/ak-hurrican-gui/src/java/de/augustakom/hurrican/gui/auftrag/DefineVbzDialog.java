/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.10.2009 15:36:00
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Dialog, um die Verbindungsbezeichnung zu definieren.
 *
 *
 */
public class DefineVbzDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, ItemListener {

    private static final Logger LOGGER = Logger.getLogger(DefineVbzDialog.class);

    // GUI-Elemete
    private AKJTextField tfCustomerIdent;
    private AKJComboBox cbKindOfUseProduct;
    private AKJComboBox cbKindOfUseType;
    private AKJFormattedTextField tfUniqueCode;
    private AKJCheckBox chbOverwrite;

    // Modelle
    private final VerbindungsBezeichnung verbindungsBezeichnung;
    private final Produkt produkt;
    private final AuftragTechnik auftragTechnik;
    private Integer originalUniqueCode;

    // Services
    private PhysikService physikService;
    private ReferenceService referenceService;
    private CCAuftragService auftragService;


    public DefineVbzDialog(VerbindungsBezeichnung verbindungsBezeichnung, Produkt produkt, AuftragTechnik auftragTechnik) throws HurricanGUIException {
        super("de/augustakom/hurrican/gui/auftrag/resources/DefineVbzDialog.xml");
        this.verbindungsBezeichnung = verbindungsBezeichnung;
        this.produkt = produkt;
        this.auftragTechnik = auftragTechnik;
        if ((verbindungsBezeichnung == null) || (verbindungsBezeichnung.getUniqueCode() == null)) {
            throw new HurricanGUIException("Verbindungsbezeichnung darf nicht geändert werden!");
        }
        this.originalUniqueCode = this.verbindungsBezeichnung.getUniqueCode();
        init();
        createGUI();
        loadData();
        showValues();
    }


    /**
     * Init des Dialogs
     */
    private void init() {
        try {
            physikService = getCCService(PhysikService.class);
            referenceService = getCCService(ReferenceService.class);
            auftragService = getCCService(CCAuftragService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJLabel lblSeperator = getSwingFactory().createLabel("seperator");
        AKJLabel lblOverwrite = getSwingFactory().createLabel("overwrite");

        AKCustomListCellRenderer renderer = new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue);
        tfCustomerIdent = getSwingFactory().createTextField("customer.ident");
        cbKindOfUseProduct = getSwingFactory().createComboBox("kind.of.use.product");
        cbKindOfUseProduct.setRenderer(renderer);
        cbKindOfUseType = getSwingFactory().createComboBox("kind.of.use.type");
        cbKindOfUseType.setRenderer(renderer);
        tfUniqueCode = getSwingFactory().createFormattedTextField("unique.code");
        chbOverwrite = getSwingFactory().createCheckBox("overwrite");
        chbOverwrite.addItemListener(this);
        enableKindOfUse(false);

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblVbz, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(tfCustomerIdent, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblSeperator, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(cbKindOfUseProduct, GBCFactory.createGBC(0, 0, 5, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(cbKindOfUseType, GBCFactory.createGBC(0, 0, 6, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(tfUniqueCode, GBCFactory.createGBC(100, 0, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblOverwrite, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(chbOverwrite, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 3, 1, 1, GridBagConstraints.BOTH));
    }


    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            List<Reference> products = referenceService.findReferencesByType(Reference.REF_TYPE_VBZ_PRODUCT, Boolean.TRUE);
            cbKindOfUseProduct.addItems(products, true, Reference.class);

            List<Reference> types = referenceService.findReferencesByType(Reference.REF_TYPE_VBZ_TYPE, Boolean.TRUE);
            cbKindOfUseType.addItems(types, true, Reference.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    private void showValues() {
        tfCustomerIdent.setText(verbindungsBezeichnung.getCustomerIdent());
        cbKindOfUseProduct.selectItem("getStrValue", Reference.class, verbindungsBezeichnung.getKindOfUseProduct());
        cbKindOfUseType.selectItem("getStrValue", Reference.class, verbindungsBezeichnung.getKindOfUseType());
        tfUniqueCode.setValue(verbindungsBezeichnung.getUniqueCode());
        chbOverwrite.setSelected(verbindungsBezeichnung.getOverwritten());
    }


    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {

            verbindungsBezeichnung.setCustomerIdent(tfCustomerIdent.getText(null));
            verbindungsBezeichnung.setKindOfUseProduct(((Reference) cbKindOfUseProduct.getSelectedItem()).getStrValue());
            verbindungsBezeichnung.setKindOfUseType(((Reference) cbKindOfUseType.getSelectedItem()).getStrValue());
            verbindungsBezeichnung.setUniqueCode(tfUniqueCode.getValueAsInt(null));
            verbindungsBezeichnung.setOverwritten(chbOverwrite.isSelectedBoolean());

            checkVerbindungsBezeichnung();

            prepare4Close();
            setValue(verbindungsBezeichnung);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Methode überprüft ob VbzId bereits vorhanden, wenn ja, wird die Vbz auf die neue AuftragTechnik verlinkt
     * @throws FindException
     * @throws StoreException
     */
    private void checkVerbindungsBezeichnung() throws FindException, StoreException, ValidationException, HurricanGUIException {
        String concatenateVbz = concatenateVbz();

        VerbindungsBezeichnung existingVerbindungsBezeichnung = physikService.findVerbindungsBezeichnung(concatenateVbz);
        if(existingVerbindungsBezeichnung == null){
            physikService.saveVerbindungsBezeichnung(this.verbindungsBezeichnung);
            return;
        }

        linkExistingVbzToNewAuftragTechnik(existingVerbindungsBezeichnung);

    }

    private String concatenateVbz() {
        return (StringUtils.isNotEmpty(tfCustomerIdent.getText()) ? tfCustomerIdent.getText()+ "-" : StringUtils.EMPTY)
                    + ((Reference) cbKindOfUseProduct.getSelectedItem()).getStrValue()
                    + ((Reference) cbKindOfUseType.getSelectedItem()).getStrValue()
                    + tfUniqueCode.getText();
    }

    /**
     * Bereits existierende VBZ wird dem neuen Auftrag zugeordnet und die alte Verbindung entfernt
     * @param existingVerbindungsBezeichnung
     * @throws FindException
     * @throws HurricanGUIException
     * @throws StoreException
     */
    private void linkExistingVbzToNewAuftragTechnik(VerbindungsBezeichnung existingVerbindungsBezeichnung) throws FindException, HurricanGUIException, StoreException {
        AuftragTechnik auftragTechnik4ExistingVbz = auftragService.findAuftragTechnik4VbzId(existingVerbindungsBezeichnung.getId());
        if(auftragTechnik4ExistingVbz != null){
            checkVbz4AuftragWholesale(auftragTechnik4ExistingVbz);
            checkVbz4AuftragWholesale(this.auftragTechnik);
        }

        this.auftragTechnik.setVbzId(existingVerbindungsBezeichnung.getId());
        auftragService.saveAuftragTechnik(this.auftragTechnik, false);
    }

    /**
     * Überprüft, ob der Auftrag in der Tabelle T_AUFTRAG_WHOLESALE verlinkt ist und wirft
     * für diesen Fall eine Fehlermeldung
     * @param auftragTechnik
     * @throws FindException
     * @throws HurricanGUIException
     */
    private void checkVbz4AuftragWholesale(AuftragTechnik auftragTechnik) throws FindException, HurricanGUIException {
        Auftrag auftrag = auftragService.findAuftragById(auftragTechnik.getAuftragId());
        AuftragWholesale auftragWholesaleByAuftragId = auftragService.findAuftragWholesaleByAuftragId(auftrag.getId());
        if(auftragWholesaleByAuftragId != null){
            throw new HurricanGUIException("Verbindungsbezeichnung darf nicht geändert werden! Der Auftrag mit der ID: " + auftrag.getId() + " ist in T_AUFTRAG_WHOLESALE verknüpft");
        }
    }

    /**
     * Setzt die ComboBoxes fuer die Nutzungsart auf enabled/disabled
     */
    private void enableKindOfUse(boolean enable) {
        cbKindOfUseProduct.setEnabled(enable);
        cbKindOfUseType.setEnabled(enable);
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
    public void update(Observable arg0, Object arg1) {
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == chbOverwrite) {
            if (!chbOverwrite.isSelected()) {
                if (auftragTechnik.getVpnId() == null) {
                    cbKindOfUseProduct.selectItem("getStrValue", Reference.class, produkt == null ? null : produkt.getVbzKindOfUseProduct());
                }
                else {
                    cbKindOfUseProduct.selectItem("getStrValue", Reference.class, VerbindungsBezeichnung.KindOfUseProduct.V);
                }
                cbKindOfUseType.selectItem("getStrValue", Reference.class, produkt == null ? null : produkt.getVbzKindOfUseType());
            }
            enableKindOfUse(chbOverwrite.isSelected());
        }
    }

}


