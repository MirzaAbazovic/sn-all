-- als gueltige Zwischenprofile benoetigte DSLAM-Profile in Muenchen
UPDATE   T_DSLAM_PROFILE
   SET   GUELTIG = 1, BEMERKUNG = 'Zwischenprofil'
 WHERE   NAME IN
               ('MAXI_8000_1280_H_D001',
                'MMAX_12032_1280_F_D001',
                'MMAX_12032_1280_F_D001_ADSL1',
                'MMAX_12032_1280_H_D001',
                'MMAX_12032_1280_H_D001_ADSL1',
                'MMAX_16000_1280_F_D001',
                'MMAX_16000_1280_H_D001',
                'MMAX_16000_1280_H_D001_ADSL1',
                'MMAX_8000_1280_F_D001',
                'MMAX_8000_1280_H_D001');