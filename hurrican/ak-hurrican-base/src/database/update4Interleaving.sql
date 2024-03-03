--
-- SQL-Script, um neben Fastpath auch noch die techn. Leistung
-- 'Interleaving' bei DSL einzufuehren.
--

alter session set nls_date_format='yyyy-mm-dd';

update T_TECH_LEISTUNG set TYP='DSL_ERR_CORRECT' where id=1;
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, 
  TYP, DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, PROD_NAME_STR, GUELTIG_VON, GUELTIG_BIS) 
  values (29, 'Interleaving', 10008, 'DSL_ERR_CORRECT', 0, 0, 0, 1, 0, 1, ' ', '2000-01-01', '2200-01-01');

-- Interleaving-Option bei ADSL-Produkten mit aufnehmen
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
  values (600, 420, 29, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
  values (601, 421, 29, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
  values (602, 430, 29, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
  values (603, 431, 29, 1);
insert into T_PROD_2_TECH_LEISTUNG (ID, PROD_ID, TECH_LS_ID, IS_DEFAULT)
  values (604, 440, 29, 1);

drop sequence S_T_PROD_2_TECH_LEISTUNG_0;
create sequence S_T_PROD_2_TECH_LEISTUNG_0 start with 605;
grant select on S_T_PROD_2_TECH_LEISTUNG_0 to public;
commit;


-- Fastpath-Leistungen loeschen, wo aktiv_von=aktiv_bis
delete from t_auftrag_2_tech_ls where id in (
  select a2t.id 
    from t_auftrag_2_tech_ls a2t
    inner join t_auftrag_daten a on a.auftrag_id=a2t.auftrag_id
    where a.status_id not in (1150,3400) and a.status_id<9800 and a.gueltig_bis='2200-01-01'
    and a2t.tech_ls_id=1 and a2t.aktiv_von=aktiv_bis);


--
-- notwendige Code-Anpassungen bzw. Erweiterungen:
--
TechLeistung
  - neue Konstante fuer Typ DSL_ERROR_CORRECTION anlegen
  - Konstante in TYPEN und UNIQUE_TYPES aufnehmen  

