/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 09:55:14
 */
package de.augustakom.hurrican.model.cc.equipment;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * Modell-Klasse, um einen zu importierenden Port anzugeben. <br> Die Klasse enthaelt nur die absolut notwendigen
 * Import-Daten, die vorgegeben werden muessen. Alle weiteren Daten muessen von der entsprechenden Import-Methode selbst
 * erzeugt werden. Es handelt sich hier um kein persisitentes Modell!
 *
 *
 */
public class PortGeneratorImport extends AbstractCCModel {

    public static final int COL_ID_HW_EQN = 0;
    public static final int COL_ID_V5_PORT = 1;
    public static final int COL_ID_RANG_VERTEILER_IN = 2;
    public static final int COL_ID_RANG_BUCHT_IN = 3;
    public static final int COL_ID_RANG_LEISTE1_IN = 4;
    public static final int COL_ID_RANG_STIFT1_IN = 5;
    public static final int COL_ID_RANG_LEISTE2_IN = 6;
    public static final int COL_ID_RANG_STIFT2_IN = 7;
    public static final int COL_ID_RANG_VERTEILER_OUT = 8;
    public static final int COL_ID_RANG_BUCHT_OUT = 9;
    public static final int COL_ID_RANG_LEISTE1_OUT = 10;
    public static final int COL_ID_RANG_STIFT1_OUT = 11;
    public static final int COL_ID_RANG_LEISTE2_OUT = 12;
    public static final int COL_ID_RANG_STIFT2_OUT = 13;
    public static final int COL_ID_SWITCH = 14;

    private String hwEqn;
    private String v5Port;
    private String rangVerteilerIn;
    private String rangBuchtIn;
    private String rangLeiste1In;
    private String rangStift1In;
    private String rangLeiste2In;
    private String rangStift2In;
    private String rangVerteilerOut;
    private String rangBuchtOut;
    private String rangLeiste1Out;
    private String rangStift1Out;
    private String rangLeiste2Out;
    private String rangStift2Out;
    private String switchName;

    private boolean portAlreadyExists = false;

    public String getHwEqn() {
        return hwEqn;
    }

    public void setHwEqn(String hwEqn) {
        this.hwEqn = hwEqn;
    }

    public String getV5Port() {
        return v5Port;
    }

    public void setV5Port(String v5Port) {
        this.v5Port = v5Port;
    }

    public String getRangVerteilerIn() {
        return rangVerteilerIn;
    }

    public void setRangVerteilerIn(String rangVerteilerIn) {
        this.rangVerteilerIn = rangVerteilerIn;
    }

    public String getRangLeiste1In() {
        return rangLeiste1In;
    }

    public void setRangLeiste1In(String rangLeiste1In) {
        this.rangLeiste1In = rangLeiste1In;
    }

    public String getRangStift1In() {
        return rangStift1In;
    }

    public void setRangStift1In(String rangStift1In) {
        this.rangStift1In = rangStift1In;
    }

    public String getRangLeiste2In() {
        return rangLeiste2In;
    }

    public void setRangLeiste2In(String rangLeiste2In) {
        this.rangLeiste2In = rangLeiste2In;
    }

    public String getRangStift2In() {
        return rangStift2In;
    }

    public void setRangStift2In(String rangStift2In) {
        this.rangStift2In = rangStift2In;
    }

    public String getRangVerteilerOut() {
        return rangVerteilerOut;
    }

    public void setRangVerteilerOut(String rangVerteilerOut) {
        this.rangVerteilerOut = rangVerteilerOut;
    }

    public String getRangLeiste1Out() {
        return rangLeiste1Out;
    }

    public void setRangLeiste1Out(String rangLeiste1Out) {
        this.rangLeiste1Out = rangLeiste1Out;
    }

    public String getRangStift1Out() {
        return rangStift1Out;
    }

    public void setRangStift1Out(String rangStift1Out) {
        this.rangStift1Out = rangStift1Out;
    }

    public String getRangLeiste2Out() {
        return rangLeiste2Out;
    }

    public void setRangLeiste2Out(String rangLeiste2Out) {
        this.rangLeiste2Out = rangLeiste2Out;
    }

    public String getRangStift2Out() {
        return rangStift2Out;
    }

    public void setRangStift2Out(String rangStift2Out) {
        this.rangStift2Out = rangStift2Out;
    }

    public String getRangBuchtIn() {
        return rangBuchtIn;
    }

    public void setRangBuchtIn(String rangBuchtIn) {
        this.rangBuchtIn = rangBuchtIn;
    }

    public String getRangBuchtOut() {
        return rangBuchtOut;
    }

    public void setRangBuchtOut(String rangBuchtOut) {
        this.rangBuchtOut = rangBuchtOut;
    }

    public boolean getPortAlreadyExists() {
        return portAlreadyExists;
    }

    public void setPortAlreadyExists(boolean portAlreadyExists) {
        this.portAlreadyExists = portAlreadyExists;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }
}
