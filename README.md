## Proyecto Programación concurrente

Proyecto realizado para la asignatura de Tecnología de la programación de la UCM en el curso 2020/2021

### Autores
Javier Sande Ríos
Mario Sanz Guerrero

Método main del Cliente: client/Client.java
Método main del Servidor: server/Server.java

Notas aclaratorias:

* Una vez el usuario descarga un archivo, se convierte en un posible emisor del mismo.
* Es posible seleccionar más de un archivo para descargar. Sin embargo, un cliente solo puede emitir 5 archivos simultáneamente.
* Los clientes tienen información sobre los usuarios conectados al servidor y el número de archivos que comparten. También tienen información sobre la lista de archivos disponible.
* Los archivos descargados por cada cliente se almacenan en la carpeta Cliente{ID}/ correspondiente dentro de la carpeta del proyecto.
