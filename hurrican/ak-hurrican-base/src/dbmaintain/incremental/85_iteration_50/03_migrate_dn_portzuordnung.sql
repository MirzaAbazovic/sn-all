CREATE OR REPLACE PROCEDURE migrate_rn_portzuordnung
AS
  BEGIN
    FOR row IN (SELECT
                  *
                FROM T_AUFTRAG_VOIP_DN_2_EG_PORT dn2eg) LOOP
    INSERT INTO T_AUFTRAG_VOIPDN_2_EG_PORT P (id, auftragvoipdn_id, egport_id, aktiv_von, aktiv_bis, version)
      VALUES (S_T_AUFTRAG_VOIPDN_2_EG_PORT_0.nextval, row.AUFTRAG_VOIP_DN_ID, row.EG_PORT_ID, sysdate,
              to_date('01.01.2200', 'dd.MM.yyyy'), 0);
    END LOOP;
  END;
/

CALL migrate_rn_portzuordnung();
DROP PROCEDURE migrate_rn_portzuordnung;
DROP TABLE T_AUFTRAG_VOIP_DN_2_EG_PORT;


