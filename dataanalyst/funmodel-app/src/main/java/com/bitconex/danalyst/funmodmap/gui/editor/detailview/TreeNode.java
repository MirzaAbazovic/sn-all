package com.bitconex.danalyst.funmodmap.gui.editor.detailview;

import com.bitconex.danalyst.funmodmap.gui.commons.ImageHelper;
import com.bitconex.danalyst.funmodmap.gui.editor.detailview.actions.*;
import com.bitconex.danalyst.funmodmap.model.NodeType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import java.util.*;

public class TreeNode extends Group implements SelectableNode{
    public final static double WIDTH_RECT = 110;
    public final static double HEIGHT_RECT = 60;

    public final static double WIDTH_VALUE_INPUT = 55;

    public final static double SIZE_IMAGE = 25;

    // Border ist 20% größer als Rechteck
    public final static double INIT_WIDTH_BORDER = WIDTH_RECT + (WIDTH_RECT/100) * 20;
    public final static double INIT_HEIGHT_BORDER = HEIGHT_RECT + (HEIGHT_RECT/100) * 30;

    public final static double PADDING_INSIDE = (WIDTH_RECT/100) * 5;
    public final static double ARC_LENGTH= 10;
    public final static double SHADOW_OFFSET = (WIDTH_RECT/100) * 5;
    public final static double LENGTH_EXPAND_RECT = (WIDTH_RECT/100) * 10;
    public final static double LENGTH_EXPAND_RECT_LINE = (LENGTH_EXPAND_RECT/100) * 60;

    private final static String DRAG_DROP_CTX_PFX="TREE_NODE_ID:";

    private TreeActionListener treeActionListener;
    private Map<TreeNode, Group> childLinesMap = new HashMap<>();
    private ObjectProperty<TreeNode> parentNodeProperty = new SimpleObjectProperty<>(this, "parentNode");
    private ObjectProperty<TreeNode> previousNodeProperty  = new SimpleObjectProperty<>(this, "previousNode");
    private ObjectProperty<TreeNode> nextNodeProperty = new SimpleObjectProperty<>(this, "nextNode");

    private StringProperty nodeIdProperty = new SimpleStringProperty(this, "nodeId");
    private ListProperty<TreeNode> childListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private StringProperty indexProperty = new SimpleStringProperty(this, "index");
    private StringProperty nameProperty = new SimpleStringProperty(this, "name");
    private DoubleProperty valueProperty = new SimpleDoubleProperty(this, "value");
    private StringProperty valueTxtProperty = new SimpleStringProperty(this, "valueTxt");
    private ObjectProperty<NodeType> typeProperty = new SimpleObjectProperty<>(this, "type");
    private ObjectProperty<Image> typeImageProperty = new SimpleObjectProperty<>(this, "typeImage");
    private ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(this, "color");
    private BooleanProperty expandStateProperty = new SimpleBooleanProperty(this, "expandState");
    private DoubleProperty startXBorderProperty = new SimpleDoubleProperty(this, "startXBorder");
    private DoubleProperty startYBorderProperty = new SimpleDoubleProperty(this, "startYBorder");
    private DoubleProperty endXBorderProperty = new SimpleDoubleProperty(this, "endXBorder");
    private DoubleProperty endYBoderProperty = new SimpleDoubleProperty(this, "endYBorder");
    private DoubleProperty heightBorderProperty = new SimpleDoubleProperty(this, "heightBorder");
    private DoubleProperty widthBorderProperty = new SimpleDoubleProperty(this, "widthBorder");
    private DoubleProperty connectPointInXProperty = new SimpleDoubleProperty(this, "connectPointInX");
    private DoubleProperty connectPointInYProperty = new SimpleDoubleProperty(this, "connectPointInY");
    private DoubleProperty connectPointOutXProperty = new SimpleDoubleProperty(this, "connectPointOutX");
    private DoubleProperty connectPointOutYProperty = new SimpleDoubleProperty(this, "connectPointOutY");




    private Rectangle nodeRect;
    private ImageView typeImageView;
    TextField nameTextField = new TextField();
    TextField valueTextField = new TextField();
    DropShadow rectShadow;
    private Text nameLabel;
    private Text valueLabel;
    private Text indexLabel;
    private Group nodeRectContentGroup = new Group();



