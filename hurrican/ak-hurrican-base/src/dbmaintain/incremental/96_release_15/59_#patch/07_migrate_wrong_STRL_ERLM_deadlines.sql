-- update conformed STR-AEN deadlines with an valid Wechseltermin
UPDATE (SELECT
          r.ANSWER_DEADLINE AS deadline,
          g.wechseltermin   AS wt
        FROM T_WBCI_REQUEST r JOIN T_WBCI_GESCHAEFTSFALL g ON (r.geschaeftsfall_id = g.ID)
        WHERE r.TYP IN ('STR-AEN-AUF', 'STR-AEN-ABG')
              AND g.status <> 'COMPLETE'
              AND r.status IN ('STORNO_ERLM_EMPFANGEN', 'STORNO_ERLM_VERSENDET')
              AND r.answer_deadline IS NOT NULL
              AND g.wechseltermin IS NOT NULL
        ) up
SET up.deadline = day_in_working_days(wt, -7);