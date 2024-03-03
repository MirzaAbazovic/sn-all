--rename typo in table column
ALTER TABLE T_EG_TYPE_2_HWSWITCH RENAME COLUMN HW_SWICHT_ID TO HW_SWITCH_ID;

-- function to insert certified switches
CREATE OR REPLACE PROCEDURE add_certified_switch(
  typ_hersteller IN VARCHAR2,
  typ_modell     IN VARCHAR2,
  switch_name    IN VARCHAR2,
  switch_prioriy IN NUMBER
)
IS
  BEGIN
    INSERT INTO T_EG_TYPE_2_HWSWITCH (EG_TYPE_ID, HW_SWITCH_ID, PRIORITY)
      SELECT
        eg.ID,
        (SELECT ID
         FROM T_HW_SWITCH
         WHERE NAME LIKE switch_name),
        switch_prioriy
      FROM T_EG_TYPE eg
      WHERE HERSTELLER LIKE typ_hersteller AND MODELL LIKE typ_modell;
  END;
/

-- Uebernahme der Eintraege aus https://intranet.m-net.de/display/KPGeschaeftskunden/Premium+SIP-Trunk, Stand 06.05.2015
CALL add_certified_switch('Innovaphone', 'IP800%', 'MUC06', 1);
CALL add_certified_switch('Innovaphone', 'IP3010', 'MUC06', 1);
CALL add_certified_switch('Innovaphone', 'IP1060', 'MUC06', 1);
CALL add_certified_switch('Innovaphone', 'IP0010', 'MUC06', 1);
CALL add_certified_switch('Innovaphone', 'IP810', 'MUC06', 1);
CALL add_certified_switch('Innovaphone', 'IP305', 'MUC06', 1);
CALL add_certified_switch('Innovaphone', 'IP302', 'MUC06', 1);
CALL add_certified_switch('Innovaphone', 'IP38', 'MUC06', 1);
CALL add_certified_switch('Mitel / Aastra%', 'X320%', 'MUC06', 1);
CALL add_certified_switch('Mitel / Aastra%', '11.x', 'MUC06', 1);
CALL add_certified_switch('Pascom', '7.x', 'MUC06', 1);
CALL add_certified_switch('Starface', '5.8.x', 'MUC06', 1);
CALL add_certified_switch('Unify', 'OpenScape%V1%R3.3.3.0', 'MUC06', 1);
CALL add_certified_switch('Unify', 'OpenScape%V8%+%SBC%', 'MUC06', 1);
CALL add_certified_switch('Swyx%', '2015 R2', 'MUC06', 1);
CALL add_certified_switch('IPTAM', '3.1.2', 'MUC06', 1);

DROP PROCEDURE add_certified_switch;
