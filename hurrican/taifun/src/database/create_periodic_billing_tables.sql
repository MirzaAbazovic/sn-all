-- Erstellt einige billingrelevante Tabellen, so dass auch auf Datenbanken
-- ohne Billingdaten ein CDR-Accounting durchgefuehrt werden kann.

CREATE TABLE A_CDR_FILTERED_${var.billing.period}
(
  CDR_FILE_NO      NUMBER(10)                   NOT NULL,
  FILE_OFFSET      NUMBER(10)                   NOT NULL,
  CONN_ID          NUMBER(10),
  A_NUMBER         VARCHAR2(20 BYTE),
  B_NUMBER         VARCHAR2(18 BYTE),
  C_NUMBER         VARCHAR2(18 BYTE),
  A2_NUMBER        VARCHAR2(16 BYTE),
  ORIG_B_NUMBER    VARCHAR2(50 BYTE),
  CALL_DATETIME    VARCHAR2(17 BYTE),
  CALL_LENGTH      NUMBER(13,3),
  NO_ANSWER        CHAR(1 BYTE),
  FIRST_RECORD     CHAR(1 BYTE),
  LAST_RECORD      CHAR(1 BYTE),
  SERVICE_INFO     NUMBER(5)                    NOT NULL,
  CENTREX          CHAR(1 BYTE),
  VPN              NUMBER(10),
  CAUSE_VALUE      NUMBER(5),
  IND_INTERCONN    NUMBER(5),
  IND_SFC          NUMBER(10),
  TRUNK_GROUP_IN   CHAR(9 BYTE),
  TRUNK_GROUP_OUT  CHAR(9 BYTE),
  DURATION_DIAL    NUMBER(13,3),
  DURATION_ANSWER  NUMBER(13,3),
  NODE_CONN_NO     NUMBER(10),
  PRICE_NO         NUMBER(10),
  TOT_CALL_LENGTH  NUMBER(13,3),
  TAX_TIME         NUMBER(13,3),
  COST             NUMBER(18,2),
  TARIFF_TRANSIT   CHAR(1 BYTE),
  USER_DEF_1       VARCHAR2(255 BYTE),
  USER_DEF_2       VARCHAR2(255 BYTE),
  USER_DEF_3       VARCHAR2(255 BYTE),
  USER_DEF_4       VARCHAR2(255 BYTE),
  USER_DEF_5       VARCHAR2(255 BYTE),
  CUST_NO          NUMBER(10),
  AUFTRAG_NO       NUMBER(10),
  ERROR_DESC       VARCHAR2(255 BYTE)           NOT NULL
)
/



CREATE TABLE CA_CDR_FILTERED_${var.billing.period}
(
  CDR_FILE_NO      NUMBER(10)                   NOT NULL,
  FILE_OFFSET      NUMBER(10)                   NOT NULL,
  CONN_ID          NUMBER(10),
  A_NUMBER         VARCHAR2(31 BYTE),
  B_NUMBER         VARCHAR2(31 BYTE),
  ORIG_B_NUMBER    VARCHAR2(50 BYTE),
  CALL_DATETIME    CHAR(14 BYTE),
  CALL_LENGTH      NUMBER(10),
  TRUNK_GROUP      CHAR(9 BYTE),
  DIRECTION        CHAR(3 BYTE)                 NOT NULL,
  NO_ANSWER        CHAR(1 BYTE),
  FIRST_RECORD     CHAR(1 BYTE),
  LAST_RECORD      CHAR(1 BYTE),
  SERVICE_INFO     NUMBER(3),
  CATEGORY         NUMBER(3),
  CAUSE_VALUE      NUMBER(5),
  DURATION_DIAL    NUMBER(5),
  DURATION_ANSWER  NUMBER(5),
  CAC_TYPE         NUMBER(3),
  CAC_NUMBER       VARCHAR2(10 BYTE),
  POI_NO           NUMBER(10),
  CONN_NO          NUMBER(10),
  PRICE_NO         NUMBER(10),
  TOT_CALL_LENGTH  NUMBER(13,3),
  TAX_TIME         NUMBER(10),
  COST             NUMBER(18,2),
  TARIFF_TRANSIT   CHAR(1 BYTE),
  USER_DEF_1       VARCHAR2(255 BYTE),
  USER_DEF_2       VARCHAR2(255 BYTE),
  USER_DEF_3       VARCHAR2(255 BYTE),
  USER_DEF_4       VARCHAR2(255 BYTE),
  USER_DEF_5       VARCHAR2(255 BYTE),
  ERROR_DESC       VARCHAR2(255 BYTE)           NOT NULL
)
/



