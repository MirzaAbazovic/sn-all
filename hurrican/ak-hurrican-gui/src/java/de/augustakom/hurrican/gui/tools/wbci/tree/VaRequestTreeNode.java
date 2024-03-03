/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tree;

import java.util.*;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciCommonService;

/**
 * TreeNode fuer die Darstellung eines WBCI-Requests.
 */
public class VaRequestTreeNode extends AbstractVaTreeNode {

    private static final long serialVersionUID = -9188200513881216690L;

    private final WbciRequest wbciRequest;

    public VaRequestTreeNode(DynamicTree tree, WbciRequest wbciRequest) {
        super(tree, true);
        this.wbciRequest = wbciRequest;
        setUserObject(wbciRequest);
    }

    @Override
    protected List<DynamicTreeNode> doLoadChildren() throws Exception {
        WbciCommonService wbciCommonService = CCServiceFinder.instance().getCCService(WbciCommonService.class);
        List<Meldung> meldungen = wbciCommonService.findMeldungenForVaId(wbciRequest.getVorabstimmungsId());

        List<Meldung> meldungenForRequest = Collections.emptyList();
        if (wbciRequest instanceof VorabstimmungsAnfrage) {
            meldungenForRequest = wbciCommonService.filterMeldungenForVa(meldungen);
        }
        else if (wbciRequest instanceof TerminverschiebungsAnfrage) {
            meldungenForRequest = wbciCommonService.filterMeldungenForTv(meldungen, ((TerminverschiebungsAnfrage) wbciRequest).getAenderungsId());
        }
        else if (wbciRequest instanceof StornoAnfrage) {
            meldungenForRequest = wbciCommonService.filterMeldungenForStorno(meldungen, ((StornoAnfrage) wbciRequest).getAenderungsId());
        }

        List<DynamicTreeNode> children = new ArrayList<>();
        Collections.reverse(meldungenForRequest);
        for (Meldung meldung : meldungenForRequest) {
            children.add(new VaMeldungTreeNode(getTree(), meldung));
        }
        return children;
    }

    @Override
    public String getDisplayName() {
        String id = wbciRequest.getVorabstimmungsId();
        if (wbciRequest instanceof TerminverschiebungsAnfrage) {
            id = ((TerminverschiebungsAnfrage) wbciRequest).getAenderungsId();
        }
        else if (wbciRequest instanceof StornoAnfrage) {
            id = ((StornoAnfrage) wbciRequest).getAenderungsId();
        }
        return String.format("%s (%s)", wbciRequest.getTyp().getShortName(), id);
    }

    @Override
    protected IOType getIoType() {
        return wbciRequest.getIoType();
    }

}
