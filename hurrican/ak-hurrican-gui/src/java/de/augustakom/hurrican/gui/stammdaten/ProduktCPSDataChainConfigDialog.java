/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.07.2009 07:40:23
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.cps.CPSDataChainConfig;
import de.augustakom.hurrican.service.cc.CPSConfigService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Dialog zur Generierung eines CPSDataChainConfig-Objekts. <br> Das generierte Objekt wird gespeichert und ueber die
 * Methode setValue geschrieben und kann vom Client ueber getValue referenziert werden. <br>
 *
 *
 */
public class ProduktCPSDataChainConfigDialog extends AbstractServiceOptionDialog implements
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(ProduktCPSDataChainConfigDialog.class);

    private Produkt produkt = null;

    private AKReferenceField rfSOType = null;
    private AKReferenceField rfChain = null;

    /**
     * Konstruktor mit Angabe des Produkts, fuer das eine CPS-Konfiguration aufgenommen werden soll.
     *
     * @param produkt
     */
    public ProduktCPSDataChainConfigDialog(Produkt produkt) {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktCPSDataChainConfigDialog.xml");
        this.produkt = produkt;
        if (this.produkt == null) {
            throw new IllegalArgumentException("Produkt muss definiert werden!");
        }
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblSOType = getSwingFactory().createLabel("cps.service.order.type");
        AKJLabel lblChain = getSwingFactory().createLabel("cps.chain");
        rfSOType = getSwingFactory().createReferenceField("cps.service.order.type");
        rfChain = getSwingFactory().createReferenceField("cps.chain");

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblSOType, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        child.add(rfSOType, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblChain, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(rfChain, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 2, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            QueryCCService queryService = getCCService(QueryCCService.class);

            Reference soTypeEx = new Reference();
            soTypeEx.setType(Reference.REF_TYPE_CPS_SERVICE_ORDER_TYPE);
            rfSOType.setReferenceFindExample(soTypeEx);
            rfSOType.setFindService(queryService);

            ServiceChain chainEx = new ServiceChain();
            chainEx.setType(ServiceChain.CHAIN_TYPE_CPS);
            rfChain.setReferenceFindExample(chainEx);
            rfChain.setFindService(queryService);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            CPSDataChainConfig cpsChain = new CPSDataChainConfig();
            cpsChain.setProdId(produkt.getId());
            cpsChain.setServiceOrderTypeRefId(rfSOType.getReferenceIdAs(Long.class));
            cpsChain.setServiceChainId(rfChain.getReferenceIdAs(Long.class));

            CPSConfigService cpsCfgService = getCCService(CPSConfigService.class);
            cpsCfgService.storeCPSDataChainConfig(cpsChain);

            prepare4Close();
            setValue(cpsChain);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
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

}


