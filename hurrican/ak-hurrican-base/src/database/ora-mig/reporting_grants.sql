--
-- GRANTs fuer REPORTING
--

-- Read-only Role anlegen
CREATE ROLE "R_REPORT_USER" NOT IDENTIFIED;
GRANT SELECT , INSERT , UPDATE ON T_ARCHIV_DATEN TO R_REPORT_USER;
GRANT SELECT ON T_ARCHIV_AUSWAHL TO R_REPORT_USER;
GRANT SELECT ON T_ARCHIV_OBJECT TO R_REPORT_USER;
GRANT SELECT ON T_ARCHIV_PARAMETER_2_OBJECT TO R_REPORT_USER;
GRANT SELECT ON T_ARCHIV_POSSIBLE_PARAMETER TO R_REPORT_USER;
GRANT SELECT ON T_PRINTER TO R_REPORT_USER;
GRANT SELECT ON T_PRINTER_2_PAPER TO R_REPORT_USER;
GRANT SELECT ON T_REP2PROD_STATI TO R_REPORT_USER;
GRANT SELECT ON T_REP2PROD_TECHLS TO R_REPORT_USER;
GRANT SELECT ON T_REPORT TO R_REPORT_USER;
GRANT SELECT ON T_REPORT_2_PROD TO R_REPORT_USER;
GRANT SELECT ON T_REPORT_2_USERROLE TO R_REPORT_USER;
GRANT SELECT, INSERT, UPDATE ON T_REPORT_DATA TO R_REPORT_USER;
GRANT SELECT ON T_REPORT_GRUPPE TO R_REPORT_USER;
GRANT SELECT ON T_REPORT_PAPERFORMAT TO R_REPORT_USER;
GRANT SELECT, INSERT, UPDATE ON T_REPORT_REQUEST TO R_REPORT_USER;
GRANT SELECT ON T_REPORT_REASON TO R_REPORT_USER;
GRANT SELECT ON T_REPORT_TEMPLATE TO R_REPORT_USER;
GRANT SELECT ON T_SERVICE_CHAIN TO R_REPORT_USER;
GRANT SELECT ON T_SERVICE_COMMANDS TO R_REPORT_USER;
GRANT SELECT ON T_SERVICE_COMMAND_MAPPING TO R_REPORT_USER;
GRANT SELECT ON T_TXT_BAUSTEIN TO R_REPORT_USER;
GRANT SELECT ON T_TXT_BAUSTEIN_2_GRUPPE TO R_REPORT_USER;
GRANT SELECT ON T_TXT_BAUSTEIN_GRUPPE TO R_REPORT_USER;
GRANT SELECT ON T_TXT_BAUSTEIN_GRUPPE_2_REPORT TO R_REPORT_USER;
GRANT SELECT ON T_AUFTRAG_STATUS TO R_REPORT_USER;
GRANT SELECT ON T_PRODUKT TO R_REPORT_USER;

commit;


--
-- report-user
--
CREATE USER "REPORTUSER" PROFILE "DEFAULT" IDENTIFIED BY "troper" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "REPORTUSER";
GRANT "R_REPORT_USER" TO "REPORTUSER";
commit;



--Rolle fuer Report-Administration anlegen
CREATE ROLE "R_REPORT_ADMIN" NOT IDENTIFIED;
GRANT INSERT , UPDATE ON t_archiv_daten TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE ON t_report TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE, DELETE ON t_report_request TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE, DELETE ON t_report_data TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE, DELETE ON t_report_template TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON T_SERVICE_COMMAND_MAPPING TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_report_2_prod TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_rep2prod_stati TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_rep2prod_techls TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_report_2_userrole TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_report_data TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_report_gruppe TO R_REPORT_ADMIN;
GRANT SELECT , UPDATE , DELETE ON T_REPORT_PAPERFORMAT TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_report_reason TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_txt_baustein TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_txt_baustein_2_gruppe TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_txt_baustein_gruppe_2_report TO R_REPORT_ADMIN;
GRANT INSERT , UPDATE , DELETE ON t_txt_baustein_gruppe TO R_REPORT_ADMIN;

--
-- report-admin
--
CREATE USER "REPORTADMIN" PROFILE "DEFAULT" IDENTIFIED BY "troper" 
  DEFAULT TABLESPACE "T_HURRICAN" TEMPORARY TABLESPACE "TEMPORARY" ACCOUNT UNLOCK;
GRANT "CONNECT" TO "REPORTADMIN";
GRANT "R_REPORT_USER" TO "REPORTADMIN";
GRANT "R_REPORT_ADMIN" TO "REPORTADMIN";
commit;
