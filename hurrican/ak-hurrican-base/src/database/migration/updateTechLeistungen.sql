--
-- Update-Script, um die technischen Leistungen fuer die Produkte
-- zu konfigurieren
--


CREATE TABLE T_PROD_2_TECH_LEISTUNG (
	   ID INTEGER(9)NOT NULL AUTO_INCREMENT
     , PROD_ID INTEGER(9) NOT NULL
     , TECH_LS_ID INTEGER(9) NOT NULL
     , TECH_LS_DEPENDENCY INTEGER(9)
     , IS_DEFAULT TINYINT(1)
     , PRIMARY KEY (ID)
)ENGINE=InnoDB;
ALTER TABLE T_PROD_2_TECH_LEISTUNG
  ADD CONSTRAINT FK_P2TLSDEP_2_TLS
      FOREIGN KEY (TECH_LS_DEPENDENCY)
      REFERENCES T_TECH_LEISTUNG (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
ALTER TABLE T_PROD_2_TECH_LEISTUNG
  ADD CONSTRAINT FK_P2TLS_2_TLS
      FOREIGN KEY (TECH_LS_ID)
      REFERENCES T_TECH_LEISTUNG (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
ALTER TABLE T_PROD_2_TECH_LEISTUNG
  ADD CONSTRAINT FK_P2TLS_2_PROD
      FOREIGN KEY (PROD_ID)
      REFERENCES T_PRODUKT (PROD_ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default)
	select * from t_prod_2_tech_ls;

ALTER TABLE T_PROD_2_TECH_LEISTUNG
  ADD CONSTRAINT UQ_P2TL
      UNIQUE (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY);

-- SDSLoffice
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (312, 5, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (313, 5, null, 0);

-- AK-SDSL
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (11, 5, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (16, 5, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (17, 5, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (18, 5, null, 0);

-- AK-ADSL
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (9, 6, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (56, 6, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (57, 6, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (321, 6, null, 0);

-- AK-DSLplus
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (315, 6, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (316, 6, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (317, 6, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (320, 6, null, 0);



-- Premium
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (340, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (340, 7, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (333, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (333, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (333, 1, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (330, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (330, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (330, 2, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (330, 1, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (331, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (331, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (331, 2, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (331, 1, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (332, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (332, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (332, 1, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (336, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (336, 7, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (337, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (337, 7, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (338, 101, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 7, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 8, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 9, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 10, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 12, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 11, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (430, 101, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 7, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 8, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 9, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 10, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 11, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (431, 12, null, 0);

-- Maxi
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 7, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 8, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 9, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 10, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 11, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (420, 12, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 7, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 8, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 9, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 10, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 11, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (421, 12, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (337, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (337, 7, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (322, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (322, 7, null, 0);


insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (324, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (324, 2, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (324, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (324, 4, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (325, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (325, 2, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (325, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (325, 4, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (327, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (327, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (327, 4, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (326, 1, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (326, 3, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (326, 4, null, 0);

insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (323, 4, null, 0);
insert into t_prod_2_tech_leistung (prod_id, tech_ls_id, tech_ls_dependency, is_default) values (323, 7, null, 0);

