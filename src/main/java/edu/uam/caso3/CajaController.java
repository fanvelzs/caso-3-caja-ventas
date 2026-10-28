package edu.uam.caso3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CajaController {
    @FXML private Button iniciar, cancelar;
    @FXML private Label estado, total, dinero, avance;
    @FXML private Label vendedor1, vendedor2, vendedor3;
    @FXML private ProgressBar progreso, barra1, barra2, barra3;
    @FXML private TextArea registro;

    private Label[] vendedores;
    private ProgressBar[] barras;
    private final Object bloqueo = new Object();
    private int ventas = 0, recaudacion = 0;
    private final int[] ventasPorVendedor = new int[3];
    private Thread[] hilos;
    private boolean ejecutando = false;
    private boolean cerrando = false;
    private volatile boolean cancelado = false;
    private volatile String error = "";
    private Stage ventana;

    @FXML
    private void initialize() {
        vendedores = new Label[] {vendedor1, vendedor2, vendedor3};
        barras = new ProgressBar[] {barra1, barra2, barra3};
    }

    public void configurarVentana(Stage ventana) {
        this.ventana = ventana;
        ventana.setOnCloseRequest(e -> {
            if (ejecutando) {
                e.consume();
                cerrando = true;
                cancelar();
            }
        });
    }

    @FXML
    private void iniciar() {
        if (ejecutando || cerrando) return;
        ejecutando = true;
        cancelado = false;
        error = "";
        registro.clear();
        iniciar.setDisable(true);
        cancelar.setDisable(false);
        estado.setText("En ejecución");
        synchronized (bloqueo) {
            ventas = 0;
            recaudacion = 0;
            for (int i = 0; i < 3; i++) ventasPorVendedor[i] = 0;
            actualizar("");
        }

        hilos = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int numero = i;
            hilos[i] = new Thread(() -> trabajar(numero), "Vendedor-" + (i + 1));
        }
        for (Thread hilo : hilos) hilo.start();
        esperarFinalizacion();
    }

    private void trabajar(int numero) {
        try {
            for (int i = 0; i < 5; i++) {
                if (cancelado) break;
                Thread.sleep(400);
                synchronized (bloqueo) {
                    if (cancelado) break;
                    // La venta completa se registra con el mismo bloqueo.
                    ventas++;
                    recaudacion += 100;
                    ventasPorVendedor[numero]++;
                    actualizar("Venta " + ventas + ": vendedor " + (numero + 1) + ", C$100");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (RuntimeException e) {
            error = e.toString();
            cancelado = true;
            for (Thread hilo : hilos) hilo.interrupt();
        }
    }

    // Se llama dentro del bloqueo para copiar los datos de una misma operación.
    private void actualizar(String mensaje) {
        int v = ventas;
        int r = recaudacion;
        int[] porVendedor = ventasPorVendedor.clone();
        Platform.runLater(() -> {
            total.setText(String.valueOf(v));
            avance.setText(v + " / 15 ventas");
            dinero.setText(String.format(java.util.Locale.US, "C$%,d", r));
            progreso.setProgress(v / 15.0);
            for (int i = 0; i < 3; i++) {
                vendedores[i].setText("Vendedor " + (i + 1) + "   " + porVendedor[i] + " / 5");
                barras[i].setProgress(porVendedor[i] / 5.0);
            }
            if (!mensaje.isEmpty()) registro.appendText(mensaje + "\n");
        });
    }

    @FXML
    private void cancelar() {
        synchronized (bloqueo) {
            cancelado = true;
        }
        estado.setText("Cancelando");
        cancelar.setDisable(true);
        for (Thread hilo : hilos) hilo.interrupt();
    }

    private void esperarFinalizacion() {
        // join espera la salida real de los hilos, pero fuera del hilo JavaFX.
        new Thread(() -> {
            try {
                for (Thread hilo : hilos) hilo.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            Platform.runLater(() -> {
                ejecutando = false;
                iniciar.setDisable(false);
                cancelar.setDisable(true);
                if (!error.isEmpty()) {
                    estado.setText("Error");
                    registro.appendText(error + "\n");
                } else {
                    estado.setText(ventas == 15 ? "Completado" : "Cancelado");
                }
                if (cerrando) ventana.close();
            });
        }, "Esperar finalización").start();
    }

    public void cerrar() {
        cancelado = true;
        if (hilos != null) {
            for (Thread hilo : hilos) hilo.interrupt();
        }
    }

}
