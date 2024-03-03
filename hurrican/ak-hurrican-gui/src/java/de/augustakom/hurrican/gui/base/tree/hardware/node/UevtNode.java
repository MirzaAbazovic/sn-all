/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 30.06.2010 14:33:20
  */

package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.util.*;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.tools.NodeProperty;
import de.augustakom.hurrican.gui.base.tree.tools.TreeSortEntry;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.view.UevtBuchtView;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 *
 */
public class UevtNode extends DynamicTreeNode {

    public static final NodeProperty<String> PROP_UEVT =
            NodeProperty.create(String.class, propPath("uevt", UEVT.UEVT), "Gerätebezeichnung");

    private final UEVT uevt;

    public UevtNode(DynamicTree tree, UEVT uevt) {
        super(tree);
        this.uevt = uevt;
    }

    public UEVT getUevt() {
        return uevt;
    }

    @Override
    protected List<DynamicTreeNode> doLoadChildren() throws Exception {
        List<DynamicTreeNode> result = new ArrayList<DynamicTreeNode>();

        HVTService hvtService = CCServiceFinder.instance().getCCService(HVTService.class);
        List<UevtBuchtView> buchtenForUevt = hvtService.findUevtBuchtenForUevt(uevt.getHvtIdStandort(), uevt.getUevt());

        for (UevtBuchtView uevtBuchtView : buchtenForUevt) {
            result.add(new UevtBuchtNode(tree, uevtBuchtView));
        }

        return result;
    }

    @Override
    protected List<TreeSortEntry> getDefaultChildSort() {
        return Arrays.asList(new TreeSortEntry(UevtBuchtNode.class, UevtBuchtNode.PROP_BUCHT, true));
    }

    @Override
    public String getDisplayName() {
        StringBuilder builder = new StringBuilder("UEVT: ").append(uevt.getUevt());
        return builder.toString();
    }

    @Override
    public String getIcon() {
        return IMAGE_BASE + "dslam.gif";
    }

}
