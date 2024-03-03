/**
 *
 */
package de.augustakom.hurrican.model.cc;


/**
 * Verwendungszweck fuer IP Adressen
 */
public enum IPPurpose {

    Kundennetz(22370L),
    Transfernetz(22371L),
    DhcpV6Pd(22373L);

    private IPPurpose(Long id) {
        this.id = id;
    }

    private Long id;

    public Long getId() {
        return id;
    }
}
