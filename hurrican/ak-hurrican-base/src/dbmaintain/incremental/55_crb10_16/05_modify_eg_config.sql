-- Hibernate moechte die Parent IDs der Childs erst loeschen um danach den Parent und die Childs entfernen zu koennen.
alter table t_eg_config modify (EG2A_ID null);
grant delete on t_eg_config to HURRICANWRITER;