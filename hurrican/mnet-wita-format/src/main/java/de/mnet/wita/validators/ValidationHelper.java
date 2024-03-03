package de.mnet.wita.validators;

import org.apache.commons.lang.ArrayUtils;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.validators.groups.V1;
import de.mnet.wita.validators.groups.V2;
import de.mnet.wita.validators.groups.WorkflowV1;
import de.mnet.wita.validators.groups.WorkflowV2;

public class ValidationHelper {

    /**
     * Retrieves both the default and the workflow validation groups for the provided wita version.
     */
    public static Class<?>[] getAllValidationGroups(WitaCdmVersion witaCdmVersion) {
        Class<?>[] validationGroupsDefault = getDefaultValidationGroups(witaCdmVersion);
        Class<?>[] validationGroupsWorkflow = getWorkflowValidationGroups(witaCdmVersion);
        return (Class<?>[]) ArrayUtils.addAll(validationGroupsDefault, validationGroupsWorkflow);
    }

    /**
     * Retrieves the default validation groups for the provided wita version.
     */
    public static Class<?>[] getDefaultValidationGroups(WitaCdmVersion witaCdmVersion) {
        // todo WITAX: Welche Validatoren werden fuer V2 benoetigt ?
        if (witaCdmVersion == WitaCdmVersion.V1) {
            return new Class<?>[] { V1.class };
        }
        else if (witaCdmVersion == WitaCdmVersion.V2) {
            return new Class<?>[] { V2.class };
        }
        throw new RuntimeException(String.format("Unsupported Wita-Version '%s'!", witaCdmVersion));
    }

    /**
     * Retrieves the workflow validation groups for the provided wita version.
     */
    public static Class<?>[] getWorkflowValidationGroups(WitaCdmVersion witaCdmVersion) {
        if (witaCdmVersion == WitaCdmVersion.V1) {
            return new Class<?>[] { WorkflowV1.class };
        }
        else if (witaCdmVersion == WitaCdmVersion.V2) {
            // todo WITAX: Welche Validatoren werden fuer V2 benoetigt ?
            return new Class<?>[] { WorkflowV1.class, WorkflowV2.class };
        }
        else {
            throw new RuntimeException(String.format("Unsupported Wita-CDM-Version '%s'!", witaCdmVersion));
        }
    }

}
