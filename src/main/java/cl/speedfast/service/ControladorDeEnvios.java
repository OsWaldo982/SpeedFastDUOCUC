package cl.speedfast.service;

import cl.speedfast.interfaces.Rastreable;
import cl.speedfast.model.EstadoPedido;
import cl.speedfast.model.Pedido;
import cl.speedfast.model.Repartidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Controlador compartido por las ventanas de SpeedFast.
 * Mantiene los pedidos en memoria y coordina el inicio de las entregas.
 */
public class ControladorDeEnvios implements Rastreable {

    private final List<Pedido> pedidos = new ArrayList<>();

    public synchronized void registrarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }

        boolean idDuplicado = pedidos.stream()
                .anyMatch(p -> p.getIdPedido() == pedido.getIdPedido());

        if (idDuplicado) {
            throw new IllegalArgumentException(
                    "Ya existe un pedido con ID " + pedido.getIdPedido() + "."
            );
        }

        pedidos.add(pedido);
    }

    /**
     * Devuelve una copia para que las vistas puedan leer sin modificar la lista interna.
     */
    public synchronized List<Pedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    public synchronized int cantidadPedidos() {
        return pedidos.size();
    }

    /**
     * Inicia tres repartidores sobre una misma ZonaDeCarga.
     * Este metodo es bloqueante y debe llamarse desde un hilo de trabajo, no desde el EDT.
     */
    public boolean iniciarEntregas() {
        List<Pedido> pendientes;

        synchronized (this) {
            pendientes = pedidos.stream()
                    .filter(p -> !p.isCancelado() && p.getEstado() == EstadoPedido.PENDIENTE)
                    .toList();
        }

        if (pendientes.isEmpty()) {
            return false;
        }

        ZonaDeCarga zonaDeCarga = new ZonaDeCarga();
        for (Pedido pedido : pendientes) {
            zonaDeCarga.agregarPedido(pedido);
        }

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(new Repartidor("Gonzalo Perez", zonaDeCarga));
        executor.execute(new Repartidor("Valentina Silva", zonaDeCarga));
        executor.execute(new Repartidor("Camila Soto", zonaDeCarga));
        executor.shutdown();

        try {
            boolean finalizado = executor.awaitTermination(1, TimeUnit.MINUTES);
            if (!finalizado) {
                executor.shutdownNow();
                return false;
            }

            return pendientes.stream()
                    .allMatch(p -> p.getEstado() == EstadoPedido.ENTREGADO);

        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public synchronized void verHistorial() {
        System.out.println("Historial final:");
        for (Pedido pedido : pedidos) {
            System.out.println(
                    "- " + pedido.getClass().getSimpleName()
                            + " #" + String.format("%03d", pedido.getIdPedido())
                            + " | " + pedido.getEstado()
                            + " | repartidor: " + pedido.getRepartidorAsignado()
            );
        }
    }
}