CREATE TABLE A_CDR_OK_${var.billing.period}_0
(
  CDR_FILE_NO      NUMBER(10)                   NOT NULL,
  FILE_OFFSET      NUMBER(10)                   NOT NULL,
  CONN_ID          NUMBER(10)                   NOT NULL,
  A_NUMBER         VARCHAR2(20 BYTE)            NOT NULL,
  B_NUMBER         VARCHAR2(18 BYTE)            NOT NULL,
  C_NUMBER         VARCHAR2(18 BYTE),
  A2_NUMBER        VARCHAR2(16 BYTE),
  ORIG_B_NUMBER    VARCHAR2(50 BYTE),
  CALL_DATETIME    VARCHAR2(17 BYTE)            NOT NULL,
  CALL_LENGTH      NUMBER(13,3)                 NOT NULL,
  NO_ANSWER        CHAR(1 BYTE)                 NOT NULL,
  FIRST_RECORD     CHAR(1 BYTE)                 NOT NULL,
  LAST_RECORD      CHAR(1 BYTE)                 NOT NULL,
  SERVICE_INFO     NUMBER(5)                    NOT NULL,
  CENTREX          CHAR(1 BYTE),
  VPN              NUMBER(10),
  CAUSE_VALUE      NUMBER(5),
  IND_INTERCONN    NUMBER(5),
  IND_SFC          NUMBER(10),
  TRUNK_GROUP_IN   CHAR(9 BYTE),
  TRUNK_GROUP_OUT  CHAR(9 BYTE),
  DURATION_DIAL    NUMBER(13,3),
  DURATION_ANSWER  NUMBER(13,3),
  NODE_CONN_NO     NUMBER(10)                   NOT NULL,
  PRICE_NO         NUMBER(10),
  TOT_CALL_LENGTH  NUMBER(13,3),
  TAX_TIME         NUMBER(13,3)                 NOT NULL,
  COST             NUMBER(18,2)                 NOT NULL,
  TARIFF_TRANSIT   CHAR(1 BYTE)                 NOT NULL,
  USER_DEF_1       VARCHAR2(255 BYTE),
  USER_DEF_2       VARCHAR2(255 BYTE),
  USER_DEF_3       VARCHAR2(255 BYTE),
  USER_DEF_4       VARCHAR2(255 BYTE),
  USER_DEF_5       VARCHAR2(255 BYTE),
  CUST_NO          NUMBER(10)                   NOT NULL,
  AUFTRAG_NO       NUMBER(10)                   NOT NULL,
  INTERNAL_RESULTS VARCHAR2(1000 BYTE)
)
/



