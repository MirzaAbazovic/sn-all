/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.05.2004 10:29:53
 */

package de.augustakom.common.gui.swing.table;

import java.util.*;
import javax.swing.table.*;

/**
 * Implementierung fuer einen gruppierbaren Table-Header. <br> Download unter: <br>
 * http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html <br><br> Beispiel: <br> Folgender Header soll
 * gruppiert werden: <br> <table border="1"> <tr> <td>Column-One</td> <td>Column-Two</td> <td>Column-Three</td> </tr>
 * </table> <br><br> Ziel-Header: <br> <table border="1"> <tr> <td>Column-One</td> <td align="center"
 * colspan="2">ColumnGroup</td> </tr> <tr> <td></td> <td>Column-Two</td> <td>Column-Three</td> </tr> </table> <br><br>
 * Der zugehoerige Source-Code wuerde wie folgt aussehen: <br><br> <code> // Table 'ganz normal' erzeugen <br>
 * TableColumnModel columnModel = table.getColumnModel();                   <br> AKTableColumnGroup cg = new
 * AKTableColumnGroup("ColumnGroup");           <br> cg.add(columnModel.getColumn(1)); <br>
 * cg.add(columnModel.getColumn(2));                                        <br> AKGroupableTableHeader header = new
 * AKGroupableTableHeader(columnModel); <br> header.addColumnGroup(cg); <br> table.setTableHeader(header); <br> </code>
 */
public class AKGroupableTableHeader extends JTableHeader {
    protected Vector tableColumnGroups = null;

    public AKGroupableTableHeader(TableColumnModel model) {
        super(model);
        setUI(new AKGroupableTableHeaderUI());
        setReorderingAllowed(false);
    }

    public void setReorderingAllowed(boolean b) {
        reorderingAllowed = false;
    }

    public void addTableColumnGroup(AKTableColumnGroup g) {
        if (tableColumnGroups == null) {
            tableColumnGroups = new Vector();
        }
        tableColumnGroups.addElement(g);
    }

    public Enumeration getColumnGroups(TableColumn col) {
        if (tableColumnGroups == null) {
            return null;
        }
        Enumeration e = tableColumnGroups.elements();
        while (e.hasMoreElements()) {
            AKTableColumnGroup cGroup = (AKTableColumnGroup) e.nextElement();
            Vector v_ret = cGroup.getColumnGroups(col, new Vector());
            if (v_ret != null) {
                return v_ret.elements();
            }
        }
        return null;
    }

    public void setColumnMargin() {
        if (tableColumnGroups == null) {
            return;
        }

        int columnMargin = getColumnModel().getColumnMargin();
        Enumeration e = tableColumnGroups.elements();
        while (e.hasMoreElements()) {
            AKTableColumnGroup cGroup = (AKTableColumnGroup) e.nextElement();
            cGroup.setColumnMargin(columnMargin);
        }
    }

}
