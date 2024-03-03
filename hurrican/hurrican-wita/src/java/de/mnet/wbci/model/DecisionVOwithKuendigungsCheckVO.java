/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.02.14
 */
package de.mnet.wbci.model;

/**
 * Extends the DecisionVO with the KuendigungsVO-Objekt. This class will only make sense for the {@link
 * DecisionAttribute#KUNDENWUNSCHTERMIN}.
 *
 *
 */
public class DecisionVOwithKuendigungsCheckVO extends DecisionVO {

    private KuendigungsCheckVO kuendigungsCheckVO;

    public DecisionVOwithKuendigungsCheckVO(DecisionAttribute attribute) {
        super(attribute);
    }

    public KuendigungsCheckVO getKuendigungsCheckVO() {
        return kuendigungsCheckVO;
    }

    public void setKuendigungsCheckVO(KuendigungsCheckVO kuendigungsCheckVO) {
        this.kuendigungsCheckVO = kuendigungsCheckVO;
    }
}