CREATE TABLE CA_CDR_OK_${var.billing.period}_0
(
  CDR_FILE_NO      NUMBER(10)                   NOT NULL,
  FILE_OFFSET      NUMBER(10)                   NOT NULL,
  CONN_ID          NUMBER(10)                   NOT NULL,
  A_NUMBER         VARCHAR2(31 BYTE),
  B_NUMBER         VARCHAR2(31 BYTE),
  ORIG_B_NUMBER    VARCHAR2(50 BYTE),
  CALL_DATETIME    CHAR(14 BYTE)                NOT NULL,
  CALL_LENGTH      NUMBER(10)                   NOT NULL,
  TRUNK_GROUP      CHAR(9 BYTE)                 NOT NULL,
  DIRECTION        CHAR(3 BYTE)                 NOT NULL,
  NO_ANSWER        CHAR(1 BYTE)                 NOT NULL,
  FIRST_RECORD     CHAR(1 BYTE)                 NOT NULL,
  LAST_RECORD      CHAR(1 BYTE)                 NOT NULL,
  SERVICE_INFO     NUMBER(3)                    NOT NULL,
  CATEGORY         NUMBER(3),
  CAUSE_VALUE      NUMBER(5),
  DURATION_DIAL    NUMBER(5),
  DURATION_ANSWER  NUMBER(5),
  CAC_TYPE         NUMBER(3),
  CAC_NUMBER       VARCHAR2(10 BYTE),
  CONN_NO          NUMBER(10)                   NOT NULL,
  POI_NO           NUMBER(10)                   NOT NULL,
  PRICE_NO         NUMBER(10),
  TOT_CALL_LENGTH  NUMBER(13,3),
  TAX_TIME         NUMBER(10)                   NOT NULL,
  COST             NUMBER(18,2)                 NOT NULL,
  TARIFF_TRANSIT   CHAR(1 BYTE)                 NOT NULL,
  USER_DEF_1       VARCHAR2(255 BYTE),
  USER_DEF_2       VARCHAR2(255 BYTE),
  USER_DEF_3       VARCHAR2(255 BYTE),
  USER_DEF_4       VARCHAR2(255 BYTE),
  USER_DEF_5       VARCHAR2(255 BYTE)
)
/



CREATE TABLE A_CDR_OK_${var.billing.period}_0CALLS
(
  CDR_FILE_NO       NUMBER(10)                  NOT NULL,
  FILE_OFFSET       NUMBER(10)                  NOT NULL,
  CONN_ID           NUMBER(10)                  NOT NULL,
  A_NUMBER          VARCHAR2(20 BYTE)           NOT NULL,
  B_NUMBER          VARCHAR2(18 BYTE)           NOT NULL,
  C_NUMBER          VARCHAR2(18 BYTE),
  A2_NUMBER         VARCHAR2(16 BYTE),
  ORIG_B_NUMBER     VARCHAR2(50 BYTE),
  CALL_DATETIME     VARCHAR2(17 BYTE)           NOT NULL,
  CALL_LENGTH       NUMBER(13,3)                NOT NULL,
  NO_ANSWER         CHAR(1 BYTE)                NOT NULL,
  FIRST_RECORD      CHAR(1 BYTE)                NOT NULL,
  LAST_RECORD       CHAR(1 BYTE)                NOT NULL,
  SERVICE_INFO      NUMBER(5)                   NOT NULL,
  CENTREX           CHAR(1 BYTE),
  VPN               NUMBER(10),
  CAUSE_VALUE       NUMBER(5),
  IND_INTERCONN     NUMBER(5),
  IND_SFC           NUMBER(10),
  TRUNK_GROUP_IN    CHAR(9 BYTE),
  TRUNK_GROUP_OUT   CHAR(9 BYTE),
  DURATION_DIAL     NUMBER(13,3),
  DURATION_ANSWER   NUMBER(13,3),
  NODE_CONN_NO      NUMBER(10)                  NOT NULL,
  PRICE_NO          NUMBER(10),
  TOT_CALL_LENGTH   NUMBER(13,3),
  TAX_TIME          NUMBER(13,3)                NOT NULL,
  COST              NUMBER(18,2)                NOT NULL,
  TARIFF_TRANSIT    CHAR(1 BYTE)                NOT NULL,
  USER_DEF_1        VARCHAR2(255 BYTE),
  USER_DEF_2        VARCHAR2(255 BYTE),
  USER_DEF_3        VARCHAR2(255 BYTE),
  USER_DEF_4        VARCHAR2(255 BYTE),
  USER_DEF_5        VARCHAR2(255 BYTE),
  CUST_NO           NUMBER(10)                  NOT NULL,
  AUFTRAG_NO        NUMBER(10)                  NOT NULL,
  INTERNAL_RESULTS  VARCHAR2(1000 BYTE)
)
/



