INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
     VALUES (S_GUICOMPONENT_0.nextval, 'save', 'de.augustakom.hurrican.gui.auftrag.vpn.VPNVertragsUebersichtPanel', 'Button', 'Speichern von bearbeiteten VPNs', 1, 0);

INSERT INTO COMPBEHAVIOR
   SELECT S_COMPBEHAVIOR_0.NEXTVAL AS ID, ID AS COMP_ID, 30 AS ROLE_ID, 1 AS VISIBLE, 1 AS EXECUTABLE, 0 AS VERSION
     FROM GUICOMPONENT GC WHERE GC.NAME = 'save' and GC.PARENT = 'de.augustakom.hurrican.gui.auftrag.vpn.VPNVertragsUebersichtPanel';
