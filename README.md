# Cesta Orgánica 🥦🍎

Aplicación móvil Android para la venta de productos orgánicos (frutas, verduras y orgánicos), desarrollada en **Kotlin** con **Jetpack Compose**. Permite a los usuarios explorar un catálogo, comprar como invitado o registrado, gestionar un carrito, dejar reseñas de productos comprados, y revisar su historial de pedidos. Incluye un panel de administración para gestión de stock y pedidos.

## 📱 Características principales

- **Catálogo de productos** filtrable por categoría (Frutas, Verduras, Orgánicos, Todos), con buscador.
- **Modo invitado**: permite navegar el catálogo y comprar sin necesidad de registrarse, solicitando datos de contacto (nombre, correo, teléfono, dirección) al momento de pagar.
- **Autenticación de usuarios** con registro y login, contraseñas protegidas con **hash bcrypt** (vía `pgcrypto` en Supabase).
- **Carrito de compras** persistente en el dispositivo (Room), con cálculo automático de subtotales.
- **Historial de pedidos** ordenado por fecha real (más reciente primero), con vista de detalle completo por orden (productos, dirección de entrega, datos de contacto, estado).
- **Sistema de reseñas** con calificación de 1 a 5 estrellas y comentario, restringido a usuarios que efectivamente compraron el producto.
- **Favoritos**: marca de productos favoritos por usuario.
- **Panel de administración**: gestión de stock (alta, edición, eliminación de productos) y gestión de pedidos/usuarios.
- **Modo invitado vs. usuario registrado**: navegación y permisos diferenciados (ej. favoritos e historial solo para usuarios registrados).

## 🛠️ Stack técnico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Persistencia local | Room (carrito y catálogo en caché) |
| Backend / Base de datos remota | **Supabase** (PostgreSQL) vía API REST (PostgREST) |
| Seguridad de contraseñas | `pgcrypto` (bcrypt) en funciones RPC de PostgreSQL |
| Carga de imágenes | Coil |
| Arquitectura | MVVM (ViewModel + StateFlow + Repository) |

## 🗄️ Arquitectura de datos

La app combina almacenamiento **local** (Room) y **remoto** (Supabase):

- **Local (Room)**: carrito de compras (`carrito_items`) y caché de catálogo de productos.
- **Remoto (Supabase / PostgreSQL)**: usuarios, pedidos, favoritos, reseñas y catálogo de productos sincronizado entre dispositivos.

La comunicación con Supabase se realiza mediante un cliente HTTP propio (`SupabaseClient.kt`) basado en `HttpURLConnection` y `JSONObject` nativos de Android, sin dependencias externas adicionales, consumiendo la API REST autogenerada de PostgREST.

### Tablas en Supabase

- `usuarios` — datos de perfil y contraseña encriptada (bcrypt).
- `pedidos` — historial de compras, con datos de contacto y entrega por pedido.
- `favoritos` — relación usuario–producto marcado como favorito.
- `resenas` — calificación y comentario por producto, vinculado al usuario que compró.
- `productos` — catálogo sincronizado entre dispositivos.

### Seguridad

- Las contraseñas se almacenan **encriptadas** usando `crypt()` con `gen_salt('bf')` (bcrypt), nunca en texto plano.
- El registro y login de usuarios se procesan mediante **funciones RPC de PostgreSQL** (`registrar_usuario`, `login_usuario`), evitando exponer lógica de hashing en el cliente.

## 🚀 Cómo ejecutar el proyecto

### Requisitos

- Android Studio (versión reciente, compatible con AGP 8.7+)
- JDK 17
- Una cuenta y proyecto creado en [Supabase](https://supabase.com)

### Pasos

1. Clona el repositorio:
```bash
   git clone https://github.com/TiarePerezConcha/cesta-organica.git
```
2. Abre el proyecto en Android Studio y espera a que sincronice Gradle.
3. Configura tus credenciales de Supabase en `SupabaseClient.kt`:
```kotlin
   private const val BASE_URL = "https://TU_PROYECTO.supabase.co"
   private const val API_KEY = "TU_ANON_KEY"
```
4. Crea las tablas necesarias en el **SQL Editor** de Supabase (ver sección de esquema en `/docs` o solicitar el script de creación).
5. Habilita la extensión `pgcrypto` y crea las funciones RPC `registrar_usuario` y `login_usuario`.
6. Ejecuta la app en un emulador o dispositivo físico desde Android Studio (▶️ Run).

## 📂 Estructura del proyecto
app/src/main/java/com/example/cestaOganicaIA/

├── data/

│   ├── database/        # Entidades y configuración de Room

│   ├── dao/              # DAOs de Room (carrito, productos)

│   ├── model/             # Modelos de dominio (Producto, Credential, etc.)

│   ├── remote/           # Cliente HTTP hacia Supabase

│   ├── repository/        # Repositorios (puente entre UI y datos)

│   └── session/          # Gestión de sesión de usuario (reactiva con StateFlow)

├── navigation/            # Definición de rutas y NavHost

├── ui/                    # Pantallas organizadas por módulo (auth, catalog, gestion, etc.)

├── view/                  # Pantallas adicionales (carrito, historial, reseñas, favoritos)

├── viewmodel/             # ViewModels (MVVM)

└── MainActivity.kt

## 🧪 Estado del proyecto

Proyecto desarrollado como parte de la práctica profesional / taller aplicado de programación. Actualmente migrado de Firebase a Supabase como backend principal, con foco en:

- Corrección de estabilidad (manejo de errores de red sin crashear la app).
- Persistencia y sincronización de catálogo entre dispositivos.
- Seguridad de credenciales de usuario.

## 👤 Autora

**Tiare Pérez Concha**
Estudiante de Analista Programador, Duoc UC.

## 📄 Licencia

Proyecto académico, de uso educativo.
