/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.02.14
 */
package de.mnet.wbci.model;

import java.util.*;

import de.mnet.wita.exceptions.WitaUserException;

public class DecisionVOwithKuendigungsCheckVOHelper {

    /**
     * Looks-up and returns the {@link KuendigungsCheckVO} from the supplied {@code decisionVOs}, or throws a {@link
     * WitaUserException} if none is found.
     * <p/>
     * This method should not be used with a VA-RRNP geschaeftsfall, as the kuendigungs check is not relevant / carried
     * out for this geschaeftsfall type.
     *
     * @param decisionVOs
     * @return
     * @throws WitaUserException
     */
    public static KuendigungsCheckVO getTaifunKuendigungsstatus(Collection<DecisionVO> decisionVOs) throws WitaUserException {
        for (DecisionVO vo : decisionVOs) {
            if (vo instanceof DecisionVOwithKuendigungsCheckVO) {
                KuendigungsCheckVO kuendigungsCheckVO = ((DecisionVOwithKuendigungsCheckVO) vo).getKuendigungsCheckVO();
                if (kuendigungsCheckVO != null) {
                    return kuendigungsCheckVO;
                }
            }
        }
        throw new WitaUserException(
                "Couldn't determine KuendigungsCheckVo, please check the List of 'DecisionVOs' that at least one 'DecisionVoWithKuendigungsCheckVO' is included");
    }
}
