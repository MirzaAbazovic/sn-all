/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2011 15:54:38
 */
package de.mnet.wita.model;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * Modell zur Abbildung der TAM-Bearbeitung. Es kann nur einen TAM-Usertasks zu einem CB-Vorgang geben, auch wenn
 * mehrere TAMs empfangen wurden.
 */
@DiscriminatorValue("TAM")
@Entity
public class TamUserTask extends UserTask {

    private static final long serialVersionUID = 7778857013264588058L;

    private TamBearbeitungsStatus tamBearbeitungsStatus = TamBearbeitungsStatus.OFFEN;
    /**
     * Gibt an, ob auf den UserTask eine TV60 geschickt wurde. Wird verwendet um die Restfrist zu erhoehen.
     */
    private boolean tv60Sent = false;

    private boolean mahnTam = false;

    /**
     * Zusaeztlicher Status zu einer TAM. Bisher reine Dokumentation.
     */
    public enum TamBearbeitungsStatus {
        OFFEN("Offen"),
        IN_BEARBEITUNG("In Bearbeitung"),
        KUNDE_NICHT_ERREICHT("Kunde nicht erreicht"),
        KUNDE_MELDET_SICH("Kunde meldet sich"),
        TV_60_TAGE("TV 60 Tage"),
        TV_30_TAGE("TV 30 Tage"),
        INFO_MAILED("Info mailed");

        private final String display;

        private TamBearbeitungsStatus(String display) {
            this.display = display;
        }

        /**
         * @return spezielle Bezeichnung zum Anzeigen des Enum
         */
        public String getDisplay() {
            return display;
        }
    }

    public static String appendStatusAenderungToBemerkungen(TamBearbeitungsStatus tamBearbeitungsStatusNeu,
            TamBearbeitungsStatus tamBearbeitungsStatusAlt, String bemerkungenAltIn) {
        String bemerkungenAlt = bemerkungenAltIn;
        if (bemerkungenAlt == null) {
            bemerkungenAlt = "";
        }
        if (tamBearbeitungsStatusAlt != tamBearbeitungsStatusNeu) {
            bemerkungenAlt += " [Status von '" + tamBearbeitungsStatusAlt.getDisplay() + "' nach '"
                    + tamBearbeitungsStatusNeu.getDisplay() + "' geändert]";
        }
        return bemerkungenAlt;
    }

    @Column(name = "TAM_BEARBEITUNG_STATUS")
    @NotNull
    @Enumerated(EnumType.STRING)
    public TamBearbeitungsStatus getTamBearbeitungsStatus() {
        return tamBearbeitungsStatus;
    }

    public void setTamBearbeitungsStatus(TamBearbeitungsStatus tamBearbeitungsStatus) {
        this.tamBearbeitungsStatus = tamBearbeitungsStatus;
    }

    @Column(name = "MAHN_TAM")
    public boolean isMahnTam() {
        return mahnTam;
    }

    public void setMahnTam(boolean mahnTam) {
        this.mahnTam = mahnTam;
    }

    @Column(name = "TV_60_SENT")
    public boolean isTv60Sent() {
        return tv60Sent;
    }

    public void setTv60Sent(boolean tv60Sent) {
        this.tv60Sent = tv60Sent;
    }

}
