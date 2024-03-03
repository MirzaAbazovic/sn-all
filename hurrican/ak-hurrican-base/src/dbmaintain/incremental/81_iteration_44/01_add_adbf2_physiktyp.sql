-- Physiktyp fuer ADBF2 Baugruppen hinzufuegen
insert into T_PHYSIKTYP
   (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD, VERSION)
 values
   (516, 'ADSL2+ VoIP (H)', NULL, 2, 18000, 'ADSL', 2, 'ADSL2+', 0);
   
-- erstellten Physiktyp mit Baugruppe mappen
insert into T_HW_BG_TYP_2_PHYSIK_TYP 
    (ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION) 
  values 
    (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, 311, 516, 0);


