/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2009 17:38:21
 */
package de.augustakom.hurrican.model.cc;

import java.lang.reflect.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * Ansprechpartner sind Kontakte mit zusaetzlicher Information: - Typ - Preferred (bevorzugter Ansprechpartner) -
 * Freitext
 */
public class Ansprechpartner extends AbstractCCIDModel implements CCAuftragModel {

    public enum Typ {
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Kunde".
         */
        KUNDE(16000L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Rechnung".
         */
        RECHNUNG(16001L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "vor Ort".
         */
        VOR_ORT(16002L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Rueckfrage".
         */
        RUECKFRAGE(16003L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Technischer Service".
         */
        TECH_SERVICE(16004L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "IPSec-C2S-Kontakt".
         */
        IPSEC_C2S(16005L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Hotline Service".
         */
        HOTLINE_SERVICE(16006L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Hotline Partner".
         */
        HOTLINE_PARTNER(16007L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Endstelle A".
         */
        ENDSTELLE_A(16008L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Endstelle B".
         */
        ENDSTELLE_B(16009L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Hotline Geplante Arbeiten".
         */
        HOTLINE_GA(16010L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Interim Hotline Service".
         */
        HOTLINE_SERVICE_INTERIM(16011L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Interim geplante Arbeiten".
         */
        GEPLANTE_ARBEITEN_INTERIM(16012L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "PBX Enterprise-Administrator".
         */
        PBX_ENTERPRISE_ADMINISTRATOR(16013L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "PBX Site-Administrator".
         */
        PBX_SITE_ADMINISTRATOR(16014L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Partner Service & Wartung NE4".
         */
        PARTNER_SERVICE_WARTUNG_NE4(23003L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Partner Koax Aufschaltung NE4".
         */
        PARTNER_KOAX_AUFSCHALTUNG_NE4(23304L),
        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Partner Koax Modernisierung NE4".
         */
        PARTNER_KOAX_MODERNISIERUNG_NE4(23305L),

        /**
         * Reference-ID fuer den Ansprechpartner-Typ "Partner FttH Modernisierung NE4".
         */
        PARTNER_FTTH_MODERNISIERUNG_NE4(23315L),

        PARTNER_SICHERHEITSABFRAGEN(16015L);

        private final Long refId;

        private Typ(Long refId) {
            this.refId = refId;
        }

        public Long refId() {
            return refId;
        }

        /**
         * Liefert den Ansprechpartner-Typ fuer eine gegebene Reference ID
         *
         * @return der Typ, oder {@code null}, falls es keinen fuer die RefId gibt
         */
        public static Typ forRefId(Long refId) {
            for (Typ t : Typ.values()) {
                if (t.refId.equals(refId)) {
                    return t;
                }
            }
            return null;
        }
    }

    private CCAddress address;
    public static final String ADDRESS = "address";
    public static final String ADDRESS_NAME = "address." + CCAddress.NAME;
    public static final String ADDRESS_VORNAME = "address." + CCAddress.VORNAME;
    public static final String ADDRESS_TELEFON = "address." + CCAddress.TELEFON;
    public static final String ADDRESS_FAX = "address." + CCAddress.FAX;
    public static final String ADDRESS_HANDY = "address." + CCAddress.HANDY;
    public static final String ADDRESS_EMAIL = "address." + CCAddress.EMAIL;
    private Long auftragId;
    private Long typeRefId;
    public static final String TYPE_REF_ID = "typeRefId";
    private Boolean preferred;
    public static final String PREFERRED = "preferred";
    private String text;
    public static final String TEXT = "text";
    private Integer prio;
    public static final String PRIO = "prio";


    /**
     * Gibt einen Display-Text fuer den Ansprechpartner zurueck. Dieser setzt sich wie folgt zusammen: <br> <ul>
     * <li>adresse.name <li>adresse.vorname <li>Tel.: adresse.telefon <li>Fax: adresse.fax <li>Mail: adresse.email
     * </ul>
     *
     * @return
     */
    public String getDisplayText() {
        if (address != null) {
            String name = StringTools.join(new String[] { address.getName(), address.getVorname() }, " ", true);

            StringBuilder builder = new StringBuilder();
            builder.append(name);
            builder.append(";");

            if (getAddress() != null) {
                if (StringUtils.isNotBlank(getAddress().getTelefon())) {
                    builder.append(" Tel.: ");
                    builder.append(getAddress().getTelefon());
                    builder.append(";");
                }
                if (StringUtils.isNotBlank(getAddress().getHandy())) {
                    builder.append(" Mobil: ");
                    builder.append(getAddress().getHandy());
                    builder.append(";");
                }
                if (StringUtils.isNotBlank(getAddress().getFax())) {
                    builder.append(" Fax: ");
                    builder.append(getAddress().getFax());
                    builder.append(";");
                }
                if (StringUtils.isNotBlank(getAddress().getEmail())) {
                    builder.append(" Mail: ");
                    builder.append(getAddress().getEmail());
                }
            }

            return builder.toString();
        }
        else if (StringUtils.isNotBlank(getText())) {
            return StringUtils.replace(getText(), "\n", " || ");
        }
        return null;
    }

    /**
     * Erzeugt eine Kopie des Ansprechpartner-Objekts.
     *
     * @param toCopy        Das zu kopierenden Objekt
     * @param auftragIdDest Die Ziel-Auftrags-ID
     * @return Eine Kopie des
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Ansprechpartner createCopy(Ansprechpartner toCopy, Long auftragIdDest) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Ansprechpartner newAnsprechpartner = new Ansprechpartner();
        PropertyUtils.copyProperties(newAnsprechpartner, toCopy);
        newAnsprechpartner.setId(null);
        newAnsprechpartner.setAuftragId(auftragIdDest);
        // Adresse nicht kopieren, gleiche Adresse wieder nutzen
        return newAnsprechpartner;
    }

    public CCAddress getAddress() {
        return address;
    }

    public void setAddress(CCAddress address) {
        this.address = address;
    }

    public Long getTypeRefId() {
        return typeRefId;
    }

    public void setTypeRefId(Long typeRefId) {
        this.typeRefId = typeRefId;
    }

    public Boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }

    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the prio
     */
    public Integer getPrio() {
        return prio;
    }

    /**
     * @param prio the prio to set
     */
    public void setPrio(Integer prio) {
        this.prio = prio;
    }
}
