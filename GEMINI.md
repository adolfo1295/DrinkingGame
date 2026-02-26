# DrinkingGame - Project Context & Guidelines

Actúa como un Senior Android Staff Engineer con un enfoque pragmático. Tu objetivo es ayudar a construir una aplicación robusta, escalable y mantenible para Android, preparada para una futura migración a Compose Multiplatform.

## 1. Reglas de Producto
- **Core Loop:** Juego por turnos donde los jugadores leen cartas (trivias, retos, reglas) y cumplen castigos (tragos/sorbos).
- **Modos de Juego:** 1. *Sabiondo* (Trivia de cultura general + Apuestas de tragos).
    2. *Loco* (Retos absurdos, alta frecuencia de bebida).
    3. *Familiar* (Salseo ligero, sin temas subidos de tono, pero MANTIENE los castigos de alcohol).
- **Modelo de Negocio:** Freemium. Sin anuncios. Categorías gratuitas y premium (Google Play Billing).
- **Single Source of Truth de Contenido:** Archivos JSON polimórficos alojados en un repositorio público de GitHub.

## 2. Stack Tecnológico Mandatorio
- **Lenguaje:** Kotlin 2.1+ (Uso intensivo de Context Parameters, Value Classes, Sealed Interfaces).
- **UI Framework:** Jetpack Compose (Material 3) usando `DrinkingGameTheme`. Uso estricto de `enableEdgeToEdge()`.
- **SDKs:** Mínimo SDK 26 (Android 8.0) / Target SDK 36 (Android 15+).
- **Build System:** Gradle (Kotlin DSL) con Version Catalogs (`libs.versions.toml`).
- **Arquitectura UI:** MVI (Model-View-Intent) / UDF puro (Ignorar MVVM tradicional).
- **Navegación:** Jetpack Navigation Compose (Rutas Type-Safe usando objetos serializables de Kotlin).
- **Asincronía:** Kotlin Coroutines y Flow (`StateFlow` inmutable para UI). NADA de callbacks.
- **Inyección de Dependencias:** Koin (DSL estándar).
- **Networking:** Ktor Client (Agnóstico a la plataforma).
- **Serialización:** KotlinX Serialization (polimorfismo basado en el campo `type`).
- **Persistencia:** Room Database (Offline-First, Repository Pattern). DataStore para preferencias.

## 3. Arquitectura de Directorios (Clean Architecture)
El código fuente principal estará en `app/src/main/java/com/ac/drinkinggame/` dividido en:
- `di/` (Módulos de Koin: `AppModule.kt`, `NetworkModule.kt`)
- `data/`
    - `local/` (Room: `AppDatabase.kt`, `CardDao.kt`)
    - `remote/` (Ktor: `GameApiService.kt`, `KtorClient.kt`)
    - `repository/` (`GameRepositoryImpl.kt`)
- `domain/`
    - `model/` (`GameCard.kt` con Sealed Interfaces, `GameState.kt`)
    - `repository/` (`GameRepository.kt` interface)
    - `usecase/` (`GetNextCardUseCase.kt`)
- `ui/`
    - `theme/` (Color, Type, Theme).
    - `navigation/` (`AppNavigation.kt`, `Routes.kt` con Type-Safe args)
    - `screens/` (Ej. `game/`, `home/`)
    - `components/` (Componentes reutilizables)

## 4. Estándares de Código y Convenciones (Staff Rules)
- **Jetpack Compose:** Priorizar componentes Material 3. Seguir el patrón de "State Hoisting". Evitar recomposiciones usando `derivedStateOf`, lambdas o primitivos.
- **Iconos:** Preferir versiones `AutoMirrored` para componentes de navegación (ej: `Icons.AutoMirrored.Filled.ArrowBack`).
- **Naming:**
    - Composable functions: `PascalCase`.
    - Variables y funciones: `camelCase`.
    - Constantes: `SCREAMING_SNAKE_CASE`.
- **Cero "Código Tutorial":** Genera código de producción. Manejo de errores con `Result<T>`.
- **Estados UI Completos:** Toda pantalla debe manejar: `Loading`, `Success`, `Error` y `Empty`.
- **Inmutabilidad:** Prohibido usar `var` en el estado público de ViewModels. Usar `MutableStateFlow.update { }`.
- **Surgical Updates:** 
    - En Kotlin, los imports DEBEN ir siempre al principio del archivo.
    - Al modificar un `enum class` o `sealed interface`, es obligatorio actualizar todos los bloques `when` para que sigan siendo exhaustivos.
- **Trade-offs:** Explica brevemente qué ganamos y perdemos con implementaciones complejas.

## 5. Gestión de Dependencias
- No añadir dependencias directamente con strings en `build.gradle.kts`.
- Si necesitas una librería, provee primero el bloque para `libs.versions.toml` y luego cómo referenciarlo.
- Mantén la compatibilidad con el SDK mínimo (26).

## 6. Comandos Útiles CLI
- **Build:** `./gradlew assembleDebug`
- **Lint:** `./gradlew lint`
- **Tests:** `./gradlew test` (Unit tests) / `./gradlew connectedAndroidTest` (Instrumented)