CREATE TABLE BIE_BILL_${var.billing.period}
(
  BILL_NO                NUMBER(10)             NOT NULL,
  RUN_NO                 NUMBER(10)             NOT NULL,
  BILL_ID                VARCHAR2(20 BYTE),
  INVOICE_DATE           DATE                   NOT NULL,
  CUSTOMER_NO            NUMBER(10)             NOT NULL,
  CUSTOMER_ACCOUNT_NO    NUMBER(10)             NOT NULL,
  BILL_SPEC_NO           NUMBER(10)             NOT NULL,
  CHARGED_FROM           DATE                   NOT NULL,
  CHARGED_TO             DATE                   NOT NULL,
  AMOUNT                 NUMBER(18,2)           NOT NULL,
  CURRENCY_ID            CHAR(3 BYTE)           NOT NULL,
  VAT_EXEMPTION_CODE_ID  VARCHAR2(10 BYTE),
  PAYMENT_METHOD         VARCHAR2(5 BYTE)       NOT NULL,
  PAYMENT_PERIOD         NUMBER(10)             DEFAULT 0                     NOT NULL,
  BIC                    VARCHAR2(11 BYTE),
  IBAN                   VARCHAR2(42 BYTE),
  BANK_CLEARING_NO       VARCHAR2(10 BYTE),
  BANK_ACCOUNT_NO        VARCHAR2(25 BYTE),
  BANK_ACCOUNT_HOLDER    VARCHAR2(100 BYTE),
  MANDATE_REF            VARCHAR2(35 BYTE),
  STATE                  VARCHAR2(20 BYTE)      DEFAULT 'CREATED'             NOT NULL,
  REMAINING_AMOUNT       NUMBER(18,2)           NOT NULL,
  PAID_ON                DATE,
  RELEASED_AT            DATE,
  RELEASED_BY            NUMBER(10),
  DUNNING_LEVEL          NUMBER(3)              DEFAULT 0                     NOT NULL,
  DUNNING_LEVEL_SINCE    DATE,
  DUNNED_L1_AT           DATE,
  DUNNED_L1_BY           NUMBER(10),
  DUNNED_L2_AT           DATE,
  DUNNED_L2_BY           NUMBER(10),
  CANCEL_ID              VARCHAR2(20 BYTE),
  CANCEL_CAUSE_NO        NUMBER(10),
  CANCELLED_AT           DATE,
  CANCELLED_BY           NUMBER(10),
  WRITE_OFF_ID           VARCHAR2(20 BYTE),
  WRITE_OFF_CAUSE_NO     NUMBER(10),
  WRITTEN_OFF_AT         DATE,
  WRITTEN_OFF_BY         NUMBER(10),
  REMARK                 VARCHAR2(500 BYTE),
  USERW                  VARCHAR2(32 BYTE)      NOT NULL,
  DATEW                  DATE                   NOT NULL,
  VAT_REG_NO             VARCHAR2(20 BYTE),
  GEO_COUNTRY_ID         CHAR(2 BYTE),
  FACTORY_INLAND         CHAR(1 BYTE)
)
/


CREATE INDEX I_BIE_BILL_${var.billing.period}_1 ON BIE_BILL_${var.billing.period}
(BILL_ID)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


CREATE INDEX I_BIE_BILL_${var.billing.period}_2 ON BIE_BILL_${var.billing.period}
(CUSTOMER_NO)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


CREATE INDEX I_BIE_BILL_${var.billing.period}_3 ON BIE_BILL_${var.billing.period}
(BILL_SPEC_NO)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


