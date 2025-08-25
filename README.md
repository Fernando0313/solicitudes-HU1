# Solicitudes - Microservicio WebFlux (Clean Architecture)

Microservicio para registrar solicitudes de préstamos con programación reactiva usando Spring WebFlux, siguiendo principios de Clean Architecture. Expone un endpoint para crear solicitudes, valida la información del cliente y del préstamo, maneja transacciones de forma reactiva, incorpora trazas de logging y un manejo unificado de errores para una experiencia predecible.

## Tabla de contenido
- Descripción general
- Arquitectura y módulos
- Requisitos
- Configuración y ejecución
- Endpoints
- Validaciones y reglas de negocio
- Manejo de errores
- Logging y observabilidad
- Seguridad y CORS
- Configuración (application.yaml)
- Pruebas

## Descripción general
- **Registrar una solicitud de préstamo**: Como cliente, puedo enviar mi solicitud (monto, plazo, email, documento, tipo de préstamo) para que sea evaluada por CrediYa.
- **Endpoint principal**: `POST /api/v1/solicitud`.
- **Estado inicial**: Las solicitudes se crean con estado "Pendiente de revisión".
- **Validaciones**: Se valida la existencia del tipo de préstamo y del cliente (vía consumidor REST externo), además de campos obligatorios.
- **Reactividad**: El flujo es completamente no bloqueante usando Spring WebFlux y R2DBC.
- **Errores controlados**: Todas las excepciones se transforman en respuestas JSON consistentes.

## Arquitectura y módulos
El proyecto sigue Clean Architecture separando capas y dependencias:

- **applications/app-service**: Ensamblaje, configuración y arranque de la app (`MainApplication`), propiedades, Swagger, CORS y cabeceras de seguridad.
- **domain/model**: Modelos de dominio y contratos (gateways) para `Application`, `LoanType` y `State`.
- **domain/usecase**: Casos de uso orquestan la lógica de aplicación (por ejemplo `ApplicationUseCase`).
- **infrastructure/driven-adapters**:
  - `r2dbc-postgresql`: Persistencia reactiva (R2DBC) para PostgreSQL, entidades y repositorios reactivos.
  - `rest-consumer`: Cliente HTTP externo (p. ej. validación del cliente por documento).
- **infrastructure/entry-points/reactive-web**: API reactiva con RouterFunctions y Handlers. Incluye DTOs, mapeos, validación, Swagger UI, CORS y manejo global de errores.

## Requisitos
- Java 17+
- Gradle (wrapper incluido)
- PostgreSQL (o H2 para desarrollo) con R2DBC

## Configuración y ejecución
1. Ajusta variables en `applications/app-service/src/main/resources/application.yaml` (puerto, base de datos, CORS, rutas, URLs de consumidores externos).
2. Asegura la base de datos y esquema. Hay un script de ejemplo en `local_enviroment/script_create_table.sql`.
3. Ejecuta la aplicación:

```bash
./gradlew clean bootRun
```

Swagger UI estará disponible en `/swagger-ui.html` y OpenAPI en `/v3/api-docs`.

## Endpoints
### Crear solicitud de préstamo
- **Método**: POST
- **Ruta**: configurada en `routes.paths.solicitud` (por defecto `/api/v1/solicitud`)
- **Handler**: `Handler.listenSaveApplication`

#### Request body
`LoanApplicationDto`
```json
{
  "amount": 15000.50,
  "term": 24,
  "email": "cliente@email.com",
  "loanTypeId": "523b3307-7d27-4165-b942-5cd7dbbc328d",
  "identityDocument": "22229850"
}
```

#### Respuesta 201 (creado)
`LoanApplicationResponseDto`
```json
{
  "amount": 15000.50,
  "term": 24,
  "email": "cliente@email.com",
  "loanTypeId": "523b3307-7d27-4165-b942-5cd7dbbc328d",
  "stateId": "3a7d0a6e-2a1e-4d7a-9a4b-b61f3f1a1f2b"
}
```

## Validaciones y reglas de negocio
- **Documento obligatorio**: Si `identityDocument` viene vacío o nulo se retorna error de negocio con código `DOCUMENT_NOT_FOUND`.
- **Cliente existente**: Se consulta un servicio externo (`RestConsumer.getUserByIdentityDocument`) para validar el cliente por documento. Si no existe, se retorna error de negocio `DOCUMENT_NOT_FOUND` con mensaje "Usuario no encontrado en API externa".
- **Tipo de préstamo válido**: El `loanTypeId` referenciado debe existir; validación a través de capa de caso de uso/repositorios.
- **Estado inicial**: Al crear la solicitud queda en "Pendiente de revisión" mediante la lógica de dominio/caso de uso.
- **Transacciones reactivas**: Persistencia con R2DBC y flujos `Mono`/`Flux` sin bloqueo.

## Manejo de errores
El manejo global se realiza con `GlobalControllerAdvice` y responde siempre JSON del tipo `ErrorResponse`:

```json
{
  "timestamp": "2025-08-24T15:20:30.123Z",
  "path": "/api/v1/solicitud",
  "status": 400,
  "error": "Bad Request",
  "requestId": "e6c1e8f0-2b6e-4c2a-a0a7-0c1d8a9b0b1c",
  "errorCode": "DOCUMENT_NOT_FOUND",
  "message": "identityDocument es obligatorio"
}
```

- **400**: Errores de negocio (`BusinessException`).
- **500**: Errores genéricos no controlados.
- Campos nulos se omiten gracias a `@JsonInclude(Include.NON_NULL)`.

## Logging y observabilidad
- Niveles configurables en `application.yaml` (por defecto `INFO` global, `DEBUG` para paquetes de negocio si se requiere).
- Logging a archivo en `logs/app.log`.
- Se recomienda trazar inicios/fin de operaciones en Handlers y UseCases; el proyecto incluye configuración base de Log4j2.

## Seguridad y CORS
- Filtro de **CORS** configurable en `cors.allowed-origins`.
- **Cabeceras de seguridad** añadidas por `SecurityHeadersConfig` (CSP, HSTS, X-Content-Type-Options, etc.).

## Configuración (application.yaml)
Ubicación: `applications/app-service/src/main/resources/application.yaml`

Parámetros relevantes:
- **server.port**: 8085
- **routes.paths.solicitud**: `/api/v1/solicitud`
- **routes.paths.solicitudById**: `/api/v1/solicitud/{id}`
- **adapters.r2dbc.***: host, puerto, credenciales y base de datos PostgreSQL
- **adapter.restconsumer.url**: URL base del consumidor externo para validar cliente
- **springdoc.swagger-ui.path**: `/swagger-ui.html`
- **logging.file.name**: `logs/app.log`

## Pruebas
- Tests unitarios y de integración en módulos `usecase` y `infrastructure`.
- Ejecutar:

```bash
./gradlew clean test
```

---

Referencias: Clean Architecture — Aislando los detalles: `https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a`
