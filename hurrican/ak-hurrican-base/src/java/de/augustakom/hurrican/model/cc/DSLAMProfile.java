/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2007 16:17:27
 */
package de.augustakom.hurrican.model.cc;

import static com.google.common.collect.Lists.*;

import java.util.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;


/**
 * Modell definiert DSLAM-Profile.
 *
 *
 */
public class DSLAMProfile extends AbstractCCIDModel {

    /**
     * Konstante fuer den ADSL-Typ 'ADSL1'
     */
    public static final String ADSL_TYPE_1 = "ADSL1";

    public static final String L2POWER_NA = "noL2 Power Save Mode (n/a)";
    public static final String L2POWER_ENABLED = "L2 Power Save Mode enabled";
    public static final String L2POWER_DISABLED = "noL2 Power Save Mode";

    /* UETV Typen */
    public static final String UETV_ADSL2PLUS = "ADSL2+";
    public static final String UETV_H13 = "H13";
    public static final String UETV_ADSL = "ADSL";
    public static final String UETV_H04 = "H04";

    /**
     * Alle ADSL2+ UETVs.
     */
    public static final List<String> UETV_4_ADSL2PLUS = Arrays.asList(UETV_ADSL2PLUS, UETV_H13);

    /**
     * Liste enthaelt Pairs, die zu einem Downstream (First) den maximalen Upstream (Second) darstellen. Es sind dabei
     * die Long-Values der zugehoerigen techn. Leistungen ( {@link TechLeistung} ) konfiguriert.
     */
    @SuppressWarnings("unchecked")
    public static final List<Bandwidth> MAX_UPSTREAM_4_DOWNSTREAM = Arrays.asList(
            Bandwidth.create(18000, 1000),
            Bandwidth.create(6000, 512)
    );

    public static final String NAME = "name";
    private String name = null;
    private String bemerkung = null;
    public static final String DOWNSTREAM = "bandwidth.downstream";
    public static final String UPSTREAM = "bandwidth.upstream";
    @Nonnull
    private Bandwidth bandwidth;
    @CheckForNull
    private Bandwidth bandwidthNetto = null;
    public static final String FASTPATH = "fastpath";
    private Boolean fastpath = null;
    public static final String UETV = "uetv";
    private String uetv = null;
    private Long downstreamTechLs = null;
    private Long upstreamTechLs = null;
    private Long fastpathTechLs = null;
    public static final String GUELTIG = "gueltig";
    private Boolean gueltig;
    public static final String TM_DOWN = "tmDown";
    private Integer tmDown = null;
    public static final String TM_UP = "tmUp";
    private Integer tmUp = null;
    public static final String L2POWERSAFE_ENABLED = "l2PowersafeEnabled";
    private Boolean l2PowersafeEnabled = null;
    public static final String FORCE_ADSL1 = "forceADSL1";
    private Boolean forceADSL1 = null;
    public static final String BAUGRUPPEN_TYP_ID = "baugruppenTypId";
    private Long baugruppenTypId = null;
    public static final String ENABLED_FOR_AUTOCHANGE = "enabledForAutochange";
    private Boolean enabledForAutochange = null;


    public static Integer getMaxUpstreamForDownstream(Integer downstream) {
        for (Bandwidth bandwidth : MAX_UPSTREAM_4_DOWNSTREAM) {
            if (bandwidth.getDownstream().equals(downstream)) {
                return bandwidth.getUpstream();
            }
        }
        return null;
    }


    public static final Comparator<DSLAMProfile> DSLAMPROFILE_COMPARATOR = new Comparator<DSLAMProfile>() {
        @Override
        public int compare(DSLAMProfile arg0, DSLAMProfile arg1) {
            return arg0.bandwidth.compareTo(arg1.bandwidth);
        }
    };


    /**
     * Ueberprueft, ob es sich bei dem DSLAM-Profil um ein ADSL 1 Profil handelt.
     *
     * @return
     */
    public boolean isADSL1Profile() {
        return getName().endsWith(ADSL_TYPE_1) || ((forceADSL1 != null) && forceADSL1);
    }

