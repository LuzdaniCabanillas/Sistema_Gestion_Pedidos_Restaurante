package service;

import controlador.MesaDAO;
import controlador.MozoDAO;
import controlador.PedidoDAO;

import java.util.List;

import modelo.EstadoPedido;
import modelo.Mesa;
import modelo.Mozo;
import modelo.Pedido;

public class PedidoService {

    private PedidoDAO pedidoDAO;
    private MesaDAO mesaDAO;
    private MozoDAO mozoDAO;

    public PedidoService() {

        pedidoDAO = new PedidoDAO();
        mesaDAO = new MesaDAO();
        mozoDAO = new MozoDAO();
    }

    public boolean registrarPedido(int mesaId, int mozoId) {

        if (mesaId <= 0 || mozoId <= 0) {
            System.out.println("Mesa y mozo deben ser válidos.");
            return false;
        }

        Mesa mesa = mesaDAO.buscarPorId(mesaId);
        if (mesa == null) {
            System.out.println("La mesa no existe.");
            return false;
        }

        Mozo mozo = buscarMozo(mozoId);
        if (mozo == null) {
            System.out.println("El mozo no existe.");
            return false;
        }
        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setMozo(mozo);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setSubTotal(0);
        return pedidoDAO.insertar(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoDAO.listar();
    }

    public Pedido buscarPedido(int id) {
        return pedidoDAO.buscarPorId(id);
    }

    public boolean actualizarPedido(int id, int mesaId, int mozoId) {
        Pedido pedido = pedidoDAO.buscarPorId(id);

        if (pedido == null) {
            System.out.println("El pedido no existe.");
            return false;
        }

        if (!EstadoPedido.PENDIENTE.equals(pedido.getEstado())) {
            System.out.println("Solo se puede modificar " + "un pedido pendiente.");
            return false;
        }

        Mesa mesa = mesaDAO.buscarPorId(mesaId);
        if (mesa == null) {
            return false;
        }
        Mozo mozo = buscarMozo(mozoId);
        if (mozo == null) {
            return false;
        }
        pedido.setMesa(mesa);
        pedido.setMozo(mozo);
        return pedidoDAO.actualizar(pedido);
    }

    public boolean eliminarPedido(int id) {
        Pedido pedido = pedidoDAO.buscarPorId(id);
        if (pedido == null) {
            return false;
        }
        if (!pedido.puedeEliminarse()) {
            System.out.println("Solo se puede eliminar " + "un pedido pendiente.");
            return false;
        }
        return pedidoDAO.eliminar(id);
    }

    public boolean enviarACocina(int id) {
        Pedido pedido = pedidoDAO.buscarPorId(id);
        if (pedido == null) {
            return false;
        }

        if (!EstadoPedido.PENDIENTE.equals(pedido.getEstado())) {
            System.out.println("Solo un pedido pendiente " + "puede enviarse a cocina.");
            return false;
        }
        pedido.enviarACocina();
        return pedidoDAO.actualizarEstado(id, pedido.getEstado());
    }

    public boolean entregarPedido(int id) {
        Pedido pedido = pedidoDAO.buscarPorId(id);
        if (pedido == null) {
            return false;
        }

        if (!EstadoPedido.EN_PREPARACION.equals(pedido.getEstado())) {
            System.out.println("Solo un pedido en preparación " + "puede entregarse.");
            return false;
        }
        pedido.entregarPedido();
        return pedidoDAO.actualizarEstado(id, pedido.getEstado());
    }

    public List<Mesa> listarMesas() {
        return mesaDAO.listar();
    }

    public List<Mozo> listarMozos() {
        return mozoDAO.listar();
    }

    private Mozo buscarMozo(int id) {
        for (Mozo mozo : mozoDAO.listar()) {
            if (mozo.getId() == id) {
                return mozo;
            }
        }
        return null;
    }
}