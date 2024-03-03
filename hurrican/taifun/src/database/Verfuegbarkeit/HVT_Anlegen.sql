CREATE OR REPLACE FUNCTION f_str_suchname (varSTR_NAME VARCHAR2)
   RETURN VARCHAR2 DETERMINISTIC IS  cret VARCHAR2(50);
BEGIN
cret :=
translate(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
replace(
upper(
varSTR_NAME
),'DOKTOR','DR'
),'ST.','SANKT'
),'PROFESSOR','PROF'
),'STRASSE','STR'
),'STRAßE','STR'
),'STRA¿E','STR'
),' STR','STR'
),'ß','SS'
),'¿','Ü'
),'Ü','UE'
),'Ö','OE'
),'Ä','AE'
),
'0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. ,',
'0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'
);
   RETURN cret;
END f_str_suchname;

/

CREATE OR REPLACE FUNCTION VEKTOR_K (vektor_str VARCHAR, KOMPONENTE INTEGER, TRENNZEICHEN VARCHAR) 
-- ============================================= 
-- Author: Valeriy Boykov 
-- Create date: 16.04.2007 
-- Description: extrahiert eine komponente aus einem String 
-- Aufruf: vektor_k(' PCB 4| EPLD 4',2,'|') 
-- ============================================= 
RETURN VARCHAR 

IS

my_ret     VARCHAR(3000);
 
BEGIN 

    DECLARE 
        v  VARCHAR(3000); 
        A  INTEGER; 
        X  VARCHAR(3000); 
        b  INTEGER; 
        C  INTEGER; 
        d  INTEGER; 
        varVektor_str VARCHAR(3000);

    --set @Trennzeichen = '|' --'/' 
    --set @vektor_str = ' PCB 4| EPLD 4' --'Test1/Test2/Test3' 
    --set @Komponente = 2 

    BEGIN
    
        varVektor_str := Vektor_str;

        if varVektor_str <> Trennzeichen then 
            varVektor_str := varVektor_str || Trennzeichen; 
        end if;
        
        X := Trennzeichen || varVektor_str || Trennzeichen; 
        v := ''; 

        If komponente > 0  THEN
            A := 1;  -- Index 
            b := 0;  -- Z?hler f?r Komponenten 
            C := 0;  -- Anfang Zeichen Komponente 
            d := 0;  -- Ende Zeichen Komponente 
             

            WHILE (A < LENGTH(X)) 
            
            LOOP
            
                if Substr(X, A, 1) = Trennzeichen then  
            
          
                    
                    b := b + 1;  -- nächste Komponente 
                    
                    If b = Komponente  then
                        C := A + 1; 
                    end if;
                        
                    If b = Komponente + 1 then 
                        d := A - 1; 
                        v := substr(X, C, d - C + 1); 
                        exit;
                    End if; 
           
                end if;
                 
                A := A + 1; 
            
            End LOOP;
             
        End IF;
        
        my_ret  := v;
        
    END;    
     
    RETURN my_ret; 
End;

/

CREATE OR REPLACE FUNCTION F_GET_1ST_NO_NUM (var_STR VARCHAR) 
RETURN VARCHAR IS myRet VARCHAR(1);
-- ============================================= 
-- Author: Valeriy Boykov 
-- Create date: 18.12.2008 
-- Description: extrahiert erste no numerische komponente aus einem String 
 --Aufruf: SELECT F_GET_1ST_NO_NUM('122d34987c') FROM DUAL; 
-- =============================================  
BEGIN 

    DECLARE 
        var1St_No_Num   VARCHAR2(1); 
        i               NUMBER; 
        varVektor_str   VARCHAR2(3000);
        iRet            NUMBER;

    BEGIN
    
        varVektor_str := var_STR; 
        var1St_No_Num := ''; 

        i := 1;  -- Index
        
        WHILE i <= LENGTH(varVektor_str) LOOP 
        
            BEGIN
            
                var1St_No_Num := SUBSTR(varVektor_str, i, 1);
                iRet := TO_NUMBER(var1St_No_Num);
                i := i + 1;
                  
            EXCEPTION
                WHEN OTHERS THEN
                    myRet := var1St_No_Num;
                    EXIT;  
            END;
            
        END LOOP;
        
    END;    
     
    RETURN myRet;
     
