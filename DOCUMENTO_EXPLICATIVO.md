# Documento Explicativo - LogiTrack

## 📋 Tabla de Contenidos

1. [Diagrama de Clases](#diagrama-de-clases)
2. [Descripción de Arquitectura](#descripción-de-arquitectura)
3. [Ejemplo de Token JWT y su Uso](#ejemplo-de-token-jwt-y-su-uso)

---

## 📊 Diagrama de Clases

### Entidades Principales

```
┌─────────────────────────────────────────────────────────────────┐
│                         MODELO DE DATOS                         │
└─────────────────────────────────────────────────────────────────┘

┌──────────────┐
│   Usuario    │
├──────────────┤
│ +id: Long    │
│ +nombre: Str │
│ +documento:  │
│   String     │
│ +password:   │
│   String     │
│ +role: Role  │
└───────┬──────┘
        │ 1
        │
        │ *
        │
┌───────▼───────┐
│    Bodega     │
├───────────────┤
│ +id: Long     │
│ +nombre: Str  │
│ +ubicacion:   │
│   String      │
│ +capacidad:   │
│   Integer     │
│ +encargado:   │
│   Usuario     │
└───────┬───────┘
        │ 1
        │
        │ *
        │
┌───────▼──────────────┐
│    StockBodega       │
├──────────────────────┤
│ +id: Long            │
│ +producto: Producto  │
│ +bodega: Bodega      │
│ +cantidad: Integer   │
└───────┬──────────────┘
        │
        │ *
        │
┌───────▼──────────────┐
│     Producto         │
├──────────────────────┤
│ +id: Long            │
│ +nombre: String      │
│ +categoria: String   │
│ +stock: Integer      │
│ +precio: Double      │
└───────┬──────────────┘
        │ 1
        │
        │ *
        │
┌───────▼──────────────────────┐
│   DetalleMovimiento           │
├───────────────────────────────┤
│ +id: Long                     │
│ +cantidad: Integer            │
│ +producto: Producto           │
│ +movimiento: MovimientoInv.   │
└───────┬───────────────────────┘
        │ *
        │
        │ 1
        │
┌───────▼──────────────────────┐
│  MovimientoInventario         │
├───────────────────────────────┤
│ +id: Long                     │
│ +fecha: LocalDateTime         │
│ +tipoMovimiento:              │
│   TipoMovimiento              │
│ +usuarioResponsable: Usuario  │
│ +bodegaOrigen: Bodega         │
│ +bodegaDestino: Bodega        │
│ +detalles: List<Detalle>      │
└───────┬───────────────────────┘
        │
        │
┌───────▼──────────────┐
│     Auditoria       │
├──────────────────────┤
│ +id: Long            │
│ +tipoOperacion:     │
│   TipoOperacion      │
│ +fechaHora:         │
│   LocalDateTime     │
│ +usuarioResponsable:│
│   Usuario           │
│ +entidadAfectada:   │
│   String            │
│ +valoresAnteriores: │
│   String (JSON)     │
│ +valoresNuevos:     │
│   String (JSON)     │
└──────────────────────┘
```

### Relaciones entre Entidades

```
Usuario (1) ────────< (*) Bodega
  │                      (encargado)
  │
  │ (1) ────────< (*) MovimientoInventario
  │                      (usuarioResponsable)
  │
  │ (1) ────────< (*) Auditoria
  │                      (usuarioResponsable)

Producto (1) ────────< (*) StockBodega
  │                      (producto)
  │
  │ (1) ────────< (*) DetalleMovimiento
  │                      (producto)

Bodega (1) ────────< (*) StockBodega
  │                      (bodega)
  │
  │ (1) ────────< (*) MovimientoInventario
  │                      (bodegaOrigen)
  │
  │ (1) ────────< (*) MovimientoInventario
  │                      (bodegaDestino)

MovimientoInventario (1) ────────< (*) DetalleMovimiento
                              (movimiento)
```

### Enumeraciones

```
┌──────────────────┐
│      Role        │
├──────────────────┤
│ ADMIN            │
│ USER             │
└──────────────────┘

┌──────────────────┐
│  TipoMovimiento  │
├──────────────────┤
│ ENTRADA          │
│ SALIDA            │
│ TRANSFERENCIA    │
└──────────────────┘

┌──────────────────┐
│  TipoOperacion   │
├──────────────────┤
│ INSERT           │
│ UPDATE           │
│ DELETE           │
└──────────────────┘
```

---

## 🏗️ Descripción de Arquitectura

### Arquitectura General

El proyecto **LogiTrack** sigue una arquitectura en capas (Layered Architecture) basada en Spring Boot, implementando el patrón MVC (Model-View-Controller) y principios de diseño orientado a objetos.

```
┌─────────────────────────────────────────────────────────────┐
│                      CAPA DE PRESENTACIÓN                   │
│  Frontend (HTML/CSS/JavaScript)                             │
│  - index.html (Login/Registro)                             │
│  - dashboard.html (Panel de administración)               │
└─────────────────────────────────────────────────────────────┘
                            ↕ HTTP/REST
┌─────────────────────────────────────────────────────────────┐
│                      CAPA DE CONTROLADORES                 │
│  Controllers (REST API)                                     │
│  - AuthController                                           │
│  - UsuarioController                                        │
│  - BodegaController                                         │
│  - ProductoController                                       │
│  - MovimientoInventarioController                           │
│  - AuditoriaController                                      │
│  - ReporteController                                        │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                      CAPA DE SERVICIOS                      │
│  Services (Lógica de Negocio)                               │
│  - UsuarioService / UsuarioServiceImpl                      │
│  - BodegaService / BodegaServiceImpl                       │
│  - ProductoService / ProductoServiceImpl                     │
│  - MovimientoInventarioService / ...Impl                   │
│  - StockBodegaService / StockBodegaServiceImpl            │
│  - AuditoriaService / AuditoriaServiceImpl                  │
│  - JwtUtil (Utilidades JWT)                                │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                      CAPA DE PERSISTENCIA                  │
│  Repositories (JPA)                                          │
│  - UsuarioRepository                                        │
│  - BodegaRepository                                         │
│  - ProductoRepository                                       │
│  - MovimientoInventarioRepository                          │
│  - StockBodegaRepository                                    │
│  - AuditoriaRepository                                      │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                      BASE DE DATOS                          │
│  PostgreSQL                                                  │
│  - Tablas: usuarios, bodegas, productos,                    │
│    movimientos_inventario, detalle_movimiento,              │
│    stock_bodega, auditorias                                 │
│  - Triggers: fn_registrar_auditoria()                      │
└─────────────────────────────────────────────────────────────┘
```

### Capas del Sistema

#### 1. **Capa de Presentación (Frontend)**
- **Tecnologías**: HTML5, CSS3, JavaScript (Vanilla)
- **Librerías**: SweetAlert2
- **Archivos principales**:
  - `index.html`: Página de login y registro
  - `dashboard.html`: Panel principal de administración
  - `js/common.js`: Funciones comunes (JWT, API calls)
  - `js/auth.js`: Lógica de autenticación
  - `js/dashboard.js`: Lógica del dashboard
  - `css/styles.css`: Estilos del sistema

#### 2. **Capa de Controladores (Controllers)**
- **Responsabilidad**: Manejar las peticiones HTTP y devolver respuestas
- **Patrón**: RESTful API
- **Endpoints principales**:
  - `/auth/**`: Autenticación (login, registro)
  - `/api/usuarios/**`: Gestión de usuarios
  - `/bodegas/**`: Gestión de bodegas
  - `/productos/**`: Gestión de productos
  - `/movimientoInventario/**`: Gestión de movimientos
  - `/auditorias/**`: Consulta de auditorías
  - `/reportes/**`: Reportes del sistema

#### 3. **Capa de Servicios (Services)**
- **Responsabilidad**: Contener la lógica de negocio
- **Patrón**: Service Layer Pattern
- **Características**:
  - Validaciones de negocio
  - Transformaciones de datos
  - Coordinación entre repositorios
  - Gestión de transacciones (`@Transactional`)

#### 4. **Capa de Persistencia (Repositories)**
- **Responsabilidad**: Acceso a datos
- **Tecnología**: Spring Data JPA
- **Características**:
  - Métodos CRUD automáticos
  - Consultas personalizadas con `@Query`
  - Relaciones JPA (`@ManyToOne`, `@OneToMany`)

#### 5. **Capa de Modelo (Entities)**
- **Responsabilidad**: Representar las entidades del dominio
- **Tecnología**: JPA/Hibernate
- **Anotaciones principales**:
  - `@Entity`: Marca la clase como entidad JPA
  - `@Table`: Especifica el nombre de la tabla
  - `@Id`: Identificador único
  - `@ManyToOne`, `@OneToMany`: Relaciones entre entidades
  - `@Enumerated`: Mapeo de enums

### Configuración y Seguridad

#### **SecurityConfig**
- Configuración de Spring Security
- Filtros de autenticación JWT
- Reglas de autorización por roles
- Configuración CORS

#### **JwtFilter**
- Filtro personalizado para validar tokens JWT
- Extrae información del token y la establece en el contexto de seguridad

#### **DataInitializer**
- Inicialización de datos al arrancar la aplicación
- Crea el usuario root si no existe ningún ADMIN

### Patrones de Diseño Implementados

1. **Repository Pattern**: Abstracción del acceso a datos
2. **Service Layer Pattern**: Separación de lógica de negocio
3. **DTO Pattern**: Transferencia de datos entre capas
4. **Singleton Pattern**: Servicios Spring (por defecto)
5. **Strategy Pattern**: Diferentes tipos de movimientos (ENTRADA, SALIDA, TRANSFERENCIA)
6. **Observer Pattern**: Triggers de PostgreSQL para auditoría

### Flujo de una Petición

```
1. Cliente (Frontend)
   ↓ HTTP Request
2. SecurityConfig (Validación de rutas)
   ↓
3. JwtFilter (Validación de token JWT)
   ↓
4. Controller (Mapeo de endpoint)
   ↓
5. Service (Lógica de negocio)
   ↓
6. Repository (Acceso a datos)
   ↓
7. Base de Datos (PostgreSQL)
   ↓
8. Trigger (Auditoría automática)
   ↓
9. Response (JSON)
   ↓
10. Cliente (Frontend)
```

---

## 🔐 Ejemplo de Token JWT y su Uso

### Estructura del Token JWT

Un token JWT está compuesto por tres partes separadas por puntos (`.`):

```
header.payload.signature
```

#### 1. **Header (Encabezado)**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### 2. **Payload (Carga útil)**
```json
{
  "sub": "123456789",
  "role": "ADMIN",
  "permissions": [
    "READ_USERS",
    "CREATE_USERS",
    "DELETE_USERS",
    "READ_MOVIMIENTO_INVENTARIO",
    "CREATE_MOVIMIENTO_INVENTARIO",
    "DELETE_MOVIMIENTO_INVENTARIO"
  ],
  "authorities": [
    "READ_USERS",
    "CREATE_USERS",
    "DELETE_USERS",
    "READ_MOVIMIENTO_INVENTARIO",
    "CREATE_MOVIMIENTO_INVENTARIO",
    "DELETE_MOVIMIENTO_INVENTARIO",
    "ROLE_ADMIN"
  ],
  "exp": 1704110400,
  "iat": 1704106800
}
```

#### 3. **Signature (Firma)**
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

### Ejemplo Completo de Token

**Token JWT generado** (ejemplo):
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkiLCJyb2xlIjoiQURNSU4iLCJwZXJtaXNzaW9ucyI6WyJSRUFEX1VTRVJTIiwiQ1JFQVRFX1VTRVJTIiwiREVMRVRFX1VTRVJTIiwiUkVBRF9NT1ZJTUlFTlRPX0lOVkVOVEFSSU8iLCJDUkVBVEVfTU9WSU1JRU5UT19JTlZFTlRBUklPIiwiREVMRVRFX01PVklNSUVOVE9fSU5WRU5UQVJJTyJdLCJhdXRob3JpdGllcyI6WyJSRUFEX1VTRVJTIiwiQ1JFQVRFX1VTRVJTIiwiREVMRVRFX1VTRVJTIiwiUkVBRF9NT1ZJTUlFTlRPX0lOVkVOVEFSSU8iLCJDUkVBVEVfTU9WSU1JRU5UT19JTlZFTlRBUklPIiwiREVMRVRFX01PVklNSUVOVE9fSU5WRU5UQVJJTyIsIlJPTEVfQURNSU4iXSwiZXhwIjoxNzA0MTEwNDAwfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### Generación del Token

El token se genera en `JwtUtil.generateToken()`:

```java
public String generateToken(String documento, Role role) {
    List<String> permissions = role.permissions.stream()
            .map(Enum::name)
            .collect(Collectors.toList());

    return Jwts.builder()
            .setSubject(documento)              // sub: documento del usuario
            .claim("role", role.name())         // role: ADMIN o USER
            .claim("permissions", permissions)  // permissions: lista de permisos
            .claim("authorities", role.getAuthorities()) // authorities: permisos + ROLE_*
            .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // exp: 1 hora
            .signWith(key)                      // Firma con clave secreta
            .compact();
}
```

### Uso del Token en el Frontend

#### 1. **Login y Almacenamiento**

```javascript
// Frontend/js/auth.js
async function login() {
  const response = await fetch("http://localhost:8080/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ documento: "123456789", password: "password123" })
  });
  
  const token = await response.text(); // El backend devuelve solo el token como string
  
  // Almacenar en localStorage
  localStorage.setItem("jwt", token);
  localStorage.setItem("userDocumento", "123456789");
}
```

#### 2. **Inclusión en Peticiones**

```javascript
// Frontend/js/common.js
async function apiFetch(path, options = {}) {
  const token = localStorage.getItem("jwt");
  
  const headers = new Headers(options.headers || {});
  headers.set("Content-Type", "application/json");
  
  if (token) {
    headers.set("Authorization", "Bearer " + token);
  }
  
  const response = await fetch(API_URL + path, {
    ...options,
    headers
  });
  
  return response.json();
}
```

**Ejemplo de petición con token**:
```javascript
// Obtener lista de productos
const productos = await apiFetch("/productos", {
  method: "GET"
});

// Headers enviados:
// Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
// Content-Type: application/json
```

#### 3. **Extracción de Información del Token**

```javascript
// Frontend/js/common.js
function parseJwt(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split('')
      .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
      .join('')
  );
  return JSON.parse(jsonPayload);
}

function getCurrentUser() {
  const token = getToken();
  if (!token) return null;
  
  const payload = parseJwt(token);
  return {
    documento: payload.sub,        // "123456789"
    role: payload.role,             // "ADMIN"
    permissions: payload.permissions, // ["READ_USERS", ...]
    authorities: payload.authorities // ["READ_USERS", ..., "ROLE_ADMIN"]
  };
}
```

### Validación del Token en el Backend

#### 1. **JwtFilter (Filtro de Seguridad)**

```java
@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) {
        String authHeader = req.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                String documento = jwtUtil.getDocumento(token);
                String role = jwtUtil.getRole(token);
                List<String> authorities = jwtUtil.getAuthorities(token);
                
                // Crear autenticación para Spring Security
                Authentication auth = new UsernamePasswordAuthenticationToken(
                    documento,
                    null,
                    authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
                );
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // Token inválido
            }
        }
        
        chain.doFilter(req, res);
    }
}
```

#### 2. **Configuración de Seguridad**

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/bodegas/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/bodegas/**").hasRole("ADMIN")
                .requestMatchers("/movimientoInventario/**")
                    .hasAnyAuthority("READ_MOVIMIENTO_INVENTARIO", "ROLE_ADMIN")
                // ... más reglas
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### Ejemplo de Uso Completo

#### **Escenario: Usuario ADMIN crea una bodega**

1. **Login**:
   ```http
   POST /auth/login
   Content-Type: application/json
   
   {
     "documento": "123456789",
     "password": "password123"
   }
   ```
   
   **Response**:
   ```
   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkiLCJyb2xlIjoiQURNSU4i...
   ```

2. **Crear Bodega** (con token):
   ```http
   POST /bodegas
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   Content-Type: application/json
   
   {
     "nombre": "Bodega Central",
     "ubicacion": "Calle Principal 123",
     "capacidad": 1000,
     "encargado": { "id": 1 }
   }
   ```

3. **Validación en el Backend**:
   - `JwtFilter` extrae el token del header `Authorization`
   - Valida la firma y expiración
   - Extrae `documento`, `role` y `authorities`
   - Establece la autenticación en `SecurityContext`
   - `SecurityConfig` verifica que el usuario tenga `ROLE_ADMIN`
   - Si es válido, permite el acceso al endpoint

### Información Contenida en el Token

| Campo | Descripción | Ejemplo |
|-------|-------------|---------|
| `sub` | Subject (documento del usuario) | `"123456789"` |
| `role` | Rol del usuario | `"ADMIN"` o `"USER"` |
| `permissions` | Lista de permisos específicos | `["READ_USERS", "CREATE_USERS", ...]` |
| `authorities` | Permisos + rol con prefijo `ROLE_` | `["READ_USERS", ..., "ROLE_ADMIN"]` |
| `exp` | Fecha de expiración (timestamp Unix) | `1704110400` |
| `iat` | Fecha de emisión (timestamp Unix) | `1704106800` |

### Seguridad del Token

- **Algoritmo**: HS256 (HMAC con SHA-256)
- **Expiración**: 1 hora (3,600,000 ms)
- **Clave secreta**: Generada automáticamente al iniciar la aplicación
- **Validación**: 
  - Firma criptográfica
  - Expiración temporal
  - Integridad del contenido

### Buenas Prácticas Implementadas

1. ✅ **Almacenamiento seguro**: Token en `localStorage` (frontend)
2. ✅ **Transmisión segura**: Token en header `Authorization: Bearer <token>`
3. ✅ **Validación en cada petición**: `JwtFilter` valida en cada request
4. ✅ **Expiración automática**: Token expira después de 1 hora
5. ✅ **Stateless**: No se almacenan sesiones en el servidor
6. ✅ **Roles y permisos**: Sistema granular de autorización

---

## 📝 Notas Adicionales

### Tecnologías Utilizadas

- **Backend**: Spring Boot 3.3.1, Java 17
- **Base de Datos**: PostgreSQL
- **ORM**: Hibernate/JPA
- **Seguridad**: Spring Security + JWT (jjwt 0.11.5)
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla), SweetAlert2
- **Documentación API**: Swagger/OpenAPI 3

### Características Destacadas

- ✅ Sistema de roles y permisos granular
- ✅ Auditoría automática mediante triggers de PostgreSQL
- ✅ Gestión de stock por bodega (no global)
- ✅ Validaciones de capacidad de bodegas
- ✅ Protección del usuario root y usuario logueado
- ✅ Interfaz responsive y moderna
- ✅ Manejo de errores centralizado

---

**Documento generado para el proyecto LogiTrack - Sistema de Gestión de Inventario**

