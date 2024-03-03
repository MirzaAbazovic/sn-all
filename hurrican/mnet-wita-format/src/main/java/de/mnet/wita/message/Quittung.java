/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 16:12:09
 */
package de.mnet.wita.message;

import java.util.*;
import javax.validation.constraints.*;

import de.mnet.wita.TeqMeldungsCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

/**
 * <b>Beispiel für eine technische Quittung (TEQ):<b>
 * <p/>
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;complex:TEQ xmlns:complex="http://wholesale.telekom.de/oss/v4/complex"
 *               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *               xsi:schemaLocation="http://wholesale.telekom.de/oss/v4/complex ../../../main/xsd/oss-type-complex.xsd"&gt;
 *    &lt;meldungsTyp&gt;TEQ&lt;/meldungsTyp&gt;
 *    &lt;externeAuftragsnummer&gt;TAL.001.NEU&lt;/externeAuftragsnummer&gt;
 *    &lt;kundenNummer&gt;1100100122&lt;/kundenNummer&gt;
 *    &lt;meldungspositionen&gt;
 *      &lt;position&gt;
 *        &lt;meldungscode&gt;0000&lt;/meldungscode&gt;
 *        &lt;meldungstext&gt;Auftrag angenommen&lt;/meldungstext&gt;
 *      &lt;/position&gt;
 *    &lt;/meldungspositionen&gt;
 * &lt;/complex:TEQ&gt;
 * </pre>
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
public class Quittung implements IncomingMessage {

    private static final long serialVersionUID = -7629591693116925835L;

    /**
     * meldungsTyp (1 - required) *
     */
    private final MeldungsType meldungsTyp;

    private WitaCdmVersion witaCdmVersion;

    /**
     * externeAuftragsnummer (0..1 - optional due to synchronous response to message) but after unmarshalling
     * externeAuftragsnummer is set for sure
     */
    private String externeAuftragsnummer;

    /**
     * kundennummerBesteller (0..1 - optional)
     */
    private final String kundennummerBesteller;

    /**
     * kundenNummer (0..1 - optional due to synchronous response to message)
     */
    private final String kundenNummer;

    /**
     * meldungspositionen (required) und position (1..* - min 1 required)
     */
    private final List<MeldungsPosition> meldungspositionen;

    private String kundennummerOfCorrespondingRequest;

    // moved kdnr und kdnrBest

    public Quittung(String extAuftragsnr, String kundennrBesteller, String kundenNr, List<MeldungsPosition> meldungspos) {
        this.meldungsTyp = MeldungsType.TEQ;
        this.externeAuftragsnummer = extAuftragsnr;
        this.kundennummerBesteller = kundennrBesteller;
        this.kundenNummer = kundenNr;
        this.meldungspositionen = meldungspos;
    }

    public Quittung(String externeAuftragsnr, String kundennrBesteller, String kundenNr, TeqMeldungsCode teqCode) {
        this(externeAuftragsnr, kundennrBesteller, kundenNr,
                Arrays.asList(new MeldungsPosition(teqCode.meldungsCode, teqCode.meldungsText)));
    }

    public Quittung(TeqMeldungsCode teqMeldungCode) {
        this(null, null, null, teqMeldungCode);
    }

    @NotNull
    public MeldungsType getMeldungsTyp() {
        return meldungsTyp;
    }

    @NotNull
    @Size(min = 1, max = 20)
    public String getExterneAuftragsnummer() {
        return externeAuftragsnummer;
    }

    public void setExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
    }

    @Size(min = 10, max = 10)
    public String getKundennummerBesteller() {
        return kundennummerBesteller;
    }

    @Size(min = 10, max = 10)
    public String getKundenNummer() {
        return kundenNummer;
    }

    public List<MeldungsPosition> getMeldungspositionen() {
        return meldungspositionen;
    }

    @Override
    public WitaCdmVersion getCdmVersion() {
        return witaCdmVersion;
    }

    @Override
    public void setCdmVersion(WitaCdmVersion witaCdmVersion) {
        this.witaCdmVersion = witaCdmVersion;
    }

    @Override
    public String toString() {
        return "Quittung [meldungspositionen=" + meldungspositionen + ", cdmVersion=" + witaCdmVersion + ", externeAuftragsnummer="
                + getExterneAuftragsnummer() + ", kundennummerBesteller=" + kundennummerBesteller + ", kundenNummer="
                + kundenNummer + "]";
    }

    public boolean isPositiveQuittung() {
        for (MeldungsPosition meldungsPosition : this.getMeldungspositionen()) {
            if (!meldungsPosition.getMeldungsCode().equals(TeqMeldungsCode.OK.meldungsCode)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gibt die fuer die Signatur der TEQ relevante Kundennummer des zugehoerigen Requests an. Dies ist entweder die
     * {@code kundennummerBesteller} oder die {@code kundennummer} aus dem Request, der der TEQ vorausgeht.
     */
    public String getKundennummerOfCorrespondingRequest() {
        return kundennummerOfCorrespondingRequest;
    }

    public void setKundennummerOfCorrespondingRequest(String kundennummerOfCorrespondingRequest) {
        this.kundennummerOfCorrespondingRequest = kundennummerOfCorrespondingRequest;
    }

}
