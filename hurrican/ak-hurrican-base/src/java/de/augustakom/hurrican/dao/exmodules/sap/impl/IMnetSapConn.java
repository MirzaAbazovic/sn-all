package de.augustakom.hurrican.dao.exmodules.sap.impl;


import org.jawin.COMException;
import org.jawin.COMPtr;
import org.jawin.DispatchPtr;
import org.jawin.GUID;
import org.jawin.IdentityManager;

/**
 * Jawin generated code please do not edit
 * <p/>
 * Dispatch: IMnetSapConn Description: MnetSapConn_TypeLib Help file:
 *
 *
 */

public class IMnetSapConn extends DispatchPtr {
    public static final GUID DIID = new GUID("{e15ef506-a856-49d0-90DE-AC22082038C6}");
    public static final int IID_TOKEN;

    static {
        IID_TOKEN = IdentityManager.registerProxy(DIID, IMnetSapConn.class);
    }

    /**
     * The required public no arg constructor. <br><br> <b>Important:</b>Should never be used as this creates an
     * uninitialized IMnetSapConn (it is required by Jawin for some internal working though).
     */
    public IMnetSapConn() {
        super();
    }

    /**
     * For creating a new COM-object with the given progid and with the IMnetSapConn interface.
     *
     * @param progid the progid of the COM-object to create.
     */
    public IMnetSapConn(String progid) throws COMException {
        super(progid, DIID);
    }

    /**
     * For creating a new COM-object with the given clsid and with the IMnetSapConn interface.
     *
     * @param clsid the GUID of the COM-object to create.
     */
    public IMnetSapConn(GUID clsid) throws COMException {
        super(clsid, DIID);
    }

    /**
     * For getting the IMnetSapConn interface on an existing COM-object. This is an alternative to calling {@link
     * #queryInterface(Class)} on comObject.
     *
     * @param comObject the COM-object to get the IMnetSapConn interface on.
     */
    public IMnetSapConn(COMPtr comObject) throws COMException {
        super(comObject);
    }

    public int getIIDToken() {
        return IID_TOKEN;
    }


    /**
     * @return short
     */
    public int ekgrp_MaxLength() throws COMException {
        return ((Integer) invokeN("Ekgrp_MaxLength", new Object[] { })).intValue();
    }

    /**
     * @return short
     */
    public int bs_LineLength() throws COMException {
        return ((Integer) invokeN("Bs_LineLength", new Object[] { })).intValue();
    }

    /**
     * @return String
     */
    public String aktSaldo_Header() throws COMException {
        return (String) invokeN("AktSaldo_Header", new Object[] { });
    }

    /**
     * @return short
     */
    public int aktSaldo_MaxLength() throws COMException {
        return ((Integer) invokeN("AktSaldo_MaxLength", new Object[] { })).intValue();
    }

    /**
     * @param Debitornr
     * @param Buchungskreis
     * @return String
     */
    public int bank_Query(String Debitornr, String Buchungskreis) throws COMException {
        return ((Integer) invokeN("Bank_Query", new Object[] { Debitornr, Buchungskreis })).intValue();
    }

    /**
     * @return short
     */
    public int aktSaldo_LineLength() throws COMException {
        return ((Integer) invokeN("AktSaldo_LineLength", new Object[] { })).intValue();
    }

    /**
     * @return String
     */
    public String bs_Header() throws COMException {
        return (String) invokeN("Bs_Header", new Object[] { });
    }

    /**
     * @return short
     */
    public int bs_MaxLength() throws COMException {
        return ((Integer) invokeN("Bs_MaxLength", new Object[] { })).intValue();
    }

    /**
     * @param Debitornr
     * @param Buchungskreis
     * @return String
     */
    public String getMahnstufe(String Debitornr, String Buchungskreis) throws COMException {
        Object result = invokeN("GetMahnstufe", new Object[] { Debitornr, Buchungskreis });
        return (result instanceof String) ? (String) result : null;
    }

    /**
     * @return String
     */
    public String ekgrp_Get() throws COMException {
        return (String) invokeN("Ekgrp_Get", new Object[] { });
    }

    /**
     * @return String
     */
    public String op_Header() throws COMException {
        return (String) invokeN("Op_Header", new Object[] { });
    }

    /**
     * @return String
     */
    public String ap_Get() throws COMException {
        return (String) invokeN("Ap_Get", new Object[] { });
    }

