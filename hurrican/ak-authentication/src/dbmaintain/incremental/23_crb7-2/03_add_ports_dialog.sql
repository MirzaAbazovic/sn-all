INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragDISPOPanel', 'Button', 'Button, um die Bauauftrags-Ports anzuzeigen(aus DISPO).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragStConnectPanel', 'Button', 'Button, um die Ports zu einem Bauauftrag anzuzeigen (aus ST Connect).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragStVoicePanel', 'Button', 'Button, um die Ports zu einem Bauauftrag anzuzeigen (ST Voice).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragFieldServicePanel', 'Button', 'Button, um die Ports zu einem Bauauftrag anzuzeigen (FieldService).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragStOnlinePanel', 'Button', 'Button, um die Ports zu einem Bauauftrag anzuzeigen (aus ST Online).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragDispoRLPanel', 'Button', 'Button, um die Ports zu einem Bauauftrag anzuzeigen (aus DISPO RL).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragAmRlPanel', 'Button', 'Button, um die Ports zu einem Bauauftrag anzuzeigen (AM RL).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoPanel', 'Button', 'Button, um die Ports zu einer Projektierung anzuzeigen (DISPO).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.ProjektierungFieldServicePanel', 'Button', 'Button, um die Ports zu einer Projektierung anzuzeigen (FieldService).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.ProjektierungStConnectPanel', 'Button', 'Button, um die Ports zu einer Projektierung anzuzeigen (ST Connect).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.ProjektierungStOnlinePanel', 'Button', 'Button, um die Ports zu einer Projektierung anzuzeigen (ST Online).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.ProjektierungDispoRLPanel', 'Button', 'Button, um die Ports zu einer Projektierung anzuzeigen (RL DISPO).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.ProjektierungAmRlPanel', 'Button', 'Button, um die Ports zu einer Projektierung anzuzeigen (RL AM).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragEXTPanel', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.ProjektierungStVoicePanel', 'Button', 'Button, um die Ports zu einer Projektierung anzuzeigen (ST Voice).', 1, 0);
INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'show.ports', 'de.augustakom.hurrican.gui.verlauf.BauauftragMQueuePanel', 'Button', 'Button, um die Ports zu einem Bauauftrag anzuzeigen (aus M-Queue).', 1, 0);

INSERT INTO COMPBEHAVIOR
   SELECT S_COMPBEHAVIOR_0.NEXTVAL AS ID, ID AS COMP_ID, 20 AS ROLE_ID, 1 AS VISIBLE, 1 AS EXECUTABLE, 0 AS VERSION
     FROM GUICOMPONENT GC WHERE GC.NAME = 'show.ports';
