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

    private TableView<Mesa> tablaMesas;

    private MesaService mesaService;

    @Override
    public void start(Stage stage) {

        mesaService = new MesaService();

        Label lblTitulo = new Label("GESTIÓN DE MESAS");
        lblTitulo.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );
        Label lblId = new Label("ID:");
        Label lblNumero = new Label("Número:");
        Label lblCapacidad = new Label("Capacidad:");
        Label lblEstado = new Label("Estado:");

        txtId = new TextField();
        txtNumero = new TextField();
        txtCapacidad = new TextField();

        cboEstado = new ComboBox<>();
        cboEstado.getItems().addAll(
                "DISPONIBLE",
                "OCUPADA"
        );

        txtId.setEditable(false);

        cboEstado.setDisable(true);

        cboEstado.setValue("DISPONIBLE");

        GridPane formulario = new GridPane();

        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(10));

        formulario.add(lblId, 0, 0);
        formulario.add(txtId, 1, 0);

        formulario.add(lblNumero, 0, 1);
        formulario.add(txtNumero, 1, 1);

        formulario.add(lblCapacidad, 0, 2);
        formulario.add(txtCapacidad, 1, 2);

        formulario.add(lblEstado, 0, 3);
        formulario.add(cboEstado, 1, 3);

        btnNuevo = new Button("NUEVO");
        btnGuardar = new Button("GUARDAR");
        btnActualizar = new Button("ACTUALIZAR");
        btnEliminar = new Button("ELIMINAR");
        btnLimpiar = new Button("LIMPIAR");

        HBox botones = new HBox(10);

        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(
                btnNuevo,
                btnGuardar,
                btnActualizar,
                btnEliminar,
                btnLimpiar
        );

        tablaMesas = new TableView<>();

        TableColumn<Mesa, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        TableColumn<Mesa, Integer> colNumero
                = new TableColumn<>("Número");

        colNumero.setCellValueFactory(
                new PropertyValueFactory<>("numero")
        );

        TableColumn<Mesa, Integer> colCapacidad
                = new TableColumn<>("Capacidad");

        colCapacidad.setCellValueFactory(
                new PropertyValueFactory<>("capacidad")
        );

        TableColumn<Mesa, String> colEstado
                = new TableColumn<>("Estado");

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        tablaMesas.getColumns().addAll(
                colId,
                colNumero,
                colCapacidad,
                colEstado
        );

        tablaMesas.setPrefHeight(250);

        VBox root = new VBox(15);

        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(
                lblTitulo,
                formulario,
                botones,
                tablaMesas
        );

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Gestión de Mesas");
        stage.setScene(scene);
        stage.show();

        cargarMesas();

        tablaMesas.setOnMouseClicked(event -> {

            Mesa mesaSeleccionada
                    = tablaMesas.getSelectionModel().getSelectedItem();

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

    }

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

        boolean actualizado =
                mesaService.actualizarMesa(
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

    boolean eliminado =
            mesaService.eliminarMesa(id);

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
