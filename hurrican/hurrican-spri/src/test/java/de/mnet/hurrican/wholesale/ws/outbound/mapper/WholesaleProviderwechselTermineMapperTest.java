package de.mnet.hurrican.wholesale.ws.outbound.mapper;

import static org.hamcrest.Matchers.*;

import java.time.*;
import org.junit.Assert;
import org.junit.Test;

import de.mnet.esb.cdm.supplierpartner.wholesaleorderservice.v2.order.ProviderwechselTermineType;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciGeschaeftsfall;

/**
 * Created by wieran on 03.02.2017.
 */
public class WholesaleProviderwechselTermineMapperTest {

    private WholesaleProviderwechselTermineMapper termineMapper = new WholesaleProviderwechselTermineMapper();

    @Test
    public void testCreatePvTermine_should_map_correctly() throws Exception {
        LocalDate expectedDate = LocalDate.now();
        WbciGeschaeftsfall geschaeftsfall = createWbciGeschaeftsfall(expectedDate);

        ProviderwechselTermineType pvTermine = termineMapper.createPvTermine(geschaeftsfall);

        Assert.assertThat(pvTermine.getAuftraggeberWunschtermin().getDatum(), is(expectedDate));
    }

    private WbciGeschaeftsfall createWbciGeschaeftsfall(LocalDate expectedDate) {
        WbciGeschaeftsfall geschaeftsfall = new WbciGeschaeftsfall() {
            @Override
            public GeschaeftsfallTyp getTyp() {
                return null;
            }
        };
        geschaeftsfall.setKundenwunschtermin(expectedDate);
        return geschaeftsfall;
    }
}