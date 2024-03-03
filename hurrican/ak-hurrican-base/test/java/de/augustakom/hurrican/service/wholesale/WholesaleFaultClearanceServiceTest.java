package de.augustakom.hurrican.service.wholesale;

import java.util.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.wholesale.WholesaleChangeReason;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.wholesale.WholesaleFaultClearanceService.RequestedChangeReasonType;

@Test(groups = BaseTest.SERVICE)
public class WholesaleFaultClearanceServiceTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private WholesaleFaultClearanceService cut;

    @Test
    public void getChangeVdslProfileChangeReasons() throws Exception {
        Iterable<DSLAMProfileChangeReason> wholesaleReasons = Iterables.filter(getCCService(QueryCCService.class)
                .findAll(DSLAMProfileChangeReason.class), new Predicate<DSLAMProfileChangeReason>() {
            @Override
            public boolean apply(DSLAMProfileChangeReason input) {
                return input.getWholesale() == null ? Boolean.FALSE : input.getWholesale();
            }
        });
        Map<Long, DSLAMProfileChangeReason> wholesaleReasonsMap = CollectionMapConverter.convert2Map(wholesaleReasons,
                "getId", null);

        List<WholesaleChangeReason> changeReasons = cut.getChangeReasons(RequestedChangeReasonType.CHANGE_VDSL_PROFILE);
        for (WholesaleChangeReason changeReason : changeReasons) {
            DSLAMProfileChangeReason wholesaleChangeReason = wholesaleReasonsMap.remove(changeReason
                    .getChangeReasonId());
            Assert.assertNotNull(wholesaleChangeReason);
            Assert.assertEquals(changeReason.getDescription(), wholesaleChangeReason.getName());
        }
        Assert.assertEquals(wholesaleReasonsMap.size(), 0);
    }
}
