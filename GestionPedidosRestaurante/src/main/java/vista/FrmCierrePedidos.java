package vista;

import controlador.CuentaDAO;
import controlador.PedidoDAO;
import java.sql.SQLException;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.Cuenta;
import modelo.Pedido;
import service.PedidoService;

public class FrmCierrePedidos extends BorderPane {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final PedidoService pedidoService = new PedidoService();

    private final TableView<PedidoCierreRow> tabla
            = new TableView<>();

    private final Label lblPedido = new Label("-");
    private final Label lblMesa = new Label("-");
    private final Label lblCuenta = new Label("-");
    private final Label lblMetodoPago = new Label("-");
    private final Label lblSubtotal = new Label("S/ 0.00");
    private final Label lblIgv = new Label("S/ 0.00");
    private final Label lblTotal = new Label("S/ 0.00");
    private final Label lblEstadoPedido = new Label("-");
    private final Label lblEstadoCuenta = new Label("-");

    private final Button btnCerrar
            = new Button("CERRAR PEDIDO");

    private final Button btnActualizar
            = new Button("ACTUALIZAR");

    public FrmCierrePedidos() {
        construirInterfaz();
        configurarEventos();
        cargarPedidos();
    }

    private void construirInterfaz() {

        setPadding(new Insets(25));
        setStyle("-fx-background-color: #F5F7F5;");

        // Título
        Label titulo = new Label("CIERRE DE PEDIDOS");
        titulo.setStyle(
                "-fx-font-size: 25px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #245B36;"
        );

        Label subtitulo = new Label(
                "Pedidos entregados con cuenta pagada"
        );

        subtitulo.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: #657268;"
        );

        VBox encabezado = new VBox(6, titulo, subtitulo);
        encabezado.setPadding(new Insets(0, 0, 20, 0));

        setTop(encabezado);

        // Tabla de pedidos
        configurarTabla();

        VBox panelTabla = new VBox(10);
        Label tituloTabla = new Label(
                "PEDIDOS DISPONIBLES PARA CERRAR"
        );

        tituloTabla.setStyle(
                "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
        );

        panelTabla.getChildren().addAll(
                tituloTabla,
                tabla
        );

        panelTabla.setPadding(new Insets(0, 0, 20, 0));

        // Panel de detalle
        Label tituloDetalle = new Label(
                "DETALLE DEL PEDIDO SELECCIONADO"
        );

        tituloDetalle.setStyle(
                "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
        );

