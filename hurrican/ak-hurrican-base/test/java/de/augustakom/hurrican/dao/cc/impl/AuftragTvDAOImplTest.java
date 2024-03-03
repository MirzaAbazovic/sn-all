package de.augustakom.hurrican.dao.cc.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.AuftragTvDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.shared.view.TvFeedView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class AuftragTvDAOImplTest extends AbstractHurricanBaseServiceTest {

    private static final ZonedDateTime TODAY = ZonedDateTime.now().with(LocalDate.now().atStartOfDay());
    private static final ZonedDateTime YESTERDAY = TODAY.minusDays(1);
    private static final ZonedDateTime TOMORROW = TODAY.plusDays(1);

    private static final Function<List<Auftrag>, List<Long>> AUFTRAG_ID_EXTRACTOR = new Function<List<Auftrag>, List<Long>>() {
        @Override
        public List<Long> apply(List<Auftrag> in) {
            List<Long> out = new ArrayList<>(in.size());
            if (CollectionUtils.isNotEmpty(in)) {
                for (Auftrag auftrag : in) {
                    out.add(auftrag.getId());
                }
            }
            return out;
        }
    };

    @Autowired
    private AuftragTvDAO auftragTvDAO;

    @Test
    public void testFindTvFeed4MultipleAuftraege() throws Exception {
        Auftrag shouldFind = createTestAuftrag(Date.from(TOMORROW.toInstant()), null);
        Auftrag shouldNotFind1 = createTestAuftrag(Date.from(TODAY.toInstant()), null);
        Auftrag shouldNotFind2 = createTestAuftrag(Date.from(YESTERDAY.toInstant()), null);

        List<Long> auftragIds = AUFTRAG_ID_EXTRACTOR.apply(Arrays.asList(shouldFind, shouldNotFind1, shouldNotFind2));

        List<TvFeedView> tvFeed4Auftraege = auftragTvDAO.findTvFeed4Auftraege(auftragIds, Collections.<Integer>emptyList());
        Assert.assertEquals(tvFeed4Auftraege.size(), 1);
        Assert.assertEquals(tvFeed4Auftraege.get(0).getAuftragsId(), shouldFind.getAuftragId());
    }

    @Test
    public void testFindTvFeed4MitversorgteAuftraege() throws Exception {
        final Integer buendelNr = Integer.MAX_VALUE;
        Auftrag shouldFind = createTestAuftrag(Date.from(TOMORROW.toInstant()), buendelNr);
        createTestAuftrag(Date.from(TOMORROW.toInstant()), buendelNr);

        List<Long> auftragIds = AUFTRAG_ID_EXTRACTOR.apply(Arrays.asList(shouldFind));

        List<TvFeedView> tvFeed4Auftraege = auftragTvDAO.findTvFeed4Auftraege(auftragIds, Lists.newArrayList(buendelNr));
        Assert.assertEquals(tvFeed4Auftraege.size(), 2);
    }

    @Test
    public void testFindTvFeed4SingleAuftrag() throws Exception {
        Auftrag shouldFind = createTestAuftrag(Date.from(TOMORROW.toInstant()), null);

        List<Long> auftragIds = AUFTRAG_ID_EXTRACTOR.apply(Arrays.asList(shouldFind));

        List<TvFeedView> tvFeed4Auftraege = auftragTvDAO.findTvFeed4Auftraege(auftragIds, Collections.<Integer>emptyList());
        Assert.assertEquals(tvFeed4Auftraege.size(), 1);
        Assert.assertEquals(tvFeed4Auftraege.get(0).getAuftragsId(), shouldFind.getAuftragId());
    }

    @Test
    public void testFindTvFeed4NoMatchingAuftrag() throws Exception {
        Auftrag shouldFind = createTestAuftrag(Date.from(YESTERDAY.toInstant()), null);

        List<Long> auftragIds = AUFTRAG_ID_EXTRACTOR.apply(Arrays.asList(shouldFind));

        List<TvFeedView> tvFeed4Auftraege = auftragTvDAO.findTvFeed4Auftraege(auftragIds, Collections.<Integer>emptyList());
        Assert.assertEquals(tvFeed4Auftraege.size(), 0);
    }

    @Test
    public void testFindTvFeed4EmptyList() throws Exception {
        List<TvFeedView> tvFeed4Auftraege = auftragTvDAO.findTvFeed4Auftraege(null, null);
        Assert.assertEquals(tvFeed4Auftraege.size(), 0);
    }

    private Auftrag createTestAuftrag(Date rangierungGueltigBis, @Nullable Integer buendelNr) {
        Auftrag auftrag = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class).withBuendelNr(buendelNr))
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class)
                                .withEndstelleBuilder(getBuilder(EndstelleBuilder.class)
                                                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                                                                .withGueltigBis(rangierungGueltigBis)
                                                )
                                                .withHvtStandortBuilder(getBuilder(HVTStandortBuilder.class))
                                )
                ).build();
        return auftrag;
    }
}
