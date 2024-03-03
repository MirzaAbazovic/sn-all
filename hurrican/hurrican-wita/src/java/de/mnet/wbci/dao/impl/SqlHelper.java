/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 23.07.2014 
 */
package de.mnet.wbci.dao.impl;

import static de.mnet.wbci.model.CarrierCode.*;
import static de.mnet.wbci.model.MeldungPositionTyp.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;
import static de.mnet.wita.message.MeldungsType.*;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.StringTools;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Helper class for encapsulating large native sql queries
 */
public class SqlHelper {

    public static final String WECHSELTERMIN_PLACEHOLDER = "wt";

    public static String findAutomateableTvErlmsForWitaProcessingSQL(List<Technologie> mnetTechnologies) {
        String sql =
                "SELECT"
                        + "  tverlm.* "
                        + "FROM"
                        // get latest TV_ERLM
                        + "  (SELECT"
                        + "     sub_tverlm.*,"
                        + "     ROW_NUMBER()"
                        + "     OVER (PARTITION BY sub_tverlm.GESCHAEFTSFALL_ID"
                        + "       ORDER BY sub_tverlm.ID DESC) RN"
                        + "   FROM T_WBCI_MELDUNG sub_tverlm"
                        + "     WHERE sub_tverlm.typ = '%s'"
                        + "  ) tverlm"
                        + "  JOIN T_WBCI_GESCHAEFTSFALL gf ON (gf.ID = tverlm.geschaeftsfall_id)"
                        + "  JOIN T_WBCI_REQUEST r ON (gf.id = r.GESCHAEFTSFALL_ID AND r.TYP = '%s')"
                        // get count of already released wita orders
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      cb.VORABSTIMMUNGSID,"
                        + "                      count(cb.ID) ORDER_COUNT"
                        + "                    FROM"
                        + "                      T_CB_VORGANG cb"
                        + "                    WHERE"
                        + "                      CBV_TYPE = 'WITA'"
                        + "                      AND vorabstimmungsid IS NOT NULL"
                        + "                    GROUP BY"
                        + "                      VORABSTIMMUNGSID"
                        + "                    ) wita ON (wita.VORABSTIMMUNGSID = gf.VORABSTIMMUNGSID)"
                        // get count of active storno 
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      GESCHAEFTSFALL_ID,"
                        + "                      count(GESCHAEFTSFALL_ID) COUNT_STORNO"
                        + "                    FROM"
                        + "                      T_WBCI_REQUEST"
                        + "                    WHERE"
                        + "                      TYP <> '%s'" // != VA
                        + "                      AND STATUS IN (%s)"  //open request states for STORNO
                        + "                    GROUP BY"
                        + "                      GESCHAEFTSFALL_ID"
                        + "                  ) storno ON (storno.GESCHAEFTSFALL_ID = gf.ID)"
                        // get count of automating errors
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      GESCHAEFTSFALL_ID,"
                        + "                      count(GESCHAEFTSFALL_ID) COUNT_AT_ERR"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      AUTOMATABLE = '1'"
                        + "                      AND STATUS <> '%s'" // automation error is not COMPLETED
                        + "                    GROUP BY"
                        + "                      GESCHAEFTSFALL_ID"
                        + "                  ) at_err ON (at_err.GESCHAEFTSFALL_ID = gf.ID)"
                        // get count of automation logs of type 'WITA_SEND_TV' for the current ERLM-TV
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      MELDUNG_ID,"
                        + "                      count(MELDUNG_ID) COUNT_AT_MELDUNG"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      TASK_NAME = '%s'"   // TaskName = WITA_SEND_TV
                        + "                    GROUP BY"
                        + "                      MELDUNG_ID"
                        + "                  ) at_meldung ON (at_meldung.MELDUNG_ID = tverlm.ID)"
                        + "WHERE"
                        + "  tverlm.RN = 1"
                        + "  AND r.STATUS = '%s'"  // TV ERLM empfangen
                        + "  AND gf.KLAERFALL = '0'"
                        + "  AND gf.AUTOMATABLE = '1'"
                        + "  AND gf.STATUS IN (%s)"  // ACTIVE, PASSIVE
                        + "  AND gf.MNET_TECHNOLOGIE IN (%s)" // list of wita relevant technologies
                        + "  AND at_err.COUNT_AT_ERR IS NULL" // count of automation errors is null
                        + "  AND at_meldung.COUNT_AT_MELDUNG IS NULL"  // no automation task for the current meldung and type WITA_SEND_TV
                        + "  AND wita.ORDER_COUNT > 0"  // at least one wita order
                        + "  AND storno.COUNT_STORNO IS NULL";   // no open TVs or Stornos
        return String.format(sql,
                ErledigtmeldungTerminverschiebung.DB_MELDUNG_TYP,
                RequestTyp.TV.name(),
                RequestTyp.VA.name(),
                getActiveStornoRequestStatuses(),
                AutomationTask.AutomationStatus.COMPLETED.name(),
                AutomationTask.TaskName.WITA_SEND_TV.name(),
                WbciRequestStatus.TV_ERLM_EMPFANGEN.name(),
                getGeschaeftsfallStatusString(getActiveStatuses()),
                getTechnologieString(mnetTechnologies)
        );
    }


