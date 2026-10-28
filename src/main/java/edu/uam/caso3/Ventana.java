package edu.uam.caso3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Ventana extends Application {
    private CajaController controlador;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Ventana.class.getResource("caja-ventas.fxml"));
        Parent vista = loader.load();
        controlador = loader.getController();
        controlador.configurarVentana(stage);

        stage.setScene(new Scene(vista));
        stage.setMinWidth(620);
        stage.setMinHeight(700);
        stage.setTitle("Caso 3 - Caja de ventas");
        stage.show();
    }

    @Override
    public void stop() {
        if (controlador != null) controlador.cerrar();
    }

}
