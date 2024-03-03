--
-- Update-Script, um Verlaeufe auch negativ abschliessen zu koennen
--

-- Tabellenerweiterungen
alter table T_VERLAUF add NOT_POSSIBLE CHAR(1);
comment on COLUMN T_VERLAUF.NOT_POSSIBLE 
  IS 'gesetztes Flag definiert, dass min. eine Abteilung den Verlauf nicht umsetzen kann';

alter table T_VERLAUF_ABTEILUNG add NOT_POSSIBLE CHAR(1);
comment on COLUMN T_VERLAUF_ABTEILUNG.NOT_POSSIBLE 
  IS 'gesetztes Flag definiert, dass die Abteilung den Verlauf nicht umsetzen kann';

alter table T_VERLAUF_ABTEILUNG add NOT_POSSIBLE_REASON_REF_ID NUMBER(10);
comment on COLUMN T_VERLAUF_ABTEILUNG.NOT_POSSIBLE_REASON_REF_ID 
  IS 'Angabe eines Grundes, wieso ein Verlauf nicht realisiert werden kann';
CREATE INDEX IX_FK_VERLNOTPOS_2_REF ON T_VERLAUF_ABTEILUNG (NOT_POSSIBLE_REASON_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_VERLAUF_ABTEILUNG
  ADD CONSTRAINT FK_VERLNOTPOS_2_REF
      FOREIGN KEY (NOT_POSSIBLE_REASON_REF_ID)
      REFERENCES T_REFERENCE (ID);
commit;

-- Referenzen eintragen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1300, 'VERLAUF_REASONS', 'Ressourcenmangel (Hardware)', 1, 10, 
  'Definiert, dass ein Verlauf aus Hardware-Ressourcenmangel nicht realisierbar ist.');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1301, 'VERLAUF_REASONS', 'Ressourcenmangel (Personal)', 1, 20, 
  'Definiert, dass ein Verlauf aus Personal-Ressourcenmangel nicht realisierbar ist.');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1302, 'VERLAUF_REASONS', 'Fehlende Angaben im Auftrag', 1, 30, 
  'Definiert, dass ein Verlauf aufgrund fehlender Angaben im Auftrag nicht realisierbar ist.');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1303, 'VERLAUF_REASONS', 'Falsche Angaben im Auftrag', 1, 40, 
  'Definiert, dass ein Verlauf aufgrund falscher Angaben im Auftrag nicht realisierbar ist.');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1304, 'VERLAUF_REASONS', 'technisch nicht realisierbar', 1, 50, 
  'Definiert, dass ein Verlauf bzw. ein Auftrag technisch nicht realisierbar ist.');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (1305, 'VERLAUF_REASONS', 'Kunde verweigert Installation', 1, 60, 
  'Definiert, dass ein Auftrag nicht ausgefuehrt werden konnte, da der Kunde die Inst. verweigert.');
commit;



  
  
  
