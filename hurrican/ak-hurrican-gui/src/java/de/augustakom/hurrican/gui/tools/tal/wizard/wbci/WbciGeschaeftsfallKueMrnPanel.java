package de.augustakom.hurrican.gui.tools.tal.wizard.wbci;

import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.gui.swing.wizard.AKJWizardFinishVetoException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.wbci.helper.RufnummernportierungHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;
import de.mnet.wbci.service.WbciVaService;

/**
 * GUI dialog to enter all necessary data for the Geschäftsfall "Kuendigun mit Rufnummerportierung"
 *
 *
 */
public class WbciGeschaeftsfallKueMrnPanel extends AbstractWbciGeschaeftsfallPanel {

    private static final long serialVersionUID = -6383439697196799202L;
    private WbciVaService<WbciGeschaeftsfallKueMrn> wbciVaService;

    public WbciGeschaeftsfallKueMrnPanel(AKJWizardComponents wizardComponents) {
        super(wizardComponents, GeschaeftsfallTyp.VA_KUE_MRN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void initServices() throws ServiceNotFoundException {
        wbciVaService = getCCService("WbciVaKueMrnService", WbciVaService.class);
        super.initServices();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createAndSendVorabstimmungsAnfrage() throws AKJWizardFinishVetoException {
        final WbciGeschaeftsfallBuilder<WbciGeschaeftsfallKueMrn> builder = new WbciGeschaeftsfallKueMrnBuilder()
                .withRufnummernportierung(
                        RufnummernportierungHelper.getRufnummernportierung(getSelectedPortierungszeitfenster(),
                                getRufnummerPortierungSelections(), isAlleRufnummernPortieren())
                )
                .withStandort(this.getStandort())
                .withEndkunde(this.getPersonOderFirma())
                .withKundenwunschtermin(this.getCustomerDesiredDate())
                .withProjekt(this.getProjekt())
                .withAuftragId(this.getAuftragId())
                .withBillingOrderNoOrig(this.getTaifunAuftragId())
                .withStrAenVorabstimmungsId(this.getSelectedStrAenVorabstimmungsId())
                .withAbgebenderEKP(this.getAbgebenderCarrier())
                .withBearbeiter(HurricanSystemRegistry.instance().getCurrentUser())
                .withAutomatable(isAutomatable());

        if (isWeitererAnschlussinhaberSelected()) {
            builder.addAnschlussinhaber(getWeitererAnschlussinhaber());
        }
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = builder.build();

        VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> req =
                new VorabstimmungsAnfrageBuilder<WbciGeschaeftsfallKueMrn>()
                        .withVaKundenwunschtermin(wbciGeschaeftsfallKueMrn.getKundenwunschtermin())
                        .withWbciGeschaeftsfall(wbciGeschaeftsfallKueMrn)
                        .build();
        validateWbciRequest(req);
        wbciVaService.createWbciVorgang(wbciGeschaeftsfallKueMrn);
    }

}
