/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.02.2006 16:58:05
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.SperrklasseService;


/**
 * Dialog zum Kündigen einer Rufnummernleistung.
 *
 *
 */
public class DnLeistung2AuftragKuendigenDialog extends AbstractServiceOptionDialog
        implements AKDataLoaderComponent {

    private static final String KUEND_ALL_DN = "kuend.alldn";

    private static final Logger LOGGER = Logger.getLogger(DnLeistung2AuftragKuendigenDialog.class);

    private DNLeistungsView leistungView = null;

    private AKJDateComponent dcKuendDate = null;
    private AKJTextField tfLeistung = null;

    private AKJCheckBox cbKuendLeistungFuerAlleDn;

    private final HWSwitchType hwSwitchType;

    /**
     * Const.
     *
     * @param dnLeistungsView
     */
    public DnLeistung2AuftragKuendigenDialog(DNLeistungsView dnLeistungsView, HWSwitchType hwSwitchType) {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnLeistung2AuftragKuendigenDialog.xml");
        this.leistungView = dnLeistungsView;
        this.hwSwitchType = hwSwitchType;
        createGUI();
        loadData();
    }

    /**
     * @return Returns the hwSwitchType.
     */
    private HWSwitchType getHwSwitchType() {
        return hwSwitchType;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            if (dcKuendDate.getDate(null) == null) {
                MessageHelper.showInfoDialog(getMainFrame(), "Bitte Kündigungsdatum eingeben!");
            }
            else if (DateTools.isDateBefore(dcKuendDate.getDate(null), new Date())) {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Bitte Kündigungsdatum zu heute oder in die Zukunft eingeben!");
            }
            else {
                prepare4Close();
                if (leistungView != null) {
                    setValue(Integer.valueOf(OK_OPTION));
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    protected Sperrklasse findSperrklasseBySperrklasseNumber(Integer sperrklasseNumber) throws FindException,
            ServiceNotFoundException {
        SperrklasseService service = getCCService(SperrklasseService.class);
        return service.findSperrklasseBySperrklasseNo(sperrklasseNumber, getHwSwitchType());
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("Leistungen kuendigen");

        AKJLabel lblLeistung = getSwingFactory().createLabel("leistung");
        tfLeistung = getSwingFactory().createTextField("leistung", false);
        AKJLabel lblKuendAm = getSwingFactory().createLabel("kuend.am");
        dcKuendDate = getSwingFactory().createDateComponent("kuend.am");
        AKJPanel center = new AKJPanel(new GridBagLayout());
        final AKJLabel lblKuendAllDn = getSwingFactory().createLabel(KUEND_ALL_DN);
        cbKuendLeistungFuerAlleDn = getSwingFactory().createCheckBox(KUEND_ALL_DN);
        cbKuendLeistungFuerAlleDn.setSelected(Boolean.TRUE);

        if ((leistungView.getLeistungsId()).equals(Leistung4Dn.SPERRKLASSE_ID)) {
            Sperrklasse sperrklasse = null;
            try {
                if(leistungView.getParameter() != null) {
                    Integer sperrklasseNumber = Integer.valueOf(leistungView.getParameter());
                    sperrklasse = findSperrklasseBySperrklasseNumber(sperrklasseNumber);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
            DnLeistungSperrklassePanel sk = new DnLeistungSperrklassePanel(sperrklasse, getHwSwitchType(), null);
            center.add(sk, GBCFactory.createGBC(0, 0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        }

        //@formatter:off
        center.add(lblLeistung  ,               GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(tfLeistung   ,               GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(lblKuendAm   ,               GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(dcKuendDate  ,               GBCFactory.createGBC(100,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(lblKuendAllDn,               GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(cbKuendLeistungFuerAlleDn,   GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(center, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        tfLeistung.setText(leistungView.getLeistung());
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

    public boolean isLeistungFuerAlleDnKuendigen() {
        return cbKuendLeistungFuerAlleDn.isSelected();
    }

    public Date getKuendigenAm() {
        return dcKuendDate.getDate(null);
    }
}


