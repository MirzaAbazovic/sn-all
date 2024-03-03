delete from compbehavior where comp_id=(select id from guicomponent where name='abgleich.eg4Kunde');
delete from guicomponent where name='abgleich.eg4Kunde';
