-- Rename auftrag table
ALTER TABLE T_MWF_AUFTRAG RENAME TO T_MWF_REQUEST;

-- Add discriminator column
ALTER TABLE T_MWF_REQUEST ADD (REQUESTTYPE VARCHAR2(10) DEFAULT 'Auftrag');
ALTER TABLE T_MWF_REQUEST MODIFY (REQUESTTYPE NOT NULL);

-- Rename sequence
RENAME S_T_MWF_AUFTRAG_0 TO S_T_MWF_REQUEST_0;