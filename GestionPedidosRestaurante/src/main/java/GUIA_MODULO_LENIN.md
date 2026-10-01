# Guía de implementación — Módulo de Cocina y Entrega

---

## 1. Objetivo del módulo

El módulo corresponde al flujo de **Cocina y Entrega del Pedido**.

El alcance contempla:

- Envío del pedido a cocina.
- Cambio del estado a `EN_PREPARACION`.
- Entrega del pedido.
- Cambio del estado a `ENTREGADO`.
- Persistencia del estado en MySQL.
- La **Cocina se considera un subproceso**, por lo que no se implementará como una clase o módulo


El sistema únicamente registra el envío del pedido a cocina y posteriormente la entrega.

---

### Archivos modificados

```text
src/
└── main/
    └── java/
        ├── MySQL/
        │   └── ConexionMySQL.java
        │
        └── restaurante/
            ├── EstadoPedido.java
            ├── Pedido.java
            ├── PedidoDAO.java
            ├── Mozo.java
            └── GestionPedidosRestaurante.java
```

---

# 2. Flujo del módulo

```text
PENDIENTE
    |
    | enviarACocina()
    v
EN_PREPARACION
    |
    | [Proceso de cocina fuera del sistema]
    |
    v
ENTREGADO
```

La cocina no se desarrolla internamente. El sistema únicamente registra el estado correspondiente.

---

# 3. Relación con el diagrama UML

La clase `Pedido` ya contempla:

```text
+ enviarACocina() : void
+ entregarPedido() : void
```

y:

```text
- estado : EstadoPedido
```

La implementación se concentra en `Pedido`.

La clase `Mozo` solicita las operaciones:

```text
Mozo
  |
  | enviarACocina()
  v
Pedido
  |
  | cambia estado
  v
EN_PREPARACION
```

y:

```text
Mozo
  |
  | entregarPedido()
  v
Pedido
  |
  | cambia estado
  v
ENTREGADO
```

El `Mozo` no cambia directamente el estado. La lógica pertenece a `Pedido`.

---

# 4. `restaurante/EstadoPedido.java`



Implementación:

```java
package restaurante;

public enum EstadoPedido {
    PENDIENTE,
    EN_PREPARACION,
    ENTREGADO
}
```

Se utiliza `enum` porque el pedido trabaja con un conjunto definido de estados:

```java
EstadoPedido.PENDIENTE
EstadoPedido.EN_PREPARACION
EstadoPedido.ENTREGADO
```

---

# 5. `restaurante/Pedido.java`





### `enviarACocina()`

```text
PENDIENTE
    |
    | enviarACocina()
    v
EN_PREPARACION
```

### `entregarPedido()`

```text
EN_PREPARACION
    |
    | entregarPedido()
    v
ENTREGADO
```

No se permite entregar directamente un pedido que todavía está `PENDIENTE`.

---

# 6. `restaurante/Mozo.java`

Para asignar un pedido:

```java
Mozo mozo = new Mozo();
Pedido pedido = new Pedido(1);

mozo.asignarPedido(pedido);
```

---

# 7. `ConexionMySQL.java`



Ahora otras clases pueden solicitar:

```java
Connection conexion = ConexionMySQL.obtenerConexion();
```

---

# 8. `restaurante/PedidoDAO.java`

El DAO se encarga de actualizar el estado en MySQL:

```java
package restaurante;

import MySQL.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PedidoDAO {

    public boolean actualizarEstado(
            int idPedido,
            EstadoPedido estado) {

        String sql =
                "UPDATE Pedido " +
                "SET estado = ? " +
                "WHERE id = ?";

        try (
            Connection conexion =
                    ConexionMySQL.obtenerConexion();

            PreparedStatement sentencia =
                    conexion.prepareStatement(sql)
        ) {

            sentencia.setString(1, estado.name());
            sentencia.setInt(2, idPedido);

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar el estado "
                    + "del pedido: "
                    + e.getMessage()
            );

            return false;
        }
    }
}
```

Por ejemplo:

