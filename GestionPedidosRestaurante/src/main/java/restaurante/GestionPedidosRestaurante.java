/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package restaurante;

import modelo.Mozo;
import modelo.Pedido;

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
