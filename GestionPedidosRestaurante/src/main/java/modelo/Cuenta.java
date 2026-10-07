package modelo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Cuenta {

    private int id;
    private LocalDateTime fechaGeneracion;
    private double subTotal;
    private double igv;
    private double total;
    private EstadoCuenta estado;

    // Constructor vacío
    public Cuenta() {
    }

    // Constructor para REGISTRAR una cuenta nueva
    public Cuenta(double subTotal, EstadoCuenta estado) {

        this.fechaGeneracion = LocalDateTime.now();
        this.subTotal = subTotal;
        this.igv = calcularIGV();
        this.total = calcularTotal();
        this.estado = estado;
    }

    // Constructor usado por el mapper
    public Cuenta(
            double subTotal,
            double igv,
            double total,
            EstadoCuenta estado) {

        this.subTotal = subTotal;
        this.igv = igv;
        this.total = total;
        this.estado = estado;
    }

    public void generarCuenta() {
        this.fechaGeneracion = LocalDateTime.now();
        this.igv = calcularIGV();
        this.total = calcularTotal();
    }

    public double calcularIGV() {
        return subTotal * 0.18;
    }

    public double calcularTotal() {
        return subTotal + igv;
    }

    public void cambiarEstado(EstadoCuenta estado) {
        this.estado = estado;
    }

    public void cerrarCuenta() {
        this.estado = EstadoCuenta.CERRADA;
    }

    // GETTERS Y SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(
            LocalDateTime fechaGeneracion) {

        this.fechaGeneracion = fechaGeneracion;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
        this.igv = calcularIGV();
        this.total = calcularTotal();
    }

    public double getIgv() {
        return igv;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuenta estado) {
        this.estado = estado;
    }

}
