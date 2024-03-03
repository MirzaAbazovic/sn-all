/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 30.06.2010 15:15:55
  */

package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.util.*;
import java.util.stream.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.tools.NodeProperty;
import de.augustakom.hurrican.gui.base.tree.tools.TreeSortEntry;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.view.UevtBuchtView;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 *
 */
public class UevtBuchtNode extends EquipmentNodeContainer {
    private static final Logger LOGGER = Logger.getLogger(UevtBuchtNode.class);

    public static final NodeProperty<String> PROP_BUCHT =
            NodeProperty.create(String.class, propPath("uevtBucht", "bucht"), "Bucht");

    private final UevtBuchtView uevtBucht;

    public UevtBuchtNode(DynamicTree tree, UevtBuchtView uevtBucht) {
        super(tree);
        this.uevtBucht = uevtBucht;
    }

    public UevtBuchtView getUevtBucht() {
        return uevtBucht;
    }


    @Override
    public Collection<Equipment> getEquipments() {
        try {
            RangierungsService rangierungsService = CCServiceFinder.instance().getCCService(RangierungsService.class);
            Equipment example = new Equipment();
            example.setHvtIdStandort(uevtBucht.getHvtIdStandort());
            example.setRangVerteiler(uevtBucht.getUevt());
            example.setRangLeiste1(uevtBucht.getBucht());
            List<Equipment> equipments = rangierungsService.findEquipments(example, Equipment.HW_EQN);
            return equipments.stream()
                    .filter(e -> e.getHwBaugruppenId() == null)
                    .collect(Collectors.toList());
        }
        catch (Exception ex) {
            LOGGER.error("Konnte Equipments nicht laden", ex);
        }
        return null;
    }

    @Override
    protected List<TreeSortEntry> getDefaultChildSort() {
        return Arrays.asList(new TreeSortEntry(EquipmentNode.class, EquipmentNode.PROP_STIFT, true));
    }

    @Override
    public String getDisplayName() {
        return String.format("Bucht: %s", uevtBucht.getBucht());
    }

    @Override
    public String getIcon() {
        return IMAGE_BASE + "baugruppe.gif";
    }
}
