
-- fix incorrect migrated due to RANG_VERTEILER on DSLAM and DLU ports in MUC
-- and first FttB with HW_EQN like M-Arnul204-z01-02 (-> OR EQ.CARRIER = 'MNET'; inserted manually by lippertul)
UPDATE T_EQUIPMENT EQ SET EQ.UEVT_CLUSTER_NO = NULL
    WHERE EQ.UEVT_CLUSTER_NO IS NOT NULL
        AND (EQ.CARRIER IS NULL OR EQ.CARRIER = 'MNET');
