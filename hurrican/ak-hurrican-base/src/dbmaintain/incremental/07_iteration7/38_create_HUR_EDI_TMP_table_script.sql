----------------------------------------------------------
-- tmp edi table
----------------------------------------------------------
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE T_EDI_KUP_TMP';
EXCEPTION
  WHEN OTHERS
    THEN
  NULL;
END;
/
CREATE TABLE T_EDI_KUP_TMP
(
  EDI_ID                         NUMBER(11),   
  EDT_ID                         NUMBER(11)     NOT NULL,
  EDF_MONAT                      DATE           NOT NULL,
  EDF_RECHNUNGSDATUM             DATE           NOT NULL,
  EDF_BETRAG_MOA9                NUMBER(11,2)   NOT NULL,
  EDF_BETRAG_MOA77               NUMBER(11,2)   NOT NULL,
  EDF_BETRAG_MOA79               NUMBER(11,2)   NOT NULL,
  EDF_BETRAG_MOA124              NUMBER(11,2)   NOT NULL,
  EDF_BETRAG_MOA125              NUMBER(11,2)   NOT NULL,
  EDF_NAME                       VARCHAR2(255) NOT NULL,
  EDF_TEXT1                      VARCHAR2(255),
  EDF_TEXT2                      VARCHAR2(255),
  T_DTAG_RECHNUNG_RECH_ID        NUMBER(11),
  EDF_ABRECHNUNG_ART             VARCHAR2(10),
  EDF_DATEITYP                   VARCHAR2(10),
  EDF_ABRECHNUNG_BIS             DATE,
  EDT_EDF_ID                     NUMBER(11)     NOT NULL,
  EDT_EDA_ID                     NUMBER(11)     NOT NULL,
  EDT_ETA_ID                     NUMBER(11)     NOT NULL,
  EDT_ETY_ID                     NUMBER(11)     NOT NULL,
  EDT_EAL_ID                     NUMBER(11),
  EAL_NR_ELFE                    VARCHAR2(10) NOT NULL,
  EDT_VON                        DATE           NOT NULL,
  EDT_BIS                        DATE           NOT NULL,
  EDT_ANZ                        NUMBER(11)     NOT NULL,
  EDT_BETRAG_POS                 NUMBER(11,2)   NOT NULL,
  EDT_BETRAG                     NUMBER(11,2)   NOT NULL,
  LBZ_COMP                       VARCHAR2(50)   NOT NULL,
  EDT_LEITUNGSNR                 VARCHAR2(50)   NOT NULL,
  EDT_VERTRAGSNR                 VARCHAR2(50),
  EDT_REFERENZ                   VARCHAR2(50)   NOT NULL,
  EDT_NAME                       VARCHAR2(255)  NOT NULL,
  EDT_TEXT1                      VARCHAR2(255),
  EDT_TEXT2                      VARCHAR2(255),
  EDT_LEITUNGSNR_ORI             VARCHAR2(50)   NOT NULL,
  EDT_ENDSTELLE_A                VARCHAR2(50),
  EDT_ENDSTELLE_B                VARCHAR2(50),
  EDT_EXTERNE_AUFTRAGSNUMMER     VARCHAR2(50),
  EDT_STOERUNGSNUMMER            VARCHAR2(50),
  EDT_STOERUNGSDATUM             DATE,
  EDT_GUTSCHRIFT_VERTRAGSNUMMER  VARCHAR2(50),
  EDT_MATERIALNUMMER             VARCHAR2(50),
  EDT_KONDITIONSID               VARCHAR2(50),
  EDT_VERTRAGSNUMMER             VARCHAR2(50),
  T_DTAG_TAL_RECH_ID             NUMBER(11),
  T_DTAG_TAL_ID                  NUMBER(11),
  EAL_KONDITIONS_ID              VARCHAR2(20),
  EAL_ID                         NUMBER(11),
  EAL_NR                         VARCHAR2(10),
  EAL_RECHNUNGSTEXT              VARCHAR2(100),
  EAL_TEXT                       VARCHAR2(127),
  LBZ_ONKZ                       VARCHAR2(5),
  EDI_IMPORT_TS              DATE         default sysdate              
)
TABLESPACE T_HURRICAN
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOLOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING
/
ALTER TABLE T_EDI_KUP_TMP
ADD PRIMARY KEY  (EDI_ID )  USING INDEX TABLESPACE I_HURRICAN
/
commit
/
BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_TV1KUP_TCB_AKT';
EXCEPTION
  WHEN OTHERS
    THEN
  NULL;
END;
/
CREATE SEQUENCE SEQ_TV1KUP_TCB_AKT
INCREMENT BY 1 START WITH 1000
/
CREATE OR REPLACE TRIGGER TRG_T_EDI_KUP_TMP
BEFORE INSERT OR UPDATE ON T_EDI_KUP_TMP
FOR EACH ROW
DECLARE 
    iCounter T_EDI_KUP_TMP.EDI_ID%TYPE; 
    cannot_change_counter EXCEPTION; 
BEGIN 
    IF INSERTING THEN 
        IF :new.EDI_ID is NULL THEN 
            Select SEQ_TV1KUP_TCB_AKT.NEXTVAL INTO iCounter FROM Dual; 
           :new.EDI_ID := iCounter; 
        END IF;
    END IF; 

    IF UPDATING THEN 
        IF NOT (:new.EDI_ID = :old.EDI_ID) THEN 
            RAISE cannot_change_counter; 
        END IF; 
    END IF; 
EXCEPTION 
     WHEN cannot_change_counter THEN 
         raise_application_error(-20000, 'Cannot Change Counter Value'); 
END; 
/
commit
/
CREATE INDEX I1T_EDI_KUP_TMP ON T_EDI_KUP_TMP
(EDT_LEITUNGSNR)
NOLOGGING
TABLESPACE I_HURRICAN
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
