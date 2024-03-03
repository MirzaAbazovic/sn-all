/**
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2008 13:15:45
 */
package de.augustakom.hurrican.gui.base.tree;

import java.awt.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.tree.tools.DynamicTreeNodeComparator;
import de.augustakom.hurrican.gui.base.tree.tools.NodeProperty;
import de.augustakom.hurrican.gui.base.tree.tools.TreeSortEntry;


/**
 * TreeNode-Implementierung fuer einen dynamischen Tree. <br>
 */
public abstract class DynamicTreeNode extends DefaultMutableTreeNode {
    private static final Logger LOGGER = Logger.getLogger(DynamicTreeNode.class);
    protected static final String IMAGE_BASE = "de/augustakom/hurrican/gui/images/";
    private static final long serialVersionUID = 1100947779586399228L;

    private Boolean childrenFound = null;
    protected final DynamicTree tree;

    private final List<TreeSortEntry> sort;


    /**
     * Leerer Konstruktor
     */
    public DynamicTreeNode(DynamicTree tree) {
        this(tree, true);
    }

    /**
     * Konstruktor mit Angabe des User-Objects
     */
    public DynamicTreeNode(DynamicTree tree, boolean allowsChildren) {
        super(null, allowsChildren);
        this.tree = tree;
        this.sort = getDefaultChildSort();
    }


    /**
     * Laedt die Kindknoten des Knotens
     */
    public final void loadChildren() {
        if (childrenFound == null) {
            this.removeAllChildren();
            try {
                List<DynamicTreeNode> childList = doLoadChildren();
                Collections.sort(childList, getComparatorChain());
                childList.forEach(this::add);
                childrenFound = !childList.isEmpty();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(
                        (Component) GUISystemRegistry.instance().getValue(GUISystemRegistry.REGKEY_MAINFRAME), e);
            }
            tree.getModel().nodeStructureChanged(this);
        }

        if (Boolean.TRUE.equals(childrenFound)) {
            for (Object child : this.children) {
                DynamicTreeNode childNode = (DynamicTreeNode) child;
                if (childNode.childrenFound == null) {
                    SwingWorker<List<DynamicTreeNode>, Object> worker =
                            new LoadChildrenInBackgroundWorker(tree, childNode, false);
                    worker.execute();
                }
            }
        }
    }


    /**
     * Laedt die Kindknoten des Knotens neu, auch wenn sie bereits geladen waren
     */
    public final void refreshChildren() {
        childrenFound = null;
        loadChildren();
    }


    /**
     * Constructs a comparator chain of the sort list. If the sort list is empty, a comparator is returned that does not
     * alter the order.
     */
    private Comparator<DynamicTreeNode> getComparatorChain() {
        if (!sort.isEmpty()) {
            ComparatorChain chain = new ComparatorChain();
            for (TreeSortEntry treeSortEntry : sort) {
                chain.addComparator(new DynamicTreeNodeComparator(treeSortEntry));
            }
            @SuppressWarnings("unchecked")
            Comparator<DynamicTreeNode> result = chain;
            return result;
        }
        return (o1, o2) -> 0;
    }


    /**
     * @return A list of TreeSortEntries, defining the default sort order of the children.
     */
    @SuppressWarnings("unchecked")
    protected List<TreeSortEntry> getDefaultChildSort() {
        return Collections.EMPTY_LIST;
    }


    /**
     * Gibt zurück, ob für den Knoten Child-Elemente gefunden wurden
     *
     * @return true = es gibt Children, false = es gibt keine Children, null = Children wurden noch nicht geladen
     */
    public Boolean getChildrenFound() {
        return childrenFound;
    }


    /**
     * @return A list of node 'properties' that can be used for sorting, filtering etc.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<NodeProperty> getNodeProperties() {
        try {
            Field field = this.getClass().getDeclaredField("NODE_PROPERTIES");
            if ((field != null) && NodeProperty.class.equals(field.getType().getComponentType())) {
                NodeProperty[] result = (NodeProperty[]) field.get(this);
                return Arrays.asList(result);
            }
        }
        catch (Exception e) {
            LOGGER.debug("getNodeProperties() - exception while trying to get field NODE_PROPERTIES", e);
        }
        return Collections.EMPTY_LIST;
    }


    /**
     * @return {@code true}, falls Kindknoten gefunden wurden, sonst {@code false}
     */
    protected List<DynamicTreeNode> doLoadChildren() throws Exception {
        return new ArrayList<>(0);
    }


