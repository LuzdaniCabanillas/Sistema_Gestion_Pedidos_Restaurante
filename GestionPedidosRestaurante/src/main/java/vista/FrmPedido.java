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
import javafx.scene.control.TextInputDialog;

import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Mozo;
import modelo.Pedido;
import service.AtencionMesaService;
import service.PedidoService;

public class FrmPedido extends Application {

    // PALETA DE COLORES
    private static final String VERDE_OSCURO = "#183D32";
    private static final String VERDE = "#28785B";
    private static final String VERDE_HOVER = "#35966F";
    private static final String FONDO = "#F3F6F4";
    private static final String BLANCO = "#FFFFFF";
    private static final String TEXTO = "#24352D";
    private static final String GRIS = "#64746B";

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
    private Button btnModificarCantidad;
    private Button btnQuitarPlato;
    private Button btnLimpiar;
    private Button btnEnviarCocina;
    private Button btnVolver;

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

        // ==========================================
        // TÍTULO
        // ==========================================
        Label titulo = new Label("TOMAR PEDIDO");
        titulo.setStyle(
                "-fx-font-size: 26px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE_OSCURO + ";"
        );

        Label subtitulo = new Label(
                "Registra los platos y administra el pedido de la mesa"
        );
        subtitulo.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: " + GRIS + ";"
        );

        VBox encabezado = new VBox(5, titulo, subtitulo);
        encabezado.setPadding(new Insets(22, 25, 18, 25));
        encabezado.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-border-color: #E0E8E2;"
                + "-fx-border-width: 0 0 1 0;"
        );

        // ==========================================
        // CAMPOS DEL PEDIDO
        // ==========================================
        txtId = new TextField();
        txtId.setEditable(false);
        estilizarCampo(txtId);

        txtFecha = new TextField();
        txtFecha.setEditable(false);
        estilizarCampo(txtFecha);

        txtEstado = new TextField();
        txtEstado.setEditable(false);
        estilizarCampo(txtEstado);

        txtSubtotal = new TextField();
        txtSubtotal.setEditable(false);
        estilizarCampo(txtSubtotal);

        cboMozo = new ComboBox<>();
        cboMozo.setPrefWidth(220);
        estilizarCombo(cboMozo);

        cboMesa = new ComboBox<>();
        cboMesa.setPrefWidth(220);
        estilizarCombo(cboMesa);

        GridPane datosPedido = new GridPane();
        datosPedido.setHgap(18);
        datosPedido.setVgap(14);
        datosPedido.setPadding(new Insets(20));

        agregarCampo(datosPedido, "ID DEL PEDIDO", txtId, 0, 0);
        agregarCampo(datosPedido, "FECHA Y HORA", txtFecha, 2, 0);
        agregarCampo(datosPedido, "MOZO", cboMozo, 0, 1);
        agregarCampo(datosPedido, "MESA", cboMesa, 2, 1);
        agregarCampo(datosPedido, "ESTADO", txtEstado, 0, 2);
        agregarCampo(datosPedido, "SUBTOTAL", txtSubtotal, 2, 2);

        datosPedido.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E0E8E2;"
                + "-fx-border-radius: 12;"
                + "-fx-border-width: 1;"
        );

        // ==========================================
        // BOTÓN AGREGAR PLATO
        // ==========================================
        btnAgregarPlato = new Button("+  AGREGAR PLATO");
        btnAgregarPlato.setPrefWidth(200);
        btnAgregarPlato.setPrefHeight(40);
        estilizarBoton(btnAgregarPlato, "primario");

        btnAgregarPlato.setVisible(false);
        btnAgregarPlato.setManaged(false);

        btnAgregarPlato.setOnAction(e -> abrirHome());

        HBox panelAgregar = new HBox(btnAgregarPlato);
        panelAgregar.setAlignment(Pos.CENTER_LEFT);
        panelAgregar.setPadding(new Insets(5, 0, 5, 0));

        // ==========================================
        // TABLA DE DETALLES
        // ==========================================
        tablaDetalles = new TableView<>();
        tablaDetalles.setItems(listaDetalles);

        tablaDetalles.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tablaDetalles.setPlaceholder(
                new Label("Todavía no se han agregado platos al pedido.")
        );

        tablaDetalles.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #DCE6DF;"
                + "-fx-border-radius: 10;"
                + "-fx-border-width: 1;"
                + "-fx-font-size: 13px;"
        );

        colId = new TableColumn<>("ID");
        colPlato = new TableColumn<>("Plato");
        colCantidad = new TableColumn<>("Cantidad");
        colPrecio = new TableColumn<>("Precio unitario");
        colSubtotal = new TableColumn<>("Subtotal");

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colPlato.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getPlato().getNombre()
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

        colId.setPrefWidth(65);
        colPlato.setPrefWidth(250);
        colCantidad.setPrefWidth(120);
        colPrecio.setPrefWidth(150);
        colSubtotal.setPrefWidth(150);

        tablaDetalles.getColumns().addAll(
                colId,
                colPlato,
                colCantidad,
                colPrecio,
                colSubtotal
        );

        // ==========================================
        // BOTONES DE OPERACIÓN
        // ==========================================
        btnNuevo = new Button("NUEVO");
        btnGuardar = new Button("GUARDAR");
        btnModificarCantidad = new Button("MODIFICAR CANTIDAD");
        btnQuitarPlato = new Button("QUITAR PLATO");
        btnLimpiar = new Button("LIMPIAR");
        btnEnviarCocina = new Button("ENVIAR A COCINA");
        btnEnviarCocina.setVisible(false);
        btnEnviarCocina.setManaged(false);
        btnVolver = new Button("VOLVER");

        btnNuevo.setOnAction(e -> nuevoPedido());
        btnGuardar.setOnAction(e -> guardarPedido());
        btnModificarCantidad.setOnAction(e -> modificarCantidad());
        btnQuitarPlato.setOnAction(e -> quitarPlato());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        btnEnviarCocina.setOnAction(e -> enviarACocina());

        btnVolver.setOnAction(e -> stage.close());

        // Estilo individual de los botones
        for (Button boton : new Button[]{
            btnNuevo, btnGuardar, btnModificarCantidad,
            btnQuitarPlato, btnLimpiar, btnEnviarCocina,
            btnVolver
        }) {
            boton.setPrefHeight(39);
            boton.setMinHeight(39);
        }

        btnNuevo.setPrefWidth(100);
        btnGuardar.setPrefWidth(110);
        btnModificarCantidad.setPrefWidth(175);
        btnQuitarPlato.setPrefWidth(130);
        btnLimpiar.setPrefWidth(100);
        btnEnviarCocina.setPrefWidth(155);
        btnVolver.setPrefWidth(100);

        estilizarBoton(btnNuevo, "secundario");
        estilizarBoton(btnGuardar, "primario");
        estilizarBoton(btnModificarCantidad, "secundario");
        estilizarBoton(btnQuitarPlato, "peligro");
        estilizarBoton(btnLimpiar, "secundario");
        estilizarBoton(btnEnviarCocina, "primario");
        estilizarBoton(btnVolver, "volver");

        HBox botones = new HBox(
                9,
                btnNuevo,
                btnGuardar,
                btnModificarCantidad,
                btnQuitarPlato,
                btnLimpiar,
                btnEnviarCocina,
                btnVolver
        );

        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(16, 0, 5, 0));

        // ==========================================
        // CONTENIDO CENTRAL
        // ==========================================
        Label tituloDetalles = new Label("DETALLE DEL PEDIDO");
        tituloDetalles.setStyle(
                "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE_OSCURO + ";"
        );

        VBox centro = new VBox(
                14,
                datosPedido,
                panelAgregar,
                tituloDetalles,
                tablaDetalles,
                botones
        );

        centro.setPadding(new Insets(20, 25, 20, 25));
        VBox.setVgrow(tablaDetalles, javafx.scene.layout.Priority.ALWAYS);

        // ==========================================
        // VENTANA PRINCIPAL
        // ==========================================
        BorderPane root = new BorderPane();
        root.setTop(encabezado);
        root.setCenter(centro);

        root.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        cargarMozos();
        cargarMesas();

        cboMozo.setOnAction(e -> verificarInicioPedido());
        cboMesa.setOnAction(e -> verificarInicioPedido());

        Scene scene = new Scene(root, 1150, 750);

        stage.setTitle("Restaurante | Tomar pedido");
        stage.setMinWidth(1000);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    // ==========================================
    // MÉTODOS DE ESTILO
    // ==========================================
    private void agregarCampo(
            GridPane grid,
            String texto,
            javafx.scene.Node control,
            int columna,
            int fila) {

        Label etiqueta = new Label(texto);
        etiqueta.setStyle(
                "-fx-font-size: 11px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + GRIS + ";"
        );

        VBox grupo = new VBox(6, etiqueta, control);
        grupo.setAlignment(Pos.CENTER_LEFT);
        grupo.setPrefWidth(300);

        grid.add(grupo, columna, fila);
    }

    private void estilizarCampo(TextField campo) {
        campo.setPrefHeight(36);
        campo.setStyle(
                "-fx-background-color: #F8FAF8;"
                + "-fx-border-color: #D5E1D9;"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-padding: 8 10 8 10;"
                + "-fx-font-size: 13px;"
                + "-fx-text-fill: " + TEXTO + ";"
        );
    }

    private void estilizarCombo(ComboBox<?> combo) {
        combo.setPrefHeight(36);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setStyle(
                "-fx-background-color: #F8FAF8;"
                + "-fx-border-color: #D5E1D9;"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-font-size: 13px;"
        );
    }

    private void estilizarBoton(Button boton, String tipo) {

        String normal;
        String hover;

        switch (tipo) {
            case "primario":
                normal
                        = "-fx-background-color: " + VERDE + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-cursor: hand;";

                hover
                        = "-fx-background-color: " + VERDE_HOVER + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-cursor: hand;";
                break;

            case "peligro":
                normal
                        = "-fx-background-color: #F9E9E7;"
                        + "-fx-text-fill: #A63F35;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-cursor: hand;";

                hover
                        = "-fx-background-color: #F1D4D0;"
                        + "-fx-text-fill: #8C2D25;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-cursor: hand;";
                break;

            case "volver":
                normal
                        = "-fx-background-color: #E9EEEB;"
                        + "-fx-text-fill: " + VERDE_OSCURO + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-cursor: hand;";

                hover
                        = "-fx-background-color: #D9E3DD;"
                        + "-fx-text-fill: " + VERDE_OSCURO + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-cursor: hand;";
                break;

            default:
                normal
                        = "-fx-background-color: #E5F1E9;"
                        + "-fx-text-fill: " + VERDE_OSCURO + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-cursor: hand;";

                hover
                        = "-fx-background-color: #D0E6D8;"
                        + "-fx-text-fill: " + VERDE_OSCURO + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 9 12 9 12;"
                        + "-fx-cursor: hand;";
                break;
        }

        boton.setStyle(normal);

        boton.setOnMouseEntered(e -> {
            if (!boton.isDisabled()) {
                boton.setStyle(hover);
            }
        });

        boton.setOnMouseExited(e -> boton.setStyle(normal));
    }

    // ==========================================
    // CARGAR MOZOS
    // ==========================================
    private void cargarMozos() {

        MozoDAO mozoDAO = new MozoDAO();
        List<Mozo> lista = mozoDAO.listar();

        cboMozo.setItems(
                FXCollections.observableArrayList(lista)
        );
    }

    // ==========================================
    // CARGAR MESAS DISPONIBLES
    // ==========================================
    private void cargarMesas() {

        controlador.MesaDAO mesaDAO
                = new controlador.MesaDAO();

        List<Mesa> lista = mesaDAO.listar();

        ObservableList<Mesa> mesasDisponibles
                = FXCollections.observableArrayList();

        for (Mesa mesa : lista) {
            if ("DISPONIBLE".equalsIgnoreCase(mesa.getEstado())) {
                mesasDisponibles.add(mesa);
            }
        }

        cboMesa.setItems(mesasDisponibles);
    }

    // ==========================================
    // VERIFICAR SI SE PUEDE INICIAR
    // ==========================================
    private void verificarInicioPedido() {

        if (pedidoActual != null) {
            return;
        }

        if (cboMozo.getValue() != null
                && cboMesa.getValue() != null) {
            iniciarPedido();
        }
    }

    // ==========================================
    // INICIAR ATENCIÓN Y PEDIDO
    // ==========================================
    private void iniciarPedido() {

        try {

            Mozo mozo = cboMozo.getValue();
            Mesa mesa = cboMesa.getValue();

            if (mozo == null || mesa == null) {
                mostrarError("Debe seleccionar un mozo y una mesa.");
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

            pedidoActual = pedidoService.crearPedido(mesa, mozo);

            mostrarPedidoActual();

            btnAgregarPlato.setVisible(true);
            btnAgregarPlato.setManaged(true);
            btnAgregarPlato.setDisable(false);

            cboMozo.setDisable(true);
            cboMesa.setDisable(true);

            mostrarInformacion(
                    "Atención iniciada correctamente.\n\n"
                    + "Mozo: " + mozo.getNombre() + "\n"
                    + "Mesa: " + mesa.getNumero() + "\n\n"
                    + "Ahora puede agregar platos."
            );

        } catch (Exception e) {
            mostrarError(
                    "No se pudo iniciar la atención y el pedido:\n"
                    + e.getMessage()
            );
        }
    }

    // ==========================================
    // MOSTRAR PEDIDO ACTUAL
    // ==========================================
    private void mostrarPedidoActual() {

        if (pedidoActual == null) {
            return;
        }

        txtId.setText(
                pedidoActual.getId() == 0
                ? "Pendiente"
                : String.valueOf(pedidoActual.getId())
        );

        txtFecha.setText(
                pedidoActual.getFechaHora().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                )
        );

        txtEstado.setText(pedidoActual.getEstado());

        txtSubtotal.setText(
                String.format("S/ %.2f", pedidoActual.getSubTotal())
        );

        listaDetalles.setAll(pedidoActual.getDetalles());
    }

    // ==========================================
    // MODIFICAR CANTIDAD
    // ==========================================
    private void modificarCantidad() {

        DetallePedido detalle = tablaDetalles.getSelectionModel()
                .getSelectedItem();

        if (pedidoActual == null || detalle == null) {
            mostrarError("Seleccione un plato de la tabla.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(
                String.valueOf(detalle.getCantidad())
        );

        dialog.setTitle("Modificar cantidad");
        dialog.setHeaderText(
                "Plato: " + detalle.getPlato().getNombre()
        );
        dialog.setContentText("Ingrese la nueva cantidad:");

        dialog.showAndWait().ifPresent(texto -> {
            try {
                int cantidad = Integer.parseInt(texto.trim());

                pedidoService.modificarCantidad(
                        pedidoActual, detalle, cantidad
                );

                mostrarPedidoActual();

            } catch (NumberFormatException ex) {
                mostrarError("Ingrese una cantidad válida.");
            } catch (Exception ex) {
                mostrarError(
                        "No se pudo modificar la cantidad:\n"
                        + ex.getMessage()
                );
            }
        });
    }

    // ==========================================
    // QUITAR PLATO
    // ==========================================
    private void quitarPlato() {

        DetallePedido detalle = tablaDetalles.getSelectionModel()
                .getSelectedItem();

        if (pedidoActual == null || detalle == null) {
            mostrarError("Seleccione un plato de la tabla.");
            return;
        }

        try {
            pedidoService.quitarPlato(pedidoActual, detalle);
            mostrarPedidoActual();

            mostrarInformacion(
                    detalle.getCantidad() > 0
                    && pedidoActual.getDetalles().contains(detalle)
                    ? "Se redujo la cantidad del plato."
                    : "Se quitó el último plato del detalle."
            );

        } catch (Exception ex) {
            mostrarError(
                    "No se pudo quitar el plato:\n"
                    + ex.getMessage()
            );
        }
    }

    // ==========================================
    // ABRIR HOME PARA AGREGAR PLATOS
    // ==========================================
    private void abrirHome() {

        if (pedidoActual == null) {
            mostrarError("Primero debe iniciar un pedido.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(pedidoActual.getEstado())
                && !"EN_PREPARACION".equalsIgnoreCase(
                        pedidoActual.getEstado())) {
            mostrarError(
                    "Solo se pueden agregar platos a un pedido "
                    + "pendiente o en preparación."
            );
            return;
        }

        Home home = new Home(pedidoActual);
        home.mostrarModal();
        mostrarPedidoActual();
    }

    // ==========================================
    // GUARDAR PEDIDO
    // ==========================================
    private void guardarPedido() {

        if (pedidoActual == null) {
            mostrarError("Primero debe iniciar un pedido.");
            return;
        }

        try {

            if (pedidoActual.getDetalles().isEmpty()) {
                mostrarError("Debe agregar al menos un plato.");
                return;
            }

            if (pedidoActual.getId() == 0) {

                int id = pedidoService.guardarPedido(pedidoActual);

                mostrarPedidoActual();

                mostrarInformacion(
                        "Pedido guardado correctamente.\n"
                        + "ID del pedido: " + id
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

    // ==========================================
    // ACTUALIZAR PEDIDO
    // ==========================================
    private void actualizarPedido() {

        if (pedidoActual == null) {
            mostrarError("No existe un pedido para actualizar.");
            return;
        }

        try {
            pedidoService.actualizarPedido(pedidoActual);
            mostrarInformacion("Pedido actualizado correctamente.");

        } catch (Exception e) {
            mostrarError(
                    "No se pudo actualizar el pedido:\n"
                    + e.getMessage()
            );
        }
    }

    // ==========================================
    // ELIMINAR PEDIDO
    // ==========================================
    private void eliminarPedido() {

        if (pedidoActual == null || pedidoActual.getId() == 0) {
            mostrarError("No existe un pedido guardado.");
            return;
        }

        try {
            pedidoService.eliminarPedido(pedidoActual.getId());

            mostrarInformacion("Pedido eliminado correctamente.");
            limpiarFormulario();

        } catch (Exception e) {
            mostrarError(
                    "No se pudo eliminar el pedido:\n"
                    + e.getMessage()
            );
        }
    }

    // ==========================================
    // ENVIAR A COCINA
    // ==========================================
    private void enviarACocina() {

        if (pedidoActual == null) {
            mostrarError("No existe un pedido.");
            return;
        }

        try {

            if (pedidoActual.getId() == 0
                    || pedidoActual.isCambiosSinGuardar()) {
                mostrarError("Primero debe guardar el pedido.");
                return;
            }

            pedidoService.enviarACocina(pedidoActual);

            mostrarPedidoActual();

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

    // ==========================================
    // NUEVO PEDIDO
    // ==========================================
    private void nuevoPedido() {

        limpiarFormulario();

        cboMozo.setDisable(false);
        cboMesa.setDisable(false);

        cargarMesas();
    }

    // ==========================================
    // LIMPIAR FORMULARIO
    // ==========================================
    private void limpiarFormulario() {

        pedidoActual = null;

        txtId.clear();
        txtFecha.clear();
        txtEstado.clear();
        txtSubtotal.clear();

        listaDetalles.clear();

        cboMozo.getSelectionModel().clearSelection();
        cboMesa.getSelectionModel().clearSelection();

        btnAgregarPlato.setDisable(true);
        btnAgregarPlato.setVisible(false);
        btnAgregarPlato.setManaged(false);

        cboMozo.setDisable(false);
        cboMesa.setDisable(false);

        cargarMesas();
    }

    // ==========================================
    // MOSTRAR ERROR
    // ==========================================
    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // ==========================================
    // MOSTRAR INFORMACIÓN
    // ==========================================
    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
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
