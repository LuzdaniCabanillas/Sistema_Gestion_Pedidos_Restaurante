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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.Mesa;
import service.MesaService;

public class FrmMesa extends Application {

    private TextField txtId;
    private TextField txtNumero;
    private TextField txtCapacidad;

    private ComboBox<String> cboEstado;

    private Button btnNuevo;
    private Button btnGuardar;
    private Button btnActualizar;
    private Button btnEliminar;
    private Button btnLimpiar;
    private Button btnVolver;

    private TableView<Mesa> tablaMesas;

    private MesaService mesaService;

    // Colores del formulario
    private final String VERDE = "#285943";
    private final String VERDE_HOVER = "#1D4432";
    private final String FONDO = "#F3F6F4";
    private final String BORDE = "#DCE5DF";
    private final String TEXTO = "#26352C";

    @Override
    public void start(Stage stage) {

        mesaService = new MesaService();

        // =========================
        // ENCABEZADO
        // =========================
        Label lblTitulo = new Label("GESTIÓN DE MESAS");
        lblTitulo.setStyle(
                "-fx-font-size: 26px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        Label lblSubtitulo = new Label(
                "Administra los números, capacidades y estados de las mesas"
        );
        lblSubtitulo.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: #718078;"
                + "-fx-font-family: 'Segoe UI';"
        );

        VBox encabezado = new VBox(6, lblTitulo, lblSubtitulo);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        encabezado.setPadding(new Insets(0, 0, 8, 0));

        // =========================
        // FORMULARIO
        // =========================
        Label lblId = crearEtiqueta("ID:");
        Label lblNumero = crearEtiqueta("Número de mesa:");
        Label lblCapacidad = crearEtiqueta("Capacidad:");
        Label lblEstado = crearEtiqueta("Estado:");

        txtId = crearCampo("Se genera automáticamente");
        txtNumero = crearCampo("Ingrese el número de mesa");
        txtCapacidad = crearCampo("Ingrese la capacidad");

        txtId.setEditable(false);
        txtId.setStyle(txtId.getStyle()
                + "-fx-background-color: #E9EEEB;"
                + "-fx-text-fill: #69776F;");

        cboEstado = new ComboBox<>();
        cboEstado.getItems().addAll(
                "DISPONIBLE",
                "OCUPADA"
        );

        cboEstado.setMaxWidth(Double.MAX_VALUE);
        cboEstado.setPrefHeight(38);
        cboEstado.setDisable(true);
        cboEstado.setValue("DISPONIBLE");
        cboEstado.setStyle(
                "-fx-background-color: white;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 13px;"
        );

        GridPane formulario = new GridPane();
        formulario.setHgap(18);
        formulario.setVgap(12);
        formulario.setPadding(new Insets(20));

        formulario.add(lblId, 0, 0);
        formulario.add(txtId, 1, 0);

        formulario.add(lblNumero, 0, 1);
        formulario.add(txtNumero, 1, 1);

        formulario.add(lblCapacidad, 0, 2);
        formulario.add(txtCapacidad, 1, 2);

        formulario.add(lblEstado, 0, 3);
        formulario.add(cboEstado, 1, 3);

        // Distribución de columnas
        formulario.setPrefWidth(700);
        formulario.setMaxWidth(Double.MAX_VALUE);

        for (int i = 0; i < 2; i++) {
            javafx.scene.layout.ColumnConstraints columna
                    = new javafx.scene.layout.ColumnConstraints();
            columna.setPercentWidth(50);
            columna.setHgrow(
                    javafx.scene.layout.Priority.ALWAYS
            );
            formulario.getColumnConstraints().add(columna);
        }

        // Los campos ocupan todo el ancho de su columna
        txtId.setMaxWidth(Double.MAX_VALUE);
        txtNumero.setMaxWidth(Double.MAX_VALUE);
        txtCapacidad.setMaxWidth(Double.MAX_VALUE);

        // =========================
        // BOTONES
        // =========================
        btnNuevo = new Button("NUEVO");
        btnGuardar = new Button("GUARDAR");
        btnActualizar = new Button("ACTUALIZAR");
        btnEliminar = new Button("ELIMINAR");
        btnLimpiar = new Button("LIMPIAR");
        btnVolver = new Button("VOLVER");

        estilizarBoton(btnNuevo, "#64756B", "#526158");
        estilizarBoton(btnGuardar, VERDE, VERDE_HOVER);
        estilizarBoton(btnActualizar, "#39765A", "#2C6047");
        estilizarBoton(btnEliminar, "#B94A48", "#963A38");
        estilizarBoton(btnLimpiar, "#75847B", "#5E6C63");
        estilizarBoton(btnVolver, "#FFFFFF", "#E8EEE9");

        btnVolver.setStyle(btnVolver.getStyle()
                + "-fx-text-fill: " + VERDE + ";"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-width: 1;");

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_LEFT);
        botones.getChildren().addAll(
                btnNuevo,
                btnGuardar,
                btnActualizar,
                btnEliminar,
                btnLimpiar,
                btnVolver
        );

