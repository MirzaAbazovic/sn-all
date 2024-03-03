/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2012 16:21:00
 */
package de.augustakom.hurrican.model.wholesale;

import java.util.*;
import com.google.common.collect.ImmutableSet;

/**
 * DTO für WholesaleProduct.
 *
 *
 */
public class WholesaleProduct {
    WholesaleProductName name;
    Set<WholesaleProductAttribute> attributes = ImmutableSet.of();

    public void setName(WholesaleProductName name) {
        this.name = name;
    }

    public WholesaleProductName getName() {
        return name;
    }

    public WholesaleProductGroup getGroup() {
        return name.productGroup;
    }

    public Set<WholesaleProductAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<WholesaleProductAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((attributes == null) ? 0 : attributes.hashCode());
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WholesaleProduct other = (WholesaleProduct) obj;
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        }
        else if (!attributes.equals(other.attributes)) {
            return false;
        }
        if (name != other.name) {
            return false;
        }
        return true;
    }

}


