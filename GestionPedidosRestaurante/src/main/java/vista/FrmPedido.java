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
import modelo.Mesa;
import modelo.Mozo;
import modelo.Pedido;
import service.PedidoService;

public class FrmPedido extends Application {

    private TextField txtId;
    private TextField txtFechaHora;
    private TextField txtEstado;
    private TextField txtSubTotal;

    private ComboBox<Mesa> cboMesa;
    private ComboBox<Mozo> cboMozo;

    private Button btnNuevo;
    private Button btnGuardar;
    private Button btnActualizar;
    private Button btnEliminar;
    private Button btnEnviarCocina;
    private Button btnEntregar;

    private TableView<Pedido> tablaPedidos;

    private PedidoService pedidoService;

    @Override
    public void start(Stage stage) {

        pedidoService = new PedidoService();

        Label lblTitulo = new Label("GESTIÓN DE PEDIDOS");
        lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label lblId = new Label("ID:");
        Label lblFecha = new Label("Fecha y hora:");
        Label lblMesa = new Label("Mesa:");
        Label lblMozo = new Label("Mozo:");
        Label lblEstado = new Label("Estado:");
        Label lblSubtotal = new Label("Subtotal:");

        txtId = new TextField();
        txtFechaHora = new TextField();
        txtEstado = new TextField();
        txtSubTotal = new TextField();

        cboMesa = new ComboBox<>();
        cboMozo = new ComboBox<>();

        txtId.setEditable(false);
        txtFechaHora.setEditable(false);
        txtEstado.setEditable(false);
        txtSubTotal.setEditable(false);

        cboMesa.setItems(FXCollections.observableArrayList(pedidoService.listarMesas()));
        cboMozo.setItems(FXCollections.observableArrayList(pedidoService.listarMozos()));

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(10));

        formulario.add(lblId, 0, 0);
        formulario.add(txtId, 1, 0);
        formulario.add(lblFecha, 0, 1);
        formulario.add(txtFechaHora, 1, 1);
        formulario.add(lblMesa, 0, 2);
        formulario.add(cboMesa, 1, 2);
        formulario.add(lblMozo, 0, 3);
        formulario.add(cboMozo, 1, 3);
        formulario.add(lblEstado, 0, 4);
        formulario.add(txtEstado, 1, 4);
        formulario.add(lblSubtotal, 0, 5);
        formulario.add(txtSubTotal, 1, 5);

        btnNuevo = new Button("NUEVO");
        btnGuardar = new Button("GUARDAR");
        btnActualizar = new Button("ACTUALIZAR");
        btnEliminar = new Button("ELIMINAR");
        btnEnviarCocina = new Button("ENVIAR A COCINA");
        btnEntregar = new Button("ENTREGAR PEDIDO");

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(
                btnNuevo, btnGuardar, btnActualizar, btnEliminar,
                btnEnviarCocina, btnEntregar
        );

        tablaPedidos = new TableView<>();

