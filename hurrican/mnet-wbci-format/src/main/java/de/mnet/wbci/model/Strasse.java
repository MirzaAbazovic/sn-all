package de.mnet.wbci.model;

import java.io.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.NotEmpty;

import de.mnet.wbci.validation.groups.V1RequestVa;

public class Strasse implements Serializable {

    private static final long serialVersionUID = 5747379831655415956L;

    private String strassenname;
    private String hausnummer;
    private String hausnummernZusatz;

    @NotEmpty(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Size(min = 1, max = 40, groups = { V1RequestVa.class })
    public String getStrassenname() {
        return strassenname;
    }

    public void setStrassenname(String strassenname) {
        this.strassenname = strassenname;
    }

    @NotEmpty(groups = { V1RequestVa.class }, message = "darf nicht leer sein")
    @Pattern(regexp = "\\d{1,4}", groups = { V1RequestVa.class },
            message = "ungültiges Format: erwartet 1 - 4 nummerische Zeichen")
    public String getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }

    @Size(min = 1, max = 6, groups = { V1RequestVa.class })
    public String getHausnummernZusatz() {
        return hausnummernZusatz;
    }

    public void setHausnummernZusatz(String hausnummernZusatz) {
        this.hausnummernZusatz = hausnummernZusatz;
    }

    @Override
    public String toString() {
        return "Strasse{" +
                "strassenname='" + strassenname + '\'' +
                ", hausnummer='" + hausnummer + '\'' +
                ", hausnummernZusatz='" + hausnummernZusatz + '\'' +
                '}';
    }
}
