/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 11:21:36
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.util.*;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.tools.TreeSortEntry;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * Root Node fuer den Hardware-Tree.
 *
 *
 */
public class HardwareRootNode extends DynamicTreeNode {

    public HardwareRootNode(DynamicTree tree) {
        super(tree, true);
    }


    @Override
    public List<DynamicTreeNode> doLoadChildren() throws Exception {
        NiederlassungService niederlassungService = CCServiceFinder.instance().getCCService(NiederlassungService.class);
        List<Niederlassung> niederlassungen = niederlassungService.findNiederlassungen();

        HVTService hvtService = CCServiceFinder.instance().getCCService(HVTService.class);
        List<HVTGruppe> hvtGruppen = hvtService.findHVTGruppen();
        List<HVTStandort> hvtStandorte = hvtService.findHVTStandorte();

        List<DynamicTreeNode> result = new ArrayList<DynamicTreeNode>();
        for (Niederlassung niederlassung : niederlassungen) {
            result.add(new NiederlassungNode(tree, niederlassung, hvtGruppen, hvtStandorte));
        }
        return result;
    }


    @Override
    public List<TreeSortEntry> getDefaultChildSort() {
        return Arrays.asList(TreeSortEntry.create(NiederlassungNode.class, NiederlassungNode.PROP_NIEDERLASSUNG, true));
    }


    @Override
    public String getDisplayName() {
        return "Hardware Tree";
    }

    @Override
    public String getIcon() {
        return "";
    }
}
