package de.augustakom.hurrican.dao.cc.impl;

import java.time.*;
import java.time.format.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.VerlaufViewDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.cc.view.AbstractVerlaufView;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.model.cc.view.VerlaufAmRlView;
import de.augustakom.hurrican.model.cc.view.VerlaufDispoRLView;
import de.augustakom.hurrican.model.cc.view.VerlaufEXTView;
import de.augustakom.hurrican.model.cc.view.VerlaufFieldServiceView;
import de.augustakom.hurrican.model.cc.view.VerlaufStVoiceView;

@Test(groups = BaseTest.SLOW)
public class VerlaufViewDAOImplTest extends AbstractVerlaufBaseDaoTest {

    @Autowired
    VerlaufViewDAO verlaufViewDAO;

    @Test
    public void testFindRL4DispoOrNP() throws Exception {
        Date realisierungOn = Date.from(LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long abteilungId = Abteilung.NP;

        Auftrag auftrag = createTestAuftrag();
        Verlauf verlauf = createTestVerlauf(auftrag, Boolean.FALSE, BAVerlaufAnlass.NEUSCHALTUNG, realisierungOn);
        VerlaufAbteilung abteilung = createTestVerlaufAbteilung(verlauf.getId(), VerlaufStatus.STATUS_IM_UMLAUF, abteilungId, realisierungOn);

        List<VerlaufDispoRLView> rlViews = verlaufViewDAO.findRL4DispoOrNP(abteilungId, realisierungOn, realisierungOn);
        Assert.assertEquals(CollectionUtils.size(rlViews), 1);
        VerlaufDispoRLView rlView = rlViews.get(0);
        validateBauauftragView(rlView, auftrag, verlauf, abteilung);
    }

    @Test
    public void testFind4STVoice() throws Exception {
        Date realisierungOn = Date.from(LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long abteilungId = Abteilung.ST_VOICE;

        Auftrag auftrag = createTestAuftrag();
        Verlauf verlauf = createTestVerlauf(auftrag, Boolean.FALSE, BAVerlaufAnlass.NEUSCHALTUNG, realisierungOn);
        VerlaufAbteilung abteilung = createTestVerlaufAbteilung(verlauf.getId(), VerlaufStatus.STATUS_IM_UMLAUF, abteilungId, realisierungOn);

        List<VerlaufStVoiceView> stVoiceViews = verlaufViewDAO.find4STVoice(realisierungOn, realisierungOn);
        Assert.assertEquals(CollectionUtils.size(stVoiceViews), 1);
        VerlaufStVoiceView stVoiceView = stVoiceViews.get(0);
        validateBauauftragView(stVoiceView, auftrag, verlauf, abteilung);
    }

    @Test
    public void testFind4EXTERN() throws Exception {
        Date realisierungOn = Date.from(LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long abteilungId = Abteilung.FIELD_SERVICE;

        Auftrag auftrag = createTestAuftrag();
        Verlauf verlauf = createTestVerlauf(auftrag, Boolean.FALSE, BAVerlaufAnlass.NEUSCHALTUNG, realisierungOn);
        VerlaufAbteilung abteilung = createTestVerlaufAbteilung(verlauf.getId(), VerlaufStatus.STATUS_IM_UMLAUF, abteilungId, realisierungOn);

        List<VerlaufEXTView> extViews = verlaufViewDAO.find4EXTERN(abteilungId, realisierungOn, realisierungOn);
        Assert.assertEquals(CollectionUtils.size(extViews), 1);
        VerlaufEXTView extView = extViews.get(0);
        validateBauauftragView(extView, auftrag, verlauf, abteilung);
    }

    @Test
    public void testFind4FieldService() throws Exception {
        Date realisierungOn = Date.from(LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Auftrag auftrag = createTestAuftrag();
        Verlauf verlauf = createTestVerlauf(auftrag, Boolean.FALSE, BAVerlaufAnlass.NEUSCHALTUNG, realisierungOn);
        VerlaufAbteilung abteilung = createTestVerlaufAbteilung(verlauf.getId(), VerlaufStatus.STATUS_IM_UMLAUF, Abteilung.FIELD_SERVICE, realisierungOn);

        List<VerlaufFieldServiceView> fieldServiceViews = verlaufViewDAO.find4FieldService(realisierungOn, realisierungOn);
        Assert.assertEquals(CollectionUtils.size(fieldServiceViews), 1);
        VerlaufFieldServiceView fieldServiceView = fieldServiceViews.get(0);
        Assert.assertEquals(fieldServiceView.getBearbeiter(), abteilung.getBearbeiter());
        validateBauauftragView(fieldServiceView, auftrag, verlauf, abteilung);
    }

    @Test
    public void testFindRL4Am() throws Exception {
        Date realisierungOn = Date.from(LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Auftrag auftrag = createTestAuftrag();
        Verlauf verlauf = createTestVerlauf(auftrag, Boolean.FALSE, BAVerlaufAnlass.NEUSCHALTUNG, realisierungOn);
        VerlaufAbteilung abteilung = createTestVerlaufAbteilung(verlauf.getId(), VerlaufStatus.STATUS_IM_UMLAUF, Abteilung.AM, realisierungOn);

        List<VerlaufAmRlView> verlaufAmRlViews = verlaufViewDAO.findRL4Am(realisierungOn, realisierungOn);
        Assert.assertEquals(CollectionUtils.size(verlaufAmRlViews), 1);
        VerlaufAmRlView verlaufAmRlView = verlaufAmRlViews.get(0);
        Assert.assertEquals(verlaufAmRlView.getBearbeiter(), abteilung.getBearbeiter());
        validateBauauftragView(verlaufAmRlView, auftrag, verlauf, abteilung);
    }

    @Test
    public void testFindProjektierungen() throws Exception {
        Auftrag auftrag = createTestAuftrag();
        Verlauf verlauf = createTestVerlauf(auftrag, Boolean.TRUE);
        VerlaufAbteilung abteilung = createTestVerlaufAbteilung(verlauf.getId(), VerlaufStatus.STATUS_IM_UMLAUF, Abteilung.ZP_ZENTRALE_INFRASTRUKTUR);

        List<ProjektierungsView> projektierungen = verlaufViewDAO.findProjektierungen(Abteilung.ZP_ZENTRALE_INFRASTRUKTUR, true);
        Assert.assertNotNull(projektierungen);
        Optional<ProjektierungsView> projektierungsView = projektierungen.stream()
            .filter(v -> verlauf.getId().equals(v.getVerlaufId())).findFirst();
        Assert.assertTrue(projektierungsView.isPresent());
        Assert.assertEquals(projektierungsView.get().getBearbeiter(), abteilung.getBearbeiter());
        validateVerlaufView(projektierungsView.get(), auftrag, verlauf, abteilung);
    }

    @Test
    public void testFindBAVerlaufViews4KundeInShortTerm() throws Exception {
        Auftrag auftrag = createTestAuftrag();
        Verlauf verlauf = createTestVerlauf(auftrag, Boolean.FALSE, BAVerlaufAnlass.NEUSCHALTUNG);
        createTestVerlaufAbteilung(verlauf.getId(), VerlaufStatus.STATUS_IM_UMLAUF, Abteilung.ZP_ZENTRALE_INFRASTRUKTUR);
        List<AbstractBauauftragView> bauauftragViews = verlaufViewDAO.findBAVerlaufViews4KundeInShortTerm(auftrag.getKundeNo(),
                Date.from(ZonedDateTime.now().minusDays(1).toInstant()), Date.from(ZonedDateTime.now().plusDays(1).toInstant()));

        Assert.assertNotNull(bauauftragViews);
        Optional<AbstractBauauftragView> optional = bauauftragViews.stream()
                .filter(v -> verlauf.getId().equals(v.getVerlaufId())).findFirst();
        Assert.assertTrue(optional.isPresent());
        AbstractBauauftragView bauauftragView = optional.get();
        Assert.assertEquals(bauauftragView.getAuftragId(), auftrag.getAuftragId());
        Assert.assertEquals(bauauftragView.getKundeNo(), auftrag.getKundeNo());
        Assert.assertEquals(bauauftragView.getAuftragId(), auftrag.getAuftragId());
        Assert.assertEquals(bauauftragView.getVerlaufId(), verlauf.getId());
    }

    private void validateBauauftragView(AbstractBauauftragView view, Auftrag auftrag, Verlauf verlauf, VerlaufAbteilung abteilung) {
        Assert.assertEquals(view.getAnlassId(), verlauf.getAnlass());
        Assert.assertEquals(view.getNiederlassungId(), abteilung.getNiederlassungId());
        validateVerlaufView(view, auftrag, verlauf, abteilung);
    }

    private void validateVerlaufView(AbstractVerlaufView view, Auftrag auftrag, Verlauf verlauf, VerlaufAbteilung abteilung) {
        Assert.assertEquals(view.getAuftragId(), auftrag.getAuftragId());
        Assert.assertEquals(view.getKundeNo(), auftrag.getKundeNo());
        Assert.assertEquals(view.getVerlaufId(), verlauf.getId());
        Assert.assertEquals(view.getRealisierungstermin().getTime(), verlauf.getRealisierungstermin().getTime());
        Assert.assertEquals(view.getVerlaufAbtId(), abteilung.getId());
        Assert.assertEquals(view.getVerlaufAbtStatusId(), abteilung.getVerlaufStatusId());
        Assert.assertEquals(view.getHasSubOrders(), Boolean.FALSE);

        Assert.assertEquals(view.getObserveProcess(), verlauf.getObserveProcess());

    }
}
