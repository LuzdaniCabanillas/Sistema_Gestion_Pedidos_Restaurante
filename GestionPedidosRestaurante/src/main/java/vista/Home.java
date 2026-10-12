package vista;

import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Plato;
import service.PedidoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class Home {

    private final Pedido pedidoActual;
    private final PedidoService pedidoService;

    private Stage stage;

    private TableView<Plato> tablaPlatos;

    private TextField txtBuscarId;
    private TextField txtBuscarNombre;

    private Button btnBuscarId;
    private Button btnBuscarNombre;
    private Button btnAgregar;

    private Label lblPedido;

    private final ObservableList<Plato> listaPlatos
            = FXCollections.observableArrayList();

    // PALETA DE COLORES
    private static final String VERDE_OSCURO = "#183D32";
    private static final String VERDE = "#28785B";
    private static final String VERDE_HOVER = "#35966F";
    private static final String FONDO = "#F3F6F4";
    private static final String TEXTO = "#24352D";
    private static final String GRIS = "#64746B";
    private static final String BLANCO = "#FFFFFF";

    public Home() {
        this(null);
    }

    public Home(Pedido pedidoActual) {
        this.pedidoActual = pedidoActual;
        this.pedidoService = new PedidoService();
    }

    public void mostrar() {
        construirVentana(false);
    }

    public void mostrarModal() {
        construirVentana(true);
    }

    // =====================================================
    // CONSTRUIR VENTANA PRINCIPAL
    // =====================================================
    private void construirVentana(boolean esperarCierre) {

        stage = new Stage();

        // TÍTULO
        Label titulo = new Label("CARTA DEL RESTAURANTE");
        titulo.setStyle(
                "-fx-font-size: 25px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE_OSCURO + ";"
        );

        Label subtitulo = new Label(
                "Explora nuestros platos y encuentra tu favorito"
        );
        subtitulo.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: " + GRIS + ";"
        );

        // INFORMACIÓN DEL PEDIDO
        lblPedido = new Label();
        actualizarInformacionPedido();

        lblPedido.setMaxWidth(Double.MAX_VALUE);
        lblPedido.setPadding(new Insets(12));
        lblPedido.setStyle(
                "-fx-background-color: #E5F1E9;"
                + "-fx-background-radius: 8;"
                + "-fx-font-size: 13px;"
                + "-fx-text-fill: " + VERDE_OSCURO + ";"
                + "-fx-font-weight: bold;"
        );

        // BUSCAR POR ID
        txtBuscarId = new TextField();
        txtBuscarId.setPromptText("Ingrese el ID");
        txtBuscarId.setPrefWidth(160);
        estilizarCampo(txtBuscarId);

        btnBuscarId = new Button("BUSCAR");
        estilizarBoton(btnBuscarId, "primario");
        btnBuscarId.setOnAction(e -> buscarPorId());

        Label lblId = new Label("ID del plato");
        estilizarEtiqueta(lblId);

        HBox buscarId = new HBox(
                12, lblId, txtBuscarId, btnBuscarId
        );
        buscarId.setAlignment(Pos.CENTER_LEFT);

        // BUSCAR POR NOMBRE
        txtBuscarNombre = new TextField();
        txtBuscarNombre.setPromptText("Ej. Lomo Saltado");
        txtBuscarNombre.setPrefWidth(220);
        estilizarCampo(txtBuscarNombre);

        btnBuscarNombre = new Button("BUSCAR");
        estilizarBoton(btnBuscarNombre, "primario");
        btnBuscarNombre.setOnAction(e -> buscarPorNombre());

        Label lblNombre = new Label("Nombre del plato");
        estilizarEtiqueta(lblNombre);

        HBox buscarNombre = new HBox(
                12, lblNombre, txtBuscarNombre, btnBuscarNombre
        );
        buscarNombre.setAlignment(Pos.CENTER_LEFT);

        // =================================================
        // TABLA DE PLATOS
        // =================================================
        tablaPlatos = new TableView<>();
        tablaPlatos.setItems(listaPlatos);
        tablaPlatos.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        tablaPlatos.setPlaceholder(
                new Label("No hay platos para mostrar.")
        );

        tablaPlatos.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #DCE6DF;"
                + "-fx-border-radius: 10;"
                + "-fx-border-width: 1;"
                + "-fx-font-size: 12px;"
        );

        TableColumn<Plato, Integer> colId
                = new TableColumn<>("ID");

        TableColumn<Plato, String> colImagen
                = new TableColumn<>("Imagen");

        TableColumn<Plato, String> colNombre
                = new TableColumn<>("Nombre");

        TableColumn<Plato, String> colDescripcion
                = new TableColumn<>("Descripción");

        TableColumn<Plato, Double> colPrecio
                = new TableColumn<>("Precio");

        TableColumn<Plato, String> colCategoria
                = new TableColumn<>("Categoría");

        TableColumn<Plato, String> colEstado
                = new TableColumn<>("Estado");

        TableColumn<Plato, Integer> colStock
                = new TableColumn<>("Stock");

        // VALORES DE LAS COLUMNAS
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colImagen.setCellValueFactory(
                new PropertyValueFactory<>("imagen")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion")
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio")
        );

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria")
        );

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        colStock.setCellValueFactory(
                new PropertyValueFactory<>("stock")
        );

        // MOSTRAR IMÁGENES
        colImagen.setCellFactory(columna
                -> new TableCell<Plato, String>() {

            private final ImageView vistaImagen = new ImageView();

            @Override
            protected void updateItem(String ruta, boolean vacio) {
                super.updateItem(ruta, vacio);

                if (vacio || ruta == null || ruta.isBlank()) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                try {
                    java.net.URL recurso
                            = Home.class.getResource(ruta);

                    if (recurso == null) {
                        setGraphic(null);
                        setText("Sin imagen");
                        return;
                    }

                    Image imagen = new Image(
                            recurso.toExternalForm()
                    );

                    vistaImagen.setImage(imagen);
                    vistaImagen.setFitWidth(75);
                    vistaImagen.setFitHeight(55);
                    vistaImagen.setPreserveRatio(true);
                    vistaImagen.setSmooth(true);

                    setGraphic(vistaImagen);
                    setText(null);
                    setAlignment(Pos.CENTER);

                } catch (Exception ex) {
                    setGraphic(null);
                    setText("Error de imagen");
                }
            }
        });

        // TAMAÑOS DE COLUMNAS
        colId.setPrefWidth(50);
        colImagen.setPrefWidth(95);
        colNombre.setPrefWidth(145);
        colDescripcion.setPrefWidth(200);
        colPrecio.setPrefWidth(85);
        colCategoria.setPrefWidth(110);
        colEstado.setPrefWidth(100);
        colStock.setPrefWidth(65);

        tablaPlatos.getColumns().addAll(
                colId,
                colImagen,
                colNombre,
                colDescripcion,
                colPrecio,
                colCategoria,
                colEstado,
                colStock
        );

        // =================================================
        // BOTÓN AGREGAR
        // =================================================
        btnAgregar = new Button("＋  AGREGAR AL PEDIDO");
        btnAgregar.setPrefWidth(230);
        btnAgregar.setPrefHeight(42);

        estilizarBoton(btnAgregar, "primario");

        btnAgregar.setDisable(
                pedidoActual == null
                || (!"PENDIENTE".equalsIgnoreCase(
                        pedidoActual.getEstado())
                && !"EN_PREPARACION".equalsIgnoreCase(
                        pedidoActual.getEstado()))
        );

        btnAgregar.setOnAction(
                e -> agregarPlatoAlPedido()
        );

        HBox panelAgregar = new HBox(btnAgregar);
        panelAgregar.setAlignment(Pos.CENTER);
        panelAgregar.setPadding(new Insets(16));
        panelAgregar.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-border-color: #E1E9E3;"
                + "-fx-border-width: 1 0 0 0;"
        );

        // =================================================
        // CABECERA
        // =================================================
        VBox superior = new VBox(
                9,
                titulo,
                subtitulo,
                lblPedido,
                buscarId,
                buscarNombre
        );

        superior.setPadding(new Insets(22, 25, 18, 25));
        superior.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-border-color: #E1E9E3;"
                + "-fx-border-width: 0 0 1 0;"
        );

        // =================================================
        // CONTENEDOR PRINCIPAL
        // =================================================
        BorderPane root = new BorderPane();
        root.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        // =================================================
        // MENÚ LATERAL
        // =================================================
        if (!esperarCierre) {

            Label marca = new Label("RESTAURANTE");
            marca.setStyle(
                    "-fx-font-size: 17px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: white;"
            );

            Label descripcionMarca = new Label("SISTEMA DE GESTIÓN");
            descripcionMarca.setStyle(
                    "-fx-font-size: 10px;"
                    + "-fx-text-fill: #C7DED2;"
            );

            VBox encabezadoMenu = new VBox(
                    5, marca, descripcionMarca
            );
            encabezadoMenu.setPadding(
                    new Insets(8, 0, 20, 5)
            );

            Button btnInicio = new Button("⌂   Carta");
            Button btnPedidos = new Button("＋   Tomar pedido");
            Button btnSeguimientoPedidos
                    = new Button("◷   Seguimiento de pedidos");
            Button btnAtencion
                    = new Button("▣   Atención de mesas");
            Button btnMesas
                    = new Button("▦   Gestión de mesas");
            Button btnPlatos
                    = new Button("☷   Gestión de platos");
            Button btnCierrePedidos
                    = new Button("✓   Cierre de pedidos");
            Button btnContraer = new Button("☰");

            VBox opcionesMenu = new VBox(
                    7,
                    btnInicio,
                    btnPedidos,
                    btnSeguimientoPedidos,
                    btnAtencion,
                    btnMesas,
                    btnPlatos,
                    btnCierrePedidos
            );

            opcionesMenu.setFillWidth(true);
            opcionesMenu.setSpacing(7);

            VBox menuLateral = new VBox(
                    12,
                    encabezadoMenu,
                    btnContraer,
                    opcionesMenu
            );

            menuLateral.setPadding(new Insets(14, 10, 14, 10));
            menuLateral.setPrefWidth(245);
            menuLateral.setMinWidth(245);
            menuLateral.setMaxWidth(245);
            menuLateral.setFillWidth(true);
            menuLateral.setStyle(
                    "-fx-background-color: " + VERDE_OSCURO + ";"
            );

            // ESTILO DE LOS BOTONES DEL MENÚ
            for (Button boton : new Button[]{
                btnInicio,
                btnPedidos,
                btnSeguimientoPedidos,
                btnAtencion,
                btnMesas,
                btnPlatos,
                btnCierrePedidos
            }) {
                boton.setMaxWidth(Double.MAX_VALUE);
                boton.setAlignment(Pos.CENTER_LEFT);
                boton.setPrefHeight(36);
                boton.setMinHeight(36);
                boton.setMaxHeight(36);
                estilizarBoton(boton, "menu");
            }

            btnContraer.setMaxWidth(Double.MAX_VALUE);
            btnContraer.setPrefHeight(38);
            estilizarBoton(btnContraer, "menu");

            // ACCIONES ORIGINALES
            btnInicio.setOnAction(e -> mostrarCarta());
            btnPedidos.setOnAction(e -> new FrmPedido().mostrar());
            btnSeguimientoPedidos.setOnAction(
                    e -> new FrmSeguimientoPedidos().mostrar()
            );
            btnAtencion.setOnAction(
                    e -> new FrmAtencionMesa().mostrar()
            );
            btnMesas.setOnAction(e -> new FrmMesa().mostrar());
            btnPlatos.setOnAction(e -> new FrmPlato().mostrar());
            btnCierrePedidos.setOnAction(
                    e -> new FrmCierrePedidos().mostrar()
            );

            // CONTRAER / EXPANDIR MENÚ
            btnContraer.setOnAction(e -> {
                boolean visible = opcionesMenu.isVisible();

                opcionesMenu.setVisible(!visible);
                opcionesMenu.setManaged(!visible);
                encabezadoMenu.setVisible(!visible);
                encabezadoMenu.setManaged(!visible);

                if (visible) {
                    menuLateral.setPrefWidth(65);
                    menuLateral.setMinWidth(65);
                    menuLateral.setMaxWidth(65);
                    btnContraer.setText("☰");
                } else {
                    menuLateral.setPrefWidth(245);
                    menuLateral.setMinWidth(245);
                    menuLateral.setMaxWidth(245);
                    btnContraer.setText("✕");
                }
            });

            root.setLeft(menuLateral);
        }

        root.setTop(superior);
        root.setCenter(tablaPlatos);
        root.setBottom(panelAgregar);

        BorderPane.setMargin(
                tablaPlatos,
                new Insets(20, 25, 20, 25)
        );

        // =================================================
        // ESCENA
        // =================================================
        Scene scene = new Scene(root, 1180, 720);

        stage.setTitle("Restaurante | Carta y pedidos");
        stage.setMinWidth(950);
        stage.setMinHeight(620);
        stage.setScene(scene);

        mostrarCarta();

        if (esperarCierre) {
            stage.showAndWait();
        } else {
            stage.show();
        }
    }

    // =====================================================
    // ESTILOS REUTILIZABLES
    // =====================================================
    private void estilizarBoton(Button boton, String tipo) {

        String normal;
        String hover;

        switch (tipo) {
            case "menu":
                normal
                        = "-fx-background-color: transparent;"
                        + "-fx-text-fill: #E8F1EC;"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 11 12 11 12;"
                        + "-fx-cursor: hand;";

                hover
                        = "-fx-background-color: #2B5948;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 11 12 11 12;"
                        + "-fx-cursor: hand;";
                break;

            case "secundario":
                normal
                        = "-fx-background-color: #E5F1E9;"
                        + "-fx-text-fill: " + VERDE_OSCURO + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 10 16 10 16;"
                        + "-fx-cursor: hand;";

                hover
                        = "-fx-background-color: #D0E6D8;"
                        + "-fx-text-fill: " + VERDE_OSCURO + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 10 16 10 16;"
                        + "-fx-cursor: hand;";
                break;

            default:
                normal
                        = "-fx-background-color: " + VERDE + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 10 17 10 17;"
                        + "-fx-cursor: hand;";

                hover
                        = "-fx-background-color: " + VERDE_HOVER + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 10 17 10 17;"
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

    private void estilizarCampo(TextField campo) {
        campo.setStyle(
                "-fx-background-color: #F8FAF8;"
                + "-fx-border-color: #D5E1D9;"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-padding: 10;"
                + "-fx-font-size: 13px;"
                + "-fx-text-fill: " + TEXTO + ";"
        );
    }

    private void estilizarEtiqueta(Label etiqueta) {
        etiqueta.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
        );
    }

    // =====================================================
    // CARGAR CARTA
    // =====================================================
    private void mostrarCarta() {
        try {
            List<Plato> platos = pedidoService.mostrarCarta();
            listaPlatos.setAll(platos);
        } catch (SQLException e) {
            mostrarError(
                    "No se pudo cargar la carta:\n" + e.getMessage()
            );
        }
    }

    // =====================================================
    // BUSCAR POR ID
    // =====================================================
    private void buscarPorId() {

        String texto = txtBuscarId.getText().trim();

        if (texto.isEmpty()) {
            mostrarError("Ingrese el ID del plato.");
            return;
        }

        try {
            int id = Integer.parseInt(texto);

            Plato plato = pedidoService.buscarPlatoPorId(id);

            listaPlatos.clear();

            if (plato != null) {
                listaPlatos.add(plato);
            } else {
                mostrarInformacion(
                        "No se encontró ningún plato con el ID indicado."
                );
            }

        } catch (NumberFormatException e) {
            mostrarError("El ID debe ser un número.");
        } catch (SQLException e) {
            mostrarError(
                    "Error al buscar el plato:\n" + e.getMessage()
            );
        }
    }

    // =====================================================
    // BUSCAR POR NOMBRE
    // =====================================================
    private void buscarPorNombre() {

        String nombre = txtBuscarNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarError("Ingrese el nombre del plato.");
            return;
        }

        try {
            List<Plato> platos
                    = pedidoService.buscarPlatoPorNombre(nombre);

            listaPlatos.setAll(platos);

            if (platos.isEmpty()) {
                mostrarInformacion(
                        "No se encontraron platos con ese nombre."
                );
            }

        } catch (SQLException e) {
            mostrarError(
                    "Error al buscar los platos:\n" + e.getMessage()
            );
        }
    }

    // =====================================================
    // AGREGAR PLATO AL PEDIDO
    // =====================================================
    private void agregarPlatoAlPedido() {

        if (pedidoActual == null) {
            mostrarError("No existe un pedido activo.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(pedidoActual.getEstado())
                && !"EN_PREPARACION".equalsIgnoreCase(
                        pedidoActual.getEstado())) {
            mostrarError(
                    "El pedido ya no admite más platos "
                    + "(solo Pendiente o En Preparación)."
            );
            return;
        }

        Plato plato = tablaPlatos.getSelectionModel()
                .getSelectedItem();

        if (plato == null) {
            mostrarError("Seleccione un plato de la carta.");
            return;
        }

        try {
            pedidoService.agregarPlato(pedidoActual, plato);
            stage.close();

        } catch (Exception e) {
            mostrarError(
                    "No se pudo agregar el plato:\n" + e.getMessage()
            );
        }
    }

    // =====================================================
    // OBTENER CANTIDAD DE UN PLATO
    // =====================================================
    private int obtenerCantidad(Plato plato) {

        if (pedidoActual == null || plato == null) {
            return 0;
        }

        for (DetallePedido detalle : pedidoActual.getDetalles()) {
            if (detalle.getPlato().getId() == plato.getId()) {
                return detalle.getCantidad();
            }
        }

        return 0;
    }

    // =====================================================
    // INFORMACIÓN DEL PEDIDO
    // =====================================================
    private void actualizarInformacionPedido() {

        if (pedidoActual == null) {
            lblPedido.setText("Modo carta: no hay un pedido activo.");
            return;
        }

        String idPedido = pedidoActual.getId() == 0
                ? "Pendiente de guardar"
                : String.valueOf(pedidoActual.getId());

        String mozo = pedidoActual.getMozo() != null
                ? pedidoActual.getMozo().getNombre()
                : "-";

        String mesa = pedidoActual.getMesa() != null
                ? String.valueOf(pedidoActual.getMesa().getNumero())
                : "-";

        lblPedido.setText(
                "Pedido: " + idPedido
                + "     |     Mozo: " + mozo
                + "     |     Mesa: " + mesa
        );
    }

    // =====================================================
    // MENSAJES
    // =====================================================
    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
