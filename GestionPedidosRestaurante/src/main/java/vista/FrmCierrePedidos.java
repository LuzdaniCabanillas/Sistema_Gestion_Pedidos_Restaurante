package vista;

import controlador.CuentaDAO;
import controlador.PedidoDAO;
import java.sql.SQLException;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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

    // Paleta visual del sistema
    private static final String VERDE = "#285943";
    private static final String VERDE_HOVER = "#1D4432";
    private static final String FONDO = "#F3F6F4";
    private static final String BORDE = "#DCE5DF";
    private static final String TEXTO = "#26352C";
    private static final String TEXTO_SECUNDARIO = "#68776D";

    public FrmCierrePedidos() {
        construirInterfaz();
        configurarEventos();
        cargarPedidos();
    }

    private void construirInterfaz() {

        setPadding(new Insets(25));
        setStyle("-fx-background-color: " + FONDO + ";");

        // Encabezado
        Label titulo = new Label("CIERRE DE PEDIDOS");
        titulo.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 25px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE + ";"
        );

        Label subtitulo = new Label(
                "Finaliza los pedidos entregados y con la cuenta pagada."
        );
        subtitulo.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 13px;"
                + "-fx-text-fill: " + TEXTO_SECUNDARIO + ";"
        );

        VBox encabezado = new VBox(6, titulo, subtitulo);
        encabezado.setPadding(new Insets(0, 0, 20, 0));

        setTop(encabezado);

        // Tabla de pedidos
        configurarTabla();

        Label tituloTabla = new Label(
                "PEDIDOS DISPONIBLES PARA CERRAR"
        );
        tituloTabla.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
        );

        VBox panelTabla = new VBox(12, tituloTabla, tabla);
        panelTabla.setPadding(new Insets(18));
        panelTabla.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 12;"
        );
        VBox.setVgrow(tabla, javafx.scene.layout.Priority.ALWAYS);

        // Detalle del pedido seleccionado
        Label tituloDetalle = new Label(
                "DETALLE DEL PEDIDO SELECCIONADO"
        );
        tituloDetalle.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
        );

        GridPane detalle = new GridPane();
        detalle.setHgap(25);
        detalle.setVgap(17);
        detalle.setPadding(new Insets(20));
        detalle.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 12;"
        );

        detalle.setMaxWidth(Double.MAX_VALUE);

        agregarCampo(detalle, 0, 0, "Número de pedido", lblPedido);
        agregarCampo(detalle, 1, 0, "Número de mesa", lblMesa);
        agregarCampo(detalle, 2, 0, "Número de cuenta", lblCuenta);

        agregarCampo(
                detalle, 0, 1, "Estado del pedido", lblEstadoPedido
        );

        agregarCampo(
                detalle, 1, 1, "Estado de la cuenta", lblEstadoCuenta
        );

        agregarCampo(
                detalle, 2, 1, "Método de pago", lblMetodoPago
        );

        agregarCampo(detalle, 0, 2, "Subtotal", lblSubtotal);
        agregarCampo(detalle, 1, 2, "IGV", lblIgv);
        agregarCampo(detalle, 2, 2, "TOTAL PAGADO", lblTotal);

        lblTotal.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 20px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE + ";"
        );

        // Botones
        estilizarBotonPrincipal(btnCerrar);
        estilizarBotonSecundario(btnActualizar);

        btnCerrar.setDisable(true);

        HBox botones = new HBox(12, btnActualizar, btnCerrar);
        botones.setAlignment(Pos.CENTER_RIGHT);
        botones.setPadding(new Insets(5, 0, 0, 0));

        VBox panelInferior = new VBox(
                12, tituloDetalle, detalle, botones
        );
        panelInferior.setPadding(new Insets(20, 0, 0, 0));

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
                = new TableColumn<>("Estado del pedido");

        colEstadoPedido.setCellValueFactory(d
                -> new ReadOnlyStringWrapper(
                        d.getValue().pedido.getEstado()
                )
        );

        TableColumn<PedidoCierreRow, String> colEstadoCuenta
                = new TableColumn<>("Estado de la cuenta");

        colEstadoCuenta.setCellValueFactory(d
                -> new ReadOnlyStringWrapper(
                        d.getValue().cuenta.getEstado()
                )
        );

        TableColumn<PedidoCierreRow, String> colTotal
                = new TableColumn<>("Total pagado");

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
        tabla.setMinHeight(180);
        tabla.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 12px;"
                + "-fx-background-color: white;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 8;"
        );
    }

    private void agregarCampo(
            GridPane grid,
            int columna,
            int fila,
            String texto,
            Label valor) {

        VBox campo = new VBox(6);

        Label etiqueta = new Label(texto);
        etiqueta.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-text-fill: " + TEXTO_SECUNDARIO + ";"
                + "-fx-font-size: 12px;"
        );

        valor.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
        );

        campo.getChildren().addAll(etiqueta, valor);
        grid.add(campo, columna, fila);
    }

    private void estilizarBotonPrincipal(Button boton) {

        String estiloNormal
                = "-fx-background-color: " + VERDE + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 12 20 12 20;"
                + "-fx-background-radius: 7;"
                + "-fx-cursor: hand;";

        String estiloHover
                = "-fx-background-color: " + VERDE_HOVER + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 12 20 12 20;"
                + "-fx-background-radius: 7;"
                + "-fx-cursor: hand;";

        boton.setStyle(estiloNormal);
        boton.setOnMouseEntered(e -> {
            if (!boton.isDisabled()) {
                boton.setStyle(estiloHover);
            }
        });
        boton.setOnMouseExited(e -> boton.setStyle(estiloNormal));
    }

    private void estilizarBotonSecundario(Button boton) {

        String estiloNormal
                = "-fx-background-color: white;"
                + "-fx-text-fill: " + VERDE + ";"
                + "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-border-color: " + VERDE + ";"
                + "-fx-border-width: 1px;"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-padding: 11 20 11 20;"
                + "-fx-cursor: hand;";

        String estiloHover
                = "-fx-background-color: #E8F0EB;"
                + "-fx-text-fill: " + VERDE_HOVER + ";"
                + "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-border-color: " + VERDE_HOVER + ";"
                + "-fx-border-width: 1px;"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-padding: 11 20 11 20;"
                + "-fx-cursor: hand;";

        boton.setStyle(estiloNormal);
        boton.setOnMouseEntered(e -> boton.setStyle(estiloHover));
        boton.setOnMouseExited(e -> boton.setStyle(estiloNormal));
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