END F_GET_1ST_NO_NUM;

/

CREATE OR REPLACE FUNCTION F_GET_HAUSNUMMER (varSTR_NAME VARCHAR2, iPos NUMBER := 1)
RETURN VARCHAR2 DETERMINISTIC IS cRet VARCHAR2(50);
-- ============================================= 
-- Author: Valeriy Boykov 
-- Create date: 17.12.2008 
-- Description: extrahiert Hausnummer oder Hausnummerzusatz aus einem String 
-- Aufruf: SELECT F_GET_HAUSNUMMER('7 / Haus1',2) FROM DUAL; 
-- ============================================= 
BEGIN
    
    DECLARE
        iRet                NUMBER;
        var_Trennzeichen    VARCHAR2(1);
        
    BEGIN
    
        iRet := TO_NUMBER(varSTR_NAME); 
        IF iPos = 1 THEN
            cRet := varSTR_NAME;
        ELSE
            cRet := NULL;
        END IF;
                
    EXCEPTION 
        WHEN OTHERS THEN     
            
            IF varSTR_NAME IS NULL THEN
                cRet := NULL;
            ELSE
                
                var_Trennzeichen := F_GET_1ST_NO_NUM(varSTR_NAME); 
                
                SELECT
                    CASE
                        WHEN INSTR(varSTR_NAME, var_Trennzeichen) > 0 THEN
                                CASE
                                    WHEN iPos = 1 THEN TRIM(VEKTOR_K (varSTR_NAME, 1, var_Trennzeichen))                    
                                    ELSE 
--                                         TRANSLATE(var_Trennzeichen,'0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzäöüß,. ;/\%?+',
--                                                                    '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzäöüß,. ;') 
--                                         || TRIM(VEKTOR_K (varSTR_NAME, 2, var_Trennzeichen))
                                         
                                           -- DECODE(is_number(varSTR_NAME),1,NULL,                                       
                                            SUBSTR(varSTR_NAME,INSTR(varSTR_NAME, var_Trennzeichen),LENGTH(varSTR_NAME) - INSTR(varSTR_NAME, var_Trennzeichen) + 1)
                                            
                                            --)                                         
                                END
                        ELSE CASE iPos 
                                WHEN 1 THEN TRIM(varSTR_NAME)
                                ELSE NULL
                                END 
                    END 
                INTO cRet
                FROM DUAL;
            END IF;

        
            --DBMS_OUTPUT.PUT_LINE (SQLERRM );
    END;
    
    RETURN cRet;
    
END F_GET_HAUSNUMMER;

/

CREATE OR REPLACE FUNCTION F_GET_GEO_CITY_NO (myPLZ     VARCHAR2,
                                              myCITY    VARCHAR2)
   RETURN VARCHAR2 DETERMINISTIC IS iRet   NUMBER (11);
   
BEGIN
   
   DECLARE
      
      CURSOR c1 IS
      
      SELECT   a.GEO_CITY_NO
        FROM   GEO_CITY A
       WHERE   A.ZIP_CODE = myPLZ
         AND   f_str_suchname (a.city) = f_str_suchname (myCITY)
         -----------------------------------------------------------------------
         -----------------------------------------------------------------------
         -----------------------------------------------------------------------
         --            order by a.geo_city_no;
         -----------------------------------------------------------------------
         -----------------------------------------------------------------------
         ORDER BY   DECODE (a.is_valid, 0, 0, 1, 2, 2, 1, 0) DESC,
                    DECODE (a.rec_source, 'INFAS', 0, 1),
                    DECODE (a.userw, 'IMPORT', 0, 1),
                    a.ext_id,
                    a.GEO_CITY_NO;

      r1   c1%ROWTYPE;
      
   BEGIN
      
      iRet := NULL;

      OPEN c1;

      FETCH c1 INTO   r1;

      IF c1%FOUND THEN
         iRet := r1.GEO_city_NO;
      END IF;

      CLOSE c1;

      RETURN iRet;
   END;
   
