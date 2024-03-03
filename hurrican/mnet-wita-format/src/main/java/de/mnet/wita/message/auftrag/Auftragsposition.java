/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2011 16:09:52
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.validators.SchaltangabenValid;

/**
 * MWF-Objekt fuer die Abbildung der WITA 'Auftragsposition'.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_AUFTRAGSPOSITION")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_AUFTRAGSPOSITION_0", allocationSize = 1)
@SchaltangabenValid
public class Auftragsposition extends MwfEntity {

    private static final long serialVersionUID = 4496914864477981341L;
    private static final Logger LOGGER = Logger.getLogger(Auftragsposition.class);

    /**
     * Enum mit den moeglichen Werten der DTAG Produkte. ACHTUNG! Neue Werte unbedingt in der View V_HURRICAN_DTAG_CB
     * nachpflegen!!!
     */
    public enum ProduktBezeichner {
        // @formatter:off

        // HVt
        HVT_2N("TAL; CuDA 2 Draht (HVt)", 1, "96U"),
        HVT_2ZWR("TAL; CuDA 2 Draht mit ZwR", 1, "96Y"),
        HVT_4ZWR("TAL; CuDA 2 Draht mit ZwR", 1, "96Z"),
        HVT_2H("TAL; CuDA 2 Draht hbr (HVt)", 1, "96W"),
        HVT_4H("TAL; CuDA 4 Draht hbr (HVt)", 2, "96X"),

        // KVz
        KVZ_2H("TAL; CuDA 2 Draht hbr (KVz)", 1, "9KW", true),
        KVZ_4H("TAL; CuDA 4 Draht hbr (KVz)", 2, "9KX", true),

        // REX-MK ProduktBezeichner
        TAL_REXMK_ONE("TAL; Rufnummernmitnahme Einzelrufnummer", 0, null),
        TAL_REXMK_TWO("Rufnummernmitnahme Durchwahlrufnummer", 0, null),
        TAL_REXMK_MUL("TAL; Rufnummernmitnahme Mehrfachrufnummer", 0, null),

        // nachfolgende ProduktBezeichner nur fuer Kuendigung, nicht fuer Neubestellungen/Aenderungen
        LWL("TAL; Glasfaser 2 Fasern", 2, "95W"),
        HVT_CCA_ZWR("TAL; CCA-Analog", 0, "96R"),
        HVT_CCA("TAL; CCA-Basic ohne ZwR", 0, "96Q"),
        HVT_CCA_PRIMARY("TAL; CCA-Primary", 0, "96T"),
        HVT_OPAL_TELASL("TAL; Analoge TelAsl bei OPAL", 0, "95R"),
        HVT_OPAL("TAL; BaAsl bei OPAL", 0, "95S"),
        HVT_ISIS_TELASL("TAL; Analoge TelAsl bei ISIS-outdoor", 0, "95T"),
        HVT_ISIS("TAL; BaAsl bei ISIS-outdoor", 0, "95U"),
        HVT_ISIS_PMX("TAL; PMxAsl bei ISIS-outdoor", 0, "96D"),
        HVT_2AAL("TAL; CuDA 2 Draht hbr (AAL)", 0, "96C"),
        HVT_4AAL("TAL; CuDA 4 Draht hbr (AAL)", 0, "96S"),

        //nur aufgefuehrt wegen WITA-ESS
        KVZ_9KU("TAL; CuDA 2 Draht (KVz)", 1, "9KU", true),
        LWL_95V("TAL; Glasfaser 1 Faser", 1, "95V"),

        // Default-Produkt fuers UN(!)MARSHALLING, falls ein Produkt kommt, das wir nicht kennen
        UNBEKANNT("Unbekanntes Produkt", 0, null);
        // @formatter:on

        private final String produktName;
        private final int requiredSchaltangabenCount;
        private final String leitungsSchluesselZahl;
        private boolean kvz = false;

        /**
         * @param produktName                Produkt-Name der DTAG
         * @param requiredSchaltangabenCount Anzahl der benötigten Schaltangaben für Bestellung dieser Produktvariante
         */
        private ProduktBezeichner(String produktName, int requiredSchaltangabenCount, String leitungsSchluesselZahl) {
            this.produktName = produktName;
            this.requiredSchaltangabenCount = requiredSchaltangabenCount;
            this.leitungsSchluesselZahl = leitungsSchluesselZahl;
        }

        private ProduktBezeichner(String produktName, int requiredSchaltangabenCount, String leitungsSchluesselZahl, boolean kvz) {
            this.produktName = produktName;
            this.requiredSchaltangabenCount = requiredSchaltangabenCount;
            this.leitungsSchluesselZahl = leitungsSchluesselZahl;
            this.kvz = kvz;
        }

        public String getProduktName() {
            return produktName;
        }

        public int getRequiredSchaltangabenCount() {
            return requiredSchaltangabenCount;
        }

        public boolean isKvz() {
            return kvz;
        }

        public static ProduktBezeichner getByProduktName(String produktName) {
            for (ProduktBezeichner produktBezeichner : ProduktBezeichner.values()) {
                if (produktBezeichner.produktName.equalsIgnoreCase(StringUtils.trimToEmpty(produktName))) {
                    return produktBezeichner;
                }
            }
            return ProduktBezeichner.UNBEKANNT;
        }

        public static ProduktBezeichner getByLeitungsSchluesselZahl(String leitungsSchluesselZahl) {
            for (ProduktBezeichner produktBezeichner : ProduktBezeichner.values()) {
                if (StringUtils.equalsIgnoreCase(produktBezeichner.leitungsSchluesselZahl, StringUtils.trimToEmpty(leitungsSchluesselZahl))) {
                    return produktBezeichner;
                }
            }
            // Leitungsbezeichnung = LBZ in T_CARRIERBESTELLUNG
            LOGGER.error(String.format("Bitte Leitungsbezeichnung ueberpruefen. Produktbezeichner kann aus "
                    + "erstem Teil der Leitungsbezeichnung nicht richtig ermittelt werden : %s" ,leitungsSchluesselZahl));
            return null;
        }

    }

    private Produkt produkt;
    private ProduktBezeichner produktBezeichner;
    private GeschaeftsfallProdukt geschaeftsfallProdukt;

    private AktionsCode aktionsCode;

    private Auftragsposition position;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 100, columnDefinition = "varchar2(100)")
    public Produkt getProdukt() {
        return produkt;
    }

    public void setProdukt(Produkt produkt) {
        this.produkt = produkt;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, columnDefinition = "varchar2(10)")
    public ProduktBezeichner getProduktBezeichner() {
        return produktBezeichner;
    }

    public void setProduktBezeichner(ProduktBezeichner produktBezeichner) {
        this.produktBezeichner = produktBezeichner;
    }

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public GeschaeftsfallProdukt getGeschaeftsfallProdukt() {
        return geschaeftsfallProdukt;
    }

    public void setGeschaeftsfallProdukt(GeschaeftsfallProdukt geschaeftsfallProdukt) {
        this.geschaeftsfallProdukt = geschaeftsfallProdukt;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 10, columnDefinition = "varchar2(10)")
    public AktionsCode getAktionsCode() {
        return aktionsCode;
    }

    public void setAktionsCode(AktionsCode aktionsCode) {
        this.aktionsCode = aktionsCode;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Auftragsposition getPosition() {
        return position;
    }

    public void setPosition(Auftragsposition position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Auftragsposition [produkt=" + produkt + ", produktBezeichner=" + produktBezeichner
                + ", geschaeftsfallProdukt=" + geschaeftsfallProdukt + ", aktionsCode=" + aktionsCode + ", position="
                + position + "]";
    }
}