    public static String findAutomateableStrAufhErlmsForWitaProcessingSQL(List<Technologie> mnetTechnologies) {
        String sql =
                "SELECT"
                        + "  strauferlm.* "
                        + "FROM"
                        // get latest STR-AUF ERLM
                        + "  (SELECT"
                        + "     sub_strauferlm.*,"
                        + "     ROW_NUMBER()"
                        + "     OVER (PARTITION BY sub_strauferlm.GESCHAEFTSFALL_ID"
                        + "       ORDER BY sub_strauferlm.ID DESC) RN"
                        + "   FROM T_WBCI_MELDUNG sub_strauferlm"
                        + "     WHERE sub_strauferlm.typ = '%s'"
                        + "  ) strauferlm"
                        + "  JOIN T_WBCI_GESCHAEFTSFALL gf ON (gf.ID = strauferlm.geschaeftsfall_id)"
                        + "  JOIN T_WBCI_REQUEST r ON (gf.id = r.GESCHAEFTSFALL_ID AND r.TYP = '%s')"
                        // get count of already released wita orders
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      cb.VORABSTIMMUNGSID,"
                        + "                      count(cb.ID) ORDER_COUNT"
                        + "                    FROM"
                        + "                      T_CB_VORGANG cb"
                        + "                    WHERE"
                        + "                      CBV_TYPE = 'WITA'"
                        + "                      AND vorabstimmungsid IS NOT NULL"
                        + "                    GROUP BY"
                        + "                      VORABSTIMMUNGSID"
                        + "                    ) wita ON (wita.VORABSTIMMUNGSID = gf.VORABSTIMMUNGSID)"
                        // get count of automating errors
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      GESCHAEFTSFALL_ID,"
                        + "                      count(GESCHAEFTSFALL_ID) COUNT_AT_ERR"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      AUTOMATABLE = '1'"
                        + "                      AND STATUS <> '%s'" // automation error is not COMPLETED
                        + "                    GROUP BY"
                        + "                      GESCHAEFTSFALL_ID"
                        + "                  ) at_err ON (at_err.GESCHAEFTSFALL_ID = gf.ID)"
                        // get count of automation logs of type 'WITA_SEND_STORNO' for the current ERLM-TV
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      MELDUNG_ID,"
                        + "                      count(MELDUNG_ID) COUNT_AT_MELDUNG"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      TASK_NAME = '%s'"   // TaskName = WITA_SEND_STORNO
                        + "                    GROUP BY"
                        + "                      MELDUNG_ID"
                        + "                  ) at_meldung ON (at_meldung.MELDUNG_ID = strauferlm.ID)"
                        + "WHERE"
                        + "  strauferlm.RN = 1"
                        + "  AND gf.KLAERFALL = '0'"
                        + "  AND gf.AUTOMATABLE = '1'"
                        + "  AND gf.AUFNEHMENDEREKP = '%s'"  // M-net = aufnehmender Carrier
                        + "  AND gf.MNET_TECHNOLOGIE IN (%s)" // list of wita relevant technologies
                        + "  AND at_err.COUNT_AT_ERR IS NULL" // count of automation errors is null
                        + "  AND at_meldung.COUNT_AT_MELDUNG IS NULL"  // no automation task for the current meldung and type WITA_SEND_STORNO
                        + "  AND wita.ORDER_COUNT > 0"  // at least one wita order
                        + "  AND r.STATUS = '%s' " // Storno ERLM empfangen
                        + "  AND gf.STATUS IN (%s)";  // ACTIVE, PASSIVE
        return String.format(sql,
                ErledigtmeldungStornoAuf.DB_MELDUNG_TYP,
                RequestTyp.STR_AUFH_AUF.getShortName(),
                AutomationTask.AutomationStatus.COMPLETED.name(),
                AutomationTask.TaskName.WITA_SEND_STORNO.name(),
                CarrierCode.MNET.name(),
                getTechnologieString(mnetTechnologies),
                WbciRequestStatus.STORNO_ERLM_EMPFANGEN.name(),
                getGeschaeftsfallStatusString(getActiveStatuses())
            );
    }


    public static String findAutomateableStrAufhErlmsDonatingProcessingSQL() {
        String sql =
                "SELECT"
                        + "  strauferlm.* "
                        + "FROM"
                        // get latest STR-AUF ERLM
                        + "  (SELECT"
                        + "     sub_strauferlm.*,"
                        + "     ROW_NUMBER()"
                        + "     OVER (PARTITION BY sub_strauferlm.GESCHAEFTSFALL_ID"
                        + "       ORDER BY sub_strauferlm.ID DESC) RN"
                        + "   FROM T_WBCI_MELDUNG sub_strauferlm"
                        + "     WHERE sub_strauferlm.typ = '%s'"
                        + "  ) strauferlm"
                        + "  JOIN T_WBCI_GESCHAEFTSFALL gf ON (gf.ID = strauferlm.geschaeftsfall_id)"
                        + "  JOIN T_WBCI_REQUEST r ON (gf.id = r.GESCHAEFTSFALL_ID AND r.TYP in ('%s', '%s'))"
                        // get count of automating errors
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      GESCHAEFTSFALL_ID,"
                        + "                      count(GESCHAEFTSFALL_ID) COUNT_AT_ERR"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      AUTOMATABLE = '1'"
                        + "                      AND STATUS <> '%s'" // automation error is not COMPLETED
                        + "                    GROUP BY"
                        + "                      GESCHAEFTSFALL_ID"
                        + "                  ) at_err ON (at_err.GESCHAEFTSFALL_ID = gf.ID)"
                        // get count of automation logs of type 'UNDO_AUFTRAG_KUENDIGUNG' for the current ERLM-TV
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      MELDUNG_ID,"
                        + "                      count(MELDUNG_ID) COUNT_AT_MELDUNG"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      TASK_NAME = '%s'"   // TaskName = UNDO_AUFTRAG_KUENDIGUNG
                        + "                    GROUP BY"
                        + "                      MELDUNG_ID"
                        + "                  ) at_meldung ON (at_meldung.MELDUNG_ID = strauferlm.ID)"
                        + "WHERE"
                        + "  strauferlm.RN = 1"
                        + "  AND gf.KLAERFALL = '0'"
                        + "  AND gf.AUTOMATABLE = '1'"
                        + "  AND gf.ABGEBENDEREKP = '%s'"  // M-net = abgebender Carrier
                        + "  AND at_err.COUNT_AT_ERR IS NULL" // count of automation errors is null
                        + "  AND at_meldung.COUNT_AT_MELDUNG IS NULL"  // no automation task for the current meldung and type UNDO_AUFTRAG_KUENDIGUNG
                        + "  AND r.STATUS in ('%s', '%s') "   // Storno ERLM empfangen / Storno ERLM versendet
                        + "  AND gf.STATUS IN (%s)";          // ACTIVE, PASSIVE, COMPLETE
        return String.format(sql,
                ErledigtmeldungStornoAuf.DB_MELDUNG_TYP,
                RequestTyp.STR_AUFH_AUF.getShortName(), RequestTyp.STR_AUFH_ABG.getShortName(),
                AutomationTask.AutomationStatus.COMPLETED.name(),
                AutomationTask.TaskName.UNDO_AUFTRAG_KUENDIGUNG.name(),
                CarrierCode.MNET.name(),
                WbciRequestStatus.STORNO_ERLM_EMPFANGEN.name(),
                WbciRequestStatus.STORNO_ERLM_VERSENDET.name(),
                getGeschaeftsfallStatusString(getActiveAndCompleteStatuses())
        );
    }


