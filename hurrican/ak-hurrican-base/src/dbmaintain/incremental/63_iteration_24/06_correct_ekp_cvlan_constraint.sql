-- VOD kommt hinzu
alter table T_CVLAN drop constraint CONSTR_CVLAN_TYP;
alter table T_CVLAN add constraint CONSTR_CVLAN_TYP check (TYP in ('HSI', 'HSIPLUS', 'VOIP', 'POTS', 'IAD', 'TV', 'VOD'));

-- Constraint-Name an Konvention anpassen
alter table T_CVLAN drop constraint CONSTR_CVLAN_CONTR_TYP_UNIQUE;
alter table T_CVLAN add constraint UK_CVLAN_CONTR_TYP unique(EKP_FRAME_CONTRACT_ID, TYP);

alter table T_EKP_FRAME_CONTRACT drop constraint CONSTR_EKP_EKP_ID_UNIQUE;
alter table T_EKP_FRAME_CONTRACT add constraint UK_EKP_EKP_ID unique (EKP_ID, CONTRACT_ID);

-- 'besseren' Spaltennamen verwenden
alter table T_CVLAN rename column PBIT to PBIT_LIMIT;

-- Tabellen mit Praefix FTTX versehen
alter table T_EKP_FRAME_CONTRACT rename to T_FTTX_EKP_FRAME_CONTRACT;
alter table T_CVLAN rename to T_FTTX_CVLAN;