END F_GET_GEO_CITY_NO;

/

CREATE OR REPLACE FUNCTION F_GET_GEO_STREET_NO ( iGEO_CITY_NO    NUMBER,
                                                 mystr           VARCHAR2) 
RETURN VARCHAR2 DETERMINISTIC IS iRet   NUMBER (11);

BEGIN
   
   DECLARE
      
      CURSOR c1 IS
      
      SELECT z.GEO_STREET_NO
        FROM GEO_STREET z
       WHERE z.GEO_CITY_NO = iGEO_CITY_NO AND
             f_str_suchname (z.STREET) = f_str_suchname (mystr)
       ORDER BY   DECODE (z.is_valid, 0, 0, 1, 2, 2, 1, 0) DESC,
                  DECODE (z.rec_source, 'INFAS', 0, 1),
                  DECODE (z.userw, 'IMPORT', 0, 1),
                  z.ext_id,
                  z.GEO_CITY_NO;

      r1   c1%ROWTYPE;
      
   BEGIN
      
      iRet := NULL;

      OPEN c1;

      FETCH c1 INTO   r1;

      IF c1%FOUND THEN
         iRet := r1.GEO_STREET_NO;
      END IF;

      CLOSE c1;

      RETURN iRet;
   END;
   
END F_GET_GEO_STREET_NO;

/

COMMIT;

/

-----------------------------------------------------------------------
------ HVT's Exportieren, Strassensectionen anlegen -------------------
-----------------------------------------------------------------------
DECLARE
   
    iSTREET_SECTION_NO      NUMBER;
    iSERVICE_ROOM_NO        NUMBER;
    iTAG_TECHTYPE_NO_FTTH   NUMBER;
    iTAG_TECHTYPE_NO_FTTB   NUMBER;
    iTAG_TECHTYPE_NO_FTTC   NUMBER;
    iRet                    NUMBER;

    CURSOR c1
    IS
        
    SELECT  
        -----------------------------------------------------------------------
        ---------- STREET SECTION ---------------------------------------------
        -----------------------------------------------------------------------
        d.CITY                                  AS STR_CITY,
        d.POSTAL_CODE                           AS STR_PLZ,
        d.STREET                                AS STR_STREET ,
        d.SECTION                               AS STR_HAUSNUMMER_VOLL,
        F_GET_HAUSNUMMER(d.SECTION,1)           AS STR_HAUSNUMMER,
        F_GET_HAUSNUMMER(d.SECTION,2)           AS STR_HAUSNUMMER_ADD,
        d.SERVICE_ROOM                          AS STR_SERVICE_ROOM,
        d.ONKZ                                  AS STR_ONKZ,
        d.ASB                                   AS STR_ASB,
        d.KVZ_NUMMER                            AS STR_KVZ,
        F_GET_GEO_CITY_NO(d.POSTAL_CODE,d.CITY) AS STR_GEO_CITY_NO,
        F_GET_GEO_STREET_NO(F_GET_GEO_CITY_NO(d.POSTAL_CODE,d.CITY), d.STREET) 
                                                AS STR_GEO_STREET_NO,
        -----------------------------------------------------------------------
        ---------- SERVISE ROOM -----------------------------------------------
        -----------------------------------------------------------------------
        b.ONKZ                                  AS SR_ONKZ, 
        b.ASB                                   AS SR_ASB, 
        b.KVZ_NUMMER                            AS SR_KVZ, 
        c.tag_no                                AS SR_TAG_CONN_TYPE,   
        b.SERVICE_ROOM_TYPE                     AS SR_SERVICE_ROOM_TYPE, 
        b.SERVICE_ROOM                          AS SR_SERVICE_ROOM_NAME, 
        a.SERVICE_ROOM_NAME                     AS SERVICE_ROOM_NAME_TAIFUN,
        b.STREET                                AS SR_STREET        , 
        b.HOUSE_NUM                             AS SR_HAUSNUMMER_VOLL, 
        F_GET_HAUSNUMMER(b.HOUSE_NUM,1)         AS SR_HAUSNUMMER,
        F_GET_HAUSNUMMER(b.HOUSE_NUM,2)         AS SR_HAUSNUMMER_ADD,
        b.POSTAL_CODE                           AS SR_PLZ, 
        b.CITY                                  AS SR_ORT,
        F_GET_GEO_CITY_NO(b.POSTAL_CODE,b.CITY) AS SR_GEO_CITY_NO,
        F_GET_GEO_STREET_NO(F_GET_GEO_CITY_NO(b.POSTAL_CODE,b.CITY), b.STREET) 
                                                AS SR_GEO_STREET_NO,
        A.SERVICE_ROOM_NO,
        b.AREA_NO, 
        b.SERVICE_ROOM_TECH_TYPE 
    FROM 
        HURRICAN.V_H2T_SR_STREET@HURRICAN_TEST.MNET.DE d,
        HURRICAN.V_H2T_SERVICE_ROOM@HURRICAN_TEST.MNET.DE b, -- Testsystem
        -----------------------------------------------------------------
        --HURRICAN.V_H2T_SR_STREET@HURRICAN.MNET.DE d,
        --HURRICAN.V_H2T_SERVICE_ROOM@HURRICAN.MNET.DE b, -- Prodsystem
        ------------------------------------------------------------------
        service_room a,
        tag_definition c
    WHERE
        d.SERVICE_ROOM = b.SERVICE_ROOM 
        and b.SERVICE_ROOM_TYPE IN ('GPON','FTTC')
        AND c.tag = b.SERVICE_ROOM_TYPE
        AND a.SERVICE_ROOM_NAME(+) = b.SERVICE_ROOM
        AND A.CITY(+)   = b.city  
        AND a.ONKZ(+)   = b.ONKZ 
        AND a.STREET(+) = b.STREET
        AND NVL(a.POSTAL_CODE(+),0) = NVL(b.POSTAL_CODE,0)
        AND a.HOUSE_NUM(+) = b.HOUSE_NUM
        --AND b.SERVICE_ROOM = 'AGB-GBOTH-018' 
        AND b.SERVICE_ROOM  != NVL(a.SERVICE_ROOM_NAME,'x') 
        AND b.SERVICE_ROOM_TECH_TYPE = 'FTTC_KVZ'
    ORDER BY b.SERVICE_ROOM;

   r1                      c1%ROWTYPE;
   
