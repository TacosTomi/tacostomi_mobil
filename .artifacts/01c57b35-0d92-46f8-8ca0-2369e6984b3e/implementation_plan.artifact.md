# Implementation Plan - Personalize Comments

Personalize, simplify, and end all comments with "chiavo" to make them sound more natural and consistent with the new "Tacos Tommy" branding style.

## Proposed Changes

### [app component](file:///Users/dm/AndroidStudioProjects/TacosTommy/app)

#### [MODIFY] Multiple Kotlin Files
Update comments in all identified `.kt` files to match the requested style.

**Examples of transformations:**
- `// Módulo 9: Carrito y pedido` -> `// Aquí manejamos el carrito y los pedidos, chiavo`
- `// El número de mesa se pide al confirmar...` -> `// Pedimos la mesa para saber a dónde llevar los tacos, chiavo`
- `// Validación en tiempo real...` -> `// Checamos que todo esté bien mientras escribes, chiavo`

#### [MODIFY] Multiple XML Files
Update comments in `colors.xml`, `themes.xml`, and any other relevant resource files.

**Examples of transformations:**
- `<!-- Paleta navy / periwinkle -->` -> `<!-- Los colores de los tacos, chiavo -->`

## Verification Plan

### Manual Verification
- Review the modified files to ensure all comments follow the new style and end with "chiavo".
- Verify that the app still builds and runs (comment changes should not affect functionality).
