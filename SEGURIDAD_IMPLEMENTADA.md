# 🔒 Implementación de Seguridad y Protección de Rutas - Book Journal

## 📋 Resumen General

Se ha implementado un sistema completo de **seguridad con Spring Security** que:
- ✅ Encripta contraseñas usando BCrypt
- ✅ Protege rutas que requieren autenticación
- ✅ Permite que cada usuario solo vea sus propios libros
- ✅ Valida permisos de acceso a recursos

---

## 🔧 Componentes Implementados

### 1. **SecurityConfig.java** (Nuevo)
Ubicación: `back-end/src/main/java/com/example/back_end/config/SecurityConfig.java`

**Funcionalidades:**
- Encriptador BCrypt para contraseñas
- Configuración de rutas públicas vs protegidas
- CORS habilitado para el frontend
- Basic Authentication con Spring Security

**Rutas Públicas (sin login):**
```
POST /api/usuarios/registro          → Registrarse
POST /api/usuarios/login             → Iniciar sesión
POST /api/usuarios/verificar-registro → Verificar si existe un usuario
GET  /api/libros/**                  → Ver todos los libros (público)
```

**Rutas Protegidas (requieren autenticación):**
```
GET  /api/usuarios/**                → Obtener perfil del usuario
POST /api/libros/**                  → Crear libros
PUT  /api/libros/**                  → Actualizar libros
DELETE /api/libros/**                → Eliminar libros
GET  /api/libros/mis-libros          → Obtener tus libros
```

---

### 2. **UsuarioService.java** (Mejorado)
Ubicación: `back-end/src/main/java/com/example/back_end/service/UsuarioService.java`

**Nuevas funcionalidades:**
```java
// Inyección de PasswordEncoder (BCrypt)
private final PasswordEncoder passwordEncoder;

// Encriptar contraseña al registrar
usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

// Comparar contraseña encriptada en login
if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword()))

// Métodos de utilidad
public Usuario obtenerPorCorreo(String correo)
public Usuario verificarRegistro(String correo)
public boolean estaRegistrado(String correo)
```

**Mejoras de seguridad:**
- Las contraseñas se encriptan automáticamente
- Las contraseñas no se guardan en texto plano
- Se valida el correo durante login con contraseña encriptada

---

### 3. **UsuarioController.java** (Mejorado)
Ubicación: `back-end/src/main/java/com/example/back_end/controller/UsuarioController.java`

**Nuevos endpoints:**

#### Registro
```
POST /api/usuarios/registro
Body: { "correo": "user@example.com", "password": "123456", "nombre": "Juan" }

Response:
{
  "exito": true,
  "mensaje": "Registro exitoso",
  "usuario": { id, correo, nombre, ... }
}
```

#### Login
```
POST /api/usuarios/login
Body: { "correo": "user@example.com", "password": "123456" }

Response:
{
  "exito": true,
  "mensaje": "Login exitoso",
  "usuario": { id, correo, nombre, ... }
}
```

#### Verificar Registro
```
POST /api/usuarios/verificar-registro
Body: { "correo": "user@example.com" }

Response:
{
  "exito": true,
  "mensaje": "Usuario registrado",
  "usuario": { id, correo, ... }
}
```

#### Obtener Usuario Autenticado
```
GET /api/usuarios/me
(Requiere autenticación)

Response:
{
  "exito": true,
  "mensaje": "Usuario encontrado",
  "usuario": { id, correo, nombre, ... }
}
```

**Validaciones incluidas:**
- ✅ Correo requerido
- ✅ Contraseña requerida
- ✅ No permitir correos duplicados
- ✅ Manejo de excepciones con respuestas apropiadas
- ✅ Códigos HTTP correctos (401, 403, 400, 500)

---

### 4. **LibroController.java** (Mejorado)
Ubicación: `back-end/src/main/java/com/example/back_end/controller/LibroController.java`

**Cambios principales:**

#### Obtener Usuario Autenticado
```java
private Usuario obtenerUsuarioAutenticado() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        return null;
    }
    String correo = auth.getName();
    return usuarioService.obtenerPorCorreo(correo);
}
```