```sql
UPDATE Pedido
SET estado = 'EN_PREPARACION'
WHERE id = 1;
```

o:

```sql
UPDATE Pedido
SET estado = 'ENTREGADO'
WHERE id = 1;
```

---

# 9. Relación con la base de datos

La tabla `Pedido` "Mysql" ya dispone del campo:

```sql
estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE'
```

Por ello no es necesario crear una tabla `Cocina`.

Los estados utilizados son:

```text
PENDIENTE
EN_PREPARACION
ENTREGADO
```

---

# 10. Separación de responsabilidades

```text
Pedido
  |
  |-- contiene la lógica del pedido
  |-- cambia el estado
  v
PedidoDAO
  |
  |-- actualiza la información en MySQL
  v
ConexionMySQL
  |
  |-- establece la conexión
  v
MySQL
```

| Clase | Responsabilidad |
|---|---|
| `EstadoPedido` | Define los estados válidos |
| `Pedido` | Controla el flujo y cambio de estado |
| `Mozo` | Solicita el envío y la entrega |
| `PedidoDAO` | Actualiza el estado en MySQL |
| `ConexionMySQL` | Proporciona la conexión |

---

# 11. Prueba del módulo

En `restaurante/GestionPedidosRestaurante.java`:

```java
package restaurante;

/**
 *
 * @author Luz
 */
public class GestionPedidosRestaurante {

    public static void main(String[] args) {
        //Este codigo solo es prueva de cocina y entrega "Lenin" lo pueden borrar
        Pedido pedido = new Pedido(1);
        Mozo mozo = new Mozo();
        mozo.asignarPedido(pedido);
        System.out.println("Estado inicial: " + pedido.getEstado());
        mozo.enviarACocina();
        System.out.println("Estado después del envío: " + pedido.getEstado());
        // La preparación de cocina no se implementa en el sistema.
        mozo.entregarPedido();
        System.out.println("Estado final: " + pedido.getEstado());
    }
}
```

Resultado esperado:

```text
Estado inicial: PENDIENTE
Pedido 1 enviado a cocina.
Estado después del envío: EN_PREPARACION
Pedido 1 entregado al cliente.
Estado final: ENTREGADO
```

---

# 12. Arquitectura del módulo cocina y entrega

```text
                    APLICACIÓN JAVA

                         |
                         v
                       Mozo
                         |
                         v
                       Pedido
                    /         \
                   v           v
       enviarACocina()     entregarPedido()
                  |              |
                  v              v
          EN_PREPARACION     ENTREGADO
                  \              /
                   \            /
                    v          v
                    PedidoDAO
                         |
                         v
                  ConexionMySQL
                         |
                         v
                      MySQL
                         |
                         v
                  Tabla Pedido
```

---

# 13. Resumen del aporte

El aporte consiste en implementar el flujo de **Envío a Cocina y Entrega del Pedido**, utilizando el estado del pedido como mecanismo de control.

El sistema no administra el proceso interno de cocina. Únicamente registra:

```text
PENDIENTE
     ↓
EN_PREPARACION
     ↓
ENTREGADO
```

Responsabilidades:

```text
Mozo
  ↓
solicita operación

Pedido
  ↓
controla cambio de estado

PedidoDAO
  ↓
persiste el estado

ConexionMySQL
  ↓
conecta con la BD

MySQL
  ↓
tabla Pedido
```

# 13. Pull Request

Crear un Pull Request:

```text
feature/lenin-cocina-entrega
                |
                v
             develop
```

Descripción sugerida:

```text
## Descripción

Se implementó el módulo de Cocina y Entrega del Pedido.

## Cambios realizados

- Se implementó EstadoPedido como enum.
- Se implementó el envío del pedido a cocina.
- Se implementó la entrega del pedido.
- Se agregó la asignación de pedidos al Mozo.
- Se preparó la conexión reutilizable con MySQL.
- Se agregó PedidoDAO para actualizar el estado del pedido.
- Cocina se mantiene como subproceso fuera del alcance de implementación.

## Flujo

PENDIENTE -> EN_PREPARACION -> ENTREGADO
```