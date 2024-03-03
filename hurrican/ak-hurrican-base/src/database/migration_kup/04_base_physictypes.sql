--
-- Migration KuP / Hurrican
-- ===========================================================================
-- Inhalt:
--   - Anlage von notwendigen Physiktypen
--
-- Info zu PT_GROUP:
--		1=Phone
--		2=Data
--		3=TV
--		4=FttB
--

insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (100, 'ADSL (Alc-ATM)', 'Alcatel ADSL Technik - ATM basiert', 
  6, 6000, 'ADSL', 2);
insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (101, 'ADSL2+ (Alc-ATM)', 'Alcatel ADSL2+ Technik - ATM basiert (nur bis 6000 kbit/s)', 
  6, 6000, 'ADSL', 2);
insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (102, 'ADSL2+ (Alc-IP)', 'Alcatel ADSL2+ Technik - IP basiert (bis 18000 kbit/s)', 
  6, 18000, 'ADSL', 2);
insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (103, 'ADSL-UK0 (Alc)', '', 6, null, 'UK0', 1); 
insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (104, 'ADSL2+ only (Alc-IP)', 'Alcatel ADSL2+ Technik (IP basiert) fuer DSL-Pur', 
  6, 18000, 'ADSL_PUR', 2);
insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (105, 'SDSL (Alc-ATM)', 'Alcatel SDSL Technik - ATM basiert', 
  6, null, 'SDSL_OUT', 2);
insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (106, 'SDSL (Alc-IP)', 'Alcatel SDSL Technik - IP basiert', 
  6, null, 'SDSL_OUT', 2);
insert into T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP)
  values (107, 'SHDSL (Alc-IP)', 'Alcatel SHDSL Technik - IP basiert', 
  6, 10000, 'SDSL_OUT', 2);
  
commit;


