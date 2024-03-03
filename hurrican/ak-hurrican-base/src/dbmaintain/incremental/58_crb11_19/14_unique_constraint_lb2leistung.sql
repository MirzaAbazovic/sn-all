-- doppelt hinterlegte Konfiguration entfernen
delete from T_LB_2_LEISTUNG where ID in (331,174,173,169);

-- UniqueConstraint auf LB_ID/LEISTUNG_ID/VERWENDEN_BIS hinterlegen
alter table T_LB_2_LEISTUNG ADD CONSTRAINT UQ_LB2LEISTUNG UNIQUE (LB_ID, LEISTUNG_ID, VERWENDEN_BIS);
