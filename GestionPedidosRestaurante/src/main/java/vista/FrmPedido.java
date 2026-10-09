package vista;

import controlador.MozoDAO;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.application.Application;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Mozo;
import modelo.Pedido;
import service.AtencionMesaService;
import service.PedidoService;

public class FrmPedido extends Application {

    private TextField txtId;
    private TextField txtFecha;
    private TextField txtEstado;
    private TextField txtSubtotal;

    private ComboBox<Mozo> cboMozo;
    private ComboBox<Mesa> cboMesa;

    private TableView<DetallePedido> tablaDetalles;
    private TableColumn<DetallePedido, Integer> colId;
    private TableColumn<DetallePedido, String> colPlato;
    private TableColumn<DetallePedido, Integer> colCantidad;
    private TableColumn<DetallePedido, Double> colPrecio;
    private TableColumn<DetallePedido, Double> colSubtotal;

    private Button btnNuevo;
    private Button btnAgregarPlato;
    private Button btnGuardar;
    private Button btnActualizar;
    private Button btnEliminar;
    private Button btnLimpiar;
    private Button btnEnviarCocina;

    private Pedido pedidoActual;

    private final PedidoService pedidoService;
    private final AtencionMesaService atencionMesaService;

    private final ObservableList<DetallePedido> listaDetalles
            = FXCollections.observableArrayList();

    public FrmPedido() {
        pedidoService = new PedidoService();
        atencionMesaService = new AtencionMesaService();
    }

