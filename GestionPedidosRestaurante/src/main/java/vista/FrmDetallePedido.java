package vista;

import javafx.application.Application;
import javafx.collections.FXCollections;
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
import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Plato;
import service.DetallePedidoService;

public class FrmDetallePedido extends Application {

    private TextField txtId;
    private TextField txtCantidad;
    private TextField txtPrecio;
    private TextField txtSubtotal;

    private ComboBox<Pedido> cboPedido;
    private ComboBox<Plato> cboPlato;

    private Button btnNuevo;
    private Button btnGuardar;
    private Button btnActualizar;
    private Button btnEliminar;

    private TableView<DetallePedido> tablaDetalles;

    private DetallePedidoService detalleService;

    @Override
    public void start(Stage stage) {

        detalleService = new DetallePedidoService();

        Label lblTitulo = new Label("GESTIÓN DE DETALLES DE PEDIDO");
        lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label lblId = new Label("ID:");
        Label lblPedido = new Label("Pedido:");
        Label lblPlato = new Label("Plato:");
        Label lblCantidad = new Label("Cantidad:");
        Label lblPrecio = new Label("Precio unitario:");
        Label lblSubtotal = new Label("Subtotal:");

        txtId = new TextField();
        txtCantidad = new TextField();
        txtPrecio = new TextField();
        txtSubtotal = new TextField();

        cboPedido = new ComboBox<>();
        cboPlato = new ComboBox<>();

        txtId.setEditable(false);
        txtPrecio.setEditable(false);
        txtSubtotal.setEditable(false);

        cargarCombos();

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(10));

        formulario.add(lblId, 0, 0);
        formulario.add(txtId, 1, 0);
        formulario.add(lblPedido, 0, 1);
        formulario.add(cboPedido, 1, 1);
        formulario.add(lblPlato, 0, 2);
        formulario.add(cboPlato, 1, 2);
        formulario.add(lblCantidad, 0, 3);
        formulario.add(txtCantidad, 1, 3);
        formulario.add(lblPrecio, 0, 4);
        formulario.add(txtPrecio, 1, 4);
        formulario.add(lblSubtotal, 0, 5);
        formulario.add(txtSubtotal, 1, 5);

        btnNuevo = new Button("NUEVO");
        btnGuardar = new Button("GUARDAR");
        btnActualizar = new Button("ACTUALIZAR");
        btnEliminar = new Button("ELIMINAR");

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(
                btnNuevo, btnGuardar, btnActualizar, btnEliminar
        );

        tablaDetalles = new TableView<>();

        TableColumn<DetallePedido, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<DetallePedido, Integer> colPedido = new TableColumn<>("Pedido");
        colPedido.setCellValueFactory(new PropertyValueFactory<>("pedidoId"));

        TableColumn<DetallePedido, Integer> colPlato = new TableColumn<>("Plato");
        colPlato.setCellValueFactory(new PropertyValueFactory<>("platoId"));

