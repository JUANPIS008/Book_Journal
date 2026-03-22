// URL correcta de tu API
const API_URL = 'http://localhost:8080/api/libros/leidos';

// navegación
function irlectura_actual() { window.location.href = "lectura_actual.html"; }
function irlibros_leidos() { window.location.href = "libros_leidos.html"; }
function irlista_deseos() { window.location.href = "lista_deseos.html"; }
function irperfil() { window.location.href = "perfil.html"; }
function irlogin() { window.location.href = "login.html"; }

// cargar datos al iniciar
document.addEventListener('DOMContentLoaded', () => {
    cargarHistorial();
});

// cargar libros desde la API
async function cargarHistorial() {
    const contenedor = document.getElementById('historial-libros');
    contenedor.innerHTML = "";

    try {
        const respuesta = await fetch(API_URL);

        if (!respuesta.ok) {
            throw new Error("Error al obtener datos");
        }

        const libros = await respuesta.json();

        if (libros.length === 0) {
            contenedor.innerHTML = `
                <div style="text-align:center; padding:50px;">
                    <p>No hay libros leídos aún 📚</p>
                </div>`;
            return;
        }

        libros.forEach(libro => renderizarTarjeta(libro));

    } catch (error) {
        console.error("Error conectando con la API:", error);
        contenedor.innerHTML = `<p>Error cargando datos</p>`;
    }
}

// renderizar cada libro
function renderizarTarjeta(libro) {
    const contenedor = document.getElementById('historial-libros');
    const tarjeta = document.createElement('div');
    tarjeta.className = 'libro-card';
    tarjeta.id = `libro-${libro.id}`;

    // estrellas
    let estrellasHTML = '';
    const calif = parseInt(libro.calificacion) || 0;

    for (let i = 1; i <= 5; i++) {
        estrellasHTML += `
            <span style="color:${i <= calif ? '#ffd700' : '#ccc'};">
                ★
            </span>`;
    }

    tarjeta.innerHTML = `
        <h2>${libro.titulo}</h2>
        <p><strong>Autor:</strong> ${libro.autor || 'Desconocido'}</p>
        <p><strong>Género:</strong> ${libro.genero || 'N/A'}</p>
        <p><strong>Fechas:</strong> ${libro.inicio} - ${libro.fin}</p>
        <p><strong>Reseña:</strong> ${libro.resena || 'Sin reseña'}</p>
        <div><strong>Calificación:</strong> ${estrellasHTML}</div>
        <button onclick="eliminarLibro(${libro.id})">Eliminar</button>
    `;

    contenedor.appendChild(tarjeta);
}

// eliminar libro desde la API
async function eliminarLibro(id) {
    if (!confirm("¿Eliminar este libro?")) return;

    try {
        const respuesta = await fetch(`http://localhost:8080/api/libros/${id}`, {
            method: 'DELETE'
        });

        if (respuesta.ok) {
            alert("Libro eliminado correctamente");
            cargarHistorial();
        } else {
            alert("No se pudo eliminar");
        }

    } catch (error) {
        console.error("Error eliminando:", error);
    }
}