        GridPane detalle = new GridPane();
        detalle.setHgap(25);
        detalle.setVgap(14);
        detalle.setPadding(new Insets(18));
        detalle.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #DCE5DD;"
                + "-fx-border-radius: 8;"
        );

        agregarCampo(detalle, 0, 0, "Pedido:", lblPedido);
        agregarCampo(detalle, 1, 0, "Mesa:", lblMesa);
        agregarCampo(detalle, 2, 0, "Cuenta:", lblCuenta);

        agregarCampo(
                detalle, 0, 1, "Estado del pedido:", lblEstadoPedido
        );

        agregarCampo(
                detalle, 1, 1, "Estado de la cuenta:", lblEstadoCuenta
        );

        agregarCampo(
                detalle, 2, 1, "Método de pago:", lblMetodoPago
        );

        agregarCampo(
                detalle, 0, 2, "Subtotal:", lblSubtotal
        );

        agregarCampo(
                detalle, 1, 2, "IGV:", lblIgv
        );

        agregarCampo(
                detalle, 2, 2, "TOTAL PAGADO:", lblTotal
        );

        lblTotal.setStyle(
                "-fx-font-size: 19px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #245B36;"
        );

        // Botones
        btnCerrar.setStyle(
                "-fx-background-color: #28643B;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 11 20 11 20;"
                + "-fx-background-radius: 5;"
        );

        btnActualizar.setStyle(
                "-fx-background-color: white;"
                + "-fx-text-fill: #28643B;"
                + "-fx-border-color: #28643B;"
                + "-fx-border-radius: 5;"
                + "-fx-padding: 10 20 10 20;"
        );

        btnCerrar.setDisable(true);

        HBox botones = new HBox(
                12, btnCerrar, btnActualizar
        );

        botones.setAlignment(Pos.CENTER_RIGHT);
        botones.setPadding(new Insets(18, 0, 0, 0));

        VBox panelInferior = new VBox(
                12, tituloDetalle, detalle, botones
        );

        setCenter(panelTabla);
        setBottom(panelInferior);
    }

    private void configurarTabla() {

        TableColumn<PedidoCierreRow, Number> colPedido
                = new TableColumn<>("N.º Pedido");

        colPedido.setCellValueFactory(d
                -> new ReadOnlyObjectWrapper<>(
                        d.getValue().pedido.getId()
                )
        );

        TableColumn<PedidoCierreRow, Number> colMesa
                = new TableColumn<>("Mesa");

        colMesa.setCellValueFactory(d
                -> new ReadOnlyObjectWrapper<>(
                        d.getValue().pedido.getMesa().getNumero()
                )
        );

        TableColumn<PedidoCierreRow, String> colMozo
                = new TableColumn<>("Mozo");

        colMozo.setCellValueFactory(d
                -> new ReadOnlyStringWrapper(
                        d.getValue().pedido.getMozo().getNombre()
                )
        );

        TableColumn<PedidoCierreRow, String> colEstadoPedido
                = new TableColumn<>("Pedido");

        colEstadoPedido.setCellValueFactory(d
                -> new ReadOnlyStringWrapper(
                        d.getValue().pedido.getEstado()
                )
        );

        TableColumn<PedidoCierreRow, String> colEstadoCuenta
                = new TableColumn<>("Cuenta");

        colEstadoCuenta.setCellValueFactory(d
                -> new ReadOnlyStringWrapper(
                        d.getValue().cuenta.getEstado()
                )
        );

        TableColumn<PedidoCierreRow, String> colTotal
                = new TableColumn<>("Total");

        colTotal.setCellValueFactory(d
                -> new ReadOnlyStringWrapper(
                        moneda(d.getValue().cuenta.getTotal())
                )
        );

        tabla.getColumns().addAll(
                colPedido,
                colMesa,
                colMozo,
                colEstadoPedido,
                colEstadoCuenta,
                colTotal
        );

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabla.setPlaceholder(
                new Label(
                        "No hay pedidos entregados con cuentas pagadas."
                )
        );

        tabla.setPrefHeight(300);
    }

    private void agregarCampo(
            GridPane grid,
            int columna,
            int fila,
            String texto,
            Label valor) {

        VBox campo = new VBox(5);

        Label etiqueta = new Label(texto);
        etiqueta.setStyle(
                "-fx-text-fill: #657268;"
                + "-fx-font-size: 12px;"
        );

        valor.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
        );

        campo.getChildren().addAll(etiqueta, valor);
        grid.add(campo, columna, fila);
    }

    private void configurarEventos() {

        tabla.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionado) -> {

                    if (seleccionado == null) {
                        limpiarDetalle();
                        btnCerrar.setDisable(true);
                        return;
                    }

                    mostrarDetalle(seleccionado);
                    btnCerrar.setDisable(false);
                });

        btnActualizar.setOnAction(e -> cargarPedidos());

        btnCerrar.setOnAction(e -> cerrarPedidoSeleccionado());
    }

    private void cargarPedidos() {

        try {
            List<Pedido> pedidos
                    = pedidoDAO.listarEntregadosConCuentaPagada();

            tabla.getItems().clear();

            for (Pedido pedido : pedidos) {

                Cuenta cuenta
                        = cuentaDAO.buscarPorPedidoId(pedido.getId());

                if (cuenta == null) {
                    continue;
                }

                if (!"PAGADO".equalsIgnoreCase(
                        cuenta.getEstado())) {
                    continue;
                }

                tabla.getItems().add(
                        new PedidoCierreRow(pedido, cuenta)
                );
            }

            limpiarDetalle();
            btnCerrar.setDisable(true);

        } catch (SQLException ex) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al consultar",
                    "No se pudieron cargar los pedidos.",
                    ex.getMessage()
            );
        }
    }

    private void mostrarDetalle(PedidoCierreRow fila) {

        Pedido pedido = fila.pedido;
        Cuenta cuenta = fila.cuenta;

        lblPedido.setText(String.valueOf(pedido.getId()));
        lblMesa.setText(
                String.valueOf(pedido.getMesa().getNumero())
        );

        lblCuenta.setText(String.valueOf(cuenta.getId()));
        lblMetodoPago.setText(
                cuenta.getMetodoPago() == null
                ? "-"
                : cuenta.getMetodoPago()
        );

        lblEstadoPedido.setText(pedido.getEstado());
        lblEstadoCuenta.setText(cuenta.getEstado());

        lblSubtotal.setText(moneda(cuenta.getSubTotal()));
        lblIgv.setText(moneda(cuenta.getIGV()));
        lblTotal.setText(moneda(cuenta.getTotal()));
    }

    private void cerrarPedidoSeleccionado() {

        PedidoCierreRow fila
                = tabla.getSelectionModel().getSelectedItem();

        if (fila == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selecciona un pedido",
                    "Debes seleccionar un pedido de la tabla.",
                    null
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Confirmar cierre");
        confirmacion.setHeaderText(
                "¿Deseas finalizar este pedido?"
        );

        confirmacion.setContentText(
                "Pedido: " + fila.pedido.getId()
                + "\nMesa: " + fila.pedido.getMesa().getNumero()
                + "\nTotal pagado: " + moneda(fila.cuenta.getTotal())
                + "\n\nEsta acción finalizará el pedido y su cuenta."
        );

        confirmacion.showAndWait().ifPresent(respuesta -> {

            if (respuesta != ButtonType.OK) {
                return;
            }

            try {
                pedidoService.cerrarPedido(
                        fila.pedido.getId()
                );

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Cierre completado",
                        "El pedido y la cuenta fueron finalizados.",
                        "El sistema guardó los cambios en la base de datos."
                );

                cargarPedidos();

            } catch (SQLException | IllegalStateException
                    | IllegalArgumentException ex) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "No se pudo cerrar el pedido",
                        "El cierre no se completó.",
                        ex.getMessage()
                );
            }
        });
    }

    private void limpiarDetalle() {

        lblPedido.setText("-");
        lblMesa.setText("-");
        lblCuenta.setText("-");
        lblMetodoPago.setText("-");
        lblEstadoPedido.setText("-");
        lblEstadoCuenta.setText("-");
        lblSubtotal.setText("S/ 0.00");
        lblIgv.setText("S/ 0.00");
        lblTotal.setText("S/ 0.00");
    }

    private String moneda(java.math.BigDecimal importe) {
        if (importe == null) {
            importe = java.math.BigDecimal.ZERO;
        }

        return "S/ " + importe.setScale(
                2, java.math.RoundingMode.HALF_UP
        ).toPlainString();
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String encabezado,
            String contenido) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);

        if (contenido != null) {
            alerta.setContentText(contenido);
        }

        alerta.showAndWait();
    }

    // Objeto auxiliar para mostrar cada pedido con su cuenta.
    private static class PedidoCierreRow {

        private final Pedido pedido;
        private final Cuenta cuenta;

        public PedidoCierreRow(Pedido pedido, Cuenta cuenta) {
            this.pedido = pedido;
            this.cuenta = cuenta;
        }
    }

    // Permite abrir esta ventana desde el menú principal.
    public static void mostrar() {

        Stage stage = new Stage();

        stage.setTitle("Cierre de Pedidos");
        stage.setScene(
                new Scene(new FrmCierrePedidos(), 1050, 700)
        );

        stage.show();
    }
}
