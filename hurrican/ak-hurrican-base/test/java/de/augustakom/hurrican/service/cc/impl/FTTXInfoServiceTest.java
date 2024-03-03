package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.hurrican.service.cc.impl.FTTXInfoServiceImpl.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.lang.time.DateUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Device;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.view.FTTXKundendatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.DeviceService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.FTTXInfoService;
import de.augustakom.hurrican.service.cc.FTTXInfoService.FttxPortStatus;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = BaseTest.UNIT)
public class FTTXInfoServiceTest extends BaseTest {

    @InjectMocks
    private FTTXInfoServiceImpl testling;

    @Mock
    private BillingAuftragService billingAuftragService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private ProduktService produktService;
    @Mock
    private PhysikService physikService;
    @Mock
    private KundenService kundenService;
    @Mock
    private DeviceService deviceService;
    @Mock
    private BAService baService;
    @Mock
    private HWService hwService;

    Long stdKundenNo;
    Long stdBillingId;
    Produkt stdProdukt;
    Long stdProduktId;
    Adresse stdKundenAdresse;
    Adresse stdAnschlussAdresse;
    VerbindungsBezeichnung stdVbz;
    AuftragStatus stdAuftragStatus;

    @BeforeClass
    public void setUp() throws FindException {
        testling = new FTTXInfoServiceImpl();
        MockitoAnnotations.initMocks(this);
        testPrologStubbing();
    }

