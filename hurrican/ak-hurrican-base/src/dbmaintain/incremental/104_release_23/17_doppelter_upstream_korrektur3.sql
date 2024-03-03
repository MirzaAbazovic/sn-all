-- zuviel angelegte Leistungen wieder entfernen
delete from T_PROD_2_TECH_LEISTUNG where (
(tech_ls_id = 27 and tech_ls_dependency = 23) or
(tech_ls_id = 28 and tech_ls_dependency = 24) or
(tech_ls_id = 74 and tech_ls_dependency = 25) or
(tech_ls_id = 53 and tech_ls_dependency = 72) or
(tech_ls_id = 53 and tech_ls_dependency = 66))
and prod_id in (512,513,514,515);

-- Punkt als Trennzeichen entfernen
update t_tech_leistung set name = '40000 kbit/s', str_value = '40000', prod_name_str = '40000'
where extern_leistung__no = 10074 and typ = 'UPSTREAM';

