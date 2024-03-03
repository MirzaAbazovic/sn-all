/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2015
 */
package de.mnet.migration.hurrican.talbestellungenffm;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.migration.common.result.SourceIdList;
import de.mnet.migration.common.result.SourceTargetId;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;
import de.mnet.migration.common.util.Messages;
import de.mnet.migration.hurrican.common.HurricanMigrationStarter;
import de.mnet.migration.hurrican.common.HurricanTransformator;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.auftrag.Kundenwunschtermin;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * Migration timeslots & ffm
 * {@see HUR-21200} {@see ANF-404.04}
 *
 */
public class TALBestellungenFFMTransformer extends HurricanTransformator<TALBestellungenFFM> {

    private static final Logger LOGGER = Logger.getLogger(TALBestellungenFFMTransformer.class);

    public static class MigMessages extends HurricanMessages {
        Messages.Message SUCCESS = new Messages.Message(
                TransformationStatus.OK, 0x01L,
                "Migration der Zeitfenster erfolgreich durchgefuehrt: %s");
        Messages.Message ERROR_FINDER = new Messages.Message(
                TransformationStatus.ERROR, 0x02L,
                "Aggregation der Daten fehlgeschlagen: %s");
        Messages.Message ERROR_STORE = new Messages.Message(
                TransformationStatus.ERROR, 0x04L,
                "Aktualisieren der Daten fehlgeschlagen: %s");
        Messages.Message ERROR_UNEXPECTED = new Messages.Message(
                TransformationStatus.ERROR, 0x08L,
                "Unspezifizierter Fehler: %s");
        Messages.Message INFO = new Messages.Message(
                TransformationStatus.INFO, 0x10L,
                "Bitte Hinweis beachten: %s");
    }

    static MigMessages messages = new MigMessages();

    @Resource(name = "de.augustakom.hurrican.service.cc.ffm.FFMService")
    private FFMService ffmService;

    @Resource(name = "de.mnet.wita.service.impl.WitaDataService")
    private WitaDataService witaDataService;

    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;

