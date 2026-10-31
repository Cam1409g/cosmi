// Llena el modal según se cree o se edite un producto (HU15)
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('productoModal');
    if (!modal) {
        return;
    }
    const campo = function (id) { return document.getElementById(id); };

    modal.addEventListener('show.bs.modal', function (event) {
        const boton = event.relatedTarget;
        const editando = boton && boton.getAttribute('data-id');

        campo('productoTituloNuevo').classList.toggle('d-none', !!editando);
        campo('productoTituloEditar').classList.toggle('d-none', !editando);

        campo('productoId').value = editando ? boton.getAttribute('data-id') : '';
        campo('productoNombre').value = editando ? boton.getAttribute('data-nombre') : '';
        campo('productoDescripcion').value = editando ? (boton.getAttribute('data-descripcion') || '') : '';
        campo('productoPrecio').value = editando ? boton.getAttribute('data-precio') : '';
        campo('productoExistencias').value = editando ? boton.getAttribute('data-existencias') : 0;
        campo('productoTipoPiel').value = editando ? (boton.getAttribute('data-tipopiel') || 'todas') : 'todas';
        if (editando && boton.getAttribute('data-categoria')) {
            campo('productoCategoria').value = boton.getAttribute('data-categoria');
        }
        campo('productoImagen').value = '';
        campo('productoVistaPrevia').classList.add('d-none');
    });
});

// Vista previa de la imagen, validando que sea JPG/PNG y de máximo 2 MB
function previsualizarProducto(input) {
    const vista = document.getElementById('productoVistaPrevia');
    if (!input.files || !input.files[0]) {
        vista.classList.add('d-none');
        return;
    }
    const archivo = input.files[0];
    if (archivo.type !== 'image/jpeg' && archivo.type !== 'image/png') {
        alert(input.getAttribute('data-error-tipo'));
        input.value = '';
        vista.classList.add('d-none');
        return;
    }
    if (archivo.size > 2 * 1024 * 1024) {
        alert(input.getAttribute('data-error-tamano'));
        input.value = '';
        vista.classList.add('d-none');
        return;
    }
    const lector = new FileReader();
    lector.onload = function (e) {
        vista.src = e.target.result;
        vista.classList.remove('d-none');
    };
    lector.readAsDataURL(archivo);
}
