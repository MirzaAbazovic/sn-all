package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.cc.HVTService;

@Test(groups = { BaseTest.UNIT })
public class ImportKvzAdresseCommandTest extends BaseTest {
    @InjectMocks
    @Spy
    private ImportKvzAdresseCommand cut;

    @Mock
    protected HVTService hvtService;

    @BeforeMethod
    public void setUp() {
        cut = new ImportKvzAdresseCommand();
        initMocks(this);
    }

    @Test
    public void testImport() throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        XSSFRow row = sheet.createRow(0);
        row.createCell(ImportKvzAdresseCommand.COL_ONKZ).setCellValue("911");
        row.createCell(ImportKvzAdresseCommand.COL_ASB).setCellValue("99");
        row.createCell(ImportKvzAdresseCommand.COL_KVZNR).setCellValue("A999");
        row.createCell(ImportKvzAdresseCommand.COL_PLZ).setCellValue("90429");
        row.createCell(ImportKvzAdresseCommand.COL_ORT).setCellValue("Nürnberg");
        row.createCell(ImportKvzAdresseCommand.COL_STR_HAUSNR).setCellValue("Spittlertorgraben Testlabor 13XYZ");

        List<HVTStandort> hvtStandorte = new ArrayList<HVTStandort>();
        hvtStandorte.add(new HVTStandortBuilder().withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ).get());
        Mockito.when(hvtService.findHVTStandort4DtagAsb(Mockito.anyString(), Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(hvtStandorte);
        List<Equipment> equipments = Arrays.asList(new EquipmentBuilder().get());
        Mockito.when(hvtService.findEquipments4Kvz(Mockito.anyLong(), Mockito.anyString())).thenReturn(equipments);
        cut.prepare(ImportKvzAdresseCommand.PARAM_IMPORT_ROW, row);
        SingleRowResult result = (SingleRowResult) cut.execute();
        Assert.assertEquals(result.isIgnored(), false);
        verify(hvtService, times(1)).saveKvzAdresse(any(KvzAdresse.class));
    }
}
