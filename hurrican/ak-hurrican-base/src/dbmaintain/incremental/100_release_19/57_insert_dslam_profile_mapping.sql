-- function to insert profile mappings
CREATE OR REPLACE PROCEDURE add_profile_mapping(
  profil_name_alt  IN VARCHAR2,
  profil_name_neu  IN VARCHAR2,
  uetv_name        IN VARCHAR2
)
IS
  BEGIN
    INSERT INTO T_DSLAM_PROFILE_MAPPING (ID, PROFIL_NAME_ALT, UETV, PROFIL_NAME_NEU)
      VALUES (S_T_DSLAM_PROFILE_MAPPING_0.nextval, profil_name_alt, profil_name_neu, uetv_name);
  END;
/

CALL add_profile_mapping('MAXI_384_96_H_D001_ADSL1', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_512_128_H_D001_ADSL1', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_768_192_H_D001_ADSL1', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('KiTa_128_768_ADSL1', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1152_320_H_D001_ADSL1', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1152_640_H_D001_ADSL1', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1792_320_H_D001_ADSL1', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1792_640_H_D001_ADSL1', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_2336_320_H_D001_ADSL1', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_2336_640_H_D001_ADSL1', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_3552_320_H_D001_ADSL1', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_3552_640_H_D001_ADSL1', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_5024_640_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_5024_320_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_6000_512_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_6144_256_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_6144_512_H_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_7168_640_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_7168_512_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_7168_256_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_8000_800_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_12032_1280_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_16000_1280_H_D001_ADSL1', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_16000_1280_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_384_96_F_D001_ADSL1', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_512_128_F_D001_ADSL1', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_768_192_F_D001_ADSL1', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1152_320_F_D001_ADSL1', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1152_640_F_D001_ADSL1', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('AK-ADSL_1000 1152/288_ADSL1', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1792_320_F_D001_ADSL1', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1792_640_F_D001_ADSL1', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('AK-ADSL_2000 2240/288_ADSL1', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_2336_320_F_D001_ADSL1', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_2336_640_F_D001_ADSL1', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('AK-ADSL_3000 3328/288_ADSL1', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_3552_320_F_D001_ADSL1', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_3552_640_F_D001_ADSL1', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_5024_640_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_5024_320_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_6144_256_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_6144_512_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('AK-ADSL_6000 6752/576_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_7168_640_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_7168_256_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_7168_512_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MMAX_8000_800_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MMAX_12032_1280_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_24000_1400_F_D001_ADSL1', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('384_96_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('512_128_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('768_192_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1152_320_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1152_640_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1792_320_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1792_640_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('2336_320_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('2336_640_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('3552_320_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('3552_640_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('5024_320_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('5024_640_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('7168_640_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('8000_1280_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('12032_1280_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('16000_1280_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('20032_1280_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('22000_1280_H_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('384_96_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('512_128_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('768_192_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('1152_320_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('1152_640_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('1792_320_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('1792_640_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('2336_320_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('2336_640_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('3552_320_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('3552_640_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('5024_320_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('5024_640_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('7168_640_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('8000_1280_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('12032_1280_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('16000_1280_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('20032_1280_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('22000_1280_F_DEF_DEF_noL2_ADSL1force', 'H04', 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_384_96_H_D001_ADSL1', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_512_128_H_D001_ADSL1', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_768_192_H_D001_ADSL1', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('KiTa_128_768_ADSL1', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1152_320_H_D001_ADSL1', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1152_640_H_D001_ADSL1', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1792_320_H_D001_ADSL1', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1792_640_H_D001_ADSL1', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_2336_320_H_D001_ADSL1', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_2336_640_H_D001_ADSL1', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_3552_320_H_D001_ADSL1', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_3552_640_H_D001_ADSL1', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_5024_640_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_5024_320_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_6000_512_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_6144_256_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_6144_512_H_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_7168_640_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_7168_512_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_7168_256_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_8000_800_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_12032_1280_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_16000_1280_H_D001_ADSL1', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_16000_1280_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_384_96_F_D001_ADSL1', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_512_128_F_D001_ADSL1', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_768_192_F_D001_ADSL1', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1152_320_F_D001_ADSL1', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1152_640_F_D001_ADSL1', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('AK-ADSL_1000 1152/288_ADSL1', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1792_320_F_D001_ADSL1', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1792_640_F_D001_ADSL1', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('AK-ADSL_2000 2240/288_ADSL1', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_2336_320_F_D001_ADSL1', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_2336_640_F_D001_ADSL1', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('AK-ADSL_3000 3328/288_ADSL1', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_3552_320_F_D001_ADSL1', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_3552_640_F_D001_ADSL1', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_5024_640_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_5024_320_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_6144_256_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_6144_512_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('AK-ADSL_6000 6752/576_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_7168_640_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_7168_256_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_7168_512_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MMAX_8000_800_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MMAX_12032_1280_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_24000_1400_F_D001_ADSL1', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('384_96_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('512_128_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('768_192_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1152_320_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1152_640_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1792_320_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1792_640_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('2336_320_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('2336_640_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('3552_320_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('3552_640_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('5024_320_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('5024_640_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('7168_640_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('8000_1280_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('12032_1280_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('16000_1280_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('20032_1280_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('22000_1280_H_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('384_96_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('512_128_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('768_192_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('1152_320_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('1152_640_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-1-0M5-FP-TM6');
CALL add_profile_mapping('1792_320_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('1792_640_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('2336_320_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('2336_640_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('3552_320_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('3552_640_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-3-0M5-FP-TM6');
CALL add_profile_mapping('5024_320_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('5024_640_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('7168_640_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('8000_1280_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('12032_1280_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('16000_1280_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('20032_1280_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('22000_1280_F_DEF_DEF_noL2_ADSL1force', 'H13', 'A-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_384_96_H_D001', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('384_96_H_DEF_DEF_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('384_96_H_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_512_128_H_D001', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('512_128_H_DEF_DEF_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('512_128_H_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_768_192_H_D001', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('KiTa_128_768', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('768_192_H_DEF_DEF_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('768_192_H_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1152_320_H_D001', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1152_640_H_D001', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1152_320_H_DEF_DEF_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1152_640_H_DEF_DEF_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1152_320_H_9_DEF_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM9');
CALL add_profile_mapping('1152_640_H_9_DEF_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM9');
CALL add_profile_mapping('1152_320_H_12_DEF_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('1152_640_H_12_DEF_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('1152_320_H_12_12_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('1152_640_H_12_12_noL2', NULL, 'Ax-1-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('1152_320_H_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1152_640_H_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1792_320_H_D001', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_1792_640_H_D001', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1792_320_H_DEF_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1792_640_H_DEF_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1792_320_H_9_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM9');
CALL add_profile_mapping('1792_640_H_9_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM9');
CALL add_profile_mapping('1792_320_H_12_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('1792_640_H_12_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('1792_320_H_12_12_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('1792_640_H_12_12_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('1792_320_H_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('1792_640_H_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_2000_256_H_D001', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_2336_320_H_D001', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_2336_640_H_D001', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('2336_320_H_DEF_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('2336_640_H_DEF_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('2336_320_H_9_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM9');
CALL add_profile_mapping('2336_640_H_9_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM9');
CALL add_profile_mapping('2336_320_H_12_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('2336_640_H_12_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('2336_320_H_12_12_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('2336_640_H_12_12_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('2336_320_H_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('2336_640_H_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_3552_320_H_D001', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_3552_640_H_D001', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('3552_320_H_DEF_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('3552_640_H_DEF_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('3552_320_H_9_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM9');
CALL add_profile_mapping('3552_640_H_9_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM9');
CALL add_profile_mapping('3552_320_H_12_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('3552_640_H_12_DEF_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('3552_320_H_12_12_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('3552_640_H_12_12_noL2', NULL, 'Ax-3-0M5-INPx-ID8-TM12');
CALL add_profile_mapping('3552_320_H_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('3552_640_H_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_5024_640_H_D001', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_5024_320_H_D001', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('5024_320_H_DEF_DEF_noL2', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('5024_640_H_DEF_DEF_noL2', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('5024_320_H_9_DEF_noL2', NULL, 'Ax-6-1-INPx-ID8-TM9');
CALL add_profile_mapping('5024_640_H_9_DEF_noL2', NULL, 'Ax-6-1-INPx-ID8-TM9');
CALL add_profile_mapping('5024_320_H_12_DEF_noL2', NULL, 'Ax-6-1-INPx-ID8-TM12');
CALL add_profile_mapping('5024_640_H_12_DEF_noL2', NULL, 'Ax-6-1-INPx-ID8-TM12');
CALL add_profile_mapping('5024_320_H_12_12_noL2', NULL, 'Ax-6-1-INPx-ID8-TM12');
CALL add_profile_mapping('5024_640_H_12_12_noL2', NULL, 'Ax-6-1-INPx-ID8-TM12');
CALL add_profile_mapping('5024_320_H_DEF_DEF_L2enabled', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('5024_640_H_DEF_DEF_L2enabled', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_7168_640_H_D001', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_7168_256_H_D001', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_7168_512_H_D001', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('7168_640_H_DEF_DEF_noL2', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('7168_640_H_9_DEF_noL2', NULL, 'Ax-6-1-INPx-ID8-TM9');
CALL add_profile_mapping('7168_640_H_12_DEF_noL2', NULL, 'Ax-6-1-INPx-ID8-TM12');
CALL add_profile_mapping('7168_640_H_12_12_noL2', NULL, 'Ax-6-1-INPx-ID8-TM12');
CALL add_profile_mapping('7168_640_H_DEF_DEF_L2enabled', NULL, 'Ax-6-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_8000_800_H_D001', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_8000_1280_H_D001', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_8000_1280_H_D001', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_8000_1280_F_D001', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('8000_1280_H_DEF_DEF_noL2', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('8000_1280_H_9_DEF_noL2', NULL, 'Ax-12-1-INPx-ID8-TM9');
CALL add_profile_mapping('8000_1280_H_12_DEF_noL2', NULL, 'Ax-12-1-INPx-ID8-TM12');
CALL add_profile_mapping('8000_1280_H_12_12_noL2', NULL, 'Ax-12-1-INPx-ID8-TM12');
CALL add_profile_mapping('8000_1280_H_DEF_DEF_L2enabled', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_12032_800_H_D001', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_12032_1280_H_D001', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('12032_1280_H_DEF_DEF_noL2', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('12032_1280_H_9_DEF_noL2', NULL, 'Ax-12-1-INPx-ID8-TM9');
CALL add_profile_mapping('12032_1280_H_12_DEF_noL2', NULL, 'Ax-12-1-INPx-ID8-TM12');
CALL add_profile_mapping('12032_1280_H_12_12_noL2', NULL, 'Ax-12-1-INPx-ID8-TM12');
CALL add_profile_mapping('12032_1280_H_DEF_DEF_L2enabled', NULL, 'Ax-12-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_16000_1280_H_D001', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('16000_1280_H_DEF_DEF_noL2', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('16000_1280_H_9_DEF_noL2', NULL, 'Ax-18-1-INPx-ID8-TM9');
CALL add_profile_mapping('16000_1280_H_12_DEF_noL2', NULL, 'Ax-18-1-INPx-ID8-TM12');
CALL add_profile_mapping('16000_1280_H_12_12_noL2', NULL, 'Ax-18-1-INPx-ID8-TM12');
CALL add_profile_mapping('16000_1280_H_DEF_DEF_L2enabled', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_17600_1280_H_D001', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_20032_1280_H_D001', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('20032_1280_H_DEF_DEF_noL2', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('20032_1280_H_9_DEF_noL2', NULL, 'Ax-18-1-INPx-ID8-TM9');
CALL add_profile_mapping('20032_1280_H_12_DEF_noL2', NULL, 'Ax-18-1-INPx-ID8-TM12');
CALL add_profile_mapping('20032_1280_H_12_12_noL2', NULL, 'Ax-18-1-INPx-ID8-TM12');
CALL add_profile_mapping('20032_1280_H_DEF_DEF_L2enabled', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('MMAX_22000_1280_H_D001', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('22000_1280_H_DEF_DEF_noL2', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('22000_1280_H_9_DEF_noL2', NULL, 'Ax-18-1-INPx-ID8-TM9');
CALL add_profile_mapping('22000_1280_H_12_DEF_noL2', NULL, 'Ax-18-1-INPx-ID8-TM12');
CALL add_profile_mapping('22000_1280_H_12_12_noL2', NULL, 'Ax-18-1-INPx-ID8-TM12');
CALL add_profile_mapping('22000_1280_H_DEF_DEF_L2enabled', NULL, 'Ax-18-1-INPx-ID8-TM6');
CALL add_profile_mapping('MAXI_384_96_F_D001', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('384_96_F_DEF_DEF_noL2', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('384_96_F_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_512_128_F_D001', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('512_128_F_DEF_DEF_noL2', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('512_128_F_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_768_192_F_D001', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('768_192_F_DEF_DEF_noL2', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('768_192_F_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1152_320_F_D001', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1152_640_F_D001', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('AK-ADSL_1000 1152/288', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('1152_320_F_DEF_DEF_noL2', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('1152_640_F_DEF_DEF_noL2', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('1152_320_F_9_DEF_noL2', NULL, 'Ax-1-0M5-FP-TM9');
CALL add_profile_mapping('1152_640_F_9_DEF_noL2', NULL, 'Ax-1-0M5-FP-TM9');
CALL add_profile_mapping('1152_320_F_12_DEF_noL2', NULL, 'Ax-1-0M5-FP-TM12');
CALL add_profile_mapping('1152_640_F_12_DEF_noL2', NULL, 'Ax-1-0M5-FP-TM12');
CALL add_profile_mapping('1152_320_F_12_12_noL2', NULL, 'Ax-1-0M5-FP-TM12');
CALL add_profile_mapping('1152_640_F_12_12_noL2', NULL, 'Ax-1-0M5-FP-TM12');
CALL add_profile_mapping('1152_320_F_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('1152_640_F_DEF_DEF_L2enabled', NULL, 'Ax-1-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1792_320_F_D001', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_1792_640_F_D001', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('1792_320_F_DEF_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('1792_640_F_DEF_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('1792_320_F_9_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM9');
CALL add_profile_mapping('1792_640_F_9_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM9');
CALL add_profile_mapping('1792_320_F_12_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('1792_640_F_12_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('1792_320_F_12_12_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('1792_640_F_12_12_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('1792_320_F_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('1792_640_F_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('AK-ADSL_2000 2240/288', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_2336_320_F_D001', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_2336_640_F_D001', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('2336_320_F_DEF_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('2336_640_F_DEF_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('2336_320_F_9_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM9');
CALL add_profile_mapping('2336_640_F_9_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM9');
CALL add_profile_mapping('2336_320_F_12_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('2336_640_F_12_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('2336_320_F_12_12_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('2336_640_F_12_12_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('2336_320_F_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('2336_640_F_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('AK-ADSL_3000 3328/288', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_3552_320_F_D001', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_3552_640_F_D001', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('3552_320_F_DEF_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('3552_640_F_DEF_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('3552_320_F_9_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM9');
CALL add_profile_mapping('3552_640_F_9_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM9');
CALL add_profile_mapping('3552_320_F_12_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('3552_640_F_12_DEF_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('3552_320_F_12_12_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('3552_640_F_12_12_noL2', NULL, 'Ax-3-0M5-FP-TM12');
CALL add_profile_mapping('3552_320_F_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('3552_640_F_DEF_DEF_L2enabled', NULL, 'Ax-3-0M5-FP-TM6');
CALL add_profile_mapping('MAXI_5024_640_F_D001', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_5024_320_F_D001', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('5024_320_F_DEF_DEF_noL2', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('5024_640_F_DEF_DEF_noL2', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('5024_320_F_9_DEF_noL2', NULL, 'Ax-6-1-FP-TM9');
CALL add_profile_mapping('5024_640_F_9_DEF_noL2', NULL, 'Ax-6-1-FP-TM9');
CALL add_profile_mapping('5024_320_F_12_DEF_noL2', NULL, 'Ax-6-1-FP-TM12');
CALL add_profile_mapping('5024_640_F_12_DEF_noL2', NULL, 'Ax-6-1-FP-TM12');
CALL add_profile_mapping('5024_320_F_12_12_noL2', NULL, 'Ax-6-1-FP-TM12');
CALL add_profile_mapping('5024_640_F_12_12_noL2', NULL, 'Ax-6-1-FP-TM12');
CALL add_profile_mapping('5024_320_F_DEF_DEF_L2enabled', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('5024_640_F_DEF_DEF_L2enabled', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('AK-ADSL_6000 6752/576', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_7168_640_F_D001', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_7168_512_F_D001', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MAXI_7168_256_F_D001', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('7168_640_F_DEF_DEF_noL2', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('7168_640_F_9_DEF_noL2', NULL, 'Ax-6-1-FP-TM9');
CALL add_profile_mapping('7168_640_F_12_DEF_noL2', NULL, 'Ax-6-1-FP-TM12');
CALL add_profile_mapping('7168_640_F_12_12_noL2', NULL, 'Ax-6-1-FP-TM12');
CALL add_profile_mapping('7168_640_F_DEF_DEF_L2enabled', NULL, 'Ax-6-1-FP-TM6');
CALL add_profile_mapping('MMAX_8000_800_F_D001', NULL, 'Ax-12-1-FP-TM6');
CALL add_profile_mapping('MMAX_8000_1280_F_D001', NULL, 'Ax-12-1-FP-TM6');
CALL add_profile_mapping('8000_1280_F_DEF_DEF_noL2', NULL, 'Ax-12-1-FP-TM6');
CALL add_profile_mapping('8000_1280_F_9_DEF_noL2', NULL, 'Ax-12-1-FP-TM9');
CALL add_profile_mapping('8000_1280_F_12_DEF_noL2', NULL, 'Ax-12-1-FP-TM12');
CALL add_profile_mapping('8000_1280_F_12_12_noL2', NULL, 'Ax-12-1-FP-TM12');
CALL add_profile_mapping('8000_1280_F_DEF_DEF_L2enabled', NULL, 'Ax-12-1-FP-TM6');
CALL add_profile_mapping('MMAX_12032_800_F_D001', NULL, 'Ax-12-1-FP-TM6');
CALL add_profile_mapping('MMAX_12032_1280_F_D001', NULL, 'Ax-12-1-FP-TM6');
CALL add_profile_mapping('12032_1280_F_DEF_DEF_noL2', NULL, 'Ax-12-1-FP-TM6');
CALL add_profile_mapping('12032_1280_F_9_DEF_noL2', NULL, 'Ax-12-1-FP-TM9');
CALL add_profile_mapping('12032_1280_F_12_DEF_noL2', NULL, 'Ax-12-1-FP-TM12');
CALL add_profile_mapping('12032_1280_F_12_12_noL2', NULL, 'Ax-12-1-FP-TM12');
CALL add_profile_mapping('12032_1280_F_DEF_DEF_L2enabled', NULL, 'Ax-12-1-FP-TM6');
CALL add_profile_mapping('MMAX_16000_1280_F_D001', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('16000_1280_F_DEF_DEF_noL2', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('16000_1280_F_9_DEF_noL2', NULL, 'Ax-18-1-FP-TM9');
CALL add_profile_mapping('16000_1280_F_12_DEF_noL2', NULL, 'Ax-18-1-FP-TM12');
CALL add_profile_mapping('16000_1280_F_12_12_noL2', NULL, 'Ax-18-1-FP-TM12');
CALL add_profile_mapping('16000_1280_F_DEF_DEF_L2enabled', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('MMAX_17600_1280_F_D001', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('MMAX_20032_1280_F_D001', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('20032_1280_F_DEF_DEF_noL2', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('20032_1280_F_9_DEF_noL2', NULL, 'Ax-18-1-FP-TM9');
CALL add_profile_mapping('20032_1280_F_12_DEF_noL2', NULL, 'Ax-18-1-FP-TM12');
CALL add_profile_mapping('20032_1280_F_12_12_noL2', NULL, 'Ax-18-1-FP-TM12');
CALL add_profile_mapping('20032_1280_F_DEF_DEF_L2enabled', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('MMAX_22000_1280_F_D001', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('22000_1280_F_DEF_DEF_noL2', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('22000_1280_F_9_DEF_noL2', NULL, 'Ax-18-1-FP-TM9');
CALL add_profile_mapping('22000_1280_F_12_DEF_noL2', NULL, 'Ax-18-1-FP-TM12');
CALL add_profile_mapping('22000_1280_F_12_12_noL2', NULL, 'Ax-18-1-FP-TM12');
CALL add_profile_mapping('22000_1280_F_DEF_DEF_L2enabled', NULL, 'Ax-18-1-FP-TM6');
CALL add_profile_mapping('MAXI_24000_1400_F_D001', NULL, 'Ax-18-1-FP-TM6');

DROP PROCEDURE add_profile_mapping;