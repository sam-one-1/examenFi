# 📦 LogiTrack - Sistema de Gestión de Inventario

**LogiTrack** es una aplicación web completa para la gestión de inventarios distribuidos, desarrollada con Spring Boot como backend y un frontend HTML/CSS/JavaScript. El sistema permite gestionar bodegas, productos, movimientos de inventario, usuarios y proporciona un sistema completo de auditoría para rastrear todos los cambios en la base de datos.

---

## 📋 Tabla de Contenidos

- [Características Principales](#-características-principales)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Modelo de Datos](#-modelo-de-datos)
- [Autenticación JWT](#-autenticación-jwt)
- [Sistema de Roles y Permisos](#-sistema-de-roles-y-permisos)
- [API Endpoints](#-api-endpoints)
- [Sistema de Auditoría](#-sistema-de-auditoría)
- [Frontend](#-frontend)
- [Documentación API (Swagger)](#-documentación-api-swagger)
- [Ejemplos de Uso](#-ejemplos-de-uso)
- [Seguridad](#-seguridad)
- [Licencia](#-licencia)

---

## ✨ Características Principales

- 🔐 **Autenticación JWT** con roles y permisos granular
- 📦 **Gestión de Bodegas** - Crear y administrar múltiples bodegas
- 🧾 **Gestión de Productos** - CRUD completo de productos con control de stock
- 🔁 **Movimientos de Inventario** - Entradas, salidas y transferencias entre bodegas
- 👥 **Gestión de Usuarios** - Administración de usuarios con diferentes roles
- 🕵️ **Sistema de Auditoría** - Triggers automáticos en PostgreSQL para rastrear todos los cambios
- 📊 **Reportes** - Resúmenes de stock por bodega y productos más movidos
- 🎨 **Frontend Moderno** - Interfaz web intuitiva y responsive

---

## 🛠 Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security** - Para autenticación y autorización
- **Spring Data JPA** - Para acceso a datos
- **PostgreSQL** - Base de datos relacional
- **JWT (JJWT 0.11.5)** - Tokens de autenticación
- **Lombok** - Para reducir código boilerplate
- **SpringDoc OpenAPI** - Documentación de API (Swagger)

### Frontend
- **HTML5**
- **CSS3** - Estilos modernos y responsive
- **JavaScript (Vanilla)** - Sin frameworks, código puro
- **LocalStorage** - Para almacenamiento del token JWT

### Base de Datos
- **PostgreSQL** - Base de datos principal
- **Triggers SQL** - Para auditoría automática

---

## 📦 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java 17** o superior
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Node.js** (opcional, solo para desarrollo frontend)
- Un navegador web moderno

---

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd LogiTrack_Proyecto_SpringBoot
```

### 2. Configurar Base de Datos

1. Crea una base de datos en PostgreSQL:

```sql
CREATE DATABASE logitrack;
```

2. Configura las credenciales en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/logitrack
spring.datasource.username=postgres
spring.datasource.password=tu_password
```

### 3. Ejecutar Triggers de Auditoría

Ejecuta el script SQL para crear los triggers de auditoría:

```bash
psql -U postgres -d logitrack -f BD/triggers_auditoria.sql
```

### 4. Compilar y Ejecutar el Backend

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

El servidor estará disponible en: `http://localhost:8081`

**Nota importante**: Al iniciar por primera vez, la aplicación creará automáticamente un usuario **root/admin** si no existe ningún usuario ADMIN en el sistema. Las credenciales por defecto son:

- **Documento**: `root`
- **Contraseña**: `admin123`
- **Rol**: ADMIN

Puedes configurar estas credenciales en `application.properties`:
```properties
app.root.user.documento=root
app.root.user.password=admin123
app.root.user.nombre=Administrador Root
```

**⚠️ IMPORTANTE**: Cambiar la contraseña del usuario root después del primer acceso por seguridad.

### 5. Configurar el Frontend

1. Abre el frontend en un servidor web local (puedes usar Live Server en VS Code o cualquier servidor HTTP):

```bash
# Si tienes Python instalado:
cd Frontend
python -m http.server 5500

# O si tienes Node.js con http-server:
npx http-server -p 5500
```

2. Accede a la aplicación en: `http://localhost:5500/index.html`

---

## 📁 Estructura del Proyecto

```
LogiTrack_Proyecto_SpringBoot/
│
├── src/main/java/com/campus/proyecto_springboot/
│   ├── config/              # Configuración de seguridad y filtros
│   │   ├── DataInitializer.java
│   │   ├── JwtFilter.java
│   │   └── SecurityConfig.java
│   │
│   ├── controller/          # Controladores REST
│   │   ├── AuthController.java
│   │   ├── ProductoController.java
│   │   ├── BodegaController.java
│   │   ├── MovimientoInventarioController.java
│   │   ├── UsuarioController.java
│   │   ├── AuditoriaController.java
│   │   └── ReporteController.java
│   │
│   ├── dto/                 # Objetos de Transferencia de Datos
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── ResumenGeneralDTO.java
│   │   ├── StockPorBodegaDTO.java
│   │   └── ProductoMasMovidoDTO.java
│   │
│   ├── exception/           # Manejo de excepciones
│   │   ├── ErrorResponse.java
│   │   ├── GlobalExceptionHandler.java
│   │   ├── InvalidInputException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── UnauthorizedException.java
│   │
│   ├── model/               # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Producto.java
│   │   ├── Bodega.java
│   │   ├── MovimientoInventario.java
│   │   ├── DetalleMovimiento.java
│   │   ├── Auditoria.java
│   │   ├── Role.java
│   │   ├── Permission.java
│   │   ├── TipoMovimiento.java
│   │   └── TipoOperacion.java
│   │
│   ├── repository/          # Repositorios JPA
│   │   ├── UsuarioRepository.java
│   │   ├── ProductoRepository.java
│   │   ├── BodegaRepository.java
│   │   ├── MovimientoInventarioRepository.java
│   │   └── AuditoriaRepository.java
│   │
│   ├── security/            # Servicios de seguridad
│   │   └── CurrentUserService.java
│   │
│   ├── service/             # Lógica de negocio
│   │   ├── JwtUtil.java
│   │   ├── Usuario/
│   │   ├── Producto/
│   │   ├── Bodega/
│   │   ├── MovimientoInventario/
│   │   ├── Auditoria/
│   │   └── Reporte/
│   │
│   └── ProyectoSpringbootApplication.java
│
├── src/main/resources/
│   └── application.properties
│
├── Frontend/
│   ├── index.html           # Página de login/registro
│   ├── dashboard.html       # Panel principal
│   ├── css/
│   │   └── styles.css
│   └── js/
│       ├── auth.js
│       ├── common.js
│       └── dashboard.js
│
├── BD/
│   └── triggers_auditoria.sql
│
└── pom.xml
```

---

## 🗄 Modelo de Datos

### Entidades Principales

#### Usuario
- `id` (Long) - Identificador único
- `nombre` (String) - Nombre del usuario
- `documento` (String, único) - Documento de identidad
- `password` (String) - Contraseña encriptada
- `role` (Role enum) - Rol del usuario (ADMIN o USER)

#### Producto
- `id` (Long) - Identificador único
- `nombre` (String, único) - Nombre del producto
- `categoria` (String) - Categoría del producto
- `stock` (Integer) - Cantidad disponible
- `precio` (Double) - Precio unitario

#### Bodega
- `id` (Long) - Identificador único
- `nombre` (String) - Nombre de la bodega
- `ubicacion` (String) - Ubicación física
- `capacidad` (Integer) - Capacidad máxima
- `encargado` (Usuario) - Usuario encargado

#### MovimientoInventario
- `id` (Long) - Identificador único
- `fecha` (LocalDateTime) - Fecha y hora del movimiento
- `tipoMovimiento` (TipoMovimiento enum) - ENTRADA, SALIDA o TRANSFERENCIA
- `usuarioResponsable` (Usuario) - Usuario que realizó el movimiento
- `bodegaOrigen` (Bodega) - Bodega origen (opcional)
- `bodegaDestino` (Bodega) - Bodega destino (opcional)
- `detalles` (List<DetalleMovimiento>) - Lista de productos movidos

#### DetalleMovimiento
- `id` (Long) - Identificador único
- `cantidad` (Integer) - Cantidad movida
- `producto` (Producto) - Producto movido
- `movimiento` (MovimientoInventario) - Movimiento padre

#### Auditoria
- `id` (Long) - Identificador único
- `tipoOperacion` (TipoOperacion enum) - INSERT, UPDATE o DELETE
- `fechaHora` (LocalDateTime) - Fecha y hora de la operación
- `usuarioResponsable` (Usuario) - Usuario que realizó la operación
- `entidadAfectada` (String) - Nombre de la tabla afectada
- `valoresAnteriores` (String) - JSON con valores anteriores
- `valoresNuevos` (String) - JSON con valores nuevos

### Relaciones

- **Usuario ↔ Bodega**: Uno a Muchos (Un usuario puede ser encargado de varias bodegas)
- **MovimientoInventario ↔ Usuario**: Muchos a Uno
- **MovimientoInventario ↔ Bodega**: Muchos a Uno (origen y destino)
- **MovimientoInventario ↔ DetalleMovimiento**: Uno a Muchos
- **DetalleMovimiento ↔ Producto**: Muchos a Uno

---

## 🔐 Autenticación JWT

### Usuario Root (Admin Inicial)

Al iniciar la aplicación por primera vez, se crea automáticamente un usuario **root/admin** si no existe ningún usuario ADMIN en el sistema. Este usuario tiene:

- **Documento**: `root` (configurable en `application.properties`)
- **Contraseña**: `admin123` (configurable en `application.properties`)
- **Nombre**: `Administrador Root` (configurable en `application.properties`)
- **Rol**: ADMIN

**Comportamiento**:
- Si existe al menos un usuario ADMIN, no se crea el usuario root
- Si el usuario root existe pero no es ADMIN, se promociona automáticamente a ADMIN
- Puedes usar este usuario para crear otros administradores y gestionar el sistema

**⚠️ IMPORTANTE**: Cambiar la contraseña del usuario root después del primer acceso por seguridad.

### Flujo de Autenticación

1. **Inicialización**: Al arrancar, se verifica si existe un usuario ADMIN, si no, se crea el usuario root
2. **Registro/Login**: El usuario se registra o inicia sesión en `/auth/register` o `/auth/login`
3. **Generación de Token**: El servidor genera un JWT con la información del usuario
4. **Almacenamiento**: El frontend almacena el token en `localStorage`
5. **Envío en Requests**: Todas las peticiones incluyen el header `Authorization: Bearer <token>`
6. **Validación**: El `JwtFilter` valida el token en cada petición protegida

### Estructura del Token JWT

El token JWT contiene:
- **Subject (sub)**: Documento del usuario
- **role**: Rol del usuario (ADMIN o USER)
- **permissions**: Lista de permisos del usuario
- **authorities**: Lista de autoridades (permisos + rol)
- **exp**: Fecha de expiración (1 hora desde la generación)

### Configuración JWT

El token se genera utilizando:
- **Algoritmo**: HS256
- **Expiración**: 1 hora (3600000 ms)
- **Secret Key**: Generada automáticamente al iniciar la aplicación

---

## 👥 Sistema de Roles y Permisos

### Asignación de Roles

- **Registro de usuarios**: Todos los usuarios nuevos se registran automáticamente con rol `USER`
- **Promoción a ADMIN**: Solo un administrador puede promover un usuario USER a ADMIN mediante el endpoint `/api/usuarios/{id}/rol`
- **Seguridad**: Los usuarios no pueden auto-promoverse ni registrarse directamente como ADMIN

### Roles Disponibles

#### ADMIN
Tiene acceso completo al sistema:
- ✅ Lectura, creación y eliminación de usuarios
- ✅ **Cambio de roles de usuarios** (promover USER a ADMIN o degradar ADMIN a USER)
- ✅ Lectura, creación y eliminación de movimientos de inventario
- ✅ Acceso total a bodegas, productos y auditorías

#### USER (Empleado)
Tiene acceso limitado:
- ✅ Lectura de movimientos de inventario
- ✅ Creación de movimientos de inventario
- ✅ Eliminación de movimientos de inventario
- ❌ No puede gestionar usuarios, bodegas, productos ni auditorías
- ❌ No puede cambiar roles de usuarios

### Permisos Disponibles

```java
READ_USERS
CREATE_USERS
DELETE_USERS
READ_MOVIMIENTO_INVENTARIO
CREATE_MOVIMIENTO_INVENTARIO
DELETE_MOVIMIENTO_INVENTARIO
```

### Configuración de Seguridad

La seguridad se configura en `SecurityConfig.java`:

- **Endpoints públicos**: `/auth/**`, `/swagger-ui/**`
- **Endpoints de movimientos**: Requieren permisos específicos o rol ADMIN
- **Endpoints administrativos**: Requieren rol ADMIN (`/bodegas/**`, `/productos/**`, `/auditorias/**`, `/usuarios/**`)

---

## 🌐 API Endpoints

### Base URL
```
http://localhost:8081
```

### Autenticación (`/auth`)

#### POST `/auth/register`
Registra un nuevo usuario. **Todos los usuarios se registran automáticamente con rol USER**. Solo los administradores pueden promover usuarios a ADMIN.

**Request Body:**
```json
{
  "nombre": "Juan Pérez",
  "documento": "123456789",
  "password": "password123"
}
```

**Nota:** El campo `role` no es necesario y será ignorado si se envía. Todos los nuevos usuarios se crean con rol `USER`.

**Response:** Usuario creado (200 OK)

---

#### POST `/auth/login`
Inicia sesión y obtiene un token JWT.

**Request Body:**
```json
{
  "documento": "123456789",
  "password": "password123"
}
```

**Response:** Token JWT (String)

---

### Productos (`/productos`)

**Requiere:** Rol ADMIN

#### GET `/productos`
Obtiene todos los productos.

**Response:**
```json
[
  {
    "id": 1,
    "nombre": "Laptop HP",
    "categoria": "Electrónica",
    "stock": 50,
    "precio": 899.99
  }
]
```

---

#### GET `/productos/{id}`
Obtiene un producto por ID.

**Response:**
```json
{
  "id": 1,
  "nombre": "Laptop HP",
  "categoria": "Electrónica",
  "stock": 50,
  "precio": 899.99
}
```

---

#### GET `/productos/nombre/{nombre}`
Obtiene un producto por nombre.

---

#### POST `/productos`
Crea un nuevo producto.

**Request Body:**
```json
{
  "nombre": "Laptop HP",
  "categoria": "Electrónica",
  "stock": 50,
  "precio": 899.99
}
```

**Response:** Producto creado (201 Created)

---

#### PUT `/productos/{id}`
Actualiza un producto existente.

**Request Body:**
```json
{
  "nombre": "Laptop HP Pro",
  "categoria": "Electrónica",
  "stock": 60,
  "precio": 999.99
}
```

**Response:** Producto actualizado (200 OK)

---

#### DELETE `/productos/{id}`
Elimina un producto.

**Response:** 204 No Content

---

#### GET `/productos/stock-bajo?limite=10`
Obtiene productos con stock menor al límite especificado.

**Query Parameters:**
- `limite` (Integer, opcional): Límite de stock (default: 10)

**Response:** Lista de productos con stock bajo

---

### Bodegas (`/bodegas`)

**Requiere:** Rol ADMIN

#### GET `/bodegas`
Obtiene todas las bodegas.

**Response:**
```json
[
  {
    "id": 1,
    "nombre": "Bodega Central",
    "ubicacion": "Bogotá",
    "capacidad": 1000,
    "encargado": {
      "id": 1,
      "nombre": "Juan Pérez",
      "documento": "123456789"
    }
  }
]
```

---

#### GET `/bodegas/{id}`
Obtiene una bodega por ID.

---

#### GET `/bodegas/nombre/{nombre}`
Obtiene una bodega por nombre.

---

#### POST `/bodegas`
Crea una nueva bodega.

**Request Body:**
```json
{
  "nombre": "Bodega Central",
  "ubicacion": "Bogotá",
  "capacidad": 1000,
  "encargado": {
    "id": 1
  }
}
```

**Response:** Bodega creada (200 OK)

---

#### PUT `/bodegas/{id}`
Actualiza una bodega existente.

**Request Body:**
```json
{
  "nombre": "Bodega Central Actualizada",
  "ubicacion": "Medellín",
  "capacidad": 1500,
  "encargado": {
    "id": 2
  }
}
```

**Response:** Bodega actualizada (200 OK)

---

#### DELETE `/bodegas/{id}`
Elimina una bodega.

**Response:** 204 No Content

---

### Movimientos de Inventario (`/movimientoInventario`)

**Requiere:** Permisos de movimiento o rol ADMIN

#### GET `/movimientoInventario`
Lista todos los movimientos de inventario.

**Response:**
```json
[
  {
    "id": 1,
    "fecha": "2024-01-15T10:30:00",
    "tipoMovimiento": "ENTRADA",
    "usuarioResponsable": {
      "id": 1,
      "nombre": "Juan Pérez",
      "documento": "123456789"
    },
    "bodegaOrigen": null,
    "bodegaDestino": {
      "id": 1,
      "nombre": "Bodega Central"
    },
    "detalles": [
      {
        "id": 1,
        "cantidad": 10,
        "producto": {
          "id": 1,
          "nombre": "Laptop HP"
        }
      }
    ]
  }
]
```

---

#### GET `/movimientoInventario/{id}`
Obtiene un movimiento por ID.

---

#### POST `/movimientoInventario`
Registra un nuevo movimiento de inventario y actualiza el stock automáticamente.

**Request Body (ENTRADA):**
```json
{
  "tipoMovimiento": "ENTRADA",
  "bodegaDestino": {
    "id": 1
  },
  "detalles": [
    {
      "cantidad": 10,
      "producto": {
        "id": 1
      }
    }
  ]
}
```

**Request Body (SALIDA):**
```json
{
  "tipoMovimiento": "SALIDA",
  "bodegaOrigen": {
    "id": 1
  },
  "detalles": [
    {
      "cantidad": 5,
      "producto": {
        "id": 1
      }
    }
  ]
}
```

**Request Body (TRANSFERENCIA):**
```json
{
  "tipoMovimiento": "TRANSFERENCIA",
  "bodegaOrigen": {
    "id": 1
  },
  "bodegaDestino": {
    "id": 2
  },
  "detalles": [
    {
      "cantidad": 5,
      "producto": {
        "id": 1
      }
    }
  ]
}
```

**Notas:**
- La fecha se asigna automáticamente si no se proporciona
- El usuario responsable se obtiene del contexto de seguridad
- El stock se actualiza automáticamente según el tipo de movimiento:
  - **ENTRADA**: Aumenta el stock
  - **SALIDA**: Disminuye el stock (valida disponibilidad)
  - **TRANSFERENCIA**: No modifica el stock global

---

#### PUT `/movimientoInventario/{id}`
Actualiza un movimiento existente (actualización ligera, no recalcula stock).

---

#### DELETE `/movimientoInventario/{id}`
Elimina un movimiento (no recalcula stock).

---

#### GET `/movimientoInventario/por-fecha?desde=2024-01-01T00:00:00&hasta=2024-01-31T23:59:59`
Filtra movimientos por rango de fechas.

**Query Parameters:**
- `desde` (DateTime): Fecha inicial
- `hasta` (DateTime): Fecha final

**Response:** Lista de movimientos en el rango especificado

---

### Usuarios (`/api/usuarios`)

**Requiere:** Rol ADMIN

#### GET `/api/usuarios`
Obtiene todos los usuarios.

---

#### GET `/api/usuarios/{id}`
Obtiene un usuario por ID.

---

#### GET `/api/usuarios/documento/{documento}`
Obtiene un usuario por documento.

---

#### POST `/api/usuarios`
Crea un nuevo usuario.

**Request Body:**
```json
{
  "nombre": "María García",
  "documento": "987654321",
  "password": "password123",
  "role": "USER"
}
```

---

#### PUT `/api/usuarios/{id}`
Actualiza un usuario existente.

---

#### DELETE `/api/usuarios/{id}`
Elimina un usuario.

---

#### PATCH `/api/usuarios/{id}/rol`
Cambia el rol de un usuario. **Solo los administradores pueden cambiar roles.**

**Request Body:**
```json
{
  "nuevoRol": "ADMIN"
}
```

**Valores válidos para `nuevoRol`:**
- `USER` - Usuario normal (Empleado)
- `ADMIN` - Administrador

**Response:** Usuario actualizado con el nuevo rol (200 OK)

**Nota:** Este es el único método para promover un usuario USER a ADMIN. Los usuarios no pueden auto-promoverse ni registrarse como ADMIN.

---

### Auditorías (`/auditorias`)

**Requiere:** Rol ADMIN

#### GET `/auditorias`
Obtiene todas las auditorías.

**Response:**
```json
[
  {
    "id": 1,
    "tipoOperacion": "INSERT",
    "fechaHora": "2024-01-15T10:30:00",
    "usuarioResponsable": {
      "id": 1,
      "nombre": "Juan Pérez",
      "documento": "123456789"
    },
    "entidadAfectada": "productos",
    "valoresAnteriores": null,
    "valoresNuevos": "{\"id\":1,\"nombre\":\"Laptop HP\",\"categoria\":\"Electrónica\",\"stock\":50,\"precio\":899.99}"
  }
]
```

---

#### GET `/auditorias/{id}`
Obtiene una auditoría por ID.

---

#### GET `/auditorias/listar?documento=123456789&tipoOperacion=INSERT`
Filtra auditorías por usuario o tipo de operación.

**Query Parameters:**
- `documento` (String, opcional): Documento del usuario
- `tipoOperacion` (String, opcional): INSERT, UPDATE o DELETE

---

#### POST `/auditorias`
Crea una auditoría manualmente (las auditorías normalmente se crean automáticamente mediante triggers).

---

#### PUT `/auditorias/{id}`
Actualiza una auditoría.

---

#### DELETE `/auditorias/{id}`
Elimina una auditoría.

---

### Reportes (`/reportes`)

#### GET `/reportes/resumen`
Obtiene un resumen general con stock por bodega y productos más movidos.

**Response:**
```json
{
  "stockPorBodega": [
    {
      "bodegaId": 1,
      "bodegaNombre": "Bodega Central",
      "totalStock": 150
    }
  ],
  "productosMasMovidos": [
    {
      "productoId": 1,
      "productoNombre": "Laptop HP",
      "totalMovimientos": 25
    }
  ]
}
```

---

## 🕵️ Sistema de Auditoría

El sistema incluye un sistema de auditoría automática mediante triggers de PostgreSQL. Cada cambio (INSERT, UPDATE, DELETE) en las siguientes tablas se registra automáticamente:

- `bodegas`
- `productos`
- `movimientos_inventario`
- `detalle_movimiento`
- `usuarios`

### Funcionamiento

1. **Trigger Function**: `fn_registrar_auditoria()` captura todos los cambios
2. **Registro Automático**: Se crea un registro en la tabla `auditorias` con:
   - Tipo de operación (INSERT, UPDATE, DELETE)
   - Fecha y hora
   - Usuario responsable (si está disponible en el contexto)
   - Entidad afectada (nombre de la tabla)
   - Valores anteriores (JSON)
   - Valores nuevos (JSON)

### Configuración del Usuario en Auditoría

Para que la auditoría capture el usuario responsable, el backend establece una variable de sesión en PostgreSQL:

```java
// Ejemplo en el servicio
auditoriaContextService.setUsuarioActual(userId);
```

Esto permite que los triggers identifiquen quién realizó la operación.

### Instalación de Triggers

Los triggers se instalan ejecutando el script SQL:

```bash
psql -U postgres -d logitrack -f BD/triggers_auditoria.sql
```

---

## 🎨 Frontend

El frontend está desarrollado en HTML, CSS y JavaScript vanilla, sin frameworks adicionales.

### Estructura del Frontend

- **index.html**: Página de login y registro
- **dashboard.html**: Panel principal con todas las funcionalidades
- **css/styles.css**: Estilos modernos y responsive
- **js/auth.js**: Lógica de autenticación
- **js/common.js**: Funciones comunes (fetch, JWT, etc.)
- **js/dashboard.js**: Lógica del dashboard

### Características del Frontend

- ✅ **Autenticación JWT**: Almacenamiento y uso del token
- ✅ **Gestión de Sesión**: Detección de expiración del token
- ✅ **Interfaz Responsive**: Adaptable a diferentes tamaños de pantalla
- ✅ **Feedback Visual**: Alertas de éxito y error
- ✅ **Tablas Dinámicas**: Renderizado automático de datos

### Configuración

El frontend se conecta al backend mediante la variable `API_URL` en `common.js`:

```javascript
const API_URL = "http://localhost:8081";
```

Asegúrate de que el backend esté ejecutándose en ese puerto antes de usar el frontend.

### Navegación

1. **Login/Registro** (`index.html`):
   - Permite registrarse o iniciar sesión
   - **Todos los usuarios se registran automáticamente con rol USER**
   - No se puede seleccionar el rol durante el registro
   - Al iniciar sesión, redirige al dashboard

2. **Dashboard** (`dashboard.html`):
   - Secciones: Bodegas, Productos, Movimientos, Auditorías, Usuarios (solo ADMIN), Reportes
   - Acceso basado en permisos del usuario
   - Funcionalidad completa de CRUD para cada módulo
   - **Gestión de Usuarios (solo ADMIN)**:
     - Listar todos los usuarios del sistema
     - Cambiar el rol de usuarios (USER ↔ ADMIN)
     - Solo los administradores pueden promocionar usuarios a ADMIN

---

## 📚 Documentación API (Swagger)

El proyecto incluye Swagger/OpenAPI para documentación interactiva de la API.

### Acceder a Swagger

Una vez que la aplicación esté ejecutándose, accede a:

```
http://localhost:8081/swagger-ui.html
```

### Funcionalidades de Swagger

- 📖 Visualización de todos los endpoints
- 🔍 Prueba de endpoints directamente desde el navegador
- 📝 Documentación automática de modelos y DTOs
- 🔐 Autenticación con JWT en Swagger

### Configurar JWT en Swagger

1. Inicia sesión y obtén tu token JWT
2. En Swagger UI, haz clic en el botón "Authorize"
3. Ingresa: `Bearer <tu-token-jwt>`
4. Ahora puedes probar todos los endpoints protegidos

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Iniciar Sesión con Usuario Root

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "documento": "root",
    "password": "admin123"
  }'
```

**Response:** Token JWT que puedes usar para autenticarte en otros endpoints.

### Ejemplo 2: Registrar un Producto

```bash
curl -X POST http://localhost:8081/productos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <tu-token-jwt>" \
  -d '{
    "nombre": "Mouse Logitech",
    "categoria": "Periféricos",
    "stock": 100,
    "precio": 25.99
  }'
```

### Ejemplo 3: Crear una Entrada de Inventario

```bash
curl -X POST http://localhost:8081/movimientoInventario \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <tu-token-jwt>" \
  -d '{
    "tipoMovimiento": "ENTRADA",
    "bodegaDestino": {
      "id": 1
    },
    "detalles": [
      {
        "cantidad": 50,
        "producto": {
          "id": 1
        }
      }
    ]
  }'
```

### Ejemplo 4: Obtener Resumen de Reportes

```bash
curl -X GET http://localhost:8081/reportes/resumen \
  -H "Authorization: Bearer <tu-token-jwt>"
```

### Ejemplo 5: Filtrar Movimientos por Fecha

```bash
curl -X GET "http://localhost:8081/movimientoInventario/por-fecha?desde=2024-01-01T00:00:00&hasta=2024-01-31T23:59:59" \
  -H "Authorization: Bearer <tu-token-jwt>"
```

### Ejemplo 6: Cambiar Rol de Usuario (Solo ADMIN)

```bash
curl -X PATCH http://localhost:8081/api/usuarios/2/rol \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <tu-token-jwt>" \
  -d '{
    "nuevoRol": "ADMIN"
  }'
```

---

## 🔒 Seguridad

### Medidas de Seguridad Implementadas

1. **Autenticación JWT**: Tokens con expiración de 1 hora
2. **Autorización basada en Roles**: Control de acceso granular
3. **Filtro de Seguridad**: Validación de tokens en cada request
4. **CORS Configurado**: Solo permite requests desde orígenes específicos
5. **CSRF Deshabilitado**: No aplicable para APIs REST stateless
6. **Contraseñas Encriptadas**: Almacenamiento seguro de contraseñas
7. **Validación de Input**: Validación en controladores y servicios
8. **Manejo de Excepciones**: Respuestas de error consistentes

### Configuración CORS

Los orígenes permitidos están configurados en `SecurityConfig.java`:

```java
c.setAllowedOrigins(List.of(
    "http://localhost:5500",
    "http://127.0.0.1:5500"
));
```

Si necesitas agregar más orígenes, modifica esta configuración.

### Recomendaciones de Seguridad

- ✅ Usa HTTPS en producción
- ✅ Cambia la secret key de JWT a una variable de entorno
- ✅ Implementa rate limiting
- ✅ Usa variables de entorno para credenciales de BD
- ✅ Considera implementar refresh tokens
- ✅ Valida y sanitiza todos los inputs

---

## 🐛 Solución de Problemas

### Error de Conexión a la Base de Datos

**Problema**: `Connection refused` o error al conectarse a PostgreSQL

**Solución**:
1. Verifica que PostgreSQL esté ejecutándose
2. Confirma las credenciales en `application.properties`
3. Asegúrate de que la base de datos `logitrack` existe

### Token JWT Expirado

**Problema**: Error 401 (Unauthorized) en todas las peticiones

**Solución**: Inicia sesión nuevamente para obtener un nuevo token

### Error 403 (Forbidden)

**Problema**: No tienes permisos para realizar la acción

**Solución**: Verifica que tu usuario tenga el rol y permisos necesarios

### Frontend no se conecta al Backend

**Problema**: Error de CORS o conexión rechazada

**Solución**:
1. Verifica que el backend esté ejecutándose en el puerto 8081
2. Confirma que la URL en `common.js` sea correcta
3. Verifica la configuración de CORS en `SecurityConfig.java`

---

## 📝 Notas Adicionales

### Desarrollo

- El proyecto usa Lombok para reducir código boilerplate
- Las contraseñas se encriptan en el servicio de usuarios
- El stock se actualiza automáticamente al crear movimientos
- Los triggers de auditoría se ejecutan a nivel de base de datos
- **Usuario root**: Se crea automáticamente al iniciar la aplicación si no existe ningún ADMIN
- El componente `DataInitializer` se ejecuta al arrancar y verifica/crea el usuario root

### Producción

Antes de desplegar a producción:

1. Cambia las credenciales de la base de datos
2. Configura variables de entorno para valores sensibles
3. Usa HTTPS
4. Configura un secret key de JWT más seguro
5. Revisa y ajusta la configuración de CORS
6. Implementa logging adecuado
7. Configura backups de la base de datos

---

## 📄 Licencia

Este proyecto es de uso educativo y académico.

---

## 👥 Contribuciones

Este proyecto fue desarrollado como parte de un proyecto académico. Las contribuciones son bienvenidas.

---

## Autores

- Santiago Mendoza
- Nicolas Florez
- Julian Garcia
- Samuel Niño
- Diego Figueroa
- Santiago Maestre


**LogiTrack** - Sistema de Gestión de Inventario con Spring Boot 🚀

