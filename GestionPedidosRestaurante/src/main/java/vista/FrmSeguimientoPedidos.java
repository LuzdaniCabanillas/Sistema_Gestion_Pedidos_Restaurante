package vista;

import controlador.MesaDAO;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Pedido;
import service.PedidoService;

/**
 * LEAN
 */
public class FrmSeguimientoPedidos {

    private Stage ventana;

    private ComboBox<Mesa> cboMesa;

    private javafx.scene.control.TextField txtIdPedido;
    private javafx.scene.control.TextField txtFecha;
    private javafx.scene.control.TextField txtMozo;
    private javafx.scene.control.TextField txtEstado;
    private javafx.scene.control.TextField txtSubtotal;
    private Button btnGestionarCuenta;

    private TableView<DetallePedido> tablaDetalles;
    private DetallePedido detalleActual;

    private TableColumn<DetallePedido, Integer> colPedido;
    private TableColumn<DetallePedido, String> colPlato;
    private TableColumn<DetallePedido, Integer> colCantidad;
    private TableColumn<DetallePedido, Double> colPrecio;
    private TableColumn<DetallePedido, Double> colSubtotal;
    private TableColumn<DetallePedido, String> colEstado;

    private Button btnEnviarCocina;
    private Button btnRegistrarEntrega;
    private Button btnActualizar;
    private Button btnVolver;

    private final PedidoService pedidoService;

    private final ObservableList<DetallePedido> listaDetalles
            = FXCollections.observableArrayList();

    private final List<Pedido> pedidosDeMesa = new ArrayList<>();

    private Pedido pedidoActual;

    public FrmSeguimientoPedidos() {
        pedidoService = new PedidoService();
    }

    public void mostrar() {

        ventana = new Stage();

        Label titulo = new Label("SEGUIMIENTO DE PEDIDOS");
        titulo.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        cboMesa = new ComboBox<>();
        cboMesa.setPrefWidth(220);
        cboMesa.setPromptText("Seleccione una mesa");

        // Mostrar el número de mesa en el ComboBox.
        cboMesa.setConverter(new StringConverter<Mesa>() {

            @Override
            public String toString(Mesa mesa) {
                if (mesa == null) {
                    return "";
                }

                return "Mesa " + mesa.getNumero();
            }

            @Override
            public Mesa fromString(String texto) {
                return null;
            }
        });

        txtIdPedido = crearCampo();
        txtFecha = crearCampo();
        txtMozo = crearCampo();
        txtEstado = crearCampo();
        txtSubtotal = crearCampo();

        GridPane datos = new GridPane();
        datos.setHgap(12);
        datos.setVgap(12);
        datos.setPadding(new Insets(15));

        datos.add(new Label("Mesa ocupada:"), 0, 0);
        datos.add(cboMesa, 1, 0);

        datos.add(new Label("ID Pedido:"), 0, 1);
        datos.add(txtIdPedido, 1, 1);

        datos.add(new Label("Fecha y hora:"), 2, 1);
        datos.add(txtFecha, 3, 1);

        datos.add(new Label("Mozo:"), 0, 2);
        datos.add(txtMozo, 1, 2);

        datos.add(new Label("Estado del plato:"), 2, 2);
        datos.add(txtEstado, 3, 2);

        datos.add(new Label("Subtotal:"), 0, 3);
        datos.add(txtSubtotal, 1, 3);

        // Tabla de platos de los pedidos de la mesa seleccionada.
        tablaDetalles = new TableView<>();
        tablaDetalles.setItems(listaDetalles);
        tablaDetalles.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        colPedido = new TableColumn<>("ID Pedido");
        colPlato = new TableColumn<>("Plato");
        colCantidad = new TableColumn<>("Cantidad");
        colPrecio = new TableColumn<>("Precio unitario");
        colSubtotal = new TableColumn<>("Subtotal");
        colEstado = new TableColumn<>("Estado");

        colPedido.setCellValueFactory(cell -> {
            Pedido pedido = cell.getValue().getPedido();

            return new javafx.beans.property.SimpleObjectProperty<>(
                    pedido == null ? 0 : pedido.getId()
            );
        });

        colPlato.setCellValueFactory(cell -> {
            DetallePedido detalle = cell.getValue();

            String nombre = detalle.getPlato() == null
                    ? "Plato no disponible"
                    : detalle.getPlato().getNombre();

            return new javafx.beans.property.SimpleStringProperty(nombre);
        });

        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precioUnitario")
        );

