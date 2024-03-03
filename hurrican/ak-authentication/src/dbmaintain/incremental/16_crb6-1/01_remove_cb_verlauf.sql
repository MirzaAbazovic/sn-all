
delete from COMPBEHAVIOR where COMP_ID in (
    select ID FROM GUICOMPONENT where name='hvt.cbverlauf.action'
);

delete from GUICOMPONENT where name='hvt.cbverlauf.action';
