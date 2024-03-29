-- neue Parameter hinzufügen
INSERT INTO T_PROFILE_PARAMETER_MAPPER
(ID, PARAM_NAME, PARAM_GUI_NAME, PARAM_CPS_NAME, SORTORDER)
VALUES
  (S_T_PROFILE_PARAMETER_MAPPER_0.nextval, 'Traffic Table Upstream', 'Traffic Table Upstream', 'TR165TRAFFICTABLEUP', 1);

INSERT INTO T_PROFILE_PARAMETER_MAPPER
(ID, PARAM_NAME, PARAM_GUI_NAME, PARAM_CPS_NAME, SORTORDER)
VALUES
  (S_T_PROFILE_PARAMETER_MAPPER_0.nextval, 'Traffic Table Downstream', 'Traffic Table Downstream', 'TR165TRAFFICTABLEDOWN', 2);

-- Die Reihenfolge der Parameter anpassen
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 3 WHERE PARAM_NAME = 'Rate Up';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 4 WHERE PARAM_NAME = 'Rate Down';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 5 WHERE PARAM_NAME = 'G.fast Line Spectrum';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 6 WHERE PARAM_NAME = 'SOS';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 7 WHERE PARAM_NAME = 'Virtual Noise';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 8 WHERE PARAM_NAME = 'INM';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 9 WHERE PARAM_NAME = 'RFI';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 10 WHERE PARAM_NAME = 'UPBO';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 11 WHERE PARAM_NAME = 'DPBO';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 12 WHERE PARAM_NAME = 'noise margin';
UPDATE T_PROFILE_PARAMETER_MAPPER SET SORTORDER = 13 WHERE PARAM_NAME = 'INP Delay';

/* Bisher erfassten Parameter-Zuordnungen auf eine BaugruppenTypId zwischen speichern =>
Die HwTypId negieren, damit diese für die Inserts von neuen Werten benutzt und später entfernt werden können */
UPDATE T_PROFILE_PARAMETER SET HW_BAUGRUPPEN_TYP_ID = HW_BAUGRUPPEN_TYP_ID*(-1);

-- Neue Werte-Matrix anlegen

/* Werte für 'Traffic Table Upstream' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '1' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '5' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '10' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '25' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '40' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '50' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '100' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '150' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '300' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Upstream' AS PARAMETER_NAME,
    '500' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'Traffic Table Upstream' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Downstream' AS PARAMETER_NAME,
    '5' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Downstream' AS PARAMETER_NAME,
    '10' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Downstream' AS PARAMETER_NAME,
    '25' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Downstream' AS PARAMETER_NAME,
    '50' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Downstream' AS PARAMETER_NAME,
    '100' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Downstream' AS PARAMETER_NAME,
    '150' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Downstream' AS PARAMETER_NAME,
    '300' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Downstream' AS PARAMETER_NAME,
    '500' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'Rate Up' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Rate Up' AS PARAMETER_NAME,
    '2000M' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'Rate Down' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Rate Down' AS PARAMETER_NAME,
    '2000M' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'G.fast Line Spectrum' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'G.fast Line Spectrum' AS PARAMETER_NAME,
    '2M2' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'G.fast Line Spectrum' AS PARAMETER_NAME,
    '17M' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'G.fast Line Spectrum' AS PARAMETER_NAME,
    '35M' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'SOS' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'SOS' AS PARAMETER_NAME,
    'DEFVAL' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'Virtual Noise' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Virtual Noise' AS PARAMETER_NAME,
    'DEFVAL' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'INM' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'INM' AS PARAMETER_NAME,
    'DEFVAL' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'RFI' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'RFI' AS PARAMETER_NAME,
    'DEFVAL' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'UPBO' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'UPBO' AS PARAMETER_NAME,
    'DEFVAL' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'UPBO' AS PARAMETER_NAME,
    'UPBO' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'DPBO' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'DPBO' AS PARAMETER_NAME,
    'DEFVAL' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'noise margin' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'noise margin' AS PARAMETER_NAME,
    'TM6' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'noise margin' AS PARAMETER_NAME,
    'TM9' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'noise margin' AS PARAMETER_NAME,
    'TM12' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'INP Delay' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'INP Delay' AS PARAMETER_NAME,
    'INP2-ID7' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

/* Werte für 'TDD Profil' */
INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'TDD Profil' AS PARAMETER_NAME,
    'PK' AS PARAMETER_VALUE,
    1 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID*(-1) AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0 GROUP BY HW_BAUGRUPPEN_TYP_ID) EXISTING_BAUGRUPPE;

-- Nicht mehr benötgten Alt-Werte aus der DB entfernen
DELETE FROM T_PROFILE_PARAMETER WHERE HW_BAUGRUPPEN_TYP_ID < 0;
