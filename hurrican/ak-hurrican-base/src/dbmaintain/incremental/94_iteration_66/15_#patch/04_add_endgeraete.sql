INSERT INTO T_EG
(ID               ,INTERNE__ID,NAME             ,BESCHREIBUNG     ,LS_TEXT          ,EXT_LEISTUNG__NO,VERFUEGBAR_VON            ,VERFUEGBAR_BIS            ,GARANTIEZEIT,PRODUKTCODE,CONFIGURABLE,CONF_PORTFORWARDING,CONF_S0BACKUP,TYPE,VERSION,CPS_PROVISIONING,USERW,EGTYPES) VALUES
(S_T_EG_0.nextval ,null       ,'Kundenequipment','Kundenequipment','Kundenequipment',null            ,TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),TO_DATE('2200-01-01 00:00', 'YYYY-MM-DD HH24:MI'),null        ,null       ,'1'         ,null               ,null         ,4   ,0      ,null            ,null ,null   )
;

update t_eg set interne__id = id where name = 'Kundenequipment'
;


INSERT INTO t_prod_2_eg
(ID                     ,PROD_ID,EG_ID                                               ,IS_DEFAULT,IS_ACTIVE,VERSION) VALUES
(S_T_PROD_2_EG_0.nextval,580    ,(select id from t_eg where name = 'Kundenequipment'),'1'       ,'1'      ,0      )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER    ,MODELL  ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Innovaphone' ,'IP3010','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Asterisk' ,'11'   ,'Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL    ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Asterisk' ,'MobyDick','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Hipath'   ,'2030' ,'Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL                     ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Avaya'    ,'Communication Server 2100','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL        ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Aastra'   ,'Opencom 1000','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL       ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Aastra'   ,'Opencom 100','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER  ,MODELL      ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Panasonic' ,'KX-NCP1000','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER  ,MODELL     ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Panasonic' ,'KX-NCP500','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER  ,MODELL     ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Panasonic' ,'KX-TDE600','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER  ,MODELL     ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Panasonic' ,'KX-TDE100','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER  ,MODELL     ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Panasonic' ,'KX-TDE200','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER  ,MODELL     ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Panasonic' ,'KX-NS1000','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER  ,MODELL             ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Auerswald' ,'COMpact 5010 VoIP','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER  ,MODELL             ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Auerswald' ,'COMpact 5020 VoIP','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'IPTAM'    ,'3.0'  ,'Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL          ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'3CX'      ,'Mediatrix 1104','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL        ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Digium'   ,'Switchvox 80','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL         ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Digium'   ,'Switchvox 310','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL    ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Starface' ,'Advanced','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL      ,USERW            ,DATEW                     ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Starface' ,'Enterprise','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;

INSERT INTO t_eg_type
(ID                    ,HERSTELLER ,MODELL    ,USERW            ,DATEW                                            ,VERSION,EGGRUPPEN) VALUES
(S_T_EG_TYPE_0.nextval ,'Starface' ,'Platinum','Lorenz Grehlich',TO_DATE('2014-01-01 00:00', 'YYYY-MM-DD HH24:MI'),1      ,null     )
;
