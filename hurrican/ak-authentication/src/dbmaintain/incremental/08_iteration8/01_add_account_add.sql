--
-- Rechte und GUI-Componenten fuer Client2Site fixen. Falscher Name.

insert into GUICOMPONENT (ID, NAME, PARENT, TYPE, DESCRIPTION, APP_ID)
    values (S_GUICOMPONENT_0.nextVal,
    'add.account', 'de.augustakom.hurrican.gui.auftrag.internet.InternetPanel',
    'Button', 'Button um manuell einen Einwahlaccount anzulegen', 1);