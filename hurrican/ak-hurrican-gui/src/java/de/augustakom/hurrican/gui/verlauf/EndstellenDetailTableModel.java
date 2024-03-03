/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2005 12:14:55
 */
package de.augustakom.hurrican.gui.verlauf;

import java.util.*;

import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.service.base.iface.IHurricanService;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.augustakom.hurrican.service.cc.utils.ICCServiceFinder;


/**
 * TableModel fuer die Darstellung von Endstellen-Daten.
 *
 *
 */
public class EndstellenDetailTableModel extends AKTableModel<Endstelle> implements ICCServiceFinder {

    private static final int ROW_ANBINDUNG = 0;
    private static final int ROW_LEITUNG = 1;
    private static final int ROW_SCHNITTSTELLE = 2;
    private static final int ROW_ENDSTELLE = 3;
    private static final int ROW_NAME = 4;
    private static final int ROW_ANSP = 5;
    private static final int ROW_ORT = 6;
    private static final int ROW_PLZ = 7;
    private static final int ROW_CARRIER = 9;
    private static final int ROW_BESTELLT_AM = 10;
    private static final int ROW_BEREITSTELLUNG = 11;
    private static final long serialVersionUID = 1408433987097417014L;

    private Endstelle esA = null;
    private Endstelle esB = null;

    private Anschlussart anschlussartA = null;
    private Anschlussart anschlussartB = null;
    private Leitungsart leitungA = null;
    private Leitungsart leitungB = null;
    private Schnittstelle schnittstelleA = null;
    private Schnittstelle schnittstelleB = null;
    private EndstelleAnsprechpartner esAnspA = null;
    private EndstelleAnsprechpartner esAnspB = null;
    private Carrierbestellung carrierBestA = null;
    private Carrierbestellung carrierBestB = null;
    private Carrier carrierA = null;
    private Carrier carrierB = null;

    private List<Throwable> errors = null;

    /**
     * Uebergibt dem TableModel die anzuzeigenden Endstellen.
     *
     * @param esA Endstelle A
     * @param esB Endstelle B
     */
    protected void setEndstellen(Endstelle esA, Endstelle esB) {
        clear();

        this.esA = esA;
        this.esB = esB;
        loadDetails();
        fireTableDataChanged();
    }

    /* Laedt die Detail-Daten zu den Endstellen. */
    private void loadDetails() {
        anschlussartA = loadAnschlussart(esA);
        anschlussartB = loadAnschlussart(esB);
        leitungA = loadLeitungsart(esA);
        leitungB = loadLeitungsart(esB);
        schnittstelleA = loadSchnittstelle(esA);
        schnittstelleB = loadSchnittstelle(esB);
        esAnspA = loadEsAnsp(esA);
        esAnspB = loadEsAnsp(esB);
        carrierBestA = loadCarrierbestellung(esA);
        carrierBestB = loadCarrierbestellung(esB);
        carrierA = (carrierBestA != null) ? loadCarrierById(carrierBestA.getCarrier()) : null;
        carrierB = (carrierBestB != null) ? loadCarrierById(carrierBestB.getCarrier()) : null;
    }

    /* Laedt die Anschlussart fuer eine Endstelle. */
    private Anschlussart loadAnschlussart(Endstelle es) {
        if (es != null) {
            try {
                PhysikService ps = getCCService(PhysikService.class);
                return ps.findAnschlussart(es.getAnschlussart());
            }
            catch (Exception e) {
                errors.add(e);
            }
        }
        return null;
    }

    /* Laedt die Leitungsart einer best. Endstelle. */
    private Leitungsart loadLeitungsart(Endstelle es) {
        if (es != null) {
            try {
                PhysikService ps = getCCService(PhysikService.class);
                return ps.findLeitungsart4ES(es.getId());
            }
            catch (Exception e) {
                errors.add(e);
            }
        }
        return null;
    }

    /* Laedt die Schnittstelle einer best. Endstelle. */
    private Schnittstelle loadSchnittstelle(Endstelle es) {
        if (es != null) {
            try {
                PhysikService ps = getCCService(PhysikService.class);
                return ps.findSchnittstelle4ES(es.getId());
            }
            catch (Exception e) {
                errors.add(e);
            }
        }
        return null;
    }

    /* Laedt den Ansprechpartner zu einer best. Endstelle. */
    private EndstelleAnsprechpartner loadEsAnsp(Endstelle es) {
        if (es != null) {
            try {
                EndstellenService esSrv = getCCService(EndstellenService.class);
                return esSrv.findESAnsp4ES(es.getId());
            }
            catch (Exception e) {
                errors.add(e);
            }
        }
        return null;
    }

