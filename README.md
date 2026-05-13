# ProyectoMovilidad - App de Atención Temprana (AT)

Este es un proyecto **Compose Multiplatform** para la gestión de vídeos y escalas de valoración (ALP/GAS) en procesos de atención temprana.

## 🚀 Estado del Proyecto (Punto de Control)

### ✅ Implementado
- **Configuración Firebase (Android)**: Plugin de Google Services y dependencias de Storage/Database configurados.
- **Java 17**: Proyecto actualizado a JVM target 17 para compatibilidad con librerías de Firebase.
- **Persistencia con Realtime Database**: Gestiona el historial en `usuarios/{userId}/videos_entrenamiento/` y el perfil en `usuarios/{userId}/perfil/`.
- **UI de Vídeos**: Pantalla funcional con carga de historial, ordenación cronológica (más reciente primero) y formato de fecha/hora en español.
- **Subida Robusta**: Implementación de Timeouts (60s Storage / 15s Database) y Logs de diagnóstico para evitar bloqueos.
- **Reproductor de Vídeo (Android)**: Integración de Media3 ExoPlayer para reproducir vídeos directamente desde el historial.
- **Borrado Completo**: Interacción *Swipe-to-Dismiss* para eliminar vídeos tanto de Storage como de la Database con diálogo de confirmación.
- **Navegación Intuitiva**: Indicador dinámico de "Ver más vídeos" y contador total en el historial para mejorar la usabilidad.
- **Diseño Profesional**: Identidad visual "Desarrollo" (multicolor) con Material 3 y saludos personalizados.

### 🛠️ Pendiente (Próximos Pasos)
1. **Notificaciones de Seguimiento**: Implementar avisos si pasan 15 días sin que el usuario suba un nuevo vídeo.
2. **Configuración iOS**: Pendiente rematar en Xcode y adaptar el VideoPlayer para iOS (AVPlayer).
3. **Caché y Optimización**: Implementar caché de vídeo y compresión de archivos antes de la subida.
4. **Módulos ALP/GAS**: Desarrollar la lógica de negocio para las escalas de valoración y objetivos.

### ⚠️ Notas de Configuración
- El archivo `composeApp/google-services.json` es **obligatorio** para que la app Android arranque.
- Se utiliza el SDK `dev.gitlive:firebase-kotlin-sdk` para compatibilidad multiplatform.

---

## Estructura del Proyecto

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…