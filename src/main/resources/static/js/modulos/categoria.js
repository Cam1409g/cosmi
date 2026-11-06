// Pasa los datos de la categoría al formulario de la derecha para editarla
document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('categoriaForm');
    if (!form) {
        return;
    }
    const titulo = document.getElementById('categoriaFormTitulo');
    const id = document.getElementById('categoriaId');
    const nombre = document.getElementById('categoriaNombre');
    const descripcion = document.getElementById('categoriaDescripcion');
    const cancelar = document.getElementById('categoriaCancelar');

    document.querySelectorAll('.btn-editar').forEach(function (boton) {
        boton.addEventListener('click', function () {
            id.value = boton.getAttribute('data-id');
            nombre.value = boton.getAttribute('data-nombre');
            descripcion.value = boton.getAttribute('data-descripcion') || '';
            titulo.textContent = titulo.getAttribute('data-editar');
            cancelar.classList.remove('d-none');
            nombre.focus();
        });
    });

    cancelar.addEventListener('click', function () {
        form.reset();
        id.value = '';
        titulo.textContent = titulo.getAttribute('data-nueva');
        cancelar.classList.add('d-none');
    });
});

    