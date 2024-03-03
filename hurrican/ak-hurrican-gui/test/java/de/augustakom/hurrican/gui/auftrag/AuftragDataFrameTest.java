package de.augustakom.hurrican.gui.auftrag;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;

@Test(groups = BaseTest.UNIT)
public class AuftragDataFrameTest {


    @Mock
    private ProduktService produktService;

    @Mock
    private CCAuftragService auftragService;

    @Mock
    private KundenService kundenService;

    private AuftragDataFrame cut;

    //Prevent initilaization of gui components through static Methods
    private class Testling extends AuftragDataFrame {
        Testling(ProduktService produktService, CCAuftragService auftragService, KundenService kundenService) {
            super.produktService = produktService;
            super.auftragService = auftragService;
            super.kundenService = kundenService;
        }

        @Override
        void initServices() {
            //do nothing
        }

        @Override
        protected void createGUI() {
            //do nothing
        }

        @Override
        void createGUI4Auftrag() {
            //do nothing
        }
    }

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.cut = GuiActionRunner.execute(new GuiQuery<Testling>() {
            @Override
            protected Testling executeInEDT() throws Throwable {
                return new Testling(produktService, auftragService, kundenService);
            }
        });
    }

    @Test
    public void testSetModelLoadsProdukt4Auftrag() throws Exception {
        final AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withRandomId()
                .withKundeNo(RandomTools.createLong());

        final AuftragDaten model = new AuftragDatenBuilder()
                .withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .build();

        final Produkt produkt = new ProduktBuilder()
                .withRandomId()
                .build();

        final Kunde kunde = new Kunde();

        when(produktService.findProdukt4Auftrag(model.getAuftragId())).thenReturn(produkt);
        when(auftragService.findAuftragById(model.getAuftragId())).thenReturn(auftragBuilder.get());
        when(kundenService.findKunde(auftragBuilder.getKundeNo())).thenReturn(kunde);

        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                cut.setModel(model);
                assertThat(cut.produkt, equalTo(produkt));
            }
        });
    }
}
