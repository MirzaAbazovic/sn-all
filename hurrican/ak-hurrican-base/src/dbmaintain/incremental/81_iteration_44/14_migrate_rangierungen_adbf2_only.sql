-- alle Rangierungen, die den PT 'ADSL2+ only (H)' = 514 gesetzt haben und mit einer ADBF2 Baugruppe verdrahtet sind, auf PT 'ADSL2+ only MS (H)' = 517 aendern 
UPDATE t_rangierung
   SET PHYSIK_TYP = 517, USERW = 'ADBF2_MIG'
 WHERE RANGIER_ID IN (SELECT R.RANGIER_ID
                        FROM t_hw_baugruppen_typ bgt
                             INNER JOIN t_hw_baugruppe bg
                                ON BGT.ID = BG.HW_BG_TYP_ID
                             INNER JOIN t_equipment eq
                                ON BG.ID = EQ.HW_BAUGRUPPEN_ID
                             INNER JOIN t_rangierung r
                                ON EQ.EQ_ID = R.EQ_IN_ID
                       WHERE BGT.ID = 311 AND r.PHYSIK_TYP = 514);
