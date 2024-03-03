CREATE OR REPLACE PROCEDURE add_lb(lb_id IN NUMBER, prod_no IN NUMBER, name_ IN VARCHAR2, extern_ls_no IN NUMBER)
IS
  row_exists NUMBER;
  BEGIN
    SELECT
      count(*)
    INTO row_exists
    FROM T_LB_2_PRODUKT
    WHERE lb_id = lb_id AND leistung__no = extern_ls_no;

    IF (row_exists = 0)
    THEN
      INSERT INTO T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION, VERSION)
      VALUES (lb_id, extern_ls_no, prod_no, name_, 0);
    END IF;

  END;
/

CALL add_lb(26, 3011, 'Prem. GF-DSL 100/10, IP-MGA', 85240);
CALL add_lb(26, 3011, 'Prem. GF-DSL 100/10 Regio, IP-MGA', 85239);
CALL add_lb(27, 3011, 'Prem. GF-DSL Dfl 100/10 IP-TK', 85236);
CALL add_lb(27, 3011, 'Prem. GF-DSL Dfl 100/10 IP-TK Regio', 85235);
CALL add_lb(26, 3011, 'Prem. GF-DSL Dfl 300/30 IP-MGA', 85242);
CALL add_lb(26, 3011, 'Prem. GF-DSL Dfl 300/30 IP-MGA Regio', 85241);
CALL add_lb(27, 3011, 'Prem. GF-DSL Dfl 300/30 IP-TK', 85238);
CALL add_lb(27, 3011, 'Prem. GF-DSL Dfl 300/30 IP-TK Regio', 85237);

CALL add_lb(16, 3401, 'SurfFon-Flat 300', 85231);

DROP PROCEDURE add_lb;
