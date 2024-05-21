module org.example.demojavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires org.testng;
    requires java.desktop;
    requires guava;


    opens org.example.demojavafx to javafx.fxml;
    exports org.example.demojavafx;
    exports org.example.demojavafx.CardsGeneration;
    opens org.example.demojavafx.CardsGeneration to javafx.fxml;
}