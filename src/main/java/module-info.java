module com.example.scrumapppp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Exporteer & open application
    exports com.example.scrumapppp.Application;
    opens com.example.scrumapppp.Application to javafx.fxml;

    // Exporteer & open controller
    exports com.example.scrumapppp.Controller;
    opens com.example.scrumapppp.Controller to javafx.fxml;

    // (optioneel) open het root package als je dat echt nodig hebt:
    opens com.example.scrumapppp to javafx.fxml;
}
