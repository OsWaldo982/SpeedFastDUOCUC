package cl.speedfast.controlador;

import cl.speedfast.dao.PedidoDAO;
import cl.speedfast.dao.RepartidorDAO;
import cl.speedfast.interfaces.Rastreable;
import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.Repartidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ControladorDeEnvios implements Rastreable {

    private final List<Pedido> pedidos = new ArrayList<>();

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    public synchronized void registrarPedido(Pedido pedido) {

        if (pedido == null) {
            throw new IllegalArgumentException(
                    "El pedido no puede ser nulo."
            );
        }

        boolean guardado = pedidoDAO.guardar(pedido);

        if (!guardado) {
            throw new IllegalArgumentException(
                    "No fue posible guardar el pedido en la base de datos."
            );
        }

        pedidos.add(pedido);
    }

    public synchronized List<Pedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    public synchronized int cantidadPedidos() {
        return pedidos.size();
    }

    public boolean iniciarEntregas() {

        List<Pedido> pendientes;

        synchronized (this) {

            pendientes = pedidos.stream()
                    .filter(p ->
                            !p.isCancelado()
                                    && p.getEstado()
                                    == EstadoPedido.PENDIENTE
                    )
                    .toList();
        }

        if (pendientes.isEmpty()) {
            return false;
        }

        // Obtener repartidores registrados en MySQL
        List<Repartidor> repartidores =
                repartidorDAO.listarTodos();

        if (repartidores.isEmpty()) {

            System.out.println(
                    "No existen repartidores registrados en MySQL."
            );

            return false;
        }

        // Crear zona de carga
        ZonaDeCarga zonaDeCarga = new ZonaDeCarga();

        for (Pedido pedido : pendientes) {
            zonaDeCarga.agregarPedido(pedido);
        }

        // Máximo 3 hilos simultáneos
        int cantidadHilos =
                Math.min(3, repartidores.size());

        ExecutorService executor =
                Executors.newFixedThreadPool(cantidadHilos);

        // Crear un hilo por repartidor disponible
        for (Repartidor repartidorBD : repartidores) {

            Repartidor trabajador = new Repartidor(
                    repartidorBD.getId(),
                    repartidorBD.getNombre(),
                    zonaDeCarga
            );

            executor.execute(trabajador);
        }

        executor.shutdown();

        try {

            boolean finalizado =
                    executor.awaitTermination(
                            1,
                            TimeUnit.MINUTES
                    );

            if (!finalizado) {

                executor.shutdownNow();

                return false;
            }

            return pendientes.stream()
                    .allMatch(
                            p -> p.getEstado()
                                    == EstadoPedido.ENTREGADO
                    );

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
                    "- "
                            + pedido.getClass().getSimpleName()
                            + " #"
                            + String.format(
                            "%03d",
                            pedido.getIdPedido()
                    )
                            + " | "
                            + pedido.getEstado()
                            + " | repartidor: "
                            + pedido.getRepartidorAsignado()
            );
        }
    }
}