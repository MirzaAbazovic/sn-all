/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2011 12:12:44
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Dialog, um die Details zu einer RUEM-PV anzugeben und diese zu versenden.
 */
public class SendRuemPvDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, ItemListener {

    private static final long serialVersionUID = -704663102833399544L;
    private static final Logger LOGGER = Logger.getLogger(SendRuemPvDialog.class);

    private static final String RESOUCE = "de/augustakom/hurrican/gui/tools/tal/resources/SendRuemPvDialog.xml";

    private static final String ANTWORT_CODE = "antwort.code";
    private static final String ANTWORT_TEXT = "antwort.text";
    private static final String NEW_DATE = "new.date";

    private AKJComboBox cbAntwortCode;
    private AKJTextArea taAntwortText;

    private final AkmPvUserTask pvUserTask;
    private final boolean sendPositiveRuemPv;

    private WitaTalOrderService witaTalOrderService;

    public SendRuemPvDialog(AkmPvUserTask pvUserTask, boolean sendPositiveRuemPv) {
        super(RESOUCE);
        this.pvUserTask = pvUserTask;
        this.sendPositiveRuemPv = sendPositiveRuemPv;
        initServices();
        createGUI();
        loadData();
    }

    private void initServices() {
        try {
            witaTalOrderService = getCCService(WitaTalOrderService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "Senden", "Sendet die Rückmeldung an die DTAG", true, true);

        AKJLabel lblAntwortCode = getSwingFactory().createLabel(ANTWORT_CODE);
        AKJLabel lblAntwortText = getSwingFactory().createLabel(ANTWORT_TEXT);
        AKJLabel lblNewDate = getSwingFactory().createLabel(NEW_DATE);

        cbAntwortCode = getSwingFactory().createComboBox(ANTWORT_CODE);
        cbAntwortCode.addItemListener(this);
        taAntwortText = getSwingFactory().createTextArea(ANTWORT_TEXT, false);
        AKJScrollPane spAntwortText = new AKJScrollPane(taAntwortText);

        AKJPanel details = new AKJPanel(new GridBagLayout());
        int yCorrdinate = 0;
        // @formatter:off
        details.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 0, yCorrdinate  , 1, 1, GridBagConstraints.NONE));
        details.add(lblAntwortCode      , GBCFactory.createGBC(  0,  0, 1, ++yCorrdinate, 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, 2, yCorrdinate  , 1, 1, GridBagConstraints.NONE));
        details.add(cbAntwortCode       , GBCFactory.createGBC(100,  0, 3, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(lblAntwortText      , GBCFactory.createGBC(  0,  0, 1, ++yCorrdinate, 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(spAntwortText       , GBCFactory.createGBC(100,  0, 3, yCorrdinate  , 1, 1, GridBagConstraints.HORIZONTAL));
        details.add(new AKJPanel()      , GBCFactory.createGBC(100,100, 4, ++yCorrdinate, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(details, BorderLayout.CENTER);
    }

    @Override
    protected void validateSaveButton() {
        // nothing to do - save button should not be managed!
    }

    @Override
    public final void loadData() {
        List<RuemPvAntwortCode> antwortCodes = RuemPvAntwortCode.getRuemPvAntwortCodes(sendPositiveRuemPv);
        Collections.sort(antwortCodes, new Comparator<RuemPvAntwortCode>() {
            @Override
            public int compare(RuemPvAntwortCode o1, RuemPvAntwortCode o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        cbAntwortCode.addItems(antwortCodes, true);
        cbAntwortCode.selectItemRaw((sendPositiveRuemPv) ? RuemPvAntwortCode.OK : RuemPvAntwortCode.SONSTIGES);
    }

    @Override
    protected void doSave() {
        try {
            validateInputs();

            String businessKey = pvUserTask.getExterneAuftragsnummer();
            RuemPvAntwortCode antwortCode = (RuemPvAntwortCode) cbAntwortCode.getSelectedItem();
            String antwortText = taAntwortText.getText(null);
            if (sendPositiveRuemPv) {
                witaTalOrderService.sendPositiveRuemPv(businessKey, antwortCode, antwortText,
                        HurricanSystemRegistry.instance().getCurrentUser());
            }
            else {
                witaTalOrderService.sendNegativeRuemPv(businessKey, antwortCode, antwortText,
                        HurricanSystemRegistry.instance().getCurrentUser());
            }

            MessageHelper.showInfoDialog(this,
                    "Die Rückmeldung Providerwechsel wurde erfolgreich erzeugt und wird demnächst an die Schnittstelle übermittelt.");
            prepare4Close();
            setValue(null);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void validateInputs() throws HurricanGUIException {
        if (taAntwortText.isEditable() && StringUtils.isBlank(taAntwortText.getText(null))) {
            throw new HurricanGUIException(getSwingFactory().getText("antwort.text.missing"));
        }
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
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cbAntwortCode) {
            RuemPvAntwortCode antwortCode = (RuemPvAntwortCode) cbAntwortCode.getSelectedItem();
            taAntwortText.setEditable(antwortCode.antwortTextRequired);
            taAntwortText.setText(antwortCode.defaultText);
        }
    }

}
