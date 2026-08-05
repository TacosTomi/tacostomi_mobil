# Walkthrough - Remove All Comments

All comments have been removed from the Kotlin classes in the project to provide a cleaner codebase.

## Changes Made

### 1. Kotlin Files Cleaned
Removed all `//`, `/* */`, and `/** */` comments from the following files:
- All Activities (e.g., `ActividadPrincipal.kt`, `ActividadRegistro.kt`)
- All Fragments (e.g., `FragmentoMenu.kt`, `FragmentoPerfil.kt`)
- All Adapters (e.g., `AdaptadorCarrito.kt`, `AdaptadorProducto.kt`)
- All Data Managers (e.g., `GestorCarrito.kt`, `DatosPrueba.kt`)
- All Models (`Modelos.kt`)

### 2. Line Collapsing
Collapsed multiple consecutive empty lines that were left after comment removal to maintain a compact and readable code structure.

## Verification Results

- **Build**: Successfully ran `./gradlew :app:assembleDebug`. Functional code was preserved during the cleaning process.
