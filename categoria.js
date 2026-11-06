document.addEventListener('DOMContentLoaded', function () {

    const modal = document.getElementById('categoriaModal');
    if (!modal) {
        return;
    }

    const form = document.getElementById('categoriaForm');
    const header = document.getElementById('categoriaModalHeader');
    const titulo = document.getElementById('categoriaModalTitulo');
    const id = document.getElementById('categoriaId');
    const nombre = document.getElementById('categoriaNombre');
    const descripcion = document.getElementById('categoriaDescripcion');
    const activo = document.getElementById('categoriaActivo');

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }

        const modo = button.getAttribute('data-modo');

        form.reset();
        id.value = '';

        titulo.textContent = button.getAttribute('data-titulo') || '';

        header.classList.remove('bg-info', 'bg-warning', 'text-white');

        if (modo === 'editar') {

            id.value = button.getAttribute('data-id') || '';
            nombre.value = button.getAttribute('data-nombre') || '';
            descripcion.value = button.getAttribute('data-descripcion') || '';
            activo.checked = button.getAttribute('data-activo') === 'true';

            header.classList.add('bg-warning');

        } else {

            activo.checked = true;

            header.classList.add('bg-info', 'text-white');
        }
    });
});


    