    /* Laedt die Carrierbestellung zu einer best. Endstelle. */
    private Carrierbestellung loadCarrierbestellung(Endstelle es) {
        if (es != null) {
            try {
                CarrierService cs = getCCService(CarrierService.class);
                List<Carrierbestellung> cbs = cs.findCBs4Endstelle(es.getId());
                return ((cbs != null) && (!cbs.isEmpty())) ? cbs.get(cbs.size() - 1) : null;
            }
            catch (Exception e) {
                errors.add(e);
            }
        }
        return null;
    }

    /* Laedt den Carrier zu einer best. ID. */
    private Carrier loadCarrierById(Long carrierId) {
        if (carrierId != null) {
            try {
                CarrierService cs = getCCService(CarrierService.class);
                return cs.findCarrier(carrierId);
            }
            catch (Exception e) {
                errors.add(e);
            }
        }
        return null;
    }

    /**
     * Sollte vom 'Client' abgefragt werden, nachdem die Endstellen uebergeben wurden. <br> Ueber die Methode koennen
     * alle Fehler abgefragt werden, die beim Laden der zusaetzlich benoetigten Daten zu einer Endstelle auftreten.
     *
     * @return
     */
    protected List<Throwable> getErrors() {
        return errors;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return 3;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return 12;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 1:
                return "Endstelle A";
            case 2:
                return "Endstelle B";
            default:
                return " ";
        }
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            switch (row) {
                case ROW_ANBINDUNG:
                    return "Anschlussart:";
                case ROW_LEITUNG:
                    return "Leitungsart:";
                case ROW_SCHNITTSTELLE:
                    return "Schnittstelle:";
                case ROW_ENDSTELLE:
                    return "Endstelle:";
                case ROW_NAME:
                    return "Name:";
                case ROW_ANSP:
                    return "Ansprechpartner:";
                case ROW_ORT:
                    return "Ort:";
                case ROW_PLZ:
                    return "PLZ:";
                case ROW_CARRIER:
                    return "Carrier:";
                case ROW_BESTELLT_AM:
                    return "Bestellt am:";
                case ROW_BEREITSTELLUNG:
                    return "Bereitst. am:";
                default:
                    break;
            }
        }
        else {
            Endstelle es = (column == 1) ? esA : esB;
            Anschlussart ansArt = (column == 1) ? anschlussartA : anschlussartB;
            Leitungsart leitung = (column == 1) ? leitungA : leitungB;
            Schnittstelle schnittstelle = (column == 1) ? schnittstelleA : schnittstelleB;
            EndstelleAnsprechpartner esAnsp = (column == 1) ? esAnspA : esAnspB;
            Carrier carrier = (column == 1) ? carrierA : carrierB;
            Carrierbestellung carrierBest = (column == 1) ? carrierBestA : carrierBestB;
            if (es != null) {
                switch (row) {
                    case ROW_ANBINDUNG:
                        return (ansArt != null) ? ansArt.getAnschlussart() : null;
                    case ROW_LEITUNG:
                        return (leitung != null) ? leitung.getName() : null;
                    case ROW_SCHNITTSTELLE:
                        return (schnittstelle != null) ? schnittstelle.getSchnittstelle() : null;
                    case ROW_ENDSTELLE:
                        return es.getEndstelle();
                    case ROW_NAME:
                        return es.getName();
                    case ROW_ANSP:
                        return (esAnsp != null) ? esAnsp.getAnsprechpartner() : null;
                    case ROW_ORT:
                        return es.getOrt();
                    case ROW_PLZ:
                        return es.getPlz();
                    case ROW_CARRIER:
                        return (carrier != null) ? carrier.getName() : null;
                    case ROW_BESTELLT_AM:
                        return (carrierBest != null) ? carrierBest.getBestelltAm() : null;
                    case ROW_BEREITSTELLUNG:
                        return (carrierBest != null) ? carrierBest.getBereitstellungAm() : null;
                    default:
                        break;
                }
            }
        }
        return null;
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /* 'Loescht' alle Modelle. */
    private void clear() {
        if (errors != null) {
            errors.clear();
        }
        else {
            errors = new ArrayList<>();
        }

        anschlussartA = null;
        anschlussartB = null;
        leitungA = null;
        leitungB = null;
        schnittstelleA = null;
        schnittstelleB = null;
        esAnspA = null;
        esAnspB = null;
        carrierBestA = null;
        carrierBestB = null;
        carrierA = null;
        carrierB = null;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends ICCService> T getCCService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(serviceName, serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.Class)
     */
    @Override
    public <T extends ICCService> T getCCService(Class<T> serviceType) throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.base.utils.IServiceFinder#findService(String, Class)
     */
    @Override
    public <T extends IHurricanService> T findService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return HurricanServiceFinder.instance().findService(serviceName, serviceType);
    }
}


