/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 16:22:57
 */
package de.mnet.wita.aggregator;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.RufnummerOnkz;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.WitaCBVorgang;

// @formatter:off
/**
 * Aggregator, um fuer eine Verbundleistung die {@link de.mnet.wita.message.auftrag.BestandsSuche} zu erstellen. <br/>
 * Bei der Bestandssuche handelt es sich um eine Identifizierung der zu uebernehmenden Leitung auf Seiten der DTAG.
 * <br/> <br/>
 * <b>Sonderfaelle:</b>
 * <pre>
 *   * Anlagenanschluesse:
 *   ** Bei Anlagenanschluessen wird von den Bearbeitern statt der Rufnummer eine Leitungsbezeichnung fuer den
 *      Anlagenanschluss eingetragen, die nur auf der Kundenrechnung steht (diese Leitungsbezeichnung hat nichts
 *      mit der WITA LBZ zu tun!); somit darf bei Wechsel von DTAG zu M-net bei Anlagenanschluessen die Rufnummer
 *      nicht ueber die WBCI ermittelt werden!
 *   * VBL mit WBCI
 *   ** Partner-Carrier ist DTAG: es wird versucht, die Bestandssuche immer zu fuellen
 *   *** falls keine Rufnummernportierung in der RUEM-VA gefunden und WBCI GF = KUE_ORN,
 *       dann die in der WITA VBL notwendige 'Bestandssuche' mit Dummy-Werten (ONKZ=111, Rufnummer=11111)
 *       automatisch befüllen (HUR-22676)
 *   ** Partner-Carrier _nicht_ DTAG: keine Ermittlung der Bestandssuche; siehe dazu auch Anmerkungen in
 *      Dokument 'Kap. 4.4 AuftragsMeldungsstruktur_Order-SST-V7.00.xls' (wenn in VBL die Bestandssuche gesetzt ist,
 *      dann hat diese Vorrang vor einer angegebenen Vertragsnummer; dadurch wird die PV statt an den Dritt-Carrier
 *      an die DTAG selbst geschickt, was dann zu einer Ablehnung fuehren wuerde.)
 * </pre>
 * <p/>
 * <b>Ausblick:</b> In WITA v9 plant die DTAG, die Angabe der Bestandssuche zu entfernen :)
 */
// @formatter:on
public class BestandsSucheVblAggregator extends AbstractWitaDataAggregator<BestandsSuche> {

    public static final String DUMMY_ONKZ = "111";
    public static final String DUMMY_DN = "11111";

    @Override
    public BestandsSuche aggregate(WitaCBVorgang cbVorgang) throws WitaDataAggregationException {
        BestandsSuche result = null;
        if (cbVorgang.getVorabstimmungsId() != null) {
            RueckmeldungVorabstimmung ruemVa = witaWbciServiceFacade.getRuemVa(cbVorgang.getVorabstimmungsId());
            if (ruemVa == null) {
                throw new WitaDataAggregationException(
                        String.format("Es konnte keine RuemVA zur VorabstimmungsId'%s' ermittelt werden!",
                                cbVorgang.getVorabstimmungsId()));
            }

            if (!CarrierCode.DTAG.equals(ruemVa.getWbciGeschaeftsfall().getEKPPartner())) {
                // falls WBCI VA _nicht_ mit DTAG, dann keine Bestandssuche erstellen!
                return null;
            }

            final Rufnummernportierung rufnummernportierung = ruemVa.getRufnummernportierung();
            if (rufnummernportierung != null) {
                if (rufnummernportierung instanceof RufnummernportierungEinzeln) {
                    final RufnummernportierungEinzeln einzeln = (RufnummernportierungEinzeln) rufnummernportierung;
                    RufnummerOnkz rufnummerOnkz = einzeln.getRufnummernOnkz().get(0);
                    result = createBestandsSuche(rufnummerOnkz.getOnkz(), rufnummerOnkz.getRufnummer());
                }
            } else if (GeschaeftsfallTyp.VA_KUE_ORN.equals(ruemVa.getWbciGeschaeftsfall().getTyp())) {
                // bei KUE_ORN mit DTAG: Dummy-Werte als Bestandssuche verwenden
                result = createBestandsSuche(DUMMY_ONKZ, DUMMY_DN);
            }
        }
        
        if (result == null) {
            Vorabstimmung vorabstimmung = witaDataService.loadVorabstimmung(cbVorgang);
            if (vorabstimmung.isBestandsSucheSet()) {
                    result = createBestandsSuche(vorabstimmung.getBestandssucheOnkzWithoutLeadingZeros(),
                            vorabstimmung.getBestandssucheDn());
            }
        }

        return result;
    }

    private BestandsSuche createBestandsSuche(String onkz, String dn) {
        return new BestandsSuche(onkz, dn, null, null, null);
    }

}