#### Crear Libro (Protegido)
```
POST /api/libros
Body: { "titulo": "...", "autor": "...", ... }

Validación: Usuario debe estar autenticado
Automático: Se asigna el usuario autenticado al libro
```

#### Actualizar Libro (Protegido)
```
PUT /api/libros/{id}
Body: { "titulo": "...", ... }

Validaciones:
- User debe estar autenticado
- User debe ser propietario del libro
- Si no es propietario → Error 403 Forbidden
```

#### Eliminar Libro (Protegido)
```
DELETE /api/libros/{id}

Validaciones:
- User debe estar autenticado
- User debe ser propietario del libro
```

#### Obtener Mis Libros (Protegido)
```
GET /api/libros/mis-libros
(Requiere autenticación)

Response:
{
  "exito": true,
  "total": 5,
  "libros": [...],
  "usuario": { id, correo, ... }
}

Nota: Devuelve SOLO los libros del usuario autenticado
```

---

## 📦 Dependencias Agregadas (pom.xml)

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Spring Web (completo) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

## 🔐 Flujo de Autenticación y Seguridad

```
┌─────────────────────────────────────────────────────┐
│  USUARIO SIN AUTENTICAR                             │
├─────────────────────────────────────────────────────┤
│  ✅ Puede registrarse                               │
│  ✅ Puede hacer login                               │
│  ✅ Puede ver todos los libros (públicos)           │
│  ❌ NO puede crear libros                           │
│  ❌ NO puede actualizar libros                      │
│  ❌ NO puede eliminar libros                        │
│  ❌ NO puede ver sus libros privados                │
└─────────────────────────────────────────────────────┘

                        ↓ LOGIN ↓

┌─────────────────────────────────────────────────────┐
│  USUARIO AUTENTICADO                                │
├─────────────────────────────────────────────────────┤
│  ✅ Puede crear libros                              │
│  ✅ Puede actualizar SOLO SUS libros               │
│  ✅ Puede eliminar SOLO SUS libros                 │
│  ✅ Puede ver SOLO SUS libros en /mis-libros       │
│  ✅ Puede ver su perfil                            │
│  ✅ Puede cerrar sesión                            │
│  ❌ NO puede ver/editar/eliminar libros de otros  │
└─────────────────────────────────────────────────────┘
```

---

## 🛡️ Seguridad a Nivel de Controlador

### Ejemplo: Protección al Actualizar Libro

```java
@PutMapping("/{id}")
public ResponseEntity<Map<String, Object>> actualizar(
    @PathVariable Long id, 
    @RequestBody Libro libro) {
    
    // 1. Obtener usuario autenticado
    Usuario usuario = obtenerUsuarioAutenticado();
    
    if (usuario == null) {
        // ❌ No está autenticado
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(crearRespuestaError("Debe autenticarse"));
    }

    // 2. Obtener el libro
    Libro libroExistente = service.obtener(id);
    
    if (libroExistente == null) {
        // ❌ Libro no existe
        return ResponseEntity.notFound().build();
    }

    // 3. Verificar que es propietario
    if (!libroExistente.getUsuario().getId().equals(usuario.getId())) {
        // ❌ No es el propietario
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(crearRespuestaError("No tienes permiso para actualizar este libro"));
    }

    // ✅ Todo OK - actualizar libro
    libro.setUsuario(usuario);
    Libro libroActualizado = service.actualizar(id, libro);
    return ResponseEntity.ok(crearRespuesta("Actualizado con éxito", libroActualizado));
}
```

---

## 📱 Archivos Modificados

### Backend
- ✅ `pom.xml` - Agregadas dependencias de Spring Security
- ✅ `UsuarioService.java` - Encriptación de contraseñas
- ✅ `UsuarioController.java` - Endpoints mejorados
- ✅ `LibroController.java` - Protección de rutas
- ✅ `SecurityConfig.java` - **NUEVO** - Configuración de seguridad

