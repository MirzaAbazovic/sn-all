DELETE FROM COMPBEHAVIOR b
      WHERE B.COMP_ID IN
               (SELECT C.ID
                  FROM GUICOMPONENT c
                 WHERE C.NAME IN
                          ('action.showXMLRequest', 'action.showXMLResponse'));

DELETE FROM GUICOMPONENT c
      WHERE C.NAME IN ('action.showXMLRequest', 'action.showXMLResponse');