        TableColumn<Pedido, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Pedido, Object> colFecha = new TableColumn<>("Fecha y hora");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));

        TableColumn<Pedido, Integer> colMesa = new TableColumn<>("Mesa");
        colMesa.setCellValueFactory(new PropertyValueFactory<>("mesaId"));

        TableColumn<Pedido, Integer> colMozo = new TableColumn<>("Mozo");
        colMozo.setCellValueFactory(new PropertyValueFactory<>("mozoId"));

        TableColumn<Pedido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<Pedido, Double> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subTotal"));

        tablaPedidos.getColumns().addAll(
                colId, colFecha, colMesa, colMozo, colEstado, colSubtotal
        );
        tablaPedidos.setPrefHeight(300);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(lblTitulo, formulario, botones, tablaPedidos);

        Scene scene = new Scene(root, 1050, 700);
        stage.setTitle("Gestión de Pedidos");
        stage.setScene(scene);
        stage.show();

        cargarPedidos();

        tablaPedidos.setOnMouseClicked(event -> seleccionarPedido());

        btnNuevo.setOnAction(event -> limpiarCampos());
        btnGuardar.setOnAction(event -> guardarPedido());
        btnActualizar.setOnAction(event -> actualizarPedido());
        btnEliminar.setOnAction(event -> eliminarPedido());
        btnEnviarCocina.setOnAction(event -> enviarACocina());
        btnEntregar.setOnAction(event -> entregarPedido());
    }

    private void cargarPedidos() {
        tablaPedidos.setItems(
                FXCollections.observableArrayList(pedidoService.listarPedidos())
        );
    }

    private void seleccionarPedido() {
        Pedido pedido = tablaPedidos.getSelectionModel().getSelectedItem();

        if (pedido == null) {
            return;
        }

        txtId.setText(String.valueOf(pedido.getId()));
        txtFechaHora.setText(
                pedido.getFechaHora() == null ? "" : pedido.getFechaHora().toString()
        );
        txtEstado.setText(pedido.getEstado());
        txtSubTotal.setText(String.format("%.2f", pedido.getSubTotal()));

        seleccionarMesa(pedido.getMesaId());
        seleccionarMozo(pedido.getMozoId());
    }

    private void seleccionarMesa(int id) {
        for (Mesa mesa : cboMesa.getItems()) {
            if (mesa.getId() == id) {
                cboMesa.setValue(mesa);
                break;
            }
        }
    }

    private void seleccionarMozo(int id) {
        for (Mozo mozo : cboMozo.getItems()) {
            if (mozo.getId() == id) {
                cboMozo.setValue(mozo);
                break;
            }
        }
    }

    private void guardarPedido() {
        if (cboMesa.getValue() == null || cboMozo.getValue() == null) {
            mostrarMensaje("Aviso", "Selecciona una mesa y un mozo.");
            return;
        }

        boolean registrado = pedidoService.registrarPedido(
                cboMesa.getValue().getId(),
                cboMozo.getValue().getId()
        );

        if (registrado) {
            mostrarMensaje("Éxito", "El pedido se registró correctamente.");
            cargarPedidos();
            limpiarCampos();
        } else {
            mostrarMensaje("Aviso", "No se pudo registrar el pedido.");
        }
    }

    private void actualizarPedido() {
        if (txtId.getText().isEmpty()) {
            mostrarMensaje("Aviso", "Selecciona un pedido de la tabla.");
            return;
        }

        if (cboMesa.getValue() == null || cboMozo.getValue() == null) {
            mostrarMensaje("Aviso", "Selecciona una mesa y un mozo.");
            return;
        }

        boolean actualizado = pedidoService.actualizarPedido(
                Integer.parseInt(txtId.getText()),
                cboMesa.getValue().getId(),
                cboMozo.getValue().getId()
        );

        mostrarMensaje(
                actualizado ? "Éxito" : "Aviso",
                actualizado ? "El pedido se actualizó correctamente." : "No se pudo actualizar el pedido."
        );

        if (actualizado) {
            cargarPedidos();
            limpiarCampos();
        }
    }

    private void eliminarPedido() {
        if (txtId.getText().isEmpty()) {
            mostrarMensaje("Aviso", "Selecciona un pedido de la tabla.");
            return;
        }

        boolean eliminado = pedidoService.eliminarPedido(
                Integer.parseInt(txtId.getText())
        );

        mostrarMensaje(
                eliminado ? "Éxito" : "Aviso",
                eliminado ? "El pedido se eliminó correctamente." : "No se pudo eliminar el pedido."
        );

        if (eliminado) {
            cargarPedidos();
            limpiarCampos();
        }
    }

    private void enviarACocina() {
        if (txtId.getText().isEmpty()) {
            mostrarMensaje("Aviso", "Selecciona un pedido de la tabla.");
            return;
        }

        boolean enviado = pedidoService.enviarACocina(
                Integer.parseInt(txtId.getText())
        );

        mostrarMensaje(
                enviado ? "Éxito" : "Aviso",
                enviado ? "El pedido fue enviado a cocina." : "El pedido no puede ser enviado a cocina."
        );

        if (enviado) {
            cargarPedidos();
            limpiarCampos();
        }
    }

    private void entregarPedido() {
        if (txtId.getText().isEmpty()) {
            mostrarMensaje("Aviso", "Selecciona un pedido de la tabla.");
            return;
        }

        boolean entregado = pedidoService.entregarPedido(
                Integer.parseInt(txtId.getText())
        );

        mostrarMensaje(
                entregado ? "Éxito" : "Aviso",
                entregado ? "El pedido fue entregado." : "El pedido todavía no puede ser entregado."
        );

        if (entregado) {
            cargarPedidos();
            limpiarCampos();
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtFechaHora.clear();
        txtEstado.clear();
        txtSubTotal.clear();
        cboMesa.setValue(null);
        cboMozo.setValue(null);
        tablaPedidos.getSelectionModel().clearSelection();
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
