-- User anlegen (Exceptions - falls User schon vorhanden - werden unterdrueckt)
DECLARE
   dummy   NUMBER;
BEGIN
   EXECUTE IMMEDIATE
      (' CREATE USER "VENTO" PROFILE "DEFAULT" IDENTIFIED BY "vento2010" DEFAULT TABLESPACE "T_HURRICAN"');
EXCEPTION
   WHEN OTHERS
   THEN
      DBMS_OUTPUT.PUT_LINE ('SQLERRM = ' || SQLERRM);
      NULL;
END;
/

GRANT "CONNECT" TO "VENTO";

create table VENTO_MIG_ADDRESS2GEOID (
  ADDRESS_ID NUMBER(10),
  ENDSTELLE_ID NUMBER(10),
  GEO_ID NUMBER(19)
);

comment on table VENTO_MIG_ADDRESS2GEOID
  is 'Hilfstabelle fuer Vento-Migration; wird von Vento-Team befuellt und dient als Migrationsbasis fuer Hurrican';

GRANT INSERT, SELECT, UPDATE ON VENTO_MIG_ADDRESS2GEOID TO VENTO;
