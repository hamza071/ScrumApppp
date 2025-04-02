module com.example.scrumapppp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.scrumapppp to javafx.fxml;
    exports com.example.scrumapppp.Application;
    opens com.example.scrumapppp.Application to javafx.fxml;
    exports com.example.scrumapppp.Controller;
    opens com.example.scrumapppp.Controller to javafx.fxml;
}