/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 16:00:20
 */
package de.augustakom.hurrican.service.cc.fttx.impl;

import java.time.*;
import java.util.*;
import java.util.Map.*;
import javax.annotation.*;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.fttx.A10NspDao;
import de.augustakom.hurrican.dao.cc.fttx.A10NspPortDao;
import de.augustakom.hurrican.dao.cc.fttx.EkpFrameContractDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;

/**
 * Service-Implementierung von {@link EkpFrameContractService}
 */
@CcTxRequired
public class EkpFrameContractServiceImpl implements EkpFrameContractService {

    @Resource(name = "de.augustakom.hurrican.dao.cc.fttx.EkpFrameContractDAO")
    private EkpFrameContractDAO ekpFrameContractDao;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Autowired
    private A10NspDao a10NspDao;
    @Autowired
    private A10NspPortDao a10NspPortDao;

    @Override
    public EkpFrameContract saveEkpFrameContract(EkpFrameContract ekpFrameContract) {
        return ekpFrameContractDao.store(ekpFrameContract);
    }

    @Override
    public EkpFrameContract findEkpFrameContract(String ekpId, String contractId) {
        return ekpFrameContractDao.findEkpFrameContract(ekpId, contractId);
    }

    @Override
    @Nonnull
    public Auftrag2EkpFrameContract assignEkpFrameContract2Auftrag(EkpFrameContract toAssign, Auftrag auftrag,
            LocalDate validAt, @Nullable AuftragAktion auftragAktion) {
        Auftrag2EkpFrameContract existing =
                ekpFrameContractDao.findAuftrag2EkpFrameContract(auftrag.getAuftragId(), validAt);

        Long auftragAktionsId = (auftragAktion != null) ? auftragAktion.getId() : null;
        if (existing != null) {
            if (!toAssign.getId().equals(existing.getEkpFrameContract().getId())) {
                existing.setAssignedTo(validAt);
                existing.setAuftragAktionsIdRemove(auftragAktionsId);
                ekpFrameContractDao.store(existing);
            }
            else {
                return existing;
            }
        }

        Auftrag2EkpFrameContract assignedContract = new Auftrag2EkpFrameContract();
        assignedContract.setAuftragId(auftrag.getAuftragId());
        assignedContract.setEkpFrameContract(toAssign);
        assignedContract.setAssignedFrom(validAt);
        assignedContract.setAssignedTo(DateTools.getHurricanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        assignedContract.setAuftragAktionsIdAdd(auftragAktionsId);
        return ekpFrameContractDao.store(assignedContract);
    }

    @Override
    public Auftrag2EkpFrameContract findAuftrag2EkpFrameContract(Long auftragId, LocalDate validAt) {
        return ekpFrameContractDao.findAuftrag2EkpFrameContract(auftragId, validAt);
    }

    @Override
    public boolean hasAuftrag2EkpFrameContract(EkpFrameContract ekpFrameContract) {
        return ekpFrameContractDao.hasAuftrag2EkpFrameContract(Collections.singletonList(ekpFrameContract));
    }

    @Override
    public EkpFrameContract findEkp4AuftragOrDefaultMnet(final Long auftragId, final LocalDate when,
            boolean useDefaultEkp) throws FindException {
        Auftrag2EkpFrameContract auftrag2Ekp = findAuftrag2EkpFrameContract(auftragId, when);
        if (auftrag2Ekp != null) {
            return auftrag2Ekp.getEkpFrameContract();
        }
        if (useDefaultEkp) {
            return getDefaultEkpFrameContract();
        }
        throw new FindException(String.format("Kein EKP Frame Contract für Auftrag-Nr: %d vorhanden.", auftragId));
    }

    @Override
    public Auftrag2EkpFrameContract cancelEkpFrameContractAssignment(Long auftragId, AuftragAktion auftragAktion) {
        Auftrag2EkpFrameContract example = new Auftrag2EkpFrameContract();
        example.setAuftragId(auftragId);
        List<Auftrag2EkpFrameContract> frameContracts = ekpFrameContractDao.queryByExample(example,
                Auftrag2EkpFrameContract.class);

        Auftrag2EkpFrameContract reactivatedAuftrag2Ekp = null;
        for (Auftrag2EkpFrameContract auftrag2Ekp : frameContracts) {
            if (NumberTools.equal(auftragAktion.getId(), auftrag2Ekp.getAuftragAktionsIdAdd())) {
                ekpFrameContractDao.deleteAuftrag2EkpFrameContract(auftrag2Ekp.getId());
            }
            else if (NumberTools.equal(auftragAktion.getId(), auftrag2Ekp.getAuftragAktionsIdRemove())) {
                auftrag2Ekp.setAssignedTo(DateTools.getHurricanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                auftrag2Ekp.setAuftragAktionsIdRemove(null);
                ekpFrameContractDao.store(auftrag2Ekp);
                reactivatedAuftrag2Ekp = auftrag2Ekp;
            }
        }

        return reactivatedAuftrag2Ekp;
    }

    @Override
    public boolean hasAuftrag2EkpFrameContract(A10NspPort a10NspPort) {
        List<EkpFrameContract> allEkpFrameContract = findAllEkpFrameContract();
        nextEkp:
        for (Iterator<EkpFrameContract> ekpIter = allEkpFrameContract.iterator(); ekpIter.hasNext(); ) {
            EkpFrameContract ekp = ekpIter.next();
            for (Entry<A10NspPort, Boolean> entry : ekp.getA10NspPortsOfEkp().entrySet()) {
                if (entry.getKey().getId().equals(a10NspPort.getId())) {
                    continue nextEkp;
                }
            }
            ekpIter.remove();
        }
        return ekpFrameContractDao.hasAuftrag2EkpFrameContract(allEkpFrameContract);
    }

    @Override
    public boolean hasAuftrag2EkpFrameContract(A10Nsp a10Nsp) {
        List<EkpFrameContract> allEkpFrameContract = findAllEkpFrameContract();
        nextEkp:
        for (Iterator<EkpFrameContract> ekpIter = allEkpFrameContract.iterator(); ekpIter.hasNext(); ) {
            EkpFrameContract ekp = ekpIter.next();
            for (Entry<A10NspPort, Boolean> entry : ekp.getA10NspPortsOfEkp().entrySet()) {
                if (entry.getKey().getA10Nsp().getId().equals(a10Nsp.getId())) {
                    continue nextEkp;
                }
            }
            ekpIter.remove();
        }
        return ekpFrameContractDao.hasAuftrag2EkpFrameContract(allEkpFrameContract);
    }

    private
    @CheckForNull
    A10NspPort findDefaultA10NspPort(final EkpFrameContract contract) {
        Map<A10NspPort, Boolean> a10Map = contract.getA10NspPortsOfEkp();
        for (Entry<A10NspPort, Boolean> entry : a10Map.entrySet()) {
            if (BooleanTools.nullToFalse(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public A10NspPort findA10NspPort(final EkpFrameContract contract, final @Nullable Long oltId) {
        Map<A10NspPort, Boolean> a10Map = contract.getA10NspPortsOfEkp();
        Iterator<A10NspPort> iterator = a10Map.keySet().iterator();
        while (iterator.hasNext()) {
            A10NspPort a10NspPort = iterator.next();
            boolean oltMatches = Iterables.any(a10NspPort.getOlt(), new Predicate<HWOlt>() {
                @Override
                public boolean apply(HWOlt input) {
                    return (input.getId() != null) && input.getId().equals(oltId);
                }
            });

            if (oltMatches) {
                return a10NspPort;
            }
        }
        return findDefaultA10NspPort(contract);
    }

    @Override
    public A10NspPort createA10NspPort(A10Nsp a10Nsp) throws StoreException {
        try {
            VerbindungsBezeichnung vbz = physikService.createVerbindungsBezeichnung("F", "E");

            A10NspPort a10NspPort = new A10NspPort(a10Nsp, vbz);
            physikService.saveVerbindungsBezeichnung(vbz);
            return a10NspPortDao.store(a10NspPort);
        }
        catch (Exception e) {
            throw new StoreException("Kann neuen A10-NSP Port nicht anlegen", e);
        }
    }

    @Override
    public A10Nsp saveA10Nsp(A10Nsp a10Nsp) {
        return a10NspDao.store(a10Nsp);
    }

    @Override
    public Auftrag2EkpFrameContract saveAuftrag2EkpFrameContract(Auftrag2EkpFrameContract auftrag2EkpFrameContract) {
        return ekpFrameContractDao.store(auftrag2EkpFrameContract);
    }

    @Override
    public void deleteAuftrag2EkpFrameContract(Long id) {
        ekpFrameContractDao.deleteAuftrag2EkpFrameContract(id);
    }

    @Override
    @Nonnull
    public EkpFrameContract getDefaultEkpFrameContract() {
        EkpFrameContract contract = findEkpFrameContract(EkpFrameContract.EKP_ID_MNET,
                EkpFrameContract.FRAME_CONTRACT_ID_MNET);
        if (contract == null) {
            throw new IllegalStateException("Kein MNET EKP frame contract als Default-Contract für Retail gefunden.");
        }
        return contract;
    }

    @Override
    public List<A10Nsp> findAllA10Nsp() {
        return a10NspDao.queryByExample(new A10Nsp(), A10Nsp.class);
    }

    @Override
    public List<A10NspPort> findA10NspPorts(A10Nsp a10Nsp) throws FindException {
        return a10NspPortDao.findByProperty(A10NspPort.class, "a10Nsp", a10Nsp);
    }

    @Override
    public List<EkpFrameContract> findAllEkpFrameContract() {
        return ekpFrameContractDao.findAll();
    }

    @Override
    public void deleteEkpFrameContract(EkpFrameContract ekpFrameContract) {
        ekpFrameContractDao.delete(ekpFrameContract);
    }

    @Override
    public void deleteA10NspPort(A10NspPort selectedA10NspPort) {
        a10NspPortDao.delete(selectedA10NspPort);
        physikService.deleteVerbindungsBezeichnung(selectedA10NspPort.getVbz());
    }

    @Override
    public A10NspPort saveA10NspPort(A10NspPort a10nspPort) {
        return a10NspPortDao.store(a10nspPort);
    }

    @Override
    public A10NspPort findA10NspPortById(Long id) {
        return a10NspPortDao.findById(id, A10NspPort.class);
    }

    @Override
    public List<EkpFrameContract> findEkpFrameContractsByA10NspPort(A10NspPort a10NspPort) {
        List<EkpFrameContract> ekps = Lists.newLinkedList();
        for (EkpFrameContract ekp : findAllEkpFrameContract()) {
            if (ImmutableList.copyOf(ekp.getA10NspPortsOfEkp().keySet()).contains(a10NspPort)) {
                ekps.add(ekp);
            }
        }
        return ekps;
    }

    @Override
    public boolean isHwOltAssignedToEkpFrameContract(EkpFrameContract ekpFrameContract, HWOlt olt) {
        int cnt = 0;
        for (A10NspPort a10NspPort : ekpFrameContract.getA10NspPortsOfEkp().keySet()) {
            if (ImmutableList.copyOf(a10NspPort.getOlt()).contains(olt)) {
                cnt++;
            }
        }
        return cnt > 0;
    }

    @Override
    public void deleteA10Nsp(A10Nsp a10Nsp) throws FindException {
        List<A10NspPort> ports = findA10NspPorts(a10Nsp);
        for (A10NspPort port : ports) {
            deleteA10NspPort(port);
        }
        if (!ports.isEmpty()) {
            // Werden zum uebergebenen A10-NSP Ports gefunden, so verweisen diese Ports auf eine neue an die
            // Hibernate-Session attachte Instanz. Zum Loeschen muss dann die an die Session attachte Instanz verwendet
            // werden.
            a10NspPortDao.delete(ports.get(0).getA10Nsp());
        }
        else {
            a10NspPortDao.delete(a10Nsp);
        }
    }

    @Override
    public AKWarnings checkA10NspPortAssignableToEkp(EkpFrameContract ekpFrameContract, A10NspPort a10NspPort) {
        AKWarnings warnings = new AKWarnings();
        for (HWOlt olt : a10NspPort.getOlt()) {
            if (this.isHwOltAssignedToEkpFrameContract(ekpFrameContract, olt)) {
                warnings.addAKWarning(
                        this,
                        String.format(
                                "A10NSP-Port %s kann dem EKP %s nicht zugeordnet werden, da die OLT %s dem EKP bereits zugeordnet ist!%n",
                                a10NspPort.getId(),
                                ekpFrameContract.getFrameContractId(), olt.getGeraeteBez())
                );
            }
        }
        return warnings;
    }

    @Override
    public AKWarnings filterNotAssignableOlts(A10NspPort a10NspPort, List<HWOlt> oltsToFilter) {
        AKWarnings warnings = new AKWarnings();

        List<HWOlt> notAssignableOlts = Lists.newLinkedList();
        List<EkpFrameContract> ekps = this.findEkpFrameContractsByA10NspPort(a10NspPort);

        for (HWOlt olt : oltsToFilter) {
            for (EkpFrameContract ekp : ekps) {
                if (this.isHwOltAssignedToEkpFrameContract(ekp, olt)) {
                    warnings.addAKWarning(this, String.format(
                            "Dem Ekp %s ist die OLT %s bereits durch einen anderen A10Nsp-Port zugeordnet!%n",
                            ekp.getFrameContractId(), olt.getGeraeteBez()));
                    notAssignableOlts.add(olt);
                }
            }
        }
        oltsToFilter.removeAll(notAssignableOlts);
        return warnings;
    }

    @Override
    public List<Auftrag2EkpFrameContract> findAuftrag2EkpFrameContract(Long auftragId, LocalDate assignedFrom,
            LocalDate assignedTo) {
        Auftrag2EkpFrameContract example = new Auftrag2EkpFrameContract();
        example.setAuftragId(auftragId);
        example.setAssignedFrom(assignedFrom);
        example.setAssignedTo(assignedTo);
        return ekpFrameContractDao.queryByExample(example, Auftrag2EkpFrameContract.class);
    }

}
