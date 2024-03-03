delete from COMPBEHAVIOR where COMP_ID in
    (select ID from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.auftrag.RangierungPanel' and NAME = 'getzv.rangierung'));
delete from GUICOMPONENT where (PARENT = 'de.augustakom.hurrican.gui.auftrag.RangierungPanel' and NAME = 'getzv.rangierung');
