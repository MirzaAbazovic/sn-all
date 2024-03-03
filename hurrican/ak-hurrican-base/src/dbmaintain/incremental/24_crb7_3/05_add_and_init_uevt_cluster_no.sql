
-- add ÜVT cluster number and set default to 1
ALTER TABLE T_UEVT ADD (UEVT_CLUSTER_NO NUMBER(10));
UPDATE T_UEVT SET UEVT_CLUSTER_NO = 1; -- ok for munich

-- set ÜVT cluster number to not null
ALTER TABLE T_UEVT MODIFY(UEVT_CLUSTER_NO NOT NULL);

-- init cluster number for Augsburg/Kempten i.e. uevts in same room get same cluster id
CREATE OR REPLACE PROCEDURE INIT_UEVT_CLUSTER_NO
IS
BEGIN
    DECLARE
        i_hvt_id_standort  number;

        cursor c_uevts is
            SELECT U.UEVT_ID, U.HVT_ID_STANDORT, RAU.ID AS RAUM_ID FROM T_UEVT U
            LEFT OUTER JOIN T_HW_RACK RCK ON RCK.ID = U.RACK_ID
            LEFT OUTER JOIN T_HVT_RAUM RAU ON RAU.ID = RCK.HVT_RAUM_ID;
        r_uevts       c_uevts%ROWTYPE;

        cursor c_uevt_clusters IS
            SELECT ROWNUM AS CLUSTER_NO, UEVT.RAUM_ID FROM
                (SELECT UI.HVT_ID_STANDORT, RAUI.ID AS RAUM_ID FROM T_UEVT UI
                LEFT OUTER JOIN T_HW_RACK RCKI ON RCKI.ID = UI.RACK_ID
                LEFT OUTER JOIN T_HVT_RAUM RAUI ON RAUI.ID = RCKI.HVT_RAUM_ID AND RAUI.HVT_ID_STANDORT = UI.HVT_ID_STANDORT
                GROUP BY UI.HVT_ID_STANDORT, RAUI.ID
                ORDER BY UI.HVT_ID_STANDORT, RAUI.ID) UEVT
            WHERE UEVT.HVT_ID_STANDORT = i_hvt_id_standort;
        r_cluster       c_uevt_clusters%ROWTYPE;

    BEGIN
        FOR r_uevts IN c_uevts
        LOOP
            i_hvt_id_standort := r_uevts.HVT_ID_STANDORT;
            OPEN c_uevt_clusters;
            LOOP
                FETCH c_uevt_clusters INTO r_cluster;
                EXIT WHEN c_uevt_clusters%NOTFOUND;
                if (r_cluster.RAUM_ID = r_uevts.RAUM_ID) THEN
                    UPDATE T_UEVT U SET U.UEVT_CLUSTER_NO = r_cluster.CLUSTER_NO WHERE U.UEVT_ID = r_uevts.UEVT_ID;
                END IF;
            END LOOP;
            CLOSE c_uevt_clusters;
        END LOOP;
    END;
END;
/

BEGIN
  INIT_UEVT_CLUSTER_NO;
  COMMIT;
END;
/

DROP PROCEDURE INIT_UEVT_CLUSTER_NO;

/*
-- statement to check correctness of above stored procedure
SELECT RCK1.GERAETEBEZ, RCK2.GERAETEBEZ, RAU1.RAUM, RAU2.RAUM, U1.UEVT_CLUSTER_NO, U2.UEVT_CLUSTER_NO
FROM T_UEVT U1
    LEFT OUTER JOIN T_HW_RACK RCK1 ON RCK1.ID = U1.RACK_ID
    LEFT OUTER JOIN T_HVT_RAUM RAU1 ON RAU1.ID = RCK1.HVT_RAUM_ID
    LEFT JOIN T_UEVT U2 ON U2.UEVT_ID <> U1.UEVT_ID AND U2.HVT_ID_STANDORT = U1.HVT_ID_STANDORT
    LEFT OUTER JOIN T_HW_RACK RCK2 ON RCK2.ID = U2.RACK_ID
    LEFT OUTER JOIN T_HVT_RAUM RAU2 ON RAU2.ID = RCK2.HVT_RAUM_ID
WHERE U1.UEVT_CLUSTER_NO < U2.UEVT_CLUSTER_NO;
*/
