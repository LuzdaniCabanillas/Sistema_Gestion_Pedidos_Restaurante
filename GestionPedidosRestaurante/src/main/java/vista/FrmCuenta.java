package vista;

/**
 * LEAN
 */
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import modelo.Cuenta;
import service.CuentaService;

public class FrmCuenta extends BorderPane {

    private final CuentaService cuentaService;

    private final TableView<Cuenta> tablaCuentas;

    private final TextField txtSubtotal;
    private final TextField txtIGV;
    private final TextField txtTotal;

    private final ComboBox<String> cboMetodoPago;

    public FrmCuenta() {
        cuentaService = new CuentaService();

        tablaCuentas = new TableView<>();

        txtSubtotal = new TextField();
        txtIGV = new TextField();
        txtTotal = new TextField();

        cboMetodoPago = new ComboBox<>();

        configurarFormulario();
        configurarTablas();
        cargarDatos();
    }

    private void configurarFormulario() {
        setPadding(new Insets(15));

        Label titulo = new Label("GESTIÓN DE CUENTAS");
        titulo.setStyle(
                "-fx-font-size: 22px; -fx-font-weight: bold;"
        );

        Label lblCuentas = new Label("Cuentas registradas");
        lblCuentas.setStyle("-fx-font-weight: bold;");

        Button btnActualizar = new Button("Actualizar");
        btnActualizar.setOnAction(e -> cargarDatos());

        HBox encabezadoCuentas = new HBox(
                15, lblCuentas, btnActualizar
        );

        txtSubtotal.setEditable(false);
        txtIGV.setEditable(false);
        txtTotal.setEditable(false);

        GridPane importes = new GridPane();
        importes.setHgap(10);
        importes.setVgap(8);

        importes.add(new Label("Subtotal:"), 0, 0);
        importes.add(txtSubtotal, 1, 0);

        importes.add(new Label("IGV (18 %):"), 0, 1);
        importes.add(txtIGV, 1, 1);

        importes.add(new Label("Total:"), 0, 2);
        importes.add(txtTotal, 1, 2);

        cboMetodoPago.setItems(
                FXCollections.observableArrayList(
                        "EFECTIVO", "TARJETA"
                )
        );

        cboMetodoPago.setPromptText("Seleccione método");

        Button btnPagar = new Button("Registrar pago");
        btnPagar.setOnAction(e -> registrarPago());

        HBox panelPago = new HBox(
                10,
                new Label("Método de pago:"),
                cboMetodoPago,
                btnPagar
        );

        VBox panelCuentas = new VBox(
                10,
                encabezadoCuentas,
                tablaCuentas,
                importes,
                panelPago
        );

        VBox contenido = new VBox(
                18,
                titulo,
                panelCuentas
        );

        contenido.setPadding(new Insets(10));

        setCenter(contenido);
    }

    private void configurarTablas() {

        TableColumn<Cuenta, Integer> colCuentaId
                = new TableColumn<>("ID cuenta");

        colCuentaId.setCellValueFactory(
                celda -> new SimpleObjectProperty<>(
                        celda.getValue().getId()
                )
        );

        TableColumn<Cuenta, Integer> colPedidoCuenta
                = new TableColumn<>("ID pedido");

        colPedidoCuenta.setCellValueFactory(
                celda -> new SimpleObjectProperty<>(
                        celda.getValue().getPedidoId()
                )
        );

        TableColumn<Cuenta, BigDecimal> colSubtotal
                = new TableColumn<>("Subtotal");

        colSubtotal.setCellValueFactory(
                celda -> new SimpleObjectProperty<>(
                        celda.getValue().getSubTotal()
                )
        );

        TableColumn<Cuenta, BigDecimal> colIGV
                = new TableColumn<>("IGV");

        colIGV.setCellValueFactory(
                celda -> new SimpleObjectProperty<>(
                        celda.getValue().getIGV()
                )
        );

        TableColumn<Cuenta, BigDecimal> colTotal
                = new TableColumn<>("Total");

        colTotal.setCellValueFactory(
                celda -> new SimpleObjectProperty<>(
                        celda.getValue().getTotal()
                )
        );

        TableColumn<Cuenta, String> colEstado
                = new TableColumn<>("Estado");

        colEstado.setCellValueFactory(
                celda -> new SimpleStringProperty(
                        celda.getValue().getEstado()
                )
        );

        TableColumn<Cuenta, String> colMetodo
                = new TableColumn<>("Método de pago");

        colMetodo.setCellValueFactory(
                celda -> new SimpleStringProperty(
                        celda.getValue().getMetodoPago() == null
                        ? "-"
                        : celda.getValue().getMetodoPago()
                )
        );

        tablaCuentas.getColumns().addAll(
                colCuentaId,
                colPedidoCuenta,
                colSubtotal,
                colIGV,
                colTotal,
                colEstado,
                colMetodo
        );

        tablaCuentas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tablaCuentas.setPrefHeight(300);

        tablaCuentas.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, cuenta) -> {
                    if (cuenta != null) {
                        mostrarImportes(cuenta);
                    } else {
                        limpiarImportes();
                    }
                });
    }

    private void cargarDatos() {
        try {
            List<Cuenta> cuentas
                    = cuentaService.listarCuentas();

            tablaCuentas.setItems(
                    FXCollections.observableArrayList(cuentas)
            );

            limpiarImportes();

        } catch (SQLException ex) {
            mostrarError(
                    "No se pudieron cargar las cuentas: "
                    + ex.getMessage()
            );
        }
    }

    private void registrarPago() {
        Cuenta cuenta = tablaCuentas.getSelectionModel()
                .getSelectedItem();

        if (cuenta == null) {
            mostrarAdvertencia(
                    "Seleccione una cuenta."
            );
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(
                cuenta.getEstado())) {
            mostrarAdvertencia(
                    "Solo se puede pagar una cuenta pendiente."
            );
            return;
        }

        String metodo = cboMetodoPago.getValue();

        if (metodo == null) {
            mostrarAdvertencia(
                    "Seleccione Efectivo o Tarjeta."
            );
            return;
        }

        try {
            cuentaService.registrarPago(
                    cuenta.getId(), metodo
            );

            mostrarInformacion(
                    "El pago se registró correctamente."
            );

            cboMetodoPago.getSelectionModel().clearSelection();

            cargarDatos();

        } catch (SQLException | IllegalStateException
                | IllegalArgumentException ex) {
            mostrarError(
                    "No se pudo registrar el pago: "
                    + ex.getMessage()
            );
        }
    }

    private void mostrarImportes(Cuenta cuenta) {
        txtSubtotal.setText(
                cuenta.getSubTotal().toPlainString()
        );

        txtIGV.setText(
                cuenta.getIGV().toPlainString()
        );

        txtTotal.setText(
                cuenta.getTotal().toPlainString()
        );
    }

    private void limpiarImportes() {
        txtSubtotal.clear();
        txtIGV.clear();
        txtTotal.clear();
    }

    private void mostrarInformacion(String mensaje) {
        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle("Gestión de cuentas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alerta = new Alert(
                Alert.AlertType.WARNING
        );

        alerta.setTitle("Gestión de cuentas");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(
                Alert.AlertType.ERROR
        );

        alerta.setTitle("Gestión de cuentas");
        alerta.setHeaderText("Ocurrió un problema");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