    public static String findAutomateableAkmTRsForWitaProcessingSQL(List<Technologie> mnetTechnologies) {
        String sql =
                "SELECT"
                        + "  akmtr.*"
                        + "FROM"
                        //get latest AKM-TR
                        + "  (SELECT"
                        + "     sub_akmtr.*,"
                        + "     ROW_NUMBER()"
                        + "     OVER (PARTITION BY sub_akmtr.GESCHAEFTSFALL_ID"
                        + "       ORDER BY sub_akmtr.ID DESC) RN"
                        + "   FROM T_WBCI_MELDUNG sub_akmtr"
                        + "   WHERE sub_akmtr.typ = '%s'"
                        + "  ) akmtr"
                        + "  JOIN T_WBCI_GESCHAEFTSFALL gf ON (gf.ID = akmtr.geschaeftsfall_id)"
                        + "  JOIN T_WBCI_REQUEST r ON (gf.id = r.GESCHAEFTSFALL_ID AND r.TYP = '%s')"
                        //get count of already released wita orders
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      cb.VORABSTIMMUNGSID,"
                        + "                      count(cb.ID) ORDER_COUNT"
                        + "                    FROM"
                        + "                      T_CB_VORGANG cb"
                        + "                    WHERE"
                        + "                      CBV_TYPE = 'WITA'"
                        + "                      AND vorabstimmungsid IS NOT NULL"
                        + "                    GROUP BY"
                        + "                      VORABSTIMMUNGSID"
                        + "                    ) wita ON (wita.VORABSTIMMUNGSID = gf.VORABSTIMMUNGSID)"
                        //get count of active storno or tvs
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      GESCHAEFTSFALL_ID,"
                        + "                      count(GESCHAEFTSFALL_ID) COUNT_AEN"
                        + "                    FROM"
                        + "                      T_WBCI_REQUEST"
                        + "                    WHERE"
                        + "                      TYP <> '%s'" // != VA
                        + "                      AND STATUS IN (%s)"  //open request states for TV and STORNO
                        + "                    GROUP BY"
                        + "                      GESCHAEFTSFALL_ID"
                        + "                  ) aen ON (aen.GESCHAEFTSFALL_ID = gf.ID)"
                        //get count of automating errors
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      GESCHAEFTSFALL_ID,"
                        + "                      count(GESCHAEFTSFALL_ID) COUNT_AT_ERR"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      AUTOMATABLE = '1'"
                        + "                      AND STATUS <> '%s'" // automation error is not COMPLETED
                        + "                    GROUP BY"
                        + "                      GESCHAEFTSFALL_ID"
                        + "                  ) at_err ON (at_err.GESCHAEFTSFALL_ID = gf.ID)"
                        // get count of automation logs of type 'WITA_SEND_NEUBESTELLUNG' or 'WITA_SEND_ANBIETERWECHSEL' for the current AKM-TR
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      MELDUNG_ID,"
                        + "                      count(MELDUNG_ID) COUNT_AT_MELDUNG"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      TASK_NAME in ('%s', '%s')" // TaskName WITA_SEND_NEUBESTELLUNG || WITA_SEND_ANBIETERWECHSEL
                        + "                    GROUP BY"
                        + "                      MELDUNG_ID"
                        + "                  ) at_meldung ON (at_meldung.MELDUNG_ID = akmtr.ID)"
                        + "WHERE"
                        + "  akmtr.RN = 1"
                        + "  AND r.STATUS = '%s'"  //AKM-TR versendet
                        + "  AND gf.KLAERFALL = '0'"
                        + "  AND gf.AUTOMATABLE = '1'"
                        + "  AND gf.STATUS IN (%s)"  //ACTIVE, PASSIVE
                        + "  AND gf.MNET_TECHNOLOGIE IN (%s)" //list of wita relevant technologies
                        + "  AND at_err.COUNT_AT_ERR IS NULL" //count of automation errors is null
                        + "  AND at_meldung.COUNT_AT_MELDUNG IS NULL" // no automation task for the current akmtr
                        + "  AND wita.ORDER_COUNT IS NULL"  // no open wita orders
                        + "  AND aen.COUNT_AEN IS NULL";   // no open TVs or Stornos
        return String.format(sql,
                MeldungTyp.AKM_TR.toString(),
                RequestTyp.VA.name(),
                RequestTyp.VA.name(),
                getActiveChangeRequestStatuses(),
                AutomationTask.AutomationStatus.COMPLETED.name(),
                AutomationTask.TaskName.WITA_SEND_NEUBESTELLUNG, AutomationTask.TaskName.WITA_SEND_ANBIETERWECHSEL,
                WbciRequestStatus.AKM_TR_VERSENDET.name(),
                getGeschaeftsfallStatusString(getActiveStatuses()),
                getTechnologieString(mnetTechnologies)
        );
    }


