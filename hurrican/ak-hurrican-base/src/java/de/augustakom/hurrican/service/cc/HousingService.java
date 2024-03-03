/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.02.2010 14:30:25
 */

package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.model.cc.housing.HousingFloor;
import de.augustakom.hurrican.model.cc.housing.HousingParcel;
import de.augustakom.hurrican.model.cc.housing.HousingRoom;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;
import de.augustakom.hurrican.model.cc.view.CCAuftragHousingView;
import de.augustakom.hurrican.model.shared.view.AuftragHousingQuery;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer die Bearbeitung von Housing-Daten.
 */
public interface HousingService extends ICCService {

    /**
     * Speichert das angegebene AuftragHousing-Objekt.
     *
     * @param toSave
     * @throws StoreException
     */
    void saveAuftragHousing(AuftragHousing toSave) throws StoreException;

    /**
     * Ermittelt ein AuftragHousing-Objekt zu dem angegebenen Auftrag.
     *
     * @param auftragId
     * @return
     * @throws FindException
     */
    public AuftragHousing findAuftragHousing(Long auftragId) throws FindException;

    /**
     * Ermittelt alle Housing-Gebaeude.
     *
     * @return Liste mit Objekten des Typs {@link HousingBuilding}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    public List<HousingBuilding> findHousingBuildings() throws FindException;

    /**
     * Ermittelt das Housing-Gebaeude des Auftrags.
     *
     * @return Objekt des Typs {@link HousingBuilding}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    public HousingBuilding findHousingBuilding4Auftrag(Long auftragId) throws FindException;

    /**
     * Ermittelt Housing-Floor Objekte.
     *
     * @return Objekt des Typs {@link HousingFloor}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    public HousingFloor findHousingFloorById(Long floorId) throws FindException;

    /**
     * Ermittelt Housing-Room Objekte.
     *
     * @return Objekt des Typs {@link HousingRoom}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    public HousingRoom findHousingRoomById(Long roomId) throws FindException;

    /**
     * Ermittelt Housing-Parcel Objekte.
     *
     * @return Objekt des Typs {@link HousingParcel}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    public HousingParcel findHousingParcelById(Long parcelId) throws FindException;

    /**
     * Ermittelt alle Housing-Transponder.
     *
     * @return Liste mit Objekten des Typs {@link AuftragHousingKey}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    public List<AuftragHousingKeyView> findHousingKeys(Long auftragId) throws FindException;

    /**
     * Ermittelt einen {@link AuftragHousingKey} ueber dessen ID.
     *
     * @param id
     * @return
     * @throws FindException
     */
    public AuftragHousingKey findHousingKey(Long id) throws FindException;

    /**
     * Ermittelt alle Housingaufträge mithilfe eines Query-Objekts.
     *
     * @return Liste mit Objekten des Typs {@link CCAuftragHousingView}
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    public List<CCAuftragHousingView> findHousingsByQuery(AuftragHousingQuery housingQuery) throws FindException;

    /**
     * Speichert das angegebene AuftragHousingKey-Objekt.
     *
     * @param toSave
     * @throws StoreException
     */
    void saveAuftragHousingKey(AuftragHousingKey toSave) throws StoreException;

    /**
     * Löscht das angegebene AuftragHousingKey-Objekt.
     *
     * @param toDelete
     * @throws DeleteException
     */
    void deleteAuftragHousingKey(AuftragHousingKey toDelete) throws DeleteException;

    /**
     * Ermittelt alle Transponder-Gruppen, die dem Kunden mit der angegebenen {@code kundeNo} zugeordnet sind.
     *
     * @param kundeNo Kundennummer
     * @return Liste der zugeordneten Transponder-Gruppen
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<TransponderGroup> findTransponderGroups(Long kundeNo) throws FindException;

    /**
     * Speichert die angegebene Transponder-Gruppe.
     *
     * @param toSave zu speicherndes Objekt.
     * @throws StoreException
     */
    void saveTransponderGroup(TransponderGroup toSave) throws StoreException;

    /**
     * Entfernt die angegebene {@link TransponderGroup}
     *
     * @param toDelete
     * @throws DeleteException
     */
    void deleteTransponderGroup(TransponderGroup toDelete) throws DeleteException;

}
