# SpeedFast - Semana 6

Continuacion del proyecto SpeedFast para la actividad de Semana 6 de Desarrollo Orientado a Objetos II.

## Objetivo
Agregar una interfaz grafica de escritorio con Java Swing al sistema de pedidos y entregas desarrollado en semanas anteriores.

## Estructura principal
- `cl.speedfast.model`: modelos de pedidos, estados y repartidores.
- `cl.speedfast.service`: controlador compartido y zona de carga sincronizada.
- `cl.speedfast.vista`: ventanas Swing.
- `cl.speedfast.main`: punto de entrada de la aplicacion.

## Ventanas
- `VentanaPrincipal`: navegacion principal.
- `VentanaRegistroPedido`: formulario con ID, direccion y tipo.
- `VentanaListaPedidos`: JTable con `DefaultTableModel` y boton Refrescar.

## Ejecucion
Ejecutar `cl.speedfast.main.Main` desde IntelliJ IDEA.

La Semana 6 mantiene los datos en memoria. No requiere conexion a base de datos.
