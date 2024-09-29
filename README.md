Integrantes:
  
  Valentina Gomez A00398790
  
  David Troya A00399865
  
  Daniel Gonzales A00399873
  
  Alexis Delgado A00399176

Instrucciones para ejecutar el codigo:

Inicializacion del Codigo y Primera Funcionalidad:

Primero se ejecuta la clase Server y posteriormente se ejecuta la clase Client, si deseas probar todas las funcionalidades desde tu propio computador, ejecuta varias veces la clase Client para tener varios usuarios en el sistema,
posteriormente la terminal te va a pedir que ingreses tu nombre y a continuacion te presenta el menu de funcionalidades, si existen varios usuarios en el sistema, puedes ingresar a la funcionalidad numero 1 del menu llamada 
"Enviar mensaje a un usuario", y te va a mostrar todos los usuarios del sistema, para que escribas el nombre del Usuario al que le quieres enviar un mensaje, despues de ingresar el nombre te muestra todos los mensajes anteriores que has
tenido con ese Usuario, si no tienes ninguno te dira "No hay mensajes anteriores con ..." y ya puedas escribir el mensaje que deseas enviar. En el otro lado, el receptor, si desea visualizar el mensaje, tiene que ingresar a la misma
funcionalidad (1) y seleccionar el nombre del Usuario que lo envio, y a continuacion te mostrara su mensaje. Se realizo asi para que tuviera una semejanza con un chat real y el mensaje no entrara directamente a la terminal, dañando asi
el menu de funcionalidades. Por ultimo, si se mantienen mandando mensajes, cada que ingresen al "chat" les mostrara el historial de mensajes anteriores con esa persona.

Segunda Funcionalidad:
La segunda Funcionalidad del menu se llama "Crear grupo" esta funcionalidad es muy sencilla, solo ingresas esa opcion del menu, el nombre que deseas ponerle al grupo y de inmediato el sistema te añade a ese grupo que creaste

Tercera Funcionalidad:
La tercera funcionalidad, "Unirme a un grupo" si la seleccionas desde el usuario que creo el grupo en la anterior funcionalidad, no te va a mostrar algun grupo disponible, puesto que en el unico grupo creado en el sistema, 
ya estas unido a el, pero si ejecutas esta misma funcionalidad desde el lado del otro cliente, ya te mostrara entre los grupos disponibles, el grupo que el otro usuario creo, te pedira que ingreses el nombre del grupo y te añadira a el

Cuarta Funcionalidad:
La cuarta funcionalidad "Ver mis grupos" es una funcionalidad muy sencilla, solo te mostrara los grupos en los que cada usuario esta ingresado

Quinta Funcionalidad:
Esta funcionalidad, "Enviar mensaje a un grupo" te mostrara todos los grupos a los que estas unido, te pedira que ingreses el nombre del grupo al que deseas enviarle el mensaje y posteriormente te pedira que escribes el mensaje que deseas 
enviar, y puedes verificar si ese mensaje llego, ingresando a la opcion del menu "Enviar mensaje a un grupo" de los otros clientes que estan unidos en el. Se te mostrara el mensaje que se envio junto con el historial de ese grupo (si existe)

Sexta Funcionalidad: Enviar audio a una persona
La sexta funcionalidad en el menú se llama "Enviar audio a una persona". Cuando seleccionas esta opción, el sistema te muestra la lista de los clientes conectados. Después, te solicita que escribas el nombre del usuario al que deseas enviarle un audio. Una vez ingresado el nombre, el sistema te pedirá que ingreses la ruta del archivo de audio que deseas enviar. Si la ruta es válida y el archivo existe, se inicia el proceso de envío del audio.

El sistema envía el nombre del archivo, su tamaño, y finalmente los datos del archivo de audio al cliente receptor. El destinatario recibirá el audio en su terminal y podrá reproducirlo desde la ruta donde se almacenó. Si ocurre algún error durante el envío (por ejemplo, si el archivo no se encuentra), se mostrará un mensaje de error en la terminal del remitente.

Séptima Funcionalidad: Enviar audio a un grupo
La séptima funcionalidad se llama "Enviar audio a un grupo". Cuando seleccionas esta opción, el sistema te muestra una lista de los grupos a los que perteneces. A continuación, te pide que ingreses el nombre del grupo al que deseas enviar el audio. Una vez seleccionado el grupo, te solicita la ruta del archivo de audio que deseas compartir.

Si el archivo existe, el sistema procede a enviar el audio a todos los miembros del grupo. Se utiliza un ExecutorService para gestionar el envío de manera concurrente, lo que significa que el audio se envía a varios miembros al mismo tiempo. Cada miembro del grupo recibe y guarda el audio en su directorio correspondiente. Una vez finalizado el proceso, se muestra un mensaje confirmando que el audio ha sido enviado correctamente al grupo. En caso de que el archivo no se encuentre o si no eres miembro del grupo, el sistema te mostrará un mensaje de error.