    private void testPrologStubbing() throws FindException {
        stdKundenNo = Long.valueOf(1);
        stdBillingId = Long.valueOf(1);
        stdProduktId = Long.valueOf(1);

        stdAnschlussAdresse = new Adresse();
        stdAnschlussAdresse.setVorname("#Vorname#");
        stdAnschlussAdresse.setAnrede("#Anrede#");
        stdAnschlussAdresse.setFloor("#Floor#");
        stdAnschlussAdresse.setName("#Name#");

        stdKundenAdresse = new Adresse();
        stdKundenAdresse.setVorname("#Vorname#");
        stdKundenAdresse.setAnrede("#Anrede#");
        stdKundenAdresse.setName("#Name#");

        stdVbz = new VerbindungsBezeichnung();
        stdVbz.setVbz("#Vbz#");

        stdProdukt = new Produkt();
        stdProdukt.setAnschlussart("#Anschlussart#");

        stdAuftragStatus = new AuftragStatus();
        stdAuftragStatus.setStatusText("#StatusText#");

        when(billingAuftragService.findAnschlussAdresse4Auftrag(stdBillingId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(stdAnschlussAdresse);
        when(billingAuftragService.findAuftrag(stdBillingId)).thenReturn(null);
        when(kundenService.getAdresse4Kunde(stdKundenNo)).thenReturn(stdKundenAdresse);
        when(kundenService.findKunde(stdKundenNo)).thenReturn(null);
        when(deviceService.findDevices4Auftrag(stdBillingId,
                Device.PROV_SYSTEM_HURRICAN, Device.DEVICE_CLASS_IAD)).thenReturn(null);
        when(produktService.findProdukt(stdProduktId)).thenReturn(stdProdukt);
        when(auftragService.findAuftragStatus(AuftragStatus.IN_BETRIEB)).thenReturn(stdAuftragStatus);
        when(auftragService.findAuftragStatus(AuftragStatus.TECHNISCHE_REALISIERUNG)).thenReturn(stdAuftragStatus);
    }

    /**
     * Datum in der Vergangenheit nicht erlaubt
     */
    private FTTXKundendatenView datumInDerVergangenheit() {
        FTTXKundendatenView view = new FTTXKundendatenView();

        // Request Parameter
        view.setDatum(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -1));

        return view;
    }

    /**
     * FTTH ohne Port (fuehrt wie MDU ohne Port zum Fehler)
     */
    private FTTXKundendatenView ftthOhnePort() {
        FTTXKundendatenView view = new FTTXKundendatenView();
        String ontBezeichner = "ONT-400103";

        // Request Parameter
        view.setGeraetebezeichnung(ontBezeichner);
        view.setPort(null);
        view.setDatum(null);

        return view;
    }


    /**
     * FTTH zu ONT keine Rangierung verfügbar
     */
    private FTTXKundendatenView zuOntKeineRangierungVerfuegbar() {
        try {
            FTTXKundendatenView view = new FTTXKundendatenView();
            String ontBezeichner = "ONT-400104";
            String portBezeichner = "1-1";

            HWOnt ont = new HWOnt();
            ont.setId(104L);
            List<HWBaugruppe> ontBaugruppenListe = new ArrayList<HWBaugruppe>();
            HWBaugruppe hwBaugruppe = new HWBaugruppe();
            hwBaugruppe.setId(104L);
            hwBaugruppe.setRackId(ont.getId());
            ontBaugruppenListe.add(hwBaugruppe);
            Equipment equipment = new Equipment();
            equipment.setId(104L);

            when(hwService.findActiveRackByBezeichnung(ontBezeichner)).thenReturn(ont);
            when(hwService.findBaugruppen4Rack(ont.getId())).thenReturn(ontBaugruppenListe);
            when(rangierungsService.findEquipmentByBaugruppe(hwBaugruppe.getId(), portBezeichner, null)).thenReturn(equipment);
            when(rangierungsService.findRangierung4Equipment(equipment.getId(), Boolean.TRUE)).thenReturn(null);

            // Request Parameter
            view.setGeraetebezeichnung(ontBezeichner);
            view.setPort(portBezeichner);
            view.setDatum(null);

            return view;
        }
        catch (FindException e) {
            return null;
        }
    }

    /**
     * FTTH 1 aktiver Auftrag
     */
    private FTTXKundendatenView einAktikverAuftrag() {
        try {
            FTTXKundendatenView view = new FTTXKundendatenView();
            String ontBezeichner = "ONT-400105";
            String portBezeichner = "1-1";

            HWOnt ont = new HWOnt();
            ont.setId(105L);
            List<HWBaugruppe> ontBaugruppenListe = new ArrayList<HWBaugruppe>();
            HWBaugruppe hwBaugruppe = new HWBaugruppe();
            hwBaugruppe.setId(105L);
            hwBaugruppe.setRackId(ont.getId());
            ontBaugruppenListe.add(hwBaugruppe);
            Equipment equipment = new Equipment();
            equipment.setId(105L);

            Rangierung rang = new Rangierung();
            rang.setFreigegeben(Freigegeben.freigegeben);
            rang.setOntId(ontBezeichner);
            rang.setEqInId(equipment.getId());
            rang.setEsId(105L);

            List<AuftragDaten> auftragDatenListe = new ArrayList<AuftragDaten>();
            AuftragDaten auftragDaten = new AuftragDaten();
            auftragDaten.setAuftragId(105L);
            auftragDaten.setProdId(stdProduktId);
            auftragDaten.setAuftragNoOrig(stdBillingId);
            auftragDaten.setAuftragStatusId(AuftragStatus.IN_BETRIEB);
            auftragDaten.setInbetriebnahme(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -1));
            auftragDatenListe.add(auftragDaten);

            Auftrag auftrag = new Auftrag();
            auftrag.setKundeNo(stdKundenNo);
            auftrag.setAuftragId(105L);

            when(hwService.findActiveRackByBezeichnung(ontBezeichner)).thenReturn(ont);
            when(hwService.findBaugruppen4Rack(ont.getId())).thenReturn(ontBaugruppenListe);
            when(rangierungsService.findEquipmentByBaugruppe(hwBaugruppe.getId(), portBezeichner, null)).thenReturn(equipment);
            when(rangierungsService.findRangierung4Equipment(equipment.getId(), Boolean.TRUE)).thenReturn(rang);
            when(auftragService.findAuftragDatenByEquipment(rang.getEqInId())).thenReturn(auftragDatenListe);
            when(auftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
            when(physikService.findVerbindungsBezeichnungByAuftragId(auftrag.getId())).thenReturn(stdVbz);

            // Request Parameter
            view.setGeraetebezeichnung(ontBezeichner);
            view.setPort(portBezeichner);
            view.setDatum(null);

            // Check this
            view.setPortstatus(FTTXInfoService.FttxPortStatus.belegt.name());
            view.setAuftragsnummerHurrican(auftrag.getId().longValue());

            return view;
        }
        catch (FindException e) {
            return null;
        }
    }

    /**
     * FTTH 2 Aufträge überschneiden sich zu heute, erster in Kündigung, zweiter in Realisierung
     */
    private FTTXKundendatenView auftraegeUeberschneidenSichZuHeute() {
        try {
            FTTXKundendatenView view = new FTTXKundendatenView();
            String ontBezeichner = "ONT-400106";
            String portBezeichner = "1-1";

            HWOnt ont = new HWOnt();
            ont.setId(106L);
            List<HWBaugruppe> ontBaugruppenListe = new ArrayList<HWBaugruppe>();
            HWBaugruppe hwBaugruppe = new HWBaugruppe();
            hwBaugruppe.setId(106L);
            hwBaugruppe.setRackId(ont.getId());
            ontBaugruppenListe.add(hwBaugruppe);
            Equipment equipment = new Equipment();
            equipment.setId(106L);

            Rangierung rang = new Rangierung();
            rang.setFreigegeben(Freigegeben.freigegeben);
            rang.setOntId(ontBezeichner);
            rang.setEqInId(equipment.getId());
            rang.setEsId(106L);

            List<AuftragDaten> auftragDatenListe = new ArrayList<AuftragDaten>();
            AuftragDaten auftragDaten1 = new AuftragDaten();
            auftragDaten1.setAuftragId(1061L);
            auftragDaten1.setProdId(stdProduktId);
            auftragDaten1.setAuftragNoOrig(stdBillingId);
            auftragDaten1.setAuftragStatusId(AuftragStatus.KUENDIGUNG_ERFASSEN);
            auftragDaten1.setInbetriebnahme(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -1));
            auftragDaten1.setKuendigung(new Date());

            AuftragDaten auftragDaten2 = new AuftragDaten();
            auftragDaten2.setAuftragId(1062L);
            auftragDaten2.setProdId(stdProduktId);
            auftragDaten2.setAuftragNoOrig(stdBillingId);
            auftragDaten2.setAuftragStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG);
            auftragDaten2.setInbetriebnahme(new Date());

            auftragDatenListe.add(auftragDaten1);
            auftragDatenListe.add(auftragDaten2);

            Auftrag auftrag = new Auftrag();
            auftrag.setKundeNo(stdKundenNo);
            auftrag.setAuftragId(1062L);

            when(hwService.findActiveRackByBezeichnung(ontBezeichner)).thenReturn(ont);
            when(hwService.findBaugruppen4Rack(ont.getId())).thenReturn(ontBaugruppenListe);
            when(rangierungsService.findEquipmentByBaugruppe(hwBaugruppe.getId(), portBezeichner, null)).thenReturn(equipment);
            when(rangierungsService.findRangierung4Equipment(equipment.getId(), Boolean.TRUE)).thenReturn(rang);
            when(auftragService.findAuftragDatenByEquipment(rang.getEqInId())).thenReturn(auftragDatenListe);
            when(auftragService.findAuftragById(auftragDaten2.getAuftragId())).thenReturn(auftrag);
            when(physikService.findVerbindungsBezeichnungByAuftragId(auftrag.getId())).thenReturn(stdVbz);

            // Request Parameter
            view.setGeraetebezeichnung(ontBezeichner);
            view.setPort(portBezeichner);
            view.setDatum(null);

            // Check this
            view.setPortstatus(FTTXInfoService.FttxPortStatus.belegt.name());
            view.setAuftragsnummerHurrican(auftrag.getId().longValue());

            return view;
        }
        catch (FindException e) {
            return null;
        }
    }

    /**
     * FTTH Rangierung ohne Auftrag
     */
    private FTTXKundendatenView rangierungOhneAuftrag() {
        try {
            FTTXKundendatenView view = new FTTXKundendatenView();
            String ontBezeichner = "ONT-400107";
            String portBezeichner = "1-1";

            HWOnt ont = new HWOnt();
            ont.setId(107L);
            List<HWBaugruppe> ontBaugruppenListe = new ArrayList<HWBaugruppe>();
            HWBaugruppe hwBaugruppe = new HWBaugruppe();
            hwBaugruppe.setId(107L);
            hwBaugruppe.setRackId(ont.getId());
            ontBaugruppenListe.add(hwBaugruppe);
            Equipment equipment = new Equipment();
            equipment.setId(107L);

            Rangierung rang = new Rangierung();
            rang.setFreigegeben(Freigegeben.freigegeben);
            rang.setOntId(ontBezeichner);
            rang.setEqInId(equipment.getId());
            rang.setEsId(107L);

            when(hwService.findActiveRackByBezeichnung(ontBezeichner)).thenReturn(ont);
            when(hwService.findBaugruppen4Rack(ont.getId())).thenReturn(ontBaugruppenListe);
            when(rangierungsService.findEquipmentByBaugruppe(hwBaugruppe.getId(), portBezeichner, null)).thenReturn(equipment);
            when(rangierungsService.findRangierung4Equipment(equipment.getId(), Boolean.TRUE)).thenReturn(rang);
            when(auftragService.findAuftragDatenByEquipment(rang.getEqInId())).thenReturn(null);

            // Request Parameter
            view.setGeraetebezeichnung(ontBezeichner);
            view.setPort(portBezeichner);
            view.setDatum(null);

            // check this
            view.setPortstatus(FttxPortStatus.reserviert.name());

            return view;
        }
        catch (FindException e) {
            return null;
        }
    }

    /**
     * FTTB keine MDU mit der Bezeichnung verfügbar
     */
    private FTTXKundendatenView keineMduMitDerBezeichnungVerfuegbar() {
        try {
            FTTXKundendatenView view = new FTTXKundendatenView();
            String mduBezeichner = "MDU-400201";
            String portBezeichner = "0-1-1";

            when(hwService.findActiveRackByBezeichnung(mduBezeichner)).thenReturn(null);

            // Request Parameter
            view.setDatum(null);
            view.setGeraetebezeichnung(mduBezeichner);
            view.setPort(portBezeichner);

            return view;
        }
        catch (FindException e) {
            return null;
        }
    }

    /**
     * FTTB 1 aktiver Auftrag, 1 Auftrag storniert
     */
    private FTTXKundendatenView einAktiverUndEinStornierterAuftrag() {
        try {
            FTTXKundendatenView view = new FTTXKundendatenView();
            String mduBezeichner = "MDU-400202";
            String portBezeichner = "0-1-1";

            HWMdu mdu = new HWMdu();
            mdu.setId(202L);
            List<HWBaugruppe> mduBaugruppenListe = new ArrayList<HWBaugruppe>();
            HWBaugruppe hwBaugruppe = new HWBaugruppe();
            hwBaugruppe.setId(202L);
            hwBaugruppe.setRackId(mdu.getId());
            mduBaugruppenListe.add(hwBaugruppe);
            Equipment equipment = new Equipment();
            equipment.setId(202L);

            Rangierung rang = new Rangierung();
            rang.setFreigegeben(Freigegeben.freigegeben);
            rang.setOntId(mduBezeichner);
            rang.setEqInId(202L);
            rang.setEsId(202L);

            List<AuftragDaten> auftragDatenListe = new ArrayList<AuftragDaten>();
            AuftragDaten auftragDaten1 = new AuftragDaten();
            auftragDaten1.setAuftragId(2021L);
            auftragDaten1.setProdId(stdProduktId);
            auftragDaten1.setAuftragNoOrig(stdBillingId);
            auftragDaten1.setAuftragStatusId(AuftragStatus.IN_BETRIEB);
            auftragDaten1.setInbetriebnahme(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -1));
            auftragDatenListe.add(auftragDaten1);

            AuftragDaten auftragDaten2 = new AuftragDaten();
            auftragDaten2.setAuftragId(2022L);
            auftragDaten2.setAuftragStatusId(AuftragStatus.STORNO);
            auftragDatenListe.add(auftragDaten2);

            Auftrag auftrag = new Auftrag();
            auftrag.setKundeNo(stdKundenNo);
            auftrag.setAuftragId(2021L);

            when(hwService.findActiveRackByBezeichnung(mduBezeichner)).thenReturn(mdu);
            when(hwService.findBaugruppen4Rack(mdu.getId())).thenReturn(mduBaugruppenListe);
            when(rangierungsService.findEquipmentByBaugruppe(hwBaugruppe.getId(), portBezeichner, null)).thenReturn(equipment);
            when(rangierungsService.findRangierung4Equipment(equipment.getId(), Boolean.TRUE)).thenReturn(rang);
            when(auftragService.findAuftragDatenByEquipment(rang.getEqInId())).thenReturn(auftragDatenListe);
            when(auftragService.findAuftragById(auftragDaten1.getAuftragId())).thenReturn(auftrag);
            when(physikService.findVerbindungsBezeichnungByAuftragId(auftrag.getId())).thenReturn(stdVbz);

            // Request Parameter
            view.setDatum(null);
            view.setGeraetebezeichnung(mduBezeichner);
            view.setPort(portBezeichner);

            // Check this
            view.setPortstatus(FTTXInfoService.FttxPortStatus.belegt.name());
            view.setAuftragsnummerHurrican(auftrag.getId().longValue());

            return view;
        }
        catch (FindException e) {
            return null;
        }
    }

    /**
     * FTTB kein Auftrag zur Rangierung/Port verfügbar
     */
    private FTTXKundendatenView keinAuftragZurRangierungVerfuegbar() {
        try {
            FTTXKundendatenView view = new FTTXKundendatenView();

            // FTTB
            // 1 aktiver Auftrag
            String portBezeichner = "0-1-1";
            String mduBezeichner = "MDU-400203";

            HWMdu mdu = new HWMdu();
            mdu.setId(203L);
            List<HWBaugruppe> mduBaugruppenListe = new ArrayList<HWBaugruppe>();
            HWBaugruppe hwBaugruppe = new HWBaugruppe();
            hwBaugruppe.setId(203L);
            hwBaugruppe.setRackId(mdu.getId());
            mduBaugruppenListe.add(hwBaugruppe);
            Equipment equipment = new Equipment();
            equipment.setId(203L);

            Rangierung rang = new Rangierung();
            rang.setFreigegeben(Freigegeben.freigegeben);
            rang.setOntId(mduBezeichner);
            rang.setEqInId(203L);
            rang.setEsId(null);

            when(hwService.findActiveRackByBezeichnung(mduBezeichner)).thenReturn(mdu);
            when(hwService.findBaugruppen4Rack(mdu.getId())).thenReturn(mduBaugruppenListe);
            when(rangierungsService.findEquipmentByBaugruppe(hwBaugruppe.getId(), portBezeichner, null)).thenReturn(equipment);
            when(rangierungsService.findRangierung4Equipment(equipment.getId(), Boolean.TRUE)).thenReturn(rang);
            when(auftragService.findAuftragDatenByEquipment(rang.getEqInId())).thenReturn(null);

            // Request Parameter
            view.setDatum(null);
            view.setGeraetebezeichnung(mduBezeichner);
            view.setPort(portBezeichner);

            // Check this
            view.setPortstatus(FTTXInfoService.FttxPortStatus.frei.name());

            return view;
        }
        catch (FindException e) {
            return null;
        }
    }

    /**
     * FTTB 1 aktiver Auftrag, Querydatum nach Kündigungstermin Auftrag, Porststatus == PORT_STATUS_FREIGABEBEREIT
     */
    private FTTXKundendatenView aktiverAuftragMitQueryDatumNachKuendigungstermin() {
        try {
            FTTXKundendatenView view = new FTTXKundendatenView();
            String mduBezeichner = "MDU-400204";
            String portBezeichner = "0-1-1";

            HWMdu mdu = new HWMdu();
            mdu.setId(204L);
            List<HWBaugruppe> mduBaugruppenListe = new ArrayList<HWBaugruppe>();
            HWBaugruppe hwBaugruppe = new HWBaugruppe();
            hwBaugruppe.setId(204L);
            hwBaugruppe.setRackId(mdu.getId());
            mduBaugruppenListe.add(hwBaugruppe);
            Equipment equipment = new Equipment();
            equipment.setId(204L);

            Rangierung rang = new Rangierung();
            rang.setFreigegeben(Freigegeben.freigegeben);
            rang.setOntId(mduBezeichner);
            rang.setEqInId(204L);
            rang.setEsId(204L);

            List<AuftragDaten> auftragDatenListe = new ArrayList<AuftragDaten>();
            AuftragDaten auftragDaten = new AuftragDaten();
            auftragDaten.setAuftragId(204L);
            auftragDaten.setProdId(stdProduktId);
            auftragDaten.setAuftragNoOrig(stdBillingId);
            auftragDaten.setAuftragStatusId(AuftragStatus.IN_BETRIEB);
            auftragDaten.setInbetriebnahme(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, -1));
            auftragDaten.setKuendigung(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, 1));
            auftragDatenListe.add(auftragDaten);

            Auftrag auftrag = new Auftrag();
            auftrag.setKundeNo(stdKundenNo);
            auftrag.setAuftragId(204L);

            when(hwService.findActiveRackByBezeichnung(mduBezeichner)).thenReturn(mdu);
            when(hwService.findBaugruppen4Rack(mdu.getId())).thenReturn(mduBaugruppenListe);
            when(rangierungsService.findEquipmentByBaugruppe(hwBaugruppe.getId(), portBezeichner, null)).thenReturn(equipment);
            when(rangierungsService.findRangierung4Equipment(equipment.getId(), Boolean.TRUE)).thenReturn(rang);
            when(auftragService.findAuftragDatenByEquipment(rang.getEqInId())).thenReturn(auftragDatenListe);
            when(auftragService.findAuftragById(auftragDaten.getAuftragId())).thenReturn(auftrag);
            when(physikService.findVerbindungsBezeichnungByAuftragId(auftrag.getId())).thenReturn(stdVbz);

            // Request Parameter
            view.setGeraetebezeichnung(mduBezeichner);
            view.setPort(portBezeichner);
            view.setDatum(DateTools.changeDate(new Date(), Calendar.DAY_OF_MONTH, 2));

            // Check this
            view.setPortstatus(FTTXInfoService.FttxPortStatus.freigabebereit.name());

            return view;
        }
        catch (FindException e) {
            return null;
        }
    }

    @DataProvider
    public Object[][] dataProviderGetKundendaten4Command() {
        return new Object[][] {
                // FTTXKundendatenView view, boolean fail
                // 1XX FTTH
                // 2XX FTTB
                { datumInDerVergangenheit(), true },  // Datum in der Vergangenheit nicht erlaubt
                { ftthOhnePort(), true },  // FTTH ohne Port (fuehrt wie MDU ohne Port zum Fehler)
                { zuOntKeineRangierungVerfuegbar(), true },  // zu ONT keine Rangierung verfügbar
                { einAktikverAuftrag(), false }, // 1 aktiver Auftrag
                { auftraegeUeberschneidenSichZuHeute(), false }, // 2 Aufträge überschneiden sich zu heute, erster in Kündigung, zweiter
                // in Realisierung
                { rangierungOhneAuftrag(), false }, // Rangierung ohne Auftrag
                { keineMduMitDerBezeichnungVerfuegbar(), true },  // keine MDU mit der Bezeichnung verfügbar
                { einAktiverUndEinStornierterAuftrag(), false }, // 1 aktiver Auftrag, 1 Auftrag storniert
                { keinAuftragZurRangierungVerfuegbar(), false }, // kein Auftrag zur Rangierung/Port verfügbar
                { aktiverAuftragMitQueryDatumNachKuendigungstermin(), false }, // 1 aktiver Auftrag, Querydatum nach Kündigungstermin Auftrag,
                // Portstatus == PORT_STATUS_FREIGABEBEREIT
        };
    }

    @Test(dataProvider = "dataProviderGetKundendaten4Command")
    public void testGetKundendaten4Command(FTTXKundendatenView viewIn, boolean fail) throws Exception {
        assertNotNull(viewIn, "Test fehlgeschlagen, da viewIn == null.");

        FTTXKundendatenView viewOut = callGetKundendaten(viewIn.getGeraetebezeichnung(), viewIn.getPort(), viewIn.getDatum());
        if (fail) {
            assertNull(viewOut, "Test muss fehl schlagen!");
        }
        else {
            assertNotNull(viewOut, "Test muss eine View liefern!");
            if (viewIn.getAuftragsnummerHurrican() != null) {
                assertEquals(viewOut.getAuftragsnummerHurrican().longValue(),
                        viewIn.getAuftragsnummerHurrican().longValue(),
                        "Test hat falsche Auftrags ID ermittelt.");
            }
            if (viewIn.getPortstatus() != null) {
                assertEquals(viewOut.getPortstatus(), viewIn.getPortstatus(),
                        "Test hat falschen Portstatus ermittelt.");
            }
        }
    }

    private FTTXKundendatenView callGetKundendaten(String geraetebezeichnung, String port, Date datum) {
        try {
            FTTXKundendatenView view = new FTTXKundendatenView();
            view.setGeraetebezeichnung(geraetebezeichnung);
            view.setPort(port);
            view.setDatum(datum);
            testling.getKundendaten4Command(view);
            return view;
        }
        catch (FindException ex) {
            return null;
        }
    }


    @DataProvider
    public Object[][] dataProviderGetPortstatus() {
        return new Object[][] {
                { Freigegeben.freigegeben, null, null, new AuftragDaten(), FttxPortStatus.belegt.name() },
                { Freigegeben.freigegeben, null, null, null, FttxPortStatus.frei.name() },
                { Freigegeben.defekt, null, null, null, FttxPortStatus.defekt.name() },
                { Freigegeben.gesperrt, null, null, null, FttxPortStatus.gesperrt.name() },
                { Freigegeben.freigegeben, Long.valueOf(-1), null, null, FttxPortStatus.freigabebereit.name() },
                { Freigegeben.freigegeben, Long.valueOf(1), null, null, FttxPortStatus.reserviert.name() }
        };
    }

    @Test(dataProvider = "dataProviderGetPortstatus")
    public void testGetPortstatus(Freigegeben freigegeben, Long esId, Long eqInId, AuftragDaten auftragDaten, String expected)
            throws FindException {
        Rangierung rang = new Rangierung();
        rang.setEsId(esId);
        rang.setFreigegeben(freigegeben);
        rang.setEqInId(eqInId);

        Date now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date datum = now;

        FTTXInfoServiceImpl cut = new FTTXInfoServiceImpl();
        String result = cut.getPortstatus(rang, auftragDaten, now, datum);
        assertEquals(result, expected);
    }

    @DataProvider
    public Object[][] rangAuftragDataProvider() {
        return new Object[][] {
                {AuftragStatus.IN_BETRIEB, RangAuftrag.inBetrieb},
                {AuftragStatus.AENDERUNG, RangAuftrag.inAenderung},
                {AuftragStatus.AENDERUNG_IM_UMLAUF, RangAuftrag.inAenderung},
                {AuftragStatus.TECHNISCHE_REALISIERUNG, RangAuftrag.inRealisierung},
                {AuftragStatus.PROJEKTIERUNG, RangAuftrag.inProjektierung},
                {AuftragStatus.KUENDIGUNG_ERFASSEN, RangAuftrag.inKuendigung},
                {AuftragStatus.KUENDIGUNG_TECHN_REAL, RangAuftrag.inKuendigung},
                {AuftragStatus.ERFASSUNG, RangAuftrag.inErfassung},
                {AuftragStatus.ERFASSUNG_SCV, RangAuftrag.inErfassung},
                {AuftragStatus.AUFTRAG_GEKUENDIGT, RangAuftrag.gekuendigt},
                {AuftragStatus.KONSOLIDIERT, RangAuftrag.nil},
        };
    }

    @Test(dataProvider = "rangAuftragDataProvider")
    public void getRangAuftrag(Long auftragStatusId, RangAuftrag expectedRangAuftrag) {
        assertEquals(testling.getRangAuftrag(getAuftragDaten(auftragStatusId)), expectedRangAuftrag);
    }

    private AuftragDaten getAuftragDaten(Long auftragStatusId) {
        AuftragDaten ad = new AuftragDaten();
        ad.setStatusId(auftragStatusId);
        return ad;
    }

    @DataProvider
    public Object[][] bestMatchDataProvider() {
        AuftragDaten auftragDatenInBetrieb = getAuftragDaten(AuftragStatus.IN_BETRIEB);
        AuftragDaten auftragDatenInAenderung = getAuftragDaten(AuftragStatus.AENDERUNG);
        AuftragDaten auftragDatenInRealisierung = getAuftragDaten(AuftragStatus.TECHNISCHE_REALISIERUNG);
        AuftragDaten auftragDatenInProjektierung = getAuftragDaten(AuftragStatus.PROJEKTIERUNG);
        AuftragDaten auftragDatenInKuendigung = getAuftragDaten(AuftragStatus.KUENDIGUNG_ERFASSEN);
        AuftragDaten auftragDatenInErfassung = getAuftragDaten(AuftragStatus.ERFASSUNG);
        AuftragDaten auftragDatenGekuendigt = getAuftragDaten(AuftragStatus.AUFTRAG_GEKUENDIGT);
        AuftragDaten auftragDatenNil = getAuftragDaten(AuftragStatus.KONSOLIDIERT);
        return new Object[][] {
                {Arrays.asList(auftragDatenInBetrieb, auftragDatenInAenderung, auftragDatenInRealisierung,
                        auftragDatenInProjektierung, auftragDatenInKuendigung, auftragDatenInErfassung,
                        auftragDatenGekuendigt, auftragDatenNil), auftragDatenInBetrieb},
                {Arrays.asList(auftragDatenInRealisierung, auftragDatenInProjektierung, auftragDatenInKuendigung,
                        auftragDatenInErfassung, auftragDatenGekuendigt, auftragDatenNil), auftragDatenInRealisierung},
                {Arrays.asList(auftragDatenInErfassung, auftragDatenGekuendigt, auftragDatenNil), auftragDatenInErfassung},
                {Arrays.asList(auftragDatenNil), null},
        };
    }

    @Test(dataProvider = "bestMatchDataProvider")
    public void getBestMatch(List<AuftragDaten> auftragDatenList, AuftragDaten expectedBestMatch) {
        assertEquals(testling.getBestMatch(auftragDatenList), expectedBestMatch);
    }

}
