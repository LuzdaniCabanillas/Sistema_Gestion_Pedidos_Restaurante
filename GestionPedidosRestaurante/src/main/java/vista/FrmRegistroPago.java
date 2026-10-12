
package vista;

/**
 SERGIO
 */
import controlador.EmpleadoPagoDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
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
    private final Label lblResultadoTarjeta =
            new Label("Resultado de tarjeta (simulación):");

    private final Button btnConfirmar = new Button("CONFIRMAR PAGO");
    private final Button btnCancelar = new Button("CANCELAR");

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

    private void configurarFormulario() {
        setPadding(new Insets(20));

        Label titulo = new Label("REGISTRO DE PAGO");
        titulo.setStyle(
            "-fx-font-size: 20px; -fx-font-weight: bold;"
        );

        lblCuenta.setText("Cuenta: " + cuenta.getId());

        BigDecimal total = cuenta.getTotal() == null
                ? BigDecimal.ZERO
                : cuenta.getTotal();

        lblTotal.setText(
            "Total a pagar: S/ " +
            total.setScale(2, RoundingMode.HALF_UP)
        );

        cboModalidad.setItems(FXCollections.observableArrayList(
            "DIRECTO_MOZO",
            "EN_CAJA"
        ));
        cboModalidad.setValue("EN_CAJA");

        cboMetodo.setItems(FXCollections.observableArrayList(
            "EFECTIVO",
            "TARJETA"
        ));
        cboMetodo.setValue("EFECTIVO");

        cboResultadoTarjeta.setItems(
            FXCollections.observableArrayList(
                "APROBADA (SIMULACIÓN)",
                "RECHAZADA"
            )
        );
        cboResultadoTarjeta.setValue("APROBADA (SIMULACIÓN)");

        txtMontoRecibido.setPromptText("Ejemplo: 150.00");
        txtVuelto.setEditable(false);
        txtVuelto.setPromptText("Se calcula automáticamente");
        txtReferencia.setPromptText("Código de operación");

        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(12);

        formulario.add(lblCuenta, 0, 0, 2, 1);
        formulario.add(lblTotal, 0, 1, 2, 1);

        formulario.add(new Label("Modalidad:"), 0, 2);
        formulario.add(cboModalidad, 1, 2);

        formulario.add(new Label("Método de pago:"), 0, 3);
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

        HBox botones = new HBox(10, btnConfirmar, btnCancelar);

        VBox contenido = new VBox(15, titulo, formulario, botones);
        contenido.setPadding(new Insets(5));

        setCenter(contenido);
    }

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

    private void actualizarCampos() {
        boolean esEfectivo =
            "EFECTIVO".equals(cboMetodo.getValue());

        boolean esTarjeta =
            "TARJETA".equals(cboMetodo.getValue());

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

    private void registrarTarjeta(
            BigDecimal total,
            Integer mozoId,
            Integer cajeroId) throws SQLException {

        String referencia = txtReferencia.getText().trim();

        boolean aprobada =
            "APROBADA (SIMULACIÓN)".equals(
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

    private void cerrarVentana() {
        if (getScene() != null
                && getScene().getWindow() instanceof Stage ventana) {
            ventana.close();
        }
    }

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
