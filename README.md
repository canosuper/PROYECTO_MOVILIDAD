# ProyectoMovilidad - App de Atención Temprana (AT)

Este es un proyecto **Compose Multiplatform** para la gestión de vídeos y escalas de valoración (ALP/GAS) en procesos de atención temprana.

## 🚀 Estado del Proyecto (Punto de Control)

### ✅ Implementado
- **Sistema de Login Híbrido**: Autenticación dual que detecta automáticamente si el usuario es un paciente (PIN 4-6 dígitos) o un profesional (Usuario/Contraseña).
- **Integración con Ecosistema Web**: Conexión activa con los nodos oficiales de `pacientes`, `usuarios` (fisios) y `sedes` para interoperabilidad total.
- **Sincronización de Perfiles**: Al subir el primer vídeo, la app vincula automáticamente los datos globales del paciente con su historial privado en `usuarios_movil`.
- **Persistencia Robusta**: Historial gestionado en `usuarios_movil/{userId}/videos_entrenamiento/` con soporte para formatos de datos mixtos (Map/List).
- **UI de Vídeos**: Pantalla funcional con carga de historial, ordenación cronológica y formato de fecha/hora en español.
- **Subida Robusta y Optimizada**:
    - **Compresión Nativa (720p)**: Integración de `Media3 Transformer` para comprimir vídeos antes de la subida.
    - **Progreso en Tiempo Real**: Barra de progreso visual (`LinearProgressIndicator`) vinculada al flujo de bytes de Firebase Storage.
    - **Control de Timeouts**: Gestión estricta de 60 segundos por subida para optimizar la experiencia y detectar problemas de red rápidamente.
    - **Feedback de Errores**: Sistema de alertas (`AlertDialog`) para notificar al usuario sobre timeouts o fallos de conexión.
    - **Calidad Adaptativa**: Configuración de captura optimizada para balancear peso y visibilidad clínica.
- **Reproductor de Vídeo (Android)**: Integración de Media3 ExoPlayer para reproducir vídeos directamente desde el historial.
- **Borrado Completo**: Interacción *Swipe-to-Dismiss* para eliminar vídeos tanto de Storage como de la Database con diálogo de confirmación.
- **Navegación Intuitiva**: Indicador dinámico de "Ver más vídeos" y contador total en el historial para mejorar la usabilidad.
- **Diseño Profesional**: Identidad visual Premium basada en tonos azules y estilo moderno con Material 3 y saludos personalizados.
- **Icono Adaptativo Custom**: Iconografía personalizada que integra los conceptos de "atención" y "reproducción de vídeo", optimizada para un aspecto profesional en el launcher.
- **Documentación General**: Acceso centralizado a guías, manuales y documentos de compromiso gestionados dinámicamente desde el nodo `documentos_generales`.
- **Splash Screen Profesional**: Implementación de la API de Splash Screen de Android con icono personalizado y transición fluida.
- **Notificaciones Push e Inactividad**: Sistema completo de re-engagement mediante Firebase Cloud Messaging.
    - **Captura de Tokens**: Sincronización automática del Token FCM y la `lastActivity` en el login y subida de vídeos.
    - **Cloud Functions**: Script en Node.js (2ª Gen) desplegado para monitorizar la inactividad de los usuarios.
    - **Notificaciones Inteligentes**: Envío automático de mensajes personalizados ("¿subes un vídeo hoy?") tras 14 días de inactividad.
    - **Automatización**: Programación mediante Cloud Scheduler para ejecución diaria a las 9:00 AM.
- **Cierre de Sesión Seguro**: Botón de salida con limpieza total de estados y caché del usuario para evitar autologin indeseado.

### 🛠️ Pendiente (Próximos Pasos)
1. **Módulos ALP/GAS**: Desarrollar la lógica de negocio para las escalas de valoración y objetivos (Rollback realizado para limpieza de código).
2. **Configuración iOS**: Pendiente rematar en Xcode y adaptar el VideoPlayer para iOS (AVPlayer).
3. **Refactorización de Escalas**: Re-implementar ALP y GAS con la arquitectura limpia validada.

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