    /**
     * @param Debitornr
     * @param Buchungskreis
     * @return String
     */
    public int ap_Query(String Debitornr, String Buchungskreis) throws COMException {
        return ((Integer) invokeN("Ap_Query", new Object[] { Debitornr, Buchungskreis })).intValue();
    }

    /**
     * @return short
     */
    public int bank_MaxLength() throws COMException {
        return ((Integer) invokeN("Bank_MaxLength", new Object[] { })).intValue();
    }

    /**
     * @return short
     */
    public int ap_MaxLength() throws COMException {
        return ((Integer) invokeN("Ap_MaxLength", new Object[] { })).intValue();
    }

    /**
     * @param User
     * @param Password
     * @return String
     */
    public void connect_KR4(String User, String Password) throws COMException {
        invokeN("Connect_KR4", new Object[] { User, Password });
    }

    /**
     * @return String
     */
    public String bank_Header() throws COMException {
        return (String) invokeN("Bank_Header", new Object[] { });
    }

    /**
     * @return short
     */
    public int ekgrp_LineLength() throws COMException {
        return ((Integer) invokeN("Ekgrp_LineLength", new Object[] { })).intValue();
    }

    /**
     * @return short
     */
    public int ap_LineLength() throws COMException {
        return ((Integer) invokeN("Ap_LineLength", new Object[] { })).intValue();
    }

    /**
     * @param User
     * @param Password
     * @return String
     */
    public void connect_XR4(String User, String Password) throws COMException {
        invokeN("Connect_XR4", new Object[] { User, Password });
    }

    /**
     * @return String
     */
    public String op_Get() throws COMException {
        return (String) invokeN("Op_Get", new Object[] { });
    }

    /**
     * @return short
     */
    public int op_MaxLength() throws COMException {
        return ((Integer) invokeN("Op_MaxLength", new Object[] { })).intValue();
    }

    /**
     * @param Debitornr
     * @param Buchungskreis
     * @return String
     */
    public int op_Query(String Debitornr, String Buchungskreis) throws COMException {
        return ((Integer) invokeN("Op_Query", new Object[] { Debitornr, Buchungskreis })).intValue();
    }

    /**
     * @return String
     */
    public String ekgrp_Header() throws COMException {
        return (String) invokeN("Ekgrp_Header", new Object[] { });
    }

    /**
     * @param Debitornr
     * @param Buchungskreis
     * @return String
     */
    public int bs_Query(String Debitornr, String Buchungskreis) throws COMException {
        return ((Integer) invokeN("Bs_Query", new Object[] { Debitornr, Buchungskreis })).intValue();
    }

    /**
     * @return String
     */
    public String bs_Get() throws COMException {
        return (String) invokeN("Bs_Get", new Object[] { });
    }

    /**
     * @param WHERE
     * @return String
     */
    public int ekgrp_Query(String WHERE) throws COMException {
        return ((Integer) invokeN("Ekgrp_Query", new Object[] { WHERE })).intValue();
    }

    /**
     * @param Debitornr
     * @param Buchungskreis
     * @return String
     */
    public int aktSaldo_Query(String Debitornr, String Buchungskreis) throws COMException {
        return ((Integer) invokeN("AktSaldo_Query", new Object[] { Debitornr, Buchungskreis })).intValue();
    }

    /**
     * @return short
     */
    public int bank_LineLength() throws COMException {
        return ((Integer) invokeN("Bank_LineLength", new Object[] { })).intValue();
    }

    /**
     * @return short
     */
    public int op_LineLength() throws COMException {
        return ((Integer) invokeN("Op_LineLength", new Object[] { })).intValue();
    }

    /**
     * @param User
     * @param Password
     * @return String
     */
    public void connect_PR4(String User, String Password) throws COMException {
        invokeN("Connect_PR4", new Object[] { User, Password });
    }

    /**
     * @return String
     */
    public void disconnect() throws COMException {
        invokeN("Disconnect", new Object[] { });
    }

    /**
     * @return String
     */
    public String bank_Get() throws COMException {
        return (String) invokeN("Bank_Get", new Object[] { });
    }

    /**
     * @return String
     */
    public String aktSaldo_Get() throws COMException {
        return (String) invokeN("AktSaldo_Get", new Object[] { });
    }

    /**
     * @return String
     */
    public String ap_Header() throws COMException {
        return (String) invokeN("Ap_Header", new Object[] { });
    }
}
