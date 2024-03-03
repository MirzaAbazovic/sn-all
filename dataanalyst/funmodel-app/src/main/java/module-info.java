module funmodel.app {
    exports com.bitconex.danalyst.funmodmap.gui;
    exports com.bitconex.danalyst.funmodmap.adapter.jsonfile;
    requires transitive javafx.fxml;
    requires transitive javafx.base;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires transitive javafx.media;
    requires com.fasterxml.jackson.databind;

}