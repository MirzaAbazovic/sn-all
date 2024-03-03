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


/**
 * Node fuer den Hardware-Tree zur Darstellung von Standorttypen;
 *
 *
 */
public class StandortTypNode extends DynamicTreeNode {

    public static final NodeProperty<String> PROP_STANDORTTYP =
            NodeProperty.create(String.class, propPath("standortTyp", Reference.STR_VALUE), "Standorttyp");
    public static final NodeProperty<?>[] NODE_PROPERTIES = new NodeProperty[] {
            PROP_STANDORTTYP
    };

    private final Niederlassung niederlassung;
    private final Reference standortTyp;
    private final List<HVTGruppe> hvtGruppen;
    private final List<HVTStandort> hvtStandorte;


    public StandortTypNode(DynamicTree tree, Niederlassung niederlassung, Reference standortTyp,
            List<HVTGruppe> hvtGruppen, List<HVTStandort> hvtStandorte) {
        super(tree, true);
        this.niederlassung = niederlassung;
        this.standortTyp = standortTyp;
        this.hvtGruppen = hvtGruppen;
        this.hvtStandorte = hvtStandorte;
    }


    @Override
    public List<DynamicTreeNode> doLoadChildren() throws Exception {
        Map<Long, HVTGruppe> hvtGruppeMap = new HashMap<Long, HVTGruppe>();
        for (HVTGruppe hvtGruppe : hvtGruppen) {
            hvtGruppeMap.put(hvtGruppe.getId(), hvtGruppe);
        }

        List<DynamicTreeNode> result = new ArrayList<DynamicTreeNode>();
        for (HVTStandort hvtStandort : hvtStandorte) {
            HVTGruppe hvtGruppe = hvtGruppeMap.get(hvtStandort.getHvtGruppeId());
            if ((hvtGruppe != null) && this.standortTyp.getId().equals(hvtStandort.getStandortTypRefId()) &&
                    this.niederlassung.getId().equals(hvtGruppe.getNiederlassungId())) {
                result.add(new HvtNode(tree, hvtStandort, hvtGruppe));
            }
        }
        return result;
    }


    @Override
    public List<TreeSortEntry> getDefaultChildSort() {
        return Arrays.asList(new TreeSortEntry(HvtNode.class, HvtNode.PROP_NAME, true));
    }


    @Override
    public String getDisplayName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(standortTyp.getStrValue());
        return stringBuilder.toString();
    }


    @Override
    public String getIcon() {
        return IMAGE_BASE + "hvt.gif";
    }
}
