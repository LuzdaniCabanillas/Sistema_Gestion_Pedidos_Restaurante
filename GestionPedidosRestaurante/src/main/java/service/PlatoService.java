package service;

import controlador.PlatoDAO;
import java.util.List;
import modelo.Plato;

public class PlatoService {

    private PlatoDAO platoDAO;

    public PlatoService() {
        platoDAO = new PlatoDAO();
    }

    //Registrar
    public boolean registrarPlato(
            String nombre,
            String descripcion,
            double precio,
            String categoria,
            String estado,
            int stock,
            String imagen) {

        if (nombre == null || nombre.trim().isEmpty()) {

            System.out.println(
                    "El nombre del plato es obligatorio."
            );

            return false;
        }

        if (precio < 0) {

            System.out.println(
                    "El precio no puede ser negativo."
            );

            return false;
        }

        if (stock < 0) {

            System.out.println(
                    "El stock no puede ser negativo."
            );

            return false;
        }

        if (!estadoValido(estado)) {

            System.out.println(
                    "El estado del plato no es válido."
            );

            return false;
        }

        List<Plato> platos
                = platoDAO.buscarPorNombre(nombre);

        for (Plato plato : platos) {

            if (plato.getNombre()
                    .equalsIgnoreCase(nombre.trim())) {

                System.out.println(
                        "Ya existe un plato con ese nombre."
                );

                return false;
            }
        }

        Plato plato = new Plato();

        plato.setNombre(nombre.trim());
        plato.setDescripcion(descripcion);
        plato.setPrecio(precio);
        plato.setCategoria(categoria);
        plato.setEstado(estado);
        plato.setStock(stock);
        plato.setImagen(imagen);

        return platoDAO.insertar(plato);
    }

    //Listar
    public List<Plato> listarPlatos() {

        return platoDAO.listar();
    }

    //Buscar por Id
    public Plato buscarPlatoPorId(int id) {

        if (id <= 0) {

            System.out.println(
                    "El ID del plato no es válido."
            );

            return null;
        }

        return platoDAO.buscarPorId(id);
    }

    //Buscar por Nombre
    public List<Plato> buscarPlatoPorNombre(
            String nombre) {

        if (nombre == null
                || nombre.trim().isEmpty()) {

            return platoDAO.listar();
        }

        return platoDAO.buscarPorNombre(
                nombre.trim()
        );
    }

    //Actualizar
    public boolean actualizarPlato(
            int id,
            String nombre,
            String descripcion,
            double precio,
            String categoria,
            String estado,
            int stock,
            String imagen) {

        if (id <= 0) {

            System.out.println(
                    "El ID del plato no es válido."
            );

            return false;
        }

        if (nombre == null
                || nombre.trim().isEmpty()) {

            System.out.println(
                    "El nombre del plato es obligatorio."
            );

            return false;
        }

        if (precio < 0) {

            System.out.println(
                    "El precio no puede ser negativo."
            );

            return false;
        }

        if (stock < 0) {

            System.out.println(
                    "El stock no puede ser negativo."
            );

            return false;
        }

        if (!estadoValido(estado)) {

            System.out.println(
                    "El estado del plato no es válido."
            );

            return false;
        }

        Plato platoExistente
                = platoDAO.buscarPorId(id);

        if (platoExistente == null) {

            System.out.println(
                    "El plato no existe."
            );

            return false;
        }

        List<Plato> platos
                = platoDAO.buscarPorNombre(nombre);

        for (Plato otroPlato : platos) {

            if (otroPlato.getId() != id
                    && otroPlato.getNombre()
                            .equalsIgnoreCase(nombre.trim())) {

                System.out.println(
                        "Ya existe otro plato con ese nombre."
                );

                return false;
            }
        }

        platoExistente.setNombre(
                nombre.trim()
        );

        platoExistente.setDescripcion(
                descripcion
        );

        platoExistente.setPrecio(
                precio
        );

        platoExistente.setCategoria(
                categoria
        );

        platoExistente.setEstado(
                estado
        );

        platoExistente.setStock(
                stock
        );

        platoExistente.setImagen(
                imagen
        );

        return platoDAO.actualizar(
                platoExistente
        );
    }

    //Eliminar
    public boolean eliminarPlato(int id) {

        if (id <= 0) {

            System.out.println(
                    "El ID del plato no es válido."
            );

            return false;
        }

        Plato plato
                = platoDAO.buscarPorId(id);

        if (plato == null) {

            System.out.println(
                    "El plato no existe."
            );

            return false;
        }

        return platoDAO.eliminar(id);
    }

    // VALIDAR ESTADO
    private boolean estadoValido(String estado) {

        if (estado == null) {
            return false;
        }

        return estado.equals("DISPONIBLE")
                || estado.equals("NO_DISPONIBLE");
    }
}
