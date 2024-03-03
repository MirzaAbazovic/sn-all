package de.augustakom.hurrican.gui.tools.tal.wizard.wbci;

import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.gui.swing.wizard.AKJWizardFinishVetoException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.wbci.helper.RufnummernportierungHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpBuilder;
import de.mnet.wbci.service.WbciVaService;

/**
 * Dialog zum Erfassen aller notwendigen Daten für den Geschaeftsfall "Reine Rufnummerportierung"
 *
 *
 */
public class WbciGeschaeftsfallRRPnPanel extends AbstractWbciGeschaeftsfallPanel {

    private static final long serialVersionUID = -8961991975268155343L;
    private WbciVaService<WbciGeschaeftsfallRrnp> wbciVaService;

    public WbciGeschaeftsfallRRPnPanel(AKJWizardComponents wizardComponents) {
        super(wizardComponents, GeschaeftsfallTyp.VA_RRNP);
    }

    @Override
    protected final void initServices() throws ServiceNotFoundException {
        wbciVaService = getCCService("WbciVaRrnpService", WbciVaService.class);
        super.initServices();
    }

    @Override
    public void createAndSendVorabstimmungsAnfrage() throws AKJWizardFinishVetoException {
        final WbciGeschaeftsfallBuilder<WbciGeschaeftsfallRrnp> builder = new WbciGeschaeftsfallRrnpBuilder()
                .withRufnummernportierung(this.getRufnummernportierung())
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
        WbciGeschaeftsfallRrnp wbciGeschaeftsfallRrnp = builder.build();

        VorabstimmungsAnfrage<WbciGeschaeftsfallRrnp> req =
                new VorabstimmungsAnfrageBuilder<WbciGeschaeftsfallRrnp>()
                        .withVaKundenwunschtermin(wbciGeschaeftsfallRrnp.getKundenwunschtermin())
                        .withWbciGeschaeftsfall(wbciGeschaeftsfallRrnp)
                        .build();
        validateWbciRequest(req);
        wbciVaService.createWbciVorgang(wbciGeschaeftsfallRrnp);
    }

    private Rufnummernportierung getRufnummernportierung() throws AKJWizardFinishVetoException {
        Rufnummernportierung rufnummernportierung =
                RufnummernportierungHelper.getRufnummernportierung(getSelectedPortierungszeitfenster(),
                        getRufnummerPortierungSelections(), isAlleRufnummernPortieren());
        rufnummernportierung.setPortierungskennungPKIauf(getNullableString(tfPKIAuf.getText()));
        return rufnummernportierung;
    }

}
