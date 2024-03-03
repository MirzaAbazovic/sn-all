/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 13:12:35
 */
package de.augustakom.hurrican.model.exmodules.massenbenachrichtigung;

import java.util.*;

import de.augustakom.hurrican.model.exmodules.AbstractExModuleModel;
import de.augustakom.hurrican.model.shared.iface.LongIdModel;

/**
 *
 */
public class TServiceExp extends AbstractExModuleModel implements LongIdModel {

    private final static String SDSL = "SDSL";

    private Long id = null;

    /**
     * @see de.augustakom.hurrican.model.shared.iface.LongIdModel#getId()
     */
    public Long getId() {
        return id;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.LongIdModel#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    private Long tsxId = null;
    private Long tsxStaId = Long.valueOf(0);
    private Long tsxTypId = null;
    private Long tsxAufId = null;
    private Long tsxDruckId = Long.valueOf(0);
    private Long tsxVpvAsamPort = null;
    private Date tsxVon = null;
    private Date tsxTicketNrAm = null;
    private Date tsxIstTermin = null;
    private Date tsxKuendigungZum = null;
    private String tsxTemplate = null;
    private String tsxTicketNr = null;
    private String tsxTyp = TServiceExp.SDSL;
    private String tsxStatus = null;
    private String tsxAuftragsnummer = null;
    private String tsxKunde = null;
    private String tsxProdukt = null;
    private String tsxVpvAsambezeichnung = null;
    private String tsxVpvAsamkarte = null;
    private String tsxVpvDslTyp = TServiceExp.SDSL;
    private String tsxPlz = null;
    private String tsxOrt = null;
    private String tsxStrasse = null;
    private String tsxHausnr = null;
    private String tsxWohnung = null;
    private String tsxAnspr1 = null;
    private String tsxAnsprTel1 = null;
    private String tsxAnsprFax1 = null;
    private String tsxAnsprEmail1 = null;
    private String tsxAnspr2 = null;
    private String tsxAnsprTel2 = null;
    private String tsxAnsprFax2 = null;
    private String tsxAnsprEmail2 = null;
    private String tsxAnspr3 = null;
    private String tsxAnsprTel3 = null;
    private String tsxAnsprFax3 = null;
    private String tsxAnsprEmail3 = null;
    private String tsxText1 = null;
    private String tsxText2 = null;
    private String tsxText3 = null;
    private String tsxText4 = null;
    private Date tsxTimestamp = null;
    private String tsxUserName = null;
    private Date tsxUpdateTimestamp = null;
    private String tsxUpdUsername = null;


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TServiceExp [");
        if (tsxVpvDslTyp != null) {
            builder.append("tsxVpvDslTyp=");
            builder.append(tsxVpvDslTyp);
            builder.append(", ");
        }
        if (tsxAufId != null) {
            builder.append("tsxAufId=");
            builder.append(tsxAufId);
            builder.append(", ");
        }
        if (tsxAuftragsnummer != null) {
            builder.append("tsxAuftragsnummer=");
            builder.append(tsxAuftragsnummer);
            builder.append(", ");
        }
        if (tsxProdukt != null) {
            builder.append("tsxProdukt=");
            builder.append(tsxProdukt);
            builder.append(", ");
        }
        if (tsxAnspr1 != null) {
            builder.append("tsxAnspr1=");
            builder.append(tsxAnspr1);
            builder.append(", ");
        }
        if (tsxAnsprEmail1 != null) {
            builder.append("tsxAnsprEmail1=");
            builder.append(tsxAnsprEmail1);
            builder.append(", ");
        }
        if (tsxAnsprTel1 != null) {
            builder.append("tsxAnsprTel1=");
            builder.append(tsxAnsprTel1);
            builder.append(", ");
        }
        if (tsxAnsprFax1 != null) {
            builder.append("tsxAnsprFax1=");
            builder.append(tsxAnsprFax1);
            builder.append(", ");
        }
        if (tsxAnspr2 != null) {
            builder.append("tsxAnspr2=");
            builder.append(tsxAnspr2);
            builder.append(", ");
        }
        if (tsxAnsprEmail2 != null) {
            builder.append("tsxAnsprEmail2=");
            builder.append(tsxAnsprEmail2);
            builder.append(", ");
        }
        if (tsxAnsprTel2 != null) {
            builder.append("tsxAnsprTel2=");
            builder.append(tsxAnsprTel2);
            builder.append(", ");
        }
        if (tsxAnsprFax2 != null) {
            builder.append("tsxAnsprFax2=");
            builder.append(tsxAnsprFax2);
            builder.append(", ");
        }
        if (tsxAnspr3 != null) {
            builder.append("tsxAnspr3=");
            builder.append(tsxAnspr3);
            builder.append(", ");
        }
        if (tsxAnsprEmail3 != null) {
            builder.append("tsxAnsprEmail3=");
            builder.append(tsxAnsprEmail3);
            builder.append(", ");
        }
        if (tsxAnsprTel3 != null) {
            builder.append("tsxAnsprTel3=");
            builder.append(tsxAnsprTel3);
            builder.append(", ");
        }
        if (tsxAnsprFax3 != null) {
            builder.append("tsxAnsprFax3=");
            builder.append(tsxAnsprFax3);
            builder.append(", ");
        }
        if (tsxDruckId != null) {
            builder.append("tsxDruckId=");
            builder.append(tsxDruckId);
            builder.append(", ");
        }
        if (tsxIstTermin != null) {
            builder.append("tsxIstTermin=");
            builder.append(tsxIstTermin);
            builder.append(", ");
        }
        if (tsxKuendigungZum != null) {
            builder.append("tsxKuendigungZum=");
            builder.append(tsxKuendigungZum);
            builder.append(", ");
        }
        if (tsxKunde != null) {
            builder.append("tsxKunde=");
            builder.append(tsxKunde);
            builder.append(", ");
        }
        if (tsxPlz != null) {
            builder.append("tsxPlz=");
            builder.append(tsxPlz);
            builder.append(", ");
        }
        if (tsxOrt != null) {
            builder.append("tsxOrt=");
            builder.append(tsxOrt);
            builder.append(", ");
        }
        if (tsxStrasse != null) {
            builder.append("tsxStrasse=");
            builder.append(tsxStrasse);
            builder.append(", ");
        }
        if (tsxHausnr != null) {
            builder.append("tsxHausnr=");
            builder.append(tsxHausnr);
            builder.append(", ");
        }
        if (tsxWohnung != null) {
            builder.append("tsxWohnung=");
            builder.append(tsxWohnung);
            builder.append(", ");
        }
        if (tsxStaId != null) {
            builder.append("tsxStaId=");
            builder.append(tsxStaId);
            builder.append(", ");
        }
        if (tsxStatus != null) {
            builder.append("tsxStatus=");
            builder.append(tsxStatus);
            builder.append(", ");
        }
        if (tsxTemplate != null) {
            builder.append("tsxTemplate=");
            builder.append(tsxTemplate);
            builder.append(", ");
        }
        if (tsxText1 != null) {
            builder.append("tsxText1=");
            builder.append(tsxText1);
            builder.append(", ");
        }
        if (tsxText2 != null) {
            builder.append("tsxText2=");
            builder.append(tsxText2);
            builder.append(", ");
        }
        if (tsxText3 != null) {
            builder.append("tsxText3=");
            builder.append(tsxText3);
            builder.append(", ");
        }
        if (tsxText4 != null) {
            builder.append("tsxText4=");
            builder.append(tsxText4);
            builder.append(", ");
        }
        if (tsxTicketNr != null) {
            builder.append("tsxTicketNr=");
            builder.append(tsxTicketNr);
            builder.append(", ");
        }
        if (tsxTicketNrAm != null) {
            builder.append("tsxTicketNrAm=");
            builder.append(tsxTicketNrAm);
            builder.append(", ");
        }
        if (tsxTimestamp != null) {
            builder.append("tsxTimestamp=");
            builder.append(tsxTimestamp);
            builder.append(", ");
        }
        if (tsxTyp != null) {
            builder.append("tsxTyp=");
            builder.append(tsxTyp);
            builder.append(", ");
        }
        if (tsxTypId != null) {
            builder.append("tsxTypId=");
            builder.append(tsxTypId);
            builder.append(", ");
        }
        if (tsxUpdUsername != null) {
            builder.append("tsxUpdUsername=");
            builder.append(tsxUpdUsername);
            builder.append(", ");
        }
        if (tsxUpdateTimestamp != null) {
            builder.append("tsxUpdateTimestamp=");
            builder.append(tsxUpdateTimestamp);
            builder.append(", ");
        }
        if (tsxVon != null) {
            builder.append("tsxVon=");
            builder.append(tsxVon);
            builder.append(", ");
        }
        if (tsxVpvAsamPort != null) {
            builder.append("tsxVpvAsamPort=");
            builder.append(tsxVpvAsamPort);
            builder.append(", ");
        }
        if (tsxVpvAsambezeichnung != null) {
            builder.append("tsxVpvAsambezeichnung=");
            builder.append(tsxVpvAsambezeichnung);
            builder.append(", ");
        }
        if (tsxVpvAsamkarte != null) {
            builder.append("tsxVpvAsamkarte=");
            builder.append(tsxVpvAsamkarte);
            builder.append(", ");
        }
        if (tsxUserName != null) {
            builder.append("tsxUserName=");
            builder.append(tsxUserName);
        }
        builder.append("]");
        return builder.toString();
    }


    public Long getTsxId() {
        return tsxId;
    }

    public void setTsxId(Long tsxId) {
        this.tsxId = tsxId;
    }

    public Long getTsxStaId() {
        return tsxStaId;
    }

    public void setTsxStaId(Long tsxStaId) {
        this.tsxStaId = tsxStaId;
    }

    public Long getTsxTypId() {
        return tsxTypId;
    }

    public void setTsxTypId(Long tsxTypId) {
        this.tsxTypId = tsxTypId;
    }

    public Long getTsxAufId() {
        return tsxAufId;
    }

    public void setTsxAufId(Long tsxAufId) {
        this.tsxAufId = tsxAufId;
    }

    public Long getTsxDruckId() {
        return tsxDruckId;
    }

    public void setTsxDruckId(Long tsxDruckId) {
        this.tsxDruckId = tsxDruckId;
    }

    public Long getTsxVpvAsamPort() {
        return tsxVpvAsamPort;
    }

    public void setTsxVpvAsamPort(Long tsxVpvAsamPort) {
        this.tsxVpvAsamPort = tsxVpvAsamPort;
    }

    public Date getTsxVon() {
        return tsxVon;
    }

    public void setTsxVon(Date tsxVon) {
        this.tsxVon = tsxVon;
    }

    public Date getTsxTicketNrAm() {
        return tsxTicketNrAm;
    }

    public void setTsxTicketNrAm(Date tsxTicketNrAm) {
        this.tsxTicketNrAm = tsxTicketNrAm;
    }

    public Date getTsxIstTermin() {
        return tsxIstTermin;
    }

    public void setTsxIstTermin(Date tsxIstTermin) {
        this.tsxIstTermin = tsxIstTermin;
    }

    public Date getTsxKuendigungZum() {
        return tsxKuendigungZum;
    }

    public void setTsxKuendigungZum(Date tsxKuendigungZum) {
        this.tsxKuendigungZum = tsxKuendigungZum;
    }

    public String getTsxTemplate() {
        return tsxTemplate;
    }

    public void setTsxTemplate(String tsxTemplate) {
        this.tsxTemplate = tsxTemplate;
    }

    public String getTsxTicketNr() {
        return tsxTicketNr;
    }

    public void setTsxTicketNr(String tsxTicketNr) {
        this.tsxTicketNr = tsxTicketNr;
    }

    public String getTsxTyp() {
        return tsxTyp;
    }

    public void setTsxTyp(String tsxTyp) {
        this.tsxTyp = tsxTyp;
    }

    public String getTsxStatus() {
        return tsxStatus;
    }

    public void setTsxStatus(String tsxStatus) {
        this.tsxStatus = tsxStatus;
    }

    public String getTsxAuftragsnummer() {
        return tsxAuftragsnummer;
    }

    public void setTsxAuftragsnummer(String tsxAuftragsnummer) {
        this.tsxAuftragsnummer = tsxAuftragsnummer;
    }

    public String getTsxKunde() {
        return tsxKunde;
    }

    public void setTsxKunde(String tsxKunde) {
        this.tsxKunde = tsxKunde;
    }

    public String getTsxProdukt() {
        return tsxProdukt;
    }

    public void setTsxProdukt(String tsxProdukt) {
        this.tsxProdukt = tsxProdukt;
    }

    public String getTsxVpvAsambezeichnung() {
        return tsxVpvAsambezeichnung;
    }

    public void setTsxVpvAsambezeichnung(String tsxVpvAsambezeichnung) {
        this.tsxVpvAsambezeichnung = tsxVpvAsambezeichnung;
    }

    public String getTsxVpvAsamkarte() {
        return tsxVpvAsamkarte;
    }

    public void setTsxVpvAsamkarte(String tsxVpvAsamkarte) {
        this.tsxVpvAsamkarte = tsxVpvAsamkarte;
    }

    public String getTsxVpvDslTyp() {
        return tsxVpvDslTyp;
    }

    public void setTsxVpvDslTyp(String tsxVpvDslTyp) {
        this.tsxVpvDslTyp = tsxVpvDslTyp;
    }

    public String getTsxPlz() {
        return tsxPlz;
    }

    public void setTsxPlz(String tsxPlz) {
        this.tsxPlz = tsxPlz;
    }

    public String getTsxOrt() {
        return tsxOrt;
    }

    public void setTsxOrt(String tsxOrt) {
        this.tsxOrt = tsxOrt;
    }

    public String getTsxStrasse() {
        return tsxStrasse;
    }

    public void setTsxStrasse(String tsxStrasse) {
        this.tsxStrasse = tsxStrasse;
    }

    public String getTsxHausnr() {
        return tsxHausnr;
    }

    public void setTsxHausnr(String tsxHausnr) {
        this.tsxHausnr = tsxHausnr;
    }

    public String getTsxWohnung() {
        return tsxWohnung;
    }

    public void setTsxWohnung(String tsxWohnung) {
        this.tsxWohnung = tsxWohnung;
    }

    public String getTsxAnspr1() {
        return tsxAnspr1;
    }

    public void setTsxAnspr1(String tsxAnspr1) {
        this.tsxAnspr1 = tsxAnspr1;
    }

    public String getTsxAnsprTel1() {
        return tsxAnsprTel1;
    }

    public void setTsxAnsprTel1(String tsxAnsprTel1) {
        this.tsxAnsprTel1 = tsxAnsprTel1;
    }

    public String getTsxAnsprFax1() {
        return tsxAnsprFax1;
    }

    public void setTsxAnsprFax1(String tsxAnsprFax1) {
        this.tsxAnsprFax1 = tsxAnsprFax1;
    }

    public String getTsxAnsprEmail1() {
        return tsxAnsprEmail1;
    }

    public void setTsxAnsprEmail1(String tsxAnsprEmail1) {
        this.tsxAnsprEmail1 = tsxAnsprEmail1;
    }

    public String getTsxAnspr2() {
        return tsxAnspr2;
    }

    public void setTsxAnspr2(String tsxAnspr2) {
        this.tsxAnspr2 = tsxAnspr2;
    }

    public String getTsxAnsprTel2() {
        return tsxAnsprTel2;
    }

    public void setTsxAnsprTel2(String tsxAnsprTel2) {
        this.tsxAnsprTel2 = tsxAnsprTel2;
    }

    public String getTsxAnsprFax2() {
        return tsxAnsprFax2;
    }

    public void setTsxAnsprFax2(String tsxAnsprFax2) {
        this.tsxAnsprFax2 = tsxAnsprFax2;
    }

    public String getTsxAnsprEmail2() {
        return tsxAnsprEmail2;
    }

    public void setTsxAnsprEmail2(String tsxAnsprEmail2) {
        this.tsxAnsprEmail2 = tsxAnsprEmail2;
    }

    public String getTsxAnspr3() {
        return tsxAnspr3;
    }

    public void setTsxAnspr3(String tsxAnspr3) {
        this.tsxAnspr3 = tsxAnspr3;
    }

    public String getTsxAnsprTel3() {
        return tsxAnsprTel3;
    }

    public void setTsxAnsprTel3(String tsxAnsprTel3) {
        this.tsxAnsprTel3 = tsxAnsprTel3;
    }

    public String getTsxAnsprFax3() {
        return tsxAnsprFax3;
    }

    public void setTsxAnsprFax3(String tsxAnsprFax3) {
        this.tsxAnsprFax3 = tsxAnsprFax3;
    }

    public String getTsxAnsprEmail3() {
        return tsxAnsprEmail3;
    }

    public void setTsxAnsprEmail3(String tsxAnsprEmail3) {
        this.tsxAnsprEmail3 = tsxAnsprEmail3;
    }

    public String getTsxText1() {
        return tsxText1;
    }

    public void setTsxText1(String tsxText1) {
        this.tsxText1 = tsxText1;
    }

    public String getTsxText2() {
        return tsxText2;
    }

    public void setTsxText2(String tsxText2) {
        this.tsxText2 = tsxText2;
    }

    public String getTsxText3() {
        return tsxText3;
    }

    public void setTsxText3(String tsxText3) {
        this.tsxText3 = tsxText3;
    }

    public String getTsxText4() {
        return tsxText4;
    }

    public void setTsxText4(String tsxText4) {
        this.tsxText4 = tsxText4;
    }

    public Date getTsxTimestamp() {
        return tsxTimestamp;
    }

    public void setTsxTimestamp(Date tsxTimestamp) {
        this.tsxTimestamp = tsxTimestamp;
    }

    public String getTsxUserName() {
        return tsxUserName;
    }

    public void setTsxUserName(String tsxUserName) {
        this.tsxUserName = tsxUserName;
    }

    public Date getTsxUpdateTimestamp() {
        return tsxUpdateTimestamp;
    }

    public void setTsxUpdateTimestamp(Date tsxUpdateTimestamp) {
        this.tsxUpdateTimestamp = tsxUpdateTimestamp;
    }

    public String getTsxUpdUsername() {
        return tsxUpdUsername;
    }

    public void setTsxUpdUsername(String tsxUpdUsername) {
        this.tsxUpdUsername = tsxUpdUsername;
    }
}
