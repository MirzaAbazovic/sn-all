CREATE TABLE T_AUFTRAG_VOIPDN_2_EG_PORT (
  id               NUMBER(19)           NOT NULL,
  auftragvoipdn_id NUMBER(19)           NOT NULL,
  egport_id        NUMBER(19)           NOT NULL,
  aktiv_von        DATE                 NOT NULL,
  aktiv_bis        DATE                 NOT NULL,
  version          NUMBER(19) DEFAULT 0 NOT NULL,
  CONSTRAINT PK_T_AUFTRAG_VOIPDN_2_EG_PORT PRIMARY KEY (id),
  CONSTRAINT FK_A2EGP_AUFTRAG_VOIPDN FOREIGN KEY (auftragvoipdn_id) REFERENCES T_AUFTRAG_VOIP_DN (id),
  CONSTRAINT FK_A2EG_EG_PORT FOREIGN KEY (egport_id) REFERENCES T_EG_PORT (id)
);

CREATE SEQUENCE S_T_AUFTRAG_VOIPDN_2_EG_PORT_0 START WITH 1 INCREMENT BY 1;
GRANT SELECT, INSERT, UPDATE ON T_AUFTRAG_VOIPDN_2_EG_PORT TO R_HURRICAN_USER;
GRANT SELECT ON T_AUFTRAG_VOIPDN_2_EG_PORT TO R_HURRICAN_READ_ONLY;
GRANT SELECT ON S_T_AUFTRAG_VOIPDN_2_EG_PORT_0 TO PUBLIC;
