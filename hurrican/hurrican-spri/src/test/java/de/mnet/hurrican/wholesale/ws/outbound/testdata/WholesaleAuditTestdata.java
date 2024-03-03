package de.mnet.hurrican.wholesale.ws.outbound.testdata;

import java.time.*;

import de.mnet.hurrican.wholesale.model.PvStatus;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;

/**
 * Created by vergaragi on 13.02.2017.
 */
public class WholesaleAuditTestdata {
    public static WholesaleAudit createWholesaleAudit(){
    WholesaleAudit wholesaleAudit = new WholesaleAudit();
    wholesaleAudit.setVorabstimmungsId("123456");
        wholesaleAudit.setBearbeiter("KSTester");
        wholesaleAudit.setBeschreibung("TestMeldung");
        wholesaleAudit.setDatum(LocalDateTime.now());
        wholesaleAudit.setStatus(PvStatus.EMPFANGEN);
    return wholesaleAudit;
    }
}
