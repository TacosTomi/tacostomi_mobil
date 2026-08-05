# Implementation Plan - Remove All Comments

Remove all single-line (`//`) and multi-line (`/* */`, `/** */`) comments from all Kotlin classes in the project to clean up the code.

## Proposed Changes

### [app component](file:///Users/dm/AndroidStudioProjects/TacosTommy/app)

#### [MODIFY] All Kotlin Files
I will iterate through all `.kt` files in `app/src/main/java/com/example/restaurante/` and remove all comments.

**Affected Files:**
- `ActividadDetallePedido.kt`
- `ActividadDetalleProducto.kt`
- `ActividadEstadoPedido.kt`
- `ActividadIniciarSesion.kt`
- `ActividadPrincipal.kt`
- `ActividadRegistro.kt`
- `FragmentoCarrito.kt`
- `FragmentoHistorial.kt`
- `FragmentoMenu.kt`
- `FragmentoPerfil.kt`
- `adapters/AdaptadorCarrito.kt`
- `adapters/AdaptadorCategoria.kt`
- `adapters/AdaptadorPedido.kt`
- `adapters/AdaptadorProducto.kt`
- `data/DatosPrueba.kt`
- `data/GestorCarrito.kt`
- `data/GestorSesion.kt`
- `model/Modelos.kt`

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure that removing comments didn't break any string literals or functional code.

### Manual Verification
- Inspect a few files to confirm that they are completely free of comments.
