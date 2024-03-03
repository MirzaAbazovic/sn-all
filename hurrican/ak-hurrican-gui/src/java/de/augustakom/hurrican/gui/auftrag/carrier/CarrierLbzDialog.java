/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2005 13:04:41
 */
package de.augustakom.hurrican.gui.auftrag.carrier;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.utils.GUIDefinitionHelper;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.gui.GUIDefinition;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.GUIService;

/**
 * Dialog fuer die automatisierte Eingabe von LBZs einer Carrierbestellung.<br> Abhaengig vom Carrier kann dem Dialog
 * ein eigenes Panel uebergeben werden, ueber das die Eingabe der LBZ moeglichst automatisiert erfolgt.
 */
public class CarrierLbzDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final long serialVersionUID = 6179207940466532427L;

    private static final Logger LOGGER = Logger.getLogger(CarrierLbzDialog.class);

    private Long carrierId = null;
    private Long endstelleId = null;
    private AbstractCarrierLbzPanel lbzPanel = null;

    /**
     * opens the {@link CarrierLbzDialog} and writes the result to the supplied {@code textField}
     */
    public static void showCarrierLbzDialogFor(AKJTextField textField, Long carrierId, Long endstelleId) {
        CarrierLbzDialog dlg = new CarrierLbzDialog(carrierId, endstelleId);
        Object result = DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dlg, true, true);
        if ((result instanceof String) && StringUtils.isNotBlank((String) result)) {
            String lbz = (String) result;
            if (StringUtils.isNotBlank(textField.getText())) {
                int option = MessageHelper.showConfirmDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        String.format("Soll die LBZ %s mit %s ueberschrieben werden?",
                                textField.getText(), lbz), "LBZ ueberschreiben?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                );
                if (option == JOptionPane.YES_OPTION) {
                    textField.setText(lbz);
                }
            }
            else {
                textField.setText(lbz);
            }
        }
    }

    /**
     * Konstruktor mit Angabe des Carriers sowie der Endstelle
     */
    public CarrierLbzDialog(Long carrierId, Long endstelleId) {
        super("de/augustakom/hurrican/gui/auftrag/resources/CarrierLbzDialog.xml");
        this.carrierId = carrierId;
        this.endstelleId = endstelleId;
        loadData();
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "Ok", getSwingFactory().getText("tooltip.save"), true, true);
        configureButton(CMD_CANCEL, "Abbrechen", getSwingFactory().getText("tooltip.cancel"), true, true);

        AKJPanel child = getChildPanel();
        child.setLayout(new BorderLayout());
        child.add(lbzPanel, BorderLayout.CENTER);
    }

    @Override
    protected void validateSaveButton() {
        // not used
    }

    @Override
    public final void loadData() {
        try {
            GUIService guis = getCCService(GUIService.class);
            List<GUIDefinition> defs = guis.getGUIDefinitions4Reference(carrierId, Carrier.class.getName(),
                    GUIDefinition.TYPE_PANEL);
            List<AKJPanel> panels = GUIDefinitionHelper.createPanels(defs);
            CollectionUtils.filter(panels, new Predicate() {
                @Override
                public boolean evaluate(Object obj) {
                    return (obj != null) ? AbstractCarrierLbzPanel.class.isAssignableFrom(obj.getClass()) : false;
                }
            });

            if ((panels != null) && (panels.size() == 1)) {
                this.lbzPanel = (AbstractCarrierLbzPanel) panels.get(0);
                this.lbzPanel.setEndstelleId(endstelleId);
                this.lbzPanel.setCarrierId(carrierId);
                this.lbzPanel.loadData();
            }
            else {
                throw new IllegalArgumentException("Für den gewählten Carrier ist keine automatisierte Eingabe "
                        + "der LBZ verfügbar!");
            }
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalArgumentException("Mit den Parametern konnte kein LBZ-Panel erzeugt werden!");
        }
    }

    @Override
    protected void doSave() {
        try {
            String lbz = lbzPanel.getLbz();
            CarrierService cs = getCCService(CarrierService.class);
            cs.validateLbz(carrierId, lbz);

            prepare4Close();
            setValue(lbz);
        }
        catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
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
