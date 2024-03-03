delete from COMPBEHAVIOR where COMP_ID in
    (select ID from GUICOMPONENT where (NAME = 'open.telbuch.search.frame.action'));
delete from GUICOMPONENT where (NAME = 'open.telbuch.search.frame.action');

