-- M-Queue nicht als zentrale Abteilung anlegen
UPDATE T_ABTEILUNG SET NIEDERLASSUNG_ID = 3 WHERE ID = 13;

DELETE FROM T_ABTEILUNG_2_NIEDERLASSUNG WHERE ABTEILUNG_ID = 13 AND NIEDERLASSUNG_ID = 5;
INSERT INTO T_ABTEILUNG_2_NIEDERLASSUNG VALUES (S_T_ABT_2_NL_0.nextval, 13, 1, 0);
INSERT INTO T_ABTEILUNG_2_NIEDERLASSUNG VALUES (S_T_ABT_2_NL_0.nextval, 13, 3, 0);
INSERT INTO T_ABTEILUNG_2_NIEDERLASSUNG VALUES (S_T_ABT_2_NL_0.nextval, 13, 4, 0);