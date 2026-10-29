# cosmi
Cosmi · Tienda en línea de cosméticos y skincare
Proyecto final del curso SC-403 Desarrollo de Aplicaciones Web y Patrones, Universidad Fidélitas.

Cosmi es una aplicación web donde los clientes compran cosméticos y productos de skincare según su tipo de piel, y donde la administración controla el catálogo, el inventario, los pedidos y las ventas.

Integrantes y división del trabajo
REYES JIMENEZ BRITZEL VALERIA	[britzelreyes12-hash]	Planteamiento del proyecto (formato IEEE)	feature/britzel
CASTRO SANCHEZ GEOVANNA JAZMIN	[Geo2030]	20 historias de usuario, criterios de aceptación y priorización del backlog	feature/Geovanna
RODRIGUEZ MARCHENA CAMILA MARIA	[Cam1409g]	Prototipo en Figma, mapa de navegación, paleta y moodboard	feature/Camila
FALLAS FALLAS MARIA SUSANA  [susi28fallas-cyber]	Modelo de datos, viabilidad técnica, repositorio y README	feature/Susana

Todas las integrantes participan en la revisión final y en el video de defensa.
Problema que resuelve: Muchas tiendas pequeñas de cosméticos venden por redes sociales, con la información de los productos dispersa y el inventario llevado a mano. Además, a los clientes les cuesta escoger productos adecuados para su tipo de piel. Cosmi centraliza la venta en línea, muestra información completa de cada producto, recomienda productos según el tipo de piel y ayuda a la tienda a controlar su inventario y sus pedidos.

Roles del sistema
Visitante: ve el catálogo, busca, filtra y se registra.
Cliente: compra, guarda favoritos, deja reseñas, ve sus pedidos y recibe recomendaciones según su tipo de piel.
Administrador: gestiona productos, categorías, inventario, pedidos, usuarios y reportes.

Tecnologías
-Java con Spring Boot 
-Thymeleaf + Bootstrap para las vistas
-Spring Data JPA con base de datos MySQL
-Spring Security para los roles CLIENTE y ADMIN
-Transacciones (@Transactional) al confirmar y cancelar pedidos

Ramas
-main
-feature/Britzel
-feature/Geovana
-feature/Camila
-feature/Susana

Los cambios llegan a main por pull request revisado por otra integrante.

Avance2 Implementos y funciones:
Historias por integrante: Se implementan 13 de las 20 historias, incluidas las 10 de prioridad alta.

Integrante	Historias	Estado
Susana	HU1 (registro y login), HU17 (inventario)	HU1 implementada, HU17 en pruebas
Camila	HU3, HU4, HU5 (catálogo, detalle y filtros)	En desarrollo
Britzel	HU6, HU7, HU8, HU15, HU18 (carrito, pedido, productos y estados)	En desarrollo
Geovanna	HU9, HU10, HU16 (estado, historial y categorías)	En desarrollo

Pendiente para el Avance 3: HU2, HU11, HU12, HU13, HU14, HU19 y HU20.

Tecnologías actualizadas
Java 21 con Spring Boot 4.1.1
Lombok y Maven
Ya disponible en main
Entidades y repositories.
Spring Security con error 403 para clientes que entran a /admin.
Pantalla de acceso (iniciar sesión y crear cuenta) con validaciones.
Inventario del administrador con alerta de stock bajo.
Navbar, menú lateral y estilos según el prototipo.
Cómo ejecutar

Requisitos: JDK 21 o superior, MySQL 8 y NetBeans (o un IDE con Maven).

git clone https://github.com/Cam1409g/cosmi.git y cambiar a su rama (git checkout feature/SuNombre).
En MySQL: CREATE DATABASE cosmi;
En src/main/resources/application.properties, poner la clave de MySQL local (no subir ese archivo con la clave real).
Abrir el proyecto en NetBeans y ejecutarlo (Run). Las tablas y los datos de prueba se crean solos.
Entrar a http://localhost:8080
Usuario de prueba	Correo	Contraseña
Administrador	admin@cosmi.com	admin123
Cliente	cliente@cosmi.com	cliente123

Enlaces
Prototipo en Figma: Prop-Cosmi
Video de defensa: pendiente

Firmado: REYES JIMENEZ BRITZEL VALERIA
CASTRO SANCHEZ GEOVANNA JAZMIN
RODRIGUEZ MARCHENA CAMILA MARIA
FALLAS FALLAS MARIA SUSANA
9-10-2026