    public TreeNode(TreeNode parentNode, TreeNode previousNode, TreeNode nextNode) {
        this.parentNodeProperty.set(parentNode);
        this.previousNodeProperty.set(previousNode);
        this.nextNodeProperty.set(nextNode);
        bindNeighbourPositionDependencies();
        bindPositionProperties();
        bindImageTypeProperties();
        bindValueTxtProperty();
        setFocusTraversable(true);


        widthBorderProperty.set(INIT_WIDTH_BORDER);
        heightBorderProperty.set(INIT_HEIGHT_BORDER);
        endXBorderProperty.bind(startXBorderProperty.add(widthBorderProperty));
        endYBoderProperty.bind(startYBorderProperty.add(heightBorderProperty));

        createNodeRect();
        setExpandState(false);
        expandStateProperty.addListener((observableObject, oldVal, newVal) -> {
                    if(newVal) {
                        expandChildElements();
                    } else {
                        closeChildElements();
                    }
        });
        createContextMenu();
        setupDragAndDropForMove();

    }

    public StringProperty nodeIdProperty() {return nodeIdProperty;}
    public ObjectProperty<TreeNode> parentNodeProperty() {
        return parentNodeProperty;
    }
    public final StringProperty indexProperty() {return indexProperty;}
    public final StringProperty nameProperty() {return nameProperty;}
    public final DoubleProperty valueProperty() {return valueProperty;}
    public final ObjectProperty<NodeType> typeProperty() {return typeProperty;}
    public final ObjectProperty<Image> typeImageProperty() {return typeImageProperty;}
    public final ObjectProperty<Color> colorProperty() {return colorProperty;}
    public final ListProperty<TreeNode> childListProperty() {return childListProperty;}
    public final BooleanProperty expandStateProperty() {return expandStateProperty;}
    public final DoubleProperty connectPointInXProperty() {return connectPointInXProperty;}
    public final DoubleProperty connectPointInYProperty() {return connectPointInYProperty;}
    public final DoubleProperty connectPointOutXProperty() {return connectPointOutXProperty;}
    public final DoubleProperty connectPointOutYProperty() {return connectPointOutYProperty;}
    public final StringProperty valueTxtProperty() {return valueTxtProperty;}

    public TreeNode getParentNode() {return this.parentNodeProperty.get();}
    public void setParentNode(TreeNode parentNode) {this.parentNodeProperty.set(parentNode);}

    public TreeNode getPreviousNode() {return this.previousNodeProperty.get();}
    public void setPreviousNode(TreeNode previousNode) { this.previousNodeProperty.set(previousNode);}

    public TreeNode getNextNode() {return this.nextNodeProperty.get();}
    public void setNextNode(TreeNode nextNode) {this.nextNodeProperty.set(nextNode);}

    public List<TreeNode> getChildList () {return this.childListProperty.get();}

    public String getIndex() { return indexProperty.get(); }
    public void setIndex(String index) {indexProperty.set(index);}

    public String getName() { return nameProperty.get(); }
    public void setName(String name) {nameProperty.set(name);}

    public Double getValue() {return valueProperty.get();}
    public void setValue(Double value) {valueProperty.set(value);}

    public NodeType getType() {return typeProperty.get(); }
    public void setType(NodeType type) {typeProperty.set(type);}

    public Image getTypeImage() {return typeImageProperty.get();}
    public void setTypeImage(Image image) {typeImageProperty.set(image);}

    public Color getColor() {return this.colorProperty.get();}
    public void setColor(Color color)  {this.colorProperty.set(color);}

    public Boolean isExpandState() {return expandStateProperty.get();}
    public void setExpandState(Boolean b) {expandStateProperty.set(b);}

    public Double getHeightBorder() {return heightBorderProperty.get();}
    public void setHeightBorder(Double heightBorder) {heightBorderProperty.set(heightBorder);}

    public Double getWidthBorder() {return widthBorderProperty.get();}
    public void setWidthBorder(Double widthBorder) {widthBorderProperty.set(widthBorder);}

