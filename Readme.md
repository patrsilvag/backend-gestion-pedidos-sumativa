# 📦 Sistema de Gestión de Pedidos - Microservicio de Productos

Este proyecto es una implementación de grado ingeniería para la asignatura de Desarrollo de Software en **Duoc UC** Se trata de un microservicio de alta disponibilidad para la gestión de productos, integrado con **Oracle Cloud Infrastructure (OCI)** y diseñado bajo estándares de arquitectura limpia, validación robusta y simulación de procesos de negocio

## 🚀 Especificaciones Técnicas
* **Lenguaje:** Java 21 (LTS)
* **Framework:** Spring Boot 3.5.12
* **Base de Datos:** Oracle Autonomous Database (Cloud)
* **Gestión de Dependencias:** Maven
* **Puerto de Servicio:** 8081

## 🛠️ Características Principales

### 1. Persistencia en la Nube
El microservicio utiliza **Oracle Wallet** para una conexión cifrada con la base de datos en la nube. El esquema se genera automáticamente mediante JPA con nombres de columna estandarizados como `ID_PRODUCTO`.

### 2. Lógica de Compra y Stock (Requerimientos #31 y #27)
Se implementó un flujo de compra que simula una transacción real:
* **Validación de Inventario:** El sistema verifica el stock disponible en **Oracle Cloud** antes de procesar cualquier pedido.
***Simulación de Pago:** Al realizar una compra, el sistema devuelve una confirmación de éxito ("Pago realizado con éxito") sin necesidad de integrar pasarelas externas como WebPay, cumpliendo con la pauta técnica.
* **Actualización Automática:** Cada compra exitosa descuenta automáticamente las unidades del inventario global.

### 3. Seguridad y Control de Acceso (RBAC)
Control de acceso basado en roles mediante parámetros de consulta:
***ADMIN:** Permisos totales para gestión (Crear, Editar, Eliminar productos).
***CLIENTE:** Permisos para búsqueda, visualización y ejecución de compras.

## 📑 API Endpoints y Códigos de Respuesta

| Método | Endpoint | Descripción | Código Éxito |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/productos` | Obtiene el catálogo completo (5 productos mín.) | 200 OK |
| **GET** | `/api/productos/{id}` | Busca un producto por su ID único | 200 OK |
| **POST** | `/api/productos/{id}/comprar` | **Compra**: Simula pago y descuenta stock | 200 OK |
| **POST** | `/api/productos` | Crea un producto (Requiere `?rol=ADMIN`) | 201 Created |
| **PUT** | `/api/productos/{id}` | Actualiza un producto (Requiere `?rol=ADMIN`) | 200 OK |
| **DELETE**| `/api/productos/{id}`| Elimina un producto (Requiere `?rol=ADMIN`)| 204 No Content |

### Manejo de Errores Personalizado
* **400 Bad Request:** Fallo en validaciones o stock insuficiente para la compra.
* **403 Forbidden:** Cliente intentando realizar acciones de administrador.
* **404 Not Found:** El producto no existe en el catálogo de la nube.
* **409 Conflict:** Intento de duplicar un nombre de producto existente.

## 🧪 Pruebas con Postman
La colección de Postman para el puerto **8081** permite validar el ciclo de vida del producto:
1. **Carga Inicial:** Creación de los **5 productos** requeridos por el caso asignado.
2. **Flujo de Venta:** Simulación de compra exitosa con reducción de stock en tiempo real.
3. **Validación de Negocio:** Error controlado al intentar comprar más unidades de las disponibles.
4. **Seguridad:** Bloqueo de operaciones administrativas para el rol `CLIENTE`.