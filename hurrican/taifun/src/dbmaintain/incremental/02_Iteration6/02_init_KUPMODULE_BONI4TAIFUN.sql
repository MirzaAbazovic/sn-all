CREATE OR REPLACE FUNCTION Bonistatus(
  referenz_nummer_in   IN   VARCHAR2,
  kd_familienname_in   IN   VARCHAR2,
  kd_vorname_in        IN   VARCHAR2,
  kd_geschlecht_in     IN   VARCHAR2,
  kd_geburtsdatum_in   IN   VARCHAR2,
  kd_plz_in            IN   VARCHAR2,
  kd_ort_in            IN   VARCHAR2,
  kd_strasse_in        IN   VARCHAR2,
  kd_hausnr_in         IN   VARCHAR2,
  kd_telefonnr_in      IN   VARCHAR2,
  kd_blz_in            IN   VARCHAR2,
  kd_ktonr_in          IN   VARCHAR2,
  bearbeiter_in        IN   VARCHAR2
)
  RETURN NUMBER
AS
  LANGUAGE JAVA
  NAME 'de.mnet.CEG.HTTP.query(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String) return int';
/

----------------------------------------------------------------------------------------------------
-- Achtung die function muss abhgg. von der Zielummgebung unterschiedlich gesetzt werden
-- nur TAPROD01 greift auf die produktive CEG-Onlineschnittstelle durch
----------------------------------------------------------------------------------------------------

DECLARE
 i number(11);
BEGIN
----------------------------------------------------------------------------------------------------
-- TAPROD01 ..
----------------------------------------------------------------------------------------------------
if SYS_CONTEXT('USERENV','CURRENT_SCHEMA') = 'TAPROD01' then

