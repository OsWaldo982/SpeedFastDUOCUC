package cl.speedfast.controlador;

import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurso compartido por todos los repartidores.
 * Los metodos synchronized garantizan que un pedido solo pueda ser retirado
 * una vez, evitando condiciones de carrera y entregas duplicadas.
 */
public class ZonaDeCarga {

    private final List<Pedido> pedidosPendientes;

    public ZonaDeCarga() {
        this.pedidosPendientes = new ArrayList<>();
        System.out.println("[Zona de carga inicializada]");
    }

    public synchronized void agregarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("No se puede agregar un pedido nulo.");
        }

        if (pedido.isCancelado()) {
            throw new IllegalArgumentException(
                    "No se puede agregar el pedido #" + pedido.getIdPedido() + " porque esta cancelado."
            );
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden agregar pedidos con estado PENDIENTE."
            );
        }

        boolean idDuplicado = pedidosPendientes.stream()
                .anyMatch(p -> p.getIdPedido() == pedido.getIdPedido());

        if (idDuplicado) {
            throw new IllegalArgumentException(
                    "Ya existe un pedido con id #" + pedido.getIdPedido() + " en la zona de carga."
            );
        }

        pedidosPendientes.add(pedido);

        System.out.println(
                "Pedido #" + pedido.getIdPedido()
                        + " agregado. Destino: " + pedido.getDireccionEntrega()
        );
    }

    /**
     * Retira de forma atomica un unico pedido. El cambio a EN_REPARTO se
     * realiza dentro del mismo bloque sincronizado para que ningun otro hilo
     * pueda obtener ese mismo pedido.
     */
    public synchronized Pedido retirarPedido() {
        while (!pedidosPendientes.isEmpty()) {
            Pedido pedido = pedidosPendientes.remove(0);

            if (pedido != null
                    && !pedido.isCancelado()
                    && pedido.getEstado() == EstadoPedido.PENDIENTE) {

                pedido.setEstado(EstadoPedido.EN_REPARTO);
                return pedido;
            }
        }

        return null;
    }

    public synchronized boolean estaVacia() {
        return pedidosPendientes.isEmpty();
    }

    public synchronized int cantidadPedidosPendientes() {
        return pedidosPendientes.size();
    }
}
