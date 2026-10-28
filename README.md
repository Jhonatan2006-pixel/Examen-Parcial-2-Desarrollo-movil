# GeoTracker - Sistema de Registro de Ubicaciones de Interés

Evaluación Parcial II - Desarrollo de Aplicaciones Móviles.

| Carpeta | Contenido |
|---|---|
| `GeoTracker/` | App Android (Kotlin, Jetpack Compose, Material 3, MVVM) |
| `BackendFinal/` | API REST (Node.js, Express, TypeScript, Sequelize, JWT) |

## App Android

**Tecnologías:** Kotlin · Jetpack Compose + Material Design 3 · MVVM · Navigation Compose ·
StateFlow · Retrofit + OkHttp · Google Maps (maps-compose) · FusedLocationProviderClient.

### Estructura

```
com.example.geotracker
├── GeoTrackerApp.kt          Crea los repositorios (una sola vez)
├── MainActivity.kt
├── model/                    Modelos del API (LoginRequest, Ubicacion, ApiResponse...)
├── network/
│   ├── ApiService.kt         Endpoints de Retrofit
│   ├── AuthInterceptor.kt    Agrega "Authorization: Bearer <token>" a cada petición
│   └── RetrofitClient.kt     OkHttp + Retrofit
├── data/
│   ├── TokenManager.kt       Guarda el token JWT
│   ├── AuthRepository.kt     Login
│   ├── UbicacionRepository.kt Guardar / listar ubicaciones
│   ├── LocationRepository.kt GPS con FusedLocationProviderClient
│   └── ErrorRed.kt           Mensajes de error de red y del backend
└── ui/
    ├── navigation/           NavHost (login, dashboard, captura, listado, mapa)
    ├── login/                LoginState · LoginViewModel · LoginScreen
    ├── dashboard/            DashboardViewModel · DashboardScreen
    ├── captura/              CapturaState · CapturaViewModel · CapturaScreen
    ├── listado/              ListadoState · ListadoViewModel · ListadoScreen
    ├── mapa/                 MapaState · MapaViewModel · MapaScreen (AlertDialog M3)
    ├── componentes/          Indicadores de carga, mensajes y formato
    └── theme/                Tema Material 3
```

### Configuración

En `GeoTracker/local.properties` (no se sube al repositorio):

```properties
sdk.dir=C\:\\Users\\<usuario>\\AppData\\Local\\Android\\Sdk
MAPS_API_KEY=AIza...tu_clave...
BASE_URL=http://77.113.138.104:3001/
```

- `MAPS_API_KEY`: clave de **Maps SDK for Android** creada en Google Cloud Console.
- `BASE_URL`: URL del backend. Para el backend local desde el emulador: `http://10.0.2.2:3000/`.

## Backend

### Endpoints

| Método | Ruta | Token | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | No | Registrar usuario `{nombre, email, password, telefono?}` |
| POST | `/api/auth/login` | No | Iniciar sesión `{email, password}` → `{token, data}` |
| GET | `/api/auth/profile` | Sí | Datos del usuario autenticado |
| POST | `/api/ubicaciones` | Sí | Guardar ubicación `{nombre, descripcion, latitud, longitud}` |
| GET | `/api/ubicaciones` | Sí | Ubicaciones del usuario autenticado |

Las rutas con token exigen el header `Authorization: Bearer <token>` (middleware `authMiddleware`).

### Base de datos

Tabla `dbo.ubicaciones`: `id`, `usuario_id` (FK a `usuarios`), `nombre`, `descripcion`,
`latitud`, `longitud`, `created_at`.

- PostgreSQL (servidor AWS): `database_postgres.sql`
- SQL Server (local): `database.sql`

### Ejecutar

```bash
cd BackendFinal
cp .env.example .env   # completar los valores
npm install
npm run dev            # desarrollo
bash desplegar_aws.sh  # publicar en el servidor de AWS
```
