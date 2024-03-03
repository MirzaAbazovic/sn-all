package de.augustakom.hurrican.service.cc.impl;

import java.util.regex.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.annotation.CcTxRequiredReadOnly;
import de.augustakom.hurrican.dao.cc.CfgRegularExpressionDAO;
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.CfgRegularExpression.Info;
import de.augustakom.hurrican.service.cc.RegularExpressionService;


/**
 * Service fuer Regular Expressions, die in der Datenbank gehalten werden.
 *
 *
 */
@CcTxRequiredReadOnly
public class RegularExpressionServiceImpl extends DefaultCCService implements RegularExpressionService {
    private static final Logger LOGGER = Logger.getLogger(RegularExpressionServiceImpl.class);

    private CfgRegularExpressionDAO cfgRegularExpressionDAO;

    @Override
    public String match(Long refId, Class<?> refClass, Info requestedInfo, String match) {
        return internalMatch(refId, null, refClass, requestedInfo, match, true);
    }

    @Override
    public String match(String refName, Class<?> refClass, Info requestedInfo, String match) {
        return internalMatch(null, refName, refClass, requestedInfo, match, true);
    }

    @Override
    public String matches(Long refId, Class<?> refClass, Info requestedInfo, String match) {
        return internalMatch(refId, null, refClass, requestedInfo, match, false);
    }

    @Override
    public String matches(String refName, Class<?> refClass, Info requestedInfo, String match) {
        return internalMatch(null, refName, refClass, requestedInfo, match, false);
    }


    private String internalMatch(Long refId, String refName, Class<?> refClass, Info requestedInfo,
            String match, boolean returnMatch) {
        CfgRegularExpression regularExpression =
                cfgRegularExpressionDAO.findRegularExpression(refId, refName, refClass, requestedInfo);
        String error = "<no error>";
        if (regularExpression != null) {
            try {
                Matcher matcher = Pattern.compile(regularExpression.getRegExp()).matcher(match);
                if (matcher.matches()) {
                    if (!returnMatch) {
                        return null;
                    }
                    else {
                        Integer matchGroup = regularExpression.getMatchGroup();
                        if (matchGroup == null) {
                            matchGroup = Integer.valueOf(0);
                        }
                        if (matchGroup <= matcher.groupCount()) {
                            String result = matcher.group(matchGroup);
                            if (result != null) {
                                return result;
                            }
                            else {
                                error = "group " + matchGroup + " did not match anything";
                            }
                        }
                        else {
                            error = "not enough groups (groups: " + matcher.groupCount() + ", requested group: " + matchGroup + ")";
                        }
                    }
                }
                else {
                    if (!returnMatch) {
                        return regularExpression.getDescription();
                    }
                    error = "match string did not match";
                }
            }
            catch (PatternSyntaxException e) {
                error = "error in pattern: " + e.getMessage();
            }
        }
        else {
            error = "regular expression not found in database";
        }
        // Error - log info
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("internalMatch() - Error in RegularExpression for item with ");
        if (refId != null) {
            stringBuilder.append("id ");
            stringBuilder.append(refId);
        }
        if ((refId != null) && (refName != null)) {
            stringBuilder.append(" and ");
        }
        if (refName != null) {
            stringBuilder.append("name ");
            stringBuilder.append(refName);
        }
        stringBuilder.append(" (match string: '");
        stringBuilder.append(match);
        stringBuilder.append("')");
        stringBuilder.append(": ");
        stringBuilder.append(error);
        stringBuilder.append(". Returning null.");
        LOGGER.error(stringBuilder.toString());
        return null;
    }


    /**
     * Injected
     */
    public void setCfgRegularExpressionDAO(CfgRegularExpressionDAO cfgRegularExpressionDAO) {
        this.cfgRegularExpressionDAO = cfgRegularExpressionDAO;
    }
}
