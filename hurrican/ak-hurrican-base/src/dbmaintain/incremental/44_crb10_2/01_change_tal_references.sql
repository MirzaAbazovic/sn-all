delete from T_REFERENCE where ID in (8006,8007);
update T_REFERENCE set STR_VALUE='Port-Änderung (LAE,LMAE,SER-POW)',
  DESCRIPTION='Typ fuer eine TAL-Bestellung - Geschaeftsfaelle: LAE, LMAE, SER-POW ; INT_VALUE = ID der ServiceChain',
  INT_VALUE=43 where ID=8005;