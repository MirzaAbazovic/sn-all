<ns0:annehmenAuftragRequest xmlns:ns0="http://wholesale.telekom.de/oss/v${agreement.majorInterfaceVersion}/envelope">
    <control>
        <zeitstempel>${messageInfo.timestamp}</zeitstempel>
        <majorRelease>${agreement.majorInterfaceVersion}</majorRelease>
        <minorRelease>${agreement.minorInterfaceVersion}</minorRelease>
        <signaturId>
            <issuer>CN=wita.m-net.de, OU=IT, O=M-net Telekommunikations GmbH, L=Munich, ST=Bavaria, C=DE</issuer>
            <serial>1d2fc434</serial>
        </signaturId>
    </control>
    <auftrag>
        <externeAuftragsnummer>${order.id}</externeAuftragsnummer>
        <kunde>
            <kundennummer>${agreement.customerNumber}</kundennummer>
            <leistungsnummer>${agreement.agreementNumber}</leistungsnummer>
        </kunde>
        <geschaeftsfall xmlns:ns="http://wholesale.telekom.de/oss/v${agreement.majorInterfaceVersion}/order"
                        xmlns:pfx4="http://wholesale.telekom.de/oss/v${agreement.majorInterfaceVersion}/complex"
                        xmlns:ns15="http://wholesale.telekom.de/oss/v${agreement.majorInterfaceVersion}/tal"
                        xmlns:ns2="http://wholesale.telekom.de/oss/v${agreement.majorInterfaceVersion}/xdsl"
                        xmlns:ns3="http://wholesale.telekom.de/oss/v${agreement.majorInterfaceVersion}/ftth"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ns:${agreement.businessCase}">

            <BKTOFaktura>5883006816</BKTOFaktura>
            <ansprechpartner>
                <auftragsmanagement>
                    <anrede>${order.orderManagementContactPerson.salutation}</anrede>
                    <vorname>${order.orderManagementContactPerson.firstName}</vorname>
                    <nachname>${order.orderManagementContactPerson.lastName}</nachname>
                    <telefonnummer>${order.orderManagementContactPerson.phone}</telefonnummer>
                    <emailadresse>${order.orderManagementContactPerson.email}</emailadresse>
                </auftragsmanagement>
            </ansprechpartner>
            <termine>
                <kundenwunschtermin>
                    <datum>${order.requestedCompletionDate?string["yyyy-MM-dd"]}</datum>
                    <zeitfenster>${order.requestedCompletionTime}</zeitfenster>
                </kundenwunschtermin>
            </termine>
            <auftragsposition>
                <produkt xsi:type="ns2:${agreement.productType}">
                    <bezeichner>${agreement.productId}</bezeichner>
                </produkt>
                <geschaeftsfallProdukt xsi:type="ns2:${agreement.orderType}">
                    <standortA>
                        <#if order.endCustomer.organizationName??>
                            <firma>
                                <anrede>${order.endCustomer.salutation}</anrede>
                                <firmenname>${order.endCustomer.organizationName}</firmenname>
                            </firma>
                        <#else>
                            <person>
                                <anrede>${order.endCustomer.salutation}</anrede>
                                <vorname>${order.endCustomer.firstName}</vorname>
                                <nachname>${order.endCustomer.lastName}</nachname>
                            </person>
                        </#if>
                        <strasse>
                            <strassenname>${order.endCustomerLocation.street}</strassenname>
                            <hausnummer>${order.endCustomerLocation.houseNumber}</hausnummer>
                        </strasse>
                        <land>${order.endCustomerLocation.country}</land>
                        <postleitzahl>${order.endCustomerLocation.zipCode}</postleitzahl>
                        <ort>
                            <ortsname>${order.endCustomerLocation.city}</ortsname>
                        </ort>
                        <#-- TODO Do this need to be resolved? -->
                        <lageTAE_ONT>UG 04-1 EDV Raum APL</lageTAE_ONT>
                    </standortA>
                    <ansprechpartnerMontage>
                        <anrede>${order.technicianContact.salutation}</anrede>
                        <nachname>${order.technicianContact.lastName}</nachname>
                        <telefonnummer>${order.technicianContact.phone}</telefonnummer>
                        <emailadresse>${order.technicianContact.email}</emailadresse>
                    </ansprechpartnerMontage>
                </geschaeftsfallProdukt>
                <#list order.additionalServices as service>
                    <position>
                        <aktionscode>Z</aktionscode>
                        <produkt xsi:type="ns2:${agreement.productType}">
                            <bezeichner>${service}</bezeichner>
                        </produkt>
                    </position>
                </#list>
            </auftragsposition>
        </geschaeftsfall>
    </auftrag>
</ns0:annehmenAuftragRequest>