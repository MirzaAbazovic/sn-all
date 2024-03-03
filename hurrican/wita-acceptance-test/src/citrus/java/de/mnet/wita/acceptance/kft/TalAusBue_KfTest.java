/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2015
 */
package de.mnet.wita.acceptance.kft;

import static de.mnet.wita.WitaSimulatorTestUser.*;

import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.PropertyPlaceholderHelper;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;

/**
 * KFT fuer AUS-BUE
 * Der initiale Request erfolgt ueber ein XML-Template, da die AUS-BUE Funktion in Hurrican nicht eingebaut ist!
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class TalAusBue_KfTest extends AbstractWitaAcceptanceTest {

    @Value("${atlas.lineorderservice.createOrder:/LineOrderService/createOrder}")
    private String createOderSoapAction;


    /**
     * Abfrage eines Gesamtbestandes mit den Rueckmeldungen: QEB / 0000, ERGM / 0010, ENTM / 0010
     */
    @CitrusTest(name = "TalAusBue_01_KfTest")
    public void talAuskunftAusBue01() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_AUSKUNFT_BESTANDSUEBERSICHT_01, getWitaVersionForAcceptanceTest());

        String resource = String.format("de/mnet/wita/xml/cdm/v%s/talAuskunftAusBue01.xml",
                getWitaVersionForAcceptanceTest().getVersion()
        );
        doBestandsuebersicht(resource,
                CarrierKennung.DTAG_KUNDEN_NR_MNET,
                CarrierKennung.DTAG_LEISTUNGS_NR_MNET,
                "5883000320",
                null,
                null);
    }


    /**
     * Abfrage Gesamtbestand im Namen Dritter
     */
    @CitrusTest(name = "TalAusBue_01Akom_KfTest")
    public void talAuskunftAusBue01Akom() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_AUSKUNFT_BESTANDSUEBERSICHT_01, getWitaVersionForAcceptanceTest());

        String resource = String.format("de/mnet/wita/xml/cdm/v%s/talAuskunftAusBue01Akom.xml",
                getWitaVersionForAcceptanceTest().getVersion()
        );
        doBestandsuebersicht(resource,
                CarrierKennung.DTAG_KUNDEN_NR_AUGUSTAKOM,
                CarrierKennung.DTAG_LEISTUNGS_NR_AUGUSTAKOM,
                "5883000326",
                CarrierKennung.DTAG_KUNDEN_NR_MNET,
                CarrierKennung.DTAG_LEISTUNGS_NR_MNET);
    }


    private void doBestandsuebersicht(String file, String kundenNr, String kundenLeistungsNr, String bktoNr,
            String bestellerKundenNr, String bestellerKundenLeistungsNr) throws Exception {
        String xmlTemplateString = Resources.toString(Resources.getResource(file), Charset.defaultCharset());
        PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");
        Properties fileProperties = new Properties();
        fileProperties.put("zeit.stempel", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss.SSSZZ"))); // 2011-08-16T11:03:58.864+02:00

        final String externeAuftragsnummer = hurrican().getNextCarrierRefNr();
        fileProperties.put("externe.auftrags.nummer", externeAuftragsnummer);
        fileProperties.put("kunde.kunden.nummer", kundenNr);
        fileProperties.put("kunde.leistungs.nummer", kundenLeistungsNr);

        if ((bestellerKundenNr != null) && (bestellerKundenLeistungsNr != null)) {
            fileProperties.put("besteller.kunden.nummer", bestellerKundenNr);
            fileProperties.put("besteller.leistungs.nummer", bestellerKundenLeistungsNr);
        }

        fileProperties.put("ansprechpartner.nachname", TAL_AUSKUNFT_AUS_BUE_01.getName());

        fileProperties.put("bkto.bestand", bktoNr); // M-net

        LocalDate bestandsuebersichtStichtag = DateCalculationHelper.addWorkingDays(LocalDate.now(), 7);
        fileProperties.put("bestandsuebersicht.stichtag", bestandsuebersichtStichtag.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));

        String xmlString = propertyPlaceholderHelper.replacePlaceholders(xmlTemplateString, fileProperties);

        // Info fuer Umstellung auf wita-acc-test: Beispiel, siehe TalNeu_KfTest#talBereitNeu01a
        atlas().sendXmlToAtlas(xmlString, createOderSoapAction);

        ReceiveMessageActionDefinition action = atlas().receiveNotification("NEU", false)
                .extractFromPayload(WitaLineOrderXpathExpressions.HAS_THIRD_PARTY_SALESMAN.getXpath(), "salesman")
                .extractFromPayload(WitaLineOrderXpathExpressions.CUSTOMER_ID.getXpath(), VariableNames.CUSTOMER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.EXTERNAL_ORDER_ID.getXpath(), VariableNames.EXTERNAL_ORDER_ID);
        if (bestellerKundenNr != null) {
            action.extractFromPayload(WitaLineOrderXpathExpressions.THIRD_PARTY_SALESMAN_CUSTOMER_ID.getXpath(), VariableNames.THIRD_PARTY_SALESMAN_CUSTOMER_ID);
        }

        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                if (Boolean.valueOf(context.getVariable("salesman"))) {
                    context.setVariable("salesman", "1");
                }
                else {
                    context.setVariable("salesman", "0");
                }
            }
        });

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("QEB");
        atlas().receiveError("ERROR", AtlasEsbConstants.HUR_ERROR_CODE, "WITA_TECH_1000");

        conditional(
                atlas().sendNotification("ERGM")
        ).when("${salesman} = 0");

        conditional(
                atlas().sendNotification("ERGM-AKOM")
        ).when("${salesman} = 1");

        atlas().receiveError("ERROR_ERGM", AtlasEsbConstants.HUR_ERROR_CODE, "HUR_TECH_1000");

        conditional(
                atlas().sendNotification("ENTM")
        ).when("${salesman} = 0");

        conditional(
                atlas().sendNotification("ENTM-AKOM")
        ).when("${salesman} = 1");

        atlas().receiveError("ERROR", AtlasEsbConstants.HUR_ERROR_CODE, "WITA_TECH_1000");
    }


}
