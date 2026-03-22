const API_URL = 'http://localhost:8080/api/usuarios';
const usuario = JSON.parse(localStorage.getItem("usuarioLogueado"));

// navegación
function irARegistro() { 
    window.location.href = "registro.html"; 
}

function irHome() { 
    window.location.href = "lectura_actual.html"; 
}

// login
async function login() {
    const correo = document.getElementById('user').value;
    const password = document.getElementById('pass').value;

    if (!correo || !password) {
        alert("Completa todos los campos");
        return;
    }

    try {
        const respuesta = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ correo, password })
        });

        if (!respuesta.ok) {
            alert("Credenciales incorrectas");
            return;
        }

        const data = await respuesta.json();

        if (data && data.usuario) {
            // guardar sesión
            localStorage.setItem("usuarioLogueado", JSON.stringify(data.usuario));
            localStorage.setItem("password", password); // Guardar contraseña para autenticación

            alert("Login exitoso 🎉");
            irHome();
        } else {
            alert("Credenciales incorrectas");
        }

    } catch (error) {
        console.error("Error:", error);
        alert("Error conectando con el servidor");
    }
}