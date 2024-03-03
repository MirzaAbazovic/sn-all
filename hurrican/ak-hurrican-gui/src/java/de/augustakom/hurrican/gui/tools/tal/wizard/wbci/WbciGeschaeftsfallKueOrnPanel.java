package de.augustakom.hurrican.gui.tools.tal.wizard.wbci;

import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.gui.swing.wizard.AKJWizardFinishVetoException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.builder.RufnummerOnkzBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnBuilder;
import de.mnet.wbci.service.WbciVaService;

/**
 * GUI dialog to enter all necessary data for the Geschäftsfall "Kuendigun ohne Rufnummerportierung"
 *
 *
 */
public class WbciGeschaeftsfallKueOrnPanel extends AbstractWbciGeschaeftsfallPanel {

    private static final long serialVersionUID = 6702935299302221385L;
    private WbciVaService<WbciGeschaeftsfallKueOrn> wbciVaService;

    public WbciGeschaeftsfallKueOrnPanel(AKJWizardComponents wizardComponents) {
        super(wizardComponents, GeschaeftsfallTyp.VA_KUE_ORN);
    }

    @Override
    protected final void initServices() throws ServiceNotFoundException {
        wbciVaService = getCCService("WbciVaKueOrnService", WbciVaService.class);
        super.initServices();
    }

    @Override
    public void createAndSendVorabstimmungsAnfrage() throws AKJWizardFinishVetoException {
        final WbciGeschaeftsfallBuilder<WbciGeschaeftsfallKueOrn> builder = new WbciGeschaeftsfallKueOrnBuilder()
                .withAnschlussIdentifikation(this.getAnschlussIdentifikation())
                .withStandort(this.getStandort())
                .withEndkunde(this.getPersonOderFirma())
                .withKundenwunschtermin(this.getCustomerDesiredDate())
                .withProjekt(this.getProjekt())
                .withAuftragId(this.getAuftragId())
                .withBillingOrderNoOrig(this.getTaifunAuftragId())
                .withAbgebenderEKP(this.getAbgebenderCarrier())
                .withStrAenVorabstimmungsId(this.getSelectedStrAenVorabstimmungsId())
                .withBearbeiter(HurricanSystemRegistry.instance().getCurrentUser())
                .withAutomatable(isAutomatable());

        if (isWeitererAnschlussinhaberSelected()) {
            builder.addAnschlussinhaber(getWeitererAnschlussinhaber());
        }
        WbciGeschaeftsfallKueOrn wbciGeschaeftsfallKueOrn = builder.build();

        VorabstimmungsAnfrage<WbciGeschaeftsfallKueOrn> req =
                new VorabstimmungsAnfrageBuilder<WbciGeschaeftsfallKueOrn>()
                        .withVaKundenwunschtermin(wbciGeschaeftsfallKueOrn.getKundenwunschtermin())
                        .withWbciGeschaeftsfall(wbciGeschaeftsfallKueOrn)
                        .build();
        validateWbciRequest(req);
        wbciVaService.createWbciVorgang(wbciGeschaeftsfallKueOrn);
    }

    private RufnummerOnkz getAnschlussIdentifikation() {
        String onkz = getNullableString(ftfIdentificationOnkz.getText());
        String rufnummer = getNullableString(ftfIdentificationNumber.getText());

        if (onkz == null && rufnummer == null) {
            return null;
        }
        return new RufnummerOnkzBuilder()
                .withOnkz(onkz)
                .withRufnummer(rufnummer)
                .build();
    }

}
