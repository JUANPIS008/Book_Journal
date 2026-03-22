const STORAGE_KEY = 'usuarioLogueado';

// navegación
function irlectura_actual() { window.location.href = "lectura_actual.html"; }
function irlibros_leidos() { window.location.href = "libros_leidos.html"; }
function irlista_deseos() { window.location.href = "lista_deseos.html"; }
function irperfil() { window.location.href = "perfil.html"; }
function irlogin() { window.location.href = "login.html"; }

// cargar perfil
document.addEventListener('DOMContentLoaded', async () => {
    const datos = localStorage.getItem(STORAGE_KEY);

    if (!datos) {
        alert("Debes iniciar sesión");
        irlogin();
        return;
    }

    const usuario = JSON.parse(datos);
    
    // Validar que usuario tenga correo
    if (!usuario || !usuario.correo) {
        alert("Datos de usuario inválidos");
        localStorage.removeItem(STORAGE_KEY);
        localStorage.removeItem("password");
        irlogin();
        return;
    }

    renderizarPerfil(usuario);
});

// mostrar datos
function renderizarPerfil(usuario) {
    const contenedor = document.getElementById('datos-perfil');

    contenedor.querySelector('.nombrecompleto').textContent =
        `Nombre completo: ${usuario.nombre || 'No definido'}`;

    contenedor.querySelector('.correo').innerHTML =
        `<strong>Correo:</strong> ${usuario.correo || 'No definido'}`;

    contenedor.querySelector('.fechaNacimiento').innerHTML =
        `<strong>Fecha de nacimiento:</strong> ${usuario.fechaNacimiento || 'No definida'}`;

    contenedor.querySelector('.promedioLectura').innerHTML =
        `<strong>Promedio de lectura:</strong> ${usuario.promedioLectura || '0'} min`;

    contenedor.querySelector('.generoFavorito').innerHTML =
        `<strong>Género favorito:</strong> ${usuario.generoFavorito || 'No definido'}`;
}

// cerrar sesión
function cerrarSesion() {
    if (confirm("¿Cerrar sesión?")) {
        localStorage.removeItem(STORAGE_KEY);
        localStorage.removeItem("password");
        irlogin();
    }
}