-- Kostenstelle auf 100 Zeichen begrenzen und Leerzeichen abschneiden
update t_ia set kostenstelle=trim(kostenstelle) where kostenstelle is not null;
alter table t_ia modify kostenstelle varchar2(100);