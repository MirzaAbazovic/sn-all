-- Add column for termin - nullable because storno and auftrag don't have one
ALTER TABLE T_MWF_REQUEST ADD (TERMIN timestamp);