    @Resource(name = "cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Inject
    private TALBestellungenMigIdHolder migIdHolder;
    private static Date gueltigAb;

    public static void main(String[] args) {
        final LocalDate localDate = DateCalculationHelper.addWorkingDays(LocalDate.now(), 2);
        gueltigAb = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


        final HurricanMigrationStarter migrationStarter = new HurricanMigrationStarter();
        migrationStarter.startMigration(args, "de/mnet/migration/hurrican/talbestellungenffm/tal-bestellungen-ffm-migration.xml").finish();
        System.exit(0); // Close VM, destroy atlas connection
    }

    @Override
    public TransformationResult transform(TALBestellungenFFM row) {
        final LocalDate abmLiefertermin = DateConverterUtils.asLocalDate(row.abmLiefertermin);

        String message = String.format("Migriere Zeitfenster fuer die Externe Auftragsnummer %s (Auftrag-ID %s, "
                + "ABM Meldung-ID %s, Verb. Liefertermin %s, CBVorgang ID %s, Carrierbestellung ID %s)",
                row.externeAuftragsNr, row.auftragId, row.abmId, abmLiefertermin.toString(),
                row.cbvId, row.cbId);
        messages.prepare(message);
        LOGGER.info(message);

        SourceIdList sourceIdList = new SourceIdList(Arrays.asList(new SourceTargetId("Externe Auftragsnummer",
                row.externeAuftragsNr), new SourceTargetId("ABM-ID", row.abmId), new SourceTargetId("Auftrag-ID",
                row.auftragId), new SourceTargetId("Verb. Liefertermin", abmLiefertermin.toString())));

        return messages.evaluate(sourceIdList, execute(row));
    }

    TargetIdList execute(TALBestellungenFFM row) {
        Optional<FfmMigrationResult> ffmMigrationResult = Optional.empty();
        try {
            MnetWitaRequest mnetWitaRequest = buildMnetWitaRequest(row.requestId, row.kwtZeitfenster);
            AuftragsBestaetigungsMeldung abm = buildAuftragsBestaetigungsMeldung(row.meldungsCode);

            TalRealisierungsZeitfenster mappedTimeSlot = witaDataService.transformWitaZeitfenster(mnetWitaRequest, abm);
            updateCbVorgang(row.cbvId, mappedTimeSlot);
            if (row.cbId != null) {
                updateCarrierbestellung(row.cbId, mappedTimeSlot);
            }

            if (DateTools.isDateAfterOrEqual(row.abmLiefertermin, gueltigAb)) {
                ffmMigrationResult = migrateFFM(row); // CBVs muessen keine CB haben, aber immer einen techn. Auftrag
                final String msg = String.format("Migration mit neuem Zeitfenster %s durchgefuehrt",
                        mappedTimeSlot.getDisplayText());
                messages.SUCCESS.add(msg);
                LOGGER.info(msg);
            }
            else {
                final LocalDate abmLiefertermin = DateConverterUtils.asLocalDate(row.abmLiefertermin);
                final String msg = String.format("FFM Migration fuer die Externe Auftragsnummer %s nicht duchgefuehrt, "
                                + "da verb. Liefertermin %s mindestens 2 Werktage voraus liegen muss!",
                        abmLiefertermin.toString(), row.externeAuftragsNr);
                messages.INFO.add(msg);
                LOGGER.info(msg);
            }
        }
        catch (FindException e) {
            messages.ERROR_FINDER.add(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        catch (StoreException e) {
            messages.ERROR_STORE.add(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        catch (Exception e) {
            messages.ERROR_UNEXPECTED.add(e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        return buildTargetIdList(row, ffmMigrationResult);
    }

    private TargetIdList buildTargetIdList(TALBestellungenFFM row, Optional<FfmMigrationResult> ffmMigrationResult) {
        List<SourceTargetId> ids = new ArrayList<>();
        ids.add(new SourceTargetId("CBVorgang", row.cbvId));
        ids.add(new SourceTargetId("Carrierbestellung", row.cbId));
        ffmMigrationResult.ifPresent(ffm -> {
            ids.add(new SourceTargetId("Bauauftrag", ffm.bauauftrag.getId()));
            ids.add(new SourceTargetId("WorkforceOrderId", ffm.workforceOrderId));
        });
        return new TargetIdList(ids);
    }


    MnetWitaRequest buildMnetWitaRequest(Long requestId, String kwtZeitfenster) throws FindException {
        try {
            MnetWitaRequest witaRequest = new MnetWitaRequest();
            Geschaeftsfall geschaeftsfall = new Geschaeftsfall() {
                @Override
                public GeschaeftsfallTyp getGeschaeftsfallTyp() {
                    return null;
                }
            };
            Kundenwunschtermin kwt = new Kundenwunschtermin();
            kwt.setZeitfenster(Kundenwunschtermin.Zeitfenster.valueOf(kwtZeitfenster));
            geschaeftsfall.setKundenwunschtermin(kwt);
            witaRequest.setGeschaeftsfall(geschaeftsfall);
            return witaRequest;
        }
        catch (Exception e) {
            throw new FindException(String.format("Der MnetWitaRequest mit der ID %s konnte nicht ermittelt werden!",
                    requestId));
        }
    }

    AuftragsBestaetigungsMeldung buildAuftragsBestaetigungsMeldung(String meldungsCode) {
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldung();
        if (meldungsCode != null) {
            MeldungsPositionWithAnsprechpartner meldungsPosition = new MeldungsPositionWithAnsprechpartner();
            meldungsPosition.setMeldungsCode(meldungsCode);
            abm.addMeldungsPosition(meldungsPosition);
        }
        return abm;
    }

    private void updateCbVorgang(Long cbvId, TalRealisierungsZeitfenster mappedTimeSlot) throws StoreException {
        try {
            SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(
                    "UPDATE T_CB_VORGANG SET TAL_REAL_TIMESLOT = ? "
                            + "WHERE ID = ?");
            //Parameter
            //noinspection JpaQueryApiInspection
            sqlQuery.setParameter(0, mappedTimeSlot.name());
            sqlQuery.setParameter(1, cbvId);

            sqlQuery.executeUpdate();
        }
        catch (Exception e) {
            throw new StoreException(String.format("Die CBVorgang mit der ID %s konnte nicht gespeichert werden! "
                    + "Grund: %s", cbvId, e.getMessage()));
        }
    }

    private void updateCarrierbestellung(Long cbId, TalRealisierungsZeitfenster mappedTimeSlot) throws StoreException {
        try {
            SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(
                    "UPDATE T_CARRIERBESTELLUNG SET TAL_REAL_TIMESLOT = ? "
                            + "WHERE CB_ID = ?");
            //Parameter
            //noinspection JpaQueryApiInspection
            sqlQuery.setParameter(0, mappedTimeSlot.name());
            sqlQuery.setParameter(1, cbId);

            sqlQuery.executeUpdate();
        }
        catch (Exception e) {
            throw new StoreException(String.format("Die Carrierbestellung mit der ID %s konnte nicht gespeichert werden! "
                    + "Grund: %s", cbId, e.getMessage()));
        }
    }

    protected Optional<FfmMigrationResult> migrateFFM(TALBestellungenFFM row) throws FindException, StoreException {
        Optional<FfmMigrationResult> ffmMigrationResult = Optional.empty();
        if (row.verlaufId != null && hasOpenFFMAbteilung(row)) {
            final Verlauf bauauftrag = baService.findVerlauf(row.verlaufId);
            ffmMigrationResult = doFfmMigration(bauauftrag);
            if (ffmMigrationResult.isPresent()) {
                bauauftrag.setWorkforceOrderId(ffmMigrationResult.get().workforceOrderId);
                baService.saveVerlauf(bauauftrag);
            }
        }
        return ffmMigrationResult;
    }

    protected boolean hasOpenFFMAbteilung(TALBestellungenFFM row) {
        return row.ffmVerlaufId != null && row.ffmDatumErledigt == null;
    }

    private Optional<FfmMigrationResult> doFfmMigration(Verlauf bauauftrag) {
        Optional<FfmMigrationResult> ffmMigrationResult = Optional.empty();
        // Der Verlaufs Check sollte eigentlich nicht mehr notwendig sein, da die Migrations View dahingehend optimiert
        // sein sollte.
        if (migIdHolder.checkVerlaufIdAndMigrate(bauauftrag.getId())) {
            LOGGER.info(String.format("Lösche WorkforceOrder für Bauauftrag mit der ID %s", bauauftrag.getId()));
            ffmService.deleteOrder(bauauftrag);

            final FfmMigrationResult result = new FfmMigrationResult();
            result.bauauftrag = bauauftrag;
            result.workforceOrderId = ffmService.createAndSendOrder(bauauftrag);
            LOGGER.info(String.format("Neue WorkforceOrder mit der ID %s für den Bauauftrag mit der ID %s erstellt",
                    result.workforceOrderId, bauauftrag.getId()));
            ffmMigrationResult = Optional.of(result);
        }
        else {
            // verlauf could be already migrated with another cbid
            LOGGER.warn(String.format("Verlauf [%d] of Auftrag [%d] is already migrated with another cvb",
                    bauauftrag.getId(), bauauftrag.getAuftragId()));
        }
        return ffmMigrationResult;
    }

    protected class FfmMigrationResult {
        public Verlauf bauauftrag;
        public String workforceOrderId;
    }
}
