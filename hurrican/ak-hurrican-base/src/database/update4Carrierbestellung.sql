--
-- SQL-Script, um die Carrierbestellung um die Referenz auf
-- einen Equipment-Datensatz zu erweitern
--

alter table T_CARRIERBESTELLUNG add EQ_OUT_ID NUMBER(10);

CREATE INDEX IX_FK_CB_2_EQ
        ON T_CARRIERBESTELLUNG (EQ_OUT_ID) TABLESPACE "I_HURRICAN";

ALTER TABLE t_carrierbestellung
  ADD CONSTRAINT FK_CB_2_EQ
      FOREIGN KEY (EQ_OUT_ID)
      REFERENCES t_equipment (EQ_ID);

commit;
