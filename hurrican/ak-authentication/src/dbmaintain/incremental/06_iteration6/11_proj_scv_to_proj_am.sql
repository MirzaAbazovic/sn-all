
delete from COMPBEHAVIOR where COMP_ID = (select id from GUICOMPONENT where name = 'vorgabe.am');
delete from GUICOMPONENT where name = 'vorgabe.am';

update GUICOMPONENT set name = 'vorgabe.am'
where name = 'vorgabe.scv';

update GUICOMPONENT set name = 'ba.abschliessen.am'
where name = 'ba.abschliessen.scv';

update GUICOMPONENT set description = 'Datumsfeld f.
Vorgabe AM.'
where description = 'Datumsfeld f.
Vorgabe SCV.';

update GUICOMPONENT set description = 'Button, um die Bemerkungen
zu einem Bauauftrag
anzuzeigen (AM RL).'
where description = 'Button, um die Bemerkungen
zu einem Bauauftrag
anzuzeigen (SCV RL).';

update GUICOMPONENT set description = 'Button, um einen Bauauftrag
komplett abzuschliessen und
den zug. Auftrag auf <in
Betrieb> zu setzen (AM RL).'
where description = 'Button, um einen Bauauftrag
komplett abzuschliessen und
den zug. Auftrag auf <in
Betrieb> zu setzen (SCV RL).';

update GUICOMPONENT set description = 'Button, um eine Projektierung
komplett abzuschliessen
(RL AM).'
where description = 'Button, um eine Projektierung
komplett abzuschliessen
(RL SCV).';

update GUICOMPONENT set description = 'Button, um die Bemerkungen
zu einer Projektierung
anzuzeigen (RL AM).'
where description = 'Button, um die Bemerkungen
zu einer Projektierung
anzuzeigen (RL SCV).';

update GUICOMPONENT set parent = 'de.augustakom.hurrican.gui.verlauf.ProjektierungAmRlPanel'
where parent = 'de.augustakom.hurrican.gui.verlauf.ProjektierungScvRLPanel';

update GUICOMPONENT set parent = 'de.augustakom.hurrican.gui.verlauf.BauauftragAmRlPanel'
where parent = 'de.augustakom.hurrican.gui.verlauf.BauauftragScvRLPanel';

