INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'create.va.kue.mrn',
  'de.augustakom.hurrican.gui.tools.tal.wizard.WitaSelectCBVorgangWizardPanel', 'BUTTON', 
  'VA-KUE-MRN Button', 1, 0);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='create.va.kue.mrn'
  and g.parent='de.augustakom.hurrican.gui.tools.tal.wizard.WitaSelectCBVorgangWizardPanel';


INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'create.va.kue.orn',
  'de.augustakom.hurrican.gui.tools.tal.wizard.WitaSelectCBVorgangWizardPanel', 'BUTTON',
  'VA-KUE-MRN Button', 1, 0);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='create.va.kue.orn'
  and g.parent='de.augustakom.hurrican.gui.tools.tal.wizard.WitaSelectCBVorgangWizardPanel';


INSERT INTO GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID, VERSION)
  VALUES (S_GUICOMPONENT_0.nextVal, 'create.va.kue.rrnp',
  'de.augustakom.hurrican.gui.tools.tal.wizard.WitaSelectCBVorgangWizardPanel', 'BUTTON',
  'VA-KUE-MRN Button', 1, 0);
insert into COMPBEHAVIOR (ID, ROLE_ID, VISIBLE, EXECUTABLE, COMP_ID)
  select S_COMPBEHAVIOR_0.nextVal, 9, '1', '1',
  g.ID from GUICOMPONENT g where g.name='create.va.kue.rrnp'
  and g.parent='de.augustakom.hurrican.gui.tools.tal.wizard.WitaSelectCBVorgangWizardPanel';