CREATE INDEX I_BIE_BILL_${var.billing.period}_4 ON BIE_BILL_${var.billing.period}
(STATE, DUNNING_LEVEL)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


CREATE UNIQUE INDEX PK_BIE_BILL_${var.billing.period} ON BIE_BILL_${var.billing.period}
(BILL_NO)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


ALTER TABLE BIE_BILL_${var.billing.period} ADD (
  CONSTRAINT R_BIE_BILL_${var.billing.period}_11
  CHECK (PAYMENT_METHOD IN ('DD-B', 'DD-P', 'PS', 'BT', 'CC', 'CASH', 'INT', 'COST')),
  CONSTRAINT R_BIE_BILL_${var.billing.period}_17
  CHECK (STATE IN ('CREATED', 'RELEASABLE', 'RELEASING', 'RELEASED', 'TO_DUN', 'DUNNED', 'REOPENED', 'IN_DISPUTE', 'PAID', 'CLOSED', 'CANCELLED', 'WRITTEN_OFF')),
  CONSTRAINT PK_BIE_BILL_${var.billing.period}
  PRIMARY KEY
  (BILL_NO)
  USING INDEX PK_BIE_BILL_${var.billing.period})
/

ALTER TABLE BIE_BILL_${var.billing.period} ADD (
  CONSTRAINT FK_BIE_BILL_${var.billing.period}_0
  FOREIGN KEY (RUN_NO)
  REFERENCES BIE_RUN (RUN_NO),
  CONSTRAINT FK_BIE_BILL_${var.billing.period}_4
  FOREIGN KEY (RELEASED_BY)
  REFERENCES PERSON (PERSON_NO),
  CONSTRAINT FK_BIE_BILL_${var.billing.period}_5
  FOREIGN KEY (DUNNED_L1_BY)
  REFERENCES PERSON (PERSON_NO),
  CONSTRAINT FK_BIE_BILL_${var.billing.period}_6
  FOREIGN KEY (DUNNED_L2_BY)
  REFERENCES PERSON (PERSON_NO))
/




CREATE TABLE BIE_BILL_ITEM_${var.billing.period}
(
  BILL_ITEM_NO         NUMBER(10)               NOT NULL,
  BILL_NO              NUMBER(10)               NOT NULL,
  ORIG_RUN_NO          NUMBER(10)               NOT NULL,
  ITEM_TYPE_NO         NUMBER(10)               NOT NULL,
  CUSTOMER_NO          NUMBER(10)               NOT NULL,
  SERVICE__NO          NUMBER(10)               NOT NULL,
  PRODUCT__NO          NUMBER(10)               NOT NULL,
  SERVICE_ITEM__NO     NUMBER(10),
  PRODUCT_ITEM_NO      NUMBER(10),
  DISCOUNT_NO          NUMBER(10),
  CHARGED_FROM         DATE,
  CHARGED_TO           DATE,
  QUANTITY             NUMBER(18,4),
  PRICE                NUMBER(18,4),
  AMOUNT               NUMBER(18,2)             NOT NULL,
  CURRENCY_ID          CHAR(3 BYTE)             NOT NULL,
  VAT_CODE_ID          VARCHAR2(10 BYTE),
  VAT_RATE             NUMBER(18,2),
  MNET_NO_CALLS        NUMBER(10),
  MNET_GROUP           VARCHAR2(50 BYTE),
  MNET_PRICE_TYPE_NO   NUMBER(10),
  MNET_CPRICE_TYPE_NO  NUMBER(10),
  MNET_AMOUNT_DIS      NUMBER(18,2)             NOT NULL,
  MNET_AMOUNT_NET      NUMBER(18,2)             NOT NULL
)
/


CREATE INDEX I_BIE_BILL_ITEM_${var.billing.period}_1 ON BIE_BILL_ITEM_${var.billing.period}
(BILL_NO)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


CREATE INDEX I_BIE_BILL_ITEM_${var.billing.period}_2 ON BIE_BILL_ITEM_${var.billing.period}
(CUSTOMER_NO)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


