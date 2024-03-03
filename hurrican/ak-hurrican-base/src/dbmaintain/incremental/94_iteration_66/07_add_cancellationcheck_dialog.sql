Insert into T_GUI_DEFINITION
   (ID, CLASS, TYPE, NAME, TEXT,
    TOOLTIP, ORDER_NO, ADD_SEPARATOR, ACTIVE, VERSION)
 Values
   (331, 'de.augustakom.hurrican.gui.tools.wbci.actions.OpenCancellationCheckDialogAction', 'ACTION',
   'cancellation.check', 'K�ndigungsdatum...',
    '�ffnet einen Dialog, in dem das fr�hest m�gliche K�ndigungsdatum f�r den Auftrag dargestellt wird', 100, '1', '1', 0);

INSERT INTO T_GUI_MAPPING (ID,  GUI_ID,  REFERENZ_ID, REFERENZ_HERKUNFT)
  values (S_T_GUI_MAPPING_0.nextVal,
  331, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition');
