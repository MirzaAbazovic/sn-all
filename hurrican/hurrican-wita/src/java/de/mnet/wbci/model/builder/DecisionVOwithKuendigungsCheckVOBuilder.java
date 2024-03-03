/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.02.14
 */
package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.DecisionAttribute;
import de.mnet.wbci.model.DecisionVOwithKuendigungsCheckVO;
import de.mnet.wbci.model.KuendigungsCheckVO;

/**
 *
 */
public class DecisionVOwithKuendigungsCheckVOBuilder extends DecisionVOBuilder {

    private KuendigungsCheckVO kuendigungsCheckVo;

    public DecisionVOwithKuendigungsCheckVOBuilder(DecisionAttribute attribute) {
        super(attribute);
        if (!DecisionAttribute.KUNDENWUNSCHTERMIN.equals(attribute)) {
            throw new IllegalArgumentException("The DecisionVOwithKuendigungsCheckVO is only valid for the attribute '"
                    + DecisionAttribute.KUNDENWUNSCHTERMIN + "'!");
        }
    }

    @Override
    public DecisionVOwithKuendigungsCheckVO build() {
        DecisionVOwithKuendigungsCheckVO result = enrich(new DecisionVOwithKuendigungsCheckVO(attribute));
        result.setKuendigungsCheckVO(kuendigungsCheckVo);
        return result;
    }

    public DecisionVOwithKuendigungsCheckVOBuilder withKuendigungsCheckVO(KuendigungsCheckVO kuendigungsCheckVo) {
        this.kuendigungsCheckVo = kuendigungsCheckVo;
        return this;
    }
}
