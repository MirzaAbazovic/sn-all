ALTER TABLE T_MWF_REQUEST MODIFY(VERSION  DEFAULT NULL);
ALTER TABLE T_MWF_REQUEST ADD (MWF_CREATION_DATE DATE DEFAULT sysdate NOT NULL);
ALTER TABLE T_MWF_REQUEST ADD (EXTERNE_AUFTRAGSNUMMER VARCHAR2(20) DEFAULT 0 NOT NULL);