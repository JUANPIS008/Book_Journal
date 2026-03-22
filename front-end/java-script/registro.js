const API_URL = 'http://localhost:8080/api/usuarios';

// navegación
function irlogin() { 
    window.location.href = "login.html"; 
}

function irHome() { 
    window.location.href = "perfil.html"; 
}

// registro
async function registrar() {

    const nombre = document.getElementById('nombreCompleto').value;
    const correo = document.getElementById('correo').value;
    const password = document.getElementById('passwordRegistro').value;
    const confirmar = document.getElementById('passwordConfirmar').value;
    const fechaNacimiento = document.getElementById('fechaNacimiento').value;
    const promedioLectura = document.getElementById('promedioLectura').value;
    const generoFavorito = document.getElementById('generoFavorito').value;

    // validaciones
    if (!nombre || !correo || !password || !confirmar) {
        alert("Completa los campos obligatorios");
        return;
    }

    if (password !== confirmar) {
        alert("Las contraseñas no coinciden");
        return;
    }

    const nuevoUsuario = {
        nombre,
        correo,
        password,
        fechaNacimiento,
        promedioLectura,
        generoFavorito
    };

    try {
        const respuesta = await fetch(`${API_URL}/registro`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(nuevoUsuario)
        });

        if (!respuesta.ok) {
            alert("Error al registrar");
            return;
        }

        const data = await respuesta.json();

        // guardar sesión automáticamente
        if (data && data.usuario) {
            localStorage.setItem("usuarioLogueado", JSON.stringify(data.usuario));
        } else {
            // fallback por si no hay nodo "usuario"
            localStorage.setItem("usuarioLogueado", JSON.stringify(data));
        }

        alert("Registro exitoso 🎉");
        irHome();

    } catch (error) {
        console.error("Error:", error);
        alert("Error conectando con el servidor");
    }
}