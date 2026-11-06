# Cosmi

**Cosmi · Tienda en línea de cosméticos y skincare**
Proyecto final del curso SC-403 Desarrollo de Aplicaciones Web y Patrones, Universidad Fidélitas.

Cosmi es una aplicación web donde los clientes compran cosméticos y productos de skincare según su tipo de piel, y donde la administración controla el catálogo, el inventario, los pedidos y las ventas.

**Problema que resuelve:** muchas tiendas pequeñas de cosméticos venden por redes sociales, con la información de los productos dispersa y el inventario llevado a mano. Además, a los clientes les cuesta escoger productos adecuados para su tipo de piel. Cosmi centraliza la venta en línea, muestra información completa de cada producto, recomienda productos según el tipo de piel y ayuda a la tienda a controlar su inventario y sus pedidos.

## Integrantes y división del trabajo


| Reyes Jiménez Britzel Valeria | britzelreyes12-hash | Planteamiento del proyecto (formato IEEE) | HU6, HU7, HU8, HU15, HU18: carrito, pedido, productos con imagen y estados de pedido |
| Castro Sánchez Geovanna Jazmín | Geo2030 | 20 historias de usuario, criterios de aceptación y priorización del backlog | HU16: CRUD de categorías |
| Rodríguez Marchena Camila María | Cam1409g | Prototipo en Figma, mapa de navegación, paleta y moodboard | HU3, HU4, HU5: catálogo, búsqueda, detalle y filtros; navbar, footer y estilos del prototipo |
| Fallas Fallas María Susana | susi28fallas-cyber | Modelo de datos, viabilidad técnica, repositorio y README | HU1, HU17: registro, inicio de sesión e inventario; base del proyecto y script de base de datos |

Cada integrante trabaja en su propia rama (`feature/<nombre>`). Los cambios llegan a `main` por pull request revisado por otra integrante.

## Roles del sistema

- **Visitante:** ve el catálogo, busca, filtra y se registra.
- **Cliente:** compra y da seguimiento a sus pedidos. En el Avance 3: favoritos, reseñas, historial y recomendaciones según su tipo de piel.
- **Administrador:** gestiona productos, categorías, inventario y pedidos. En el Avance 3: usuarios y reportes.

## Tecnologías

- Java 21 con Spring Boot 4.1.1, Lombok y Maven
- Thymeleaf + Bootstrap, Font Awesome y jQuery (webjars)
- Estilos propios del prototipo en `static/css/cosmi.css`, sobre Bootstrap
- Spring Data JPA con MySQL 8 (las tablas las crea `creaTablas.sql`)
- Textos centralizados en `messages.properties`
- Firebase Storage para las imágenes de los productos (HU15)
- Transacciones (`@Transactional`) al realizar y cancelar pedidos
- Seguridad: por ahora el inicio de sesión usa la sesión HTTP, y `AdminInterceptor` devuelve 403 a quien no es ADMIN en `/admin/**`. Se cambiará por Spring Security cuando se vea en el curso.

## Avance 2: implementación y funciones

Se implementan **11 de las 20 historias (55%)**, incluidas **las 10 de prioridad alta**.

| Historia | Funcionalidad | Integrante | Estado |
| HU1 | Registro e inicio de sesión | Susana | Implementada |
| HU3 | Catálogo y búsqueda por nombre y categoría | Camila | Implementada |
| HU4 | Detalle del producto (con "Agotado") | Camila | Implementada |
| HU5 | Filtros combinados (categoría, tipo de piel y precio) | Camila | Implementada |
| HU6 | Agregar productos al carrito | Britzel | Implementada |
| HU7 | Ver y modificar el carrito | Britzel | Implementada |
| HU8 | Realizar pedido con rebajo de existencias | Britzel | Implementada |
| HU15 | Administración de productos con imagen (JPG/PNG, máx. 2 MB) | Britzel | Implementada |
| HU16 | Administración de categorías | Geovanna | Implementada |
| HU17 | Inventario con alerta de existencias bajas | Susana | Implementada |
| HU18 | Estados del pedido y cancelación con devolución de existencias | Britzel | Implementada |

**Pendiente para el Avance 3:** HU2, HU9, HU10, HU11, HU12, HU13, HU14, HU19 y HU20.

## Cómo ejecutar

**Requisitos:** JDK 21 o superior, MySQL 8 (local o en Aiven) y NetBeans (o un IDE con Maven).

1. Clonar el repositorio (rama `main`):

       git clone https://github.com/Cam1409g/cosmi.git

2. Ejecutar `src/main/resources/creaTablas.sql` con un usuario administrador de MySQL. Crea la base `cosmi`, el usuario `usuario_cosmi`, las tablas y los datos de prueba.
3. Revisar en `application.properties` la URL de la base de datos y la clave de `usuario_cosmi`.
4. Colocar la llave de Firebase en `src/main/resources/firebase/` con el nombre indicado en `firebase.json.file`. Por seguridad no está en el repositorio; se solicita al equipo.
5. Abrir el proyecto en NetBeans y ejecutarlo (Run).
6. Entrar a http://localhost:8080

### Usuarios de prueba

| Usuario | Correo | Contraseña |
|---|---|---|
| Administrador | admin@cosmi.com | admin123 |
| Cliente | cliente@cosmi.com | cliente123 |

## Enlaces

- Prototipo en Figma: Prop-Cosmi
- Video de defensa del Avance 2: pendiente

Firmado: Reyes Jiménez Britzel Valeria, Castro Sánchez Geovanna Jazmín, Rodríguez Marchena Camila María, Fallas Fallas María Susana. 5-11-2026.