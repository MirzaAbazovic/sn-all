/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2012 11:18:41
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import java.util.*;
import com.google.common.collect.ImmutableMap;

import de.mnet.hurrican.wholesale.workflow.Product;
import de.mnet.hurrican.wholesale.workflow.Product.ProductAttributes;
import de.mnet.hurrican.wholesale.workflow.ProductGroup;
import de.mnet.hurrican.wholesale.workflow.ProductName;
import de.mnet.hurrican.wholesale.workflow.ServiceAttribute;


public class Produkt {
    private ImmutableMap<ProductName, Integer> productName2Downstream = ImmutableMap.<ProductName, Integer>builder()
            .put(ProductName.FTTB_16, 16000)
            .put(ProductName.FTTB_25, 25000)
            .put(ProductName.FTTB_50, 50000)
            .put(ProductName.FTTB_100, 100000)
            .build();
    private ImmutableMap<ProductName, Integer> productName2Upstream = ImmutableMap.<ProductName, Integer>builder()
            .put(ProductName.FTTB_16, 1000)
            .put(ProductName.FTTB_25, 5000)
            .put(ProductName.FTTB_50, 10000)
            .put(ProductName.FTTB_100, 10000)
            .build();

    public ProductGroup productGroup = ProductGroup.FTTB_BSA;
    public ProductName productName = ProductName.FTTB_50;
    public List<ServiceAttribute> productAttributes = new ArrayList<ServiceAttribute>();

    public static Produkt fttb100() {
        return fttbProdukt().productName(ProductName.FTTB_100);
    }

    public static Produkt fttb50() {
        return fttbProdukt().productName(ProductName.FTTB_50);
    }

    public static Produkt fttb25() {
        return fttbProdukt().productName(ProductName.FTTB_25);
    }

    public static Produkt fttb16() {
        return fttbProdukt().productName(ProductName.FTTB_16);
    }

    public static Produkt fttbProdukt() {
        return new Produkt().productGroup(ProductGroup.FTTB_BSA);
    }

    private static Produkt ftthProdukt() {
        return new Produkt().productGroup(ProductGroup.FTTH_BSA);
    }

    public static Produkt ftth16() {
        return ftthProdukt().productName(ProductName.FTTH_16);
    }

    public Product toXmlBean() {
        Product product = new Product();
        product.setProductGroup(productGroup);
        product.setProductName(productName);
        if (!productAttributes.isEmpty()) {
            ProductAttributes productAttributesXml = new ProductAttributes();
            productAttributesXml.getServiceAttribute().addAll(productAttributes);
            product.setProductAttributes(productAttributesXml);
        }
        return product;
    }

    public Produkt productGroup(ProductGroup productGroup) {
        this.productGroup = productGroup;
        return this;
    }

    public Produkt productName(ProductName productName) {
        this.productName = productName;
        return this;
    }

    public Produkt withTP() {
        productAttributes.add(ServiceAttribute.TP);
        return this;
    }

    public Produkt withDU() {
        productAttributes.add(ServiceAttribute.DU);
        return this;
    }

    public Produkt withExpressEnt() {
        productAttributes.add(ServiceAttribute.EXP_ENT);
        return this;
    }

    public Produkt addAttribute(ServiceAttribute attribute) {
        productAttributes.add(attribute);
        return this;
    }

    public Integer defaultUpstream() {
        return productName2Upstream.get(productName);
    }

    public Integer upstream() {
        Integer upstream = defaultUpstream();
        if (productAttributes.contains(ServiceAttribute.DU)) {
            upstream = 2 * upstream;
        }

        return upstream;
    }

    public Integer downstream() {
        return productName2Downstream.get(productName);
    }

    @Override
    public String toString() {
        String stringRep = productName.toString();
        if (!productAttributes.isEmpty()) {
            stringRep = stringRep + " " + productAttributes;
        }
        return stringRep;
    }

}

