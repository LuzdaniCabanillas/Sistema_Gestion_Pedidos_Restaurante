package vista;

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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import modelo.Plato;
import service.PlatoService;

public class FrmPlato extends Application {

    private TextField txtId;
    private TextField txtNombre;
    private TextField txtDescripcion;
    private TextField txtPrecio;
    private TextField txtCategoria;
    private ComboBox<String> cboEstado;
    private TextField txtStock;
    private TextField txtImagen;

    private Button btnNuevo;
    private Button btnGuardar;
    private Button btnActualizar;
    private Button btnEliminar;
    private Button btnLimpiar;
    private Button btnVolver;

    private TableView<Plato> tablaPlatos;

    private PlatoService platoService;

    // Colores del diseño.
    private static final String VERDE = "#285943";
    private static final String VERDE_HOVER = "#1D4633";
    private static final String VERDE_CLARO = "#E5F0E7";
    private static final String FONDO = "#F4F7F4";
    private static final String BLANCO = "#FFFFFF";
    private static final String TEXTO = "#26382D";
    private static final String GRIS = "#68776D";
    private static final String BORDE = "#DCE6DE";
    private static final String ROJO = "#A94442";

    @Override
    public void start(Stage stage) {

        platoService = new PlatoService();

        // Título principal.
        Label lblTitulo = new Label("GESTIÓN DE PLATOS");
        lblTitulo.setStyle(
                "-fx-font-size: 25px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        Label lblDescripcion = new Label(
                "Registra, consulta y administra los platos del restaurante."
        );
        lblDescripcion.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: " + GRIS + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        VBox encabezado = new VBox(6, lblTitulo, lblDescripcion);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        encabezado.setPadding(new Insets(18, 22, 18, 22));
        encabezado.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 12;"
        );

        // Campos del formulario.
        txtId = crearCampo();
        txtId.setEditable(false);
        txtId.setPromptText("Automático");

        txtNombre = crearCampo();
        txtNombre.setPromptText("Nombre del plato");

        txtDescripcion = crearCampo();
        txtDescripcion.setPromptText("Descripción del plato");

        txtPrecio = crearCampo();
        txtPrecio.setPromptText("Ej. 18.50");

        txtCategoria = crearCampo();
        txtCategoria.setPromptText("Ej. Entradas, fondos");

        cboEstado = new ComboBox<>();
        cboEstado.getItems().addAll(
                "DISPONIBLE",
                "NO_DISPONIBLE"
        );
        cboEstado.setValue("DISPONIBLE");
        cboEstado.setMaxWidth(Double.MAX_VALUE);
        cboEstado.setStyle(estiloComboBox());

        txtStock = crearCampo();
        txtStock.setPromptText("Cantidad disponible");

        txtImagen = crearCampo();
        txtImagen.setPromptText("Ruta o nombre de la imagen");

