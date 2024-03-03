CREATE OR REPLACE FORCE VIEW MIGRATION_WITA10_BS_1 AS
  SELECT
    r.ID                         AS REQUEST_ID,
    r.EXTERNE_AUFTRAGSNUMMER     AS EXTERNE_AUFTRAGSNUMMER,
    r.SENT_AT                    AS SENT_AT,
    r.CDM_VERSION                AS CDM_VERSION,
    r.CB_VORGANG_ID              AS CB_VORGANG_ID,
    GF.VERTRAGSNUMMER            AS WITA_VERTRAGSNUMMER,
    GF.ID                        AS GESCHAEFTSFALL_ID,
    GF.ABGEBENDER_PROVIDER       AS ABGEBENDER_PROVIDER,
    GF.GESCHAEFTSFALLTYP         AS GESCHAEFTSFALLTYP,
    CBV.AUFTRAG_ID               AS AUFTRAG_ID,
    CBV.WITA_AUFTRAG_KLAMMER     AS AUFTRAGS_KLAMMER,
    CBV.VORABSTIMMUNGSID         AS VORABSTIMMUNGSID
  FROM T_Mwf_request r
    INNER JOIN T_MWF_GESCHAEFTSFALL GF ON R.GESCHAEFTSFALL_ID = GF.ID
    INNER JOIN T_CB_VORGANG CBV ON R.CB_VORGANG_ID = CBV.ID
  WHERE
    GF.GESCHAEFTSFALLTYP IN ('VERBUNDLEISTUNG', 'PROVIDERWECHSEL')
    AND
    R.SENT_AT IS NULL
    AND
    R.REQUEST_WURDE_STORNIERT = '0'
    AND
    GF.ABGEBENDER_PROVIDER = 'DTAG'
    AND
    R.CDM_VERSION = 1
    AND
    GF.VERTRAGSNUMMER IS NULL;

GRANT SELECT ON MIGRATION_WITA10_BS_1 TO R_HURRICAN_USER;

CREATE OR REPLACE FORCE VIEW MIGRATION_WITA10_BS_2 AS
  SELECT
    r.ID                         AS REQUEST_ID,
    r.EXTERNE_AUFTRAGSNUMMER     AS EXTERNE_AUFTRAGSNUMMER,
    r.SENT_AT                    AS SENT_AT,
    r.CDM_VERSION                AS CDM_VERSION,
    r.CB_VORGANG_ID              AS CB_VORGANG_ID,
    GF.VERTRAGSNUMMER            AS WITA_VERTRAGSNUMMER,
    GF.ID                        AS GESCHAEFTSFALL_ID,
    GF.ABGEBENDER_PROVIDER       AS ABGEBENDER_PROVIDER,
    GF.GESCHAEFTSFALLTYP         AS GESCHAEFTSFALLTYP,
    CBV.AUFTRAG_ID               AS AUFTRAG_ID,
    CBV.WITA_AUFTRAG_KLAMMER     AS AUFTRAGS_KLAMMER,
    CBV.VORABSTIMMUNGSID         AS VORABSTIMMUNGSID
  FROM T_Mwf_request r
    INNER JOIN T_MWF_GESCHAEFTSFALL GF ON R.GESCHAEFTSFALL_ID = GF.ID
    INNER JOIN T_CB_VORGANG CBV ON R.CB_VORGANG_ID = CBV.ID
  WHERE
    GF.GESCHAEFTSFALLTYP IN ('VERBUNDLEISTUNG', 'PROVIDERWECHSEL')
    AND
    R.SENT_AT IS NULL
    AND
    R.REQUEST_WURDE_STORNIERT = '0'
    AND
    GF.ABGEBENDER_PROVIDER = 'DTAG'
    AND
    GF.VERTRAGSNUMMER IS NOT NULL;

GRANT SELECT ON MIGRATION_WITA10_BS_2 TO R_HURRICAN_USER;
