-- M-Queue + Fieldservice
INSERT INTO T_BA_VERL_CONFIG
    SELECT S_T_BA_VERL_CONFIG_0.nextval, prod_id, anlass, 18, to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), SYSDATE, 'stollebe', 0, 1
        FROM T_BA_VERL_CONFIG
        WHERE PROD_ID IN (322,323,327,336,337,340,350,351,420,421,430,431,440,445,446,501,502,503,511,512,513) AND gueltig_bis > SYSDATE AND userw <> 'stollebe'
            AND abt_config_id IN (8,10,11,13,14,15);
-- M-Queue
INSERT INTO T_BA_VERL_CONFIG
    SELECT S_T_BA_VERL_CONFIG_0.nextval, prod_id, anlass, 17, to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), SYSDATE, 'stollebe', 0, 1
        FROM T_BA_VERL_CONFIG
        WHERE PROD_ID IN (322,323,327,336,337,340,350,351,420,421,430,431,440,445,446,501,502,503,511,512,513) AND gueltig_bis > SYSDATE AND userw <> 'stollebe'
            AND abt_config_id IN (2,5,16);

-- alle alten Configs als ungültig markieren
UPDATE T_BA_VERL_CONFIG SET GUELTIG_BIS=to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy')
    WHERE PROD_ID IN (322,323,327,336,337,340,350,351,420,421,430,431,440,445,446,501,502,503,511,512,513) AND gueltig_bis > SYSDATE
        AND userw <> 'stollebe' AND abt_config_id IN (2,5,8,10,11,13,14,15,16);

-- Zusatzverteilungen kopieren
INSERT INTO T_BA_ZUSATZ
    SELECT S_T_BA_ZUSATZ_0.nextval, (
        SELECT id FROM T_BA_VERL_CONFIG WHERE prod_id=c.prod_id AND anlass=c.anlass AND gueltig_bis > SYSDATE
    ),z.abt_id, Z.HVT_GRUPPE_ID, Z.AUCH_SM, to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), SYSDATE, 'stollebe', 0
        FROM T_BA_ZUSATZ z, T_BA_VERL_CONFIG c
        WHERE Z.BA_VERL_CONFIG_ID = C.ID AND c.PROD_ID IN (322,323,327,336,337,340,350,351,420,421,430,431,440,445,446,501,502,503,511,512,513)
            AND c.gueltig_bis < SYSDATE AND c.userw <> 'stollebe' AND c.abt_config_id IN (2,5,8,10,11,13,14,15,16);

-- alte Zusatzverteilungen als ungültig markieren
UPDATE T_BA_ZUSATZ SET GUELTIG_BIS=to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy')
    WHERE id IN (
        SELECT z.id
            FROM T_BA_ZUSATZ z, T_BA_VERL_CONFIG c
            WHERE Z.BA_VERL_CONFIG_ID = C.ID AND c.PROD_ID IN (322,323,327,336,337,340,350,351,420,421,430,431,440,445,446,501,502,503,511,512,513)
                AND c.gueltig_bis < SYSDATE AND c.userw <> 'stollebe' AND c.abt_config_id IN (2,5,8,10,11,13,14,15,16)
    );