        botones.setPadding(new Insets(5, 0, 0, 0));

        // =========================
        // TABLA DE MESAS
        // =========================
        Label lblTabla = new Label("Listado de mesas");
        lblTabla.setStyle(
                "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        Label lblTablaInfo = new Label(
                "Selecciona una fila para consultar o modificar una mesa."
        );
        lblTablaInfo.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: #718078;"
                + "-fx-font-family: 'Segoe UI';"
        );

        VBox tituloTabla = new VBox(5, lblTabla, lblTablaInfo);

        tablaMesas = new TableView<>();
        tablaMesas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        tablaMesas.setPlaceholder(
                new Label("No hay mesas registradas.")
        );
        tablaMesas.setPrefHeight(280);
        tablaMesas.setMinHeight(200);
        tablaMesas.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 8;"
                + "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 13px;"
        );

        TableColumn<Mesa, Integer> colId
                = new TableColumn<>("ID");

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );
        colId.setPrefWidth(70);
        colId.setMinWidth(55);

        TableColumn<Mesa, Integer> colNumero
                = new TableColumn<>("Número de mesa");

        colNumero.setCellValueFactory(
                new PropertyValueFactory<>("numero")
        );
        colNumero.setPrefWidth(180);
        colNumero.setMinWidth(120);

        TableColumn<Mesa, Integer> colCapacidad
                = new TableColumn<>("Capacidad");

        colCapacidad.setCellValueFactory(
                new PropertyValueFactory<>("capacidad")
        );
        colCapacidad.setPrefWidth(150);
        colCapacidad.setMinWidth(100);

        TableColumn<Mesa, String> colEstado
                = new TableColumn<>("Estado");

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );
        colEstado.setPrefWidth(180);
        colEstado.setMinWidth(120);

        tablaMesas.getColumns().addAll(
                colId,
                colNumero,
                colCapacidad,
                colEstado
        );

        // =========================
        // CONTENEDORES VISUALES
        // =========================
        VBox panelFormulario = new VBox(12);
        panelFormulario.setPadding(new Insets(18));
        panelFormulario.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 12;"
        );

        Label lblFormulario = new Label("Datos de la mesa");
        lblFormulario.setStyle(
                "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        panelFormulario.getChildren().addAll(
                lblFormulario,
                formulario,
                botones
        );

        VBox panelTabla = new VBox(14);
        panelTabla.setPadding(new Insets(18));
        panelTabla.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 12;"
        );
        panelTabla.getChildren().addAll(
                tituloTabla,
                tablaMesas
        );

        VBox root = new VBox(18);
        root.setPadding(new Insets(24));
        root.setStyle(
                "-fx-background-color: " + FONDO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        root.getChildren().addAll(
                encabezado,
                panelFormulario,
                panelTabla
        );

        Scene scene = new Scene(root, 1050, 760);

        stage.setTitle("Gestión de Mesas");
        stage.setMinWidth(900);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();

        // =========================
        // CARGA Y EVENTOS
        // =========================
        cargarMesas();

        tablaMesas.setOnMouseClicked(event -> {

            Mesa mesaSeleccionada
                    = tablaMesas.getSelectionModel()
                            .getSelectedItem();

            if (mesaSeleccionada != null) {

                txtId.setText(
                        String.valueOf(mesaSeleccionada.getId())
                );

                txtNumero.setText(
                        String.valueOf(mesaSeleccionada.getNumero())
                );

                txtCapacidad.setText(
                        String.valueOf(mesaSeleccionada.getCapacidad())
                );

                cboEstado.setValue(
                        mesaSeleccionada.getEstado()
                );
            }
        });

        btnGuardar.setOnAction(event -> guardarMesa());

        btnNuevo.setOnAction(event -> limpiarCampos());
        btnActualizar.setOnAction(event -> actualizarMesa());
        btnEliminar.setOnAction(event -> eliminarMesa());
        btnLimpiar.setOnAction(event -> limpiarCampos());

        btnVolver.setOnAction(event -> stage.close());
    }

    // =========================
    // ESTILOS AUXILIARES
    // =========================
    private Label crearEtiqueta(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );
        return etiqueta;
    }

    private TextField crearCampo(String ayuda) {
        TextField campo = new TextField();
        campo.setPromptText(ayuda);
        campo.setPrefHeight(38);
        campo.setStyle(
                "-fx-background-color: #FFFFFF;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-padding: 8 11 8 11;"
                + "-fx-font-size: 13px;"
                + "-fx-font-family: 'Segoe UI';"
        );

        campo.focusedProperty().addListener(
                (observable, anterior, enfocado) -> {
                    if (enfocado && campo.isEditable()) {
                        campo.setStyle(
                                "-fx-background-color: #FFFFFF;"
                                + "-fx-border-color: " + VERDE + ";"
                                + "-fx-border-width: 1.5;"
                                + "-fx-border-radius: 7;"
                                + "-fx-background-radius: 7;"
                                + "-fx-padding: 8 11 8 11;"
                                + "-fx-font-size: 13px;"
                                + "-fx-font-family: 'Segoe UI';"
                        );
                    } else if (campo.isEditable()) {
                        campo.setStyle(
                                "-fx-background-color: #FFFFFF;"
                                + "-fx-border-color: " + BORDE + ";"
                                + "-fx-border-radius: 7;"
                                + "-fx-background-radius: 7;"
                                + "-fx-padding: 8 11 8 11;"
                                + "-fx-font-size: 13px;"
                                + "-fx-font-family: 'Segoe UI';"
                        );
                    }
                }
        );

        return campo;
    }

    private void estilizarBoton(
            Button boton,
            String color,
            String colorHover) {

        boton.setPrefHeight(36);
        boton.setMinWidth(95);
        boton.setStyle(
                "-fx-background-color: " + color + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 11px;"
                + "-fx-font-weight: bold;"
                + "-fx-font-family: 'Segoe UI';"
                + "-fx-background-radius: 7;"
                + "-fx-padding: 8 13 8 13;"
                + "-fx-cursor: hand;"
        );

        boton.setOnMouseEntered(event
                -> boton.setStyle(
                        "-fx-background-color: " + colorHover + ";"
                        + "-fx-text-fill: "
                        + (boton == btnVolver ? VERDE : "white") + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-family: 'Segoe UI';"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 8 13 8 13;"
                        + "-fx-cursor: hand;"
                        + (boton == btnVolver
                                ? "-fx-border-color: " + BORDE + ";"
                                : "")
                )
        );

        boton.setOnMouseExited(event
                -> boton.setStyle(
                        "-fx-background-color: " + color + ";"
                        + "-fx-text-fill: "
                        + (boton == btnVolver ? VERDE : "white") + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-family: 'Segoe UI';"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 8 13 8 13;"
                        + "-fx-cursor: hand;"
                        + (boton == btnVolver
                                ? "-fx-border-color: " + BORDE + ";"
                                : "")
                )
        );
    }

    // =========================
    // MÉTODOS ORIGINALES
    // =========================
    private void cargarMesas() {

        ObservableList<Mesa> lista
                = FXCollections.observableArrayList(
                        mesaService.listarMesas()
                );

        tablaMesas.setItems(lista);
    }

    public void mostrar() {
        Stage nuevaVentana = new Stage();
        start(nuevaVentana);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void guardarMesa() {
        try {

            int numero = Integer.parseInt(txtNumero.getText());
            int capacidad = Integer.parseInt(txtCapacidad.getText());

            boolean registrado
                    = mesaService.registrarMesa(numero, capacidad);

            if (registrado) {

                mostrarMensaje(
                        "Éxito",
                        "La mesa se registró correctamente."
                );

                cargarMesas();
                limpiarCampos();

            } else {

                mostrarMensaje(
                        "Aviso",
                        "No se pudo registrar la mesa."
                );
            }

        } catch (NumberFormatException e) {

            mostrarMensaje(
                    "Error",
                    "Número y capacidad deben ser valores numéricos."
            );
        }
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNumero.clear();
        txtCapacidad.clear();

        cboEstado.setValue("DISPONIBLE");

        tablaMesas.getSelectionModel().clearSelection();
    }

    private void actualizarMesa() {

        try {

            if (txtId.getText().isEmpty()) {

                mostrarMensaje(
                        "Aviso",
                        "Selecciona una mesa de la tabla."
                );

                return;
            }

            int id = Integer.parseInt(txtId.getText());
            int numero = Integer.parseInt(txtNumero.getText());
            int capacidad = Integer.parseInt(txtCapacidad.getText());

            String estado = cboEstado.getValue();

            boolean actualizado
                    = mesaService.actualizarMesa(
                            id,
                            numero,
                            capacidad,
                            estado
                    );

            if (actualizado) {

                mostrarMensaje(
                        "Éxito",
                        "La mesa se actualizó correctamente."
                );

                cargarMesas();
                limpiarCampos();

            } else {

                mostrarMensaje(
                        "Aviso",
                        "No se pudo actualizar la mesa."
                );
            }

        } catch (NumberFormatException e) {

            mostrarMensaje(
                    "Error",
                    "Los datos numéricos no son válidos."
            );
        }
    }

    private void eliminarMesa() {

        if (txtId.getText().isEmpty()) {

            mostrarMensaje(
                    "Aviso",
                    "Selecciona una mesa de la tabla."
            );

            return;
        }

        int id = Integer.parseInt(txtId.getText());

        boolean eliminado = mesaService.eliminarMesa(id);

        if (eliminado) {

            mostrarMensaje(
                    "Éxito",
                    "La mesa se eliminó correctamente."
            );

            cargarMesas();
            limpiarCampos();

        } else {

            mostrarMensaje(
                    "Aviso",
                    "No se pudo eliminar la mesa."
            );
        }
    }
}