CREATE INDEX I_BIE_BILL_ITEM_${var.billing.period}_3 ON BIE_BILL_ITEM_${var.billing.period}
(SERVICE__NO)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


CREATE INDEX I_BIE_BILL_ITEM_${var.billing.period}_4 ON BIE_BILL_ITEM_${var.billing.period}
(DISCOUNT_NO)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


CREATE UNIQUE INDEX PK_BIE_BILL_ITEM_${var.billing.period} ON BIE_BILL_ITEM_${var.billing.period}
(BILL_ITEM_NO)
LOGGING
TABLESPACE TAIFUN_INDEX_DYN
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


ALTER TABLE BIE_BILL_ITEM_${var.billing.period} ADD (
  CONSTRAINT PK_BIE_BILL_ITEM_${var.billing.period}
  PRIMARY KEY
  (BILL_ITEM_NO)
  USING INDEX PK_BIE_BILL_ITEM_${var.billing.period})
/



CREATE TABLE DEBI_TRANS_${var.billing.period}
(
  TRANSACTION_NO    NUMBER(10)                  NOT NULL,
  TRANSACTION_ID    VARCHAR2(20 BYTE)           NOT NULL,
  TRANSACTION_DATE  DATE                        NOT NULL,
  BILL_NO           NUMBER(10)                  NOT NULL,
  STATE             VARCHAR2(20 BYTE)           NOT NULL,
  TYPE              VARCHAR2(15 BYTE)           NOT NULL,
  CAUSE_NO          NUMBER(10),
  AMOUNT            NUMBER(18,2)                NOT NULL,
  CURRENCY_ID       CHAR(3 BYTE)                NOT NULL,
  OPENED_BY         NUMBER(10)                  NOT NULL,
  OPENED_AT         DATE                        NOT NULL,
  RELEASED_BY       NUMBER(10),
  RELEASED_AT       DATE,
  ARCH_SET_ID       VARCHAR2(100 BYTE),
  FREE_TEXT         VARCHAR2(64 BYTE),
  USERW             VARCHAR2(32 BYTE)           NOT NULL,
  DATEW             DATE                        NOT NULL
)
/


CREATE UNIQUE INDEX PK_DEBI_TRANS_${var.billing.period} ON DEBI_TRANS_${var.billing.period}
(TRANSACTION_NO)
LOGGING
TABLESPACE TAIFUN_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


ALTER TABLE DEBI_TRANS_${var.billing.period} ADD (
  CONSTRAINT R_DEBI_TRANS_${var.billing.period}_4
  CHECK (STATE IN ('OPENED', 'RELEASED')),
  CONSTRAINT R_DEBI_TRANS_${var.billing.period}_5
  CHECK (TYPE IN ('WRITE_OFF', 'CANCEL', 'CREDIT_NOTE', 'DEBIT_NOTE')),
  CONSTRAINT PK_DEBI_TRANS_${var.billing.period}
  PRIMARY KEY
  (TRANSACTION_NO)
  USING INDEX PK_DEBI_TRANS_${var.billing.period})
/

ALTER TABLE DEBI_TRANS_${var.billing.period} ADD (
  CONSTRAINT FK_DEBI_TRANS_${var.billing.period}_0
  FOREIGN KEY (BILL_NO)
  REFERENCES BIE_BILL_${var.billing.period} (BILL_NO),
  CONSTRAINT FK_DEBI_TRANS_${var.billing.period}_1
  FOREIGN KEY (CAUSE_NO)
  REFERENCES DEBI_CANCEL_CAUSE (CAUSE_NO),
  CONSTRAINT FK_DEBI_TRANS_${var.billing.period}_2
  FOREIGN KEY (CURRENCY_ID)
  REFERENCES UTI_CURRENCY (CURRENCY_ID),
  CONSTRAINT FK_DEBI_TRANS_${var.billing.period}_3
  FOREIGN KEY (OPENED_BY)
  REFERENCES PERSON (PERSON_NO),
  CONSTRAINT FK_DEBI_TRANS_${var.billing.period}_4
  FOREIGN KEY (RELEASED_BY)
  REFERENCES PERSON (PERSON_NO))
