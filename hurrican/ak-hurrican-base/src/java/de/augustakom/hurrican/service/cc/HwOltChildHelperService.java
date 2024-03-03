package de.augustakom.hurrican.service.cc;

import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;

public interface HwOltChildHelperService extends ICCService {

    public <T extends HWOltChild> CPSTransaction createAndSendCpsTx(final T oltChild, final Long serviceOrderType,
            final Long sessionId);

    public <T extends HWOltChild> boolean isOltChildDeleted(final T oltChild);
}