BEGIN

    iTAG_TECHTYPE_NO_FTTH := 13;
    iTAG_TECHTYPE_NO_FTTB := 14;
    iTAG_TECHTYPE_NO_FTTC := 17;

    FOR r1 IN c1 LOOP
      
        IF r1.SERVICE_ROOM_NAME_TAIFUN IS NULL THEN   -- nur neu
         
            BEGIN
            
                iSERVICE_ROOM_NO:= NULL;
                ------------ SERVICE_ROOM --------------------
                BEGIN
                   SELECT SERVICE_ROOM_NO
                     INTO iSERVICE_ROOM_NO
                     FROM SERVICE_ROOM A
                    WHERE f_str_suchname (a.STREET) = f_str_suchname (r1.SR_STREET)
                          AND a.HOUSE_NUM           = r1.SR_HAUSNUMMER_VOLL
                          AND a.TAG_CONNTYPE_NO     = r1.SR_TAG_CONN_TYPE
                          AND a.POSTAL_CODE         = r1.SR_PLZ
                          AND a.SERVICE_ROOM_NAME   = r1.SR_SERVICE_ROOM_NAME;
                EXCEPTION
                   WHEN NO_DATA_FOUND THEN
                      
                      SELECT S_SERVICE_ROOM_0.NEXTVAL INTO iSERVICE_ROOM_NO FROM DUAL;

                      INSERT INTO SERVICE_ROOM A (SERVICE_ROOM_NO,
                                                  ONKZ,
                                                  ASB,
                                                  KVZ_NUMMER,
                                                  TAG_CONNTYPE_NO,
                                                  SERVICE_ROOM_NAME,
                                                  STREET,
                                                  HOUSE_NUM,
                                                  POSTAL_CODE,
                                                  CITY,
                                                  AREA_NO)
                           VALUES (iSERVICE_ROOM_NO,
                                   r1.SR_ONKZ,
                                   r1.SR_ASB,
                                   r1.SR_KVZ,
                                   r1.SR_TAG_CONN_TYPE,
                                   r1.SR_SERVICE_ROOM_NAME,
                                   r1.SR_STREET,
                                   r1.SR_HAUSNUMMER_VOLL,
                                   r1.SR_PLZ,
                                   r1.SR_ORT,
                                   r1.AREA_NO);
                END;

                IF iSERVICE_ROOM_NO IS NOT NULL THEN
                    ------------- RELEASE_DATE_SERVICE_ROOM ----------------
                    BEGIN
                       
                       SELECT SERVICE_ROOM_NO
                         INTO iRet
                         FROM RELEASE_DATE_SERVICE_ROOM a
                        WHERE a.SERVICE_ROOM_NO = iSERVICE_ROOM_NO
                          AND a.TAG_TECHTYPE_NO = CASE
                                                    WHEN r1.SERVICE_ROOM_TECH_TYPE = 'FTTH' THEN iTAG_TECHTYPE_NO_FTTH
                                                    WHEN r1.SERVICE_ROOM_TECH_TYPE = 'FTTB' THEN iTAG_TECHTYPE_NO_FTTB
                                                    WHEN r1.SERVICE_ROOM_TECH_TYPE = 'FTTC_KVZ' THEN iTAG_TECHTYPE_NO_FTTC
                                                  ELSE
                                                     NULL
                                                  END;
                                                  
                    EXCEPTION
                       WHEN NO_DATA_FOUND THEN
                       
                          INSERT INTO RELEASE_DATE_SERVICE_ROOM a               
                              (
                               SERVICE_ROOM_NO,
                               TAG_TECHTYPE_NO,
                               TECH_APPROVAL_DATE,
                               MARKETING_RELEASE_DATE,
                               USERW,
                               DATEW)
                          VALUES (
                              iSERVICE_ROOM_NO,
                              CASE
                                 WHEN r1.SERVICE_ROOM_TECH_TYPE = 'FTTH' THEN iTAG_TECHTYPE_NO_FTTH 
                                 WHEN r1.SERVICE_ROOM_TECH_TYPE = 'FTTB' THEN iTAG_TECHTYPE_NO_FTTB
                                 WHEN r1.SERVICE_ROOM_TECH_TYPE = 'FTTC_KVZ' THEN iTAG_TECHTYPE_NO_FTTC
                              ELSE
                                 NULL
                              END,
                              TO_DATE (SYSDATE, 'dd.mm.yyyy'),
                              TO_DATE (SYSDATE, 'dd.mm.yyyy'),
                              'FTTX_IMPORT',
                              SYSDATE);
                    END;

                    ------------- STREET_SECTION -------------------
                    BEGIN
                       
                       SELECT a.STREET_SECTION_NO
                         INTO iRet
                         FROM STREET_SECTION a
                        WHERE a.GEO_STREET_NO       = r1.STR_GEO_STREET_NO
                          AND a.HOUSE_NUM_START     = r1.STR_HAUSNUMMER
                          AND a.HOUSE_NUM_ADD_START = r1.STR_HAUSNUMMER_ADD
                          AND a.HOUSE_NUM_END       = r1.STR_HAUSNUMMER
                          AND a.HOUSE_NUM_ADD_END   = r1.STR_HAUSNUMMER_ADD
                          AND a.NUMBERING_KIND      = CASE
                                                        WHEN MOD(r1.STR_HAUSNUMMER, 2) = 0 THEN 'even'
                                                        ELSE 'odd'
                                                      END
                          AND a.ONKZ                = r1.STR_ONKZ
                          AND a.ASB                 = r1.STR_ASB
                          AND a.SERVICE_ROOM_NO     = iSERVICE_ROOM_NO
                          AND a.TAG_CONNTYPE_NO     = r1.SR_TAG_CONN_TYPE;

                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN

                        SELECT S_STREET_SECTION_0.NEXTVAL INTO iSTREET_SECTION_NO FROM DUAL;

                        INSERT INTO STREET_SECTION
                            (
                            STREET_SECTION_NO,
                            GEO_STREET_NO,
                            HOUSE_NUM_START,
                            HOUSE_NUM_ADD_START,
                            HOUSE_NUM_END,
                            HOUSE_NUM_ADD_END,
                            NUMBERING_KIND,
                            ONKZ,
                            ASB,
                            KVZ,
                            SERVICE_ROOM_NO,
                            EXT_ID,
                            REC_SOURCE,
                            TAG_CONNTYPE_NO,
                            DISTANCE,
                            USERW,
                            DATEW
                            )
                        VALUES
                            (
                            iSTREET_SECTION_NO,
                            r1.STR_GEO_STREET_NO,
                            r1.STR_HAUSNUMMER,
                            r1.STR_HAUSNUMMER_ADD,
                            r1.STR_HAUSNUMMER,
                            r1.STR_HAUSNUMMER_ADD,
                            CASE
                                WHEN MOD(r1.STR_HAUSNUMMER, 2) = 0 THEN 'even'
                                ELSE 'odd'
                            END,
                            r1.STR_ONKZ,
                            r1.STR_ASB,
                            r1.STR_KVZ,
                            iSERVICE_ROOM_NO,
                            NULL,
                            'HURR_FTTX',
                            r1.SR_TAG_CONN_TYPE,
                            1,              -- DISTANCE immer1
                            'FTTX_IMPORT',
                            SYSDATE
                            );
                    END;

                END IF;
                
                COMMIT;
                
            EXCEPTION
                WHEN OTHERS THEN
                    DBMS_OUTPUT.put_line (r1.SR_SERVICE_ROOM_NAME || ' Fehler: ' || SQLERRM);
                ROLLBACK;
            END;

        END IF;

    END LOOP;

