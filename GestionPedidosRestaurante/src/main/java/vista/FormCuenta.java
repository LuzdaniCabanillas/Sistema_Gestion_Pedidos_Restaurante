package vista;

import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Cuenta;
import modelo.EstadoCuenta;
import service.CuentaService;

public class FormCuenta extends Application {

    // ==============================
    // SERVICE
    // ==============================
    private final CuentaService service =
            new CuentaService();

    // ==============================
    // CAMPOS
    // ==============================
    private final TextField txtSubtotal =
            new TextField();

    private final TextField txtIGV =
            new TextField();

    private final TextField txtTotal =
            new TextField();

    private final ComboBox<EstadoCuenta> cboEstado =
            new ComboBox<>();

    // ==============================
    // BOTONES
    // ==============================
    private final Button btnGuardar =
            new Button("Guardar");

    private final Button btnActualizar =
            new Button("Actualizar");

    private final Button btnEliminar =
            new Button("Eliminar");

    private final Button btnLimpiar =
            new Button("Limpiar");

    // ==============================
    // TABLA
    // ==============================
    private final TableView<Cuenta> tabla =
            new TableView<>();

    @Override
    public void start(Stage stage) {

        stage.setTitle("Gestión de Cuentas");

        // ==============================
        // CONFIGURACIÓN DE CAMPOS
        // ==============================

        // IGV y Total se calculan automáticamente
        txtIGV.setEditable(false);
        txtTotal.setEditable(false);

        txtSubtotal.setPromptText("Ejemplo: 100.00");
        txtIGV.setPromptText("Automático");
        txtTotal.setPromptText("Automático");

        cboEstado.getItems().addAll(
                EstadoCuenta.values()
        );

        cboEstado.setValue(
                EstadoCuenta.PENDIENTE
        );

        // ==============================
        // FORMULARIO
        // ==============================

        GridPane formulario =
                new GridPane();

        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.setPadding(
                new Insets(15)
        );

        // SUBTOTAL
        formulario.add(
                new Label("Subtotal:"),
                0,
                0
        );

        formulario.add(
                txtSubtotal,
                1,
                0
        );

        // IGV
        formulario.add(
                new Label("IGV:"),
                2,
                0
        );

        formulario.add(
                txtIGV,
                3,
                0
        );

        // TOTAL
        formulario.add(
                new Label("Total:"),
                0,
                1
        );

        formulario.add(
                txtTotal,
                1,
                1
        );

        // ESTADO
        formulario.add(
                new Label("Estado:"),
                2,
                1
        );

        formulario.add(
                cboEstado,
                3,
                1
        );

        // ==============================
        // BOTONES
        // ==============================

        HBox botones =
                new HBox(
                        10,
                        btnGuardar,
                        btnActualizar,
                        btnEliminar,
                        btnLimpiar
                );

        botones.setAlignment(
                Pos.CENTER
        );

        botones.setPadding(
                new Insets(10)
        );

        // ==============================
        // TABLA
        // ==============================

        // ID
        TableColumn<Cuenta, String> colId =
                new TableColumn<>("ID");

        colId.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.valueOf(
                                        dato.getValue()
                                                .getId()
                                )
                        )
        );

        // FECHA
        TableColumn<Cuenta, String> colFecha =
                new TableColumn<>("Fecha");

        colFecha.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.valueOf(
                                        dato.getValue()
                                                .getFechaGeneracion()
                                )
                        )
        );

        // SUBTOTAL
        TableColumn<Cuenta, String> colSubtotal =
                new TableColumn<>("Subtotal");

        colSubtotal.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.format(
                                        "%.2f",
                                        dato.getValue()
                                                .getSubTotal()
                                )
                        )
        );

        // IGV
        TableColumn<Cuenta, String> colIGV =
                new TableColumn<>("IGV");

        colIGV.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.format(
                                        "%.2f",
                                        dato.getValue()
                                                .getIgv()
                                )
                        )
        );

        // TOTAL
        TableColumn<Cuenta, String> colTotal =
                new TableColumn<>("Total");

        colTotal.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.format(
                                        "%.2f",
                                        dato.getValue()
                                                .getTotal()
                                )
                        )
        );

        // ESTADO
        TableColumn<Cuenta, String> colEstado =
                new TableColumn<>("Estado");

        colEstado.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                String.valueOf(
                                        dato.getValue()
                                                .getEstado()
                                )
                        )
        );

        tabla.getColumns().addAll(
                colId,
                colFecha,
                colSubtotal,
                colIGV,
                colTotal,
                colEstado
        );

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        // ==============================
        // EVENTOS DE BOTONES
        // ==============================

        btnGuardar.setOnAction(
                e -> guardar()
        );

        btnActualizar.setOnAction(
                e -> actualizar()
        );

        btnEliminar.setOnAction(
                e -> eliminar()
        );

        btnLimpiar.setOnAction(
                e -> limpiar()
        );

        // ==============================
        // SELECCIONAR FILA
        // ==============================

        tabla.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable,
                         anterior,
                         seleccionado) -> {

                            if (seleccionado != null) {

                                mostrarSeleccion(
                                        seleccionado
                                );
                            }
                        }
                );

        // ==============================
        // DISEÑO
        // ==============================

        Label titulo =
                new Label(
                        "GESTIÓN DE CUENTAS"
                );

        titulo.setStyle(
                "-fx-font-size: 22px;"
                        + "-fx-font-weight: bold;"
        );

        VBox superior =
                new VBox(
                        10,
                        titulo,
                        formulario,
                        botones
                );

        superior.setAlignment(
                Pos.CENTER
        );

        superior.setPadding(
                new Insets(10)
        );

        BorderPane root =
                new BorderPane();

        root.setTop(superior);
        root.setCenter(tabla);

        BorderPane.setMargin(
                tabla,
                new Insets(10)
        );

        // ==============================
        // CARGAR DATOS
        // ==============================

        cargarTabla();

        // ==============================
        // VENTANA
        // ==============================

        Scene scene =
                new Scene(
                        root,
                        850,
                        500
                );

        stage.setScene(scene);
        stage.show();
    }

    // =====================================
    // READ - CARGAR TABLA
    // =====================================

    private void cargarTabla() {

        tabla.getItems().clear();

        List<Cuenta> lista =
                service.listarCuentas();

        tabla.getItems().addAll(
                lista
        );
    }

    // =====================================
    // LEER SUBTOTAL
    // =====================================

    private Double leerSubtotal() {

        String textoSubtotal =
                txtSubtotal
                        .getText()
                        .trim();

        // Validar campo vacío
        if (textoSubtotal.isEmpty()) {

            mensaje(
                    "Ingrese el subtotal.",
                    Alert.AlertType.WARNING
            );

            return null;
        }

        try {

            return Double.parseDouble(
                    textoSubtotal
            );

        } catch (NumberFormatException ex) {

            mensaje(
                    "El subtotal debe ser un número.",
                    Alert.AlertType.WARNING
            );

            return null;
        }
    }

    // =====================================
    // CREATE - GUARDAR
    // =====================================

    private void guardar() {

        Double subtotal =
                leerSubtotal();

        if (subtotal == null) {
            return;
        }

        if (service.registrarCuenta(
                subtotal)) {

            mensaje(
                    "Cuenta registrada correctamente.",
                    Alert.AlertType.INFORMATION
            );

            cargarTabla();
            limpiar();

        } else {

            mensaje(
                    "No se pudo registrar la cuenta.",
                    Alert.AlertType.ERROR
            );
        }
    }

    // =====================================
    // MOSTRAR REGISTRO SELECCIONADO
    // =====================================

    private void mostrarSeleccion(
            Cuenta cuenta) {

        txtSubtotal.setText(
                String.valueOf(
                        cuenta.getSubTotal()
                )
        );

        txtIGV.setText(
                String.valueOf(
                        cuenta.getIgv()
                )
        );

        txtTotal.setText(
                String.valueOf(
                        cuenta.getTotal()
                )
        );

        cboEstado.setValue(
                cuenta.getEstado()
        );
    }

    // =====================================
    // UPDATE - ACTUALIZAR
    // =====================================

    private void actualizar() {

        Cuenta seleccionada =
                tabla.getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {

            mensaje(
                    "Seleccione una cuenta de la tabla.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        Double subtotal =
                leerSubtotal();

        if (subtotal == null) {
            return;
        }

        EstadoCuenta estado =
                cboEstado.getValue();

        if (estado == null) {

            mensaje(
                    "Seleccione un estado.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        if (service.actualizarCuenta(
                seleccionada.getId(),
                subtotal,
                estado)) {

            mensaje(
                    "Cuenta actualizada correctamente.",
                    Alert.AlertType.INFORMATION
            );

            cargarTabla();
            limpiar();

        } else {

            mensaje(
                    "No se pudo actualizar la cuenta.",
                    Alert.AlertType.ERROR
            );
        }
    }

    // =====================================
    // DELETE - ELIMINAR
    // =====================================

    private void eliminar() {

        Cuenta seleccionada =
                tabla.getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {

            mensaje(
                    "Seleccione una cuenta de la tabla.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        Alert confirmar =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmar.setTitle(
                "Confirmar"
        );

        confirmar.setHeaderText(
                "Eliminar cuenta"
        );

        confirmar.setContentText(
                "¿Desea eliminar la cuenta seleccionada?"
        );

        ButtonType respuesta =
                confirmar
                        .showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (respuesta == ButtonType.OK) {

            int id =
                    seleccionada.getId();

            if (service.eliminarCuenta(id)) {

                mensaje(
                        "Cuenta eliminada correctamente.",
                        Alert.AlertType.INFORMATION
                );

                cargarTabla();
                limpiar();

            } else {

                mensaje(
                        "No se pudo eliminar la cuenta.",
                        Alert.AlertType.ERROR
                );
            }
        }
    }

    // =====================================
    // LIMPIAR
    // =====================================

    private void limpiar() {

        txtSubtotal.clear();
        txtIGV.clear();
        txtTotal.clear();

        cboEstado.setValue(
                EstadoCuenta.PENDIENTE
        );

        tabla.getSelectionModel()
                .clearSelection();

        txtSubtotal.requestFocus();
    }

    // =====================================
    // MENSAJES
    // =====================================

    private void mensaje(
            String texto,
            Alert.AlertType tipo) {

        Alert alerta =
                new Alert(tipo);

        alerta.setTitle(
                "Sistema Restaurante"
        );

        alerta.setHeaderText(null);
        alerta.setContentText(texto);

        alerta.showAndWait();
    }

    // =====================================
    // MAIN
    // =====================================

    public static void main(String[] args) {

        Application.launch(
                FormCuenta.class,
                args
        );
    }
}