# Cosmi

**Tienda en línea de cosméticos y skincare**

Proyecto final del curso SC-403 Desarrollo de Aplicaciones Web y Patrones, Universidad Fidélitas.

Cosmi es una aplicación web donde los clientes compran cosméticos y productos de skincare según su tipo de piel, y donde la administración controla el catálogo, el inventario, los pedidos y las ventas.

## Problema que resuelve

Muchas tiendas pequeñas de cosméticos venden por redes sociales, con la información de los productos dispersa y el inventario llevado a mano. Además, a los clientes les cuesta escoger productos adecuados para su tipo de piel. Cosmi centraliza la venta en línea, muestra información completa de cada producto, recomienda productos según el tipo de piel y ayuda a la tienda a controlar su inventario y sus pedidos.

## Integrantes y división del trabajo

| Integrante | Usuario GitHub | Responsabilidad | Rama |
|---|---|---|---|
| Reyes Jiménez Britzel Valeria | britzelreyes12-hash | Planteamiento del proyecto (formato IEEE) | `feature/britzel` |
| Castro Sánchez Geovanna Jazmín | Geo2030 | 20 historias de usuario, criterios de aceptación y priorización del backlog | `feature/Geovanna` |
| Rodríguez Marchena Camila María | Cam1409g | Prototipo en Figma, mapa de navegación, paleta y moodboard | `feature/Camila` |
| Fallas Fallas María Susana | susi28fallas-cyber | Modelo de datos, viabilidad técnica, repositorio y README | `feature/Susana` |

Todas las integrantes participan en la revisión final y en el video de defensa. Los cambios llegan a `main` por pull request revisado por otra integrante.

## Roles del sistema

| Rol | Qué puede hacer |
|---|---|
| Visitante | Ver el catálogo, buscar, filtrar y registrarse. |
| Cliente | Comprar, guardar favoritos, dejar reseñas, ver sus pedidos y recibir recomendaciones según su tipo de piel. |
| Administrador | Gestionar productos, categorías, inventario, pedidos, usuarios y reportes. Si un cliente intenta entrar a `/admin`, el sistema responde con un error 403. |

## Tecnologías

- Java 21 con Spring Boot 4.1.1 y Maven
- Thymeleaf y Bootstrap (por webjars, junto con Font Awesome y jQuery) para las vistas
- Spring Data JPA con base de datos MySQL 8
- Lombok (`@RequiredArgsConstructor`) y validaciones con Bean Validation
- BCrypt (`spring-security-crypto`) para cifrar contraseñas. El inicio de sesión usa la sesión HTTP y un interceptor (`AdminInterceptor`) protege `/admin/**`. Se cambiará por Spring Security cuando se vea en clase.
- Firebase Storage para las imágenes de los productos
- Mensajes en español e inglés (también hay francés y portugués) con `messages_*.properties`
- `@Transactional` en los services

## Avance 2: implementado y pendiente

| Integrante | Historias | Estado |
|---|---|---|
| Susana | HU1 (registro y login), HU17 (inventario con alerta de existencias bajas), 403 para clientes en `/admin` | Implementadas |
| Camila | HU3, HU4, HU5 (catálogo, detalle y filtros) | En desarrollo |
| Britzel | HU6, HU7 (carrito y pedido) | En desarrollo |
| Geovanna | HU9, HU10 (estado e historial de pedidos) | En desarrollo |

Pendiente para el Avance 3: HU2, HU11, HU12, HU13, HU14, HU19 y HU20.

### Implementado por Susana (rama `feature/Susana`)

- Entidades, repositories y script `creaTablas.sql` con las tablas y los datos de prueba.
- Pantalla de acceso (iniciar sesión y crear cuenta) con validaciones: correo repetido, campos vacíos y credenciales incorrectas.
- Cifrado de contraseñas con BCrypt y sesión de usuario.
- Protección de `/admin/**`: sin sesión va al login y, si no es administrador, muestra el 403.
- Inventario del administrador: lista de productos con alerta de existencias bajas (5 unidades o menos), productos agotados y ajuste de existencias.
- Menú superior, menú lateral del administrador, estilos y cambio de idioma.