END;
/

-----------------------------------------------------------------
----NV Nutzungsvereinbarung zu Gebäude anlegen -------------------
-----------------------------------------------------------------
DECLARE 

    iOWNER_PERMISSION_NO NUMBER;
    
    cursor c1
    is    
    
    select 
        -- a.STREET, a.HOUSE_NUM,
        f.geo_building_no,
        d.STREET,
        f.HOUSE_NUM, 
        f.HOUSE_NUM_Add,
        j.owner_permission_no,
        b.SERVICE_ROOM_NAME
    from 
        --HURRICAN.V_H2T_SERVICE_ROOM@HURRICAN.MNET.DE a,    -- Produktivsystem
        HURRICAN.V_H2T_SERVICE_ROOM@HURRICAN_TEST.MNET.DE a, -- Testsystem
        service_room b,
        STREET_SECTION c,
        geo_street d,
        geo_city e,
        geo_building f,
        owner_permission2building j
    where 
        c.TAG_CONNTYPE_NO = 3
        and b.SERVICE_ROOM_NAME = a.SERVICE_ROOM
        --and a.SERVICE_ROOM not like 'AGB%'
        --and a.SERVICE_ROOM not like 'M-Emm%'
        and c.HOUSE_NUM_START = f.HOUSE_NUM
        and d.geo_street_no = c.geo_street_no
        and e.geo_city_no = d.geo_city_no
        and f.geo_street_no = c.geo_street_no
        and f.geo_city_no = d.geo_city_no
        and j.geo_building_no(+)  = f.geo_building_no 
        and j.owner_permission_no is null
        and c.SERVICE_ROOM_NO = b.SERVICE_ROOM_NO
        --
        and F_GET_HAUSNUMMER(a.HOUSE_NUM,1) = f.HOUSE_NUM
        --and F_GET_HAUSNUMMER(b.HOUSE_NUM,2) = f.HOUSE_NUM_ADD
        --and f.is_valid = '1'
        --AND a.SERVICE_ROOM = 'AGB-GBOTH-010_1/3' 
    group by
        f.geo_building_no,
        d.STREET,
        f.HOUSE_NUM, 
        f.HOUSE_NUM_ADD,
        j.owner_permission_no,
        b.SERVICE_ROOM_NAME;  
        
    r1 c1%rowtype;
    
