package de.bitconex.adlatus.wholebuy.provision.adapter.geoaddress;

import de.bitconex.tmf.address.model.GeographicAddress;

public interface GeographicAddressClientService {

    GeographicAddress getAddress(String href);
}
