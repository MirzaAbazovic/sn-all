package de.mnet.wita.model;

public enum SendAllowed {

    OK(""),
    KWT_IN_ZUKUNFT("Der Kundenwunschtermin (%s) liegt weiter als %s Tage in der Zukunft."),
    SENDE_LIMIT("Für den Geschäftsfall %s ist das Sendelimit überschritten, so dass dieser erstmal zurückgehalten wird."),
    REQUEST_VORGEHALTEN("Die Anfrage wird für %s Minuten vorgehalten, um eine kostenpflichtige Stornierung zu vermeiden."),
    EARLIEST_SEND_DATE_IN_FUTURE("Der früheste mögliche Versand der Anfrage ist am %s.");

    private String template;

    private SendAllowed(String template) {
        this.template = template;
    }

    public String getText(Object... params) {
        return String.format(template, params);
    }
}
