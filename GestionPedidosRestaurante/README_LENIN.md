# CRUD Pedido y DetallePedido

## Arquitectura

```text
src/main/java/
├── modelo/
│   ├── EstadoPedido.java
│   ├── Pedido.java
│   ├── DetallePedido.java
│   └── Plato.java
├── controlador/
│   ├── PedidoDAO.java
│   ├── DetallePedidoDAO.java
│   └── PlatoDAO.java
├── service/
│   ├── PedidoService.java
│   └── DetallePedidoService.java
└── vista/
    ├── FrmPedido.java //pendiente
    └── FrmDetallePedido.java //pendiente
```

## Archivos nuevos
- `controlador/PedidoDAO.java`
- `controlador/DetallePedidoDAO.java`
- `service/PedidoService.java`
- `service/DetallePedidoService.java`
##### Aun en desarrollo
- `vista/FrmPedido.java`
- `vista/FrmDetallePedido.java`

## Flujo

`PENDIENTE -> EN_PREPARACION -> ENTREGADO`

Cocina es un subproceso externo: el sistema registra el envío a cocina y luego la entrega, pero no implementa una clase `Cocina`.

## Historias de usuario

### HU-01 Gestión de pedidos
Como mozo, quiero registrar y gestionar los pedidos de los clientes para controlar los pedidos asociados a una mesa y permitir su preparación y posterior entrega.
#### Reglas
- Un pedido debe tener una mesa.
- Un pedido debe tener un mozo.
- Un pedido nuevo inicia como PENDIENTE.
- Solo PENDIENTE puede pasar a EN_PREPARACION.
- Solo EN_PREPARACION puede pasar a ENTREGADO.
- Solo un pedido pendiente puede modificarse/eliminarse desde el CRUD.

### HU-02 Gestión de DetallePedido
Como mozo, quiero registrar los platos que forman parte de un pedido para especificar los productos solicitados por el cliente y calcular el subtotal correspondiente.
#### Reglas
- Todo detalle pertenece a un pedido.
- Todo detalle pertenece a un plato.
- La cantidad debe ser mayor que cero.
- El precio se obtiene del plato.
- El subtotal es: cantidad × precioUnitario
- El subtotal del pedido se actualiza cuando cambia un detalle. 
- Solo se pueden modificar detalles de pedidos PENDIENTE.