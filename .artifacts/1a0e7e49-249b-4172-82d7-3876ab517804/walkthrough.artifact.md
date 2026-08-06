# Resumen de Migración a Actividades

He completado la migración de la arquitectura basada en fragmentos a una arquitectura basada en **Actividades individuales**.

## Cambios Realizados

1.  **Conversión de Pantallas:**
    *   `FragmentoMenu` -> `ActividadMenu`
    *   `FragmentoCarrito` -> `ActividadCarrito`
    *   `FragmentoHistorial` -> `ActividadHistorial`
    *   `FragmentoPerfil` -> `ActividadPerfil`
2.  **Navegación:**
    *   Cada actividad ahora tiene su propio `BottomNavigationView` en su layout (`actividad_menu.xml`, etc.).
    *   La navegación entre pestañas se maneja mediante `startActivity` con transiciones instantáneas para mantener la fluidez.
3.  **Configuración:**
    *   Se registraron todas las nuevas actividades en `AndroidManifest.xml`.
    *   Se actualizaron `ActividadIniciarSesion` y `ActividadRegistro` para redirigir correctamente.
4.  **Limpieza:**
    *   Se eliminaron los archivos de fragmentos obsoletos y la `ActividadPrincipal`.

## Verificación

*   Se realizó una compilación exitosa del proyecto con el comando `:app:assembleDebug`.
*   Se verificó que las referencias a `ActividadPrincipal` fueran reemplazadas en todo el código.
