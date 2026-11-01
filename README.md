# Óptica Visión Clara — Backend

API REST en **Java 21 + Spring Boot 4** para la gestión de una óptica, construida con el
**patrón DAO** y **JDBC puro** (`Connection`, `PreparedStatement`, `ResultSet`) sobre **PostgreSQL**.

> Frontend (Next.js): <URL_REPO_FRONTEND>
> Informe completo: [docs/INFORME.md](docs/INFORME.md)

## Arquitectura (paquetes)

```
com.optica
├── model        Clases de negocio puras: Usuario, Cliente, Vendedor, Medicion, Aro, Luna, Accesorio, Apartado, Venta, DetalleVenta
├── dao          Interfaces (contratos): GenericDAO, ProductoDAO, AroDAO, VentaDAO, ...
├── dao.jdbc     Implementaciones JDBC: AroDAOJdbc, VentaDAOJdbc, ... + JdbcSupport
├── service      Lógica de negocio: AuthService, ClienteService, CatalogoService, InventarioService, VentaService
├── controller   Endpoints REST
├── dto          Peticiones/respuestas con validación
├── security     JWT (HS256) + control de roles con Spring Security
└── exception    Excepciones de negocio y manejador global
```

## Ejecutar en local

Requisitos: Java 21+, Docker.

```bash
docker compose up -d db          # PostgreSQL 16 en localhost:5433
./mvnw spring-boot:run           # API en http://localhost:8080
```

Flyway crea el esquema (`V1__schema.sql`) y carga datos de demostración (`V2__seed.sql`) al arrancar.

| Usuario | Contraseña | Rol |
|---|---|---|
| vendedor@optica.com | 123456 | Vendedor |
| cliente@optica.com | 123456 | Cliente |

Alternativa con todo en contenedores: `docker compose --profile full up --build`.

### Tests

```bash
./mvnw test    # unitarios (Mockito) + integración con Testcontainers (requiere Docker)
```

## Variables de entorno

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5433/optica` | URL JDBC |
| `DB_USER` / `DB_PASSWORD` | `optica` / `optica` | Credenciales |
| `JWT_SECRET` | clave de desarrollo | Mínimo 32 caracteres en producción |
| `CORS_ORIGINS` | `http://localhost:3000` | Orígenes permitidos (separados por coma) |
| `IGV_RATE` | `0.15` | Tasa del ISV (Honduras) aplicada a las ventas |
| `STOCK_UMBRAL` | `5` | Umbral de alerta de stock bajo |
| `PORT` | `8080` | Puerto HTTP (Render lo asigna) |

## Endpoints

| Método | Ruta | Rol |
|---|---|---|
| POST | `/api/auth/login`, `/api/auth/registro` | Público |
| GET | `/api/catalogo/aros?marca=&material=&forma=&precioMin=&precioMax=` | Autenticado |
| GET | `/api/catalogo/filtros` | Autenticado |
| GET | `/api/cliente/perfil`, `/api/cliente/mediciones` | Cliente |
| GET/POST/DELETE | `/api/cliente/apartados[/{id}]` | Cliente |
| GET/POST/PUT/DELETE | `/api/vendedor/{aros,lunas,accesorios}[/{id}]` | Vendedor |
| GET | `/api/vendedor/alertas-stock` | Vendedor |
| GET | `/api/vendedor/clientes?q=`, `/api/vendedor/clientes/{id}` | Vendedor |
| GET/POST | `/api/vendedor/clientes/{id}/mediciones` | Vendedor |
| GET | `/api/vendedor/clientes/{id}/apartados` | Vendedor |
| POST | `/api/vendedor/ventas/cotizar` | Vendedor |
| GET/POST | `/api/vendedor/ventas[/{id}]` | Vendedor |

## Despliegue

- **Base de datos:** Neon (PostgreSQL serverless, plan gratuito).
- **API:** Render (Web Service con Docker, plan gratuito) usando `Dockerfile` o `render.yaml`.

Pasos detallados en [docs/INFORME.md](docs/INFORME.md#7-despliegue).