/



  CREATE TABLE DEBI_TRANS_ITEM_${var.billing.period}
(
  TRANSACTION_ITEM_NO  NUMBER(10)               NOT NULL,
  TRANSACTION_NO       NUMBER(10)               NOT NULL,
  ITEM_TYPE_NO         NUMBER(10)               NOT NULL,
  BILL_ITEM_NO         NUMBER(10),
  AMOUNT               NUMBER(18,2)             NOT NULL,
  CURRENCY_ID          CHAR(3 BYTE)             NOT NULL,
  QUANTITY             NUMBER(18,4),
  FREE_TEXT            VARCHAR2(64 BYTE),
  DATEW                DATE                     NOT NULL,
  USERW                VARCHAR2(32 BYTE)        NOT NULL
)
/


CREATE UNIQUE INDEX PK_DEBI_TRANS_ITEM_${var.billing.period} ON DEBI_TRANS_ITEM_${var.billing.period}
(TRANSACTION_ITEM_NO)
LOGGING
TABLESPACE TAIFUN_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


ALTER TABLE DEBI_TRANS_ITEM_${var.billing.period} ADD (
  CONSTRAINT PK_DEBI_TRANS_ITEM_${var.billing.period}
  PRIMARY KEY
  (TRANSACTION_ITEM_NO)
  USING INDEX PK_DEBI_TRANS_ITEM_${var.billing.period})
/

ALTER TABLE DEBI_TRANS_ITEM_${var.billing.period} ADD (
  CONSTRAINT FK_DEBI_TRANS_ITEM_${var.billing.period}_0
  FOREIGN KEY (TRANSACTION_NO)
  REFERENCES DEBI_TRANS_${var.billing.period} (TRANSACTION_NO),
  CONSTRAINT FK_DEBI_TRANS_ITEM_${var.billing.period}_1
  FOREIGN KEY (ITEM_TYPE_NO)
  REFERENCES BIE_ITEM_TYPE (ITEM_TYPE_NO),
  CONSTRAINT FK_DEBI_TRANS_ITEM_${var.billing.period}_2
  FOREIGN KEY (BILL_ITEM_NO)
  REFERENCES BIE_BILL_ITEM_${var.billing.period} (BILL_ITEM_NO),
  CONSTRAINT FK_DEBI_TRANS_ITEM_${var.billing.period}_3
  FOREIGN KEY (CURRENCY_ID)
  REFERENCES UTI_CURRENCY (CURRENCY_ID))
/



CREATE TABLE A_PRICE_SUM_${var.billing.period}
(
  CUST_NO              NUMBER(10)               NOT NULL,
  AUFTRAG_NO           NUMBER(10)               NOT NULL,
  NODE_CONN_NO         NUMBER(10)               NOT NULL,
  PRICE_TYPE_NO        NUMBER(10)               NOT NULL,
  ORIG_PERIOD          NUMBER(10)               NOT NULL,
  COST_SUM             NUMBER(18,2)             NOT NULL,
  CALL_LENGTH_SUM      NUMBER(19,3)             NOT NULL,
  TAX_TIME_SUM         NUMBER(19,3)             NOT NULL,
  NO_CALLS             NUMBER(16)               NOT NULL,
  GROUP_QUALIFIER      VARCHAR2(40 BYTE),
  FREE_TIME_CONFIG_NO  NUMBER(10)
)
/


