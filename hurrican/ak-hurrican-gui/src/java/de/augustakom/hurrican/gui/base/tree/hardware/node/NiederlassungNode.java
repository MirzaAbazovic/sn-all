/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 11:06:35
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.util.*;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.tools.NodeProperty;
import de.augustakom.hurrican.gui.base.tree.tools.TreeSortEntry;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * Node fuer den Hardware-Tree zur Darstellung von Niederlassungen;
 *
 *
 */
public class NiederlassungNode extends DynamicTreeNode {
    public static final NodeProperty<String> PROP_NIEDERLASSUNG =
            NodeProperty.create(String.class, propPath("niederlassung", Niederlassung.NAME), "Niederlassung");
    public static final NodeProperty<?>[] NODE_PROPERTIES = new NodeProperty[] {
            PROP_NIEDERLASSUNG
    };

    private final Niederlassung niederlassung;
    private final List<HVTGruppe> hvtGruppen;
    private final List<HVTStandort> hvtStandorte;


    public NiederlassungNode(DynamicTree tree, Niederlassung niederlassung,
            List<HVTGruppe> hvtGruppen, List<HVTStandort> hvtStandorte) {
        super(tree, true);
        this.niederlassung = niederlassung;
        this.hvtGruppen = hvtGruppen;
        this.hvtStandorte = hvtStandorte;
    }


    @Override
    public List<DynamicTreeNode> doLoadChildren() throws Exception {
        ReferenceService referenceService = CCServiceFinder.instance().getCCService(ReferenceService.class);
        List<Reference> referenceTypes = referenceService.findReferencesByType(Reference.REF_TYPE_STANDORT_TYP, false);

        List<DynamicTreeNode> result = new ArrayList<DynamicTreeNode>();
        for (Reference reference : referenceTypes) {
            result.add(new StandortTypNode(tree, niederlassung, reference, hvtGruppen, hvtStandorte));
        }
        return result;
    }


    @Override
    public List<TreeSortEntry> getDefaultChildSort() {
        return Arrays.asList(new TreeSortEntry(StandortTypNode.class, StandortTypNode.PROP_STANDORTTYP, true));
    }


    @Override
    public String getDisplayName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(niederlassung.getName());
        return stringBuilder.toString();
    }


    @Override
    public String getIcon() {
        return IMAGE_BASE + "niederlassung.gif";
    }
}