    public static String findOverdueAkmTRsSQL(WbciRequestStatus requestStatus, List<Technologie> mnetTechnologies) {
        String sql =
                "SELECT"
                        + "  m.*"
                        + "FROM ("
                        + "       SELECT"
                        + "         sub_m.ID,"
                        + "         ROW_NUMBER()"
                        + "         OVER (PARTITION BY sub_m.GESCHAEFTSFALL_ID"
                        + "           ORDER BY sub_m.ID DESC) RN"
                        + "       FROM T_WBCI_MELDUNG sub_m INNER JOIN T_WBCI_GESCHAEFTSFALL gf ON (sub_m.GESCHAEFTSFALL_ID = gf.id)"
                        + "       WHERE sub_m.TYP = '%s'"
                        + "             AND gf.STATUS in (%s)"
                        + "             AND gf.WECHSELTERMIN <= :" + WECHSELTERMIN_PLACEHOLDER
                        + "             AND gf.MNET_TECHNOLOGIE in (%s)"
                        + "     ) q1,"
                        + "  T_WBCI_MELDUNG m, T_WBCI_REQUEST req "
                        + "WHERE q1.RN = 1"
                        + "      AND m.ID = q1.ID"
                        + "      AND m.geschaeftsfall_id = req.geschaeftsfall_id"
                        + "      AND req.typ = '%s'"
                        + "      AND req.status = '%s'"
                        + "ORDER BY m.id";
        return String.format(sql,
                MeldungTyp.AKM_TR.toString(),
                getGeschaeftsfallStatusString(getActiveStatuses()),
                getTechnologieString(mnetTechnologies),
                RequestTyp.VA.name(),
                requestStatus.name()
        );
    }

    /**
     * Returns an formatted String for the in clause of an SQL statement. If null all Technologies will be returned.
     */
    protected static String getTechnologieString(List<Technologie> technologiesIn) {
        List<Technologie> technologies = technologiesIn;
        if (CollectionUtils.isEmpty(technologies)) {
            technologies = Arrays.asList(Technologie.values());
        }
        return getStringFromEnumList(technologies);
    }

    /**
     * Returns an formatted String for the in clause of an SQL statement. If null all {@link WbciGeschaeftsfallStatus}es
     * will be returned.
     */
    protected static String getGeschaeftsfallStatusString(List<WbciGeschaeftsfallStatus> geschaeftsfallStatusesIn) {
        List<WbciGeschaeftsfallStatus> geschaeftsfallStatuses = geschaeftsfallStatusesIn;
        if (CollectionUtils.isEmpty(geschaeftsfallStatuses)) {
            geschaeftsfallStatuses = Arrays.asList(WbciGeschaeftsfallStatus.values());
        }
        return getStringFromEnumList(geschaeftsfallStatuses);
    }

