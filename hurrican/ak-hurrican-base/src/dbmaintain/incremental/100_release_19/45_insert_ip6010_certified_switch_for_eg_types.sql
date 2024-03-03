-- IP6010 auf den MUC06 freigeben
INSERT INTO T_EG_TYPE_2_HWSWITCH (EG_TYPE_ID, HW_SWITCH_ID, PRIORITY)
    SELECT
        eg.ID,
        (SELECT ID
            FROM T_HW_SWITCH
            WHERE NAME = 'MUC06'),
        1
    FROM T_EG_TYPE eg
    WHERE HERSTELLER = 'Innovaphone' AND MODELL = 'IP6010';
