--
-- SQL-Script, das nach der gesamten Datenuebernahme nach Oracle 
-- ausgefuehrt werden muss.
--

ALTER TABLE T_SERVICE_COMMAND_MAPPING
  ADD CONSTRAINT FK_SCMD_2_MAPPING
      FOREIGN KEY (COMMAND_ID)
      REFERENCES t_service_commands (ID)
;



ALTER TABLE T_LEISTUNG_DN ADD CONSTRAINT UK_T_LEISTUNG_DN_1 UNIQUE (DN_NO, LEISTUNGSBUENDEL_ID, LEISTUNG4DN_ID, SCV_REALISIERUNG, PARAMETER_ID)
/




	