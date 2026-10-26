Integrantes: 
- Ariana Nicole Vargas Prado 
- Andres Ignacio Zubieta Sempertegui 
- Marvin Said Colque Aguilar
- Alison Alejandra Montaño Arias 
- Marcelo David Navia Rocabado 
- Adrian Rodriguez Montecinos
Combinación Elegida:
- E-25-2
- E-25-5
- E-50-5
# Instrucciones para la demostración de los ejercicios

## 1. E25-2 — Configuraciones locales con Room

**Objetivo:** demostrar que la aplicación permite registrar, visualizar y eliminar configuraciones, conservando los datos incluso después de cerrar la aplicación.

### Pasos para la demostración

1. Abrir la aplicación en el dispositivo o emulador Android.
2. Ingresar a la pantalla de configuraciones.
3. Verificar que se muestran tres configuraciones de ejemplo al iniciar por primera vez.
4. Agregar una nueva configuración con los siguientes datos:
    - Clave: `tema`
    - Valor: `oscuro`
5. Confirmar que la nueva configuración aparece automáticamente en la lista.
6. Eliminar una de las configuraciones y comprobar que desaparece de la pantalla.
7. Cerrar completamente la aplicación y volver a abrirla.
8. Verificar que los datos guardados continúan disponibles y que la configuración eliminada no reaparece.
9. Activar el modo avión y volver a consultar las configuraciones.

**Resultado esperado:** las configuraciones se administran desde la aplicación, la lista se actualiza automáticamente y los datos permanecen guardados localmente, incluso sin conexión a Internet.

---

## 2. E25-5 — Notificaciones internas

**Objetivo:** demostrar que la aplicación puede generar notificaciones personalizadas y abrirse al pulsarlas.

### Pasos para la demostración

1. Abrir la aplicación.
2. Si Android solicita permiso para enviar notificaciones, seleccionar **Permitir**.
3. Buscar el botón para generar una notificación.
4. Pulsar el botón.
5. Deslizar hacia abajo la barra superior del dispositivo para abrir la bandeja de notificaciones.
6. Verificar que aparece una notificación con el mensaje **“Hola desde la app”**, su título y su icono personalizados.
7. Enviar la aplicación al segundo plano sin cerrarla.
8. Volver a abrir la bandeja de notificaciones.
9. Pulsar la notificación.
10. Comprobar que se abre la aplicación.

**Resultado esperado:** la aplicación genera una notificación visible con el contenido personalizado y permite regresar a ella al pulsar la notificación.

---

## 3. E50-5 — Tareas en Firestore con notificaciones

**Objetivo:** demostrar que las tareas se actualizan en tiempo real y que la aplicación notifica cuando se agrega una tarea nueva.

### Pasos para la demostración

1. Abrir la aplicación y entrar a la pantalla de tareas.
2. Crear una tarea desde la aplicación, por ejemplo: `Estudiar Kotlin`.
3. Comprobar que la tarea aparece en la lista.
4. Abrir Firebase Console y entrar a Firestore Database, en la colección `tareas`.
5. Agregar un documento nuevo con el título `Preparar examen`, utilizando los campos definidos por la aplicación.
6. Regresar a la aplicación sin reiniciarla.
7. Comprobar que la nueva tarea aparece automáticamente en la lista.
8. Abrir la bandeja de notificaciones.
9. Verificar que aparece una notificación con un mensaje similar a **“Nueva tarea: Preparar examen”**.
10. Pulsar la notificación y comprobar que se abre la pantalla de tareas.

### Prueba adicional: silenciar notificaciones

1. Regresar a la pantalla de tareas.
2. Activar el botón **Silenciar notificaciones**.
3. Agregar una tarea nueva desde Firebase Console.
4. Comprobar que la tarea aparece en la aplicación, pero no se muestra una nueva notificación.
5. Cerrar y volver a abrir la aplicación.
6. Verificar que el silencio continúa activado.
7. Desactivar el silencio y agregar otra tarea para comprobar que las notificaciones vuelven a funcionar.

**Resultado esperado:** las tareas se actualizan en tiempo real, las nuevas incorporaciones generan notificaciones y el usuario puede silenciarlas sin dejar de recibir los cambios en la lista.

---
