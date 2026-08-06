# Plan de Migración de Fragmentos a Actividades

Este plan detalla los pasos para convertir la arquitectura actual basada en fragmentos a una arquitectura basada totalmente en Actividades, manteniendo la barra de navegación inferior funcional en cada una de ellas.

## Cambios Propuestos

### Layouts

Para que la barra de navegación aparezca en todas las pantallas, modificaremos los archivos XML de los fragmentos para envolverlos en un `LinearLayout` que incluya el `BottomNavigationView`.

#### [MODIFY] [fragmento_menu.xml](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/res/layout/fragmento_menu.xml)
#### [MODIFY] [fragmento_carrito.xml](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/res/layout/fragmento_carrito.xml)
#### [MODIFY] [fragmento_historial.xml](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/res/layout/fragmento_historial.xml)
#### [MODIFY] [fragmento_perfil.xml](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/res/layout/fragmento_perfil.xml)

### Lógica de Código

Crearemos nuevas clases de Actividad y migraremos la lógica de los fragmentos.

#### [NEW] [ActividadMenu.kt](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/java/com/example/restaurante/ActividadMenu.kt)
#### [NEW] [ActividadCarrito.kt](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/java/com/example/restaurante/ActividadCarrito.kt)
#### [NEW] [ActividadHistorial.kt](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/java/com/example/restaurante/ActividadHistorial.kt)
#### [NEW] [ActividadPerfil.kt](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/java/com/example/restaurante/ActividadPerfil.kt)

### Navegación y Configuración

#### [MODIFY] [AndroidManifest.xml](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/AndroidManifest.xml)
Registrar las nuevas actividades.

#### [MODIFY] [ActividadIniciarSesion.kt](file:///Users/dm/AndroidStudioProjects/TacosTommy/app/src/main/java/com/example/restaurante/ActividadIniciarSesion.kt)
Cambiar el destino después del login de `ActividadPrincipal` a `ActividadMenu`.

#### [DELETE] [Fragmentos y ActividadPrincipal]
Una vez verificado, eliminaremos los archivos antiguos:
- `FragmentoMenu.kt`, `FragmentoCarrito.kt`, `FragmentoHistorial.kt`, `FragmentoPerfil.kt`
- `ActividadPrincipal.kt` y `actividad_principal.xml`

## Plan de Verificación

### Pruebas Manuales
1. Iniciar sesión y verificar que redirija al Menú.
2. Probar la navegación entre las 4 pestañas inferiores.
3. Verificar que las funciones de cada pantalla (buscar productos, agregar al carrito, confirmar pedido, editar perfil) sigan funcionando correctamente.
