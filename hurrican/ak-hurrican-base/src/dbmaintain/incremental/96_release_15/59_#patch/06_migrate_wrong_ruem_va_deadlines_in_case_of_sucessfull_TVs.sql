-- update RUEM_VA_EMPFANGEN deadlines
UPDATE (SELECT
          r.ANSWER_DEADLINE AS deadline,
          g.wechseltermin   AS wt
        FROM T_WBCI_REQUEST r JOIN T_WBCI_GESCHAEFTSFALL g ON (r.geschaeftsfall_id = g.ID)
        WHERE r.TYP LIKE 'VA'
              AND r.status = 'RUEM_VA_EMPFANGEN'
              AND r.answer_deadline IS NOT NULL
              AND g.wechseltermin IS NOT NULL
              AND
              g.vorabstimmungsid IN
              (SELECT
                 q1g.vorabstimmungsid
               FROM T_WBCI_REQUEST q1r JOIN T_WBCI_GESCHAEFTSFALL q1g ON (q1r.geschaeftsfall_id = q1g.id)
               WHERE q1g.status <> 'COMPLETE'
                     AND q1g.typ <> 'VA_RRNP'
                     AND q1r.TYP LIKE 'TV'
                     AND q1r.status IN ('TV_ERLM_VERSENDET', 'TV_ERLM_EMPFANGEN')
               GROUP BY q1g.vorabstimmungsid)) up
SET up.deadline = day_in_working_days(wt, (SELECT
                                             af.frist_in_stunden / 24
                                           FROM T_WBCI_ANTWORTFRIST af
                                           WHERE af.STATUS = 'RUEM_VA_EMPFANGEN') * -1);

-- update RUEM_VA_VERSENDET deadlines
UPDATE (SELECT
          r.ANSWER_DEADLINE AS deadline,
          g.wechseltermin   AS wt
        FROM T_WBCI_REQUEST r JOIN T_WBCI_GESCHAEFTSFALL g ON (r.geschaeftsfall_id = g.ID)
        WHERE r.TYP LIKE 'VA'
              AND r.status = 'RUEM_VA_VERSENDET'
              AND r.answer_deadline IS NOT NULL
              AND g.wechseltermin IS NOT NULL
              AND
              g.vorabstimmungsid IN
              (SELECT
                 q1g.vorabstimmungsid
               FROM T_WBCI_REQUEST q1r JOIN T_WBCI_GESCHAEFTSFALL q1g ON (q1r.geschaeftsfall_id = q1g.id)
               WHERE q1g.status <> 'COMPLETE'
                     AND q1g.typ <> 'VA_RRNP'
                     AND q1r.TYP LIKE 'TV'
                     AND q1r.status IN ('TV_ERLM_VERSENDET', 'TV_ERLM_EMPFANGEN')
               GROUP BY q1g.vorabstimmungsid)) up
SET up.deadline = day_in_working_days(wt, (SELECT
                                             af.frist_in_stunden / 24
                                           FROM T_WBCI_ANTWORTFRIST af
                                           WHERE af.STATUS = 'RUEM_VA_VERSENDET') * -1);

