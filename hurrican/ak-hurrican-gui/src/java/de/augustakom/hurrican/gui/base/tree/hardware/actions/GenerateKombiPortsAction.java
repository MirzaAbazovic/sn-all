/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 08.07.2010 10:58:08
  */

package de.augustakom.hurrican.gui.base.tree.hardware.actions;

import java.awt.event.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.IAuthenticationService;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.hardware.node.EquipmentNode;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 *
 */
public class GenerateKombiPortsAction extends AKAbstractAction {
    private static final Logger LOGGER = Logger.getLogger(GenerateKombiPortsAction.class);

    private final Collection<DynamicTreeNode> nodes;

    /**
     * @param nodes
     * @param hardwareTreeMouseListener
     */
    public GenerateKombiPortsAction(Set<DynamicTreeNode> nodes) {
        this.nodes = nodes;
        setName("Kombi-Port(s) generieren...");
        setActionCommand("generate.ports");
        setTooltip("Generiert zu ADSL-OUT Ports zugehörige ADSL-IN Ports");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            RangierungsService rangierungsService;
            rangierungsService = CCServiceFinder.instance().getCCService(RangierungsService.class);
            AKUserService us = getAuthenticationService(AKUserService.class);
            AKUser user = us.findUserBySessionId(HurricanSystemRegistry.instance().getSessionId());
            int count = 0, existing = 0;
            for (DynamicTreeNode node : nodes) {
                EquipmentNode equipmentNode = (EquipmentNode) node;
                Equipment equipment = equipmentNode.getEquipment();
                if (!Equipment.HW_SCHNITTSTELLE_ADSL_OUT.equals(equipment.getHwSchnittstelle())) {
                    continue;
                }
                Equipment inEquipment = rangierungsService.findEquipmentByBaugruppe(equipment.getHwBaugruppenId(), equipment.getHwEQN(), Equipment.RANG_SS_IN_WILDCARD);
                if (inEquipment != null) {
                    existing++;
                    continue;
                }
                inEquipment = cloneEquipment(equipment);
                inEquipment.setUserW(user.getLoginName());
                rangierungsService.saveEquipment(inEquipment);
                count++;
            }
            MessageHelper.showInfoDialog(null, "Es wurden {0} Ports generiert. Für {1} Ports existierte bereits ein zugehöriger ADSL-IN Port", count, existing);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(null, ex);
        }
    }

    private Equipment cloneEquipment(Equipment equipment) throws CloneNotSupportedException {
        Equipment clone = equipment.clone();
        clone.setStatus(EqStatus.frei);
        clone.setRangBucht(null);
        clone.setRangVerteiler(null);
        clone.setRangLeiste1(null);
        clone.setRangLeiste2(null);
        clone.setRangStift1(null);
        clone.setRangStift2(null);
        clone.setHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_ADSL_IN);
        clone.setRangSSType(Equipment.RANG_SS_ADSL_IN);
        return clone;
    }

    protected <T extends IAuthenticationService> T getAuthenticationService(Class<T> type) throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        return locator.getService(type.getName(), type, null);
    }
}
