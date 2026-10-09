package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private int id;
    private LocalDateTime fechaHora;
    private String estado;
    private double subTotal;
    private Mesa mesa;
    private Mozo mozo;

    private List<DetallePedido> detalles;

    public Pedido() {
        this.detalles = new ArrayList<>();
        this.estado = "PENDIENTE";
        this.fechaHora = LocalDateTime.now();

    }

    public Pedido(int id, LocalDateTime fechaHora, String estado, double subTotal, Mesa mesa, Mozo mozo, List<DetallePedido> detalles) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.subTotal = subTotal;
        this.mesa = mesa;
        this.mozo = mozo;

        this.detalles = detalles != null
                ? detalles
                : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Mozo getMozo() {
        return mozo;
    }

    public void setMozo(Mozo mozo) {
        this.mozo = mozo;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles != null
                ? detalles
                : new ArrayList<>();

        for (DetallePedido detalle : this.detalles) {
            detalle.setPedido(this);
            detalle.calcularSubtotal();
        }

        calcularSubtotal();
    }

    public void agregarPlato(Plato plato) {
        for (DetallePedido detalle : detalles) {
            if (detalle.getPlato().getId() == plato.getId()) {
                detalle.setCantidad(detalle.getCantidad() + 1);
                detalle.calcularSubtotal();
                calcularSubtotal();
                return;
            }
        }

        DetallePedido nuevoDetalle = new DetallePedido(1, plato.getPrecio(), 0, 0, this, plato);
        nuevoDetalle.calcularSubtotal();
        detalles.add(nuevoDetalle);
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        double total = 0;
        for (DetallePedido detalle : detalles) {
            total += detalle.getSubTotal();
        }
        this.subTotal = total;
    }

    public void enviarACocina() {
        if ("PENDIENTE".equals(this.estado)) {

            this.estado = "EN_PREPARACION";
        }
    }

    public void entregarPedido() {
        if ("EN_PREPARACION".equals(this.estado)) {

            this.estado = "ENTREGADO";
        }

    }

    public void cerrarPedido() {

        if ("ENTREGADO".equals(this.estado)) {

            this.estado = "FINALIZADO";
        }

    }

    @Override
    public String toString() {

        return "Pedido " + id
                + " - Mesa " + mesa
                + " - " + estado;
    }

}
