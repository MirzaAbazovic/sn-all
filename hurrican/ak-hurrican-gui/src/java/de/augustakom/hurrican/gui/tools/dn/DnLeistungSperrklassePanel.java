/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.04.2006 11:28:51
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.cc.dn.Sperrklasse;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.SperrklasseService;


/**
 * Panel zur Anzeige und Bearbeitung der Sperrklasse.
 *
 *
 */
public class DnLeistungSperrklassePanel extends AbstractDataPanel {
    private static final Logger LOGGER = Logger.getLogger(DnLeistungSperrklassePanel.class);

    private final HWSwitchType hwSwitchType;
    private final Sperrklasse sperrklasse;
    private final SperrklasseChangedListener sperrklasseChangedListener;
    private Sperrklasse sperrklasseKeine;
    private Sperrklasse sperrklasseStandard;

    private AKJComboBox cbSperrklasse;

    private AKJCheckBox chbAbgehend = null;
    private AKJCheckBox chbNational = null;
    private AKJCheckBox chbInnDienste = null;
    private AKJCheckBox chbMabez = null;
    private AKJCheckBox chbMobil = null;
    private AKJCheckBox chbVpn = null;
    private AKJCheckBox chbPrd = null;
    private AKJCheckBox chbAuskunftsd = null;
    private AKJCheckBox chbInterna = null;
    private AKJCheckBox chbKeine = null;
    private AKJCheckBox chbStandard = null;
    private AKJCheckBox chbOffline = null;
    private AKJCheckBox chbPremiumInt = null;

    private final Map<Sperrklasse.SperrklassenTypEnum, AKJCheckBox> sperrklassenCheckBoxes = new EnumMap<>(
            Sperrklasse.SperrklassenTypEnum.class);
    private SperrklassenItemListener skListener;

