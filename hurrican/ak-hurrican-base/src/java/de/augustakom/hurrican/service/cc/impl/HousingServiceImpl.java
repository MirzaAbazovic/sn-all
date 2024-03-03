/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:32:42
 */

package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AuftragHousingDAO;
import de.augustakom.hurrican.dao.cc.AuftragHousingKeyDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.model.cc.housing.HousingFloor;
import de.augustakom.hurrican.model.cc.housing.HousingParcel;
import de.augustakom.hurrican.model.cc.housing.HousingRoom;
import de.augustakom.hurrican.model.cc.housing.Transponder;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;
import de.augustakom.hurrican.model.cc.view.CCAuftragHousingView;
import de.augustakom.hurrican.model.shared.view.AuftragHousingQuery;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HousingService;

@CcTxRequired
public class HousingServiceImpl extends DefaultCCService implements HousingService {

    private static final Logger LOGGER = Logger.getLogger(HousingServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.dao.cc.AuftragHousingDAO")
    private AuftragHousingDAO auftragHousingDAO;

    @Resource(name = "de.augustakom.hurrican.dao.cc.AuftragHousingKeyDAO")
    private AuftragHousingKeyDAO auftragHousingKeyDAO;

    @Override
    public void saveAuftragHousing(AuftragHousing toSave) throws StoreException {
        saveHistoricized(toSave, auftragHousingDAO);
    }

    @Override
    public AuftragHousing findAuftragHousing(Long auftragId) throws FindException {
        try {
            return auftragHousingDAO.findAuftragHousing(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HousingBuilding> findHousingBuildings() throws FindException {
        try {
            return auftragHousingDAO.findAll(HousingBuilding.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HousingBuilding findHousingBuilding4Auftrag(Long auftragId) throws FindException {
        if (auftragId == null) { return null; }
        try {
            return auftragHousingDAO.findHousingBuilding4Auftrag(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AuftragHousingKeyView> findHousingKeys(Long auftragId) throws FindException {
        if (auftragId == null) { return null; }
        try {
            return auftragHousingKeyDAO.findAuftragHousingKeys(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragHousingKey findHousingKey(Long id) throws FindException {
        if (id == null) { return null; }
        try {
            return auftragHousingKeyDAO.findById(id, AuftragHousingKey.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HousingFloor findHousingFloorById(Long floorId) throws FindException {
        if (floorId == null) { return null; }
        try {
            return auftragHousingDAO.findById(floorId, HousingFloor.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HousingRoom findHousingRoomById(Long roomId) throws FindException {
        if (roomId == null) { return null; }
        try {
            return auftragHousingDAO.findById(roomId, HousingRoom.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public HousingParcel findHousingParcelById(Long parcelId) throws FindException {
        if (parcelId == null) { return null; }
        try {
            return auftragHousingDAO.findById(parcelId, HousingParcel.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<CCAuftragHousingView> findHousingsByQuery(AuftragHousingQuery housingQuery) throws FindException {
        if ((housingQuery == null) || housingQuery.isEmpty()) {
            throw new FindException(
                    FindException.EMPTY_FIND_PARAMETER);
        }

        boolean searchWithWildCards = (WildcardTools.containsWildcard(housingQuery.getFirstName()) || WildcardTools
                .containsWildcard(housingQuery.getLastName())) ? true : false;

        Transponder transponderExample = new Transponder();
        transponderExample.setTransponderId(housingQuery.getTransponderNr());
        transponderExample.setCustomerFirstName(WildcardTools.replaceWildcards(housingQuery.getFirstName()));
        transponderExample.setCustomerLastName(WildcardTools.replaceWildcards(housingQuery.getLastName()));
        List<Transponder> transponderList = auftragHousingKeyDAO.queryByExample(transponderExample, Transponder.class,
                searchWithWildCards);

        List<CCAuftragHousingView> retVal = new ArrayList<CCAuftragHousingView>();
        if (CollectionTools.isNotEmpty(transponderList)) {
            for (Transponder transponder : transponderList) {
                List<AuftragHousingKey> housingKeys = null;
                if (transponder.getTransponderGroup() != null) {
                    TransponderGroup group = auftragHousingKeyDAO.findById(transponder.getTransponderGroup().getId(),
                            TransponderGroup.class);
                    housingKeys = auftragHousingKeyDAO.findByProperty(AuftragHousingKey.class,
                            AuftragHousingKey.TRANSPONDER_GROUP, group);
                }
                else {
                    housingKeys = auftragHousingKeyDAO.findByProperty(AuftragHousingKey.class,
                            AuftragHousingKey.TRANSPONDER, transponder);
                }

                if (CollectionTools.isNotEmpty(housingKeys)) {
                    Set<Long> processedHousingKeys = new HashSet<Long>();
                    for (AuftragHousingKey key : housingKeys) {
                        if (!processedHousingKeys.contains(key.getId())) { // jeden HousingKey nur einmal!!!
                            processedHousingKeys.add(key.getId());

                            Auftrag auftrag = auftragHousingKeyDAO.findById(key.getAuftragId(), Auftrag.class);
                            retVal.add(CCAuftragHousingView.createAuftragHousingView(transponder, key, auftrag));
                        }
                    }
                }
            }
        }

        return retVal;
    }

    @Override
    public void saveAuftragHousingKey(AuftragHousingKey toSave) throws StoreException {
        saveHistoricized(toSave, auftragHousingKeyDAO);
    }

    @Override
    public void deleteAuftragHousingKey(AuftragHousingKey toDelete) throws DeleteException {
        this.auftragHousingKeyDAO.deleteById(toDelete.getId());
    }

    @Override
    public List<TransponderGroup> findTransponderGroups(Long kundeNo) throws FindException {
        if (kundeNo == null) { return new ArrayList<TransponderGroup>(); }
        try {
            return auftragHousingKeyDAO.findByProperty(TransponderGroup.class, TransponderGroup.KUNDE_NO, kundeNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveTransponderGroup(TransponderGroup toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            auftragHousingKeyDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteTransponderGroup(TransponderGroup toDelete) throws DeleteException {
        if ((toDelete == null) || (toDelete.getId() == null)) { return; }
        try {
            List<AuftragHousingKey> result = auftragHousingKeyDAO.findByProperty(AuftragHousingKey.class,
                    AuftragHousingKey.TRANSPONDER_GROUP, toDelete);
            if (CollectionTools.isNotEmpty(result)) {
                throw new DeleteException(
                        "Transponder-Gruppe kann nicht gelöscht werden, da sie noch min. von einem Auftrag referenziert wird!");
            }

            auftragHousingKeyDAO.deleteTransponderGroup(toDelete.getId());
        }
        catch (DeleteException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

}
