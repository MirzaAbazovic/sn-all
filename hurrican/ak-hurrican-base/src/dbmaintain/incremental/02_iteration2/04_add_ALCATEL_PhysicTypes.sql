--
-- SQL-Script, um die Physiktypen fuer Alcatel anzulegen.
--

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (100, 'ADSL (Alc-ATM)', 'Alcatel ADSL Technik - ATM basiert',
    6, 6000, 'ADSL', 2, 'ADSL');

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (101, 'ADSL2+ (Alc-ATM)', 'Alcatel ADSL2+ Technik - ATM basiert (nur bis 6000 kbit/s)',
    6, 6000, 'ADSL', 2, 'ADSL2+');

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (102, 'ADSL2+ (Alc-IP)', 'Alcatel ADSL2+ Technik - IP basiert (bis 18000 kbit/s)',
    6, 18000, 'ADSL', 2, 'ADSL2+');

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (103, 'ADSL-UK0 (Alc)', 'ISDN Part von einer ADSL+ISDN Rangierung (Alcatel)',
    6, null, 'UK0', 1, null);

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (104, 'ADSL-ab (Alc)', 'AB Part von einer ADSL+AB Rangierung (Alcatel)',
    6, null, 'AB', 1, null);

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (105, 'ADSL2+ only (Alc-IP)', 'Alcatel ADSL2+ Technik (IP basiert) fuer DSL-Pur',
    6, 18000, 'ADSL_PUR', 2, 'ADSL2+');

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (106, 'SDSL (Alc-ATM)', 'Alcatel SDSL Technik - ATM basiert',
    6, null, 'SDSL_OUT', 2, null);

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (107, 'SDSL (Alc-IP)', 'Alcatel SDSL Technik - IP basiert',
    6, null, 'SDSL_OUT', 2, null);

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG,
  HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD)
    values (108, 'SHDSL (Alc-IP)', 'Alcatel SHDSL Technik - IP basiert',
    6, 10000, 'SDSL_OUT', 2, null);

commit;

