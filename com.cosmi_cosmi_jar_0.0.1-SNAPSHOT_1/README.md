# Cosmi

**Cosmi · Tienda en línea de cosméticos y skincare**
Proyecto final del curso SC-403 Desarrollo de Aplicaciones Web y Patrones, Universidad Fidélitas.

Cosmi es una aplicación web donde los clientes compran cosméticos y productos de skincare según su tipo de piel, y donde la administración controla el catálogo, el inventario, los pedidos y las ventas.

**Problema que resuelve:** muchas tiendas pequeñas de cosméticos venden por redes sociales, con la información de los productos dispersa y el inventario llevado a mano. Además, a los clientes les cuesta escoger productos adecuados para su tipo de piel. Cosmi centraliza la venta en línea, muestra información completa de cada producto, recomienda productos según el tipo de piel y ayuda a la tienda a controlar su inventario y sus pedidos.

## Integrantes y división del trabajo

| Integrante | Usuario de GitHub | Responsabilidad | Rama |
|---|---|---|---|
| Reyes Jiménez Britzel Valeria | britzelreyes12-hash | Planteamiento del proyecto (formato IEEE) | `feature/Britzel` |
| Castro Sánchez Geovanna Jazmín | Geo2030 | 20 historias de usuario, criterios de aceptación y priorización del backlog | `feature/Geovana` |
| Rodríguez Marchena Camila María | Cam1409g | Prototipo en Figma, mapa de navegación, paleta y moodboard | `feature/Camila` |
| Fallas Fallas María Susana | susi28fallas-cyber | Modelo de datos, viabilidad técnica, repositorio y README | `feature/Susana` |

Todas las integrantes participan en la revisión final y en el video de defensa.
Los cambios llegan a `main` por pull request revisado por otra integrante.

## Roles del sistema

- **Visitante:** ve el catálogo, busca, filtra y se registra.
- **Cliente:** compra, guarda favoritos, deja reseñas, ve sus pedidos y recibe recomendaciones según su tipo de piel.
- **Administrador:** gestiona productos, categorías, inventario, pedidos, usuarios y reportes.

## Tecnologías

- Java 21 con Spring Boot 4.1.1, Lombok y Maven
- Thymeleaf + Bootstrap, Font Awesome y jQuery (webjars)
- Spring Data JPA con MySQL 8 (las tablas las crea `creaTablas.sql`)
- Internacionalización (español e inglés) con `messages.properties`
- Firebase Storage para las imágenes de los productos (HU15)
- Transacciones (`@Transactional`) al confirmar y cancelar pedidos
- Seguridad: por ahora el login usa sesión (`AdminInterceptor` devuelve 403 a quien no es ADMIN en `/admin/**`). Se cambia por Spring Security cuando se vea en clase.

## Estructura del proyecto

```
com.cosmi
├── CosmiApplication.java      Clase principal
├── ProjectConfig.java         Idiomas (i18n), cifrado BCrypt e interceptor de /admin
├── AdminInterceptor.java      Protege /admin/** (sesión y rol ADMIN)
├── StorageConfig.java         Conexión con Firebase Storage
├── controller/                Controllers: reciben la petición y devuelven la vista
├── service/                   Services: lógica de negocio y transacciones
├── repository/                Repositories: acceso a la base de datos (Spring Data JPA)
└── model/                     Entidades JPA y enumeraciones

src/main/resources
├── creaTablas.sql             Crea la base de datos, el usuario, las tablas y los datos de prueba
├── messages*.properties       Textos en español (por defecto) e inglés
├── templates/general/         Fragmentos compartidos: head, menú superior, menú lateral, footer, toast y modal de eliminar
├── templates/<módulo>/        Una carpeta por módulo, con listado.html y fragmentos.html
└── static/js/modulos/         Un archivo JavaScript por módulo
```

### Convenciones del equipo

- Los controllers y services usan `@RequiredArgsConstructor` (Lombok) para inyectar sus dependencias.
- Los ids son `Integer` y se llaman `idProducto`, `idCategoria`, etc. Las existencias del producto se llaman `existencias`.
- Los mensajes al usuario salen de `messages.properties` con `MessageSource` y se envían con `addFlashAttribute("todoOk", ...)` o `addFlashAttribute("error", ...)`. Se muestran con el fragmento `mostrarToast`.
- Cada página incluye `general/fragmentos :: head(...)`, el menú (`header` o `sidebar`) y el `footer`.
- La llave de Firebase (`firebase-adminsdk-....json`) va en `src/main/resources/firebase/` y **no se sube a GitHub**.

## Ramas

- `main`
- `feature/Britzel`
- `feature/Geovana`
- `feature/Camila`
- `feature/Susana`

## Avance 2: implementación y funciones

Se implementan 13 de las 20 historias, incluidas las 10 de prioridad alta.

| Integrante | Historias | Estado |
|---|---|---|
| Susana | HU1 (registro y login), HU17 (inventario) | HU1 implementada, HU17 en pruebas |
| Camila | HU3, HU4, HU5 (catálogo, detalle y filtros) | En desarrollo |
| Britzel | HU6, HU7, HU8, HU15, HU18 (carrito, pedido, productos y estados) | En desarrollo |
| Geovanna | HU9, HU10, HU16 (estado, historial y categorías) | En desarrollo |

**Pendiente para el Avance 3:** HU2, HU11, HU12, HU13, HU14, HU19 y HU20.

## Cómo ejecutar

**Requisitos:** JDK 21 o superior, MySQL 8 y NetBeans (o un IDE con Maven).

1. Clonar el repositorio y cambiar a su rama:
   ```
   git clone https://github.com/Cam1409g/cosmi.git
   git checkout feature/SuNombre
   ```
2. En MySQL, ejecutar el script `src/main/resources/creaTablas.sql` con un usuario administrador. Crea la base `cosmi`, el usuario `usuario_cosmi`, las tablas y los datos de prueba.
3. Revisar que `application.properties` use la clave de ese usuario.
4. Abrir el proyecto en NetBeans y ejecutarlo (Run).
5. Entrar a http://localhost:8080

### Usuarios de prueba

| Usuario | Correo | Contraseña |
|---|---|---|
| Administrador | admin@cosmi.com | admin123 |
| Cliente | cliente@cosmi.com | cliente123 |

## Enlaces

- Prototipo en Figma: [Prop-Cosmi](https://www.figma.com/design/YTRDfeklPxLth9EvXhcFM2/Prop-Cosmi)
- Video de defensa: pendiente

Firmado: Reyes Jiménez Britzel Valeria, Castro Sánchez Geovanna Jazmín, Rodríguez Marchena Camila María, Fallas Fallas María Susana. 9-10-2026