    /**
     * Gibt zu den Eingabeparameter eine Liste von unterstützten UETVs zurück.
     *
     * @param isADSL1Profile <code>true</code> wenn ADSL1 Profile gesucht werden, siehe {@link #isADSL1Profile()}
     * @param uetv           ein UETV
     * @return eine evtl. leere Liste von UETVs
     */
    public static List<String> getMatchingUETV(boolean isADSL1Profile, String uetv) {
        List<String> uetvs = newArrayList();
        // die Prüfung auf ADSL1 muss zuerst erfolgen, da dafür evtl. auch ADSL2+ UETVs verwendet werden
        if (isADSL1Profile) {
            if (uetv != null) {
                uetvs.add(uetv);
            }
        }
        else if (DSLAMProfile.UETV_4_ADSL2PLUS.contains(uetv)) {
            // Bei DSL18000 ist H13 identisch zu ADSL2+ zu sehen (siehe HUR-8472)
            uetvs.addAll(DSLAMProfile.UETV_4_ADSL2PLUS);
        }
        return uetvs;
    }

    /**
     * Filtert aus der Liste <code>profiles</code> die DSLAM-Profile heraus, die den Parametern entsprechen. <br> Im
     * Idealfall liefert die Methode eine Liste mit einem Eintrag. Sollte die Methode eine Liste mit mehr als einem
     * Eintrag liefern, muss sich der Caller um eine weitere Selektion des Profils kuemmern.
     *
     * @param profiles           Liste der zur Verfuegung stehenden DSLAM-Profile (vorgefiltert ueber Produkt?!)
     * @param downstreamTechLsId ID der Downstream-Leistung
     * @param upstreamTechLsId   ID der Upstream-Leistung
     * @param fastpathTechLsId   ID der Fastpath-Leistung
     * @param uetv               Uebertragungsverfahren der Auftragsphysik
     * @return Liste mit den DSLAM-Profilen, die den Parametern entsprechen.
     * @throws IllegalArgumentException wenn keine DSLAM-Profile fuer die Filterung uebergeben wurden.
     *
     */
    public static List<DSLAMProfile> filterProfiles(List<DSLAMProfile> profiles, final Long downstreamTechLsId,
            final Long upstreamTechLsId, final Long fastpathTechLsId, Uebertragungsverfahren uetv) throws IllegalArgumentException {
        List<DSLAMProfile> filtered = new ArrayList<DSLAMProfile>(profiles);
        if (CollectionTools.isNotEmpty(filtered)) {
            if (downstreamTechLsId != null) {
                // DSLAM-Profile nach Downstream-Leistung filtern
                CollectionUtils.filter(filtered, new Predicate() {
                    @Override
                    public boolean evaluate(Object obj) {
                        return NumberTools.equal(downstreamTechLsId, ((DSLAMProfile) obj).getDownstreamTechLs());
                    }
                });
            }

            if (upstreamTechLsId != null) {
                // DSLAM-Profile nach Upstream-Leistung filtern
                CollectionUtils.filter(filtered, new Predicate() {
                    @Override
                    public boolean evaluate(Object obj) {
                        return NumberTools.equal(upstreamTechLsId, ((DSLAMProfile) obj).getUpstreamTechLs());
                    }
                });
            }

            // DSLAM-Profile nach Fastpath-Leistung filtern
            CollectionUtils.filter(filtered, new Predicate() {
                @Override
                public boolean evaluate(Object obj) {
                    if (fastpathTechLsId == null) {
                        DSLAMProfile profile = (DSLAMProfile) obj;
                        if (profile.getName().startsWith("AK")) {
                            // alte AugustaKom-Profile haben IMMER Fastpath!
                            // dieser Hack ist fuer die alte Produktstruktur notwendig, bei der keine
                            // techn. Leistungen zugeordnet sind!
                            return true;
                        }
                        else {
                            return !BooleanTools.nullToFalse(((DSLAMProfile) obj).getFastpath());
                        }
                    }
                    else {
                        return NumberTools.equal(fastpathTechLsId, ((DSLAMProfile) obj).getFastpathTechLs());
                    }
                }
            });

            if (filtered.size() > 1) {
                List<DSLAMProfile> backup = new ArrayList<DSLAMProfile>(filtered);

                // DSLAM-Profile nach Uebertragungsverfahren filtern
                // (falls kein UETV vorhanden, vorsichtshalber H13 verwenden, da hoeherwertig)
                final Uebertragungsverfahren uetv2use = (uetv == null) ? Uebertragungsverfahren.H13 : uetv;
                CollectionUtils.filter(filtered, new Predicate() {
                    @Override
                    public boolean evaluate(Object obj) {
                        String reference = uetv2use.name();
                        String check = StringUtils.trim(((DSLAMProfile) obj).getUetv());
                        return StringUtils.equals(reference, check);
                    }
                });

                if (CollectionTools.isEmpty(filtered)) {
                    // Fallback auf urspruenglich gefilterte Liste, falls durch UETV-Filterung kein DSLAM-Profil mehr uebrig bleibt
                    return backup;
                }
            }

            return filtered;
        }
        else {
            throw new IllegalArgumentException("Es wurden keine DSLAM-Profile angegeben!");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Bandwidth getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(@Nonnull Bandwidth bandwidth) {
        this.bandwidth = bandwidth;
    }

    @NotNull
    public Boolean getFastpath() {
        return fastpath;
    }

    public void setFastpath(Boolean fastpath) {
        this.fastpath = fastpath;
    }

    public Long getDownstreamTechLs() {
        return downstreamTechLs;
    }

    public void setDownstreamTechLs(Long downstreamTechLs) {
        this.downstreamTechLs = downstreamTechLs;
    }

    public Long getUpstreamTechLs() {
        return upstreamTechLs;
    }

    public void setUpstreamTechLs(Long upstreamTechLs) {
        this.upstreamTechLs = upstreamTechLs;
    }

    public Long getFastpathTechLs() {
        return fastpathTechLs;
    }

    public void setFastpathTechLs(Long fastpathTechLs) {
        this.fastpathTechLs = fastpathTechLs;
    }

    public String getUetv() {
        return uetv;
    }

    public void setUetv(String uetv) {
        this.uetv = uetv;
    }

    public Boolean getGueltig() {
        return gueltig;
    }

    public void setGueltig(Boolean gueltig) {
        this.gueltig = gueltig;
    }

    public Integer getTmDown() {
        return tmDown;
    }

    public void setTmDown(Integer tmDown) {
        this.tmDown = tmDown;
    }

    public Integer getTmUp() {
        return tmUp;
    }

    public void setTmUp(Integer tmUp) {
        this.tmUp = tmUp;
    }

    public Boolean getL2PowersafeEnabled() {
        return l2PowersafeEnabled;
    }

    public void setL2PowersafeEnabled(Boolean l2PowersafeEnabled) {
        this.l2PowersafeEnabled = l2PowersafeEnabled;
    }

    public Boolean getForceADSL1() {
        return forceADSL1;
    }

    public void setForceADSL1(Boolean forceADSL1) {
        this.forceADSL1 = forceADSL1;
    }

    public Long getBaugruppenTypId() {
        return baugruppenTypId;
    }

    public void setBaugruppenTypId(Long baugruppenTypId) {
        this.baugruppenTypId = baugruppenTypId;
    }

    @CheckForNull
    public Bandwidth getBandwidthNetto() {
        return bandwidthNetto;
    }

    public void setBandwidthNetto(@CheckForNull Bandwidth bandwidthNetto) {
        this.bandwidthNetto = bandwidthNetto;
    }

    public Boolean getEnabledForAutochange() {
        return enabledForAutochange;
    }

    public void setEnabledForAutochange(Boolean enabledForAutochange) {
        this.enabledForAutochange = enabledForAutochange;
    }

}


