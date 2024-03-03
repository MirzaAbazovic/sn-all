--
-- SQL-Script, das am Ende der Datenmigration ausgefuehrt werden muss.
--

ALTER TABLE T_EG_CONFIG change column EG2A_ID EG2A_ID INTEGER(9) NOT NULL;


ALTER TABLE t_eg_config
  ADD CONSTRAINT FK_IPCONFIG_2_EG2A
      FOREIGN KEY (EG2A_ID)
      REFERENCES t_eg_2_auftrag (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
   
