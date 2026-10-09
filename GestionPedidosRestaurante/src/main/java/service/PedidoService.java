package service;

import controlador.DetallePedidoDAO;
import controlador.PedidoDAO;
import controlador.PlatoDAO;
import java.sql.SQLException;
import java.util.List;
import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Mozo;
import modelo.Pedido;
import modelo.Plato;

public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final DetallePedidoDAO detallePedidoDAO;
    private final PlatoDAO platoDAO;
    private final AtencionMesaService atencionMesaService = null;

    public PedidoService() {
        pedidoDAO = new PedidoDAO();
        detallePedidoDAO = new DetallePedidoDAO();
        platoDAO = new PlatoDAO();
    }

    //Crear
    public Pedido crearPedido(Mesa mesa, Mozo mozo) throws SQLException {
        if (mesa == null) {
            throw new IllegalArgumentException("Debe seleccionar una mesa.");
        }
        if (mozo == null) {
            throw new IllegalArgumentException("Debe seleccionar un mozo.");
        }
        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setMozo(mozo);
        pedido.setEstado("PENDIENTE");
        pedido.calcularSubtotal();
        return pedido;
    }

    //Guardar
    public int guardarPedido(Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }
        if (pedido.getMesa() == null) {
            throw new IllegalArgumentException("El pedido debe tener una mesa.");
        }
        if (pedido.getMozo() == null) {
            throw new IllegalArgumentException("El pedido debe tener un mozo.");
        }
        if (pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un plato al pedido.");
        }
        pedido.calcularSubtotal();
        int idPedido = pedidoDAO.insertar(pedido);
        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.setPedido(pedido);
            detallePedidoDAO.insertar(detalle);
        }
        pedido.setCambiosSinGuardar(false);
        return idPedido;
    }

    //Agregar plato a pedido
    public void agregarPlato(Pedido pedido, Plato plato)
            throws SQLException {

        if (pedido == null) {
            throw new IllegalArgumentException(
                    "No existe un pedido activo."
            );
        }

        if (plato == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un plato."
            );
        }

        if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado()) && !"EN_PREPARACION".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException("El pedido ya no está disponible para modificar (Solo PENDIENTE o EN_PREPARACION).");
        }

        if (!"DISPONIBLE".equalsIgnoreCase(
                plato.getEstado())) {
            throw new IllegalStateException(
                    "El plato seleccionado no está disponible."
            );
        }

        pedido.setCambiosSinGuardar(true);
        pedido.setPendientesDeEnvio(true);

        for (DetallePedido detalle : pedido.getDetalles()) {

            if (detalle.getPlato().getId() == plato.getId()) {

                detalle.setCantidad(
                        detalle.getCantidad() + 1
                );

                detalle.calcularSubtotal();
                pedido.calcularSubtotal();

                if (pedido.getId() > 0 && detalle.getId() > 0) {

                    detallePedidoDAO.actualizar(detalle);
                    pedidoDAO.actualizar(pedido);
                }

                return;
            }
        }

        DetallePedido detalle = new DetallePedido();

        detalle.setCantidad(1);
        detalle.setPrecioUnitario(plato.getPrecio());
        detalle.setPlato(plato);
        detalle.setPedido(pedido);
        detalle.calcularSubtotal();

        if (pedido.getId() > 0) {

            detallePedidoDAO.insertar(detalle);
        }

        pedido.getDetalles().add(detalle);
        pedido.calcularSubtotal();

        pedido.setPendientesDeEnvio(true);

        if (pedido.getId() > 0) {

            pedidoDAO.actualizar(pedido);
        }
    }

    //Quitar plato de pedido
    public void quitarPlato(Pedido pedido, DetallePedido detalle) {
        if (pedido == null || detalle == null) {
            return;
        }
        if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado()) && !"EN_PREPARACION".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException("El pedido ya no está disponible para modificar (Solo PENDIENTE o EN_PREPARACION).");
        }
        pedido.getDetalles().remove(detalle);
        pedido.calcularSubtotal();

        pedido.setCambiosSinGuardar(false);
        pedido.setPendientesDeEnvio(true);
    }

    //Aumentar cantidad
    public void aumentarCantidad(Pedido pedido, DetallePedido detalle) {
        if (pedido == null || detalle == null) {
            return;
        }
        if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado()) && !"EN_PREPARACION".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException("El pedido ya no está disponible para modificar (Solo PENDIENTE o EN_PREPARACION).");
        }
        detalle.setCantidad(detalle.getCantidad() + 1);
        detalle.calcularSubtotal();
        pedido.calcularSubtotal();
        pedido.setCambiosSinGuardar(false);
        pedido.setPendientesDeEnvio(true);
    }

    //Disminuir cantidad
    public void disminuirCantidad(Pedido pedido, DetallePedido detalle) {
        if (pedido == null || detalle == null) {
            return;
        }
        if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado()) && !"EN_PREPARACION".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException("El pedido ya no está disponible para modificar (Solo PENDIENTE o EN_PREPARACION).");
        }
        if (detalle.getCantidad() > 1) {
            detalle.setCantidad(detalle.getCantidad() - 1);
            detalle.calcularSubtotal();
            pedido.calcularSubtotal();
        } else {
            pedido.getDetalles().remove(detalle);
            pedido.calcularSubtotal();
            pedido.setPendientesDeEnvio(true);
        }
    }

    //Listar Pedidos
    public List<Pedido> listarPedidos() throws SQLException {
        return pedidoDAO.listar();
    }

    //Buscar por Id
    public Pedido buscarPedidoPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del pedido debe ser mayor que cero.");
        }
        return pedidoDAO.buscarPorId(id);
    }

    //Cargar detalles de un pedido
    public List<DetallePedido> listarDetalles(int pedidoId) throws SQLException {
        if (pedidoId <= 0) {
            throw new IllegalArgumentException("El ID del pedido no es válido.");
        }
        return detallePedidoDAO.listarPorPedido(pedidoId);
    }

    //Actualizar Pedido
    public void actualizarPedido(Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }
        pedido.calcularSubtotal();
        pedidoDAO.actualizar(pedido);
        pedido.setCambiosSinGuardar(false);
    }

    //Eliminar Pedido
    public void eliminarPedido(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del pedido no es válido.");
        }
        pedidoDAO.eliminar(id);
    }

    //Enviar a acocina
    public void enviarACocina(Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no existe.");
        }
        if (pedido.getDetalles().isEmpty()) {
            throw new IllegalStateException("No se puede enviar un pedido sin platos.");
        }
        if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado()) && !"EN_PREPARACION".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException("El pedido debe estar PENDIENTE o EN_PREPARACION para enviarse a cocina.");
        }
        if (!pedido.isPendientesDeEnvio()) {
            throw new IllegalStateException("No hay platos nuevos o modificaciones para enviar a cocina.");
        }

        pedido.enviarACocina();
        pedidoDAO.actualizar(pedido);
    }

    //Entergar Pedido
    public void entregarPedido(Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no existe.");
        }
        if (!"EN_PREPARACION".equals(pedido.getEstado())) {
            throw new IllegalStateException("El pedido no está en preparación.");
        }
        pedido.entregarPedido();
        pedidoDAO.actualizar(pedido);
    }

    //CerrarPedido
    public void cerrarPedido(Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no existe.");
        }
        if (!"ENTREGADO".equals(pedido.getEstado())) {
            throw new IllegalStateException("El pedido debe estar entregado antes de cerrarlo.");
        }
        pedido.cerrarPedido();
        pedidoDAO.actualizar(pedido);
    }

    //Buscar plato por Id
    public Plato buscarPlatoPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del plato debe ser mayor que cero.");
        }
        return platoDAO.buscarPorId(id);
    }

    // Buscar plato por nombre
    public List<Plato> buscarPlatoPorNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un nombre para buscar.");
        }
        return platoDAO.buscarPorNombre(nombre.trim());
    }

    //Mostrar Carta
    public List<Plato> mostrarCarta() throws SQLException {
        return platoDAO.listar();
    }
}
