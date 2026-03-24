const API_URL = 'http://localhost:8080/api/deseos';

function irlectura_actual() { window.location.href = "lectura_actual.html"; }
function irlibros_leidos() { window.location.href = "libros_leidos.html"; }
function irlista_deseos() { window.location.href = "lista_deseos.html"; }
function irperfil() { window.location.href = "perfil.html"; }
function irlogin() { window.location.href = "login.html"; }

document.addEventListener('DOMContentLoaded', () => {
    cargarDeseos();
});

async function cargarDeseos() {
    const contenedor = document.getElementById('lista-deseos-container');
    contenedor.innerHTML = '';

    try {
        const respuesta = await fetch(API_URL);

        if (!respuesta.ok) throw new Error();

        const listaLibros = await respuesta.json();

        if (listaLibros.length === 0) {
            contenedor.innerHTML = `
                <p style="text-align:center;">
                    Aún no tienes libros en tu lista 📚
                </p>`;
            return;
        }

        listaLibros.forEach(libro => renderizarTarjeta(libro));

    } catch (error) {
        console.error("Error cargando deseos:", error);
        contenedor.innerHTML = `<p>Error cargando datos</p>`;
    }
}

function renderizarTarjeta(libro) {
    const contenedor = document.getElementById('lista-deseos-container');

    const tarjeta = document.createElement('div');
    tarjeta.className = 'libro-card';

    tarjeta.innerHTML = `
        <h2>${libro.titulo}</h2>
        <button onclick="eliminarDeseo(${libro.id})">❌</button>
    `;

    contenedor.appendChild(tarjeta);
}

async function addWish() {
    const input = document.getElementById('wish-input');
    const titulo = input.value.trim();

    if (titulo === "") {
        alert("Escribe un libro");
        return;
    }

    try {
        await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ titulo })
        });

        input.value = "";
        cargarDeseos();

    } catch (error) {
        console.error("Error guardando:", error);
    }
}

async function eliminarDeseo(id) {
    if (!confirm("¿Eliminar este libro de tu lista?")) return;

    try {
        await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        cargarDeseos();

    } catch (error) {
        console.error("Error eliminando:", error);
    }
}