### Frontend (Recomendado actualizar)
Los archivos JavaScript del frontend deben:
- Guardar el token/usuario después del login
- Enviar credenciales en cada request (fetch con Authorization header)
- Redirigir a login si recibe error 401 (No autenticado)
- Redirigir a login si recibe error 403 (Prohibido)

---

## 🧪 Pruebas Recomendadas

### 1. Registro
- [ ] Registrar usuario nuevo con correo y contraseña
- [ ] Verificar que contraseña está encriptada en base de datos
- [ ] Intentar registrar con correo duplicado → Error 400

### 2. Login
- [ ] Hacer login con credenciales correctas → Éxito
- [ ] Hacer login con contraseña incorrecta → Error 401
- [ ] Hacer login con usuario inexistente → Error 401

### 3. Crear Libro Sin Autenticar
- [ ] POST /api/libros sin autenticación → Error 401
- [ ] POST /api/libros con user autenticado → Éxito + Libro asociado al user

### 4. Libros de Otros Usuarios
- [ ] User A crea libro
- [ ] User B intenta actualizar libro de User A → Error 403
- [ ] User B intenta eliminar libro de User A → Error 403

### 5. Ver Mis Libros
- [ ] User A accede a /api/libros/mis-libros → Ve SOLO sus libros
- [ ] User B accede a /api/libros/mis-libros → Ve SOLO sus libros (diferentes)

---

## 🚀 Próximos Pasos (Opcional)

### Para mayor seguridad en producción:
1. ✅ Implementar JWT (JSON Web Tokens) en lugar de Basic Auth
2. ✅ Agregar refresh tokens
3. ✅ Implementar HTTPS
4. ✅ Agregar rate limiting en login
5. ✅ Validar email antes de registrar
6. ✅ Implementar recuperación de contraseña

### Para el frontend:
1. ✅ Guardar JWT después del login
2. ✅ Enviar JWT en headers de cada request autenticado
3. ✅ Interceptar errores 401 y redirigir a login
4. ✅ Mostrar feedback visual cuando no hay permiso (403)

---

## 📚 Endpoints Completos

### Usuarios
| Método | Endpoint | Público | Descripción |
|--------|----------|---------|-------------|
| POST | `/api/usuarios/registro` | ✅ | Registrar nuevo usuario |
| POST | `/api/usuarios/login` | ✅ | Iniciar sesión |
| POST | `/api/usuarios/verificar-registro` | ✅ | Verificar si existe usuario |
| GET | `/api/usuarios/{id}` | ❌ | Obtener perfil por ID |
| GET | `/api/usuarios/por-correo/{correo}` | ❌ | Obtener usuario por correo |
| GET | `/api/usuarios/me` | ❌ | Obtener usuario autenticado |

### Libros
| Método | Endpoint | Público | Descripción |
|--------|----------|---------|-------------|
| GET | `/api/libros` | ✅ | Listar todos los libros |
| GET | `/api/libros/{id}` | ✅ | Obtener libro específico |
| GET | `/api/libros/leidos` | ✅ | Obtener libros leídos |
| GET | `/api/libros/mis-libros` | ❌ | Obtener tus libros |
| GET | `/api/libros/usuario/{usuarioId}` | ❌ | Obtener libros de un usuario |
| POST | `/api/libros` | ❌ | Crear nuevo libro |
| PUT | `/api/libros/{id}` | ❌ | Actualizar libro (solo propietario) |
| DELETE | `/api/libros/{id}` | ❌ | Eliminar libro (solo propietario) |

---

## ✨ Características Principales

✅ **Encriptación de Contraseñas** - BCrypt automático
✅ **Autenticación** - Spring Security básica
✅ **Autorización** - Control de acceso por recurso
✅ **Validaciones** - Campos requeridos y duplicados
✅ **Códigos HTTP** - 200, 201, 400, 401, 403, 404, 500
✅ **Respuestas Consistentes** - JSON con exito/mensaje/datos
✅ **Multi-usuario** - Cada usuario solo ve sus datos
✅ **Protección de Rutas** - Endpoints públicos vs privados

---

Generated: 2026-03-22
