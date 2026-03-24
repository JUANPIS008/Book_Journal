const API_URL = 'http://localhost:8080/api/libros';

async function Guardar_libro() {

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

    try {
        const respuesta = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(nuevoLibro)
        });

        if (respuesta.ok) {
            alert("Libro guardado correctamente en la API");
            window.location.href = "libros_leidos.html";
        } else {
            alert("Error al guardar en la API");
        }

    } catch (error) {
        console.error("Error conectando con la API:", error);
    }
}