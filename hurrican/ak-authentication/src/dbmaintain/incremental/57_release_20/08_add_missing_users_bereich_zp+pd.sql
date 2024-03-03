-- add some missing users from 07_bereich_te_zp.sql
UPDATE users
SET bereich = (SELECT id
               FROM BEREICH
               WHERE name = 'TE-ZP')
WHERE LOGINNAME IN ('aboussibaa-friesenin',
                    'EckoffGu',
                    'effenbergerkl',
                    'FreytagGu',
                    'hartmannju',
                    'jaegermr',
                    'KnoerichEv',
                    'koehler',
                    'koepkeka',
                    'RitzerWo',
                    'Roessner',
                    'sailergue',
                    'SchoenGe',
                    'stoehrjo',
                    'thuernauvo',
                    'Vierke',
                    'WirthHe',
                    'Zinecker'
);

-- add some missing users from 05_bereich_te_pd.sql
UPDATE users
SET bereich = (SELECT id
               FROM BEREICH
               WHERE name = 'TE-PD')
WHERE LOGINNAME IN ('Huebscher',
                    'koenigul',
                    'LessmannMo',
                    'schuetzal',
                    'WeissMi'
);