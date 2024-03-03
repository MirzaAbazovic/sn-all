package de.augustakom.hurrican.gui.tools.wbci.helper;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTable;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.PreAgreementType;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.Technologie;

/**
 * Tests the class {@link PreAgreementOverviewPanelHelper}
 * <p>
 * Created by wieran on 31.01.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PreAgreementOverviewPanelHelperTest {

    @Mock
    private PreAgreementTable preAgreementTable;

    @Mock
    private PreAgreementTableModel preAgreementTableModel;

    @Mock
    private PreAgreementOverviewPanelHelper.SendWholesalePVAction sendWholesalePVAction;

    @InjectMocks
    private PreAgreementOverviewPanelHelper preAgreementOverviewPanelHelper;

    /**
     * Test that {@link PreAgreementType} PK should enable action
     *
     * @throws Exception
     */
    @Test
    public void testEnableSendWholesalePVActionAuf_PK() throws Exception {
        setupMocks(createPreAgreementVO(PreAgreementType.PK));

        preAgreementOverviewPanelHelper.enableSendWholesalePVActionAuf(preAgreementTable, sendWholesalePVAction);

        verify(sendWholesalePVAction).setEnabled(eq(false));
    }

    /**
     * Test that {@link PreAgreementType} GK should disable action
     *
     * @throws Exception
     */
    @Test
    public void testEnableSendWholesalePVActionAuf_GK() throws Exception {
        setupMocks(createPreAgreementVO(PreAgreementType.GK));

        preAgreementOverviewPanelHelper.enableSendWholesalePVActionAuf(preAgreementTable, sendWholesalePVAction);

        verify(sendWholesalePVAction).setEnabled(eq(false));
    }

    /**
     * Test that {@link PreAgreementType} WS should enable action
     *
     * @throws Exception
     */
    @Test
    public void testEnableSendWholesalePVActionAuf_WS() throws Exception {
        setupMocks(createPreAgreementVO(PreAgreementType.WS));

        preAgreementOverviewPanelHelper.enableSendWholesalePVActionAuf(preAgreementTable, sendWholesalePVAction);

        verify(sendWholesalePVAction).setEnabled(eq(true));
    }

    /**
     * void {@link PreAgreementVO} should disable action
     */
    @Test
    public void testEnableFalseIfPreagreementVOisNull() {
        PreAgreementVO preAgreementVO = null;
        setupMocks(preAgreementVO);

        preAgreementOverviewPanelHelper.enableSendWholesalePVActionAuf(preAgreementTable, sendWholesalePVAction);

        verify(sendWholesalePVAction).setEnabled(eq(false));
    }

    private void setupMocks(PreAgreementVO preAgreementVO) {
        when(preAgreementTable.getSelectedRow()).thenReturn(0);
        when(preAgreementTableModel.getDataAtRow(0)).thenReturn(preAgreementVO);
        AKTableSorter<PreAgreementVO> akTableSorter = new AKTableSorter<>(preAgreementTableModel);
        when(preAgreementTable.getModel()).thenReturn(akTableSorter);
    }

    private PreAgreementVO createPreAgreementVO(PreAgreementType preAgreementType) {
        PreAgreementVO preAgreementVO = new PreAgreementVO();
        preAgreementVO.setPreAgreementType(preAgreementType);
        preAgreementVO.setMnetTechnologie(Technologie.FTTH);
        preAgreementVO.setEkpAbg(CarrierCode.EINS_UND_EINS);
        return preAgreementVO;
    }
}