package service;

import controlador.CuentaDAO;
import controlador.DetallePedidoDAO;
import controlador.PedidoDAO;
import controlador.PlatoDAO;
import java.sql.SQLException;
import java.util.List;
import modelo.Cuenta;
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
    private final CuentaDAO cuentaDAO;
    private final CuentaService cuentaService;

    public PedidoService() {
        pedidoDAO = new PedidoDAO();
        detallePedidoDAO = new DetallePedidoDAO();
        platoDAO = new PlatoDAO();

        cuentaDAO = new CuentaDAO();
        cuentaService = new CuentaService();
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

    // Agregar un plato a un pedido.
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

        if (!"DISPONIBLE".equalsIgnoreCase(plato.getEstado())) {
            throw new IllegalStateException(
                    "El plato seleccionado no está disponible."
            );
        }

        boolean pedidoEntregado
                = "ENTREGADO".equalsIgnoreCase(pedido.getEstado());

        // Si el pedido ya fue entregado, comprobar la cuenta.
        if (pedidoEntregado) {

            if (pedido.getId() <= 0) {
                throw new IllegalStateException(
                        "El pedido entregado no tiene un ID válido."
                );
            }

            Cuenta cuenta = cuentaDAO.buscarPorPedidoId(
                    pedido.getId()
            );

            if (cuenta == null) {
                throw new IllegalStateException(
                        "El pedido ya fue entregado, pero todavía no "
                                + "tiene una cuenta. Genere la cuenta antes "
                                + "de agregar más platos."
                );
            }

            if (!"PENDIENTE".equalsIgnoreCase(cuenta.getEstado())) {
                throw new IllegalStateException(
                        "La cuenta ya fue pagada. Debe crear un "
                                + "nuevo pedido para agregar más platos."
                );
            }
        } else if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado())
                && !"EN_PREPARACION".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException(
                    "El pedido no está disponible para modificaciones."
            );
        }

        // Solo incrementar un detalle que todavía está pendiente.
        // Nunca modificar cantidades de platos ya entregados
        // ni de platos que ya están en preparación.
        if (!pedidoEntregado) {
            for (DetallePedido detalle : pedido.getDetalles()) {

                boolean mismoPlato
                        = detalle.getPlato() != null
                        && detalle.getPlato().getId() == plato.getId();

                boolean detallePendiente
                        = "PENDIENTE".equalsIgnoreCase(
                        detalle.getEstado()
                );

                if (mismoPlato && detallePendiente) {

                    detalle.setCantidad(
                            detalle.getCantidad() + 1
                    );

                    detalle.calcularSubtotal();
                    pedido.calcularSubtotal();

                    if (pedido.getId() > 0 && detalle.getId() > 0) {
                        detallePedidoDAO.actualizar(detalle);
                        pedidoDAO.actualizar(pedido);
                    }

                    pedido.setCambiosSinGuardar(false);
                    pedido.setPendientesDeEnvio(true);
                    return;
                }
            }
        }

        // Crear un nuevo detalle para el plato adicional.
        DetallePedido nuevoDetalle = new DetallePedido();

        nuevoDetalle.setCantidad(1);
        nuevoDetalle.setPrecioUnitario(plato.getPrecio());
        nuevoDetalle.setPlato(plato);
        nuevoDetalle.setPedido(pedido);
        nuevoDetalle.setEstado("PENDIENTE");
        nuevoDetalle.calcularSubtotal();

        // Guardar primero el nuevo detalle si el pedido ya existe.
        if (pedido.getId() > 0) {
            detallePedidoDAO.insertar(nuevoDetalle);
        }

        pedido.getDetalles().add(nuevoDetalle);
        pedido.calcularSubtotal();

        // Un pedido entregado vuelve a preparación cuando recibe
        // un nuevo plato pendiente.
        if (pedidoEntregado) {
            pedido.setEstado("EN_PREPARACION");
        }

        pedido.setCambiosSinGuardar(false);
        pedido.setPendientesDeEnvio(true);

        if (pedido.getId() > 0) {
            pedidoDAO.actualizar(pedido);

            // Actualizar la misma cuenta pendiente con los nuevos
            // importes del pedido que ya se guardó en la BD.
            if (pedidoEntregado) {
                cuentaDAO.actualizarCuentaPendiente(
                        pedido.getId()
                );
            }
        }
    }

    //>
    // MODIFICAR CANTIDAD DE UN PLATO
    public void modificarCantidad(Pedido pedido, DetallePedido detalle,
                                  int nuevaCantidad) throws SQLException {

        if (pedido == null || detalle == null) {
            throw new IllegalArgumentException("Seleccione un plato.");
        }

        if (nuevaCantidad < 1) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado())
                && !"EN_PREPARACION".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException(
                    "El pedido ya no se puede modificar.");
        }

        detalle.setCantidad(nuevaCantidad);
        detalle.calcularSubtotal();
        pedido.calcularSubtotal();

        // Si el detalle ya está guardado, actualizarlo en la BD.
        if (pedido.getId() > 0 && detalle.getId() > 0) {
            detallePedidoDAO.actualizar(detalle);
            pedidoDAO.actualizar(pedido);
        }

        pedido.setCambiosSinGuardar(false);
        pedido.setPendientesDeEnvio(true);
    }

    // QUITAR UN PLATO: DISMINUIR UNO O ELIMINAR EL ÚLTIMO
    public void quitarPlato(Pedido pedido, DetallePedido detalle)
            throws SQLException {

        if (pedido == null || detalle == null) {
            throw new IllegalArgumentException("Seleccione un plato.");
        }

        if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado())
                && !"EN_PREPARACION".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException(
                    "El pedido ya no se puede modificar.");
        }

        if (detalle.getCantidad() > 1) {
            // Ejemplo: de 2 platos pasa a 1.
            detalle.setCantidad(detalle.getCantidad() - 1);
            detalle.calcularSubtotal();

            if (pedido.getId() > 0 && detalle.getId() > 0) {
                detallePedidoDAO.actualizar(detalle);
            }

        } else {
            // Si queda uno, quitar el detalle por completo.
            if (pedido.getId() > 0 && detalle.getId() > 0) {
                detallePedidoDAO.eliminar(detalle.getId());
            }

            pedido.getDetalles().remove(detalle);
        }

        pedido.calcularSubtotal();

        if (pedido.getId() > 0) {
            pedidoDAO.actualizar(pedido);
        }

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

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalStateException(
                    "No se puede enviar un pedido sin platos."
            );
        }

        String estado = pedido.getEstado();

        // Primer envío: PENDIENTE -> EN_PREPARACION.
        if ("PENDIENTE".equalsIgnoreCase(estado)) {
            pedido.enviarACocina();
            pedidoDAO.actualizar(pedido);
            return;
        }

        // Reenvío de cambios de un pedido que ya está en preparación.
        if ("EN_PREPARACION".equalsIgnoreCase(estado)) {
            if (!pedido.isPendientesDeEnvio()) {
                throw new IllegalStateException(
                        "No hay platos nuevos o modificaciones para enviar a cocina."
                );
            }

            pedido.enviarACocina();
            pedidoDAO.actualizar(pedido);
            return;
        }

        throw new IllegalStateException(
                "Solo se pueden enviar pedidos pendientes o con cambios por enviar."
        );
    }

    //Entergar Pedido
    public void entregarPedido(Pedido pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no existe.");
        }

        if (!"EN_PREPARACION".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException(
                    "Solo se pueden entregar pedidos en preparación."
            );
        }

        if (!pedido.entregarPedido()) {
            throw new IllegalStateException(
                    "No se pudo registrar la entrega del pedido."
            );
        }

        // Actualiza el estado; no elimina el pedido ni sus detalles.
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

    public void enviarPlatoACocina(DetallePedido detalle)
            throws SQLException {

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un plato."
            );
        }

        if (!"PENDIENTE".equalsIgnoreCase(detalle.getEstado())) {
            throw new IllegalStateException(
                    "Solo se pueden enviar a cocina platos pendientes."
            );
        }

        Pedido pedido = detalle.getPedido();

        if (pedido == null) {
            throw new IllegalStateException(
                    "El plato no tiene un pedido asociado."
            );
        }

        detalle.setEstado("EN_PREPARACION");
        detallePedidoDAO.actualizarEstado(detalle);

        // El pedido general pasa a preparación cuando se envía
        // al menos uno de sus platos.
        if ("PENDIENTE".equalsIgnoreCase(pedido.getEstado())) {
            pedido.setEstado("EN_PREPARACION");
            pedidoDAO.actualizar(pedido);
        }
    }

    public void entregarPlato(DetallePedido detalle)
            throws SQLException {

        if (detalle == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un plato."
            );
        }

        Pedido pedido = detalle.getPedido();

        if (pedido == null || pedido.getId() <= 0) {
            throw new IllegalStateException(
                    "El plato no tiene un pedido válido asociado."
            );
        }

        if (!detalle.entregar()) {
            throw new IllegalStateException(
                    "Solo se pueden entregar platos en preparación."
            );
        }

        // Guardar la entrega del detalle seleccionado.
        detallePedidoDAO.actualizarEstado(detalle);

        // Consultar todos los detalles actualizados desde MySQL.
        List<DetallePedido> detalles
                = detallePedidoDAO.listarPorPedido(pedido.getId());

        boolean todosEntregados = !detalles.isEmpty();

        for (DetallePedido otroDetalle : detalles) {
            if (!"ENTREGADO".equalsIgnoreCase(
                    otroDetalle.getEstado())) {

                todosEntregados = false;
                break;
            }
        }

        if (todosEntregados) {

            // Todos los platos se entregaron.
            pedido.setEstado("ENTREGADO");
            pedidoDAO.actualizar(pedido);

            // Genera la cuenta automáticamente si no existe.
            // Si ya hay una cuenta pendiente, actualiza sus importes.
            cuentaService.generarCuenta(pedido.getId());

        } else {

            // Todavía quedan platos pendientes o en preparación.
            pedido.setEstado("EN_PREPARACION");
            pedidoDAO.actualizar(pedido);
        }
    }

    //>
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
