# SpeedFast - Semana 7

Continuación del proyecto SpeedFast para la actividad de Semana 7 de Desarrollo Orientado a Objetos II.

## Objetivo

Integrar el sistema de gestión de pedidos y entregas desarrollado en Java con una base de datos MySQL utilizando JDBC y el patrón DAO.

La aplicación permite registrar pedidos y repartidores, almacenar la información en MySQL, asignar repartidores, procesar entregas y actualizar el estado de los pedidos.

## Tecnologías utilizadas

- Java 17
- Java Swing
- JDBC
- MySQL
- Maven
- IntelliJ IDEA

## Estructura principal

- `cl.speedfast.modelo`: modelos de pedidos, estados, repartidores y entregas.
- `cl.speedfast.dao`: acceso a datos mediante JDBC.
- `cl.speedfast.conexion`: conexión con MySQL.
- `cl.speedfast.controlador`: lógica de gestión de pedidos y entregas.
- `cl.speedfast.vista`: interfaz gráfica desarrollada con Java Swing.
- `cl.speedfast.interfaces`: interfaces utilizadas por el sistema.
- `cl.speedfast.main`: punto de entrada de la aplicación.

## Funcionalidades

- Registrar pedidos.
- Listar pedidos almacenados en MySQL.
- Registrar repartidores.
- Asignar repartidores a pedidos.
- Iniciar el proceso de entrega.
- Registrar entregas en MySQL.
- Actualizar el estado de los pedidos de `PENDIENTE` a `ENTREGADO`.
- Consultar la información almacenada en la base de datos.

## Base de datos

El proyecto utiliza la base de datos MySQL:

`speedfast_db`

Tablas principales:

- `pedido`
- `repartidor`
- `entrega`

Las tablas se relacionan mediante claves primarias y claves foráneas.

## Persistencia de datos

La aplicación utiliza JDBC y clases DAO para realizar operaciones sobre MySQL:

- `PedidoDAO`
- `RepartidorDAO`
- `EntregaDAO`

Los pedidos y repartidores registrados desde la interfaz gráfica quedan almacenados en la base de datos.

Cuando se completa una entrega, se registra en la tabla `entrega` y el pedido correspondiente cambia su estado a `ENTREGADO`.

## Ejecución

Ejecutar:

`cl.speedfast.main.Main`

desde IntelliJ IDEA.

La aplicación requiere que MySQL esté iniciado y que la base de datos `speedfast_db` esté disponible.

## Autor

Osvaldo Gonzalez
