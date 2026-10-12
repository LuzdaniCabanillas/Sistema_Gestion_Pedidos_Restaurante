package vista;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
//quitar -sergio
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

// AGREGA -SERGIO
import controlador.DetallePedidoDAO;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import modelo.DetallePedido;

public class FrmCuenta extends BorderPane {

    private final CuentaService cuentaService;

    private final TableView<Cuenta> tablaCuentas;

    private final TextField txtSubtotal;
    private final TextField txtIGV;
    private final TextField txtTotal;
    private final DetallePedidoDAO detallePedidoDAO; //AGREGADO-SERGIO

//quitar -sergio
    public FrmCuenta() {
        cuentaService = new CuentaService();
        detallePedidoDAO = new DetallePedidoDAO(); //AGREGADO-SERGIO

        tablaCuentas = new TableView<>();
        txtSubtotal = new TextField();
        txtIGV = new TextField();
        txtTotal = new TextField();

        //quitar-sergio
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

        //quitar - sergio
        Button btnVerDetalle = new Button("VER DETALLE");
        Button btnImprimir = new Button("IMPRIMIR CUENTA");
        Button btnRegistrarPago = new Button("REGISTRAR PAGO");

        btnVerDetalle.setOnAction(e -> verDetalleCuenta());
        btnImprimir.setOnAction(e -> imprimirCuenta());
        btnRegistrarPago.setOnAction(e -> abrirRegistroPago());

        HBox panelAcciones = new HBox(
                10,
                btnVerDetalle,
                btnImprimir,
                btnRegistrarPago
        );

        panelAcciones.setPadding(new Insets(10));
        
        VBox panelCuentas = new VBox(
                10,
                encabezadoCuentas,
                tablaCuentas,
                importes,
                panelAcciones
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

    //ELIMIAR -SERGIO
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

    private void verDetalleCuenta() {
    Cuenta cuenta = tablaCuentas.getSelectionModel()
            .getSelectedItem();

    if (cuenta == null) {
        mostrarAdvertencia("Seleccione una cuenta para ver su detalle.");
        return;
    }

    try {
        List<DetallePedido> detalles =
                detallePedidoDAO.listarPorPedido(cuenta.getPedidoId());

        StringBuilder contenido = new StringBuilder();

        contenido.append("DETALLE DE CUENTA\n");
        contenido.append("==============================\n");
        contenido.append("Cuenta: ").append(cuenta.getId()).append("\n");
        contenido.append("Pedido: ").append(cuenta.getPedidoId()).append("\n");
        contenido.append("Estado: ").append(cuenta.getEstado()).append("\n\n");

        contenido.append(String.format(
                "%-22s %5s %10s %10s%n",
                "PLATO", "CANT.", "PRECIO", "IMPORTE"
        ));

        contenido.append("-----------------------------------------------\n");

        for (DetallePedido detalle : detalles) {
            String nombre = detalle.getPlato().getNombre();

            contenido.append(String.format(
                    "%-22s %5d %10.2f %10.2f%n",
                    nombre.length() > 22
                            ? nombre.substring(0, 22)
                            : nombre,
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    detalle.getSubTotal()
            ));
        }

        contenido.append("\n");
        contenido.append("Subtotal: S/ ")
                .append(cuenta.getSubTotal()).append("\n");
        contenido.append("IGV (18%): S/ ")
                .append(cuenta.getIGV()).append("\n");
        contenido.append("TOTAL: S/ ")
                .append(cuenta.getTotal()).append("\n");

        TextArea area = new TextArea(contenido.toString());
        area.setEditable(false);
        area.setWrapText(false);
        area.setPrefColumnCount(65);
        area.setPrefRowCount(20);

        Alert dialogo = new Alert(Alert.AlertType.INFORMATION);
        dialogo.setTitle("Detalle de cuenta");
        dialogo.setHeaderText("Cuenta N.º " + cuenta.getId());
        dialogo.getDialogPane().setContent(area);
        dialogo.getDialogPane().setPrefWidth(650);
        dialogo.showAndWait();

    } catch (SQLException ex) {
        mostrarError(
                "No se pudo consultar el detalle de la cuenta: "
                + ex.getMessage()
        );
    }
}

    private void imprimirCuenta() {
    Cuenta cuenta = tablaCuentas.getSelectionModel()
            .getSelectedItem();

    if (cuenta == null) {
        mostrarAdvertencia("Seleccione una cuenta para imprimir.");
        return;
    }

    try {
        List<DetallePedido> detalles =
                detallePedidoDAO.listarPorPedido(cuenta.getPedidoId());

        VBox ticket = new VBox(8);
        ticket.setPadding(new Insets(20));
        ticket.setPrefWidth(350);

        ticket.getChildren().addAll(
                new javafx.scene.control.Label(
                        "SISTEMA DE GESTIÓN DE RESTAURANTE"
                ),
                new javafx.scene.control.Label(
                        "COMPROBANTE DE CUENTA"
                ),
                new javafx.scene.control.Label(
                        "Cuenta: " + cuenta.getId()
                ),
                new javafx.scene.control.Label(
                        "Pedido: " + cuenta.getPedidoId()
                ),
                new javafx.scene.control.Label(
                        "Estado: " + cuenta.getEstado()
                ),
                new javafx.scene.control.Label(
                        "--------------------------------"
                )
        );

        for (DetallePedido detalle : detalles) {
            String linea = detalle.getPlato().getNombre()
                    + " x " + detalle.getCantidad()
                    + " = S/ "
                    + String.format("%.2f", detalle.getSubTotal());

            ticket.getChildren().add(
                    new javafx.scene.control.Label(linea)
            );
        }

        ticket.getChildren().addAll(
                new javafx.scene.control.Label(
                        "--------------------------------"
                ),
                new javafx.scene.control.Label(
                        "Subtotal: S/ " + cuenta.getSubTotal()
                ),
                new javafx.scene.control.Label(
                        "IGV: S/ " + cuenta.getIGV()
                ),
                new javafx.scene.control.Label(
                        "TOTAL: S/ " + cuenta.getTotal()
                ),
                new javafx.scene.control.Label(
                        "Documento informativo. No acredita pago."
                )
        );

        PrinterJob trabajo = PrinterJob.createPrinterJob();

        if (trabajo == null) {
            mostrarError("No hay una impresora disponible.");
            return;
        }

        Stage ventana = new Stage();

        boolean aceptarImpresion =
                trabajo.showPrintDialog(ventana);

        if (aceptarImpresion) {
            boolean impreso = trabajo.printPage(ticket);

            if (impreso) {
                trabajo.endJob();
                mostrarInformacion(
                        "Se envió la cuenta a la impresora."
                );
            } else {
                trabajo.cancelJob();
                mostrarError("No se pudo imprimir la cuenta.");
            }
        } else {
            trabajo.cancelJob();
        }

    } catch (SQLException ex) {
        mostrarError(
                "No se pudo preparar la cuenta para imprimir: "
                + ex.getMessage()
        );
    }
}

    // MODIFICADO-SEGIO
    
private void abrirRegistroPago() {
    Cuenta cuentaSeleccionada =
            tablaCuentas.getSelectionModel().getSelectedItem();

    if (cuentaSeleccionada == null) {
        mostrarAdvertencia(
            "Selecciona una cuenta para registrar el pago."
        );
        return;
    }

    if (!"PENDIENTE".equalsIgnoreCase(
            cuentaSeleccionada.getEstado())) {
        mostrarAdvertencia(
            "Solo puedes registrar el pago de una cuenta pendiente."
        );
        return;
    }

    FrmRegistroPago formulario =
            new FrmRegistroPago(cuentaSeleccionada);

    Stage ventana = new Stage();
    ventana.setTitle("Registro de Pago");
    ventana.setScene(new Scene(formulario, 620, 500));
    ventana.showAndWait();

    cargarDatos();
}


}
