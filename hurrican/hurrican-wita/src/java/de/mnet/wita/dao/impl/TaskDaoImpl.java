/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2011 13:19:43
 */
package de.mnet.wita.dao.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Iterables;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.mnet.wita.dao.TaskDao;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.AbgebendeLeitungenVorgang;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.UserTask;
import de.mnet.wita.model.Vorgang;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * <b>ACHTUNG:</b> werden UserTask oder CBVorgang bzw. deren Ableitungen geladen, muss nachträglich noch der AKUser in
 * die geladenen Entities "gemerged" werden. Siehe dazu die mergeUser... Methoden.
 */
public class TaskDaoImpl extends Hibernate4DAOImpl implements TaskDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    protected AKUserService userService;

    @Override
    public List<TamVorgang> findOpenTamTasks() {
        String hql = findTamTasks(false);
        Date now = new Date();
        @SuppressWarnings("unchecked")
        List<TamVorgang> result = find(hql, UserTask.UserTaskStatus.OFFEN, now, now);
        return mergeUser(result);
    }

    @Override
    public List<TamVorgang> findOpenTamTasksWithWiedervorlage() {
        String hql = findTamTasks(true);
        Date now = new Date();
        Date dateTimeNow = new Date();
        @SuppressWarnings("unchecked")
        List<TamVorgang> result = find(hql, UserTask.UserTaskStatus.OFFEN, now, now, dateTimeNow);
        return mergeUser(result);
    }

    private String findTamTasks(boolean checkWiedervorlageDate) {
        // @formatter:off
        String hql = "select new de.mnet.wita.model.TamVorgang(cbv, max(tam), count(tam), nl.name, ad.auftragNoOrig, cb.lbz, cbv.tamUserTask.tv60Sent, cbv.tamUserTask.mahnTam)" +
                " from WitaCBVorgang as cbv, " +
                " de.mnet.wita.message.meldung.TerminAnforderungsMeldung as tam, " +
                " AuftragTechnik as at, " +
                " AuftragDaten as ad, " +
                " Niederlassung as nl, " +
                " Carrierbestellung as cb " +
                " where " +
                " cbv.auftragId = at.auftragId " +
                " and cbv.auftragId = ad.auftragId " +
                " and cb.id = cbv.cbId " +
                " and at.niederlassungId = nl.id " +
                " and tam.externeAuftragsnummer = cbv.carrierRefNr " +
                " and cbv.tamUserTask is not null and cbv.tamUserTask.status = ?" +
                " and at.gueltigBis > ? " +
                " and ad.gueltigBis > ? ";
                if (checkWiedervorlageDate) {
                    hql = hql + " and (cbv.tamUserTask.wiedervorlageAm <= ? or cbv.tamUserTask.wiedervorlageAm is null) ";
                }
                hql = (hql + " group by cbv, nl.name, ad.auftragNoOrig, cb.lbz, cbv.tamUserTask.tv60Sent, cbv.tamUserTask.mahnTam" +
                             " order by max(tam.versandZeitstempel)");
        // @formatter:on
        return hql;
    }

    @Override
    public List<AbgebendeLeitungenVorgang> findOpenAbgebendeLeitungenTasksWithWiedervorlage() {
        // @formatter:off
        String hql = "select new de.mnet.wita.model.AbgebendeLeitungenVorgang(task)" +
                    " from AbgebendeLeitungenUserTask as task " +
                    " where task.status = ?" +
                    " and (task.wiedervorlageAm <= ? or task.wiedervorlageAm is null)";
        // @formatter:on

        Date dateTimeNow = new Date();
        @SuppressWarnings("unchecked")
        List<AbgebendeLeitungenVorgang> result = find(hql, UserTask.UserTaskStatus.OFFEN, dateTimeNow);

        return mergeUser4Vorgang(result);
    }

    @Override
    public List<AbgebendeLeitungenUserTask> findAbgebendeLeitungenUserTask(String externeAuftragsnummer) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AbgebendeLeitungenUserTask.class);
        criteria.add(Restrictions.eq(AbgebendeLeitungenUserTask.EXTERNE_AUFTRAGSNUMMER_FIELD, externeAuftragsnummer));
        List<AbgebendeLeitungenUserTask> result = criteria.list();
        return mergeUser4UserTask(result);
    }

    @Override
    public AkmPvUserTask findAkmPvUserTask(String externeAuftragsnummer) {
        return findAkmPvUserTask("externeAuftragsnummer", externeAuftragsnummer);
    }

    @Override
    public AkmPvUserTask findAkmPvUserTaskByContractId(String vertragsnummer) {
        return findAkmPvUserTask("vertragsNummer", vertragsnummer);
    }

    private AkmPvUserTask findAkmPvUserTask(String propertyName, String propertyValue) {
        StringBuilder hql = new StringBuilder();
        hql.append("select ut from ");
        hql.append(AkmPvUserTask.class.getName()).append(" ut");
        hql.append(" where ut.").append(propertyName).append("=?");

        @SuppressWarnings("unchecked")
        List<AkmPvUserTask> queryResult = find(hql.toString(), propertyValue);
        return Iterables.getOnlyElement(mergeUser4UserTask(queryResult), null);
    }

    @Override
    public KueDtUserTask findKueDtTask(String externeAuftragsnummer) {
        StringBuilder hql = new StringBuilder();
        hql.append("select ut from ");
        hql.append(KueDtUserTask.class.getName()).append(" ut");
        hql.append(" where ut.externeAuftragsnummer=? order by ut.id desc");

        @SuppressWarnings("unchecked")
        List<KueDtUserTask> queryResult = find(hql.toString(), externeAuftragsnummer);
        return Iterables.getOnlyElement(mergeUser4UserTask(queryResult), null);
    }

    @Override
    public AkmPvUserTask findOpenAkmPvUserTaskByVertragNr(String vertragsNr) {
        StringBuilder hql = new StringBuilder();
        hql.append("select ut from ");
        hql.append(AkmPvUserTask.class.getName()).append(" ut");
        hql.append(" where ut.vertragsNummer=? and ut.status=?");

        @SuppressWarnings("unchecked")
        List<AkmPvUserTask> queryResult = find(hql.toString(), vertragsNr,
                UserTask.UserTaskStatus.OFFEN);
        return Iterables.getOnlyElement(mergeUser4UserTask(queryResult));
    }

    private List<TamVorgang> mergeUser(List<TamVorgang> tamVorgangs) {
        for (TamVorgang tv : tamVorgangs) {
            WitaCBVorgang cb = tv.getCbVorgang();
            if (cb.getUserId() != null) {
                AKUser user = findUserById(cb.getUserId());
                cb.setBearbeiter(user);
                tv.setBearbeiter(user); // kopierte property in TamVorgang
            }
            if ((cb.getTamUserTask() != null) && (cb.getTamUserTask().getUserId() != null)) {
                AKUser user = findUserById(cb.getTamUserTask().getUserId());
                cb.getTamUserTask().setBearbeiter(user);
            }
        }
        return tamVorgangs;
    }

    private <T extends Vorgang> List<T> mergeUser4Vorgang(List<T> tList) {
        for (T t : tList) {
            if (t.getUserTask().getUserId() != null) {
                AKUser user = findUserById(t.getUserTask().getUserId());
                t.getUserTask().setBearbeiter(user);
            }
        }
        return tList;
    }

    private <T extends UserTask> List<T> mergeUser4UserTask(List<T> tList) {
        for (T t : tList) {
            if (t.getUserId() != null) {
                AKUser user = findUserById(t.getUserId());
                t.setBearbeiter(user);
            }
        }
        return tList;
    }

    private AKUser findUserById(Long id) {
        try {
            return userService.findById(id);
        }
        catch (AKAuthenticationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