    public Double getConnectPointXIn() {return connectPointInXProperty.get();}
    public void setConnectPointInX(Double connectPointInX) {connectPointInXProperty.set(connectPointInX);}

    public Double getConnectPointInY() {return connectPointInYProperty.get();}
    public void setConnectPointInY(Double connectPointInY) {connectPointInYProperty.set(connectPointInY);}

    public Double getConnectPointOutX() {return connectPointOutXProperty.get();}
    public void setConnectPointOutX(Double connectPointOutX) {connectPointOutXProperty.set(connectPointOutX);}

    public Double getConnectPointOutY() {return connectPointOutYProperty.get();}
    public void setConnectPointOutY(Double connectPointOutY) {connectPointOutYProperty.set(connectPointOutY);}

    public String getNodeId() {
        return nodeIdProperty.get();
    }
    public void setNodeId(String nodeId) {this.nodeIdProperty.set(nodeId);}
    public TreeActionListener getTreeActionListener() {
        return treeActionListener;
    }
    public void setTreeActionListener(TreeActionListener treeActionListener) {
        this.treeActionListener = treeActionListener;
    }

    private void bindNeighbourPositionDependencies() {
        previousNodeProperty.addListener((observableValue, oldPrev, newPrev) -> {
            bindPositionProperties();
        });
    }

    private void bindPositionProperties() {
        if(startXBorderProperty.isBound()) {
            startXBorderProperty.unbind();
        } else if(startYBorderProperty.isBound()) {
            startYBorderProperty.unbind();
        }
        if(getPreviousNode() != null) {
            startXBorderProperty.bind(getPreviousNode().startXBorderProperty);
            startYBorderProperty.bind(getPreviousNode().endYBoderProperty);
        } else { //erstes Node in der ChildList
            if(getParentNode() != null) {
                startXBorderProperty.bind(getParentNode().startXBorderProperty.add(INIT_WIDTH_BORDER));
                startYBorderProperty.bind(getParentNode().startYBorderProperty);
            } else { //Root Node
                startXBorderProperty.set(150);
                startYBorderProperty.set(150);
            }

        }
    }

    private void bindImageTypeProperties() {
        this.typeProperty.addListener((observableValue, oldVal, newVal) -> {
            this.setTypeImage(ImageHelper.createImage(newVal.getImgPath(), SIZE_IMAGE, SIZE_IMAGE));
        });
    }

    private void bindValueTxtProperty() {
        this.valueTxtProperty.set(String.format(" %.2f", getValue()));
        this.valueProperty.addListener((observableValue, oldVal, newVal) -> {
            this.valueTxtProperty.set(String.format(" %.2f", newVal));
        });
    }

    private void createNodeRect() {

        nodeRect = new Rectangle();
        nodeRect.xProperty().bind(startXBorderProperty.add( (INIT_WIDTH_BORDER-WIDTH_RECT)/2 ));
        nodeRect.yProperty().bind(heightBorderProperty.divide(2).subtract(HEIGHT_RECT/2).add(startYBorderProperty));
        nodeRect.setWidth(WIDTH_RECT);
        nodeRect.setHeight(HEIGHT_RECT);
        rectShadow = new DropShadow();
        rectShadow.setOffsetX(SHADOW_OFFSET);
        rectShadow.setOffsetY(SHADOW_OFFSET);
        nodeRect.setEffect(rectShadow);
        nodeRect.fillProperty().bind(colorProperty);
        nodeRect.setArcHeight(ARC_LENGTH);
        nodeRect.setArcWidth(ARC_LENGTH);
        nameLabel = new Text();
        indexLabel = new Text();
        valueLabel = new Text();
        nameLabel.xProperty().bind(nodeRect.xProperty().add(PADDING_INSIDE));
        nameLabel.yProperty().bind(nodeRect.yProperty().add(HEIGHT_RECT - PADDING_INSIDE));
        nameLabel.textProperty().bind(nameProperty());
        handleNameLabelInput();
        indexLabel.xProperty().bind(nodeRect.xProperty().add(PADDING_INSIDE));
        indexLabel.yProperty().bind(nodeRect.yProperty().add(HEIGHT_RECT/2));
        indexLabel.textProperty().bind(indexProperty);
        valueLabel.xProperty().bind(nodeRect.xProperty().add((WIDTH_RECT/2)-(WIDTH_VALUE_INPUT/2)));
        valueLabel.yProperty().bind(nodeRect.yProperty().add(valueLabel.getFont().getSize()));
        valueLabel.textProperty().bind(valueTxtProperty);
        handleValueLabelInput();
        createTypeImageView();
        nodeRectContentGroup.getChildren().addAll(nodeRect, nameLabel, indexLabel,
                valueLabel, createExpandRectangle(), typeImageView);
        getChildren().add(nodeRectContentGroup);
    }