    private static String getStringFromEnumList(List<? extends Enum> geschaeftsfallStatuses) {
        StringBuilder sb = new StringBuilder();
        for (Enum e : geschaeftsfallStatuses) {
            sb.append("'");
            sb.append(e.name());
            sb.append("',");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Creates the native SQL query for getting the list of preagreements. The supplied {@code IoType} determines
     * whether the query will filter out results where MNET is the donating carrier ({@link IOType#IN}) or the receiving
     * carrier ({@link IOType#OUT}).
     *
     * @param mnetCarrierRole            specifies the {@link CarrierRole} of M-Net.
     * @param singlePreAgreementIdToLoad (optional) specifies a single PreAgreementId to load
     * @return the native sql query
     */
    String getMostRecentPreagreementsQuery(CarrierRole mnetCarrierRole, String singlePreAgreementIdToLoad) {
        // @formatter:off
        final String baseQueryStr = "" +
                "SELECT " +
                "  q2.VORABSTIMMUNGSID, " +
                "  q2.AUFTRAG_ID, " +
                "  q2.BILLING_ORDER__NO, " +
                "  q3.KUNDEN_TYP, " +
                "  q2.TYP GF_TYP, " +
                "  q1.TYP REQ_TYP, " +
                "  q2.ABGEBENDEREKP, " +
                "  q2.AUFNEHMENDEREKP, " +
                "  q2.KUNDENWUNSCHTERMIN, " +
                "  q2.WECHSELTERMIN, " +
                "  q2.STATUS GF_STATUS, " +
                "  q1.STATUS REQ_STATUS, " +
                "  q1.PROCESSED_AT, " +
                "  q1.LAST_MELDUNG_TYPE, " +
                "  q1.LAST_MELDUNG_DATE, " +
                "  q1.LAST_MELDUNG_CODES, " +
                "  q1.ID,                 " +
                "  q2.USER_ID,            " +
                "  q2.USER_NAME,    " +
                "  q2.TEAM_ID, " +
                "  q2.CURRENT_USER_ID,    " +
                "  q2.CURRENT_USER_NAME, " +
                "  q2.MNET_TECHNOLOGIE,   " +
                "  q2.KLAERFALL,          " +
                "  q2.AUTOMATABLE GF_AUTOMATABLE, " +
                "  q2.INTERNAL_STATUS,    " +
                "  q4.ANSWER_DEADLINE,    " +
                "  q4.IS_MNET_DEADLINE,   " +
                "  q5.NIEDERLASSUNG,      " +
                "  at_err.COUNT_AT_ERR    " +
                "FROM " +
                "    ( " +
                "      SELECT " +
                "        ID, " +
                "        GESCHAEFTSFALL_ID, " +
                "        TYP, " +
                "        UPDATED_AT, " +
                "        STATUS, " +
                "        PROCESSED_AT,  " +
                "        LAST_MELDUNG_TYPE,  " +
                "        LAST_MELDUNG_DATE,  " +
                "        LAST_MELDUNG_CODES,  " +
                "        ANSWER_DEADLINE,  " +
                "        IS_MNET_DEADLINE,  " +
                "        ROW_NUMBER() " +
                "        OVER (PARTITION BY GESCHAEFTSFALL_ID " +
                "          ORDER BY UPDATED_AT DESC) rn " +
                "      FROM T_WBCI_REQUEST  " +
                "      ORDER BY UPDATED_AT DESC)  " +
                "    q1 JOIN T_WBCI_GESCHAEFTSFALL q2 ON (q1.GESCHAEFTSFALL_ID = q2.ID and q1.rn = 1) " +
                "    JOIN T_WBCI_PERSON_ODER_FIRMA q3 ON (q2.ENDKUNDE_ID = q3.ID) " +
                "    LEFT OUTER JOIN ( " +
                "        SELECT " +
                "          GESCHAEFTSFALL_ID, " +
                "          IS_MNET_DEADLINE, " +
                "          ANSWER_DEADLINE, " +
                "          ROW_NUMBER() OVER (PARTITION BY GESCHAEFTSFALL_ID ORDER BY ANSWER_DEADLINE ASC) rn " +
                "        FROM " +
                "          T_WBCI_REQUEST " +
                "        WHERE " +
                "          ANSWER_DEADLINE is not NULL) q4 ON (q4.GESCHAEFTSFALL_ID = q2.ID AND  q4.rn = 1) " +
                "    LEFT JOIN ( " +
                "      SELECT " +
                "        atech.AUFTRAG_ID, " +
                "        nl.TEXT as NIEDERLASSUNG " +
                "      FROM " +
                "        T_NIEDERLASSUNG nl " +
                "          INNER JOIN T_AUFTRAG_TECHNIK atech on nl.ID=atech.NIEDERLASSUNG_ID " +
                "      WHERE " +
                "        atech.GUELTIG_BIS>SYSDATE) q5 ON (q2.AUFTRAG_ID = q5.AUFTRAG_ID) " +
                "    LEFT OUTER JOIN ( " + // counts the number of automation errors
                "                      SELECT " +
                "                        GESCHAEFTSFALL_ID, " +
                "                        count(GESCHAEFTSFALL_ID) COUNT_AT_ERR " +
                "                      FROM " +
                "                        T_WBCI_AUTOMATION_TASK " +
                "                      WHERE " +
                "                        STATUS <> '%s' " + // COMPLETED
                "                        AND AUTOMATABLE = '1' " +
                "                      GROUP BY " +
                "                        GESCHAEFTSFALL_ID " +
                "                    ) at_err ON (at_err.GESCHAEFTSFALL_ID = q1.GESCHAEFTSFALL_ID) " +
                " WHERE q2.AUFNEHMENDEREKP %s '%s' " +
                " AND q2.STATUS <> '%s' ";

        final String queryWithPreAgreementId = (StringUtils.isNotBlank(singlePreAgreementIdToLoad))
                ? " AND q2.VORABSTIMMUNGSID = '%s' "
                : null;
        final String orderSql = " ORDER BY q1.UPDATED_AT DESC";

        String aufnehmenderEKP;
        if (CarrierRole.AUFNEHMEND.equals(mnetCarrierRole)) {
            aufnehmenderEKP = "="; // receiving carrier is M-Net
        }
        else {
            aufnehmenderEKP = "<>"; // receiving carrier is not M-Net
        }

        final String querySql = StringTools.join(
                new String[]{baseQueryStr, queryWithPreAgreementId, orderSql}, " ", true);

        List<Object> params = new ArrayList<>();
        params.add(AutomationTask.AutomationStatus.COMPLETED);
        params.add(aufnehmenderEKP);
        params.add(MNET.name());
        params.add(COMPLETE.name());
        if (StringUtils.isNotBlank(singlePreAgreementIdToLoad)) {
            params.add(singlePreAgreementIdToLoad);
        }

        return String.format(querySql, params.toArray());
        // @formatter:off
    }

    /**
     * Creates the native SQL query for getting all preagreements that do NOT have a corresponding Wita ABM-PV.
     *
     * @return the native sql query
     */
    String findPreagreementsWithOverdueAbmPvQuery() {
        final String queryStr = "" +
                " SELECT " +
                "  gf.VORABSTIMMUNGSID, " +
                "  gf.AUFNEHMENDEREKP, " +
                "  gf.ABGEBENDEREKP, " +
                "  gf.WECHSELTERMIN, " +
                "  gf.BILLING_ORDER__NO, " +
                "  gf.AUFTRAG_ID, " +
                "  tr.VERTRAGSNUMMER, " +
                "  CASE WHEN akmpv.ID IS NOT NULL THEN 1 ELSE 0 END AS AKM_PV_RECEIVED, " +
                "  CASE WHEN abbmpv.ID IS NOT NULL THEN 1 ELSE 0 END AS ABBM_PV_RECEIVED " +
                " FROM " +
                "  T_WBCI_GESCHAEFTSFALL gf " +
                // filter after VA's with request state ('AKM_TR_EMPFANGEN' imply that M-NET is donating carrier), gf state
                "  JOIN T_WBCI_REQUEST req" +
                "    ON (req.geschaeftsfall_id = gf.ID " +
                "        AND req.typ = '%s' " +
                "        AND req.status = '%s' " +
                "        AND gf.STATUS in (%s)" +
                "        AND gf.WECHSELTERMIN < :" + SqlHelper.WECHSELTERMIN_PLACEHOLDER +
                "    )" +
                // get technische resource from RUEM-VA where VERTRAGSNUMMER is set
                "  JOIN T_WBCI_MELDUNG m ON m.GESCHAEFTSFALL_ID = gf.ID " +
                "  JOIN T_WBCI_TECHNISCHE_RESSOURCE tr ON (tr.MELDUNG_ID = m.ID AND tr.VERTRAGSNUMMER IS NOT NULL) " +
                "  JOIN ( " +  // multiple AKM-TRs can exist for a VA. This ensures we get the latest AKM-TR
                "         SELECT " +
                "           mld.GESCHAEFTSFALL_ID, " +
                "           mld.PROCESSED_AT, " +
                "           mld.TYP, " +
                "           mld.UEBERNAHME, " +
                "           mld.PORTIERUNGSKENNUNGPKIAUF, " +
                "           mld.SICHERER_HAFEN, " +
                "           ROW_NUMBER() " +
                "           OVER (PARTITION BY mld.GESCHAEFTSFALL_ID " +
                "             ORDER BY mld.PROCESSED_AT DESC) RN " +
                "         FROM " +
                "           T_WBCI_MELDUNG mld " +
                "         WHERE " +
                "           TYP = '%s' " +
                "       ) akm ON (akm.GESCHAEFTSFALL_ID = gf.ID AND akm.RN = 1) " +
                "  LEFT OUTER JOIN ( " +
                "         SELECT" +
                "             temp_akmpv.*," +
                "             ROW_NUMBER()" +
                "             OVER (PARTITION BY temp_akmpv.VERTRAGS_NUMMER" +
                "               ORDER BY temp_akmpv.ID DESC) RN" +
                "           FROM" +
                "             T_MWF_MELDUNG temp_akmpv" +
                "           WHERE" +
                "             MELDUNGSTYP = '%s'" +
                "         ) akmpv ON (akmpv.VERTRAGS_NUMMER = tr.VERTRAGSNUMMER AND akmpv.RN = 1)" +
                "  LEFT OUTER JOIN ( " +
                "         SELECT" +
                "             temp_abbmpv.*," +
                "             ROW_NUMBER()" +
                "             OVER (PARTITION BY temp_abbmpv.VERTRAGS_NUMMER" +
                "               ORDER BY temp_abbmpv.ID DESC) RN" +
                "           FROM" +
                "             T_MWF_MELDUNG temp_abbmpv" +
                "           WHERE" +
                "             MELDUNGSTYP = '%s'" +
                "         ) abbmpv ON (abbmpv.VERTRAGS_NUMMER = tr.VERTRAGSNUMMER AND abbmpv.RN = 1)" +
                " WHERE " +
                "  akm.UEBERNAHME = 1 " +
                "  AND NOT EXISTS( " + // the VERTRAGSNUMMER from the RUEM-VA and ABM-PV is used as the correlation
                "      SELECT" +
                "        witam.*" +
                "      FROM (SELECT" +
                "              temp_witam.*," +
                "              ROW_NUMBER()" +
                "              OVER (PARTITION BY temp_witam.VERTRAGS_NUMMER" +
                "                ORDER BY temp_witam.ID DESC) RN" +
                "            FROM T_MWF_MELDUNG temp_witam" +
                "               WHERE temp_witam.MELDUNGSTYP in ('%s', '%s')) witam" +
                "      WHERE" +
                "        witam.VERTRAGS_NUMMER = tr.VERTRAGSNUMMER" +
                "        AND witam.RN = 1" +
                "        AND witam.MELDUNGSTYP = '%s'" +
                "  ) " +
                " ORDER BY  " +
                "  gf.WECHSELTERMIN, " +
                "  gf.AUFNEHMENDEREKP " +
                "";

        return String.format(queryStr,
                RequestTyp.VA.name(),
                WbciRequestStatus.AKM_TR_EMPFANGEN.name(),
                getGeschaeftsfallStatusString(getActiveStatuses()),
                AKM_TR.getValue(),
                AKM_PV.getValue(),
                ABBM_PV.getValue(),
                ABBM_PV.getValue(),
                ABM_PV.getValue(),
                ABM_PV.getValue());
    }

    /**
     * Creates the native SQL query for getting all preagreements for which the RUEM-VA has been received and that can
     * be automatically processed.
     *
     * @return the native sql query
     */
    String findPreagreementsWithAutomatableRuemVa() {
        final String queryStr = "" +
                "SELECT " +
                "    gf.VORABSTIMMUNGSID " +
                "  FROM " +
                "    T_WBCI_REQUEST r " +
                "    JOIN T_WBCI_GESCHAEFTSFALL gf ON (" +
                "        gf.ID = r.GESCHAEFTSFALL_ID" +
                "        AND gf.KLAERFALL = '0' " +
                "        AND gf.AUTOMATABLE = '1' " +
                "        AND gf.STATUS IN ('%s', '%s') " + // ACTIVE or PASSIVE
                "    ) " +
                "    JOIN T_WBCI_MELDUNG m ON (m.GESCHAEFTSFALL_ID = gf.ID AND m.TYP = '%s') " +  // RUEM-VA
                "    LEFT OUTER JOIN ( " +  // counts all non 'ZWA' or 'NAT' codes
                "                      SELECT " +
                "                        MELDUNG_ID, " +
                "                        count(MELDUNG_ID) COUNT_MC " +
                "                      FROM " +
                "                        T_WBCI_MELDUNG_POSITION " +
                "                      WHERE " +
                "                        MELDUNG_CODE NOT IN ('%s', '%s') " +  // ZWA or NAT
                "                      GROUP BY " +
                "                        MELDUNG_ID " +
                "                    ) mc ON (mc.MELDUNG_ID = m.ID) " +
                "    LEFT OUTER JOIN ( " +  // counts the number of line ids
                "                      SELECT " +
                "                        MELDUNG_ID, " +
                "                        count(MELDUNG_ID) COUNT_LN " +
                "                      FROM " +
                "                        T_WBCI_TECHNISCHE_RESSOURCE " +
                "                      WHERE " +
                "                        LINE_ID IS NOT NULL " +
                "                      GROUP BY " +
                "                        MELDUNG_ID " +
                "                    ) ln ON (ln.MELDUNG_ID = m.ID) " +
                "    LEFT OUTER JOIN ( " + // counts the number of active TVs or STORNOs
                "                      SELECT " +
                "                        GESCHAEFTSFALL_ID, " +
                "                        count(GESCHAEFTSFALL_ID) COUNT_AEN " +
                "                      FROM " +
                "                        T_WBCI_REQUEST " +
                "                      WHERE " +
                "                        TYP <> '%s' " + // VA
                "                        AND STATUS IN (%s) " +
                "                      GROUP BY " +
                "                        GESCHAEFTSFALL_ID " +
                "                    ) aen ON (aen.GESCHAEFTSFALL_ID = gf.ID) " +
                "    LEFT OUTER JOIN ( " + // counts the number of automation errors
                "                      SELECT " +
                "                        GESCHAEFTSFALL_ID, " +
                "                        count(GESCHAEFTSFALL_ID) COUNT_AT_ERR " +
                "                      FROM " +
                "                        T_WBCI_AUTOMATION_TASK " +
                "                      WHERE " +
                "                        STATUS <> '%s' " + // COMPLETED
                "                        AND AUTOMATABLE = '1' " +
                "                      GROUP BY " +
                "                        GESCHAEFTSFALL_ID " +
                "                    ) at_err ON (at_err.GESCHAEFTSFALL_ID = gf.ID) " +
                // get count of automation logs of type 'TAIFUN_NACH_RUEMVA_AKTUALISIEREN' for the current RUEM-VA
                "  LEFT OUTER JOIN (" +
                "                    SELECT" +
                "                      MELDUNG_ID," +
                "                      count(MELDUNG_ID) COUNT_AT_MELDUNG" +
                "                    FROM" +
                "                      T_WBCI_AUTOMATION_TASK" +
                "                    WHERE" +
                "                      TASK_NAME = '%s'" + // TaskName TAIFUN_NACH_RUEMVA_AKTUALISIEREN
                "                    GROUP BY" +
                "                      MELDUNG_ID" +
                "                  ) at_meldung ON (at_meldung.MELDUNG_ID = m.ID)" +
                "  WHERE " +
                "    r.STATUS = '%s' " + // RUEM_VA_EMPFANGEN
                "    AND ln.COUNT_LN IS NULL " +
                "    AND mc.COUNT_MC IS NULL " +
                "    AND aen.COUNT_AEN IS NULL " +
                "    AND at_err.COUNT_AT_ERR IS NULL" + // count of automation errors is null
                "    AND at_meldung.COUNT_AT_MELDUNG IS NULL" +
                "";

        return String.format(queryStr,
                ACTIVE.name(),
                PASSIVE.name(),
                RUEM_VA.getValue(),
                MeldungsCode.ZWA.name(),
                MeldungsCode.NAT.name(),
                RequestTyp.VA.name(),
                getActiveChangeRequestStatuses(),
                AutomationTask.AutomationStatus.COMPLETED,
                AutomationTask.TaskName.TAIFUN_NACH_RUEMVA_AKTUALISIEREN,
                WbciRequestStatus.RUEM_VA_EMPFANGEN.name());
    }

    public static String findAutomateableOutgoingRuemVaForKuendigungSQL() {
        final String queryStr = "" +
                "SELECT " +
                "   gf.* " +
                "FROM T_WBCI_REQUEST r " +
                "JOIN T_WBCI_GESCHAEFTSFALL gf ON (" +
                "       gf.ID = r.GESCHAEFTSFALL_ID" +
                "       AND gf.KLAERFALL = '0' " +
                "       AND gf.AUTOMATABLE = '1' " +
                "       AND gf.STATUS IN ('%s', '%s') " + // ACTIVE or PASSIVE
                "       AND gf.TYP IN ('%s', '%s') " + // VA_KUE_MRN or VA_KUE_ORN
                "       ) " +
                "JOIN T_WBCI_MELDUNG m ON (" +
                "       m.GESCHAEFTSFALL_ID = gf.ID AND m.TYP = '%s') " +  // RUEM-VA
                "LEFT OUTER JOIN ( " + // counts the number of active TVs or STORNOs
                "       SELECT " +
                "           GESCHAEFTSFALL_ID, " +
                "           count(GESCHAEFTSFALL_ID) COUNT_AEN " +
                "       FROM T_WBCI_REQUEST " +
                "       WHERE " +
                "           TYP <> '%s' " + // VA
                "           AND STATUS IN (%s) " +
                //activeChangeRequestStates: z.B. STORNO_VERSENDET, etc. oder TV_VERSENDET, etc.
                "       GROUP BY " +
                "           GESCHAEFTSFALL_ID " +
                "       ) aen ON (aen.GESCHAEFTSFALL_ID = gf.ID) " +
                "LEFT OUTER JOIN ( " + // counts the number of automation errors
                "       SELECT " +
                "           GESCHAEFTSFALL_ID, " +
                "           count(GESCHAEFTSFALL_ID) COUNT_AT_ERR " +
                "       FROM T_WBCI_AUTOMATION_TASK " +
                "       WHERE " +
                "           STATUS <> '%s' " + // COMPLETED
                "           AND AUTOMATABLE = '1' " +
                "       GROUP BY " +
                "           GESCHAEFTSFALL_ID " +
                "       ) at_err ON (at_err.GESCHAEFTSFALL_ID = gf.ID) " +
                // get count of automation logs of type 'AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN' for the current RUEM-VA
                "LEFT OUTER JOIN (" +
                "       SELECT" +
                "           MELDUNG_ID," +
                "           count(MELDUNG_ID) COUNT_AT_MELDUNG" +
                "       FROM T_WBCI_AUTOMATION_TASK" +
                "       WHERE" +
                "           TASK_NAME = '%s'" + // TaskName AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN
                "       GROUP BY" +
                "           MELDUNG_ID" +
                "       ) at_meldung ON (at_meldung.MELDUNG_ID = m.ID) " +
                "WHERE " +
                "   r.STATUS = '%s' " + // RUEM_VA_VERSENDET
                "   AND aen.COUNT_AEN IS NULL " +
                "   AND at_err.COUNT_AT_ERR IS NULL" + // count of automation errors is null
                "   AND at_meldung.COUNT_AT_MELDUNG IS NULL" +
                "";

        return String.format(queryStr,
                ACTIVE.name(), PASSIVE.name(),
                GeschaeftsfallTyp.VA_KUE_MRN, GeschaeftsfallTyp.VA_KUE_ORN,
                RUEM_VA.getValue(),
                RequestTyp.VA.name(),
                getActiveChangeRequestStatuses(),
                AutomationTask.AutomationStatus.COMPLETED,
                AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN,
                WbciRequestStatus.RUEM_VA_VERSENDET.name());
    }



    public static String findAutomateableIncomingAkmTRsForWitaProcessingSQL() {
        String sql =
                "SELECT"
                        + "  akmtr.*"
                        + "FROM"
                        //get latest AKM-TR
                        + "  (SELECT"
                        + "     sub_akmtr.*,"
                        + "     ROW_NUMBER()"
                        + "     OVER (PARTITION BY sub_akmtr.GESCHAEFTSFALL_ID"
                        + "       ORDER BY sub_akmtr.ID DESC) RN"
                        + "   FROM T_WBCI_MELDUNG sub_akmtr"
                        + "   WHERE sub_akmtr.typ = '%s'"
                        + "  ) akmtr"
                        + "  JOIN T_WBCI_GESCHAEFTSFALL gf ON (gf.ID = akmtr.geschaeftsfall_id)"
                        + "  JOIN T_WBCI_REQUEST r ON (gf.id = r.GESCHAEFTSFALL_ID AND r.TYP = '%s')"
                        //get count of already released wita orders
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      cb.VORABSTIMMUNGSID,"
                        + "                      count(cb.ID) ORDER_COUNT"
                        + "                    FROM"
                        + "                      T_CB_VORGANG cb"
                        + "                    WHERE"
                        + "                      CBV_TYPE = 'WITA'"
                        + "                      AND vorabstimmungsid IS NOT NULL"
                        + "                    GROUP BY"
                        + "                      VORABSTIMMUNGSID"
                        + "                    ) wita ON (wita.VORABSTIMMUNGSID = gf.VORABSTIMMUNGSID)"
                        //get count of active storno or tvs
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      GESCHAEFTSFALL_ID,"
                        + "                      count(GESCHAEFTSFALL_ID) COUNT_AEN"
                        + "                    FROM"
                        + "                      T_WBCI_REQUEST"
                        + "                    WHERE"
                        + "                      TYP <> '%s'" // != VA
                        + "                      AND STATUS IN (%s)"  //open request states for TV and STORNO
                        + "                    GROUP BY"
                        + "                      GESCHAEFTSFALL_ID"
                        + "                  ) aen ON (aen.GESCHAEFTSFALL_ID = gf.ID)"
                        //get count of automating errors
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      GESCHAEFTSFALL_ID,"
                        + "                      count(GESCHAEFTSFALL_ID) COUNT_AT_ERR"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      AUTOMATABLE = '1'"
                        + "                      AND STATUS <> '%s'" // automation error is not COMPLETED
                        + "                    GROUP BY"
                        + "                      GESCHAEFTSFALL_ID"
                        + "                  ) at_err ON (at_err.GESCHAEFTSFALL_ID = gf.ID)"
                        // get count of automation logs of type 'WITA_SEND_KUENDIGUNG' for the current AKM-TR
                        + "  LEFT OUTER JOIN ("
                        + "                    SELECT"
                        + "                      MELDUNG_ID,"
                        + "                      count(MELDUNG_ID) COUNT_AT_MELDUNG"
                        + "                    FROM"
                        + "                      T_WBCI_AUTOMATION_TASK"
                        + "                    WHERE"
                        + "                      TASK_NAME in ('%s')" // TaskName WITA_SEND_KUENDIGUNG
                        + "                    GROUP BY"
                        + "                      MELDUNG_ID"
                        + "                  ) at_meldung ON (at_meldung.MELDUNG_ID = akmtr.ID)"
                        + "WHERE"
                        + "  akmtr.RN = 1"
                        + "  AND r.STATUS = '%s'"  //AKM-TR empfangen
                        + "  AND gf.KLAERFALL = '0'"
                        + "  AND gf.AUTOMATABLE = '1'"
                        + "  AND gf.STATUS IN (%s)"  //ACTIVE, PASSIVE
                        + "  AND at_err.COUNT_AT_ERR IS NULL" //count of automation errors is null
                        + "  AND at_meldung.COUNT_AT_MELDUNG IS NULL" // no automation task for the current akmtr
                        + "  AND wita.ORDER_COUNT IS NULL"  // no open wita orders
                        + "  AND aen.COUNT_AEN IS NULL";   // no open TVs or Stornos
        return String.format(sql,
                MeldungTyp.AKM_TR.toString(),
                RequestTyp.VA.name(),
                RequestTyp.VA.name(),
                getActiveChangeRequestStatuses(),
                AutomationTask.AutomationStatus.COMPLETED.name(),
                AutomationTask.TaskName.WITA_SEND_KUENDIGUNG,
                WbciRequestStatus.AKM_TR_EMPFANGEN.name(),
                getGeschaeftsfallStatusString(getActiveStatuses())
        );
    }



    private static String getActiveChangeRequestStatuses() {
        StringBuilder sb = new StringBuilder();
        for (WbciRequestStatus s : WbciRequestStatus.getActiveChangeRequestStatuses()) {
            sb.append("'");
            sb.append(s.name());
            sb.append("',");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static String getActiveStornoRequestStatuses() {
        StringBuilder sb = new StringBuilder();
        for (WbciRequestStatus s : WbciRequestStatus.getActiveStornoRequestStatuses()) {
            sb.append("'");
            sb.append(s.name());
            sb.append("',");
}
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

}