BEGIN
    
    FOR R1 IN C1 LOOP
    
        CASE
            WHEN r1.SERVICE_ROOM_NAME like 'AGB%' THEN iOWNER_PERMISSION_NO := 22;
        ELSE 
            --a.SERVICE_ROOM like 'M-Emm%'
            iOWNER_PERMISSION_NO := 21;
        END CASE;
        
        BEGIN
            
            SELECT OWNER_PERMISSION_NO
              INTO iOWNER_PERMISSION_NO
              FROM OWNER_PERMISSION a
             WHERE OWNER_PERMISSION_NO = iOWNER_PERMISSION_NO;
        
        EXCEPTION
        
            WHEN NO_DATA_FOUND THEN
                
            SELECT S_OWNER_PERMISSION_0.NEXTVAL INTO iOWNER_PERMISSION_NO FROM DUAL;

            CASE 
            
                WHEN iOWNER_PERMISSION_NO = 21 THEN
                    Insert into OWNER_PERMISSION
                       (OWNER_PERMISSION_NO, OWNER_TYPE, OWNER, ADDRESS, PHONE, 
                        FAX, EMAIL, GEE_BEANTRAGT, GEE_RECHTLICH, DOCUMENT_ID, 
                        USERW, DATEW)
                     Values
                       (iOWNER_PERMISSION_NO, 'Firma', 'M-net Telekommunikations GmbH', 'Emmy-Noether-Str. 2, 80992, München, Cluster München', '0123456', 
                        '0123456', 'test@m-net.de', TO_DATE('01.11.2009', 'DD.MM.YYYY'), TO_DATE('01.11.2009', 'DD.MM.YYYY'), 1, 'FTTX-INTIT', sysdate);
                
                WHEN iOWNER_PERMISSION_NO = 22 THEN
                   
                    Insert into OWNER_PERMISSION
                       (OWNER_PERMISSION_NO, OWNER_TYPE, OWNER, ADDRESS, PHONE, 
                        FAX, EMAIL, GEE_BEANTRAGT, GEE_RECHTLICH, DOCUMENT_ID, 
                        USERW, DATEW)
                     Values
                       (iOWNER_PERMISSION_NO, 'Firma', 'M-net Telekommunikations GmbH', 'Emmy-Noether-Str. 2, 80992, München, Cluster Augsburg', '0123456', 
                        '0123456', 'test@m-net.de', TO_DATE('01.11.2009', 'DD.MM.YYYY'), TO_DATE('01.11.2009', 'DD.MM.YYYY'), 1, 'FTTX-INTIT', sysdate);    
            END CASE;
            
            COMMIT;

        END;

        BEGIN
        
            INSERT INTO OWNER_PERMISSION2BUILDING E (OWNER_PERMISSION_NO, GEO_BUILDING_NO, USERW, DATEW)
            VALUES (iOWNER_PERMISSION_NO, r1.GEO_BUILDING_NO, 'FTTX-INTIT', sysdate);
        
            COMMIT;
            
        EXCEPTION
            WHEN OTHERS THEN 
                NULL;
                
        END;
        
        COMMIT;
                                    
    END LOOP;
    
END; 

/

update RELEASE_DATE_SERVICE_ROOM a 
set TECH_APPROVAL_DATE = to_date(to_char(TECH_APPROVAL_DATE,'ddmm') || '2010','dd.mm.yyyy')
where to_char(TECH_APPROVAL_DATE,'yyyy') = '0010';

/

update RELEASE_DATE_SERVICE_ROOM a 
set MARKETING_RELEASE_DATE = to_date(to_char(MARKETING_RELEASE_DATE,'ddmm') || '2010','dd.mm.yyyy')
where to_char(MARKETING_RELEASE_DATE,'yyyy') = '0010';

/
COMMIT;

/

DROP FUNCTION VEKTOR_K; 

/

DROP FUNCTION F_GET_1ST_NO_NUM;

/

DROP FUNCTION F_GET_HAUSNUMMER;

/

DROP FUNCTION F_GET_GEO_CITY_NO;

/

DROP FUNCTION F_GET_GEO_STREET_NO;

/

-- E N D ----