    /**
     * Ermittelt den Text, der als NodeName dargestellt werden soll
     */
    public abstract String getDisplayName();

    /**
     * Liefert den Text, der als Tooltip dargestellt werden soll
     */
    public String getTooltip() {
        return null;
    }

    /**
     * Liefert das Icon zurueck, das der Knoten haben soll
     */
    public abstract String getIcon();

    /**
     * Fuehrt eine Standard-Aktion aus. Wird nur aufgerufen, falls getPanel() null liefert, also die Standard-Aktion
     * nicht "Panel anzeigen" ist.
     */
    public void defaultAction() {
    }

    /**
     * Gibt eine Liste von Actions zurueck, die im Context-Menu des Nodes angezeigt werden sollen.
     */
    public List<AKAbstractAction> getNodeActionsForContextMenu() {
        return null;
    }


    /**
     * @return {@code true} falls der Knoten immer ein Blatt ist, {@code false} falls der Knoten Kinder hat oder haben
     * koennte
     */
    @Override
    public boolean isLeaf() {
        return Boolean.FALSE.equals(childrenFound);
    }


    /**
     * @return {@literal <className>: <displayName>}
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getDisplayName();
    }


    /**
     * Findet den uebergeordneten Knoten des gesuchten Typs. Sucht bis zur Wurzel.
     *
     * @return Knoten, oder {@code null}, falls keiner gefunden wurde.
     */
    public <T extends DynamicTreeNode> T getParentOfType(Class<T> type) {
        TreeNode parent = getParent();
        while ((parent != null) && !parent.getClass().equals(type)) {
            parent = parent.getParent();
        }
        if ((parent != null) && parent.getClass().equals(type)) {
            @SuppressWarnings("unchecked")
            T result = (T) parent;
            return result;
        }
        return null;
    }


    /**
     * Get a list of all children
     */
    public List<DynamicTreeNode> getChildren() {
        if (this.childrenFound == null) {
            loadChildren();
        }
        if (this.childrenFound) {
            @SuppressWarnings("unchecked")
            List<DynamicTreeNode> result = new ArrayList<>(this.children);
            return result;
        }
        @SuppressWarnings("unchecked")
        List<DynamicTreeNode> result = Collections.EMPTY_LIST;
        return result;
    }


    /**
     * Ermittelt das Hurrican Main-Frame und gibt es zurueck.
     */
    public AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }


    /**
     * @return A property path consisting of the given parts
     */
    protected static String propPath(String... parts) {
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (builder.length() > 0) {
                builder.append(".");
            }
            builder.append(part);
        }
        return builder.toString();
    }

    public DynamicTree getTree() {
        return tree;
    }

    /**
     * Laedt Kindknoten im Hintergrund und expandiert dann den Knoten, falls expand gesetzt ist
     */
    public void loadChildrenInBackground(boolean expand) {
        SwingWorker<List<DynamicTreeNode>, Object> worker =
                new LoadChildrenInBackgroundWorker(tree, this, expand);
        worker.execute();
    }


    /**
     * Laedt Kindknoten im Hintergrund
     */
    private static class LoadChildrenInBackgroundWorker extends SwingWorker<List<DynamicTreeNode>, Object> {
        private final DynamicTree tree;
        private final DynamicTreeNode node;
        private final boolean expand;

        public LoadChildrenInBackgroundWorker(DynamicTree tree, DynamicTreeNode node, boolean expand) {
            this.tree = tree;
            this.node = node;
            this.expand = expand;
        }

        @Override
        protected List<DynamicTreeNode> doInBackground() throws Exception {
            List<DynamicTreeNode> childList = node.doLoadChildren();
            Collections.sort(childList, node.getComparatorChain());
            return childList;
        }

        @Override
        protected void done() {
            // Do not overwrite data that was already loaded
            if (node.childrenFound == null) {
                List<DynamicTreeNode> children;
                try {
                    children = get();
                    if (children != null) {
                        children.forEach(node::add);
                        node.childrenFound = !children.isEmpty();
                        tree.getModel().nodeStructureChanged(node);
                    }
                }
                catch (Exception ignored) {
                    LOGGER.warn(ignored.getMessage());
                }
            }
            if (expand) {
                TreePath path = new TreePath(node.getPath());
                tree.expandPath(path);
            }
        }
    }
}
