/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2015
 */
package de.mnet.migration.hurrican.talbestellungenffm;

import java.util.*;

import de.mnet.migration.common.util.ColumnName;

/**
 * Entity for {@link TALBestellungenFFMTransformer} migration transformer
 *
 */
public class TALBestellungenFFM {

    @ColumnName("ABM_LIEFERTERMIN")
    public Date abmLiefertermin;

    @ColumnName("ABM_ID")
    public Long abmId;

    @ColumnName("EXTERNE_AUFTRAGS_NR")
    public String externeAuftragsNr;

    @ColumnName("MELDUNGS_CODE")
    public String meldungsCode;

    @ColumnName("REQUEST_ID")
    public Long requestId;

    @ColumnName("KWT_ZEITFENSTER")
    public String kwtZeitfenster;

    @ColumnName("CB_VORGANG_ID")
    public Long cbvId;

    @ColumnName("AUFTRAG_ID")
    public Long auftragId;

    @ColumnName("CB_ID")
    public Long cbId;

    @ColumnName("VERLAUF_ID")
    public Long verlaufId;

    @ColumnName("FFM_VERLAUF_ID")
    public Long ffmVerlaufId;

    @ColumnName("FFM_DATUM_ERLEDIGT")
    public Date ffmDatumErledigt;
}
