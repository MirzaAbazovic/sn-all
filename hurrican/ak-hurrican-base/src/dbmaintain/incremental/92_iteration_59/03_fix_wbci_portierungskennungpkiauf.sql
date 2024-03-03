-- Fix column PKIauf Portierungskennung in Rufnummernportierung table
ALTER TABLE T_WBCI_RUFNUMMERPORTIERUNG ADD (PORTIERUNGSKENNUNGPKIABG VARCHAR2(4));
