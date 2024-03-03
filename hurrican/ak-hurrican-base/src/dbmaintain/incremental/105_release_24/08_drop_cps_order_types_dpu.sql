-- ANF-767 Service Order Typen initializeDPU und updateDPU werden nicht benoetigt
DELETE FROM T_REFERENCE WHERE ID = 14013; -- initializeDPU
DELETE FROM T_REFERENCE WHERE ID = 14014; -- updateDPU
