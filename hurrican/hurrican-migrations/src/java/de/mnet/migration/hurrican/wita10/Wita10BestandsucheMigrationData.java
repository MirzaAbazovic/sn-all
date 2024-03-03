/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.06.2010 16:57:35
 */

package de.mnet.migration.hurrican.wita10;

import de.mnet.migration.common.util.ColumnName;


/**
 *
 */
public class Wita10BestandsucheMigrationData {

    @ColumnName("EXTERNE_AUFTRAGSNUMMER")
    public String externeAuftragsnummer;

    @ColumnName("REQUEST_ID")
    public Long requestID;

    @ColumnName("CB_VORGANG_ID")
    public Long cbVorgangID;

    @ColumnName("GESCHAEFTSFALL_ID")
    public Long geschaeftsfallID;

    @ColumnName("VORABSTIMMUNGSID")
    public String vorabstimmnungsID;

}
