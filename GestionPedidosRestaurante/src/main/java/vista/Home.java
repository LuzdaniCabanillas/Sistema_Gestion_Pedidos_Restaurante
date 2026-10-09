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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//imagen:
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableCell;

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
    private Button btnMostrarCarta;
    private Button btnAgregar;

    private Label lblPedido;

    private final ObservableList<Plato> listaPlatos
            = FXCollections.observableArrayList();

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

    private void construirVentana(boolean esperarCierre) {

        stage = new Stage();

        Label titulo = new Label("HOME - CARTA DEL RESTAURANTE");

        titulo.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        lblPedido = new Label();

        actualizarInformacionPedido();

        txtBuscarId = new TextField();
        txtBuscarId.setPromptText("ID del plato");
        txtBuscarId.setPrefWidth(150);

        btnBuscarId = new Button("BUSCAR POR ID");

        btnBuscarId.setOnAction(
                e -> buscarPorId()
        );

        HBox buscarId = new HBox(
                10,
                new Label("ID:"),
                txtBuscarId,
                btnBuscarId
        );

        buscarId.setAlignment(Pos.CENTER_LEFT);

        txtBuscarNombre = new TextField();
        txtBuscarNombre.setPromptText("Nombre del plato");
        txtBuscarNombre.setPrefWidth(200);

        btnBuscarNombre = new Button("BUSCAR POR NOMBRE");

        btnBuscarNombre.setOnAction(
                e -> buscarPorNombre()
        );

        HBox buscarNombre = new HBox(
                10,
                new Label("Nombre:"),
                txtBuscarNombre,
                btnBuscarNombre
        );

        buscarNombre.setAlignment(Pos.CENTER_LEFT);

        btnMostrarCarta = new Button("MOSTRAR CARTA COMPLETA");

        btnMostrarCarta.setOnAction(
                e -> mostrarCarta()
        );

        tablaPlatos = new TableView<>();

        TableColumn<Plato, Integer> colId
                = new TableColumn<>("ID");

        //imagen<
        TableColumn<Plato, String> colImagen
                = new TableColumn<>("Imagen");

        colImagen.setCellValueFactory(
                new PropertyValueFactory<>("imagen")
        );

        colImagen.setCellFactory(columna -> new TableCell<Plato, String>() {

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
                    java.net.URL recurso = Home.class.getResource(ruta);

                    if (recurso == null) {
                        setGraphic(null);
                        setText("Imagen no encontrada");
                        return;
                    }

                    Image imagen = new Image(recurso.toExternalForm());

                    vistaImagen.setImage(imagen);
                    vistaImagen.setFitWidth(90);
                    vistaImagen.setFitHeight(65);
                    vistaImagen.setPreserveRatio(true);
                    vistaImagen.setSmooth(true);

                    setGraphic(vistaImagen);
                    setText(null);

                } catch (Exception ex) {
                    setGraphic(null);
                    setText("Error de imagen");
                }
            }
        });

        colImagen.setPrefWidth(120);
        //imagen>

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

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
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

        colId.setPrefWidth(60);
        colNombre.setPrefWidth(170);
        colDescripcion.setPrefWidth(220);
        colPrecio.setPrefWidth(100);
        colCategoria.setPrefWidth(120);
        colEstado.setPrefWidth(120);
        colStock.setPrefWidth(80);

        tablaPlatos.getColumns().addAll(
                colId,
                colImagen, //Imagen
                colNombre,
                colDescripcion,
                colPrecio,
                colCategoria,
                colEstado,
                colStock
        );

        tablaPlatos.setItems(listaPlatos);

        btnAgregar = new Button("AGREGAR");

        btnAgregar.setPrefWidth(150);
        btnAgregar.setPrefHeight(35);

        //modificado
        btnAgregar.setDisable(
                pedidoActual == null
                || (!"PENDIENTE".equalsIgnoreCase(pedidoActual.getEstado())
                && !"EN_PREPARACION".equalsIgnoreCase(pedidoActual.getEstado()))
        );

        btnAgregar.setOnAction(
                e -> agregarPlatoAlPedido()
        );

        HBox panelAgregar = new HBox(
                btnAgregar
        );

        panelAgregar.setAlignment(
                Pos.CENTER
        );

        panelAgregar.setPadding(
                new Insets(10)
        );

        HBox botonesCarta = new HBox(
                10,
                btnMostrarCarta
        );

        botonesCarta.setAlignment(
                Pos.CENTER_LEFT
        );

        // PANEL SUPERIOR
        VBox superior = new VBox(
                10,
                titulo,
                lblPedido,
                buscarId,
                buscarNombre,
                botonesCarta
        );

        superior.setPadding(
                new Insets(15)
        );

        // PANEL PRINCIPAL
        BorderPane root = new BorderPane();

        if (!esperarCierre) {

            Button btnInicio = new Button("Inicio / Carta");
            Button btnPedidos = new Button("Tomar pedido");
            Button btnAtencion = new Button("Atención de mesas");
            Button btnMesas = new Button("Gestión de mesas");
            Button btnPlatos = new Button("Gestión de platos");
            Button btnContraer = new Button("☰");

            VBox opcionesMenu = new VBox(
                    12,
                    btnInicio,
                    btnPedidos,
                    btnAtencion,
                    btnMesas,
                    btnPlatos
            );

            VBox menuLateral = new VBox(
                    12,
                    btnContraer,
                    opcionesMenu
            );

            menuLateral.setPadding(new Insets(15));
            menuLateral.setPrefWidth(190);
            menuLateral.setStyle("-fx-background-color: #eeeeee;");

            for (Button boton : new Button[]{
                btnInicio, btnPedidos, btnAtencion, btnMesas, btnPlatos
            }) {
                boton.setMaxWidth(Double.MAX_VALUE);
            }

            btnInicio.setOnAction(e -> mostrarCarta());
            btnPedidos.setOnAction(e -> new FrmPedido().mostrar());
            btnAtencion.setOnAction(e -> new FrmAtencionMesa().mostrar());
            btnMesas.setOnAction(e -> new FrmMesa().mostrar());
            btnPlatos.setOnAction(e -> new FrmPlato().mostrar());

            btnContraer.setOnAction(e -> {
                boolean visible = opcionesMenu.isVisible();

                opcionesMenu.setVisible(!visible);
                opcionesMenu.setManaged(!visible);

                if (visible) {
                    menuLateral.setPrefWidth(55);
                    menuLateral.setMinWidth(55);
                    menuLateral.setMaxWidth(55);
                    btnContraer.setText("☰");
                } else {
                    menuLateral.setPrefWidth(190);
                    menuLateral.setMinWidth(190);
                    menuLateral.setMaxWidth(190);
                    btnContraer.setText("✕");
                }
            });

            root.setLeft(menuLateral);
        }
        //--

        root.setTop(superior);
        root.setCenter(tablaPlatos);
        root.setBottom(panelAgregar);

        BorderPane.setMargin(
                tablaPlatos,
                new Insets(15)
        );

        // ESCENA
        Scene scene = new Scene(
                root,
                1050,
                650
        );

        stage.setTitle(
                "Home - Carta del Restaurante"
        );

        stage.setScene(scene);

        mostrarCarta();

        if (esperarCierre) {
            stage.showAndWait();
        } else {
            stage.show();
        }
    }

    //carta
    private void mostrarCarta() {

        try {

            List<Plato> platos
                    = pedidoService.mostrarCarta();

            listaPlatos.setAll(platos);

        } catch (SQLException e) {

            mostrarError(
                    "No se pudo cargar la carta:\n"
                    + e.getMessage()
            );
        }
    }

    //buscar por Id
    private void buscarPorId() {

        String texto = txtBuscarId.getText().trim();

        if (texto.isEmpty()) {

            mostrarError(
                    "Ingrese el ID del plato."
            );

            return;
        }

        try {

            int id = Integer.parseInt(texto);

            Plato plato
                    = pedidoService.buscarPlatoPorId(id);

            listaPlatos.clear();

            if (plato != null) {

                listaPlatos.add(plato);

            } else {

                mostrarInformacion(
                        "No se encontró ningún plato "
                        + "con el ID indicado."
                );
            }

        } catch (NumberFormatException e) {

            mostrarError(
                    "El ID debe ser un número."
            );

        } catch (SQLException e) {

            mostrarError(
                    "Error al buscar el plato:\n"
                    + e.getMessage()
            );
        }
    }

    //buscar por Nombre
    private void buscarPorNombre() {

        String nombre
                = txtBuscarNombre.getText().trim();

        if (nombre.isEmpty()) {

            mostrarError(
                    "Ingrese el nombre del plato."
            );

            return;
        }

        try {

            List<Plato> platos
                    = pedidoService.buscarPlatoPorNombre(
                            nombre
                    );

            listaPlatos.setAll(platos);

            if (platos.isEmpty()) {

                mostrarInformacion(
                        "No se encontraron platos "
                        + "con ese nombre."
                );
            }

        } catch (SQLException e) {

            mostrarError(
                    "Error al buscar los platos:\n"
                    + e.getMessage()
            );
        }
    }

    //agregar plato
    private void agregarPlatoAlPedido() {

        if (pedidoActual == null) {
            mostrarError("No existe un pedido activo.");
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(pedidoActual.getEstado()) && !"EN_PREPARACION".equalsIgnoreCase(pedidoActual.getEstado())) {
            mostrarError("El pedido ya no admite más platos (Solo Pendiente o En Preparación).");
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

    private int obtenerCantidad(Plato plato) {

        for (DetallePedido detalle
                : pedidoActual.getDetalles()) {

            if (detalle.getPlato().getId()
                    == plato.getId()) {

                return detalle.getCantidad();
            }
        }

        return 0;
    }

    private void actualizarInformacionPedido() {

        if (pedidoActual == null) {
            lblPedido.setText("No hay un pedido activo.");
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
                + " | Mozo: " + mozo
                + " | Mesa: " + mesa
        );

        lblPedido.setStyle(
                "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
        );
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.ERROR
        );

        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