        TableColumn<DetallePedido, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        TableColumn<DetallePedido, Double> colPrecio = new TableColumn<>("Precio unitario");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));

        TableColumn<DetallePedido, Double> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));

        tablaDetalles.getColumns().addAll(
                colId, colPedido, colPlato, colCantidad, colPrecio, colSubtotal
        );
        tablaDetalles.setPrefHeight(300);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(lblTitulo, formulario, botones, tablaDetalles);

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Gestión de Detalles de Pedido");
        stage.setScene(scene);
        stage.show();

        cargarDetalles();

        cboPlato.setOnAction(event -> actualizarPrecio());
        txtCantidad.setOnKeyReleased(event -> calcularSubtotalVisual());

        tablaDetalles.setOnMouseClicked(event -> seleccionarDetalle());

        btnNuevo.setOnAction(event -> limpiarCampos());
        btnGuardar.setOnAction(event -> guardarDetalle());
        btnActualizar.setOnAction(event -> actualizarDetalle());
        btnEliminar.setOnAction(event -> eliminarDetalle());
    }

    private void cargarCombos() {
        cboPedido.setItems(
                FXCollections.observableArrayList(detalleService.listarPedidos())
        );
        cboPlato.setItems(
                FXCollections.observableArrayList(detalleService.listarPlatos())
        );
    }

    private void cargarDetalles() {
        tablaDetalles.setItems(
                FXCollections.observableArrayList(detalleService.listarDetalles())
        );
    }

    private void seleccionarDetalle() {
        DetallePedido detalle = tablaDetalles.getSelectionModel().getSelectedItem();

        if (detalle == null) {
            return;
        }

        txtId.setText(String.valueOf(detalle.getId()));
        txtCantidad.setText(String.valueOf(detalle.getCantidad()));
        txtPrecio.setText(String.format("%.2f", detalle.getPrecioUnitario()));
        txtSubtotal.setText(String.format("%.2f", detalle.getSubTotal()));

        seleccionarPedido(detalle.getPedidoId());
        seleccionarPlato(detalle.getPlatoId());
    }

    private void seleccionarPedido(int id) {
        for (Pedido pedido : cboPedido.getItems()) {
            if (pedido.getId() == id) {
                cboPedido.setValue(pedido);
                break;
            }
        }
    }

    private void seleccionarPlato(int id) {
        for (Plato plato : cboPlato.getItems()) {
            if (plato.getId() == id) {
                cboPlato.setValue(plato);
                break;
            }
        }
    }

    private void actualizarPrecio() {
        Plato plato = cboPlato.getValue();

        if (plato != null) {
            txtPrecio.setText(String.format("%.2f", plato.getPrecio()));
            calcularSubtotalVisual();
        }
    }

    private void calcularSubtotalVisual() {
        try {
            if (cboPlato.getValue() == null || txtCantidad.getText().isEmpty()) {
                txtSubtotal.clear();
                return;
            }

            int cantidad = Integer.parseInt(txtCantidad.getText());
            double subtotal = cantidad * cboPlato.getValue().getPrecio();
            txtSubtotal.setText(String.format("%.2f", subtotal));

        } catch (NumberFormatException e) {
            txtSubtotal.clear();
        }
    }

    private void guardarDetalle() {
        if (!validarCampos()) {
            return;
        }

        boolean registrado = detalleService.registrarDetalle(
                cboPedido.getValue().getId(),
                cboPlato.getValue().getId(),
                Integer.parseInt(txtCantidad.getText())
        );

        mostrarMensaje(
                registrado ? "Éxito" : "Aviso",
                registrado ? "El detalle se registró correctamente." : "No se pudo registrar el detalle."
        );

        if (registrado) {
            cargarDetalles();
            cargarCombos();
            limpiarCampos();
        }
    }

    private void actualizarDetalle() {
        if (txtId.getText().isEmpty()) {
            mostrarMensaje("Aviso", "Selecciona un detalle de la tabla.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        boolean actualizado = detalleService.actualizarDetalle(
                Integer.parseInt(txtId.getText()),
                cboPedido.getValue().getId(),
                cboPlato.getValue().getId(),
                Integer.parseInt(txtCantidad.getText())
        );

        mostrarMensaje(
                actualizado ? "Éxito" : "Aviso",
                actualizado ? "El detalle se actualizó correctamente." : "No se pudo actualizar el detalle."
        );

        if (actualizado) {
            cargarDetalles();
            cargarCombos();
            limpiarCampos();
        }
    }

    private void eliminarDetalle() {
        if (txtId.getText().isEmpty()) {
            mostrarMensaje("Aviso", "Selecciona un detalle de la tabla.");
            return;
        }

        boolean eliminado = detalleService.eliminarDetalle(
                Integer.parseInt(txtId.getText())
        );

        mostrarMensaje(
                eliminado ? "Éxito" : "Aviso",
                eliminado ? "El detalle se eliminó correctamente." : "No se pudo eliminar el detalle."
        );

        if (eliminado) {
            cargarDetalles();
            limpiarCampos();
        }
    }

    private boolean validarCampos() {
        if (cboPedido.getValue() == null || cboPlato.getValue() == null) {
            mostrarMensaje("Aviso", "Selecciona un pedido y un plato.");
            return false;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad <= 0) {
                mostrarMensaje("Aviso", "La cantidad debe ser mayor que 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "La cantidad debe ser un número entero.");
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        txtId.clear();
        txtCantidad.clear();
        txtPrecio.clear();
        txtSubtotal.clear();
        cboPedido.setValue(null);
        cboPlato.setValue(null);
        tablaDetalles.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
