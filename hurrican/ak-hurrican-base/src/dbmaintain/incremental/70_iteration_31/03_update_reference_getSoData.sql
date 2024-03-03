-- GUI Visibility entfernen, da sonst der Typ in dem CreateCPSTx Dialog zur Auswahl erscheint
update T_REFERENCE set GUI_VISIBLE = '0' where ID = 14009;