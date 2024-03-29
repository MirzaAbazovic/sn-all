alter table T_HVT_UMZUG add HVT_ID_STANDORT_DEST NUMBER(19);
update T_HVT_UMZUG set HVT_ID_STANDORT_DEST=758;
alter table T_HVT_UMZUG modify HVT_ID_STANDORT_DEST NOT NULL;

ALTER TABLE T_HVT_UMZUG ADD
  CONSTRAINT FK_HVT_UMZUG_2_HVT
  FOREIGN KEY (HVT_ID_STANDORT)
  REFERENCES T_HVT_STANDORT (HVT_ID_STANDORT);

ALTER TABLE T_HVT_UMZUG ADD
  CONSTRAINT FK_HVT_UMZUG_2_HVT_DEST
  FOREIGN KEY (HVT_ID_STANDORT_DEST)
  REFERENCES T_HVT_STANDORT (HVT_ID_STANDORT);
