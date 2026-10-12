package vista;

import controlador.EmpleadoPagoDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import modelo.Cajero;
import modelo.Cuenta;
import modelo.Empleado;
import modelo.Mozo;
import service.PagoService;

public class FrmRegistroPago extends BorderPane {

    private final Cuenta cuenta;
    private final PagoService pagoService;
    private final EmpleadoPagoDAO empleadoDAO;

    private final Label lblCuenta = new Label();
    private final Label lblTotal = new Label();

    private final ComboBox<String> cboModalidad = new ComboBox<>();
    private final ComboBox<String> cboMetodo = new ComboBox<>();
    private final ComboBox<Empleado> cboEmpleado = new ComboBox<>();
    private final ComboBox<String> cboResultadoTarjeta = new ComboBox<>();

    private final TextField txtMontoRecibido = new TextField();
    private final TextField txtVuelto = new TextField();
    private final TextField txtReferencia = new TextField();

    private final Label lblEmpleado = new Label("Responsable:");
    private final Label lblMontoRecibido = new Label("Monto recibido:");
    private final Label lblVuelto = new Label("Vuelto:");
    private final Label lblReferencia = new Label("Referencia de tarjeta:");
    private final Label lblResultadoTarjeta
            = new Label("Resultado de tarjeta (simulación):");

    private final Button btnConfirmar
            = new Button("CONFIRMAR PAGO");

    private final Button btnCancelar
            = new Button("CANCELAR");

    // Paleta visual
    private final String VERDE = "#285943";
    private final String VERDE_HOVER = "#1D4432";
    private final String FONDO = "#F3F6F4";
    private final String BORDE = "#DCE5DF";
    private final String TEXTO = "#26352C";

