--
-- SQL-Script, um eine Sub-Orders Tabelle fuer die CPS-Transaktionen anzulegen
--

CREATE TABLE T_CPS_TX_SUB_ORDERS (
       ID NUMBER(10) NOT NULL
     , CPS_TX_ID NUMBER(10) NOT NULL
     , AUFTRAG_ID NUMBER(10) NOT NULL
     , VERLAUF_ID NUMBER(10)
);

COMMENT ON TABLE T_CPS_TX_SUB_ORDERS 
  IS 'Tabelle in der Sub-Auftraege zu einer TX prototkolliert werden (z.B. fuer TK-Anlagen mit mehreren Hurrican-Auftraegen)';

COMMENT ON COLUMN T_CPS_TX_SUB_ORDERS.CPS_TX_ID IS 'ID der zugehoerigen CPS-Transaction';  
COMMENT ON COLUMN T_CPS_TX_SUB_ORDERS.AUFTRAG_ID 
  IS 'ID eines weiteren Hurrican-Auftrags, der mit der angegebenen CPS_TX_ID provisioniert werden muss';  
COMMENT ON COLUMN T_CPS_TX_SUB_ORDERS.CPS_TX_ID 
  IS 'optional. Angabe der Verlaufs-ID des Auftrags';  
  
ALTER TABLE T_CPS_TX_SUB_ORDERS ADD CONSTRAINT PK_T_CPS_TX_SUB_ORDERS PRIMARY KEY (ID);

CREATE INDEX IX_FK_CPSTXSUB_2_CPSTX ON T_CPS_TX_SUB_ORDERS (CPS_TX_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_CPS_TX_SUB_ORDERS ADD CONSTRAINT FK_CPSTXSUB_2_CPSTX
  FOREIGN KEY (CPS_TX_ID) REFERENCES T_CPS_TX (ID);
commit;


create SEQUENCE S_T_CPS_TX_SUB_ORDERS_0 start with 1;
grant select on S_T_CPS_TX_SUB_ORDERS_0 to public;

GRANT SELECT, INSERT, UPDATE ON T_CPS_TX_SUB_ORDERS TO R_HURRICAN_USER;
GRANT SELECT ON T_CPS_TX_SUB_ORDERS TO R_HURRICAN_READ_ONLY;

update T_PRODUKT_MAPPING set MAPPING_PART_TYPE='sdsl_sub' where PROD_ID=99;

