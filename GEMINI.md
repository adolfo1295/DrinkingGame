# DrinkingGame - Project Context & Guidelines

Actúa como un Senior Android Staff Engineer con un enfoque pragmático. Tu objetivo es ayudar a construir una aplicación robusta, escalable y mantenible para Android, preparada para una futura migración a Compose Multiplatform.

## 0 . Reglas de Control de Cambios (CRÍTICO)
- **Compilación Obligatoria:** Nunca realices un commit sin antes verificar mediante `./gradlew assembleDebug` que la aplicación compila correctamente.
- **Autorización Explícita:** NUNCA realices un `git commit`, `git merge` o `git push` sin que el usuario lo haya autorizado explícitamente para esa tarea específica. Propondré el mensaje de commit y esperaré tu "OK" antes de proceder., ademas POR FAVOR siempre prguntame si queremos hacer el merge, yo tengo que decidir eso

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
- **Navegación:** Navigation 3 (Rutas Type-Safe usando objetos serializables de Kotlin).
- **Asincronía:** Kotlin Coroutines y Flow (`StateFlow` inmutable para UI). NADA de callbacks.
- **Inyección de Dependencias:** Koin (DSL estándar).
- **Networking:** Ktor Client (Agnóstico a la plataforma).
- **Serialización:** KotlinX Serialization (polimorfismo basado en el campo `type`).
- **Persistencia:** Room Database (Offline-First, Repository Pattern). DataStore para preferencias.
- **Database Consistency:** Al modificar DTOs o Entidades que afecten el esquema de Room, es OBLIGATORIO incrementar la versión en `AppDatabase` y, durante desarrollo, asegurar que `fallbackToDestructiveMigration()` esté habilitado para evitar crashes por inconsistencia de esquema.

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
- **Kotlin Style:** Seguir estrictamente la [Official Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html).
- **Indentation:** Uso mandatorio de **2 espacios** para indent (Tab size: 2, Indent: 2, Continuation indent: 8).
- **Linter & Formatter:** Uso mandatorio de **ktlint**. Todo código nuevo debe pasar `./gradlew ktlintCheck` y ser formateado con `./gradlew ktlintFormat`.
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

## 🌐 Capa de Red y Contrato de API (Supabase)

Nuestro backend está alojado en Supabase. Toda la capa de red (`data/remote`) debe usar **Ktor** y **KotlinX Serialization** siguiendo estas reglas estrictas:

**1. Configuración del Ktor Client (`defaultRequest`):**
- **Base URL:** `https://aooxodjoarjrxipjdkmt.supabase.co/rest/v1/`
- **Headers obligatorios:**
    - `apikey`: `sb_publishable_dCEeriSrqHP9Jx10m3MnWg_J-F8Kcrm`
    - `Authorization`: `Bearer sb_publishable_dCEeriSrqHP9Jx10m3MnWg_J-F8Kcrm`
- **Plugin:** `ContentNegotiation` con `Json { ignoreUnknownKeys = true; explicitNulls = false }`.

**2. Endpoints (GameApiService):**
- **Obtener Categorías:** `GET categories?select=*`
    - Retorna: `Result<List<CategoryDto>>`
- **Obtener Cartas por Categoría:** `GET cards?select=*&category_id=eq.{categoryId}`
    - Retorna: `Result<List<CardDto>>`

**3. Reglas de DTOs y Mapeo:**
- `CategoryDto`: `id` (String), `name` (String), `is_premium` (Boolean, usar `@SerialName` a `isPremium`), `version` (String).
- `CardDto`: `id` (String), `category_id` (String, usar `@SerialName` a `categoryId`), `type` (String: "TRIVIA", "CHALLENGE", "RULE"), `content` (Polimórfico).
- **Serialización Polimórfica (CRÍTICO):** 
    - El campo `content` es un objeto anidado. 
    - Debido a que el discriminador `type` reside en el objeto padre (`CardDto`) y no dentro de `content`, el `JsonContentPolymorphicSerializer` **no tiene acceso** al campo `type` del padre.
    - **Regla:** El serializador debe inferir el tipo basándose en las claves únicas presentes dentro del objeto `content` (ej: `"question"` -> `TriviaContentDto`, `"rule"` -> `RuleContentDto`, `"description"` -> `ChallengeContentDto`).