    private void handleNameLabelInput() {
        nameLabel.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount()==2) {
                nameLabel.setVisible(false);
                nameTextField.layoutXProperty().unbind();
                nameTextField.layoutYProperty().unbind();
                nameTextField.layoutXProperty().bind(nodeRect.xProperty().add(PADDING_INSIDE));
                nameTextField.layoutYProperty().bind(nodeRect.yProperty().add(HEIGHT_RECT - PADDING_INSIDE));
                nameTextField.setMaxWidth(WIDTH_RECT-(PADDING_INSIDE*2));
                getChildren().add(nameTextField);
                nameTextField.setText(getName());
                nameTextField.requestFocus();
                nameTextField.selectAll();
                nameTextField.focusedProperty().addListener((observableValue, oldVal, newVal) -> {
                    if(!newVal) {
                        handleChangeName();
                    }
                });
                nameTextField.setOnKeyPressed(actionEvent -> {
                    if(actionEvent.getCode().equals(KeyCode.ENTER)) {
                        handleChangeName();
                    }
                });
            }
        });
    }

    private void handleChangeName() {
        try {
            if(nameTextField.getText() != null && nameTextField.getText().trim().length()>0) {
                new ChangeNameAction(this.treeActionListener, getNodeId(), nameTextField.getText().trim()).executeAction();
            }
        } finally {
            getChildren().remove(nameTextField);
            nameLabel.setVisible(true);
        }
    }

    private void handleValueLabelInput() {
        valueLabel.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount()==2) {
                valueLabel.setVisible(false);
                valueTextField.layoutXProperty().unbind();
                valueTextField.layoutYProperty().unbind();

                /*
                 valueLabel.xProperty().bind(nodeRect.xProperty().add((WIDTH_RECT/2)-(WIDTH_VALUE_INPUT/2)));
        valueLabel.yProperty().bind(nodeRect.yProperty().add(valueLabel.getFont().getSize()));
                 */
                valueTextField.layoutXProperty().bind(nodeRect.xProperty().add((WIDTH_RECT/2)-(WIDTH_VALUE_INPUT/2)));
                valueTextField.layoutYProperty().bind(nodeRect.yProperty().add(valueTextField.getFont().getSize()));
                valueTextField.setMaxWidth(WIDTH_VALUE_INPUT);
                getChildren().add(valueTextField);
                valueTextField.setText(valueTxtProperty.get());
                valueTextField.requestFocus();
                valueTextField.selectAll();
                valueTextField.focusedProperty().addListener((observableValue, oldVal, newVal) -> {
                    if(!newVal) {
                        handleChangeValue();
                    }
                });
                valueTextField.setOnKeyPressed(actionEvent -> {
                    if(actionEvent.getCode().equals(KeyCode.ENTER)) {
                        handleChangeValue();
                    }
                });
            }
        });
    }

    private void handleChangeValue() {
        try {
            if(valueTextField.getText() != null && valueTextField.getText().trim().length()>0) {
                Double newVal = Double.valueOf(valueTextField.getText().trim().replace(',', '.'));
                new ChangeValueAction(this.treeActionListener, getNodeId(), newVal).executeAction();
            }
        } finally {
            getChildren().remove(valueTextField);
            valueLabel.setVisible(true);
        }

    }

    private void createTypeImageView() {
        this.typeImageView = new ImageView();
        typeImageView.xProperty().bind(nodeRect.xProperty().add(WIDTH_RECT).subtract(SIZE_IMAGE));
        typeImageView.yProperty().bind(nodeRect.yProperty());
        typeImageView.imageProperty().bind(typeImageProperty);
    }

    private Group createExpandRectangle() {
        Rectangle expRect = new Rectangle();
        expRect.xProperty().bind(nodeRect.xProperty().add(WIDTH_RECT - (LENGTH_EXPAND_RECT/2)));
        expRect.yProperty().bind(nodeRect.yProperty().add((HEIGHT_RECT/2) - (LENGTH_EXPAND_RECT/2)));
        expRect.setWidth(LENGTH_EXPAND_RECT);
        expRect.setHeight(LENGTH_EXPAND_RECT);

        expRect.fillProperty().bind(colorProperty);
        expRect.setStroke(Color.BLACK);
        Line hLineInRect = new Line();
        hLineInRect.startXProperty().bind(expRect.xProperty().add((LENGTH_EXPAND_RECT - LENGTH_EXPAND_RECT_LINE) / 2));
        hLineInRect.startYProperty().bind(expRect.yProperty().add(LENGTH_EXPAND_RECT/2));
        hLineInRect.endXProperty().bind(expRect.xProperty().add(expRect.widthProperty()).subtract((LENGTH_EXPAND_RECT - LENGTH_EXPAND_RECT_LINE) / 2));
        hLineInRect.endYProperty().bind(expRect.yProperty().add(LENGTH_EXPAND_RECT/2));

        Line vLineInRect = new Line();
        vLineInRect.startXProperty().bind(expRect.xProperty().add(LENGTH_EXPAND_RECT/2));
        vLineInRect.startYProperty().bind(expRect.yProperty().add((LENGTH_EXPAND_RECT - LENGTH_EXPAND_RECT_LINE) / 2));
        vLineInRect.endXProperty().bind(expRect.xProperty().add(LENGTH_EXPAND_RECT/2));
        vLineInRect.endYProperty().bind(expRect.yProperty().add(expRect.heightProperty()).subtract((LENGTH_EXPAND_RECT - LENGTH_EXPAND_RECT_LINE) / 2));
        /**
         * Wir binden die Verbindungspunkte an die horizontale Linie des "Plus" Zeichens des Expand-Rechtecks
         */
        connectPointOutXProperty.bind(hLineInRect.endXProperty());
        connectPointOutYProperty.bind(hLineInRect.endYProperty());
        connectPointInXProperty.bind(nodeRect.xProperty());
        connectPointInYProperty.bind(nodeRect.yProperty().add(HEIGHT_RECT/2));


        vLineInRect.visibleProperty().bind(expandStateProperty().not());
        Group expRectGr = new Group(expRect, hLineInRect, vLineInRect);
        expRectGr.visibleProperty().bind(childListProperty().emptyProperty().not());
        expRectGr.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setExpandState(!isExpandState());
            }
        });
        return expRectGr;
    }



    private void createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addNewMenuItem = new MenuItem(null, new Label("Add child"));
        addNewMenuItem.setOnAction(actionEvent -> {
            new AddNodeAction(this.treeActionListener, this.getNodeId()).executeAction();
        });
        MenuItem removeMenuItem = new MenuItem(null, new Label("Remove"));
        removeMenuItem.setOnAction(actionEvent -> {
            new RemoveNodeAction(this.treeActionListener, this.getNodeId()).executeAction();
        });

        MenuItem moveUpMenuItem = new MenuItem(null, new Label("Move Up"));
        moveUpMenuItem.setOnAction(actionEvent -> {
            if(this.getParentNode()!=null) {
                int currentIndex = this.getParentNode().getChildList().indexOf(this);
                if(currentIndex > 0) {
                    new ChangeChildIndexAction(treeActionListener, this.getNodeId(), currentIndex-1).executeAction();
                }
            }
        });
        MenuItem moveDownMenuItem = new MenuItem(null, new Label("Move Down"));
        moveDownMenuItem.setOnAction(actionEvent -> {
            if(this.getNextNode()!=null) {
                int currentIndex = this.getParentNode().getChildList().indexOf(this);
                if(currentIndex < this.getParentNode().getChildList().size()-1) {
                    new ChangeChildIndexAction(treeActionListener, this.getNodeId(), currentIndex+1).executeAction();
                }
            }
        });

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(getColor());
        colorProperty.addListener((observableValue, colorOld, colorNew) -> colorPicker.setValue(colorNew));
        colorPicker.setOnAction(actionEvent -> {
            new ChangeColorAction(this.treeActionListener, this.getNodeId(), colorPicker.getValue()).executeAction();
            contextMenu.setAutoHide(true);
        });
        MenuItem setColorMenuItem = new MenuItem(null, colorPicker);
        colorPicker.setOnShown(event -> contextMenu.setAutoHide(false));
        colorPicker.setOnHidden(event -> contextMenu.setAutoHide(true));

        Menu changeTypeSubmenu = new Menu(null, new Label("Change Type"));
        Arrays.stream(NodeType.values()).filter(nt -> !nt.equals(getType())).forEach(nodeType -> {
            ImageView menuImg = new ImageView(ImageHelper.createImage(nodeType.getImgPath(), SIZE_IMAGE, SIZE_IMAGE));
            MenuItem nodeTypeMenuItem = new MenuItem(nodeType.getLabel(), menuImg);
            nodeTypeMenuItem.setOnAction(actionEvent ->
                    new ChangeTypeAction(this.treeActionListener, this.getNodeId(),nodeType).executeAction());
            changeTypeSubmenu.getItems().add(nodeTypeMenuItem);
        });
        contextMenu.getItems().addAll(addNewMenuItem, removeMenuItem, moveUpMenuItem, moveDownMenuItem,
                setColorMenuItem, changeTypeSubmenu);
        nodeRectContentGroup.setOnContextMenuRequested(contextMenuEvent -> {
            contextMenu.show(nodeRect, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
        });

    }


    private void setupDragAndDropForMove() {
        this.nodeRectContentGroup.setOnDragDetected(mouseEvent -> {
            Dragboard db = this.nodeRectContentGroup.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(DRAG_DROP_CTX_PFX + this.getNodeId());
            db.setContent(content);
            mouseEvent.consume();
        } );

        this.nodeRectContentGroup.setOnDragOver(dragEvent -> {
            if(validateDragEvent(dragEvent)) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
                dragEvent.consume();
            }
        });

        this.nodeRectContentGroup.setOnDragEntered(dragEvent -> {
            if(validateDragEvent(dragEvent)) {
                this.nodeRect.fillProperty().unbind();
                this.nodeRect.setFill(((Color)this.nodeRect.getFill()).invert());
                dragEvent.consume();
            }
        });

        this.nodeRectContentGroup.setOnDragExited(dragEvent -> {
            if(validateDragEvent(dragEvent)) {
                this.nodeRect.fillProperty().bind(this.colorProperty);
                dragEvent.consume();
            }
        });

        this.nodeRectContentGroup.setOnDragDropped(dragEvent -> {
            if(validateDragEvent(dragEvent)) {
                String sourceNodeId = getNodeIdFromDragContext(dragEvent.getDragboard().getString());
                new MoveNodeAction(this.treeActionListener, sourceNodeId, this.getNodeId()).executeAction();
                dragEvent.consume();
            }
        });
        this.nodeRectContentGroup.setOnDragDone(dragEvent -> dragEvent.consume());
    }


    private boolean validateDragEvent(DragEvent dragEvent) {
        boolean valid = false;
        if(dragEvent.getGestureSource() != this.nodeRectContentGroup  &&
                dragEvent.getDragboard().getString().startsWith(DRAG_DROP_CTX_PFX)) {
            String sourceNodeIdStr = getNodeIdFromDragContext(dragEvent.getDragboard().getString());
            valid = isValidDropTarget(sourceNodeIdStr);

        }
        return valid;
    }

    private boolean isValidDropTarget(String sourceNodeIdStr) {
        //SourceNode can not be one of parents of current node to be accepted
        //SourceNode can not be one of children of current node to be accepted (move to parent does not make sense)
        return !isOneOfParents(sourceNodeIdStr) && !isChildNodeId(sourceNodeIdStr);
    }

    private boolean isOneOfParents(String nodeIdStr) {
        if(this.getParentNode()!=null) {
            return this.getParentNode().getNodeId().equals(nodeIdStr) || this.getParentNode().isOneOfParents(nodeIdStr);
        }
        return false;
    }

    private boolean isChildNodeId(String nodeIdStr) {
        boolean found = false;
        if(this.getChildList() != null && !this.getChildList().isEmpty()) {
            found = this.getChildList().stream().anyMatch(ch -> ch.getNodeId().equals(nodeIdStr));
        }
        return found;
    }

    private String getNodeIdFromDragContext(String dragCtxStr) {
        String ret = null;
        if(dragCtxStr.startsWith(DRAG_DROP_CTX_PFX)) {
            ret = dragCtxStr.substring(DRAG_DROP_CTX_PFX.length());
        }
        return ret;
    }

    private void expandChildElements() {
        List<TreeNode> childList = childListProperty().get();
        if (childList.isEmpty()) {
            return;
        }
        widthBorderProperty.set(2 * INIT_WIDTH_BORDER);
        recalculateHeight();
        childList.stream().forEach(ch ->
        {
            ch.setVisible(true);
            changeLinesVisibility(ch, true);
        });

    }

    private void closeChildElements() {
        List<TreeNode> childList = childListProperty().get();
        childList.stream().forEach(
                ch -> {
                    ch.setExpandState(false);
                    ch.setVisible(false);
                    changeLinesVisibility(ch, false);
                });
        widthBorderProperty.set(INIT_WIDTH_BORDER);
        heightBorderProperty.set(INIT_HEIGHT_BORDER);

    }

    private void changeLinesVisibility(TreeNode ch, boolean visible) {
        if(childLinesMap.containsKey(ch)) {
            childLinesMap.get(ch).setVisible(visible);
        }
    }


    public TreeNode addChild() {
        setExpandState(true);
        TreeNode prevNode =  childListProperty.isEmpty() ? null : getChildList().get(getChildList().size()-1);
        TreeNode childNode = new TreeNode(this, prevNode, null);
        if(prevNode != null) {
            prevNode.setNextNode(childNode);
        }
        childListProperty.add(childNode);
        recalculateHeight();
        Line childConnectLine1 = new Line();
        childConnectLine1.startXProperty().bind(connectPointOutXProperty);
        childConnectLine1.startYProperty().bind(connectPointOutYProperty);
        childConnectLine1.endXProperty().bind(connectPointOutXProperty.add((INIT_WIDTH_BORDER-WIDTH_RECT)/2));
        childConnectLine1.endYProperty().bind(connectPointOutYProperty);

        Line childConnectLine2 = new Line();
        childConnectLine2.startXProperty().bind(childConnectLine1.endXProperty());
        childConnectLine2.startYProperty().bind(childConnectLine1.endYProperty());
        childConnectLine2.endXProperty().bind(childConnectLine1.endXProperty());
        childConnectLine2.endYProperty().bind(childNode.connectPointInYProperty);

        Line childConnectLine3 = new Line();
        childConnectLine3.startXProperty().bind(childConnectLine2.endXProperty());
        childConnectLine3.startYProperty().bind(childConnectLine2.endYProperty());
        childConnectLine3.endXProperty().bind(childNode.connectPointInXProperty);
        childConnectLine3.endYProperty().bind(childNode.connectPointInYProperty);

        Group childLinesGroup = new Group(childConnectLine1, childConnectLine2, childConnectLine3);
        this.childLinesMap.put(childNode, childLinesGroup);
        getChildren().add(childLinesGroup);

        //Wenn sich die Höhe des NodeRegions im child ändert, muss die Höhe des Parents (aktuellen nodes) etsprechend angepasst werden.
        childNode.heightBorderProperty.addListener((observable, oldVal, newVal) -> {
            recalculateHeight();
        });
        return childNode;
    }



    public void removeChild(String nodeId) {
        Optional<TreeNode> nodeOpt =
                getChildList().stream().filter(nc -> nc.getNodeId().equals(nodeId)).findFirst();
        nodeOpt.ifPresent(node ->
        {
           removeChild(node);
        });
        recalculateHeight();
    }

    private void removeChild(TreeNode childNode) {
        childNode.closeChildElements();
        childNode.setVisible(false);
        if(childLinesMap.containsKey(childNode)) {
            getChildren().remove(this.childLinesMap.get(childNode));
            childLinesMap.remove(childNode);
        }
        if(childNode.getPreviousNode()!=null) {
            childNode.getPreviousNode().setNextNode(childNode.getNextNode());
        }
        if(childNode.getNextNode() != null) {
            childNode.getNextNode().setPreviousNode(childNode.getPreviousNode());
        }
        getChildList().remove(childNode);
        getChildren().remove(childNode);
    }

    public void moveChildPosition(int oldIndex, int newIndex) {
        if(oldIndex == newIndex) {
            return;
        }

        TreeNode childtoMove = getChildList().get(oldIndex);
        if(childtoMove.getNextNode()!=null) { // node has a next node
            if (childtoMove.getPreviousNode() != null) { // node has an previous and a next node. Links must be changed
                childtoMove.getPreviousNode().setNextNode(childtoMove.getNextNode());
                childtoMove.getNextNode().setPreviousNode(childtoMove.getPreviousNode());
            } else { // node is at the begining of the list. Next node does not have any previous node
                childtoMove.getNextNode().setPreviousNode(null);
            }
        } else if(childtoMove.getPreviousNode()!=null) { // node is at the end of list. Previous doesn't have any next node
            childtoMove.getPreviousNode().setNextNode(null);
        }

        getChildList().add(newIndex, childtoMove);

        if(newIndex > oldIndex) { // move to "right"
            getChildList().get(newIndex-1).setNextNode(childtoMove);
            childtoMove.setPreviousNode(getChildList().get(newIndex-1));
            if(getChildList().size()>=newIndex) { //there is a new nextNode
                childtoMove.setNextNode(getChildList().get(newIndex+1));
                getChildList().get(newIndex+1).setPreviousNode(childtoMove);
            } else { // new Positon is last element in the list
                childtoMove.setNextNode(null);
            }
        } else {   // move to "left"

       childtoMove.setNextNode(getChildList().get(newIndex+1));

            if(newIndex>0) { // there is a new previous node
                childtoMove.setPreviousNode(getChildList().get(newIndex-1));
                getChildList().get(newIndex-1).setNextNode(childtoMove);
            }  else { // new position is 1st element in the list
                childtoMove.setPreviousNode(null);
            }

            getChildList().get(newIndex+1).setPreviousNode(childtoMove);
        }
        getChildList().remove(newIndex > oldIndex ? oldIndex : oldIndex + 1);
        recalculateHeight();
    }




    void recalculateHeight() {
        Double newHeight = getChildList().stream().mapToDouble(tn -> tn.getHeightBorder()).sum();
        heightBorderProperty.set(newHeight >= INIT_HEIGHT_BORDER ? newHeight : INIT_HEIGHT_BORDER);
        if(getParentNode() != null) {
            getParentNode().recalculateHeight();
        }
    }

    public TreeNode getTreeNodeByRect(Rectangle rect) {
        TreeNode ret = null;
        if (this.nodeRect.equals(rect)) {
            ret = this;
        } else if (!getChildList().isEmpty()) {
            for (TreeNode tn: getChildList()) {
                ret = tn.getTreeNodeByRect(rect);
                if(ret != null){
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public boolean requestSelection(boolean select) {
        return true;
    }

    @Override
    public void notifySelection(boolean select) {
        if(select){
            this.nodeRect.setStroke(((Color)this.nodeRect.getFill()).invert());
            this.nodeRect.getStrokeDashArray().addAll(5.0d, 5.0d);
            this.rectShadow.setColor(this.rectShadow.getColor().invert());
            this.treeActionListener.nodeSelected(getNodeId());

        } else {
            this.nodeRect.setStroke(Color.TRANSPARENT);
            this.nodeRect.getStrokeDashArray().clear();
            this.nodeRect.setStrokeType(StrokeType.CENTERED);
            this.rectShadow.setColor(Color.BLACK);

        }
    }
}
