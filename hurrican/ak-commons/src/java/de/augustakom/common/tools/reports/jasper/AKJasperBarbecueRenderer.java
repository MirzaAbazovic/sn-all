/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2004 09:46:43
 */
package de.augustakom.common.tools.reports.jasper;

import java.awt.*;
import java.awt.geom.*;
import net.sf.jasperreports.engine.JRAbstractSvgRenderer;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.output.OutputException;


/**
 * JasperReports Renderer-Objekt, um einen Barcode darzustellen. <br> Verwendung: <br> Im JasperReport ein Image
 * bereitstellen. Als Image-Expression folgenden Ausdruck aufnehmen: <code> new de.augustakom.common.tools.reports.jasper.BarbecueRenderer(
 * net.sourceforge.barbecue.BarcodeFactory.createCode128("barcode-value")) </code><br> Der Wert von
 * <code>barcode-value</code> muss natuerlich durch einen entsprechenden Wert (meist von einem Report-Parameter)
 * ausgetauscht werden.
 *
 *
 */
public class AKJasperBarbecueRenderer extends JRAbstractSvgRenderer {

    private Barcode barcode;

    /**
     * Konstruktor mit Angabe des darzustellenden Barcodes.
     */
    public AKJasperBarbecueRenderer(Barcode barcode) {
        this.barcode = barcode;
    }

    /**
     * @see net.sf.jasperreports.engine.JRRenderable#render(java.awt.Graphics2D, java.awt.geom.Rectangle2D)
     */
    public void render(Graphics2D grx, Rectangle2D rectangle) {
        if (barcode != null) {
            try {
                barcode.draw(grx, (int) rectangle.getX(), (int) rectangle.getY());
            }
            catch (OutputException e) {
                throw new RuntimeException(e);
            }
        }
    }

}