        colSubtotal.setCellValueFactory(
                new PropertyValueFactory<>("subTotal")
        );

        colEstado.setCellValueFactory(cell
                -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getEstado()
                )
        );

        tablaDetalles.getColumns().addAll(
                colPedido,
                colPlato,
                colCantidad,
                colPrecio,
                colSubtotal,
                colEstado
        );

        // Botones de seguimiento.
        btnEnviarCocina = new Button("ENVIAR A COCINA");
        btnRegistrarEntrega = new Button("REGISTRAR ENTREGA");
        btnActualizar = new Button("ACTUALIZAR");
        btnGestionarCuenta = new Button("GESTIONAR CUENTA");
        btnVolver = new Button("VOLVER");

        btnEnviarCocina.setOnAction(e -> enviarACocina());
        btnRegistrarEntrega.setOnAction(e -> registrarEntrega());
        btnActualizar.setOnAction(e -> actualizar());
        btnGestionarCuenta.setOnAction(e -> abrirFrmCuenta());
        btnVolver.setOnAction(e -> ventana.close());

        HBox botones = new HBox(
                10,
                btnEnviarCocina,
                btnRegistrarEntrega,
                btnActualizar,
                btnGestionarCuenta,
                btnVolver
        );

        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(15));

        VBox centro = new VBox(
                10,
                datos,
                new Label("PLATOS DEL PEDIDO"),
                tablaDetalles,
                botones
        );

        centro.setPadding(new Insets(15));

        BorderPane root = new BorderPane();
        root.setTop(titulo);
        root.setCenter(centro);

        BorderPane.setAlignment(titulo, Pos.CENTER);
        BorderPane.setMargin(titulo, new Insets(20));

        cboMesa.setOnAction(e -> cargarPedidosDeMesa());

        // Al seleccionar una fila, se identifica el pedido correspondiente.
        tablaDetalles.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, detalle) -> {

                    detalleActual = detalle;

                    if (detalleActual != null) {
                        pedidoActual = detalleActual.getPedido();
                        mostrarDatosPedido();
                    } else {
                        pedidoActual = null;
                        limpiarDatos();
                    }

                    actualizarBotones();
                });

        Scene scene = new Scene(root, 1050, 680);

        ventana.setTitle("Sistema de Gestión de Pedidos - Seguimiento");
        ventana.setScene(scene);

        actualizar();

        ventana.show();

    }

    private void abrirFrmCuenta() {
        try {
            FrmCuenta frmCuenta = new FrmCuenta();

            Stage ventanaCuenta = new Stage();
            ventanaCuenta.setTitle("Sistema de Gestión de Pedidos - Cuentas");

            Scene escena = new Scene(frmCuenta, 1000, 750);
            ventanaCuenta.setScene(escena);

            ventanaCuenta.initOwner(ventana);
            ventanaCuenta.setResizable(true);
            ventanaCuenta.show();

        } catch (Exception ex) {
            mostrarError(
                    "No se pudo abrir la ventana de cuentas:\n"
                    + ex.getMessage()
            );
        }
    }

    //>
    private javafx.scene.control.TextField crearCampo() {
        javafx.scene.control.TextField campo
                = new javafx.scene.control.TextField();

        campo.setEditable(false);
        return campo;
    }

    // Cargar mesas ocupadas que tengan pedidos por atender
    // o pedidos que ya fueron entregados.
    private void actualizar() {

        Integer numeroMesaSeleccionada = null;

        if (cboMesa != null && cboMesa.getValue() != null) {
            numeroMesaSeleccionada = cboMesa.getValue().getNumero();
        }

        try {
            List<Pedido> todosLosPedidos = pedidoService.listarPedidos();

            Map<Integer, Mesa> mesasOcupadas = new LinkedHashMap<>();

            for (Pedido pedido : todosLosPedidos) {

                if (pedido.getMesa() == null) {
                    continue;
                }

                String estadoPedido = pedido.getEstado();

                boolean pedidoVisible
                        = "PENDIENTE".equalsIgnoreCase(estadoPedido)
                        || "EN_PREPARACION".equalsIgnoreCase(estadoPedido)
                        || "ENTREGADO".equalsIgnoreCase(estadoPedido);

                boolean mesaOcupada
                        = "OCUPADA".equalsIgnoreCase(
                                pedido.getMesa().getEstado()
                        );

                if (pedidoVisible && mesaOcupada) {
                    mesasOcupadas.put(
                            pedido.getMesa().getId(),
                            pedido.getMesa()
                    );
                }
            }

            ObservableList<Mesa> listaMesas
                    = FXCollections.observableArrayList(
                            mesasOcupadas.values()
                    );

            cboMesa.setItems(listaMesas);

            Mesa mesaParaSeleccionar = null;

            if (numeroMesaSeleccionada != null) {
                for (Mesa mesa : listaMesas) {
                    if (mesa.getNumero() == numeroMesaSeleccionada) {
                        mesaParaSeleccionar = mesa;
                        break;
                    }
                }
            }

            if (mesaParaSeleccionar == null && !listaMesas.isEmpty()) {
                mesaParaSeleccionar = listaMesas.get(0);
            }

            cboMesa.setValue(mesaParaSeleccionar);

            if (mesaParaSeleccionar == null) {
                limpiarDatos();
            } else {
                cargarPedidosDeMesa();
            }

        } catch (Exception ex) {
            mostrarError(
                    "No se pudo actualizar el seguimiento:\n"
                    + ex.getMessage()
            );
        }
    }

    // Cargar todos los pedidos visibles de la mesa.
    private void cargarPedidosDeMesa() {

        Mesa mesaSeleccionada = cboMesa.getValue();

        pedidosDeMesa.clear();
        listaDetalles.clear();
        pedidoActual = null;

        if (mesaSeleccionada == null) {
            limpiarDatos();
            actualizarBotones();
            return;
        }

        try {
            List<Pedido> todosLosPedidos = pedidoService.listarPedidos();

            for (Pedido pedido : todosLosPedidos) {

                if (pedido.getMesa() == null
                        || pedido.getMesa().getId()
                        != mesaSeleccionada.getId()) {
                    continue;
                }

                String estado = pedido.getEstado();

                boolean pedidoVisible
                        = "PENDIENTE".equalsIgnoreCase(estado)
                        || "EN_PREPARACION".equalsIgnoreCase(estado)
                        || "ENTREGADO".equalsIgnoreCase(estado);

                if (!pedidoVisible) {
                    continue;
                }

                List<DetallePedido> detalles
                        = pedidoService.listarDetalles(pedido.getId());

                pedido.setDetalles(detalles);

                for (DetallePedido detalle : detalles) {
                    // Cada fila conserva la referencia a su pedido.
                    detalle.setPedido(pedido);
                }

                pedidosDeMesa.add(pedido);
            }

            // Los pedidos vienen normalmente del más reciente al más antiguo.
            for (Pedido pedido : pedidosDeMesa) {
                listaDetalles.addAll(pedido.getDetalles());
            }

            if (!listaDetalles.isEmpty()) {

                // Selecciona inicialmente el primer plato del pedido más reciente.
                tablaDetalles.getSelectionModel().select(0);

                DetallePedido detalleSeleccionado
                        = tablaDetalles.getSelectionModel().getSelectedItem();

                if (detalleSeleccionado != null) {
                    pedidoActual = detalleSeleccionado.getPedido();
                    mostrarDatosPedido();
                }

            } else {
                limpiarDatos();

                mostrarInformacion(
                        "La mesa seleccionada no tiene platos registrados "
                        + "en sus pedidos visibles."
                );
            }

            actualizarBotones();

        } catch (Exception ex) {
            mostrarError(
                    "No se pudieron cargar los pedidos de la mesa:\n"
                    + ex.getMessage()
            );
        }
    }

    private void mostrarDatosPedido() {

        if (pedidoActual == null) {
            limpiarDatos();
            return;
        }

        txtIdPedido.setText(String.valueOf(pedidoActual.getId()));

        if (pedidoActual.getFechaHora() != null) {
            txtFecha.setText(
                    pedidoActual.getFechaHora().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    )
            );
        } else {
            txtFecha.clear();
        }

        txtMozo.setText(
                pedidoActual.getMozo() == null
                ? ""
                : pedidoActual.getMozo().getNombre()
        );

        txtEstado.setText(pedidoActual.getEstado());

        txtSubtotal.setText(
                String.format("S/ %.2f", pedidoActual.getSubTotal())
        );
    }

    private void actualizarBotones() {

        boolean pendiente = detalleActual != null
                && "PENDIENTE".equalsIgnoreCase(
                        detalleActual.getEstado()
                );

        boolean enPreparacion = detalleActual != null
                && "EN_PREPARACION".equalsIgnoreCase(
                        detalleActual.getEstado()
                );

        btnEnviarCocina.setDisable(!pendiente);
        btnRegistrarEntrega.setDisable(!enPreparacion);
    }

    private void enviarACocina() {

        if (detalleActual == null) {
            mostrarError("Seleccione un plato.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(
                detalleActual.getEstado())) {
            mostrarError(
                    "Solo se pueden enviar a cocina platos pendientes."
            );
            return;
        }

        try {
            String nombrePlato = detalleActual.getPlato().getNombre();

            pedidoService.enviarPlatoACocina(detalleActual);

            mostrarInformacion(
                    "Plato enviado a cocina: " + nombrePlato
            );

            actualizar();

        } catch (Exception ex) {
            mostrarError(
                    "No se pudo enviar el plato a cocina:\n"
                    + ex.getMessage()
            );
        }
    }

    private void registrarEntrega() {

        if (detalleActual == null) {
            mostrarError("Seleccione un plato.");
            return;
        }

        if (!"EN_PREPARACION".equalsIgnoreCase(
                detalleActual.getEstado())) {
            mostrarError(
                    "Solo se pueden entregar platos en preparación."
            );
            return;
        }

        String nombrePlato = detalleActual.getPlato().getNombre();
        int idPedido = detalleActual.getPedido().getId();

        Alert confirmacion
                = new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Confirmar entrega");
        confirmacion.setHeaderText("Registrar entrega de un plato");
        confirmacion.setContentText(
                "¿Confirma que se entregó el plato \""
                + nombrePlato + "\"?\n\n"
                + "Pedido: " + idPedido + "\n"
                + "Solo cambiará el estado de este plato."
        );

        java.util.Optional<javafx.scene.control.ButtonType> respuesta
                = confirmacion.showAndWait();

        if (respuesta.isEmpty()
                || respuesta.get()
                != javafx.scene.control.ButtonType.OK) {
            return;
        }

        try {
            pedidoService.entregarPlato(detalleActual);

            mostrarInformacion(
                    "Entrega registrada correctamente.\n\n"
                    + "Plato: " + nombrePlato
                    + "\nEstado: ENTREGADO"
            );

            actualizar();

        } catch (Exception ex) {
            mostrarError(
                    "No se pudo registrar la entrega:\n"
                    + ex.getMessage()
            );
        }
    }

    private void limpiarDatos() {

        pedidoActual = null;

        txtIdPedido.clear();
        txtFecha.clear();
        txtMozo.clear();
        txtEstado.clear();
        txtSubtotal.clear();

        listaDetalles.clear();
    }

    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
