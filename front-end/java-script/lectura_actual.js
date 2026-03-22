const API_URL = 'http://localhost:8080/api/libros';

async function Guardar_libro() {

    const usuarioData = localStorage.getItem("usuarioLogueado");
    const password = localStorage.getItem("password");

    if (!usuarioData || !password) {
        alert("Debes iniciar sesión para guardar un libro.");
        window.location.href = "login.html";
        return;
    }

    let usuario = JSON.parse(usuarioData);
    // Si hay envoltorio desde la API: { exito, mensaje, usuario }
    if (usuario && usuario.usuario) {
        usuario = usuario.usuario;
    }

    if (!usuario || !usuario.correo) {
        console.error("Usuario inválido en localStorage:", usuario);
        alert("No se encontró un usuario válido. Por favor inicia sesión de nuevo.");
        localStorage.removeItem("usuarioLogueado");
        localStorage.removeItem("password");
        window.location.href = "login.html";
        return;
    }

    const nuevoLibro = {
        titulo: document.getElementById('titulo').value,
        autor: document.getElementById('autor').value,
        genero: document.getElementById('genero').value,
        resena: document.getElementById('resena').value,
        inicio: document.getElementById('inicio').value,
        fin: document.getElementById('final').value,
        calificacion: parseInt(document.getElementById('calificacion').value) || 0
    };

    if (nuevoLibro.titulo.trim() === "") {
        alert("Por favor, ingresa al menos el título del libro.");
        return;
    }

    // 🔥 SOLO API (puedes quitar localStorage si quieres)
    const authHeader = "Basic " + btoa(usuario.correo + ":" + password);

    try {
        const respuesta = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify(nuevoLibro)
        });

        if (respuesta.ok) {
            alert("Libro guardado correctamente en la API");
            window.location.href = "libros_leidos.html";
        } else if (respuesta.status === 401) {
            alert("No autorizado: tus credenciales no son válidas. Inicia sesión de nuevo.");
            localStorage.removeItem("usuarioLogueado");
            localStorage.removeItem("password");
            window.location.href = "login.html";
        } else {
            const texto = await respuesta.text();
            console.error("Error API", respuesta.status, texto);
            alert("Error al guardar en la API: " + respuesta.status);
        }

    } catch (error) {
        console.error("Error conectando con la API:", error);
        alert("Error conectando con la API: " + error.message);
    }
}

// Registro del botón para evitar dependencias de onclick inline y prevenir "not defined"
document.addEventListener('DOMContentLoaded', function() {
    const boton = document.getElementById('btn_finalizar_lectura');
    if (boton) {
        boton.addEventListener('click', Guardar_libro);
    } else {
        console.error('Botón de finalizar lectura no encontrado.');
    }
});