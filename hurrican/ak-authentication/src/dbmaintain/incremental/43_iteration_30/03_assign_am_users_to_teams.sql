-- Teams erstellen und Abteilung AM zuordnen
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Team 'PKP NBG' anlegen und AM zuordnen
INSERT INTO TEAM (ID, TEAM_NAME, DEPARTMENT_ID, VERSION)
    VALUES (S_TEAM_0.nextVal, 'PKP NBG', (SELECT dpmnt.ID FROM DEPARTMENT dpmnt WHERE dpmnt.NAME='AM'), 0);

-- Team 'PKP AUG' anlegen und AM zuordnen
INSERT INTO TEAM (ID, TEAM_NAME, DEPARTMENT_ID, VERSION)
    VALUES (S_TEAM_0.nextVal, 'PKP AUG', (SELECT dpmnt.ID FROM DEPARTMENT dpmnt WHERE dpmnt.NAME='AM'), 0);

-- Team 'PKP MUC 1' anlegen und AM zuordnen
INSERT INTO TEAM (ID, TEAM_NAME, DEPARTMENT_ID, VERSION)
    VALUES (S_TEAM_0.nextVal, 'PKP MUC 1', (SELECT dpmnt.ID FROM DEPARTMENT dpmnt WHERE dpmnt.NAME='AM'), 0);

-- Team 'PKP MUC 2' anlegen und AM zuordnen
INSERT INTO TEAM (ID, TEAM_NAME, DEPARTMENT_ID, VERSION)
    VALUES (S_TEAM_0.nextVal, 'PKP MUC 2', (SELECT dpmnt.ID FROM DEPARTMENT dpmnt WHERE dpmnt.NAME='AM'), 0);

-- Team 'INTERTEL' anlegen und AM zuordnen
INSERT INTO TEAM (ID, TEAM_NAME, DEPARTMENT_ID, VERSION)
    VALUES (S_TEAM_0.nextVal, 'INTERTEL', (SELECT dpmnt.ID FROM DEPARTMENT dpmnt WHERE dpmnt.NAME='AM'), 0);

-- User den erstellten Teams zuordnen
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- AM User dem Team 'PKP NBG' zuordnen
UPDATE USERS us SET TEAM_ID = (SELECT tm.ID FROM TEAM tm WHERE tm.TEAM_NAME='PKP NBG')
    WHERE lower(LOGINNAME) IN ('deussinggr', 'freundka', 'hackbarthma', 'hauffean', 'schmidev', 'spanheimerso', 'thomassy', 'fassbinderja');

-- AM User dem Team 'PKP AUG' zuordnen
UPDATE USERS us SET TEAM_ID = (SELECT tm.ID FROM TEAM tm WHERE tm.TEAM_NAME='PKP AUG')
    WHERE lower(LOGINNAME) IN ('bucheggerle', 'fisselst', 'imbergerma', 'listlth', 'maiergu', 'predigersu', 'siewertdo');

-- AM User dem Team 'PKP MUC 1' zuordnen
UPDATE USERS us SET TEAM_ID = (SELECT tm.ID FROM TEAM tm WHERE tm.TEAM_NAME='PKP MUC 1')
    WHERE lower(LOGINNAME) IN ('elfekyal', 'schermal', 'himmelch', 'kupinaal', 'lanzjo', 'roechnerma', 'schaetznerhe', 'stahleres', 'hagizadehaz', 'kirmeierda', 'hossein-namial', 'petzma');

-- AM User dem Team 'PKP MUC 2' zuordnen
UPDATE USERS us SET TEAM_ID = (SELECT tm.ID FROM TEAM tm WHERE tm.TEAM_NAME='PKP MUC 2')
    WHERE lower(LOGINNAME) IN ('fedaiemu', 'haasst', 'jauernigke', 'koosal', 'preissje', 'schmittse', 'zettlke', 'braeunigro', 'ciucesja', 'hossein-namime', 'kueresna', 'jungal');

-- AM User dem Team 'INTERTEL' zuordnen
UPDATE USERS us SET TEAM_ID = (SELECT tm.ID FROM TEAM tm WHERE tm.TEAM_NAME='INTERTEL')
    WHERE lower(LOGINNAME) in ('hermannla', 'ismailogluci', 'kammka', 'krämergi', 'kressel', 'markheimly', 'martinezma', 'nguyenbang', 'pionteker', 'schwarzmi', 'streileinan', 'willean');
