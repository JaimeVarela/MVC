# Modelo Vista Controlador

Este programa consiste en una página web que obtiene datos de una base de datos, ofreciendo datos según las entradas del usuario. En este caso se trata de un conjunto de archivos XML que contienen información sobre películas. El usuario navegará a través de diferentes páginas, y según la opción escogida el servidor le ofrecerá una página con el contenido acorde con la entrada seleccionada.

El programa tiene dos formas:

p2, que consiste en el sistema cliente/servidor, donde un único archivo java se encarga tanto de la parte del servlet y leer los datos de los ficheros como de crear las páginas que leerá el cliente en el navegador.

p3, que consiste en seguir el paradigma Modelo Vista Controlador: un archivo java hace de servlet atendiendo las solicitudes, un conjunto de java beans que leen y almacenan los datos de la base de datos, y un conjunto de jsp que muestran las páginas con la información acorde. Además puede elegirse ejecutar este sistema empleando AJAX, de forma que el navegador actualiza la información ofrecida al usuario de forma asíncrona, recibiendo los datos en forma de JSON.

En ambas formas puede accederse a la lista de errores que el archivo XML Schema haya encontrado o que directamente no esté bien formado el archivo XML.

![fase01](https://user-images.githubusercontent.com/44776831/48162683-06d46500-e2de-11e8-8e7b-ee87ae82b74e.png)
![fase02](https://user-images.githubusercontent.com/44776831/48162697-105dcd00-e2de-11e8-8eec-9f26f87761f5.png)
![fase11](https://user-images.githubusercontent.com/44776831/48162713-19e73500-e2de-11e8-8e34-e857cc940ed4.png)
![fase12](https://user-images.githubusercontent.com/44776831/48162723-1fdd1600-e2de-11e8-90e8-08a136be0651.png)
![fase13](https://user-images.githubusercontent.com/44776831/48162731-279cba80-e2de-11e8-879c-360c83eb05ff.png)
![fase14](https://user-images.githubusercontent.com/44776831/48162739-2d929b80-e2de-11e8-91b9-a71373487e84.png)
![fase21](https://user-images.githubusercontent.com/44776831/48162750-34211300-e2de-11e8-9f6f-463bc628dc8a.png)
![fase22](https://user-images.githubusercontent.com/44776831/48162764-3c794e00-e2de-11e8-97d8-d9a96fea54cf.png)
![fase23](https://user-images.githubusercontent.com/44776831/48162785-4307c580-e2de-11e8-9d42-d0e4cb462507.png)
![fase24](https://user-images.githubusercontent.com/44776831/48162803-4ac76a00-e2de-11e8-924c-5a2ce3341a68.png)
