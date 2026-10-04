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

    private TableView<AtencionMesa> tablaAtenciones;

    private AtencionMesaService atencionService;
    private MozoDAO mozoDAO;

    @Override
    public void start(Stage stage) {

        atencionService = new AtencionMesaService();
        mozoDAO = new MozoDAO();

        // Título
        Label lblTitulo
                = new Label("GESTIÓN DE ATENCIÓN DE MESAS");

        lblTitulo.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        // Etiquetas
        Label lblId = new Label("ID:");
        Label lblMesa = new Label("Mesa:");
        Label lblMozo = new Label("Mozo:");
        Label lblEstado = new Label("Estado:");
        Label lblFechaInicio = new Label("Fecha inicio:");
        Label lblFechaFin = new Label("Fecha fin:");

        // Campos
        txtId = new TextField();
        cboMesa = new ComboBox<>();
        cboMozo = new ComboBox<>();

        ObservableList<Mozo> listaMozos
                = FXCollections.observableArrayList(
                        mozoDAO.listar()
                );
        cboMozo.setItems(listaMozos);

        txtEstado = new TextField();
        txtFechaInicio = new TextField();
        txtFechaFin = new TextField();

        // Campos que no se escriben manualmente
        txtId.setEditable(false);
        txtEstado.setEditable(false);
        txtFechaInicio.setEditable(false);
        txtFechaFin.setEditable(false);

        // Formulario
        GridPane formulario = new GridPane();

        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(10));

        formulario.add(lblId, 0, 0);
        formulario.add(txtId, 1, 0);

        formulario.add(lblMesa, 0, 1);
        formulario.add(cboMesa, 1, 1);

        formulario.add(lblMozo, 0, 2);
        formulario.add(cboMozo, 1, 2);

        formulario.add(lblEstado, 0, 3);
        formulario.add(txtEstado, 1, 3);

        formulario.add(lblFechaInicio, 0, 4);
        formulario.add(txtFechaInicio, 1, 4);

        formulario.add(lblFechaFin, 0, 5);
        formulario.add(txtFechaFin, 1, 5);

        // Botones
        btnNuevo = new Button("NUEVO");
        btnIniciar = new Button("INICIAR");
        btnActualizar = new Button("ACTUALIZAR");
        btnFinalizar = new Button("FINALIZAR");
        btnEliminar = new Button("ELIMINAR");

        HBox botones = new HBox(10);

        botones.setAlignment(Pos.CENTER);

        botones.getChildren().addAll(
                btnNuevo,
                btnIniciar,
                btnActualizar,
                btnFinalizar,
                btnEliminar
        );

        // Tabla
        tablaAtenciones = new TableView<>();

        TableColumn<AtencionMesa, Integer> colId
                = new TableColumn<>("ID");

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        TableColumn<AtencionMesa, Integer> colMesa
                = new TableColumn<>("Mesa");

        colMesa.setCellValueFactory(
                new PropertyValueFactory<>("mesaId")
        );

        TableColumn<AtencionMesa, Integer> colMozo
                = new TableColumn<>("Mozo");

        colMozo.setCellValueFactory(
                new PropertyValueFactory<>("mozoId")
        );

        TableColumn<AtencionMesa, LocalDateTime> colInicio
                = new TableColumn<>("Fecha inicio");

        colInicio.setCellValueFactory(
                new PropertyValueFactory<>("fechaInicio")
        );

        TableColumn<AtencionMesa, LocalDateTime> colFin
                = new TableColumn<>("Fecha fin");

        colFin.setCellValueFactory(
                new PropertyValueFactory<>("fechaFin")
        );

        TableColumn<AtencionMesa, String> colEstado
                = new TableColumn<>("Estado");

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        tablaAtenciones.getColumns().addAll(
                colId,
                colMesa,
                colMozo,
                colInicio,
                colFin,
                colEstado
        );

        tablaAtenciones.setPrefHeight(280);

        // Contenedor principal
        VBox root = new VBox(15);

        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(
                lblTitulo,
                formulario,
                botones,
                tablaAtenciones
        );

        // Escena
        Scene scene
                = new Scene(root, 1000, 650);

        stage.setTitle(
                "Gestión de Atención de Mesas"
        );

        stage.setScene(scene);
        stage.show();

        cargarAtenciones();

        // Seleccionar una fila
        tablaAtenciones.setOnMouseClicked(event -> {

            AtencionMesa seleccionada
                    = tablaAtenciones
                            .getSelectionModel()
                            .getSelectedItem();

            if (seleccionada != null) {

                txtId.setText(
                        String.valueOf(
                                seleccionada.getId()
                        )
                );

                seleccionarMesa(
                        seleccionada.getMesaId()
                );

                seleccionarMozo(
                        seleccionada.getMozoId()
                );

                txtEstado.setText(
                        seleccionada.getEstado()
                );

                if (seleccionada.getFechaInicio() != null) {

                    txtFechaInicio.setText(
                            seleccionada
                                    .getFechaInicio()
                                    .toString()
                    );

                } else {

                    txtFechaInicio.clear();
                }

                if (seleccionada.getFechaFin() != null) {

                    txtFechaFin.setText(
                            seleccionada
                                    .getFechaFin()
                                    .toString()
                    );

                } else {

                    txtFechaFin.clear();
                }
            }
        });

        // Botones
        btnNuevo.setOnAction(
                event -> limpiarCampos()
        );

        btnIniciar.setOnAction(
                event -> iniciarAtencion()
        );

        btnActualizar.setOnAction(
                event -> actualizarAtencion()
        );

        btnFinalizar.setOnAction(
                event -> finalizarAtencion()
        );

        btnEliminar.setOnAction(
                event -> eliminarAtencion()
        );
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

            mostrarMensaje(
                    "Aviso",
                    "Selecciona una mesa"
            );

            return;

        }
        if (mozoSeleccionado == null) {

            mostrarMensaje(
                    "Aviso",
                    "Selecciona una mozo"
            );

            return;

        }
        boolean resultado = atencionService.iniciarAtencion(
                mesaSeleccionada.getId(),
                mozoSeleccionado.getId()
        );

        if (resultado) {

            mostrarMensaje(
                    "Exito",
                    "La atencion se inicio correctamente."
            );

            cargarAtenciones();
            cargarMesas();
            cargarMozos();
            limpiarCampos();

        } else {
            mostrarMensaje(
                    "Aviso",
                    "No se pudo iniciar la atencion."
            );
        }
    }

    private void actualizarAtencion() {

        try {

            if (txtId.getText().isEmpty()) {

                mostrarMensaje(
                        "Aviso",
                        "Selecciona una atención."
                );

                return;
            }

            Mesa mesaSeleccionada = cboMesa.getValue();
            Mozo mozoSeleccionado = cboMozo.getValue();

            if (mesaSeleccionada == null || mozoSeleccionado == null) {

                mostrarMensaje(
                        "Aviso",
                        "Selecciona una mesa y mozo."
                );

                return;

            }

            int id = Integer.parseInt(txtId.getText());

            boolean resultado
                    = atencionService.actualizarAtencion(
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

            mostrarMensaje(
                    "Aviso",
                    "Selecciona una atención."
            );

            return;
        }

        int id
                = Integer.parseInt(
                        txtId.getText()
                );

        boolean resultado
                = atencionService.finalizarAtencion(id);

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

            mostrarMensaje(
                    "Aviso",
                    "Selecciona una atención."
            );

            return;
        }

        int id
                = Integer.parseInt(
                        txtId.getText()
                );

        boolean resultado
                = atencionService.eliminarAtencion(id);

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

    cboMesa.getSelectionModel()
            .clearSelection();

    cboMozo.getSelectionModel()
            .clearSelection();

    txtEstado.clear();
    txtFechaInicio.clear();
    txtFechaFin.clear();

    tablaAtenciones
            .getSelectionModel()
            .clearSelection();
}

    private void mostrarMensaje(
            String titulo,
            String mensaje) {

        Alert alerta
                = new Alert(
                        Alert.AlertType.INFORMATION
                );

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void seleccionarMesa(int mesaId) {
        for (Mesa mesa : cboMesa.getItems()) {

        if (mesa.getId() == mesaId) {

            cboMesa.setValue(mesa);
            break;
        }
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
        ObservableList<Mesa> listaMesas =
            FXCollections.observableArrayList(
                    atencionService.listarMesas()
            );

    cboMesa.setItems(listaMesas);
    }

    private void cargarMozos() {
        ObservableList<Mozo> listaMozos =
            FXCollections.observableArrayList(
                    mozoDAO.listar()
            );

    cboMozo.setItems(listaMozos);
    }
}
