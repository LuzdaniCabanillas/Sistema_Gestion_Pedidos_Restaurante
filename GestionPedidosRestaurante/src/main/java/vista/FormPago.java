
package vista;

import controlador.CuentaDAO;
import controlador.PagoDAO;

import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import modelo.Cuenta;
import modelo.EstadoCuenta;
import modelo.MetodoPago;
import modelo.Pago;

import service.PagoService;



public class FormPago extends Application {
    
     private final PagoService pagoService =
            new PagoService();

    private final CuentaDAO cuentaDAO =
            new CuentaDAO();

    private final PagoDAO pagoDAO =
            new PagoDAO();


    private final ComboBox<Cuenta> cboCuenta =
            new ComboBox<>();

    private final TextField txtMonto =
            new TextField();

    private final ComboBox<MetodoPago> cboMetodo =
            new ComboBox<>();


    private final Button btnPagar =
            new Button("Registrar Pago");

    private final Button btnLimpiar =
            new Button("Limpiar");


    private final TableView<Pago> tabla =
            new TableView<>();


    @Override
    public void start(Stage stage) {

        stage.setTitle("Gestión de Pagos");

        // El monto no se escribe
        txtMonto.setEditable(false);

        // Métodos de pago
        cboMetodo.getItems().addAll(
                MetodoPago.values()
        );


        // Mostrar correctamente las cuentas
        cboCuenta.setConverter(
                new StringConverter<Cuenta>() {

            @Override
            public String toString(Cuenta cuenta) {

                if (cuenta == null) {
                    return "";
                }

                return "Cuenta #"
                        + cuenta.getId()
                        + " - S/ "
                        + String.format(
                                "%.2f",
                                cuenta.getTotal()
                        );
            }

            @Override
            public Cuenta fromString(String texto) {
                return null;
            }
        });


        // Al seleccionar cuenta mostrar total
        cboCuenta.setOnAction(e -> {

            Cuenta cuenta =
                    cboCuenta.getValue();

            if (cuenta != null) {

                txtMonto.setText(
                        String.format(
                                "%.2f",
                                cuenta.getTotal()
                        )
                );
            }
        });


        // FORMULARIO
        GridPane formulario =
                new GridPane();

        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(
                new Insets(15)
        );


        formulario.add(
                new Label("Cuenta:"),
                0, 0
        );

        formulario.add(
                cboCuenta,
                1, 0
        );


        formulario.add(
                new Label("Monto:"),
                0, 1
        );

        formulario.add(
                txtMonto,
                1, 1
        );


        formulario.add(
                new Label("Método:"),
                0, 2
        );

        formulario.add(
                cboMetodo,
                1, 2
        );


        // BOTONES
        HBox botones =
                new HBox(
                        10,
                        btnPagar,
                        btnLimpiar
                );

        botones.setAlignment(
                Pos.CENTER
        );


        // TABLA

        TableColumn<Pago, String> colId =
                new TableColumn<>("ID");

        colId.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.valueOf(
                                        dato.getValue()
                                                .getId()
                                )
                        )
        );


        TableColumn<Pago, String> colCuenta =
                new TableColumn<>("Cuenta");

        colCuenta.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.valueOf(
                                        dato.getValue()
                                                .getCuentaId()
                                )
                        )
        );


        TableColumn<Pago, String> colFecha =
                new TableColumn<>("Fecha");

        colFecha.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.valueOf(
                                        dato.getValue()
                                                .getFechaHora()
                                )
                        )
        );


        TableColumn<Pago, String> colMetodo =
                new TableColumn<>("Método");

        colMetodo.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.valueOf(
                                        dato.getValue()
                                                .getMetodoPago()
                                )
                        )
        );


        TableColumn<Pago, String> colMonto =
                new TableColumn<>("Monto");

        colMonto.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.format(
                                        "%.2f",
                                        dato.getValue()
                                                .getMonto()
                                )
                        )
        );


        tabla.getColumns().addAll(
                colId,
                colCuenta,
                colFecha,
                colMetodo,
                colMonto
        );

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );


        // EVENTOS
        btnPagar.setOnAction(
                e -> registrarPago()
        );

        btnLimpiar.setOnAction(
                e -> limpiar()
        );


        // TÍTULO
        Label titulo =
                new Label(
                        "GESTIÓN DE PAGOS"
                );

        titulo.setStyle(
                "-fx-font-size: 22px;"
                + "-fx-font-weight: bold;"
        );


        VBox superior =
                new VBox(
                        10,
                        titulo,
                        formulario,
                        botones
                );

        superior.setAlignment(
                Pos.CENTER
        );

        superior.setPadding(
                new Insets(10)
        );


        BorderPane root =
                new BorderPane();

        root.setTop(superior);
        root.setCenter(tabla);

        BorderPane.setMargin(
                tabla,
                new Insets(10)
        );


        cargarCuentasPendientes();
        cargarPagos();


        Scene scene =
                new Scene(
                        root,
                        800,
                        500
                );

        stage.setScene(scene);
        stage.show();
    }


    // CARGAR CUENTAS PENDIENTES
    private void cargarCuentasPendientes() {

        cboCuenta.getItems().clear();

        List<Cuenta> cuentas =
                cuentaDAO.listar();

        for (Cuenta cuenta : cuentas) {

            if (cuenta.getEstado()
                    == EstadoCuenta.PENDIENTE) {

                cboCuenta.getItems()
                        .add(cuenta);
            }
        }
    }


    // CARGAR PAGOS
    private void cargarPagos() {

        tabla.getItems().clear();

        tabla.getItems().addAll(
                pagoDAO.listar()
        );
    }


    // REGISTRAR
    private void registrarPago() {

        Cuenta cuenta =
                cboCuenta.getValue();

        if (cuenta == null) {

            mensaje(
                    "Seleccione una cuenta.",
                    Alert.AlertType.WARNING
            );

            return;
        }


        MetodoPago metodo =
                cboMetodo.getValue();

        if (metodo == null) {

            mensaje(
                    "Seleccione un método de pago.",
                    Alert.AlertType.WARNING
            );

            return;
        }


        boolean resultado =
                pagoService.registrarPago(
                        cuenta.getId(),
                        metodo
                );


        if (resultado) {

            mensaje(
                    "Pago registrado correctamente.",
                    Alert.AlertType.INFORMATION
            );

            cargarPagos();

            // La cuenta pagada desaparece
            // de las cuentas pendientes
            cargarCuentasPendientes();

            limpiar();

        } else {

            mensaje(
                    "No se pudo registrar el pago.",
                    Alert.AlertType.ERROR
            );
        }
    }


    private void limpiar() {

        cboCuenta.setValue(null);
        txtMonto.clear();
        cboMetodo.setValue(null);
    }


    private void mensaje(
            String texto,
            Alert.AlertType tipo) {

        Alert alerta =
                new Alert(tipo);

        alerta.setTitle(
                "Sistema Restaurante"
        );

        alerta.setHeaderText(null);
        alerta.setContentText(texto);

        alerta.showAndWait();
    }


    public static void main(String[] args) {

        Application.launch(
                FormPago.class,
                args
        );
    }
    
}