    /**
     * @param sperrklasse
     * @param hwSwitchType
     * @param sperrklasseChangedListener wenn nicht <code>null</code>, kann editiert werden. Aenderungen werden an den Listener zurueckgemeldet
     */
    public DnLeistungSperrklassePanel(Sperrklasse sperrklasse, HWSwitchType hwSwitchType, SperrklasseChangedListener sperrklasseChangedListener) {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnLeistungSperrklassePanel.xml");
        this.sperrklasse = sperrklasse;
        this.hwSwitchType = hwSwitchType;
        this.sperrklasseChangedListener = sperrklasseChangedListener;
        createGUI();
        try {
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJPanel center = new AKJPanel(new GridBagLayout());

        if (isImsOrNspSwitch()) {
            AKJLabel lblSperrklasse = getSwingFactory().createLabel("sperrklasse");
            cbSperrklasse = getSwingFactory().createComboBox("sperrklasse", false);
            cbSperrklasse.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if(value != null) {
                        Sperrklasse sk = (Sperrklasse) value;
                        label.setText(sk.getSperrklasseByHwSwitchType(hwSwitchType) + " " + StringUtils.stripToEmpty(sk.getName()));
                    }
                    return label;
                }
            });
            center.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblSperrklasse, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(cbSperrklasse, GBCFactory.createGBC(0, 0, 1, 1, 4, 1, GridBagConstraints.HORIZONTAL));
            center.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        else {
            AKJLabel lblAbgehend = getSwingFactory().createLabel("abgehend");
            AKJLabel lblNational = getSwingFactory().createLabel("national");
            AKJLabel lblInnDienste = getSwingFactory().createLabel("inn.dienste");
            AKJLabel lblMaBez = getSwingFactory().createLabel("mabez");
            AKJLabel lblMobil = getSwingFactory().createLabel("mobil");
            AKJLabel lblVpn = getSwingFactory().createLabel("vpn");
            AKJLabel lblPrd = getSwingFactory().createLabel("prd");
            AKJLabel lblAuskunftsd = getSwingFactory().createLabel("auskunftsdienste");
            AKJLabel lblInterna = getSwingFactory().createLabel("interna");
            AKJLabel lblKeine = getSwingFactory().createLabel("keine");
            AKJLabel lblStandard = getSwingFactory().createLabel("standard");
            AKJLabel lblOffline = getSwingFactory().createLabel("offline");
            AKJLabel lblPremiumInt = getSwingFactory().createLabel("premium.services.int");

            chbKeine = getSwingFactory().createCheckBox("keine", false);
            chbAbgehend = getSwingFactory().createCheckBox("abgehend", false);
            chbNational = getSwingFactory().createCheckBox("national", false);
            chbInnDienste = getSwingFactory().createCheckBox("inn.dienste", false);
            chbMabez = getSwingFactory().createCheckBox("mabez", false);
            chbMobil = getSwingFactory().createCheckBox("mobil", false);
            chbVpn = getSwingFactory().createCheckBox("vpn", false);
            chbPrd = getSwingFactory().createCheckBox("prd", false);
            chbAuskunftsd = getSwingFactory().createCheckBox("auskunftsdienste", false);
            chbInterna = getSwingFactory().createCheckBox("interna", false);
            chbStandard = getSwingFactory().createCheckBox("standard", false);
            chbOffline = getSwingFactory().createCheckBox("offline", false);
            chbPremiumInt = getSwingFactory().createCheckBox("premium.services.int", false);

            center.add(lblAbgehend, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbAbgehend, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblNational, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbNational, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblInnDienste, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbInnDienste, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblOffline, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbOffline, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblMaBez, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbMabez, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblMobil, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbMobil, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblVpn, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbVpn, GBCFactory.createGBC(0, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblPremiumInt, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbPremiumInt, GBCFactory.createGBC(0, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblPrd, GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbPrd, GBCFactory.createGBC(0, 0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblAuskunftsd, GBCFactory.createGBC(0, 0, 6, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbAuskunftsd, GBCFactory.createGBC(0, 0, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblInterna, GBCFactory.createGBC(0, 0, 6, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbInterna, GBCFactory.createGBC(0, 0, 7, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblKeine, GBCFactory.createGBC(0, 0, 6, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbKeine, GBCFactory.createGBC(0, 0, 7, 3, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(lblStandard, GBCFactory.createGBC(0, 0, 6, 4, 1, 1, GridBagConstraints.HORIZONTAL));
            center.add(chbStandard, GBCFactory.createGBC(0, 0, 7, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        }

        this.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);
    }

    private void loadData() throws ServiceNotFoundException, FindException {
        SperrklasseService sperrklasseService = getCCService(SperrklasseService.class);
        if (isImsOrNspSwitch()) {
            List<Sperrklasse> sperrklassen = sperrklasseService.findSperrklasseByHwSwitchType(hwSwitchType);
            cbSperrklasse.addItems(sperrklassen);
            cbSperrklasse.setSelectedItem(sperrklasse);
            if (sperrklasseChangedListener != null) {
                cbSperrklasse.addActionListener(e -> {
                    Sperrklasse selectedItem = (Sperrklasse) cbSperrklasse.getSelectedItem();
                    sperrklasseChangedListener.sperrklasseChanged(selectedItem);
                });
            } else {
                cbSperrklasse.setEnabled(false);
            }
        }
        else {
            sperrklasseKeine = sperrklasseService.findSperrklasseBySperrklasseNo(Sperrklasse.SPERRKLASSE_KEINE, hwSwitchType);
            sperrklasseStandard = sperrklasseService.findSperrklasseBySperrklasseNo(Sperrklasse.SPERRKLASSE_STANDARD, hwSwitchType);
            if (sperrklasse != null && Sperrklasse.SPERRKLASSE_KEINE.equals(sperrklasse.getSperrklasseByHwSwitchType(hwSwitchType))) {
                chbKeine.setSelected(Boolean.TRUE);
            }
            else if (sperrklasse != null) {
                chbAbgehend.setSelected(BooleanTools.nullToFalse(sperrklasse.getAbgehend()));
                chbNational.setSelected(BooleanTools.nullToFalse(sperrklasse.getNational()));
                chbInnDienste.setSelected(BooleanTools.nullToFalse(sperrklasse.getInnovativeDienste()));
                chbMabez.setSelected(BooleanTools.nullToFalse(sperrklasse.getMabez()));
                chbMobil.setSelected(BooleanTools.nullToFalse(sperrklasse.getMobil()));
                chbVpn.setSelected(BooleanTools.nullToFalse(sperrklasse.getVpn()));
                chbPrd.setSelected(BooleanTools.nullToFalse(sperrklasse.getPrd()));
                chbAuskunftsd.setSelected(BooleanTools.nullToFalse(sperrklasse.getAuskunftsdienste()));
                chbInterna.setSelected(BooleanTools.nullToFalse(sperrklasse.getInternational()));
                chbOffline.setSelected(BooleanTools.nullToFalse(sperrklasse.getOffline()));
                chbPremiumInt.setSelected(BooleanTools.nullToFalse(sperrklasse.getPremiumServicesInt()));
                if (sperrklasse.getSperrklasse().equals(Sperrklasse.SPERRKLASSE_STANDARD)) {
                    chbStandard.setSelected(Boolean.TRUE);
                }
                chbKeine.setSelected(Boolean.FALSE);
            }
            if (sperrklasseChangedListener != null) {
                prepareSperrklassenListener();
                enableSperrklassen(false);
            }
        }
    }

    private void prepareSperrklassenListener() {
        skListener = new SperrklassenItemListener();
        chbAbgehend.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.ABGEHEND, chbAbgehend);
        chbNational.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.NATIONAL, chbNational);
        chbInnDienste.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.INNOVATIVE_DIENSTE, chbInnDienste);
        chbMabez.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.MABEZ, chbMabez);
        chbMobil.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.MOBIL, chbMobil);
        chbVpn.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.VPN, chbVpn);
        chbPrd.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.PRD, chbPrd);
        chbAuskunftsd.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.AUSKUNFTSDIENSTE, chbAuskunftsd);
        chbInterna.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.INTERNATIONAL, chbInterna);
        chbOffline.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.OFFLINE, chbOffline);
        chbPremiumInt.addItemListener(skListener);
        sperrklassenCheckBoxes.put(Sperrklasse.SperrklassenTypEnum.PREMIUM_SERVICES_INT, chbPremiumInt);

        chbKeine.addItemListener(skListener);
        chbStandard.addItemListener(skListener);

    }

