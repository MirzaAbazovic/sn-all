--
-- SQL-Script, um die Physiktypen fuer Alcatel anzulegen.
--

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (110, 'ATM', 'virtueller ATM Port - QSC-Kopplungen',
    null, null, 'ATM', 2, 'ATM');

commit;

