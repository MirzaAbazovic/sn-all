delete from COMPBEHAVIOR where COMP_ID in
  (select g.ID from GUICOMPONENT g where g.parent='de.augustakom.hurrican.gui.hvt.hardware.ImportMiecomPortDatenDialog');

delete from GUICOMPONENT g where g.parent='de.augustakom.hurrican.gui.hvt.hardware.ImportMiecomPortDatenDialog';