    @Override
    public void start(Stage stage) {

        Label titulo = new Label("TOMAR PEDIDO");
        titulo.setStyle(
                "-fx-font-size: 24px; "
                + "-fx-font-weight: bold;"
        );

        txtId = new TextField();
        txtId.setEditable(false);

        txtFecha = new TextField();
        txtFecha.setEditable(false);

        txtEstado = new TextField();
        txtEstado.setEditable(false);

        txtSubtotal = new TextField();
        txtSubtotal.setEditable(false);

        cboMozo = new ComboBox<>();
        cboMozo.setPrefWidth(220);

        cboMesa = new ComboBox<>();
        cboMesa.setPrefWidth(220);

        GridPane datosPedido = new GridPane();
        datosPedido.setHgap(15);
        datosPedido.setVgap(12);
        datosPedido.setPadding(new Insets(15));

        datosPedido.add(new Label("ID Pedido:"), 0, 0);
        datosPedido.add(txtId, 1, 0);

        datosPedido.add(new Label("Fecha:"), 2, 0);
        datosPedido.add(txtFecha, 3, 0);

        datosPedido.add(new Label("Mozo:"), 0, 1);
        datosPedido.add(cboMozo, 1, 1);

        datosPedido.add(new Label("Mesa:"), 2, 1);
        datosPedido.add(cboMesa, 3, 1);

        datosPedido.add(new Label("Estado:"), 0, 2);
        datosPedido.add(txtEstado, 1, 2);

        datosPedido.add(new Label("Subtotal:"), 2, 2);
        datosPedido.add(txtSubtotal, 3, 2);

        // BOTON AGREGAR PLATO
        btnAgregarPlato = new Button("AGREGAR PLATO");
        btnAgregarPlato.setPrefWidth(180);
        btnAgregarPlato.setPrefHeight(35);
        //modificado
        btnAgregarPlato.setVisible(false);
        btnAgregarPlato.setManaged(false);
        btnAgregarPlato.setOnAction(e -> abrirHome());

        HBox panelAgregar = new HBox(btnAgregarPlato);
        panelAgregar.setAlignment(Pos.CENTER);
        panelAgregar.setPadding(new Insets(10));

        // TABLA DE DETALLES
        tablaDetalles = new TableView<>();

        colId = new TableColumn<>("ID");
        colPlato = new TableColumn<>("Plato");
        colCantidad = new TableColumn<>("Cantidad");
        colPrecio = new TableColumn<>("Precio");
        colSubtotal = new TableColumn<>("Subtotal");

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colPlato.setCellValueFactory(
                cellData
                -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue()
                                .getPlato()
                                .getNombre()
                )
        );

        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precioUnitario")
        );

        colSubtotal.setCellValueFactory(
                new PropertyValueFactory<>("subTotal")
        );

        colId.setPrefWidth(60);
        colPlato.setPrefWidth(220);
        colCantidad.setPrefWidth(100);
        colPrecio.setPrefWidth(100);
        colSubtotal.setPrefWidth(120);

        tablaDetalles.getColumns().addAll(
                colId,
                colPlato,
                colCantidad,
                colPrecio,
                colSubtotal
        );

        tablaDetalles.setItems(listaDetalles);

        // BOTONES
        btnNuevo = new Button("NUEVO");
        btnGuardar = new Button("GUARDAR");
        btnActualizar = new Button("ACTUALIZAR");
        btnEliminar = new Button("ELIMINAR");
        btnLimpiar = new Button("LIMPIAR");
        btnEnviarCocina = new Button("ENVIAR A COCINA");

        btnNuevo.setOnAction(e -> nuevoPedido());
        btnGuardar.setOnAction(e -> guardarPedido());
        btnActualizar.setOnAction(e -> actualizarPedido());
        btnEliminar.setOnAction(e -> eliminarPedido());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        btnEnviarCocina.setOnAction(e -> enviarACocina());

        HBox botones = new HBox(
                10,
                btnNuevo,
                btnGuardar,
                btnActualizar,
                btnEliminar,
                btnLimpiar,
                btnEnviarCocina
        );

        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(15));

        VBox centro = new VBox(
                10,
                datosPedido,
                panelAgregar,
                tablaDetalles,
                botones
        );

        centro.setPadding(new Insets(15));

        BorderPane root = new BorderPane();

        root.setTop(titulo);
        root.setCenter(centro);

        BorderPane.setAlignment(
                titulo,
                Pos.CENTER
        );

        BorderPane.setMargin(
                titulo,
                new Insets(15)
        );

        cargarMozos();
        cargarMesas();

        cboMozo.setOnAction(e -> verificarInicioPedido());
        cboMesa.setOnAction(e -> verificarInicioPedido());

        Scene scene = new Scene(
                root,
                950,
                650
        );

        stage.setTitle(
                "Gestión de Pedidos - Tomar Pedido"
        );

        stage.setScene(scene);
        stage.show();
    }

    // CARGAR MOZOS
    private void cargarMozos() {

        MozoDAO mozoDAO = new MozoDAO();

        List<Mozo> lista = mozoDAO.listar();

        cboMozo.setItems(
                FXCollections.observableArrayList(lista)
        );
    }

    // CARGAR MESAS DISPONIBLES
    private void cargarMesas() {

        controlador.MesaDAO mesaDAO
                = new controlador.MesaDAO();

        List<Mesa> lista = mesaDAO.listar();

        ObservableList<Mesa> mesasDisponibles
                = FXCollections.observableArrayList();

        for (Mesa mesa : lista) {

            if ("DISPONIBLE".equalsIgnoreCase(
                    mesa.getEstado())) {

                mesasDisponibles.add(mesa);
            }
        }

        cboMesa.setItems(mesasDisponibles);
    }

    // VERIFICAR SI SE PUEDE INICIAR
    private void verificarInicioPedido() {

        if (pedidoActual != null) {
            return;
        }

        if (cboMozo.getValue() != null
                && cboMesa.getValue() != null) {

            iniciarPedido();
        }
    }

    // INICIAR ATENCION Y PEDIDO
    private void iniciarPedido() {

        try {

            Mozo mozo = cboMozo.getValue();
            Mesa mesa = cboMesa.getValue();

            if (mozo == null || mesa == null) {

                mostrarError(
                        "Debe seleccionar un mozo y una mesa."
                );

                return;
            }

            boolean atencionIniciada
                    = atencionMesaService.iniciarAtencion(
                            mesa.getId(),
                            mozo.getId()
                    );

            if (!atencionIniciada) {

                mostrarError(
                        "No se pudo iniciar la atención de la mesa."
                );

                cargarMesas();

                return;
            }

            mesa.ocupar();

            pedidoActual
                    = pedidoService.crearPedido(
                            mesa,
                            mozo
                    );

            mostrarPedidoActual();

            //modificado
            btnAgregarPlato.setVisible(true);
            btnAgregarPlato.setManaged(true);
            btnAgregarPlato.setDisable(false);
            //>

            cboMozo.setDisable(true);
            cboMesa.setDisable(true);

            mostrarInformacion(
                    "Atención iniciada correctamente.\n\n"
                    + "Mozo: "
                    + mozo.getNombre()
                    + "\n"
                    + "Mesa: "
                    + mesa.getNumero()
                    + "\n\n"
                    + "Ahora puede agregar platos."
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo iniciar la atención y el pedido:\n"
                    + e.getMessage()
            );
        }
    }

    // MOSTRAR PEDIDO ACTUAL
    private void mostrarPedidoActual() {

        if (pedidoActual == null) {
            return;
        }

        txtId.setText(
                pedidoActual.getId() == 0
                ? "Pendiente"
                : String.valueOf(
                        pedidoActual.getId()
                )
        );

        txtFecha.setText(
                pedidoActual
                        .getFechaHora()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"
                                )
                        )
        );

        txtEstado.setText(
                pedidoActual.getEstado()
        );

        txtSubtotal.setText(
                String.format(
                        "S/ %.2f",
                        pedidoActual.getSubTotal()
                )
        );

        listaDetalles.setAll(
                pedidoActual.getDetalles()
        );
    }

    // ABRIR HOME
    //modificado
    private void abrirHome() {
        if (pedidoActual == null) {
            mostrarError("Primero debe iniciar un pedido.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(pedidoActual.getEstado()) && !"EN_PREPARACION".equalsIgnoreCase(pedidoActual.getEstado())) {
            mostrarError("Solo se pueden agregar platos a un pedido pendiente o en preparación.");
            return;
        }

        Home home = new Home(pedidoActual);
        home.mostrarModal();
        mostrarPedidoActual();
    }

    // GUARDAR PEDIDO
    private void guardarPedido() {

        if (pedidoActual == null) {

            mostrarError(
                    "Primero debe iniciar un pedido."
            );

            return;
        }

        try {

            if (pedidoActual.getDetalles().isEmpty()) {

                mostrarError(
                        "Debe agregar al menos un plato."
                );

                return;
            }

            if (pedidoActual.getId() == 0) {

                int id
                        = pedidoService.guardarPedido(
                                pedidoActual
                        );

                mostrarPedidoActual();

                mostrarInformacion(
                        "Pedido guardado correctamente.\n"
                        + "ID del pedido: "
                        + id
                );

            } else {

                actualizarPedido();
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudo guardar el pedido:\n"
                    + e.getMessage()
            );
        }
    }

    // ACTUALIZAR PEDIDO
    private void actualizarPedido() {

        if (pedidoActual == null) {

            mostrarError(
                    "No existe un pedido para actualizar."
            );

            return;
        }

        try {

            pedidoService.actualizarPedido(
                    pedidoActual
            );

            mostrarInformacion(
                    "Pedido actualizado correctamente."
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo actualizar el pedido:\n"
                    + e.getMessage()
            );
        }
    }

    // ELIMINAR PEDIDO
    private void eliminarPedido() {

        if (pedidoActual == null
                || pedidoActual.getId() == 0) {

            mostrarError(
                    "No existe un pedido guardado."
            );

            return;
        }

        try {

            pedidoService.eliminarPedido(
                    pedidoActual.getId()
            );

            mostrarInformacion(
                    "Pedido eliminado correctamente."
            );

            limpiarFormulario();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el pedido:\n"
                    + e.getMessage()
            );
        }
    }

    // ENVIAR A COCINA
    private void enviarACocina() {

        if (pedidoActual == null) {

            mostrarError(
                    "No existe un pedido."
            );

            return;
        }

        try {

            if (pedidoActual.getId() == 0) {

                mostrarError(
                        "Primero debe guardar el pedido."
                );

                return;
            }

            pedidoService.enviarACocina(
                    pedidoActual
            );

            mostrarPedidoActual();

            //modificado
            //btnAgregarPlato.setDisable(true);
            //btnAgregarPlato.setVisible(false);
            //btnAgregarPlato.setManaged(false);
            //>
            mostrarInformacion(
                    "Pedido enviado a cocina correctamente."
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo enviar el pedido a cocina:\n"
                    + e.getMessage()
            );
        }
    }

    // NUEVO PEDIDO
    private void nuevoPedido() {

        limpiarFormulario();

        cboMozo.setDisable(false);
        cboMesa.setDisable(false);

        cargarMesas();
    }

    // LIMPIAR FORMULARIO
    private void limpiarFormulario() {

        pedidoActual = null;

        txtId.clear();
        txtFecha.clear();
        txtEstado.clear();
        txtSubtotal.clear();

        listaDetalles.clear();

        cboMozo.getSelectionModel().clearSelection();
        cboMesa.getSelectionModel().clearSelection();

        //modificado
        btnAgregarPlato.setDisable(true);
        btnAgregarPlato.setVisible(false);
        btnAgregarPlato.setManaged(false);
        //>

        cboMozo.setDisable(false);
        cboMesa.setDisable(false);

        cargarMesas();
    }

    // MOSTRAR ERROR
    private void mostrarError(String mensaje) {

        Alert alerta
                = new Alert(
                        Alert.AlertType.ERROR
                );

        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // MOSTRAR INFORMACION
    private void mostrarInformacion(String mensaje) {

        Alert alerta
                = new Alert(
                        Alert.AlertType.INFORMATION
                );

        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void mostrar() {
        Stage nuevaVentana = new Stage();
        start(nuevaVentana);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
