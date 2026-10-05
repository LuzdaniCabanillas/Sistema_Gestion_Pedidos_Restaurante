package service;

import controlador.DetallePedidoDAO;
import controlador.PlatoDAO;

import java.util.List;

import modelo.DetallePedido;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.Plato;

public class DetallePedidoService {

    private DetallePedidoDAO detalleDAO;
    private PedidoDAO pedidoDAO;
    private PlatoDAO platoDAO;

    public DetallePedidoService() {
        detalleDAO = new DetallePedidoDAO();
        pedidoDAO = new PedidoDAO();
        platoDAO = new PlatoDAO();
    }

    public boolean registrarDetalle(int pedidoId, int platoId, int cantidad) {

        if (pedidoId <= 0 || platoId <= 0 || cantidad <= 0) {
            System.out.println("Pedido, plato y cantidad " + "deben ser válidos.");
            return false;
        }
        Pedido pedido = pedidoDAO.buscarPorId(pedidoId);
        if (pedido == null) {
            return false;
        }

        if (!EstadoPedido.PENDIENTE.equals(pedido.getEstado())) {
            System.out.println("Solo se pueden agregar " + "detalles a un pedido pendiente.");
            return false;
        }
        Plato plato = buscarPlato(platoId);
        if (plato == null) {
            return false;
        }

        if (plato.getStock() < cantidad) {
            System.out.println("No hay stock suficiente.");
            return false;
        }
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setPlato(plato);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(plato.getPrecio());
        detalle.calcularSubTotal();
        boolean registrado = detalleDAO.insertar(detalle);
        if (registrado) {
            actualizarSubtotalPedido(pedidoId);
        }
        return registrado;
    }

    public List<DetallePedido>
    listarDetalles() {
        return detalleDAO.listar();
    }

    public List<DetallePedido> listarDetallesPorPedido(int pedidoId) {
        return detalleDAO.listarPorPedido(pedidoId);
    }

    public DetallePedido buscarDetalle(int id) {
        return detalleDAO.buscarPorId(id);
    }

    public boolean actualizarDetalle(int id, int pedidoId, int platoId, int cantidad) {
        DetallePedido detalle = detalleDAO.buscarPorId(id);

        if (detalle == null) {
            return false;
        }
        Pedido pedido = pedidoDAO.buscarPorId(pedidoId);
        if (pedido == null) {
            return false;
        }

        if (!EstadoPedido.PENDIENTE.equals(pedido.getEstado())) {
            System.out.println("Solo se puede modificar " + "un detalle de un pedido pendiente.");
            return false;
        }

        if (cantidad <= 0) {
            return false;
        }

        Plato plato = buscarPlato(platoId);

        if (plato == null) {
            return false;
        }

        if (plato.getStock() < cantidad) {
            return false;
        }

        detalle.setPedido(pedido);
        detalle.setPlato(plato);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(plato.getPrecio());
        detalle.calcularSubTotal();
        boolean actualizado = detalleDAO.actualizar(detalle);

        if (actualizado) {
            actualizarSubtotalPedido(pedidoId);
        }
        return actualizado;
    }

    public boolean eliminarDetalle(int id) {
        DetallePedido detalle = detalleDAO.buscarPorId(id);
        if (detalle == null) {
            return false;
        }

        Pedido pedido = pedidoDAO.buscarPorId(detalle.getPedidoId());

        if (pedido == null) {
            return false;
        }

        if (!EstadoPedido.PENDIENTE.equals(pedido.getEstado())) {
            System.out.println("Solo se puede eliminar " + "un detalle de un pedido pendiente.");
            return false;
        }

        boolean eliminado = detalleDAO.eliminar(id);
        if (eliminado) {
            actualizarSubtotalPedido(pedido.getId());
        }
        return eliminado;
    }

    public List<Pedido> listarPedidos() {
        return pedidoDAO.listar();
    }

    public List<Plato> listarPlatos() {
        return platoDAO.listar();
    }

    private Plato buscarPlato(int id) {
        for (Plato plato : platoDAO.listar()) {
            if (plato.getId() == id) {return plato;}
        }
        return null;
    }

    private void actualizarSubtotalPedido(int pedidoId) {
        List<DetallePedido> detalles = detalleDAO.listarPorPedido(pedidoId);
        double total = 0;
        for (DetallePedido detalle : detalles) {
            total += detalle.getSubTotal();
        }
        pedidoDAO.actualizarSubTotal(pedidoId, total);
    }
}