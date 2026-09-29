package cl.speedfast.modelo;

public class PedidoExpress extends Pedido {

    private static final int TIEMPO_BASE_MINUTOS = 10;
    private static final int RECARGO_DISTANCIA_MINUTOS = 5;
    private static final int LIMITE_DISTANCIA_KM = 5;

    public PedidoExpress(int idPedido, String direccionEntrega) {
        this(idPedido, direccionEntrega, 1);
    }

    public PedidoExpress(int idPedido, String direccionEntrega, int distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public int calcularTiempoEntrega() {
        if (getDistanciaKm() > LIMITE_DISTANCIA_KM) {
            return TIEMPO_BASE_MINUTOS + RECARGO_DISTANCIA_MINUTOS;
        }
        return TIEMPO_BASE_MINUTOS;
    }


    @Override
    public void asignarRepartidor() {
        this.repartidorAsignado = "Carlos Pérez (Repartidor Express)";
    }
}