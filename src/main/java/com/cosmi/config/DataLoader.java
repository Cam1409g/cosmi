package com.cosmi.config;

import com.cosmi.domain.Categoria;
import com.cosmi.domain.Producto;
import com.cosmi.domain.Rol;
import com.cosmi.domain.Usuario;
import com.cosmi.repository.CategoriaRepository;
import com.cosmi.repository.ProductoRepository;
import com.cosmi.repository.UsuarioRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Carga usuarios, categorías y productos de prueba la primera vez que se ejecuta. */
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final CategoriaRepository categorias;
    private final ProductoRepository productos;
    private final PasswordEncoder encoder;

    public DataLoader(UsuarioRepository usuarios, CategoriaRepository categorias,
                      ProductoRepository productos, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.categorias = categorias;
        this.productos = productos;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) return;

        crearUsuario("Admin Cosmi", "admin@cosmi.com", "admin123", Rol.ADMIN);
        crearUsuario("Cliente Prueba", "cliente@cosmi.com", "cliente123", Rol.CLIENTE);

        Categoria maquillaje = crearCategoria("Maquillaje", "Bases, labiales y sombras");
        Categoria skincare = crearCategoria("Skincare", "Cuidado facial");
        Categoria cabello = crearCategoria("Cabello", "Shampoo y tratamientos");
        Categoria corporal = crearCategoria("Cuidado corporal", "Cremas y exfoliantes");

        crearProducto("Limpiador facial gel", "Limpieza suave para piel grasa", "8500", 20, "grasa", skincare);
        crearProducto("Crema hidratante", "Hidratación intensa para piel seca", "12500", 15, "seca", skincare);
        crearProducto("Sérum de vitamina C", "Ilumina y unifica el tono", "18900", 4, "todas", skincare);   // stock bajo
        crearProducto("Protector solar FPS 50", "Uso diario, textura ligera", "14200", 30, "todas", skincare);
        crearProducto("Tónico calmante", "Para piel sensible", "9800", 0, "sensible", skincare);            // agotado
        crearProducto("Base líquida mate", "Cobertura media", "11500", 12, "mixta", maquillaje);
        crearProducto("Labial rojo clásico", "Acabado cremoso", "7500", 25, "todas", maquillaje);
        crearProducto("Máscara de pestañas", "Volumen y definición", "6900", 5, "todas", maquillaje);       // stock bajo
        crearProducto("Shampoo nutritivo", "Para cabello seco", "6500", 18, "todas", cabello);
        crearProducto("Crema corporal de karité", "Piel suave por 24 horas", "10900", 10, "normal", corporal);
    }

    private void crearUsuario(String nombre, String correo, String clave, Rol rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setContrasena(encoder.encode(clave));
        u.setRol(rol);
        usuarios.save(u);
    }

    private Categoria crearCategoria(String nombre, String descripcion) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        return categorias.save(c);
    }

    private void crearProducto(String nombre, String descripcion, String precio, int stock,
                               String tipoPiel, Categoria categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(new BigDecimal(precio));
        p.setStock(stock);
        p.setTipoPiel(tipoPiel);
        p.setCategoria(categoria);
        productos.save(p);
    }
}
