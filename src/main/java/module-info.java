module com.example.teoriadosgrafosfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.teoriadosgrafosfx to javafx.fxml;
    exports com.example.teoriadosgrafosfx;
}