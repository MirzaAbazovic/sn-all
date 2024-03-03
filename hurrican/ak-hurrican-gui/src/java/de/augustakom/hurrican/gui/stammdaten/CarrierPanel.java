/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2004 13:08:41
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKSimpleModelOwner;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierVaModus;

/**
 * Panel fuer die Darstellung eines Carriers. <br> Dieses Panel wird ausserdem in dem Admin-Frame fuer die Carrier-Daten
 * verwendet!
 */
public class CarrierPanel extends AbstractServicePanel implements AKSimpleModelOwner {

    private static final long serialVersionUID = 6940016851556003079L;
    private static final Logger LOGGER = Logger.getLogger(CarrierPanel.class);
    private static final String XML_SOURCE = "de/augustakom/hurrican/gui/stammdaten/resources/CarrierPanel.xml";

    private static final String CARRIER_BEZEICHNUNG = "carrier.bezeichnung";
    private static final String CARRIER_BESTERFORDERLICH = "carrier.besterfoderlich";
    private static final String CARRIER_NETZBETREIBER = "carrier.netzbetreiber";
    private static final String CARRIER_FIRMENNAME = "carrier.firmenname";
    private static final String PORTIERUNGSKENNUNG = "carrier.portierungskennung";
    private static final String VA_MODUS = "carrier.vorabstimmungsmodus";
    private static final String ITU_CARRIER_CODE = "carrier.itu.carrier.code";
    private static final String CARRIER_ORDER_NO = "carrier.orderno";
    private static final String HAS_WITA = "has.wita";
    private static final String NEEDS_CUDA_KUENDIGUNG = "needs.cuda.kuendigung";

    private AKJTextField tfName;
    private AKJTextField tfNetzbetreiber;
    private AKJTextField tfFirmenname;
    private AKJTextField tfOrderNo;
    private AKJTextField tfPortKennung;
    private AKJTextField tfItuCarrierCode;
    private AKJComboBox cbVaModus;
    private AKJCheckBox cbBestErforderlich;
    private AKJCheckBox cbHasWita;
    private AKJCheckBox cbNeedsCudaKue;

    /**
     * Variable enableGUI entscheidet ob GUI-Elemente zum Editieren freigegeben werden können.
     */
    private boolean enableGUI = false;
    private Carrier carrier;

    public CarrierPanel() {
        super(XML_SOURCE);
        this.createGUI();
    }

