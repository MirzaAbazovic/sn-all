--insert the new mapping config
CREATE OR REPLACE PROCEDURE add_ffm_qual_mapping
  (p_qualification IN VARCHAR2, p_anschlussart IN VARCHAR2)
IS
  BEGIN
      FOR product IN (SELECT p.PROD_ID from T_PRODUKT p where p.ANSCHLUSSART = p_anschlussart)
      LOOP
        INSERT INTO T_FFM_QUALIFICATION_MAPPING (ID, QUALIFICATION_ID, PRODUCT_ID)
                VALUES (S_T_FFM_QUAL_MAPPING_0.nextval,
                (SELECT q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION = p_qualification),
                product.PROD_ID);
      END LOOP;
  END;
/

-- SDSL product to qualification mappings
CALL add_ffm_qual_mapping('SDSL', 'Glasfaser SDSL');
CALL add_ffm_qual_mapping('SDSL', 'SDSL 10000');
CALL add_ffm_qual_mapping('SDSL', 'SDSL 15000');
CALL add_ffm_qual_mapping('SDSL', 'SDSL 20000');
CALL add_ffm_qual_mapping('SDSL', 'SDSL 2300');
CALL add_ffm_qual_mapping('SDSL', 'SDSL n-Draht Option');
CALL add_ffm_qual_mapping('SDSL', 'SDSL 4600');
CALL add_ffm_qual_mapping('SDSL', 'SDSL 6800');
CALL add_ffm_qual_mapping('SDSL', 'AK-SDSL Line 2300');
CALL add_ffm_qual_mapping('SDSL', 'AK-SDSLoffice1200');
CALL add_ffm_qual_mapping('SDSL', 'AK-SDSLoffice2300');

-- SIP-Trunk product to qualification mappings
CALL add_ffm_qual_mapping('SIP Trunk', 'SIP-Trunk');

-- Connect product to qualification mappings
CALL add_ffm_qual_mapping('Connect', 'Connect-LAN');
CALL add_ffm_qual_mapping('Connect', 'Connect');
CALL add_ffm_qual_mapping('Connect', 'PMX Leitung');
CALL add_ffm_qual_mapping('Connect', 'MetroEthernet');
CALL add_ffm_qual_mapping('Connect', 'AK-connect');
CALL add_ffm_qual_mapping('Connect', 'Connect Zusatzleitung');
CALL add_ffm_qual_mapping('Connect', 'Connect Basic');
CALL add_ffm_qual_mapping('Connect', 'Dark Fiber');
CALL add_ffm_qual_mapping('Connect', 'Direct Access');

-- VPN product to qualification mappings
CALL add_ffm_qual_mapping('VPN', 'ConnectDSL SDSL');
CALL add_ffm_qual_mapping('VPN', 'M-net Partner L2TP VPN');
CALL add_ffm_qual_mapping('VPN', 'VPN IPSec Site-to-Site');

-- PMX product to qualification mappings
CALL add_ffm_qual_mapping('PMX', 'ISDN PMX');
CALL add_ffm_qual_mapping('PMX', 'AK-phone ISDN S2M P');

-- TV product to qualification mappings
CALL add_ffm_qual_mapping('TV', 'M-net Kabel-TV');

-- Housing product to qualification mappings
CALL add_ffm_qual_mapping('Housing', 'Housing');

-- Sonstige product to qualification mappings
CALL add_ffm_qual_mapping('Sonstige', 'AK-intern Trail');
CALL add_ffm_qual_mapping('Sonstige', 'AK-phone appartement');
CALL add_ffm_qual_mapping('Sonstige', 'DarkCopper');
CALL add_ffm_qual_mapping('Sonstige', 'UMTS Backup');
CALL add_ffm_qual_mapping('Interne Arbeit', 'interne Arbeit');

DROP PROCEDURE add_ffm_qual_mapping;