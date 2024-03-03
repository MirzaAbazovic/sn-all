-- On update these function see also 'deployment\wita-acceptance-test\database\taifun\holiday_function.sql'

-- Oracle Function, um den Ostersonntag zu berechnen (Grundlage fuer Feiertagsberechnungen)
CREATE OR REPLACE FUNCTION eastern(yeartocalc NUMBER DEFAULT TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY')))
  RETURN DATE IS
  RESULT DATE;
  a      INTEGER;
  b      INTEGER;
  c      INTEGER;
  d      INTEGER;
  e      INTEGER;
  f      INTEGER;
  g      INTEGER;
  BEGIN
    a := MOD(yeartocalc, 19);
    b := yeartocalc / 100;
    c := (8 * b + 13) / 25 - 2;
    d := b - (yeartocalc / 400) - 2;
    e := MOD(19 * MOD(yeartocalc, 19) + MOD(15 - c + d, 30), 30);
    IF e = 28
    THEN
      IF a > 10
      THEN
        e := 27;
      END IF;
    ELSE
      IF e = 29
      THEN
        e := 28;
      END IF;
    END IF;
    f := MOD(d + 6 * e + 2 * MOD(yeartocalc, 4) + 4 * MOD(yeartocalc, 7) + 6,
             7);
    g := e + f + 22;
    IF g > 31
    THEN
      RESULT := TO_DATE(g - 31 || '.4.' || yeartocalc, 'DD.MM.YYYY');
    ELSE
      RESULT := TO_DATE(g || '.3.' || yeartocalc, 'DD.MM.YYYY');
    END IF;
    RETURN (RESULT);
  END eastern;
/

-- Function, um das Datum von einem Feiertag (und Augsburger Friedensfest) zu ermitteln
CREATE OR REPLACE FUNCTION holiday(holidaynumber NUMBER, yeartocalc NUMBER
DEFAULT TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY')))
  RETURN DATE IS
  RESULT DATE;
  BEGIN
    IF holidaynumber = 1
    THEN
--Neujahr
      RESULT := TO_DATE('01.01.' || yeartocalc, 'DD.MM.YYYY');
    ELSIF holidaynumber = 2
      THEN
--Erscheinungsfest
        RESULT := TO_DATE('06.01.' || yeartocalc, 'DD.MM.YYYY');
    ELSIF holidaynumber = 3
      THEN
--Karfreitag
        RESULT := eastern(yeartocalc) - 2;
    ELSIF holidaynumber = 4
      THEN
--Ostersonntag
        RESULT := eastern(yeartocalc);
    ELSIF holidaynumber = 5
      THEN
--Ostermontag
        RESULT := eastern(yeartocalc) + 1;
    ELSIF holidaynumber = 6
      THEN
--Maifeiertag
        RESULT := TO_DATE('01.05.' || yeartocalc, 'DD.MM.YYYY');
    ELSIF holidaynumber = 7
      THEN
--ChristiHimmelfahrt
        RESULT := eastern(yeartocalc) + 39;
    ELSIF holidaynumber = 8
      THEN
--Pfingstmontag
        RESULT := eastern(yeartocalc) + 50;
    ELSIF holidaynumber = 9
      THEN
--Fronleichnam
        RESULT := eastern(yeartocalc) + 60;
    ELSIF holidaynumber = 10
      THEN
-- Augsburger Friedensfest
        RESULT := TO_DATE('08.08.' || yeartocalc, 'DD.MM.YYYY');
    ELSIF holidaynumber = 11
      THEN
--MariaeHimmelfahrt
        RESULT := TO_DATE('15.08.' || yeartocalc, 'DD.MM.YYYY');
    ELSIF holidaynumber = 12
      THEN
--TagDerEinheit
        RESULT := TO_DATE('03.10.' || yeartocalc, 'DD.MM.YYYY');
    ELSIF holidaynumber = 13
      THEN
--Reformationstag
        RESULT := TO_DATE('31.10.' || yeartocalc, 'DD.MM.YYYY');
    ELSIF holidaynumber = 14
      THEN
--Allerheiligen
        RESULT := TO_DATE('01.11.' || yeartocalc, 'DD.MM.YYYY');
    ELSIF holidaynumber = 15
      THEN
--BussUndBettag
        RESULT := TO_DATE('25.12.' || yeartocalc, 'DD.MM.YYYY') -
                  TO_CHAR(TO_DATE('25.12.' || yeartocalc, 'DD.MM.YYYY'), 'D') -
                  4 * 7 * 4;
    ELSIF holidaynumber = 16
      THEN
--Weihnachtsfeiertag1
        RESULT := TO_DATE('25.12.' || yeartocalc, 'DD.MM.YYYY');
    ELSIF holidaynumber = 17
      THEN
--Weihnachtsfeiertag2
        RESULT := TO_DATE('26.12.' || yeartocalc, 'DD.MM.YYYY');
    END IF;
    RETURN (RESULT);
  END holiday;
/

-- Function um zu pruefen, ob ein angegebenes Datum ein Feiertag ist
-- (siehe function 'holiday')
CREATE OR REPLACE FUNCTION isholiday(datetocheck DATE)
  RETURN INTEGER IS
  RESULT     INTEGER;
  yeartocalc INTEGER;
  BEGIN
    yeartocalc := TO_NUMBER(TO_CHAR(datetocheck, 'YYYY'));
    IF datetocheck = holiday(1, yeartocalc)
    THEN
--Neujahr
      RESULT := -1;
    ELSIF datetocheck = holiday(2, yeartocalc)
      THEN
--Erscheinungsfest
        RESULT := -1;
    ELSIF datetocheck = holiday(3, yeartocalc)
      THEN
--Karfreitag
        RESULT := -1;
    ELSIF datetocheck = holiday(4, yeartocalc)
      THEN
--Ostersonntag
        RESULT := -1;
    ELSIF datetocheck = holiday(5, yeartocalc)
      THEN
--Ostermontag
        RESULT := -1;
    ELSIF datetocheck = holiday(6, yeartocalc)
      THEN
--1. Mai
        RESULT := -1;
    ELSIF datetocheck = holiday(7, yeartocalc)
      THEN
--Christi Himmelfahrt
        RESULT := -1;
    ELSIF datetocheck = holiday(8, yeartocalc)
      THEN
--Pfingstmontag
        RESULT := -1;
    ELSIF datetocheck = holiday(9, yeartocalc)
      THEN
--Fronleichnam
        RESULT := -1;
    ELSIF datetocheck = holiday(10, yeartocalc)
      THEN
--Augsburger Friedensfest
        RESULT := -1;
    ELSIF datetocheck = holiday(11, yeartocalc)
      THEN
--MariaHimmelfahrt
        RESULT := -1;
    ELSIF datetocheck = holiday(12, yeartocalc)
      THEN
--Tag der dt. Einheit
        RESULT := -1;
    ELSIF datetocheck = holiday(14, yeartocalc)
      THEN
--Allerheiligen
        RESULT := -1;
    ELSIF datetocheck = holiday(16, yeartocalc)
      THEN
--1. Weihnachtsfeiertag
        RESULT := -1;
    ELSIF datetocheck = holiday(17, yeartocalc)
      THEN
--2. Weihnachtsfeiertag
        RESULT := -1;
    ELSE
      RESULT := 0;
    END IF;
    RETURN (RESULT);
  END isholiday;
/

-- Die Funktion prueft, ob das angegebene Datum ein Werktag ist oder nicht. Samstag, Sonntag und Feiertage sind keine Werktage.
-- Die Funktion basiert auf die bereits implementierte isHoliday-Funktion.
CREATE OR REPLACE FUNCTION isWorkingDay(datetocheck DATE)
  RETURN INTEGER IS
  BEGIN
    IF isholiday(datetocheck) = 0 AND TO_CHAR(datetocheck, 'DY', 'NLS_DATE_LANGUAGE=GERMAN') NOT IN ('SA', 'SO')
    THEN
      RETURN 1;
    ELSE
      RETURN 0;
    END IF;
  END isWorkingDay;
/

-- Die Funktionen fuegt die angegebene Anzahl von Tagen zu dem angegebenen Basisdatum und prueft anschließend, ob beide
-- das resultierende Datum Werktage ist. Wenn das der Fall ist, wird das resultierende Datum
-- zurueckgegeben, ansonsten wird es so lange um 1 erhöht bzw. verringert bis das Datum ein Werktage ist.
CREATE OR REPLACE FUNCTION day_in_working_days(basedate DATE, addDays INTEGER)
  RETURN DATE IS
  counterDays     INTEGER := 0;
  targetDays      INTEGER := 0;
  targetDate      DATE := trunc(basedate);
  negativeAddDays BOOLEAN := (addDays < 0);
  BEGIN
-- determine counter limit
    IF negativeAddDays
    THEN
      targetDays := addDays * -1;
    ELSE
      targetDays := addDays;
    END IF;
    WHILE counterDays < targetDays
    LOOP
      IF negativeAddDays
      THEN
-- on negative days, subtract 1 day
        targetDate := targetDate - 1;
      ELSE
-- on positive days, add 1 day
        targetDate := targetDate + 1;
      END IF;

-- if new date is an working day raise counter
      IF (isWorkingDay(targetDate) = 1)
      THEN
        counterDays := counterDays + 1;
      END IF;
    END LOOP;
    RETURN targetDate;
  END day_in_working_days;
/
