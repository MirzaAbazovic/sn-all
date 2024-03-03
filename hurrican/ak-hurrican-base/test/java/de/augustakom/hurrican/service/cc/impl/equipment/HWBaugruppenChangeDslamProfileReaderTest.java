package de.augustakom.hurrican.service.cc.impl.equipment;

import java.io.*;
import java.util.*;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.ArrayUtils;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.service.cc.DSLAMService;

@Test(groups = BaseTest.UNIT)
public class HWBaugruppenChangeDslamProfileReaderTest extends BaseTest {

    private static String OLD_DSLAM_PROFILE_NAME_1 = "MAXI_7168_640_F_D001_ADSL1"; // mapping H04 und H13
    private static String OLD_DSLAM_PROFILE_NAME_2 = "MAXI_1152_320_F_D001_ADSL1"; // kein Mapping
    private static String OLD_DSLAM_PROFILE_NAME_3 = "1152_640_F_DEF_DEF_noL2";    // Mapping ohne Uetv

    private static String NEW_DSLAM_PROFILE_NAME_1 = "MAXI_3552_640_F_D001_ADSL1"; // OLD_1, H04
    private static String NEW_DSLAM_PROFILE_NAME_2 = "MAXI_3552_640_H_D001";       // OLD_1, H13
    private static String NEW_DSLAM_PROFILE_NAME_3 = "1792_640_H_DEF_DEF_noL2";    // OLD_3

    final Map<String, Long> profileIds = new HashMap<>();

    private DSLAMService dslamService;

    private HWBaugruppenChangeDslamProfileReader cut;

    @BeforeMethod
    public void setUp() throws Exception {
        dslamService = Mockito.mock(DSLAMService.class);
        Mockito.when(dslamService.findDSLAMProfiles(Mockito.anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                String profileName = (String) (invocationOnMock.getArguments()[0]);
                List<DSLAMProfile> dslamProfiles = new ArrayList<>();
                if (ArrayUtils.contains(new String[] { OLD_DSLAM_PROFILE_NAME_1, OLD_DSLAM_PROFILE_NAME_2, OLD_DSLAM_PROFILE_NAME_3,
                        NEW_DSLAM_PROFILE_NAME_1, NEW_DSLAM_PROFILE_NAME_2, NEW_DSLAM_PROFILE_NAME_3 }, profileName)) {
                    Long id = profileIds.get(profileName);
                    DSLAMProfile retVal = new DSLAMProfileBuilder().withName(profileName)
                            .withRandomId().setPersist(false).build();
                    if (id == null) {
                        profileIds.put(profileName, retVal.getId());
                    }
                    else {
                        retVal.setId(id);
                    }
                    dslamProfiles.add(retVal);
                }
                return dslamProfiles;
            }
        });
        cut = new HWBaugruppenChangeDslamProfileReader(dslamService);
    }

    @Test
    public void testLoadDslamProfilesFromXls() throws Exception {
        String filePath = getClass().getResource("/HwBaugruppenChangeDslamProfileMapping.xlsx").getFile();
        Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMap = cut.loadDslamProfilesFromXls(new File(filePath));
        Assert.assertEquals(dslamProfileMap.size(), 3); // multimap hält keine "null" values

        Assert.assertEquals(dslamProfileMap.get(new Pair<>(profileIds.get(OLD_DSLAM_PROFILE_NAME_1),
                Uebertragungsverfahren.H04)).iterator().next().getName(), NEW_DSLAM_PROFILE_NAME_1);
        Assert.assertEquals(dslamProfileMap.get(new Pair<>(profileIds.get(OLD_DSLAM_PROFILE_NAME_1),
                Uebertragungsverfahren.H13)).iterator().next().getName(), NEW_DSLAM_PROFILE_NAME_2);

        Assert.assertEquals(dslamProfileMap.get(new Pair<Long, Uebertragungsverfahren>(profileIds.get(OLD_DSLAM_PROFILE_NAME_3),
                null)).iterator().next().getName(), NEW_DSLAM_PROFILE_NAME_3);
    }
}