CREATE TABLE CA_PRICE_SUM_${var.billing.period}
(
  CUST_NO          NUMBER(10)                   NOT NULL,
  AUFTRAG_NO       NUMBER(10)                   NOT NULL,
  CONN_NO          NUMBER(10)                   NOT NULL,
  POI_NO           NUMBER(10)                   NOT NULL,
  RULE_SIGN        CHAR(1 BYTE)                 NOT NULL,
  PRICE_TYPE_NO    NUMBER(10)                   NOT NULL,
  ORIG_PERIOD      NUMBER(10)                   NOT NULL,
  GROUP_QUALIFIER  VARCHAR2(20 BYTE),
  COST_SUM         NUMBER(18,2)                 NOT NULL,
  CALL_LENGTH_SUM  NUMBER(16)                   NOT NULL,
  TAX_TIME_SUM     NUMBER(16)                   NOT NULL,
  NO_CALLS         NUMBER(16)                   NOT NULL
)
/



CREATE TABLE UDR_OK_${var.billing.period}
(
  SOURCE_SYSTEM_ID   VARCHAR2(10 BYTE)          NOT NULL,
  SS_FILE_NO         NUMBER(10)                 NOT NULL,
  SS_REC_NO          NUMBER(10)                 NOT NULL,
  SS_UDR_ID          VARCHAR2(100 BYTE)         DEFAULT '0'                   NOT NULL,
  SS_UDR_DATETIME    DATE                       NOT NULL,
  SS_ORDER_ID        VARCHAR2(100 BYTE)         NOT NULL,
  SS_SERVICE_ID      VARCHAR2(20 BYTE)          NOT NULL,
  SS_BMONTH          CHAR(6 BYTE)               NOT NULL,
  SS_QUANTITY        NUMBER(18,4),
  SS_QUANTITY_UNIT   VARCHAR2(10 BYTE),
  SS_COST            NUMBER(18,4),
  SS_COST_UNIT       VARCHAR2(10 BYTE),
  UDR_BATCH_NO       NUMBER(10)                 NOT NULL,
  UDR_ORDER_NO       NUMBER(10)                 NOT NULL,
  UDR_SERVICE_CODE   VARCHAR2(16 BYTE)          NOT NULL,
  UDR_QUANTITY       NUMBER(18,4),
  UDR_QUANTITY_UNIT  VARCHAR2(10 BYTE),
  UDR_COST           NUMBER(18,4),
  UDR_COST_UNIT      VARCHAR2(10 BYTE),
  USER_DEF_1         VARCHAR2(255 BYTE),
  USER_DEF_2         VARCHAR2(255 BYTE),
  USER_DEF_3         VARCHAR2(255 BYTE),
  USER_DEF_4         VARCHAR2(255 BYTE),
  USER_DEF_5         VARCHAR2(255 BYTE)
)
/


CREATE UNIQUE INDEX PK_UDR_OK_${var.billing.period} ON UDR_OK_${var.billing.period}
(SS_FILE_NO, SS_REC_NO)
LOGGING
TABLESPACE TAIFUN_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


CREATE UNIQUE INDEX UK_UDR_OK_${var.billing.period}_1 ON UDR_OK_${var.billing.period}
(SOURCE_SYSTEM_ID, SS_FILE_NO, SS_UDR_ID, SS_UDR_DATETIME, SS_BMONTH,
SS_ORDER_ID, SS_SERVICE_ID)
LOGGING
TABLESPACE TAIFUN_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/


ALTER TABLE UDR_OK_${var.billing.period} ADD (
  CONSTRAINT PK_UDR_OK_${var.billing.period}
  PRIMARY KEY
  (SS_FILE_NO, SS_REC_NO)
  USING INDEX PK_UDR_OK_${var.billing.period},
  CONSTRAINT UK_UDR_OK_${var.billing.period}_1
  UNIQUE (SOURCE_SYSTEM_ID, SS_FILE_NO, SS_UDR_ID, SS_UDR_DATETIME, SS_BMONTH, SS_ORDER_ID, SS_SERVICE_ID)
  USING INDEX UK_UDR_OK_${var.billing.period}_1)
/
