package com.bitconex.danalyst.funmodmap.gui;

import com.bitconex.danalyst.funmodmap.boundary.FuncModelBoundary;
import com.bitconex.danalyst.funmodmap.boundary.FuncModelBoundaryImpl;
import com.bitconex.danalyst.funmodmap.model.FuncModelNode;
import com.bitconex.danalyst.funmodmap.model.FuncModelRoot;
import com.bitconex.danalyst.funmodmap.model.FuncModelTree;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


//@SpringBootApplication
public class FunModelMain extends Application {

    private final static String FUN_MODEL_FILE_EXTENSION = "fmd";
    //private ConfigurableApplicationContext springContext;

    private MenuBar menuBar;
    private MultiEditorContainer multiEditorContainer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{


        menuBar = new MenuBar();
        VBox vbox = new VBox(menuBar);
        multiEditorContainer = new MultiEditorContainer();
        vbox.getChildren().add(multiEditorContainer);
        Scene scene = new Scene(vbox, 1000, 1000);
        multiEditorContainer.prefHeightProperty().bind(scene.heightProperty().subtract(menuBar.heightProperty().add(20)));
        initMenuBar(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void initMenuBar(Stage primaryStage) {
        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        newFile.setOnAction(actionEvent -> {
            FuncModelTree newTree = FuncModelBoundaryImpl.getInstance().createNewTree();
            newTree.setName("NewTree");
            multiEditorContainer.openNewEditor(newTree);
        });
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        MenuItem saveFile = new MenuItem("Save");

        saveFile.setOnAction(actionEvent -> {
            multiEditorContainer.getActiveEditor().saveTree();
        });
        saveFile.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        MenuItem exportCSV = new MenuItem("Export CSV");
        exportCSV.setOnAction(actionEvent -> {
            multiEditorContainer.getActiveEditor().exportTreeToCSV();
        });
        exportCSV.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));

        MenuItem loadFile = new MenuItem("Open");
        loadFile.setOnAction(actionEvent -> {
           multiEditorContainer.loadFile();
        });
        fileMenu.getItems().add(newFile);
        fileMenu.getItems().add(loadFile);
        fileMenu.getItems().add(saveFile);
        fileMenu.getItems().add(exportCSV);
        menuBar.getMenus().add(fileMenu);

    }


    public void init() throws  Exception{
        String[] args = getParameters().getRaw().toArray(new String[0]);

       /* this.springContext = new SpringApplicationBuilder()
                .sources(FunModelMain.class)
                .run(args);

        */
    }
    public void stop() throws Exception{
        //springContext.close();
        Platform.exit();
    }

    private FuncModelTree initTestTree(String treeName) {
        FuncModelBoundary funcModelBoundary = FuncModelBoundaryImpl.getInstance();
        funcModelBoundary.createNewTree();
        FuncModelTree testTree = funcModelBoundary.createNewTree();
        testTree.setName(treeName);

        FuncModelRoot rootNode = testTree.getRootNode();

        FuncModelNode node_1_1 = testTree.addNode(rootNode.getId());
        node_1_1.setName("1.1");

        FuncModelNode node_1_2 = testTree.addNode(rootNode.getId());
        node_1_2.setName("1.2");

        FuncModelNode node_1_3= testTree.addNode(rootNode.getId());
        node_1_3.setName("1.3");

        FuncModelNode node_1_4= testTree.addNode(rootNode.getId());
        node_1_4.setName("1.4");

        FuncModelNode node_2_1 = testTree.addNode(node_1_2.getId());
        node_2_1.setName("2.1");

        FuncModelNode node_2_2 = testTree.addNode(node_1_2.getId());
        node_2_2.setName("2.2");

        FuncModelNode node_2_3= testTree.addNode(node_1_2.getId());
        node_2_3.setName("2.3");

        FuncModelNode node_2_2_1 = testTree.addNode(node_2_2.getId());
        node_2_2_1.setName("2.2.1");

        FuncModelNode node_2_2_2 = testTree.addNode(node_2_2.getId());
        node_2_2_2.setName("2.2.2");

        FuncModelNode node_2_2_3 = testTree.addNode(node_2_2.getId());
        node_2_2_3.setName("2.2.3");

        FuncModelNode node_3_1 = testTree.addNode(node_1_3.getId());
        node_3_1.setName("3.1");

        FuncModelNode node_4_1 = testTree.addNode(node_1_4.getId());
        node_4_1.setName("4.1");
        FuncModelNode node_4_2 = testTree.addNode(node_1_4.getId());
        node_4_2.setName("4.2");
        FuncModelNode node_4_3 = testTree.addNode(node_1_4.getId());
        node_4_3.setName("4.3");

        return testTree;
    }

}