## Modelo de datos

Siete entidades:

| Entidad | Descripción | Relaciones |
|---|---|---|
| `usuario` | Datos de la persona, tipo de piel y rol | Un carrito; muchos pedidos |
| `categoria` | Agrupa los productos | Muchos productos |
| `producto` | Nombre, precio, existencias, imagen y tipo de piel | Una categoría |
| `carrito` | Carrito activo del usuario | Un usuario; muchos detalles |
| `detalle_carrito` | Producto y cantidad dentro del carrito | Un carrito; un producto |
| `pedido` | Compra con fecha, total y estado | Un usuario; muchos detalles |
| `detalle_pedido` | Producto, cantidad y precio unitario del pedido | Un pedido; un producto |

Estados de un pedido: `PENDIENTE`, `EN_PREPARACION`, `EN_CAMINO`, `ENTREGADO` y `CANCELADO`.

## Estructura del proyecto

El proyecto está organizado en capas, igual que el proyecto de ejemplo del curso:

```
com.cosmi
├── CosmiApplication.java     Clase principal
├── ProjectConfig.java        Idiomas (i18n), cifrado BCrypt e interceptor de /admin
├── AdminInterceptor.java     Protege /admin/** (rol ADMIN) y responde 403
├── StorageConfig.java        Conexión con Firebase Storage
├── controller/               Reciben la petición y devuelven la vista
├── service/                  Lógica de negocio y transacciones
├── repository/               Acceso a la base de datos (Spring Data JPA)
└── model/                    Entidades y enumeraciones

src/main/resources
├── creaTablas.sql            Crea la base de datos, el usuario, las tablas y los datos de prueba
├── messages*.properties      Textos de la aplicación por idioma
├── templates/general/        Fragmentos compartidos (head, menú, mensajes) y menú lateral del admin
├── templates/<módulo>/       Una carpeta por módulo (listado y fragmentos)
└── static/                   Estilos (css) y un archivo JavaScript por módulo
```

### Convenciones del equipo

- Los controllers y services usan `@RequiredArgsConstructor` para inyectar sus dependencias.
- Los ids son `Integer` (`idProducto`, `idCategoria`, etc.) y las existencias del producto se llaman `existencias`.
- Los mensajes salen de `messages.properties` con `MessageSource` y se envían con `addFlashAttribute("todoOk", ...)` o `addFlashAttribute("error", ...)`.
- La llave de Firebase (`firebase-adminsdk-....json`) va en `src/main/resources/firebase/` y **no se sube a GitHub**.

## Cómo ejecutar

**Requisitos:** JDK 21 o superior, MySQL 8 y NetBeans (o un IDE con Maven).

1. Clonar el repositorio y cambiar a la rama de trabajo:
   ```
   git clone https://github.com/Cam1409g/cosmi.git
   git checkout feature/Susana
   ```
2. En MySQL, ejecutar el script `src/main/resources/creaTablas.sql` con un usuario administrador. Crea la base `cosmi`, el usuario de la aplicación, las tablas y los datos de prueba.
3. Revisar que `application.properties` tenga el mismo usuario y clave que crea el script.
4. Abrir el proyecto en NetBeans y ejecutarlo (Run).
5. Entrar a http://localhost:8080

### Usuarios de prueba

| Usuario | Correo | Contraseña |
|---|---|---|
| Administrador | admin@cosmi.com | admin123 |
| Cliente | cliente@cosmi.com | cliente123 |

### Cómo probar lo implementado

- **HU1:** crear una cuenta en `/registro` y luego iniciar sesión en `/login`.
- **403:** iniciar sesión como cliente y abrir `/admin/inventario`.
- **HU17:** iniciar sesión como administrador, abrir **Administración** y ajustar las existencias de un producto.

## Enlaces

- Prototipo en Figma: [Prop-Cosmi](https://www.figma.com/design/YTRDfeklPxLth9EvXhcFM2/Prop-Cosmi)
- Video de defensa: pendiente

Firmado: Reyes Jiménez Britzel Valeria, Castro Sánchez Geovanna Jazmín, Rodríguez Marchena Camila María, Fallas Fallas María Susana. 9-10-2026
