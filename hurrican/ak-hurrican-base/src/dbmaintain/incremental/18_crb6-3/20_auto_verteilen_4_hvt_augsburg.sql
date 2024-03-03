
-- alle Augsburger Hvts auf AUTO_VERTEILEN
UPDATE T_HVT_STANDORT HVT SET HVT.AUTO_VERTEILEN = 1 WHERE HVT_GRUPPE_ID IN (
    SELECT HVT_GRUPPE_ID FROM T_HVT_GRUPPE WHERE NIEDERLASSUNG_ID IN (1, 2)
);

-- Zusatz für Sheridan, Obstmarkt und Prinz-Karl-Weg, dass Field-Service raus muss
INSERT INTO T_BA_ZUSATZ
    SELECT S_T_BA_ZUSATZ_0.nextval, C.ID, 5, HG.HVT_GRUPPE_ID, 1, to_date(to_char(SYSDATE, 'dd.mm.yyyy'), 'dd.mm.yyyy'),
            to_date('01.01.2200', 'dd.mm.yyyy'), SYSDATE, 'schmidan', 0
        FROM T_BA_VERL_CONFIG C, T_HVT_GRUPPE HG
        WHERE C.ABT_CONFIG_ID IN (17, 18) AND HG.HVT_GRUPPE_ID IN (62, 75, 96);

-- Münchner Hvts vom Typ HVT, KVZ, FTTB, FTTH freigeben
UPDATE T_HVT_STANDORT HVT SET HVT.AUTO_VERTEILEN = 1 WHERE STANDORT_TYP_REF_ID IN (11000, 11001, 11002, 11011);