    private boolean isImsOrNspSwitch() {
        return HWSwitchType.isImsOrNsp(hwSwitchType);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        // not needed for this panel
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() throws AKGUIException {
        // not needed for this panel
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
        // not needed for this panel
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) throws AKGUIException {
        // not needed for this panel
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
        // not needed for this panel
    }

    public void enableSperrklassen(boolean enabled) {
        if (isImsOrNspSwitch()) {
            cbSperrklasse.setEnabled(enabled);
        }
        else {
            if (enabled) {
                skListener.refreshSperrklassen();
            }
            else {
                for (Map.Entry<Sperrklasse.SperrklassenTypEnum, AKJCheckBox> checkboxEntry : sperrklassenCheckBoxes.entrySet()) {
                    checkboxEntry.getValue().setEnabled(enabled);
                }
                chbKeine.setEnabled(enabled);
                chbStandard.setEnabled(enabled);
            }
        }
    }

    /* ItemListener, um die moeglichen Sperrklassen zu validieren */
    class SperrklassenItemListener implements ItemListener {
        void setProperties4Combobox(boolean isSelected, String propertyName, Sperrklasse sk, Sperrklasse sk2)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            if (isSelected) {
                PropertyUtils.setProperty(sk, propertyName, Boolean.TRUE);
                PropertyUtils.setProperty(sk2, propertyName, Boolean.TRUE);
            }
            else {
                PropertyUtils.setProperty(sk, propertyName, null);
                PropertyUtils.setProperty(sk2, propertyName, Boolean.FALSE);
            }
        }

        /**
         * Prüft die Check-Boxen fuer die Sperrklassen auf moegliche kombinationen
         */
        private void validateCheckBox(AKJCheckBox toValidate, Object typ) {
            if (typ == null) {
                toValidate.setEnabled(false);
                toValidate.setSelected(false);
            }
            else {
                toValidate.setEnabled(true);
            }
        }

        void refreshSperrklassen() {
            if (chbKeine.isSelected()) {
                if (sperrklasseChangedListener != null) {
                    sperrklasseChangedListener.sperrklasseChanged(sperrklasseKeine);
                }
                enableSperrklassen(false);
                chbKeine.setEnabled(true);
                chbStandard.setEnabled(false);
            }
            else if (chbStandard.isSelected()) {
                if (sperrklasseChangedListener != null) {
                    sperrklasseChangedListener.sperrklasseChanged(sperrklasseStandard);
                }
                enableSperrklassen(false);
                chbKeine.setEnabled(false);
                chbStandard.setEnabled(true);
            }
            else {
                try {
                    chbKeine.setEnabled(true);
                    chbStandard.setEnabled(true);

                    Sperrklasse skExample1 = new Sperrklasse();
                    Sperrklasse skExample2 = new Sperrklasse();
                    for (Map.Entry<Sperrklasse.SperrklassenTypEnum, AKJCheckBox> checkboxEntry : sperrklassenCheckBoxes.entrySet()) {
                        setProperties4Combobox(checkboxEntry.getValue().isSelected(),
                                checkboxEntry.getKey().getPropertyName(), skExample1, skExample2);
                    }

                    SperrklasseService sperrklasseService = getCCService(SperrklasseService.class);
                    List<Long> types = sperrklasseService.findPossibleSperrtypen(skExample1, hwSwitchType);
                    Sperrklasse sperrklasseFromBoxes = sperrklasseService.findSperrklasseByExample(skExample2, hwSwitchType);
                    SperrtypPredicate p = new SperrtypPredicate();
                    for (Sperrklasse.SperrklassenTypEnum sperrklassenTyp : Sperrklasse.SperrklassenTypEnum.values()) {
                        p.setSperrTyp2Check(sperrklassenTyp.getId());
                        validateCheckBox(sperrklassenCheckBoxes.get(sperrklassenTyp), CollectionUtils.find(types, p));
                    }

                    if (sperrklasseChangedListener != null) {
                        sperrklasseChangedListener.sperrklasseChanged(sperrklasseFromBoxes);
                    }
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }

        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            refreshSperrklassen();
        }
    }

    /**
     * Predicate, um Sperrtypen zu filtern.
     */
    static class SperrtypPredicate implements Predicate {
        private Long sperrTyp2Check = null;

        public void setSperrTyp2Check(Long x) {
            this.sperrTyp2Check = x;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object arg) {
            return sperrTyp2Check.equals(arg);
        }
    }

    interface SperrklasseChangedListener {
        void sperrklasseChanged(Sperrklasse sperrklasse);
    }

}