        // Formulario.
        GridPane formulario = new GridPane();
        formulario.setHgap(15);
        formulario.setVgap(12);
        formulario.setPadding(new Insets(20));
        formulario.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 12;"
        );

        formulario.add(crearEtiqueta("ID"), 0, 0);
        formulario.add(txtId, 1, 0);

        formulario.add(crearEtiqueta("Nombre"), 0, 1);
        formulario.add(txtNombre, 1, 1);

        formulario.add(crearEtiqueta("Descripción"), 0, 2);
        formulario.add(txtDescripcion, 1, 2);

        formulario.add(crearEtiqueta("Precio (S/)"), 0, 3);
        formulario.add(txtPrecio, 1, 3);

        formulario.add(crearEtiqueta("Categoría"), 2, 0);
        formulario.add(txtCategoria, 3, 0);

        formulario.add(crearEtiqueta("Estado"), 2, 1);
        formulario.add(cboEstado, 3, 1);

        formulario.add(crearEtiqueta("Stock"), 2, 2);
        formulario.add(txtStock, 3, 2);

        formulario.add(crearEtiqueta("Imagen"), 2, 3);
        formulario.add(txtImagen, 3, 3);

        // Columnas adaptables.
        ColumnConstraints colEtiqueta1 = new ColumnConstraints();
        colEtiqueta1.setMinWidth(95);

        ColumnConstraints colCampo1 = new ColumnConstraints();
        colCampo1.setHgrow(Priority.ALWAYS);
        colCampo1.setFillWidth(true);

        ColumnConstraints colEtiqueta2 = new ColumnConstraints();
        colEtiqueta2.setMinWidth(95);

        ColumnConstraints colCampo2 = new ColumnConstraints();
        colCampo2.setHgrow(Priority.ALWAYS);
        colCampo2.setFillWidth(true);

        formulario.getColumnConstraints().addAll(
                colEtiqueta1,
                colCampo1,
                colEtiqueta2,
                colCampo2
        );

        // Botones.
        btnNuevo = new Button("NUEVO");
        btnGuardar = new Button("GUARDAR");
        btnActualizar = new Button("ACTUALIZAR");
        btnEliminar = new Button("ELIMINAR");
        btnLimpiar = new Button("LIMPIAR");
        btnVolver = new Button("VOLVER");

        estilizarBoton(
                btnNuevo,
                "#E7EEE8",
                "#D6E4D8",
                VERDE
        );

        estilizarBoton(
                btnGuardar,
                VERDE,
                VERDE_HOVER,
                BLANCO
        );

        estilizarBoton(
                btnActualizar,
                "#DDEBE0",
                "#C9DECE",
                VERDE
        );

        estilizarBoton(
                btnEliminar,
                "#F8E9E8",
                "#F0D4D2",
                ROJO
        );

        estilizarBoton(
                btnLimpiar,
                "#EEF1EE",
                "#DDE4DE",
                TEXTO
        );

        estilizarBoton(
                btnVolver,
                "#EEF1EE",
                "#DDE4DE",
                TEXTO
        );

        btnVolver.setPrefWidth(100);

        btnVolver.setOnAction(e -> stage.close());

        HBox botones = new HBox(
                10,
                btnNuevo,
                btnGuardar,
                btnActualizar,
                btnEliminar,
                btnLimpiar,
                btnVolver
        );

        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(4, 0, 4, 0));
        botones.setSpacing(10);

        // Título de la tabla.
        Label lblTituloTabla = new Label("PLATOS REGISTRADOS");
        lblTituloTabla.setStyle(
                "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        Label lblAyudaTabla = new Label(
                "Selecciona un plato para ver o modificar sus datos."
        );
        lblAyudaTabla.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: " + GRIS + ";"
        );

        VBox encabezadoTabla = new VBox(
                5,
                lblTituloTabla,
                lblAyudaTabla
        );

        // Tabla de platos.
        tablaPlatos = new TableView<>();

        tablaPlatos.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tablaPlatos.setPlaceholder(
                new Label("No hay platos registrados.")
        );

        tablaPlatos.setPrefHeight(300);
        tablaPlatos.setMinHeight(200);

        tablaPlatos.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-control-inner-background: " + BLANCO + ";"
                + "-fx-control-inner-background-alt: #F8FAF8;"
                + "-fx-table-cell-border-color: #E8EEE9;"
                + "-fx-selection-bar: " + VERDE_CLARO + ";"
                + "-fx-selection-bar-non-focused: " + VERDE_CLARO + ";"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
                + "-fx-font-size: 12px;"
        );

        TableColumn<Plato, Integer> colId
                = new TableColumn<>("ID");

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );
        colId.setMinWidth(50);

        TableColumn<Plato, String> colNombre
                = new TableColumn<>("Nombre");

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );
        colNombre.setMinWidth(130);

        TableColumn<Plato, String> colDescripcion
                = new TableColumn<>("Descripción");

        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion")
        );
        colDescripcion.setMinWidth(180);

        TableColumn<Plato, Double> colPrecio
                = new TableColumn<>("Precio (S/)");

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio")
        );
        colPrecio.setMinWidth(90);

        TableColumn<Plato, String> colCategoria
                = new TableColumn<>("Categoría");

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria")
        );
        colCategoria.setMinWidth(110);

        TableColumn<Plato, String> colEstado
                = new TableColumn<>("Estado");

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );
        colEstado.setMinWidth(130);

        TableColumn<Plato, Integer> colStock
                = new TableColumn<>("Stock");

        colStock.setCellValueFactory(
                new PropertyValueFactory<>("stock")
        );
        colStock.setMinWidth(70);

        tablaPlatos.getColumns().addAll(
                colId,
                colNombre,
                colDescripcion,
                colPrecio,
                colCategoria,
                colEstado,
                colStock
        );

        // Al seleccionar un plato, mostrar sus datos.
        tablaPlatos.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, anterior, seleccionado) -> {
                            if (seleccionado != null) {
                                mostrarPlato(seleccionado);
                            }
                        }
                );

        // Acciones de los botones.
        btnNuevo.setOnAction(e -> nuevoPlato());
        btnGuardar.setOnAction(e -> guardarPlato());
        btnActualizar.setOnAction(e -> actualizarPlato());
        btnEliminar.setOnAction(e -> eliminarPlato());
        btnLimpiar.setOnAction(e -> limpiarCampos());

        // Contenedor principal.
        VBox principal = new VBox(
                15,
                encabezado,
                formulario,
                botones,
                encabezadoTabla,
                tablaPlatos
        );

        principal.setPadding(new Insets(18, 22, 22, 22));
        principal.setStyle(
                "-fx-background-color: " + FONDO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        VBox.setVgrow(tablaPlatos, Priority.ALWAYS);

        cargarPlatos();

        Scene scene = new Scene(principal, 1150, 760);

        stage.setTitle("Sistema de Gestión de Pedidos - Gestión de Platos");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(680);
        stage.show();
    }

    // Estilo para las etiquetas.
    private Label crearEtiqueta(String texto) {
        Label etiqueta = new Label(texto);

        etiqueta.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
        );

        return etiqueta;
    }

    // Estilo reutilizable para los campos.
    private TextField crearCampo() {
        TextField campo = new TextField();

        campo.setMaxWidth(Double.MAX_VALUE);
        campo.setPrefHeight(36);

        campo.setStyle(
                "-fx-background-color: #F7F9F7;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-prompt-text-fill: #89958C;"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 7;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 7;"
                + "-fx-padding: 9 10 9 10;"
        );

        return campo;
    }

    // Estilo del selector de estado.
    private String estiloComboBox() {
        return "-fx-background-color: " + BLANCO + ";"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 7;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 7;"
                + "-fx-padding: 3;";
    }

    // Estilo reutilizable para los botones y su efecto hover.
    private void estilizarBoton(
            Button boton,
            String colorFondo,
            String colorHover,
            String colorTexto) {

        boton.setMinHeight(40);
        boton.setPrefHeight(42);
        boton.setPadding(new Insets(10, 15, 10, 15));

        boton.setStyle(
                "-fx-background-color: " + colorFondo + ";"
                + "-fx-text-fill: " + colorTexto + ";"
                + "-fx-font-size: 11px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-radius: 8;"
                + "-fx-cursor: hand;"
        );

        boton.setOnMouseEntered(e -> {
            if (!boton.isDisabled()) {
                boton.setStyle(
                        "-fx-background-color: " + colorHover + ";"
                        + "-fx-text-fill: " + colorTexto + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-radius: 8;"
                        + "-fx-cursor: hand;"
                );
            }
        });

        boton.setOnMouseExited(e -> {
            boton.setStyle(
                    "-fx-background-color: " + colorFondo + ";"
                    + "-fx-text-fill: " + colorTexto + ";"
                    + "-fx-font-size: 11px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-background-radius: 8;"
                    + "-fx-border-radius: 8;"
                    + "-fx-cursor: hand;"
            );
        });
    }

    private void cargarPlatos() {

        ObservableList<Plato> lista
                = FXCollections.observableArrayList(
                        platoService.listarPlatos()
                );

        tablaPlatos.setItems(lista);
    }

    private void mostrarPlato(Plato plato) {

        txtId.setText(String.valueOf(plato.getId()));
        txtNombre.setText(plato.getNombre());
        txtDescripcion.setText(plato.getDescripcion());
        txtPrecio.setText(String.valueOf(plato.getPrecio()));
        txtCategoria.setText(plato.getCategoria());
        cboEstado.setValue(plato.getEstado());
        txtStock.setText(String.valueOf(plato.getStock()));
        txtImagen.setText(plato.getImagen());
    }

    private void nuevoPlato() {

        limpiarCampos();
        txtNombre.requestFocus();
    }

    private void guardarPlato() {

        try {

            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            double precio = Double.parseDouble(
                    txtPrecio.getText().trim()
            );

            String categoria = txtCategoria.getText().trim();
            String estado = cboEstado.getValue();

            int stock = Integer.parseInt(
                    txtStock.getText().trim()
            );

            String imagen = txtImagen.getText().trim();

            boolean resultado = platoService.registrarPlato(
                    nombre,
                    descripcion,
                    precio,
                    categoria,
                    estado,
                    stock,
                    imagen
            );

            if (resultado) {

                mostrarMensaje(
                        "Éxito",
                        "El plato fue registrado correctamente."
                );

                cargarPlatos();
                limpiarCampos();

            } else {

                mostrarMensaje(
                        "Error",
                        "No se pudo registrar el plato."
                );
            }

        } catch (NumberFormatException e) {

            mostrarMensaje(
                    "Error",
                    "Precio y stock deben contener números válidos."
            );
        }
    }

    private void actualizarPlato() {

        try {

            if (txtId.getText().isEmpty()) {
                mostrarMensaje(
                        "Advertencia",
                        "Seleccione un plato de la tabla."
                );
                return;
            }

            int id = Integer.parseInt(txtId.getText());

            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            double precio = Double.parseDouble(
                    txtPrecio.getText().trim()
            );

            String categoria = txtCategoria.getText().trim();
            String estado = cboEstado.getValue();

            int stock = Integer.parseInt(
                    txtStock.getText().trim()
            );

            String imagen = txtImagen.getText().trim();

            boolean resultado = platoService.actualizarPlato(
                    id,
                    nombre,
                    descripcion,
                    precio,
                    categoria,
                    estado,
                    stock,
                    imagen
            );

            if (resultado) {

                mostrarMensaje(
                        "Éxito",
                        "El plato fue actualizado correctamente."
                );

                cargarPlatos();
                limpiarCampos();

            } else {

                mostrarMensaje(
                        "Error",
                        "No se pudo actualizar el plato."
                );
            }

        } catch (NumberFormatException e) {

            mostrarMensaje(
                    "Error",
                    "Precio y stock deben contener números válidos."
            );
        }
    }

    private void eliminarPlato() {

        if (txtId.getText().isEmpty()) {

            mostrarMensaje(
                    "Advertencia",
                    "Seleccione un plato de la tabla."
            );

            return;
        }

        try {

            int id = Integer.parseInt(txtId.getText());

            boolean resultado = platoService.eliminarPlato(id);

            if (resultado) {

                mostrarMensaje(
                        "Éxito",
                        "El plato fue eliminado correctamente."
                );

                cargarPlatos();
                limpiarCampos();

            } else {

                mostrarMensaje(
                        "Error",
                        "No se pudo eliminar el plato."
                );
            }

        } catch (NumberFormatException e) {

            mostrarMensaje(
                    "Error",
                    "El ID no es válido."
            );
        }
    }

    private void limpiarCampos() {

        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtCategoria.clear();

        cboEstado.setValue("DISPONIBLE");

        txtStock.clear();
        txtImagen.clear();

        tablaPlatos.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(
            String titulo,
            String mensaje) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        alert.showAndWait();
    }

    public void mostrar() {
        Stage nuevaVentana = new Stage();
        start(nuevaVentana);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
