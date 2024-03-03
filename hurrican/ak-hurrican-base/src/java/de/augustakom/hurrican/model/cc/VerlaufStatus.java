/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2004 14:38:43
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell bildet einen Verlaufs-Status ab. <br>
 *
 *
 */
public class VerlaufStatus extends AbstractCCIDModel {

    /**
     * Wert fuer 'ausgetragenVon', wenn ein Verlauf automatisch ausgetragen wurde.
     */
    public static final String AUSGETRAGEN_VON_SYSTEM = "system";

    /**
     * Status-ID fuer Verlauf-Status 'im Umlauf'.
     */
    public static final Long STATUS_IM_UMLAUF = Long.valueOf(1);
    /**
     * Status-ID fuer Verlauf-Status 'in Bearbeitung'.
     */
    public static final Long STATUS_IN_BEARBEITUNG = Long.valueOf(2);
    /**
     * Status-ID fuer Verlauf-Status 'erledigt'.
     */
    public static final Long STATUS_ERLEDIGT = Long.valueOf(3);
    /**
     * Status-ID fuer Verlauf-Status 'erledigt System'.
     */
    public static final Long STATUS_ERLEDIGT_SYSTEM = Long.valueOf(4);
    /**
     * Status-ID fuer Verlauf-Status 'CPS-Bearbeitung'.
     */
    public static final Long STATUS_CPS_BEARBEITUNG = Long.valueOf(5);
    /**
     * Status-ID fuer Verlauf-Status 'erledigt CPS'.
     */
    public static final Long STATUS_ERLEDIGT_CPS = Long.valueOf(6);

    /**
     * Auftrag befindet sich z.Z. bei der zentralen Dispo (4000).
     */
    public static final Long BEI_ZENTRALER_DISPO = Long.valueOf(4000);
    /**
     * Auftrag befindet sich z.Z. bei der Dispo/NP (4100).
     */
    public static final Long BEI_DISPO = Long.valueOf(4100);
    /**
     * Auftrag befindet sich z.Z. bei der Technik (4300).
     */
    public static final Long BEI_TECHNIK = Long.valueOf(4300);
    /**
     * Auftrag wird der Dispo/NP zurueck gemeldet (4500).
     */
    public static final Long RUECKLAEUFER_DISPO = Long.valueOf(4500);
    /**
     * Auftrag wird der zentralen Dispo zurueck gemeldet (4600).
     */
    public static final Long RUECKLAEUFER_ZENTRALE_DISPO = Long.valueOf(4600);
    /**
     * Auftrag wird dem AM zurueck gemeldet (4800).
     */
    public static final Long RUECKLAEUFER_AM = Long.valueOf(4800);
    /**
     * Der Verlauf wurde komplett abgeschlossen (4900)
     */
    public static final Long VERLAUF_ABGESCHLOSSEN = Long.valueOf(4900);
    /**
     * Der Verlauf wurde storniert (4910)
     */
    public static final Long VERLAUF_STORNIERT = Long.valueOf(4910);

    /**
     * Kuendigungsauftrag befindet sich z.Z. bei der Dispo/NP (9110).
     */
    public static final Long KUENDIGUNG_BEI_DISPO = Long.valueOf(9110);
    /**
     * Kuendigungsauftrag befindet sich z.Z. bei der Technik (9120).
     */
    public static final Long KUENDIGUNG_BEI_TECHNIK = Long.valueOf(9120);
    /**
     * Kuendigungsauftrag wird der Dispo zurueck gemeldet (9130).
     */
    public static final Long KUENDIGUNG_RL_DISPO = Long.valueOf(9130);
    /**
     * Kuendigungsauftrag wird dem AM zurueck gemeldet (9140).
     */
    public static final Long KUENDIGUNG_RL_AM = Long.valueOf(9140);
    /**
     * Kuendigungsverlauf wurde komplett abgeschlossen (9190).
     */
    public static final Long KUENDIGUNG_VERLAUF_ABGESCHLOSSEN = Long.valueOf(9190);
    /**
     * Kuendigungsverlauf wurde storniert (9195).
     */
    public static final Long KUENDIGUNG_VERLAUF_STORNIERT = Long.valueOf(9195);

    private String status = null;

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}


