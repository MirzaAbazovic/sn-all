-- due to a mistake in Navision migration we have to set RANG_SS_TYPE from 4H to 2H
-- 4H is just used if RANG_LEISTE2 and RANG_STIFT2 is set as well
UPDATE T_EQUIPMENT e
   SET E.RANG_SS_TYPE = '2H'
 WHERE E.EQ_ID IN (SELECT E1.EQ_ID
                     FROM T_EQUIPMENT e1
                          JOIN T_HVT_STANDORT s
                             ON S.HVT_ID_STANDORT = E1.HVT_ID_STANDORT
                          JOIN T_HVT_GRUPPE g
                             ON G.HVT_GRUPPE_ID = S.HVT_GRUPPE_ID
                                AND G.NIEDERLASSUNG_ID = 4
                    WHERE E1.RANG_SS_TYPE = '4H');
