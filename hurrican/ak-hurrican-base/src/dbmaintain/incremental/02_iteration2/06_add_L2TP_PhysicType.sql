--
-- SQL-Script, um die Physiktypen fuer Alcatel anzulegen.
--

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (109, 'L2TP', 'virtueller L2TP Port',
    null, null, 'L2TP', 2, 'L2TP');

commit;

