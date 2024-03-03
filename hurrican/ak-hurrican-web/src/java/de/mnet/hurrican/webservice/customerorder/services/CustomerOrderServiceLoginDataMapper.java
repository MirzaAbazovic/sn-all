package de.mnet.hurrican.webservice.customerorder.services;

import java.util.*;
import java.util.stream.*;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginData;
import de.augustakom.hurrican.service.cc.impl.logindata.model.internet.LoginDataInternet;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoipDn;
import de.mnet.esb.cdm.customer.customerorderservice.v1.CustomerOrderLogin;
import de.mnet.esb.cdm.customer.customerorderservice.v1.CustomerOrderLoginDetail;
import de.mnet.esb.cdm.customer.customerorderservice.v1.LoginDetailParameter;

/**
 *  Mapper for WSDL objects for {@link de.mnet.hurrican.webservice.customerorder.CustomerOrderServiceProvider#getCustomerLoginDetails(List)}
 */
@Service
public class CustomerOrderServiceLoginDataMapper {

    private static final String DATA_INTERNET_TYPE = "DataInternet";
    private static final String VOIP_TYPE = "VOIP";

    public CustomerOrderLogin mapCustomerOrderLogin(LoginData loginData, Long taifunOrderId) {
        final List<CustomerOrderLoginDetail> customerOrderLoginDetails = new ArrayList<>();
        if (loginData.getLoginDataInternet() != null) {
            customerOrderLoginDetails.add(mapInternetLoginData(loginData.getLoginDataInternet()));
        }
        if (loginData.getLoginDataVoip() != null && loginData.getLoginDataVoip().getVoipDnList() != null) {
            final List<CustomerOrderLoginDetail> voipLoginDetails = mapVoipLoginData(loginData.getLoginDataVoip().getVoipDnList());
            customerOrderLoginDetails.addAll(voipLoginDetails);
        }
        final CustomerOrderLogin login = new CustomerOrderLogin();
        login.setCustomerOrderId(taifunOrderId.toString());
        login.getLogin().addAll(customerOrderLoginDetails);
        return login;
    }

    private CustomerOrderLoginDetail mapInternetLoginData(LoginDataInternet loginDataInternet ) {
        final CustomerOrderLoginDetail loginDetail = new CustomerOrderLoginDetail();
        loginDetail.setTechnologyType(DATA_INTERNET_TYPE);
        for (Map.Entry<String, String> e : asMap(loginDataInternet).entrySet()) {
            loginDetail.getDetail().add(parameter(e.getKey(), e.getValue()));
        }
        return loginDetail;
    }

    private Map<String, String> asMap(LoginDataInternet loginDataInternet) {

        if (loginDataInternet == null) {
            return Collections.EMPTY_MAP;
        }

        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        if (StringUtils.isNotEmpty(loginDataInternet.getPppUser())) {
            builder.put("PppUser", loginDataInternet.getPppUserWithRealm());
        }
        if (StringUtils.isNotEmpty(loginDataInternet.getPppPassword())) {
            builder.put("PppPassword", loginDataInternet.getPppPassword());
        }
        if (loginDataInternet.getIpMode() != null) {
            builder.put("IpMode", loginDataInternet.getIpMode().humanReadable());
        }
        if (loginDataInternet.getPbitDaten() != null) {
            builder.put("PbitData", loginDataInternet.getPbitDaten().toString());
        }
        if (loginDataInternet.getPbitVoip() != null) {
            builder.put("PbitVoip", loginDataInternet.getPbitVoip().toString());
        }
        if (loginDataInternet.getVlanIdDaten() != null) {
            builder.put("VlanIdDaten", loginDataInternet.getVlanIdDaten().toString());
        }
        if (loginDataInternet.getVlanIdVoip() != null) {
            builder.put("VlanIdVoip", loginDataInternet.getVlanIdVoip().toString());
        }
        if (StringUtils.isNotEmpty(loginDataInternet.getAftrAddress())) {
            builder.put("AftrAddress", loginDataInternet.getAftrAddress());
        }
        if (loginDataInternet.getAtmParameterVPI() != null) {
            builder.put("AtmParameterVPI", loginDataInternet.getAtmParameterVPI().toString());
        }
        if (loginDataInternet.getAtmParameterVCI() != null) {
            builder.put("AtmParameterVCI", loginDataInternet.getAtmParameterVCI().toString());
        }

        return builder.build();
    }

    private CustomerOrderLoginDetail mapVoipLoginData(LoginDataVoipDn loginDataVoipDn ) {
        final CustomerOrderLoginDetail loginDetail = new CustomerOrderLoginDetail();
        loginDetail.setTechnologyType(VOIP_TYPE);
        if (StringUtils.isNotEmpty(loginDataVoipDn.getSipHauptrufnummer())) {
            loginDetail.getDetail().add(parameter("SipHauptrufnummer", loginDataVoipDn.getSipHauptrufnummer()));
        }
        if (StringUtils.isNotEmpty(loginDataVoipDn.getSipDomain())) {
            loginDetail.getDetail().add(parameter("SipDomain", loginDataVoipDn.getSipDomain()));
        }
        if (StringUtils.isNotEmpty(loginDataVoipDn.getSipPassword())) {
            loginDetail.getDetail().add(parameter("SipPassword", loginDataVoipDn.getSipPassword()));
        }
        return loginDetail;
    }

    private List<CustomerOrderLoginDetail> mapVoipLoginData(List<LoginDataVoipDn> loginDataVoipDns ) {
        return loginDataVoipDns.stream().map(dn -> mapVoipLoginData(dn)).collect(Collectors.toList());
    }

    private LoginDetailParameter parameter(String key, String value) {
        final LoginDetailParameter parameter = new LoginDetailParameter();
        parameter.setKey(key);
        parameter.setValue(value);
        return parameter;
    }
}
