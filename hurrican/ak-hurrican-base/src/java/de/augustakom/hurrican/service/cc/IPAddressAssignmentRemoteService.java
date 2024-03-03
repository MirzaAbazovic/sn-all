/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2011 12:16:22
 */
package de.augustakom.hurrican.service.cc;

import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.monline.ipProviderService.ipp.AssignIPV4Response;
import de.mnet.monline.ipProviderService.ipp.AssignIPV6Response;
import de.mnet.monline.ipProviderService.ipp.ProductGroup;
import de.mnet.monline.ipProviderService.ipp.Purpose;
import de.mnet.monline.ipProviderService.ipp.Site;

/**
 * Schnittstelle zu Monline, um Bloecke von IP-Addressen Auftraegen zuzuweisen.
 *
 *
 * @since Release 10
 */
public interface IPAddressAssignmentRemoteService extends ICCService {

    /**
     * TODO
     *
     * @param productGroupEnum
     * @param vpnId
     * @param purposeEnum
     * @param netMaskSize
     * @param siteEnum
     * @param userW
     * @param auftragId
     * @return
     * @throws StoreException
     */
    public AssignIPV4Response assignIPv4(ProductGroup.Enum productGroupEnum, Long vpnId, Purpose.Enum purposeEnum,
            Integer netMaskSize, Site.Enum siteEnum, String userW, Long auftragId) throws StoreException;

    /**
     * TODO
     *
     * @param productGroupEnum
     * @param vpnId
     * @param netMaskSize
     * @param siteEnum
     * @param userW
     * @param auftragId
     * @return
     * @throws StoreException
     */
    public AssignIPV6Response assignIPv6(ProductGroup.Enum productGroupEnum, Long vpnId, Integer netMaskSize,
            Site.Enum siteEnum, String userW, Long auftragId) throws StoreException;


    /**
     * gibt eine IP-V4-Adresse in monline frei
     *
     * @param netAddress
     * @param netId
     * @param netmaskSize
     * @throws StoreException
     */
    public void releaseIPv4(String netAddress, Long netId, int netmaskSize) throws StoreException;

    /**
     * gibt eine IP-V6-Adresse in monline frei
     *
     * @param netAddress
     * @param netId
     * @param netmaskSize
     * @throws StoreException
     */
    public void releaseIPv6(String netAddress, Long netId, int netmaskSize) throws StoreException;
} // end


