module org.example.mini_project2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.mini_project2 to javafx.fxml;
    exports org.example.mini_project2;
}