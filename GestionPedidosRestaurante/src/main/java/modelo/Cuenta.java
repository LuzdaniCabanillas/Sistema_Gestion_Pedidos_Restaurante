package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cuenta {

	private int id;
	private LocalDate fechaGeneracion;
	private BigDecimal  subTotal;
	private BigDecimal  IGV;
	private BigDecimal  total;
	private String estado;
	private int pedidoId;
	private String metodoPago;
	private LocalDateTime fechaPago;

	public Cuenta() {
		this.fechaGeneracion = LocalDate.now();
		this.subTotal = BigDecimal.ZERO;
		this.IGV = BigDecimal.ZERO;
		this.total = BigDecimal.ZERO;
		this.estado = "PENDIENTE";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getFechaGeneracion() {
		return fechaGeneracion;
	}

	public void setFechaGeneracion(LocalDate fechaGeneracion) {
		this.fechaGeneracion = fechaGeneracion;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public BigDecimal getIGV() {
		return IGV;
	}

	public void setIGV(BigDecimal IGV) {
		this.IGV = IGV;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(int pedidoId) {
		this.pedidoId = pedidoId;
	}

	public String getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	public LocalDateTime getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(LocalDateTime fechaPago) {
		this.fechaPago = fechaPago;
	}


	public void calcularImportes() {
		this.IGV = subTotal
				.multiply(new BigDecimal("0.18"))
				.setScale(2, java.math.RoundingMode.HALF_UP);

		this.total = subTotal.add(IGV)
				.setScale(2, java.math.RoundingMode.HALF_UP);
	}








	public void generarCuenta() {

	}

	public double calcularIGV() {
		return 0;
	}

	public double calcularTotal() {
		return 0;
	}

	public void registrarPago() {

	}

	public void cambiarEstado() {

	}

	public void cerrarCuenta() {

	}

}
