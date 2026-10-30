// Carga los datos del producto en el modal para ajustar las existencias
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('inventarioModal');
    if (!modal) {
        return;
    }
    const nombre = document.getElementById('inventarioNombre');
    const id = document.getElementById('inventarioId');
    const existencias = document.getElementById('inventarioExistencias');
    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        id.value = button.getAttribute('data-id') || '';
        nombre.textContent = button.getAttribute('data-nombre') || '';
        existencias.value = button.getAttribute('data-existencias') || '';
    });
    // Deja el cursor listo para escribir la nueva cantidad
    modal.addEventListener('shown.bs.modal', function () {
        existencias.focus();
        existencias.select();
    });
});
