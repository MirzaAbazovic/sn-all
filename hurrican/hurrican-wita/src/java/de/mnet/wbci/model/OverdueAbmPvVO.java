/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 23.06.2014 
 */
package de.mnet.wbci.model;

import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;

/**
 * Container for preagreements that are missing the Wita {@link AuftragsBestaetigungsMeldungPv} Meldung.
 */
public class OverdueAbmPvVO extends BaseOverdueVo {

    private static final long serialVersionUID = -8943559749170332860L;
    private String vertragsnummer;
    private boolean akmPvReceived;
    private boolean abbmPvReceived; 

    public String getVertragsnummer() {
        return vertragsnummer;
    }

    public void setVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
    }

    public boolean isAkmPvReceived() {
        return akmPvReceived;
    }

    public void setAkmPvReceived(boolean akmPvReceived) {
        this.akmPvReceived = akmPvReceived;
    }

    public boolean isAbbmPvReceived() {
        return abbmPvReceived;
    }

    public void setAbbmPvReceived(boolean abbmPvReceived) {
        this.abbmPvReceived = abbmPvReceived;
    }
}
