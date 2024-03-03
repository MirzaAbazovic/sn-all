/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.13
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.VoipDn2DnBlockView;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;

public interface VoipDnPlanValidationService extends ICCService {

    public List<String> allErrorMessages(final VoipDnPlanView plan, final AuftragVoipDNView dnView);

    public boolean isValid(final VoipDnPlanView plan, final AuftragVoipDNView dnView);

    /**
     * Liefert true, wenn der Rufnummernplan mehr als MAX_PLAN_LENGTH Blöcke beinhaltet.
     */
    public boolean isTooLong(final VoipDnPlanView plan);

    /**
     * Liefert true, wenn genau ein Block im Rufnummerplan als Zentrale markiert ist und dieser Block kein Ende hat.
     */
    public boolean hasExactlyOneZentrale(final VoipDnPlanView plan);

    /**
     * Liefert true, wenn der Anfang des ersten Blocks und das Ende des letzten Blocks mit rangeFrom und rangeTo der zum
     * Rufnummernplan zugehörigen Rufnummer übereinstimmt.
     */
    public boolean boundaryMatchesRangeFromTo(final VoipDnPlanView plan, final AuftragVoipDNView dnView);

    /**
     * Liefert true, wenn dem \p dnView noch kein VoipDnPlanView mit dem Gültigkeitsdatum von \p plan zugeordnet ist.
     */
    public boolean hasUniqueGueltigAb(final VoipDnPlanView plan, final AuftragVoipDNView dnView);

    /**
     * Findet alle Blockpaare im Rufnummernplan, die Lücken aufweisen.
     */
    public List<Pair<VoipDn2DnBlockView, VoipDn2DnBlockView>> getBlocksWithGaps(final VoipDnPlanView plan);

    /**
     * Findelt alle Blockpaare im Rufnummernplan, die sich überlappen.
     */
    public List<Pair<VoipDn2DnBlockView, VoipDn2DnBlockView>> getOverlappingBlocks(final VoipDnPlanView plan);

    /**
     * Findet alle Blöcke im Rufnummernplan, deren Länge (inkl. ONKz und DNBase) größer als MAX_RUFNUMMER_LENGTH ist.
     */
    public List<VoipDn2DnBlockView> getTooLongBlocks(final VoipDnPlanView plan);

    /**
     * Findet alle Blöcke deren Anfang und Ende eine unterschiedliche Stelligkeit haben.
     */
    public List<VoipDn2DnBlockView> getBlocksWithInconsistentArity(final VoipDnPlanView plan);

    /**
     * Findet alle Blöcke, die nicht ausschließlich aus Nummern bestehen.
     */
    public List<VoipDn2DnBlockView> getNonnumericalBlocks(final VoipDnPlanView plan);

    public static final int MAX_PLAN_LENGTH = 25;

    public static final int MAX_RUFNUMMER_LENGTH = 25;
}
