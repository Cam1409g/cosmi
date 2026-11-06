package com.cosmi.repository;

import com.cosmi.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    //Consulta derivada para recuperar solo los productos activos
    public List<Producto> findByActivoTrue();

    //Productos activos ordenados por existencias, los que tienen menos primero (inventario)
    public List<Producto> findByActivoTrueOrderByExistenciasAsc();

    //Consulta derivada para buscar productos activos que contengan el texto en su nombre
    public List<Producto> findByNombreContainingIgnoreCaseAndActivoTrueOrderByNombreAsc(String nombre);

    //HU5: consultas derivadas para los filtros, todas con un rango de precio ordenado ascendentemente
    public List<Producto> findByPrecioBetweenAndActivoTrueOrderByPrecioAsc(double precioInf, double precioSup);

    public List<Producto> findByCategoriaIdCategoriaAndPrecioBetweenAndActivoTrueOrderByPrecioAsc(Integer idCategoria, double precioInf, double precioSup);

    public List<Producto> findByTipoPielAndPrecioBetweenAndActivoTrueOrderByPrecioAsc(String tipoPiel, double precioInf, double precioSup);

    public List<Producto> findByCategoriaIdCategoriaAndTipoPielAndPrecioBetweenAndActivoTrueOrderByPrecioAsc(Integer idCategoria, String tipoPiel, double precioInf, double precioSup);

    //HU16: para saber si una categoría tiene productos activos
    public boolean existsByCategoriaIdCategoriaAndActivoTrue(Integer idCategoria);
}
