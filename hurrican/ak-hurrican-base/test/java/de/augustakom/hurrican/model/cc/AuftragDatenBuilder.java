/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 13:51:17
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.DontCreateBuilder;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.common.tools.lang.DateTools;

/**
 * Baut sich automatisch einen Auftrag, wenn kein Builder uebergeben wird.
 *
 *
 */
@SuppressWarnings("unused")
public class AuftragDatenBuilder extends AbstractCCIDModelBuilder<AuftragDatenBuilder, AuftragDaten> implements
        IServiceObject {
    @DontCreateBuilder
    private AuftragBuilder auftragBuilder = null;
    private Long auftragId = null;
    private Long auftragNoOrig = randomLong(100000000);
    private Long prodId = null;
    private ProduktBuilder prodBuilder = null;
    private Long statusId = AuftragStatus.IN_BETRIEB;
    private Long mmzId = null;
    private String bemerkungen = null;
    private Date angebotDatum = new Date();
    private Date auftragDatum = new Date();
    private Date inbetriebnahme = new Date();
    private Date kuendigung = null;
    private Date vorgabeSCV = null;
    private Date vorgabeKunde = null;
    private String bestellNr = randomInt(Integer.MAX_VALUE / 2, Integer.MAX_VALUE).toString();
    private String lbzKunde = null;
    private Integer buendelNr = null;
    private String buendelNrHerkunft = null;
    private String bearbeiter = getUnitTestName();
    private Short telefonbuch = null;
    private Short inverssuche = null;
    private Date gueltigVon = DateTools.createDate(2009, 0, 1);
    private Date gueltigBis = DateTools.getHurricanEndDate();
    private Date freigegeben = null;
    private Boolean statusmeldungen;
    private boolean autoSmsAndMailVersand;
    private String lineId = null;
    private String wholesaleAuftragsId = null;
    private static String getUnitTestName() {
        try {
            throw new RuntimeException();
        }
        catch (RuntimeException e) {
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                if (stackTraceElement.getClassName().endsWith("Test")) {
                    String name = stackTraceElement.getClassName() + ":" + stackTraceElement.getLineNumber();
                    // bearbeiter column ist 100 zeilen lang - vorne abschneiden
                    if (name.length() > 100) {
                        return name.substring(name.length() - 100, name.length());
                    }
                    else {
                        return name;
                    }
                }
            }
            return "UnitTest";
        }
    }

    @Override
    protected void beforeBuild() {
        if (auftragBuilder == null) {
            auftragBuilder = getBuilder(AuftragBuilder.class);
        }
        auftragBuilder.withAuftragDatenBuilder(this);
    }

    public AuftragDatenBuilder withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public AuftragDatenBuilder withBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
        return this;
    }

    public AuftragDatenBuilder withRandomAuftragId() {
        this.auftragId = getLongId();
        return this;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public AuftragDatenBuilder withAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
        return this;
    }

    public ProduktBuilder getProdBuilder() {
        return prodBuilder;
    }

    public AuftragDatenBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        if (auftragBuilder.getAuftragDatenBuilder() != this) {
            auftragBuilder.withAuftragDatenBuilder(this);
        }
        return this;
    }

    public AuftragDatenBuilder withProdBuilder(ProduktBuilder prodBuilder) {
        this.prodBuilder = prodBuilder;
        return this;
    }

    public AuftragDatenBuilder withProdId(Long prodId) {
        this.prodId = prodId;
        this.prodBuilder = null;
        return this;
    }

    public AuftragDatenBuilder withStatusId(Long statusId) {
        this.statusId = statusId;
        return this;
    }

    public AuftragDatenBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public AuftragDatenBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public AuftragDatenBuilder withBuendelNr(Integer bundelNr) {
        this.buendelNr = bundelNr;
        return this;
    }

    public AuftragDatenBuilder withBuendelNrHerkunft(String buendelNrHerkunft) {
        this.buendelNrHerkunft = buendelNrHerkunft;
        return this;
    }

    public AuftragDatenBuilder withLbzKunde(String lbzKunde) {
        this.lbzKunde = lbzKunde;
        return this;
    }

    public AuftragDatenBuilder withKuendigung(Date kuendigung) {
        this.kuendigung = kuendigung;
        return this;
    }

    public AuftragDatenBuilder withVorgabeSCV(Date vorgabeSCV) {
        this.vorgabeSCV = vorgabeSCV;
        return this;
    }

    public AuftragDatenBuilder withInbetriebnahme(Date inbetriebnahme) {
        this.inbetriebnahme = inbetriebnahme;
        return this;
    }

    public AuftragDatenBuilder withFreigegeben(Date freigegeben) {
        this.freigegeben = freigegeben;
        return this;
    }

    public AuftragDatenBuilder withStatusmeldungen(Boolean statusmeldungen) {
        this.statusmeldungen = statusmeldungen;
        return this;
    }

    public AuftragDatenBuilder withAutoSmsVersand(boolean autoSmsVersand) {
        this.autoSmsAndMailVersand = autoSmsVersand;
        return this;
    }
    public AuftragDatenBuilder withLineId(String lineId){
        this.lineId = lineId;
        return this;
    }
    public AuftragDatenBuilder withWholesaleAuftragsId(String wsAufragsId){
        this.wholesaleAuftragsId = wsAufragsId;
        return this;
    }
}