    public FrmRegistroPago(Cuenta cuenta) {

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una cuenta."
            );
        }

        this.cuenta = cuenta;
        this.pagoService = new PagoService();
        this.empleadoDAO = new EmpleadoPagoDAO();

        configurarFormulario();
        configurarEventos();
        actualizarCampos();
        cargarEmpleados();
    }

    // ==========================================
    // CONFIGURACIÓN VISUAL DEL FORMULARIO
    // ==========================================
    private void configurarFormulario() {

        setPadding(new Insets(24));
        setStyle("-fx-background-color: " + FONDO + ";");

        Label titulo = new Label("REGISTRO DE PAGO");
        titulo.setStyle(
                "-fx-font-size: 25px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        Label subtitulo = new Label(
                "Registra el pago de la cuenta seleccionada"
        );
        subtitulo.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: #718078;"
                + "-fx-font-family: 'Segoe UI';"
        );

        lblCuenta.setText("Cuenta: " + cuenta.getId());
        lblCuenta.setStyle(
                "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        BigDecimal total = cuenta.getTotal() == null
                ? BigDecimal.ZERO
                : cuenta.getTotal();

        lblTotal.setText(
                "Total a pagar: S/ "
                + total.setScale(2, RoundingMode.HALF_UP)
        );

        lblTotal.setMaxWidth(Double.MAX_VALUE);
        lblTotal.setPadding(new Insets(14));
        lblTotal.setStyle(
                "-fx-background-color: #E7F1E9;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #C9DCCF;"
                + "-fx-border-radius: 8;"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + VERDE + ";"
                + "-fx-font-family: 'Segoe UI';"
        );

        // Opciones de modalidad
        cboModalidad.setItems(
                FXCollections.observableArrayList(
                        "DIRECTO_MOZO",
                        "EN_CAJA"
                )
        );
        cboModalidad.setValue("EN_CAJA");

        // Métodos de pago
        cboMetodo.setItems(
                FXCollections.observableArrayList(
                        "EFECTIVO",
                        "TARJETA"
                )
        );
        cboMetodo.setValue("EFECTIVO");

        // Simulación de resultado de tarjeta
        cboResultadoTarjeta.setItems(
                FXCollections.observableArrayList(
                        "APROBADA (SIMULACIÓN)",
                        "RECHAZADA"
                )
        );
        cboResultadoTarjeta.setValue("APROBADA (SIMULACIÓN)");

        // Campos de texto
        txtMontoRecibido.setPromptText("Ejemplo: 150.00");
        txtVuelto.setEditable(false);
        txtVuelto.setPromptText("Se calcula automáticamente");
        txtReferencia.setPromptText("Código de operación");

        estilizarCampo(txtMontoRecibido);
        estilizarCampo(txtVuelto);
        estilizarCampo(txtReferencia);

        estilizarCombo(cboModalidad);
        estilizarCombo(cboMetodo);
        estilizarCombo(cboEmpleado);
        estilizarCombo(cboResultadoTarjeta);

        // Etiquetas
        estilizarEtiqueta(lblEmpleado);
        estilizarEtiqueta(lblMontoRecibido);
        estilizarEtiqueta(lblVuelto);
        estilizarEtiqueta(lblReferencia);
        estilizarEtiqueta(lblResultadoTarjeta);

        // Distribución del formulario
        GridPane formulario = new GridPane();
        formulario.setHgap(18);
        formulario.setVgap(14);
        formulario.setPadding(new Insets(20));

        formulario.add(lblCuenta, 0, 0, 2, 1);
        formulario.add(lblTotal, 0, 1, 2, 1);

        formulario.add(crearEtiqueta("Modalidad:"), 0, 2);
        formulario.add(cboModalidad, 1, 2);

        formulario.add(crearEtiqueta("Método de pago:"), 0, 3);
        formulario.add(cboMetodo, 1, 3);

        formulario.add(lblEmpleado, 0, 4);
        formulario.add(cboEmpleado, 1, 4);

        formulario.add(lblMontoRecibido, 0, 5);
        formulario.add(txtMontoRecibido, 1, 5);

        formulario.add(lblVuelto, 0, 6);
        formulario.add(txtVuelto, 1, 6);

        formulario.add(lblReferencia, 0, 7);
        formulario.add(txtReferencia, 1, 7);

        formulario.add(lblResultadoTarjeta, 0, 8);
        formulario.add(cboResultadoTarjeta, 1, 8);

        cboModalidad.setMaxWidth(Double.MAX_VALUE);
        cboMetodo.setMaxWidth(Double.MAX_VALUE);
        cboEmpleado.setMaxWidth(Double.MAX_VALUE);
        cboResultadoTarjeta.setMaxWidth(Double.MAX_VALUE);

        txtMontoRecibido.setMaxWidth(Double.MAX_VALUE);
        txtVuelto.setMaxWidth(Double.MAX_VALUE);
        txtReferencia.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHgrow(
                cboModalidad,
                javafx.scene.layout.Priority.ALWAYS
        );
        GridPane.setHgrow(
                cboMetodo,
                javafx.scene.layout.Priority.ALWAYS
        );
        GridPane.setHgrow(
                cboEmpleado,
                javafx.scene.layout.Priority.ALWAYS
        );

        // Botones
        estilizarBoton(btnConfirmar, VERDE, VERDE_HOVER);
        estilizarBoton(btnCancelar, "#75847B", "#5E6C63");

        HBox botones = new HBox(12, btnConfirmar, btnCancelar);
        botones.setAlignment(Pos.CENTER_RIGHT);
        botones.setPadding(new Insets(8, 0, 0, 0));

        VBox panelFormulario = new VBox(15, formulario, botones);
        panelFormulario.setPadding(new Insets(10));
        panelFormulario.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 12;"
        );

        VBox encabezado = new VBox(6, titulo, subtitulo);
        encabezado.setPadding(new Insets(0, 0, 5, 0));

        VBox contenido = new VBox(
                18,
                encabezado,
                panelFormulario
        );
        contenido.setPadding(new Insets(4));

        setCenter(contenido);
    }

    // ==========================================
    // MÉTODOS AUXILIARES DE ESTILO
    // ==========================================
    private Label crearEtiqueta(String texto) {
        Label etiqueta = new Label(texto);
        estilizarEtiqueta(etiqueta);
        return etiqueta;
    }

    private void estilizarEtiqueta(Label etiqueta) {
        etiqueta.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-family: 'Segoe UI';"
        );
    }

    private void estilizarCampo(TextField campo) {
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
    }

    private void estilizarCombo(ComboBox<?> combo) {
        combo.setPrefHeight(38);
        combo.setStyle(
                "-fx-background-color: #FFFFFF;"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-font-size: 13px;"
                + "-fx-font-family: 'Segoe UI';"
        );
    }

    private void estilizarBoton(
            Button boton,
            String color,
            String colorHover) {

        String estiloBase
                = "-fx-background-color: " + color + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 11px;"
                + "-fx-font-weight: bold;"
                + "-fx-font-family: 'Segoe UI';"
                + "-fx-background-radius: 7;"
                + "-fx-padding: 11 16 11 16;"
                + "-fx-cursor: hand;";

        boton.setPrefHeight(40);
        boton.setMinWidth(140);
        boton.setStyle(estiloBase);

        boton.setOnMouseEntered(e
                -> boton.setStyle(
                        "-fx-background-color: " + colorHover + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-family: 'Segoe UI';"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 11 16 11 16;"
                        + "-fx-cursor: hand;"
                )
        );

        boton.setOnMouseExited(e
                -> boton.setStyle(estiloBase)
        );
    }

    // ==========================================
    // EVENTOS
    // ==========================================
    private void configurarEventos() {

        cboModalidad.setOnAction(e -> {
            cargarEmpleados();
            actualizarCampos();
        });

        cboMetodo.setOnAction(e -> actualizarCampos());

        cboResultadoTarjeta.setOnAction(e -> actualizarCampos());

        txtMontoRecibido.textProperty().addListener(
                (observable, anterior, nuevo) -> calcularVuelto()
        );

        btnConfirmar.setOnAction(e -> confirmarPago());

        btnCancelar.setOnAction(e -> cerrarVentana());
    }

    // ==========================================
    // CARGAR EMPLEADOS
    // ==========================================
    private void cargarEmpleados() {
        cboEmpleado.getItems().clear();

        try {
            if ("DIRECTO_MOZO".equals(cboModalidad.getValue())) {
                List<Mozo> mozos = empleadoDAO.listarMozos();
                cboEmpleado.getItems().setAll(mozos);
            } else {
                List<Cajero> cajeros = empleadoDAO.listarCajeros();
                cboEmpleado.getItems().setAll(cajeros);
            }

            if (!cboEmpleado.getItems().isEmpty()) {
                cboEmpleado.getSelectionModel().selectFirst();
            }

        } catch (SQLException ex) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar empleados",
                    "No se pudieron consultar los empleados de la base de datos.\n"
                    + ex.getMessage()
            );
        }
    }

    // ==========================================
    // MOSTRAR U OCULTAR CAMPOS
    // ==========================================
    private void actualizarCampos() {
        boolean esEfectivo
                = "EFECTIVO".equals(cboMetodo.getValue());

        boolean esTarjeta
                = "TARJETA".equals(cboMetodo.getValue());

        lblMontoRecibido.setVisible(esEfectivo);
        lblMontoRecibido.setManaged(esEfectivo);
        txtMontoRecibido.setVisible(esEfectivo);
        txtMontoRecibido.setManaged(esEfectivo);

        lblVuelto.setVisible(esEfectivo);
        lblVuelto.setManaged(esEfectivo);
        txtVuelto.setVisible(esEfectivo);
        txtVuelto.setManaged(esEfectivo);

        lblReferencia.setVisible(esTarjeta);
        lblReferencia.setManaged(esTarjeta);
        txtReferencia.setVisible(esTarjeta);
        txtReferencia.setManaged(esTarjeta);

        lblResultadoTarjeta.setVisible(esTarjeta);
        lblResultadoTarjeta.setManaged(esTarjeta);
        cboResultadoTarjeta.setVisible(esTarjeta);
        cboResultadoTarjeta.setManaged(esTarjeta);

        if (esTarjeta) {
            txtMontoRecibido.clear();
            txtVuelto.clear();
        } else {
            txtReferencia.clear();
        }

        calcularVuelto();
    }

    // ==========================================
    // CALCULAR VUELTO
    // ==========================================
    private void calcularVuelto() {
        if (!"EFECTIVO".equals(cboMetodo.getValue())) {
            txtVuelto.clear();
            return;
        }

        try {
            BigDecimal recibido = new BigDecimal(
                    txtMontoRecibido.getText().trim()
            );

            BigDecimal total = cuenta.getTotal();

            BigDecimal vuelto = recibido.subtract(total)
                    .setScale(2, RoundingMode.HALF_UP);

            if (vuelto.signum() >= 0) {
                txtVuelto.setText(vuelto.toPlainString());
            } else {
                txtVuelto.setText("Monto insuficiente");
            }

        } catch (NumberFormatException | NullPointerException ex) {
            txtVuelto.clear();
        }
    }

    // ==========================================
    // CONFIRMAR PAGO
    // ==========================================
    private void confirmarPago() {
        if (cboEmpleado.getValue() == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Falta responsable",
                    "Selecciona un mozo o cajero."
            );
            return;
        }

        if (!"PENDIENTE".equalsIgnoreCase(cuenta.getEstado())) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Cuenta no pendiente",
                    "Solo se puede registrar el pago de una cuenta pendiente."
            );
            return;
        }

        Empleado empleado = cboEmpleado.getValue();

        Integer mozoId = null;
        Integer cajeroId = null;

        if ("DIRECTO_MOZO".equals(cboModalidad.getValue())) {
            mozoId = empleado.getId();
        } else {
            cajeroId = empleado.getId();
        }

        BigDecimal total = cuenta.getTotal();

        try {
            if ("EFECTIVO".equals(cboMetodo.getValue())) {
                registrarEfectivo(total, mozoId, cajeroId);
            } else {
                registrarTarjeta(total, mozoId, cajeroId);
            }

        } catch (NumberFormatException ex) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Monto inválido",
                    "Ingresa un monto recibido válido, por ejemplo 150.00."
            );
        } catch (SQLException | IllegalArgumentException ex) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo registrar el pago",
                    ex.getMessage()
            );
        }
    }

    // ==========================================
    // REGISTRAR PAGO EN EFECTIVO
    // ==========================================
    private void registrarEfectivo(
            BigDecimal total,
            Integer mozoId,
            Integer cajeroId) throws SQLException {

        String texto = txtMontoRecibido.getText().trim();

        if (texto.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingresa el monto recibido."
            );
        }

        BigDecimal recibido = new BigDecimal(texto)
                .setScale(2, RoundingMode.HALF_UP);

        if (recibido.compareTo(total) < 0) {
            throw new IllegalArgumentException(
                    "El monto recibido no cubre el total de la cuenta."
            );
        }

        pagoService.registrarPagoEfectivo(
                cuenta.getId(),
                total,
                recibido,
                cboModalidad.getValue(),
                mozoId,
                cajeroId
        );

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Pago registrado",
                "El pago en efectivo se registró correctamente.\n"
                + "Vuelto: S/ "
                + recibido.subtract(total)
                        .setScale(2, RoundingMode.HALF_UP)
        );

        cerrarVentana();
    }

    // ==========================================
    // REGISTRAR PAGO CON TARJETA
    // ==========================================
    private void registrarTarjeta(
            BigDecimal total,
            Integer mozoId,
            Integer cajeroId) throws SQLException {

        String referencia = txtReferencia.getText().trim();

        boolean aprobada
                = "APROBADA (SIMULACIÓN)".equals(
                        cboResultadoTarjeta.getValue()
                );

        if (aprobada && referencia.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingresa una referencia para la simulación aprobada."
            );
        }

        pagoService.registrarResultadoTarjeta(
                cuenta.getId(),
                total,
                cboModalidad.getValue(),
                mozoId,
                cajeroId,
                aprobada,
                referencia
        );

        if (aprobada) {
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Pago simulado",
                    "El pago con tarjeta se registró como aprobado en la simulación.\n"
                    + "Esto no representa una autorización bancaria real."
            );
            cerrarVentana();
        } else {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Pago rechazado",
                    "El intento quedó rechazado. La cuenta debe seguir pendiente."
            );
        }
    }

    // ==========================================
    // CERRAR VENTANA
    // ==========================================
    private void cerrarVentana() {
        if (getScene() != null
                && getScene().getWindow() instanceof Stage ventana) {
            ventana.close();
        }
    }

    // ==========================================
    // MOSTRAR ALERTAS
    // ==========================================
    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
