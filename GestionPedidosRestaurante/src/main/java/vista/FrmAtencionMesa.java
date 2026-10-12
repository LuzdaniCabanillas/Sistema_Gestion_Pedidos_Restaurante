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

import modelo.AtencionMesa;
import modelo.Mesa;
import modelo.Mozo;

import service.AtencionMesaService;
import controlador.MozoDAO;

import java.time.LocalDateTime;

public class FrmAtencionMesa extends Application {

    private TextField txtId;
    private ComboBox<Mesa> cboMesa;
    private ComboBox<Mozo> cboMozo;

    private TextField txtEstado;
    private TextField txtFechaInicio;
    private TextField txtFechaFin;

    private Button btnNuevo;
    private Button btnIniciar;
    private Button btnActualizar;
    private Button btnFinalizar;
    private Button btnEliminar;
    private Button btnVolver;

    private TableView<AtencionMesa> tablaAtenciones;

    private AtencionMesaService atencionService;
    private MozoDAO mozoDAO;

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

        atencionService = new AtencionMesaService();
        mozoDAO = new MozoDAO();

        // Título principal.
        Label lblTitulo = new Label("GESTIÓN DE ATENCIÓN DE MESAS");
        lblTitulo.setStyle(
                "-fx-font-size: 25px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        Label lblDescripcion = new Label(
                "Administra el inicio, seguimiento y finalización "
                + "de la atención de cada mesa."
        );
        lblDescripcion.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: " + GRIS + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        VBox encabezado = new VBox(6, lblTitulo, lblDescripcion);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        encabezado.setPadding(new Insets(20, 24, 20, 24));
        encabezado.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 12;"
        );

        // Etiquetas del formulario.
        Label lblId = crearEtiqueta("ID de atención");
        Label lblMesa = crearEtiqueta("Mesa");
        Label lblMozo = crearEtiqueta("Mozo");
        Label lblEstado = crearEtiqueta("Estado");
        Label lblFechaInicio = crearEtiqueta("Fecha de inicio");
        Label lblFechaFin = crearEtiqueta("Fecha de finalización");

        // Campos del formulario.
        txtId = crearCampo();
        cboMesa = new ComboBox<>();
        cboMozo = new ComboBox<>();

        cboMesa.setPromptText("Seleccione una mesa");
        cboMozo.setPromptText("Seleccione un mozo");

        cboMesa.setMaxWidth(Double.MAX_VALUE);
        cboMozo.setMaxWidth(Double.MAX_VALUE);

        cboMesa.setStyle(estiloComboBox());
        cboMozo.setStyle(estiloComboBox());

        ObservableList<Mozo> listaMozos
                = FXCollections.observableArrayList(
                        mozoDAO.listar()
                );
        cboMozo.setItems(listaMozos);

        txtEstado = crearCampo();
        txtFechaInicio = crearCampo();
        txtFechaFin = crearCampo();

        // Campos que no se escriben manualmente.
        txtId.setEditable(false);
        txtEstado.setEditable(false);
        txtFechaInicio.setEditable(false);
        txtFechaFin.setEditable(false);

        // Formulario.
        GridPane formulario = new GridPane();
        formulario.setHgap(18);
        formulario.setVgap(14);
        formulario.setPadding(new Insets(20));
        formulario.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 12;"
        );

        formulario.add(lblId, 0, 0);
        formulario.add(txtId, 1, 0);

        formulario.add(lblMesa, 0, 1);
        formulario.add(cboMesa, 1, 1);

        formulario.add(lblMozo, 0, 2);
        formulario.add(cboMozo, 1, 2);

        formulario.add(lblEstado, 0, 3);
        formulario.add(txtEstado, 1, 3);

        formulario.add(lblFechaInicio, 2, 0);
        formulario.add(txtFechaInicio, 3, 0);

        formulario.add(lblFechaFin, 2, 1);
        formulario.add(txtFechaFin, 3, 1);

        // Ajustar el ancho de las columnas de controles.
        ColumnConstraints columnaEtiqueta1 = new ColumnConstraints();
        columnaEtiqueta1.setMinWidth(125);

        ColumnConstraints columnaControl1 = new ColumnConstraints();
        columnaControl1.setHgrow(Priority.ALWAYS);
        columnaControl1.setFillWidth(true);

        ColumnConstraints columnaEtiqueta2 = new ColumnConstraints();
        columnaEtiqueta2.setMinWidth(145);

        ColumnConstraints columnaControl2 = new ColumnConstraints();
        columnaControl2.setHgrow(Priority.ALWAYS);
        columnaControl2.setFillWidth(true);

        formulario.getColumnConstraints().addAll(
                columnaEtiqueta1,
                columnaControl1,
                columnaEtiqueta2,
                columnaControl2
        );

        // Botones.
        btnNuevo = new Button("NUEVO");
        btnIniciar = new Button("INICIAR");
        btnActualizar = new Button("ACTUALIZAR");
        btnFinalizar = new Button("FINALIZAR");
        btnEliminar = new Button("ELIMINAR");
        btnVolver = new Button("VOLVER");

        estilizarBoton(
                btnNuevo,
                "#E7EEE8",
                "#D6E4D8",
                VERDE
        );

        estilizarBoton(
                btnIniciar,
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
                btnFinalizar,
                "#477B58",
                "#356746",
                BLANCO
        );

        estilizarBoton(
                btnEliminar,
                "#F8E9E8",
                "#F0D4D2",
                ROJO
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
                btnIniciar,
                btnActualizar,
                btnFinalizar,
                btnEliminar,
                btnVolver
        );

        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(8, 0, 8, 0));
        botones.setSpacing(10);

        // Título de la tabla.
        Label lblTituloTabla = new Label("HISTORIAL DE ATENCIONES");
        lblTituloTabla.setStyle(
                "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        Label lblAyudaTabla = new Label(
                "Selecciona una atención para consultar sus datos."
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

        // Tabla.
        tablaAtenciones = new TableView<>();

        tablaAtenciones.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tablaAtenciones.setPlaceholder(
                new Label("No hay atenciones registradas.")
        );

        tablaAtenciones.setPrefHeight(300);
        tablaAtenciones.setMinHeight(200);

        tablaAtenciones.setStyle(
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

        TableColumn<AtencionMesa, Integer> colId
                = new TableColumn<>("ID");

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );
        colId.setMinWidth(55);

        TableColumn<AtencionMesa, Integer> colMesa
                = new TableColumn<>("Mesa");

        colMesa.setCellValueFactory(
                new PropertyValueFactory<>("mesaId")
        );
        colMesa.setMinWidth(65);

        TableColumn<AtencionMesa, Integer> colMozo
                = new TableColumn<>("Mozo");

        colMozo.setCellValueFactory(
                new PropertyValueFactory<>("mozoId")
        );
        colMozo.setMinWidth(65);

        TableColumn<AtencionMesa, LocalDateTime> colInicio
                = new TableColumn<>("Fecha de inicio");

        colInicio.setCellValueFactory(
                new PropertyValueFactory<>("fechaInicio")
        );
        colInicio.setMinWidth(170);

        TableColumn<AtencionMesa, LocalDateTime> colFin
                = new TableColumn<>("Fecha de finalización");

        colFin.setCellValueFactory(
                new PropertyValueFactory<>("fechaFin")
        );
        colFin.setMinWidth(185);

        TableColumn<AtencionMesa, String> colEstado
                = new TableColumn<>("Estado");

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );
        colEstado.setMinWidth(100);

        tablaAtenciones.getColumns().addAll(
                colId,
                colMesa,
                colMozo,
                colInicio,
                colFin,
                colEstado
        );

        // Contenedor principal.
        VBox root = new VBox(
                16,
                encabezado,
                formulario,
                botones,
                encabezadoTabla,
                tablaAtenciones
        );

        root.setPadding(new Insets(18, 24, 24, 24));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle(
                "-fx-background-color: " + FONDO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        VBox.setVgrow(tablaAtenciones, Priority.ALWAYS);

        // Escena.
        Scene scene = new Scene(root, 1100, 760);

        stage.setTitle("Gestión de Atención de Mesas");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(680);
        stage.show();

        cargarMesas();
        cargarMozos();
        cargarAtenciones();

        // Seleccionar una fila.
        tablaAtenciones.setOnMouseClicked(event -> {

            AtencionMesa seleccionada
                    = tablaAtenciones
                            .getSelectionModel()
                            .getSelectedItem();

            if (seleccionada != null) {

                txtId.setText(
                        String.valueOf(seleccionada.getId())
                );

                seleccionarMesa(seleccionada.getMesaId());
                seleccionarMozo(seleccionada.getMozoId());

                txtEstado.setText(seleccionada.getEstado());

                if (seleccionada.getFechaInicio() != null) {
                    txtFechaInicio.setText(
                            seleccionada.getFechaInicio().toString()
                    );
                } else {
                    txtFechaInicio.clear();
                }

                if (seleccionada.getFechaFin() != null) {
                    txtFechaFin.setText(
                            seleccionada.getFechaFin().toString()
                    );
                } else {
                    txtFechaFin.clear();
                }
            }
        });

        // Acciones de los botones.
        btnNuevo.setOnAction(event -> limpiarCampos());

        btnIniciar.setOnAction(event -> iniciarAtencion());

        btnActualizar.setOnAction(event -> actualizarAtencion());

        btnFinalizar.setOnAction(event -> finalizarAtencion());

        btnEliminar.setOnAction(event -> eliminarAtencion());
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

    // Estilo para los campos de texto.
    private TextField crearCampo() {
        TextField campo = new TextField();

        campo.setPrefWidth(200);
        campo.setMaxWidth(Double.MAX_VALUE);

        campo.setStyle(
                "-fx-background-color: #F7F9F7;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 7;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 7;"
                + "-fx-padding: 9 10 9 10;"
        );

        return campo;
    }

    // Estilo para los ComboBox.
    private String estiloComboBox() {
        return "-fx-background-color: " + BLANCO + ";"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 7;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 7;"
                + "-fx-padding: 3;";
    }

    // Estilo reutilizable para los botones.
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

    private void cargarAtenciones() {

        ObservableList<AtencionMesa> lista
                = FXCollections.observableArrayList(
                        atencionService.listarAtenciones()
                );

        tablaAtenciones.setItems(lista);
    }

    private void iniciarAtencion() {

        Mesa mesaSeleccionada = cboMesa.getValue();
        Mozo mozoSeleccionado = cboMozo.getValue();

        if (mesaSeleccionada == null) {
            mostrarMensaje("Aviso", "Selecciona una mesa.");
            return;
        }

        if (mozoSeleccionado == null) {
            mostrarMensaje("Aviso", "Selecciona un mozo.");
            return;
        }

        boolean resultado = atencionService.iniciarAtencion(
                mesaSeleccionada.getId(),
                mozoSeleccionado.getId()
        );

        if (resultado) {

            mostrarMensaje(
                    "Éxito",
                    "La atención se inició correctamente."
            );

            cargarMesas();
            cargarMozos();
            cargarAtenciones();
            limpiarCampos();

        } else {
            mostrarMensaje(
                    "Aviso",
                    "No se pudo iniciar la atención."
            );
        }
    }

    private void actualizarAtencion() {

        try {

            if (txtId.getText().isEmpty()) {
                mostrarMensaje("Aviso", "Selecciona una atención.");
                return;
            }

            Mesa mesaSeleccionada = cboMesa.getValue();
            Mozo mozoSeleccionado = cboMozo.getValue();

            if (mesaSeleccionada == null || mozoSeleccionado == null) {
                mostrarMensaje(
                        "Aviso",
                        "Selecciona una mesa y un mozo."
                );
                return;
            }

            int id = Integer.parseInt(txtId.getText());

            boolean resultado = atencionService.actualizarAtencion(
                    id,
                    mesaSeleccionada.getId(),
                    mozoSeleccionado.getId()
            );

            if (resultado) {

                mostrarMensaje(
                        "Éxito",
                        "La atención se actualizó correctamente."
                );

                cargarAtenciones();
                limpiarCampos();

            } else {
                mostrarMensaje(
                        "Aviso",
                        "No se pudo actualizar la atención."
                );
            }

        } catch (NumberFormatException e) {
            mostrarMensaje(
                    "Error",
                    "Los datos deben ser numéricos."
            );
        }
    }

    private void finalizarAtencion() {

        if (txtId.getText().isEmpty()) {
            mostrarMensaje("Aviso", "Selecciona una atención.");
            return;
        }

        int id = Integer.parseInt(txtId.getText());

        boolean resultado = atencionService.finalizarAtencion(id);

        if (resultado) {

            mostrarMensaje(
                    "Éxito",
                    "La atención fue finalizada."
            );

            cargarAtenciones();
            limpiarCampos();

        } else {
            mostrarMensaje(
                    "Aviso",
                    "No se pudo finalizar la atención."
            );
        }
    }

    private void eliminarAtencion() {

        if (txtId.getText().isEmpty()) {
            mostrarMensaje("Aviso", "Selecciona una atención.");
            return;
        }

        int id = Integer.parseInt(txtId.getText());

        boolean resultado = atencionService.eliminarAtencion(id);

        if (resultado) {

            mostrarMensaje(
                    "Éxito",
                    "La atención fue eliminada."
            );

            cargarAtenciones();
            limpiarCampos();

        } else {
            mostrarMensaje(
                    "Aviso",
                    "No se puede eliminar esta atención."
            );
        }
    }

    private void limpiarCampos() {

        txtId.clear();

        cboMesa.getSelectionModel().clearSelection();
        cboMozo.getSelectionModel().clearSelection();

        txtEstado.clear();
        txtFechaInicio.clear();
        txtFechaFin.clear();

        tablaAtenciones.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle(titulo);
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

    private void seleccionarMesa(int mesaId) {

        Mesa mesaEncontrada = null;

        for (Mesa mesa : cboMesa.getItems()) {
            if (mesa.getId() == mesaId) {
                mesaEncontrada = mesa;
                break;
            }
        }

        if (mesaEncontrada == null) {
            Mesa mesa = atencionService.buscarMesa(mesaId);

            if (mesa != null) {
                mesaEncontrada = mesa;
                cboMesa.getItems().add(mesa);
            }
        }

        if (mesaEncontrada != null) {
            cboMesa.setValue(mesaEncontrada);
        }
    }

    private void seleccionarMozo(int mozoId) {

        for (Mozo mozo : cboMozo.getItems()) {
            if (mozo.getId() == mozoId) {
                cboMozo.setValue(mozo);
                break;
            }
        }
    }

    private void cargarMesas() {

        ObservableList<Mesa> listaMesas
                = FXCollections.observableArrayList();

        for (Mesa mesa : atencionService.listarMesas()) {
            if (mesa.estaDisponible()) {
                listaMesas.add(mesa);
            }
        }

        cboMesa.setItems(listaMesas);
    }

    private void cargarMozos() {

        ObservableList<Mozo> listaMozos
                = FXCollections.observableArrayList(
                        mozoDAO.listar()
                );

        cboMozo.setItems(listaMozos);
    }
}
