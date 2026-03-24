const API_URL = 'http://localhost:8080/api/usuarios';
const STORAGE_KEY = 'usuarioLogueado';

function irperfil() {
    window.location.href = "perfil.html";
}

document.addEventListener('DOMContentLoaded', async () => {

    const datos = localStorage.getItem(STORAGE_KEY);

    if (!datos) {
        alert("Debes iniciar sesión");
        window.location.href = "login.html";
        return;
    }

    const usuario = JSON.parse(datos);

    document.getElementById('nombre').value = usuario.nombre || '';
    document.getElementById('correo').value = usuario.correo || '';
    document.getElementById('fechaNacimiento').value = usuario.fechaNacimiento || '';
    document.getElementById('promedioLectura').value = usuario.promedioLectura || '';
    document.getElementById('generoFavorito').value = usuario.generoFavorito || '';

    document.getElementById('form-editar-perfil')
        .addEventListener('submit', async function (e) {
            e.preventDefault();

            const usuarioActualizado = {
                id: usuario.id,
                nombre: document.getElementById('nombre').value,
                correo: document.getElementById('correo').value,
                fechaNacimiento: document.getElementById('fechaNacimiento').value,
                promedioLectura: document.getElementById('promedioLectura').value,
                generoFavorito: document.getElementById('generoFavorito').value,
                password: usuario.password
            };

            try {
                const respuesta = await fetch(`${API_URL}/${usuario.id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(usuarioActualizado)
                });

                if (!respuesta.ok) {
                    alert("Error al actualizar");
                    return;
                }

                const usuarioNuevo = await respuesta.json();

                localStorage.setItem(STORAGE_KEY, JSON.stringify(usuarioNuevo));

                alert("Perfil actualizado ✅");
                irperfil();

            } catch (error) {
                console.error(error);
                alert("Error de conexión");
            }
        });
});