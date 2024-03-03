
package de.mnet.hurrican.acceptance.customer;

import java.time.*;
import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;

/**
 * Tests the getPublicOrderStatus() of endpoint {@link de.mnet.hurrican.webservice.customerorder.CustomerOrderServiceProvider}
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GetPublicOrderStatus_Test extends AbstractCustomerTestBuilder {

    @CitrusTest
    @Test
    public void GetPublicOrderStatus_Test_UNDEFINIERT() {
        simulatorUseCase(SimulatorUseCase.GetPublicOrderStatus_01);

        variable("customerOrderId", "123456678");
        atlas().sendOrderDetailsCustomerOrder("getPublicOrderStatus");

        variable("expectedPublicOrderStatus", "UNDEFINIERT");
        atlas().receiveGetOrderDetailsCustomerOrderResponse("getPublicOrderStatusResponse");
    }

    @CitrusTest
    @Test
    public void GetPublicOrderStatus_Test_TERMIN_HINWEIS() throws Exception {
        simulatorUseCase(SimulatorUseCase.GetPublicOrderStatus_01);

        final Date tomorrow = DateConverterUtils.asDate(LocalDate.now().plusDays(1));
        final List<Object[]> auftragArr = hurrican().getVerlaufDAO().find(1, "select a,v from Verlauf as v,  AuftragDaten as a "
                + " where v.auftragId = a.auftragId and a.statusId not in (?, ?) and a.statusId < ? "  // active auftrag
                + " and a.gueltigVon <= ? and a.gueltigBis > ?"
                + " and v.akt = ? and v.realisierungstermin >= ?",
                AuftragStatus.ABSAGE, AuftragStatus.STORNO, AuftragStatus.KUENDIGUNG,
                new Date(), new Date(),
                true, tomorrow);

        if (auftragArr != null && !auftragArr.isEmpty()) {
            final AuftragDaten auftragDaten = (AuftragDaten) auftragArr.get(0)[0];
            final Verlauf verlauf = (Verlauf)auftragArr.get(0)[1];
            final Date realisierungsTermin = verlauf.getRealisierungstermin();

            variable("customerOrderId", auftragDaten.getAuftragNoOrig());
            atlas().sendOrderDetailsCustomerOrder("getPublicOrderStatus");

            variable("expectedPublicOrderStatus", "TERMIN_HINWEIS");
            variable("expectedDetailKey", "Realisierungstermin");
            variable("expectedDetailValue", DateConverterUtils.asLocalDate(realisierungsTermin).toString());
            atlas().receiveGetOrderDetailsCustomerOrderResponse("getPublicOrderStatusResponseWithDetail");
        } else {
            logger.warn("GetPublicOrderStatus_Test_TERMIN_HINWEIS: test data not found");
        }

    }

    @CitrusTest
    @Test
    public void GetPublicOrderStatus_Test_SCHALTTERMIN_NEGATIV() throws Exception {
        simulatorUseCase(SimulatorUseCase.GetPublicOrderStatus_01);

        Map<String, Object> params = new HashMap<>();
        params.put("absage", AuftragStatus.ABSAGE);
        params.put("storno", AuftragStatus.STORNO);
        params.put("now", new Date());
        params.put("inBetrieb", AuftragStatus.IN_BETRIEB);
        params.put("meldungstyp1", TerminAnforderungsMeldung.class);


        final List<Object[]> auftragArr = hurrican().getVerlaufDAO().find(1, ""
                + " select a, cbv, m from TerminAnforderungsMeldung as m, WitaCBVorgang as cbv, AuftragDaten as a"
                + " where m.externeAuftragsnummer = cbv.carrierRefNr " // carrierRefNr = business key
                + "   and exists (select v from Verlauf as v where v.auftragId = a.auftragId and v.akt=1 and v.realisierungstermin is not null and v.realisierungstermin < trunc(:now)-1) "
                + "   and cbv.auftragId = a.auftragId "
                + "   and a.statusId not in (:absage, :storno) and a.statusId < :inBetrieb "  // auftraege in realisierung
                + "   and a.gueltigVon <= :now and a.gueltigBis > :now"
                + "   and a.auftragNoOrig in (select add.auftragNoOrig from AuftragDaten add "
                + "                      where add.gueltigVon <= :now and add.gueltigBis > :now "
                + "                      group by add.auftragNoOrig having count(add) = 1) "
                + "   and m.id = (select max(mm.id) from Meldung as mm where mm.externeAuftragsnummer = cbv.carrierRefNr) " // latest wita meldung
                + "   and TYPE(m) in (:meldungstyp1) "
                + " ", params);

        /*
        select * from T_AUFTRAG_DATEN ad
        join T_CB_VORGANG cbv
        on cbv.AUFTRAG_ID = ad.AUFTRAG_ID
        join T_VERLAUF v
        on v.AUFTRAG_ID = ad.AUFTRAG_ID
        where ad.STATUS_ID < 6000
        and ad.STATUS_ID not in (1150, 3400)
        and cbv.CBV_TYPE = 'WITA'
        and v.AKT = '1'
        and v.REALISIERUNGSTERMIN < SYSDATE
        and ad.GUELTIG_VON <= SYSDATE
        and ad.GUELTIG_BIS > SYSDATE
        and exists (select * from T_MWF_MELDUNG m where m.EXT_AUFTRAGS_NR = cbv.CARRIER_REF_NR and m.MELDUNGSTYP = 'TAM')
        and not exists (select * from T_MWF_MELDUNG m where m.EXT_AUFTRAGS_NR = cbv.CARRIER_REF_NR and m.MELDUNGSTYP = 'ENTM');
        */

        if (auftragArr != null && !auftragArr.isEmpty()) {
            final AuftragDaten auftragDaten = (AuftragDaten) auftragArr.get(0)[0];

            variable("customerOrderId", auftragDaten.getAuftragNoOrig());
            atlas().sendOrderDetailsCustomerOrder("getPublicOrderStatus");

            variable("expectedPublicOrderStatus", "SCHALTTERMIN_NEGATIV");
            atlas().receiveGetOrderDetailsCustomerOrderResponse("getPublicOrderStatusResponse");
        } else {
            logger.warn("GetPublicOrderStatus_Test_SCHALTTERMIN_NEGATIV: test data not found");
        }

    }

    @CitrusTest
    @Test
    public void GetPublicOrderStatus_Test_SCHALTTERMIN_NEU() throws Exception {
        simulatorUseCase(SimulatorUseCase.GetPublicOrderStatus_01);

        Map<String, Object> params = new HashMap<>();
        params.put("inBetrieb", AuftragStatus.IN_BETRIEB);
        params.put("now", new Date());

        final List<Object[]> auftragArr = hurrican().getVerlaufDAO().find(1, ""
                + " select a, cbv from AuftragDaten as a, WitaCBVorgang as cbv "
                + " where cbv.auftragId = a.auftragId "
                + "   and a.statusId = :inBetrieb and a.gueltigVon <= :now and a.gueltigBis > :now "
                + "   and a.auftragNoOrig in (select add.auftragNoOrig from AuftragDaten add "
                + "                      where add.gueltigVon <= :now and add.gueltigBis > :now "
                + "                      group by add.auftragNoOrig having count(add) = 1) "
                + "   and not exists (select erlm from ErledigtMeldung as erlm where erlm.externeAuftragsnummer = cbv.carrierRefNr) " // no ERLM
                + "   and exists (select tv from TerminVerschiebung as tv where tv.externeAuftragsnummer = cbv.carrierRefNr) " // TV
                + "", params);

        /*
        entsprechendes SQL:
        select * from T_AUFTRAG_DATEN ad
        join T_CB_VORGANG cbv
        on cbv.AUFTRAG_ID = ad.AUFTRAG_ID
        where ad.STATUS_ID = 6000 -- IN_BETRIEB
        and cbv.CBV_TYPE = 'WITA'
        and not exists (select * from T_MWF_MELDUNG m where m.EXT_AUFTRAGS_NR = cbv.CARRIER_REF_NR and m.MELDUNGSTYP = 'ERLM')
        and exists (select * from T_MWF_REQUEST req where req.EXTERNE_AUFTRAGSNUMMER = cbv.CARRIER_REF_NR and req.REQUESTTYPE = 'TV');
        */

        if (auftragArr != null && !auftragArr.isEmpty()) {
            final AuftragDaten auftragDaten = (AuftragDaten) auftragArr.get(0)[0];

            variable("customerOrderId", auftragDaten.getAuftragNoOrig());
            atlas().sendOrderDetailsCustomerOrder("getPublicOrderStatus");

            variable("expectedPublicOrderStatus", "SCHALTTERMIN_NEU");
            variable("expectedDetailKey", "Realisierungstermin");
            variable("expectedDetailValue", "@ignore@");
            atlas().receiveGetOrderDetailsCustomerOrderResponse("getPublicOrderStatusResponseWithDetail");

        } else {
            logger.warn("GetPublicOrderStatus_Test_SCHALTTERMIN_NEU: test data not found");
        }

    }

    @CitrusTest
    @Test
    public void GetPublicOrderStatus_Test_ANBIETERWECHSEL_POSITIV() throws Exception {
        simulatorUseCase(SimulatorUseCase.GetPublicOrderStatus_01);

        Map<String, Object> params = new HashMap<>();
        params.put("absage", AuftragStatus.ABSAGE);
        params.put("storno", AuftragStatus.STORNO);
        params.put("inBetrieb", AuftragStatus.IN_BETRIEB);
        params.put("now", new Date());
        params.put("complete", WbciGeschaeftsfallStatus.COMPLETE);
        params.put("AKM_TR_EMPFANGEN", WbciRequestStatus.AKM_TR_EMPFANGEN);
        params.put("AKM_TR_VERSENDET", WbciRequestStatus.AKM_TR_VERSENDET);
        params.put("today", DateConverterUtils.asDate(LocalDate.now()));

        final List<Object[]> auftragArr = hurrican().getVerlaufDAO().find(1, ""
                + " select a, g "
                + " from WbciRequest as wr "
                + " left join wr.wbciGeschaeftsfall as g,  AuftragDaten as a "
                + " where a.statusId not in (:absage, :storno) and a.statusId < :inBetrieb "  // auftraege in realisierung
                + " and a.gueltigVon <= :now and a.gueltigBis > :now"
                + " and wr.requestStatus in (:AKM_TR_EMPFANGEN, :AKM_TR_VERSENDET)"
                + " and g.auftragId = a.auftragId and g.status not in (:complete)"
                + " and not exists (select v from Verlauf as v where v.auftragId = a.auftragId and v.realisierungstermin is not null) "
                + " and not exists ( from WitaCBVorgang as wcb where wcb.auftragId = a.auftragId) "
                + "", params);

        if (auftragArr != null && !auftragArr.isEmpty()) {
            final AuftragDaten auftragDaten = (AuftragDaten) auftragArr.get(0)[0];
            final WbciGeschaeftsfall geschaeftsfall = (WbciGeschaeftsfall) auftragArr.get(0)[1];

            variable("customerOrderId", auftragDaten.getAuftragNoOrig());
            atlas().sendOrderDetailsCustomerOrder("getPublicOrderStatus");

            variable("expectedPublicOrderStatus", "ANBIETERWECHSEL_POSITIV");
            variable("expectedDetailKey", "Wechseltermin");
            final LocalDate wechselTermin = geschaeftsfall.getWechseltermin() != null ? geschaeftsfall.getWechseltermin()
                    : geschaeftsfall.getKundenwunschtermin();
            variable("expectedDetailValue", wechselTermin.toString());
            atlas().receiveGetOrderDetailsCustomerOrderResponse("getPublicOrderStatusResponseWithDetail");
        } else {
            logger.warn("GetPublicOrderStatus_Test_ANBIETERWECHSEL_POSITIV: test data not found");
        }

    }

    @CitrusTest
    @Test
    public void GetPublicOrderStatus_Test_ANBIETERWECHSEL_NEGATIV() throws Exception {
        simulatorUseCase(SimulatorUseCase.GetPublicOrderStatus_01);

        Map<String, Object> params = new HashMap<>();
        params.put("absage", AuftragStatus.ABSAGE);
        params.put("storno", AuftragStatus.STORNO);
        params.put("inBetrieb", AuftragStatus.IN_BETRIEB);
        params.put("gueltigVon", new Date());
        params.put("gueltigBis", new Date());
        params.put("complete", WbciGeschaeftsfallStatus.COMPLETE);
        params.put("STORNO_EMPFANGEN", WbciRequestStatus.STORNO_EMPFANGEN);
        params.put("STORNO_VERSENDET", WbciRequestStatus.STORNO_VERSENDET);
        params.put("AKM_TR_EMPFANGEN", WbciRequestStatus.AKM_TR_EMPFANGEN);
        params.put("AKM_TR_VERSENDET", WbciRequestStatus.AKM_TR_VERSENDET);

        final List<Object[]> auftragArr = hurrican().getVerlaufDAO().find(1, ""
                + " select a,g from WbciRequest as wr left join wr.wbciGeschaeftsfall as g,  AuftragDaten as a, Verlauf as v "
                + " where a.statusId not in (:absage, :storno) and a.statusId < :inBetrieb "  // auftraege in realisierung
                + " and a.gueltigVon <= :gueltigVon and a.gueltigBis > :gueltigBis"
                + " and wr.requestStatus in (:STORNO_EMPFANGEN, :STORNO_VERSENDET)"
                + " and a.auftragId not in ( select wr1.wbciGeschaeftsfall.auftragId from WbciRequest as wr1 "
                + "                 where wr1.requestStatus in (:AKM_TR_EMPFANGEN, :AKM_TR_VERSENDET) "
                + "                 ) "
                + " and g.auftragId = a.auftragId and g.status not in (:complete)"
                + " and v.auftragId = a.auftragId and (v.akt = false OR v.realisierungstermin is null)"  // avoid SCHALTTERMIN or TERMIN_HINWEIS
                + " and not exists ( from WitaCBVorgang as wcb where wcb.auftragId = a.auftragId) "
                + "", params);

        if (auftragArr != null && !auftragArr.isEmpty()) {
            final AuftragDaten auftragDaten = (AuftragDaten) auftragArr.get(0)[0];

            variable("customerOrderId", auftragDaten.getAuftragNoOrig());
            atlas().sendOrderDetailsCustomerOrder("getPublicOrderStatus");

            variable("expectedPublicOrderStatus", "ANBIETERWECHSEL_NEGATIV");
            atlas().receiveGetOrderDetailsCustomerOrderResponse("getPublicOrderStatusResponse");
        } else {
            logger.warn("GetPublicOrderStatus_Test_ANBIETERWECHSEL_NEGATIV: test data not found");
        }
    }

    @CitrusTest
    @Test
    public void GetPublicOrderStatus_Test_LEITUNGSBESTELLUNG_NEGATIV() throws Exception {
        simulatorUseCase(SimulatorUseCase.GetPublicOrderStatus_01);

        Map<String, Object> params = new HashMap<>();
        params.put("absage", AuftragStatus.ABSAGE);
        params.put("storno", AuftragStatus.STORNO);
        params.put("kuendigung", AuftragStatus.KUENDIGUNG);
        params.put("now", new Date());
        params.put("inBetrieb", AuftragStatus.IN_BETRIEB);
        params.put("neuschaltung", CBVorgang.TYP_NEU);
        params.put("meldungstyp", Abbruchmeldung.class);

        final List<Object[]> auftragArr = hurrican().getVerlaufDAO().find(1, ""
                        + " select a, m "
                        + " from Meldung as m, WitaCBVorgang as cbv, AuftragDaten as a "
                        + " where m.id = (select max(mm.id) from Meldung as mm where mm.externeAuftragsnummer = cbv.carrierRefNr) " // carrierRefNr = business key
                        + "   and TYPE(m) = :meldungstyp "
                        + "   and cbv.auftragId = a.auftragId "
                        + "   and a.statusId not in (:absage, :storno, :inBetrieb) and a.statusId < :kuendigung "  // active auftrag
                        + "   and a.gueltigVon <= :now and a.gueltigBis > :now"
                        + "   and cbv.typ = :neuschaltung"
                        + "   and not exists (select v from Verlauf as v where v.auftragId = a.auftragId and v.realisierungstermin is not null) "
                        + "   and a.auftragNoOrig in (select add.auftragNoOrig from AuftragDaten add "
                        + "                      where add.gueltigVon <= :now and add.gueltigBis > :now "
                        + "                      group by add.auftragNoOrig having count(add) = 1) "
                , params);

        if (auftragArr != null && !auftragArr.isEmpty()) {
            final AuftragDaten auftragDaten = (AuftragDaten) auftragArr.get(0)[0];

            variable("customerOrderId", auftragDaten.getAuftragNoOrig());
            atlas().sendOrderDetailsCustomerOrder("getPublicOrderStatus");

            variable("expectedPublicOrderStatus", "LEITUNGSBESTELLUNG_NEGATIV");
            atlas().receiveGetOrderDetailsCustomerOrderResponse("getPublicOrderStatusResponse");
        } else {
            logger.warn("GetPublicOrderStatus_Test_LEITUNGSBESTELLUNG_NEGATIV: test data not found");
        }

    }

    @CitrusTest
    @Test
    public void GetPublicOrderStatus_Test_LEITUNGSBESTELLUNG_POSITIV() throws Exception {
        simulatorUseCase(SimulatorUseCase.GetPublicOrderStatus_01);

        Map<String, Object> params = new HashMap<>();
        params.put("absage", AuftragStatus.ABSAGE);
        params.put("storno", AuftragStatus.STORNO);
        params.put("kuendigung", AuftragStatus.KUENDIGUNG);
        params.put("now", new Date());
        params.put("inBetrieb", AuftragStatus.IN_BETRIEB);
        params.put("neuschaltung", CBVorgang.TYP_NEU);
        params.put("meldungstyp", AuftragsBestaetigungsMeldung.class);

        final List<Object[]> auftragArr = hurrican().getVerlaufDAO().find(1, ""
                + " select a, m "
                + " from Meldung as m, WitaCBVorgang as cbv, AuftragDaten as a "
                + " where m.id = (select max(mm.id) from Meldung as mm where mm.externeAuftragsnummer = cbv.carrierRefNr) " // carrierRefNr = business key
                + "   and TYPE(m) = :meldungstyp "
                + "   and cbv.auftragId = a.auftragId "
                + "   and a.statusId not in (:absage, :storno, :inBetrieb) and a.statusId < :kuendigung "  // active auftrag
                + "   and a.gueltigVon <= :now and a.gueltigBis > :now"
                + "   and cbv.typ = :neuschaltung"
                + "   and not exists (select v from Verlauf as v where v.auftragId = a.auftragId and v.realisierungstermin is not null) "
                + "   and a.auftragNoOrig in (select add.auftragNoOrig from AuftragDaten add "
                + "                      where add.gueltigVon <= :now and add.gueltigBis > :now "
                + "                      group by add.auftragNoOrig having count(add) = 1) "
                , params);


        if (auftragArr != null && !auftragArr.isEmpty()) {
            final AuftragDaten auftragDaten = (AuftragDaten) auftragArr.get(0)[0];

            final AuftragsBestaetigungsMeldung abm = (AuftragsBestaetigungsMeldung) auftragArr.get(0)[1];
            final LocalDate verbindlicherLiefertermin = abm.getVerbindlicherLiefertermin();

            variable("customerOrderId", auftragDaten.getAuftragNoOrig());
            atlas().sendOrderDetailsCustomerOrder("getPublicOrderStatus");

            variable("expectedPublicOrderStatus", "LEITUNGSBESTELLUNG_POSITIV");
            variable("expectedDetailKey", "Verbindlicher Liefertermin");
            variable("expectedDetailValue", verbindlicherLiefertermin.toString());
            atlas().receiveGetOrderDetailsCustomerOrderResponse("getPublicOrderStatusResponseWithDetail");
        } else {
            logger.warn("GetPublicOrderStatus_Test_LEITUNGSBESTELLUNG_POSITIV: test data not found");
        }

    }

    @CitrusTest
    @Test
    public void GetPublicOrderStatus_Test_LEITUNGSBESTELLUNG() throws Exception {
        simulatorUseCase(SimulatorUseCase.GetPublicOrderStatus_01);

        Map<String, Object> params = new HashMap<>();
        params.put("absage", AuftragStatus.ABSAGE);
        params.put("storno", AuftragStatus.STORNO);
        params.put("inBetrieb", AuftragStatus.IN_BETRIEB);
        params.put("techRealisierung", AuftragStatus.TECHNISCHE_REALISIERUNG);
        params.put("now", new Date());
        params.put("STATUS_SUBMITTED", CBVorgang.STATUS_SUBMITTED);
        params.put("STATUS_ANSWERED", CBVorgang.STATUS_ANSWERED);
        params.put("meldungstyp1", AuftragsBestaetigungsMeldung.class);
        params.put("meldungstyp2", AbbruchMeldung.class);

        final List<Object[]> auftragArr = hurrican().getVerlaufDAO().find(1, ""
                + " select a, cbv from WitaCBVorgang as cbv, AuftragDaten as a "
                + " where cbv.auftragId = a.auftragId "
                + "   and a.statusId not in (:absage, :storno) and a.statusId < :inBetrieb "  // auftraege in realisierung
                + "   and a.gueltigVon <= :now and a.gueltigBis > :now"
                + "   and a.auftragNoOrig in (select add.auftragNoOrig from AuftragDaten add "
                + "                      where add.gueltigVon <= :now and add.gueltigBis > :now "
                + "                      group by add.auftragNoOrig having count(add) = 1) "
                + "   and cbv.status is not null and cbv.status > :STATUS_SUBMITTED and cbv.status < :STATUS_ANSWERED  "
                + "   and not exists ( from Meldung as mm where mm.externeAuftragsnummer = cbv.carrierRefNr and TYPE(mm) in (:meldungstyp1, :meldungstyp2) ) " // carrierRefNr = business key
                + "", params);

        if (auftragArr != null && !auftragArr.isEmpty()) {
            final AuftragDaten auftragDaten = (AuftragDaten) auftragArr.get(0)[0];

            variable("customerOrderId", auftragDaten.getAuftragNoOrig());
            atlas().sendOrderDetailsCustomerOrder("getPublicOrderStatus");

            variable("expectedPublicOrderStatus", "LEITUNGSBESTELLUNG");
            atlas().receiveGetOrderDetailsCustomerOrderResponse("getPublicOrderStatusResponse");

        } else {
            logger.warn("GetPublicOrderStatus_Test_LEITUNGSBESTELLUNG: test data not found");
        }

    }

}
