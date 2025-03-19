module com.example.scrumapppp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.scrumapppp to javafx.fxml;
    exports com.example.scrumapppp;
}