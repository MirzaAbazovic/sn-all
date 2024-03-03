update GUICOMPONENT set parent = 'de.augustakom.hurrican.gui.verlauf.VerlaufsBemerkungenPanel', type='TextArea'
where parent = 'de.augustakom.hurrican.gui.verlauf.VerlaufsBemerkungenDialog'
and name <> 'save';

insert into GUICOMPONENT
select S_GUICOMPONENT_0.nextval as id, 'verlauf.wiedervorlage' || SUBSTR(NAME, INSTR(NAME,'.', 1, 2)) as name, parent, 'DateComp' as type, null as description, app_id
from GUICOMPONENT 
where parent = 'de.augustakom.hurrican.gui.verlauf.VerlaufsBemerkungenPanel'
and SUBSTR(NAME, 1, 18) = 'verlauf.bemerkung.';

insert into GUICOMPONENT
select S_GUICOMPONENT_0.nextval as id, 'verlauf.abteilung.status' || SUBSTR(NAME, INSTR(NAME,'.', 1, 2)) as name, parent, 'TextField' as type, null as description, app_id
from GUICOMPONENT 
where parent = 'de.augustakom.hurrican.gui.verlauf.VerlaufsBemerkungenPanel'
and SUBSTR(NAME, 1, 18) = 'verlauf.bemerkung.';

insert into COMPBEHAVIOR
select S_COMPBEHAVIOR_0.nextval as id, G2.ID as COMP_ID, C.ROLE_ID, C.VISIBLE, C.EXECUTABLE
from COMPBEHAVIOR c
join GUICOMPONENT g on C.COMP_ID = G.ID
join GUICOMPONENT g2
    on (SUBSTR(g.NAME, INSTR(g.NAME,'.', 1, 2)) = SUBSTR(g2.NAME, INSTR(g2.NAME,'.', 1, 2))
    or SUBSTR(g.NAME, INSTR(g.NAME,'.', 1, 2)) = SUBSTR(g2.NAME, INSTR(g2.NAME,'.', 1, 3)))
    and g.name <> g2.name
where G.PARENT = 'de.augustakom.hurrican.gui.verlauf.VerlaufsBemerkungenPanel'
and SUBSTR(G.NAME, 1, 18) = 'verlauf.bemerkung.';