    public CarrierPanel(boolean enableGUI) {
        super(XML_SOURCE);
        this.enableGUI = enableGUI;
        this.createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblName = getSwingFactory().createLabel(CARRIER_BEZEICHNUNG);
        AKJLabel lblBestErforderlich = getSwingFactory().createLabel(CARRIER_BESTERFORDERLICH);
        AKJLabel lblNetzbetreiber = getSwingFactory().createLabel(CARRIER_NETZBETREIBER);
        AKJLabel lblFirmenname = getSwingFactory().createLabel(CARRIER_FIRMENNAME);
        AKJLabel lblOrderNo = getSwingFactory().createLabel(CARRIER_ORDER_NO);
        AKJLabel lblPortKennung = getSwingFactory().createLabel(PORTIERUNGSKENNUNG);
        AKJLabel lblItuCarrierCode = getSwingFactory().createLabel(ITU_CARRIER_CODE);
        AKJLabel lblHasWita = getSwingFactory().createLabel(HAS_WITA);
        AKJLabel lblNeedsCudaKue = getSwingFactory().createLabel(NEEDS_CUDA_KUENDIGUNG);
        AKJLabel lblVaModus = getSwingFactory().createLabel(VA_MODUS);

        tfName = getSwingFactory().createTextField(CARRIER_BEZEICHNUNG);
        cbBestErforderlich = getSwingFactory().createCheckBox(CARRIER_BESTERFORDERLICH);
        tfNetzbetreiber = getSwingFactory().createTextField(CARRIER_NETZBETREIBER);
        tfFirmenname = getSwingFactory().createTextField(CARRIER_FIRMENNAME);
        tfOrderNo = getSwingFactory().createTextField(CARRIER_ORDER_NO);
        tfPortKennung = getSwingFactory().createTextField(PORTIERUNGSKENNUNG);
        tfItuCarrierCode = getSwingFactory().createTextField(ITU_CARRIER_CODE);
        cbVaModus = getSwingFactory().createComboBox(VA_MODUS,
                new AKCustomListCellRenderer<>(CarrierVaModus.class, CarrierVaModus::name));
        cbVaModus.addItems(ImmutableList.copyOf(CarrierVaModus.values()));
        cbVaModus.setSelectedItem(CarrierVaModus.FAX);
        cbHasWita = getSwingFactory().createCheckBox(HAS_WITA);
        cbNeedsCudaKue = getSwingFactory().createCheckBox(NEEDS_CUDA_KUENDIGUNG);

        AKJPanel carrierPanel = new AKJPanel(new GridBagLayout());
        // @formatter:off
        carrierPanel.add(lblName            , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(new AKJPanel()     , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        carrierPanel.add(tfName             , GBCFactory.createGBC(100,  0, 2, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(lblBestErforderlich, GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(cbBestErforderlich , GBCFactory.createGBC(100,  0, 2, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(lblNetzbetreiber   , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(tfNetzbetreiber	, GBCFactory.createGBC( 75,  0, 2, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(lblFirmenname      , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(tfFirmenname       , GBCFactory.createGBC( 25,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(lblPortKennung     , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(tfPortKennung      , GBCFactory.createGBC( 25,  0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(lblItuCarrierCode  , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(tfItuCarrierCode   , GBCFactory.createGBC( 25,  0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(lblOrderNo         , GBCFactory.createGBC(  0,  0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(tfOrderNo          , GBCFactory.createGBC( 25,  0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(lblHasWita         , GBCFactory.createGBC(  0,  0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(cbHasWita          , GBCFactory.createGBC( 25,  0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(lblNeedsCudaKue    , GBCFactory.createGBC(  0,  0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(cbNeedsCudaKue     , GBCFactory.createGBC( 25,  0, 2, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(lblVaModus         , GBCFactory.createGBC(  0,  0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        carrierPanel.add(cbVaModus          , GBCFactory.createGBC( 25,  0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        manageGUI();

        this.setLayout(new GridBagLayout());
        // @formatter:off
        this.add(new AKJPanel()     		, GBCFactory.createGBC(  0,  5, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(carrierPanel       		, GBCFactory.createGBC( 20,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()     		, GBCFactory.createGBC( 80,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel()     		, GBCFactory.createGBC(  0, 60, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        load();

        // Sperre GUI fürs Editieren
        enableGUI(false);
    }

    @Override
    public void setModel(Observable model) {
        this.carrier = (model instanceof Carrier) ? (Carrier) model : null;
        if (model != null) {
            tfName.setText(carrier.getName());
            cbBestErforderlich.setSelected(carrier.getCbNotwendig());
            tfNetzbetreiber.setText(carrier.getElTalEmpfId());
            tfFirmenname.setText(carrier.getCompanyName());
            tfOrderNo.setText(carrier.getOrderNo());
            tfPortKennung.setText(carrier.getPortierungskennung());
            tfItuCarrierCode.setText(carrier.getItuCarrierCode());
            cbVaModus.setSelectedItem(carrier.getVorabstimmungsModus());
            cbHasWita.setSelected(carrier.getHasWitaInterface());
            cbNeedsCudaKue.setSelected(carrier.getCudaKuendigungNotwendig());
        }
        else {
            this.clear();
        }

        // GUI zum Editieren freigeben
        if (enableGUI) {
            enableGUI(true);
        }
    }

    @Override
    public Object getModel() {
        if (carrier == null) {
            carrier = new Carrier();
        }
        carrier.setName(tfName.getText());
        carrier.setCbNotwendig(cbBestErforderlich.isSelectedBoolean());
        carrier.setElTalEmpfId(tfNetzbetreiber.getText());
        carrier.setCompanyName(tfFirmenname.getText());
        carrier.setOrderNo(Integer.valueOf(tfOrderNo.getText()));
        carrier.setPortierungskennung(tfPortKennung.getText(null));
        carrier.setItuCarrierCode(tfItuCarrierCode.getText(null));
        carrier.setVorabstimmungsModus((CarrierVaModus) cbVaModus.getSelectedItem());
        carrier.setHasWitaInterface(cbHasWita.isSelectedBoolean());
        carrier.setCudaKuendigungNotwendig(cbNeedsCudaKue.isSelectedBoolean());
        return carrier;
    }

    /**
     * Laedt die benoetigten Daten.
     */
    private void load() {
        try {
            setWaitCursor();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * 'Loescht' alle Felder
     */
    private void clear() {
        GuiTools.cleanFields(this);
    }

    /**
     * Setzt die editable-Attribute aller GUI-Elemente
     */
    private void enableGUI(boolean enableGUI) {
        GuiTools.enableContainerComponents(this, enableGUI);
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }
}
