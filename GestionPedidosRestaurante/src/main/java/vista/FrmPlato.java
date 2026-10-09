package vista;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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

    private TableView<Plato> tablaPlatos;

    private PlatoService platoService;

    @Override
    public void start(Stage stage) {
        platoService = new PlatoService();
        txtId = new TextField();
        txtId.setEditable(false);

        txtNombre = new TextField();

        txtDescripcion = new TextField();

        txtPrecio = new TextField();

        txtCategoria = new TextField();

        cboEstado = new ComboBox<>();
        cboEstado.getItems().addAll(
                "DISPONIBLE",
                "NO_DISPONIBLE"
        );
        cboEstado.setValue("DISPONIBLE");

        txtStock = new TextField();

        txtImagen = new TextField();


        btnNuevo = new Button("NUEVO");
        btnGuardar = new Button("GUARDAR");
        btnActualizar = new Button("ACTUALIZAR");
        btnEliminar = new Button("ELIMINAR");
        btnLimpiar = new Button("LIMPIAR");

        GridPane formulario = new GridPane();

        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(15));

        formulario.add(new Label("ID:"), 0, 0);
        formulario.add(txtId, 1, 0);

        formulario.add(new Label("Nombre:"), 0, 1);
        formulario.add(txtNombre, 1, 1);

        formulario.add(new Label("Descripción:"), 0, 2);
        formulario.add(txtDescripcion, 1, 2);

        formulario.add(new Label("Precio:"), 0, 3);
        formulario.add(txtPrecio, 1, 3);

        formulario.add(new Label("Categoría:"), 0, 4);
        formulario.add(txtCategoria, 1, 4);

        formulario.add(new Label("Estado:"), 0, 5);
        formulario.add(cboEstado, 1, 5);

        formulario.add(new Label("Stock:"), 0, 6);
        formulario.add(txtStock, 1, 6);

        formulario.add(new Label("Imagen:"), 0, 7);
        formulario.add(txtImagen, 1, 7);



        HBox botones = new HBox(10);

        botones.setPadding(new Insets(10));

        botones.getChildren().addAll(
                btnNuevo,
                btnGuardar,
                btnActualizar,
                btnEliminar,
                btnLimpiar
        );

        tablaPlatos = new TableView<>();

        TableColumn<Plato, Integer> colId
                = new TableColumn<>("ID");

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        TableColumn<Plato, String> colNombre
                = new TableColumn<>("Nombre");

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );
        TableColumn<Plato, String> colDescripcion
                = new TableColumn<>("Descripción");

        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion")
        );

        TableColumn<Plato, Double> colPrecio
                = new TableColumn<>("Precio");

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio")
        );

        TableColumn<Plato, String> colCategoria
                = new TableColumn<>("Categoría");

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria")
        );

        TableColumn<Plato, String> colEstado
                = new TableColumn<>("Estado");

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        TableColumn<Plato, Integer> colStock
                = new TableColumn<>("Stock");

        colStock.setCellValueFactory(
                new PropertyValueFactory<>("stock")
        );
        tablaPlatos.getColumns().addAll(
                colId,
                colNombre,
                colDescripcion,
                colPrecio,
                colCategoria,
                colEstado,
                colStock
        );

        tablaPlatos.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, anterior, seleccionado) -> {

                            if (seleccionado != null) {

                                mostrarPlato(
                                        seleccionado
                                );
                            }
                        }
                );

        btnNuevo.setOnAction(
                e -> nuevoPlato()
        );

        btnGuardar.setOnAction(
                e -> guardarPlato()
        );

        btnActualizar.setOnAction(
                e -> actualizarPlato()
        );

        btnEliminar.setOnAction(
                e -> eliminarPlato()
        );

        btnLimpiar.setOnAction(
                e -> limpiarCampos()
        );

        cargarPlatos();

        VBox principal = new VBox(10);

        principal.setPadding(
                new Insets(15)
        );

        principal.getChildren().addAll(
                formulario,
                botones,
                tablaPlatos
        );

        Scene scene = new Scene(
                principal,
                1100,
                650
        );

        stage.setTitle(
                "Gestión de Platos"
        );

        stage.setScene(scene);

        stage.show();
    }

    private void cargarPlatos() {

        ObservableList<Plato> lista
                = FXCollections.observableArrayList(
                        platoService.listarPlatos()
                );

        tablaPlatos.setItems(lista);
    }

    private void mostrarPlato(Plato plato) {

        txtId.setText(
                String.valueOf(
                        plato.getId()
                )
        );

        txtNombre.setText(
                plato.getNombre()
        );

        txtDescripcion.setText(
                plato.getDescripcion()
        );

        txtPrecio.setText(
                String.valueOf(
                        plato.getPrecio()
                )
        );

        txtCategoria.setText(
                plato.getCategoria()
        );

        cboEstado.setValue(
                plato.getEstado()
        );

        txtStock.setText(
                String.valueOf(
                        plato.getStock()
                )
        );
        txtImagen.setText(
                plato.getImagen()
        );

        
    }

    private void nuevoPlato() {

        limpiarCampos();

        txtNombre.requestFocus();
    }

    private void guardarPlato() {

        try {

            String nombre
                    = txtNombre.getText().trim();

            String descripcion
                    = txtDescripcion.getText().trim();

            double precio
                    = Double.parseDouble(
                            txtPrecio.getText().trim()
                    );

            String categoria
                    = txtCategoria.getText().trim();

            String estado
                    = cboEstado.getValue();

            int stock
                    = Integer.parseInt(
                            txtStock.getText().trim()
                    );

            String imagen
                    = txtImagen.getText().trim();

            

            boolean resultado
                    = platoService.registrarPlato(
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
                    "Precio, stock y atributos deben contener números válidos."
            );
        }
    }
    
    // ACTUALIZAR
   

    private void actualizarPlato() {

        try {

            if (txtId.getText().isEmpty()) {

                mostrarMensaje(
                        "Advertencia",
                        "Seleccione un plato de la tabla."
                );

                return;
            }

            int id
                    = Integer.parseInt(
                            txtId.getText()
                    );

            String nombre
                    = txtNombre.getText().trim();

            String descripcion
                    = txtDescripcion.getText().trim();

            double precio
                    = Double.parseDouble(
                            txtPrecio.getText().trim()
                    );

            String categoria
                    = txtCategoria.getText().trim();

            String estado
                    = cboEstado.getValue();

            int stock
                    = Integer.parseInt(
                            txtStock.getText().trim()
                    );

            String imagen
                    = txtImagen.getText().trim();

            

            boolean resultado
                    = platoService.actualizarPlato(
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
                    "Precio, stock y atributos deben contener números válidos."
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

            int id
                    = Integer.parseInt(
                            txtId.getText()
                    );

            boolean resultado
                    = platoService.eliminarPlato(id);

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

        cboEstado.setValue(
                "DISPONIBLE"
        );

        txtStock.clear();
        txtImagen.clear();


        tablaPlatos
                .getSelectionModel()
                .clearSelection();
    }

    private void mostrarMensaje(
            String titulo,
            String mensaje) {

        Alert alert
                = new Alert(
                        Alert.AlertType.INFORMATION
                );

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

