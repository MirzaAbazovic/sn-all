
-- UEVT Tabelle um KVZ Nummer erweitert
alter table T_UEVT add KVZ_NUMMER VARCHAR2(5);
comment on column T_UEVT.KVZ_NUMMER IS 'KVZ Nummer der DTAG';
comment on column T_UEVT.UEVT IS 'UEVT bzw. KVZ Schaltnummer des DTAG Verteilers';
comment on column T_EQUIPMENT.KVZ_NUMMER IS 'KVZ Nummer (DTAG)';

-- KVZ Amberg konfigurieren
update T_UEVT set KVZ_NUMMER='A031' where HVT_ID_STANDORT=819;

-- KVZ Daten von HVT Tabelle entfernen
alter table T_HVT_STANDORT drop (KVZ_NUMMER, KVZ_SCHALTNUMMER);
