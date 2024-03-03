/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.time.*;
import java.util.*;
import javax.swing.*;
import javax.validation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageBuilder;
import de.mnet.wbci.service.WbciTvService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;

public class TvDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final long serialVersionUID = 2588851120053215119L;

    private static final Logger LOGGER = Logger.getLogger(TvDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/TvDialog.xml";

    public static final String TV_TITLE = "tv.title";
    public static final String WECHSELTERMIN = "wechseltermin";

    private AKJDateComponent dcWechseltermin;

    private final WbciRequest wbciRequest;

    private WbciTvService wbciTvService;
    private WbciValidationService wbciValidationService;

    /**
     * Konstruktor mit Angabe des {@link WbciRequest}s sowie der {@link TerminverschiebungsAnfrage}, auf die sich die
     * AKM-TR beziehen soll.
     *
     * @param wbciRequest
     */
    public TvDialog(WbciRequest wbciRequest) {
        super(RESOURCE, true, true);
        this.wbciRequest = wbciRequest;

        try {
            initServices();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Looks up to the CCServices and init the service.
     *
     * @throws de.augustakom.common.service.exceptions.ServiceNotFoundException if a service could not be looked up
     */
    protected void initServices() throws ServiceNotFoundException {
        wbciTvService = getCCService("WbciTvService", WbciTvService.class);
        wbciValidationService = getCCService(WbciValidationService.class);
    }

    @Override
    protected final void createGUI() {
        setTitle("TV Daten");

        AKJLabel lblWechseltermin = getSwingFactory().createLabel(WECHSELTERMIN);

        dcWechseltermin = getSwingFactory().createDateComponent(WECHSELTERMIN, true);

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblWechseltermin     , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dtlPnl.add(dcWechseltermin      , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()       , GBCFactory.createGBC(100,100, 4, 8, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dtlPnl, BorderLayout.CENTER);

        configureButton(CMD_SAVE, getSwingFactory().getText("send.txt"), getSwingFactory().getText("send.tooltip"), true, true);
        configureButton(CMD_CANCEL, getSwingFactory().getText("cancel.txt"), getSwingFactory().getText("cancel.tooltip"), true, true);
    }

    @Override
    public final void loadData() {
        if (wbciRequest != null && wbciRequest.getWbciGeschaeftsfall() != null) {
            setTitle(String.format(getSwingFactory().getText(TV_TITLE), wbciRequest.getWbciGeschaeftsfall()
                    .getVorabstimmungsId()));
            LocalDateTime defaultDatetime = DateConverterUtils.asLocalDateTime(wbciRequest.getWbciGeschaeftsfall()
                    .getWechseltermin());
            if (defaultDatetime == null) {
                defaultDatetime = LocalDateTime.now();
            }
            dcWechseltermin.setDateTime(defaultDatetime);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        try {
            TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageBuilder()
                    .withWbciGeschaeftsfall(wbciRequest.getWbciGeschaeftsfall())
                    .withIoType(IOType.OUT)
                    .withTvTermin(getWechseltermin())
                    .build();

            if (isTvRequestValid(tv)) {
                wbciTvService.createWbciTv(tv, tv.getVorabstimmungsId());
                prepare4Close();
                setValue(AKJOptionDialog.OK_OPTION);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    protected LocalDate getWechseltermin() {
        Date wechseltermin = dcWechseltermin.getDate(null);
        if (wechseltermin != null) {
            return DateConverterUtils.asLocalDate(wechseltermin);
        }
        return null;
    }

    protected boolean isTvRequestValid(TerminverschiebungsAnfrage tv) {
        Set<ConstraintViolation<TerminverschiebungsAnfrage>> errors = wbciValidationService.checkWbciMessageForErrors(tv.getEKPPartner(), tv);
        if (errors != null && !errors.isEmpty()) {
            String title = "Die Terminverschiebung ist nicht vollstaendig";
            String errorMsg = new ConstraintViolationHelper().generateErrorMsg(errors);
            Object[] options = { "OK" };
            MessageHelper.showOptionDialog(this, errorMsg, title, JOptionPane.ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            return false;
        }
        else {
            Set<ConstraintViolation<TerminverschiebungsAnfrage>> warnings = wbciValidationService.checkWbciMessageForWarnings(tv.getEKPPartner(), tv);
            if (warnings != null && !warnings.isEmpty()) {
                String title = "Terminverschiebung abschicken?";
                String warningMsg = new ConstraintViolationHelper().generateWarningMsg(warnings);
                int result = MessageHelper.showYesNoQuestion(this, warningMsg, title);
                if (result == JOptionPane.NO_OPTION) {
                    return false;
                }
            }
        }
        return true;
    }

}
