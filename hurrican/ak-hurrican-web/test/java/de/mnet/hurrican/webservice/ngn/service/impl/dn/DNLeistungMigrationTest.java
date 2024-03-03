package de.mnet.hurrican.webservice.ngn.service.impl.dn;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Rufnummer;

public class DNLeistungMigrationTest {

    public void  thatDnIsGroupedByDnNoOrig() {
        String auftragNoOrig = RandomStringUtils.randomNumeric(5);
        List<Rufnummer> r1 = createRandomDns(auftragNoOrig, 1L, 5);
        List<Rufnummer> r2 = createRandomDns(auftragNoOrig, 2L, 3);

        List<Rufnummer> all = Lists.newArrayList();
        all.addAll(r1);
        all.addAll(r2);

        Map<Long, List<Rufnummer>> groupByDnNoOrig = DNLeistungMigration.groupDnNoOrig(all);

        assertThat(groupByDnNoOrig.size()).isEqualTo(2);
        List<Rufnummer> r1FromMap = groupByDnNoOrig.get(1L);
        List<Rufnummer> r2FromMap = groupByDnNoOrig.get(2L);
        assertThat(r1FromMap).hasSize(5);
        assertThat(r2FromMap).hasSize(3);
    }

    public void thatActiveDnIsFound() {
        String auftragNoOrig = RandomStringUtils.randomNumeric(5);
        List<Rufnummer> r1 = createRandomDns(auftragNoOrig, 1L, 5);
        Rufnummer active = new Rufnummer();
        active.setAuftragNoOrig(Long.parseLong(auftragNoOrig));
        active.setHistStatus(BillingConstants.HIST_STATUS_AKT);
        active.setDnNoOrig(r1.get(0).getDnNoOrig());
        active.setDnNo(RandomUtils.nextLong());

        r1.add(active);

        Optional<Rufnummer> activeFoundInList = DNLeistungMigration.findActiveDn(r1);
        assertThat(activeFoundInList.isPresent()).isTrue();
        assertThat(activeFoundInList.get().getDnNo()).isEqualTo(active.getDnNo());
        assertThat(activeFoundInList.get().getDnNoOrig()).isEqualTo(active.getDnNoOrig());
    }

    private List<Rufnummer> createRandomDns(String auftragNoOrig, long dnNoOrig, int num) {
        List<Rufnummer> dns = new ArrayList<>();
        int histCount = 1;
        for (int i = 0; i < num; i++) {
            Rufnummer r = new Rufnummer();
            r.setDnNoOrig(dnNoOrig);
            r.setDnBase(RandomStringUtils.randomNumeric(5));
            r.setAuftragNoOrig(Long.parseLong(auftragNoOrig));
            r.setHistCnt(histCount++);
            dns.add(r);
        }
        return dns;
    }

}