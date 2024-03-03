-- Verrechtung der Menüeinträge für die Einzeige der XML-Requests/XML-Responses. --
-- Die Rechte sind wie folgt vergeben: --
-- "XML-Request anzeigen" und "XML-Reponse anzeigen" sichtbar für: Administrator, Superuser
-- (HURRICAN_AM_SUPERUSER) und AM-Bearbeiter (HURRICAN_AM_BEARBEITER)
-- "Komplettes Request XML anzeigen" und "Komplettes Response XML anzeigen" nur für:
-- Administrator und Superuser. --

Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'action.showXMLRequest', 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'action.showXMLRequest'
                                  and parent = 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog'),
   82, '1', '1', 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'action.showXMLRequest'
                                  and parent = 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog'),
   81, '1', '1', 0);


Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'action.showXMLResponse', 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'action.showXMLResponse'
                                  and parent = 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog'),
   82, '1', '1', 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'action.showXMLResponse'
                                  and parent = 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog'),
   81, '1', '1', 0);


Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'action.showCompleteXMLRequest', 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'action.showCompleteXMLRequest'
                                  and parent = 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog'),
   82, '1', '1', 0);


Insert into GUICOMPONENT
   (ID, NAME, PARENT, APP_ID, VERSION)
 Values
   (S_GUICOMPONENT_0.nextVal, 'action.showCompleteXMLResponse', 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog', 1, 0);

Insert into COMPBEHAVIOR
   (ID, COMP_ID, ROLE_ID, VISIBLE, EXECUTABLE, VERSION)
 Values
   (S_COMPBEHAVIOR_0.nextVal,
   (select ID from GUICOMPONENT where name = 'action.showCompleteXMLResponse'
                                  and parent = 'de.augustakom.hurrican.gui.tools.tal.wita.WitaCBVorgangHistoryDialog'),
   82, '1', '1', 0);