execute immediate ('CREATE OR REPLACE
FUNCTION f_bonistatus_taifun (
   referenz_nummer_in IN VARCHAR2,
   kd_familienname_in IN VARCHAR2,
   kd_vorname_in IN VARCHAR2,
   kd_geschlecht_in IN VARCHAR2,
   kd_geburtsdatum_in IN VARCHAR2,
   kd_plz_in IN VARCHAR2,
   kd_ort_in IN VARCHAR2,
   kd_strasse_in IN VARCHAR2,
   kd_hausnr_in IN VARCHAR2,
   kd_telefonnr_in IN VARCHAR2,
   kd_blz_in IN VARCHAR2,
   kd_ktonr_in IN VARCHAR2,
   bearbeiter_in IN VARCHAR2
)
   RETURN NUMBER DETERMINISTIC IS
   iret NUMBER (11);
BEGIN
   DECLARE
      iboni NUMBER;
   BEGIN
--      IF UPPER (kd_familienname_in) IN
--            (''EINS'', ''ZWEI'', ''DREI'', ''VIER'', ''FÜNF'', ''SECHS'', ''SIEBEN'',
--             ''ACHT'', ''NEUN'', ''ZEHN'', ''ELF'', ''ZWÖLF'', ''DREIZEHN'', ''VIERZEHN'',
--             ''FÜNFZEHN'', ''SECHZEHN'', ''SIEBZEHN'', ''VIERZIG'', ''ACHTZIG'',
--             ''NEUNZIG'') THEN
      iboni :=
         bonistatus (substr(referenz_nummer_in,1,20),
                     kd_familienname_in,
                     kd_vorname_in,
                     kd_geschlecht_in,
                     kd_geburtsdatum_in,
                     kd_plz_in,
                     kd_ort_in,
                     kd_strasse_in,
                     kd_hausnr_in,
                     kd_telefonnr_in,
                     kd_blz_in,
                     kd_ktonr_in,
                     bearbeiter_in
                    );

--      ELSE
---- simuliert eine boni-abfrage
--         IF kd_geschlecht_in = 2 THEN
--            iboni := 2;
--         ELSE
--            iboni := 3;
--         END IF;
--      END IF;
      SELECT DECODE (iboni,
                     3, ''40'',                        -- Kunde ist kreditwürdig
                     2, ''60'',                  -- Kunde ist nicht kreditwürdig
                     1, ''11'',                          -- Keine Daten gefunden
-- negativer Wert
                     iboni
                    )
        INTO iret
        FROM DUAL;

      RETURN iret;
   END;
END f_bonistatus_taifun;
');

else

----------------------------------------------------------------------------------------------------
-- .. oder alle anderen TA*-UMgebungen (ausser TAPROD01)
----------------------------------------------------------------------------------------------------

execute immediate ('
CREATE OR REPLACE FUNCTION f_bonistatus_taifun (
   referenz_nummer_in IN VARCHAR2,
   kd_familienname_in IN VARCHAR2,
   kd_vorname_in IN VARCHAR2,
   kd_geschlecht_in IN VARCHAR2,
   kd_geburtsdatum_in IN VARCHAR2,
   kd_plz_in IN VARCHAR2,
   kd_ort_in IN VARCHAR2,
   kd_strasse_in IN VARCHAR2,
   kd_hausnr_in IN VARCHAR2,
   kd_telefonnr_in IN VARCHAR2,
   kd_blz_in IN VARCHAR2,
   kd_ktonr_in IN VARCHAR2,
   bearbeiter_in IN VARCHAR2
)
   RETURN NUMBER DETERMINISTIC IS
   iret NUMBER (11);
BEGIN
   DECLARE
      iboni NUMBER;
   BEGIN

      IF length(referenz_nummer_in) > 20 THEN
          raise_application_error (-20002, ''f_bonistatus_taifun: MAXLEN is 20: '' || referenz_nummer_in);
      END IF;

      IF UPPER (kd_familienname_in) IN
            (''EINS'', ''ZWEI'', ''DREI'', ''VIER'', ''FÜNF'', ''SECHS'', ''SIEBEN'',
             ''ACHT'', ''NEUN'', ''ZEHN'', ''ELF'', ''ZWÖLF'', ''DREIZEHN'', ''VIERZEHN'',
             ''FÜNFZEHN'', ''SECHZEHN'', ''SIEBZEHN'', ''VIERZIG'', ''ACHTZIG'',
             ''NEUNZIG'') THEN
         iboni :=
            bonistatus (referenz_nummer_in,
                        kd_familienname_in,
                        kd_vorname_in,
                        kd_geschlecht_in,
                        kd_geburtsdatum_in,
                        kd_plz_in,
                        kd_ort_in,
                        kd_strasse_in,
                        kd_hausnr_in,
                        kd_telefonnr_in,
                        kd_blz_in,
                        kd_ktonr_in,
                        bearbeiter_in
                       );
      ELSE
-- simuliert eine boni-abfrage
         IF kd_geschlecht_in = 2 THEN
            iboni := 2;
         ELSE
            iboni := 3;
         END IF;
      END IF;

      SELECT DECODE (iboni,
                     3, ''40'',                        -- Kunde ist kreditwürdig
                     2, ''60'',                  -- Kunde ist nicht kreditwürdig
                     1, ''11'',                          -- Keine Daten gefunden
-- negativer Wert
                     iboni
                    )
        INTO iret
        FROM DUAL;

      RETURN iret;
   END;
END f_bonistatus_taifun;
');

end if;
end;
/
------------------------------------------------------------------------
grant all on f_bonistatus_taifun to R_TAIFUN_KUP;
grant all on f_bonistatus_taifun to TAIFUN_KUP;
grant all on bonistatus to R_TAIFUN_KUP;
grant all on bonistatus to TAIFUN_KUP
/

select object_type, object_name from user_objects
  where object_type in ('FUNCTION','PACKAGE','VIEW','TRIGGER','PROCEDURE')
  and status = 'INVALID'
/
-- set serveroutput on
begin
  for one in (select object_type, object_name from user_objects
  where object_type in ('VIEW')
  and status = 'INVALID') loop
  -- dbms_output.put_line('Updating '||one.object_type||' '||one.object_name);
  begin
    execute immediate 'alter '||one.object_type||' '||one.object_name||' compile';
    exception
    when others then
    null; -- ignore, and proceed.
  end;
end loop;
end;
/
commit
/
--
-- set serveroutput on
begin
  for one in (select object_type, object_name from user_objects
  where object_type in ('FUNCTION','PACKAGE','VIEW','TRIGGER','PROCEDURE')
  and status = 'INVALID') loop
  -- dbms_output.put_line('Updating '||one.object_type||' '||one.object_name);
  begin
    execute immediate 'alter '||one.object_type||' '||one.object_name||' compile';
    exception
    when others then
    null; -- ignore, and proceed.
  end;
end loop;
end;
/
commit
/
select object_type, object_name from user_objects
  where object_type in ('FUNCTION','PACKAGE','VIEW','TRIGGER','PROCEDURE')
  and status = 'INVALID'
/
