
package vista;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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


public class FrmMesa  extends Application {
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

       // Servicio de Mesa
        mesaService = new MesaService();

        // Título
        Label lblTitulo = new Label("GESTIÓN DE MESAS");
        lblTitulo.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        // Campos
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

// El ID lo genera MySQL
        txtId.setEditable(false);

        // El estado será controlado posteriormente
        // por la atención de la mesa.
        cboEstado.setDisable(true);

        // Valor inicial
        cboEstado.setValue("DISPONIBLE");

        // Grid de datos
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

// Botones
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

// Tabla
        tablaMesas = new TableView<>();

        TableColumn<Mesa, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        TableColumn<Mesa, Integer> colNumero =
                new TableColumn<>("Número");

        colNumero.setCellValueFactory(
                new PropertyValueFactory<>("numero")
        );

        TableColumn<Mesa, Integer> colCapacidad =
                new TableColumn<>("Capacidad");

        colCapacidad.setCellValueFactory(
                new PropertyValueFactory<>("capacidad")
        );

        TableColumn<Mesa, String> colEstado =
                new TableColumn<>("Estado");

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

        // Contenedor principal
        VBox root = new VBox(15);

        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(
                lblTitulo,
                formulario,
                botones,
                tablaMesas
        );

        // Escena
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Gestión de Mesas");
        stage.setScene(scene);
        stage.show();

        // Cargar las mesas de MySQL
        cargarMesas();
    }

    private void cargarMesas() {

        ObservableList<Mesa> lista =
                FXCollections.observableArrayList(
                        mesaService.listarMesas()
                );

        tablaMesas.setItems(lista);
    }

    public static void main(String[] args) {
        launch(args);
    }
   
}
