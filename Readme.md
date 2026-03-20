# 📦 Sistema de Gestión de Pedidos - Microservicio de Productos

Este proyecto es una implementación de grado ingeniería para la asignatura de Desarrollo de Software en **Duoc UC**. Se trata de un microservicio de alta disponibilidad para la gestión de productos, integrado con **Oracle Cloud Infrastructure (OCI)** y diseñado bajo estándares de arquitectura limpia, validación robusta y seguridad por roles.

## 🚀 Especificaciones Técnicas
* **Lenguaje:** Java 21 (LTS)
* **Framework:** Spring Boot 3.5.12
* **Base de Datos:** Oracle Autonomous Database (Cloud)
* **Gestión de Dependencias:** Maven
* **Puerto de Servicio:** 8081

## 🛠️ Características Principales

### 1. Persistencia en la Nube
El microservicio utiliza **Oracle Wallet** y dependencias de seguridad específicas (`oraclepki`, `osdt_cert`) para una conexión cifrada con la base de datos en la nube. El esquema se genera automáticamente mediante JPA con nombres de columna estandarizados como `ID_PRODUCTO`.

### 2. Validaciones de Negocio (Nivel Ingeniería)
* **Moneda Nacional (CLP):** El campo `precio` se maneja como un entero (`Long`) para evitar errores de precisión decimal y ajustarse a la realidad del mercado chileno.
* **Integridad de Nombres:** Implementación de lógica `existsByNombre` para evitar la duplicidad de productos en el catálogo.
* **Restricciones de Texto:** La descripción cuenta con un rango obligatorio de 10 a 200 caracteres para asegurar la calidad de la información.

### 3. Seguridad y Control de Acceso (RBAC)
Se simula un control de acceso mediante parámetros de consulta:
* **ADMIN:** Tiene permisos para realizar operaciones de escritura, edición y eliminación.
* **CLIENTE:** Restringido a consultas de solo lectura.

## 📑 API Endpoints y Códigos de Respuesta

| Método | Endpoint | Descripción | Código Éxito |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/productos` | Obtiene el catálogo completo | 200 OK |
| **GET** | `/api/productos/{id}` | Busca un producto por su ID único | 200 OK |
| **POST** | `/api/productos` | Crea un producto (Requiere `?rol=ADMIN`) | 201 Created |
| **DELETE**| `/api/productos/{id}`| Elimina un producto (Requiere `?rol=ADMIN`)| 204 No Content |

### Manejo de Errores Personalizado
El sistema utiliza un `GlobalExceptionHandler` para devolver respuestas estandarizadas:
* **400 Bad Request:** Fallo en las validaciones de campos (`@NotBlank`, `@Min`, etc.).
* **403 Forbidden:** Intento de realizar acciones administrativas con rol de CLIENTE.
* **404 Not Found:** El recurso solicitado no existe en Oracle Cloud.
* **409 Conflict:** Intento de crear un producto con un nombre que ya existe.

## 🧪 Pruebas con Postman
Se incluye una colección de Postman configurada para el puerto **8081** que permite validar:
1. Carga masiva de productos (Smartphone, Laptop, Teclado).
2. Bloqueo de seguridad al intentar borrar como cliente.
3. Respuesta de conflicto al intentar duplicar nombres.
