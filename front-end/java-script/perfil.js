const API_URL = 'http://localhost:8080/api/usuarios';
const STORAGE_KEY = 'usuarioLogueado';

// navegación
function irlectura_actual() { window.location.href = "lectura_actual.html"; }
function irlibros_leidos() { window.location.href = "libros_leidos.html"; }
function irlista_deseos() { window.location.href = "lista_deseos.html"; }
function irperfil() { window.location.href = "perfil.html"; }
function irlogin() { window.location.href = "login.html"; }

// cargar perfil
document.addEventListener('DOMContentLoaded', async () => {

    const datosLocal = localStorage.getItem(STORAGE_KEY);

    if (!datosLocal) {
        alert("Debes iniciar sesión");
        window.location.href = "login.html";
        return;
    }

    const usuarioLocal = JSON.parse(datosLocal);

    try {
        const respuesta = await fetch(`${API_URL}/${usuarioLocal.id}`);

        if (!respuesta.ok) throw new Error();

        const usuario = await respuesta.json();

        // actualizar localStorage
        localStorage.setItem(STORAGE_KEY, JSON.stringify(usuario));

        renderizarPerfil(usuario);

    } catch (error) {
        console.warn("Usando datos locales");
        renderizarPerfil(usuarioLocal);
    }
});

// mostrar datos
function renderizarPerfil(usuario) {
    document.querySelector('.nombrecompleto').textContent =
        `Nombre completo: ${usuario.nombre}`;

    document.querySelector('.correo').innerHTML =
        `<strong>Correo:</strong> ${usuario.correo}`;

    document.querySelector('.fechaNacimiento').innerHTML =
        `<strong>Fecha de nacimiento:</strong> ${usuario.fechaNacimiento}`;

    document.querySelector('.promedioLectura').innerHTML =
        `<strong>Promedio:</strong> ${usuario.promedioLectura} min`;

    document.querySelector('.generoFavorito').innerHTML =
        `<strong>Género favorito:</strong> ${usuario.generoFavorito}`;
}

// cerrar sesión
function cerrarSesion() {
    if (confirm("¿Cerrar sesión?")) {
        localStorage.removeItem(STORAGE_KEY);
        irlogin();
    }
}