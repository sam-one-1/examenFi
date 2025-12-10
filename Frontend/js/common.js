// js/common.js

// Ajusta el puerto si tu backend usa otro
const API_URL = "http://localhost:8080";

const alertArea = document.getElementById("alertArea");

function renderAlert(message, type = "info") {
  if (!alertArea) return;
  if (!message) {
    alertArea.innerHTML = "";
    return;
  }
  const icons = {
    success: "✅",
    error: "⚠️",
    info: "ℹ️",
  };
  const baseClass =
    type === "success"
      ? "alert-success"
      : type === "error"
      ? "alert-error"
      : "alert-info";

  alertArea.innerHTML = `
    <div class="alert ${baseClass}" role="alert">
      <span class="alert-icon">${icons[type] || "ℹ️"}</span>
      <span>${message}</span>
    </div>
  `;
}

// Función helper para SweetAlert2
function showAlert(message, type = "info", title = null) {
  if (typeof Swal === 'undefined') {
    // Fallback a renderAlert si SweetAlert2 no está disponible
    renderAlert(message, type);
    return;
  }

  const config = {
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true,
    didOpen: (toast) => {
      toast.addEventListener('mouseenter', Swal.stopTimer);
      toast.addEventListener('mouseleave', Swal.resumeTimer);
    }
  };

  switch (type) {
    case "success":
      Swal.fire({
        ...config,
        icon: 'success',
        title: title || 'Éxito',
        text: message
      });
      break;
    case "error":
      Swal.fire({
        ...config,
        icon: 'error',
        title: title || 'Error',
        text: message
      });
      break;
    case "info":
    default:
      Swal.fire({
        ...config,
        icon: 'info',
        title: title || 'Información',
        text: message
      });
      break;
  }
}

// JWT helpers
function getToken() {
  return localStorage.getItem("jwt");
}

function setToken(token, documento) {
  if (token) {
    localStorage.setItem("jwt", token);
    if (documento) {
      localStorage.setItem("userDocumento", documento);
    }
  } else {
    localStorage.removeItem("jwt");
    localStorage.removeItem("userDocumento");
  }
}

// Fetch helper con JWT
async function apiFetch(path, options = {}) {
  const url = API_URL + path;
  const token = getToken();

  const headers = new Headers(options.headers || {});
  if (!headers.has("Content-Type") && options.body && typeof options.body === "string") {
    headers.set("Content-Type", "application/json");
  }
  if (token) {
    headers.set("Authorization", "Bearer " + token);
  }

  const response = await fetch(url, {
    ...options,
    headers,
  });

  let data;
  const contentType = response.headers.get("Content-Type") || "";
  if (contentType.includes("application/json")) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const msg =
      typeof data === "string"
        ? data
        : (data && (data.message || data.error || JSON.stringify(data)));
    throw new Error(msg || "Error en la llamada a la API");
  }

  return data;
}

// Render de tabla genérico
function renderTable(wrapperId, data, columns) {
  const wrapper = document.getElementById(wrapperId);
  if (!wrapper) return;

  wrapper.innerHTML = "";

  if (!Array.isArray(data) || data.length === 0) {
    const emptyMsg = document.createElement("p");
    emptyMsg.className = "section-subtitle";
    emptyMsg.textContent = "No hay datos para mostrar.";
    wrapper.appendChild(emptyMsg);
    return;
  }

  const table = document.createElement("table");
  const thead = document.createElement("thead");
  const tbody = document.createElement("tbody");

  const sample = data[0];
  const cols = columns && columns.length ? columns : Object.keys(sample);

  // Encabezados
  const trHead = document.createElement("tr");
  cols.forEach((col) => {
    const th = document.createElement("th");
    th.textContent = col;
    trHead.appendChild(th);
  });
  thead.appendChild(trHead);

  // Filas
  data.forEach((row) => {
    const tr = document.createElement("tr");

    cols.forEach((col) => {
      const td = document.createElement("td");
      const value = row[col];
      let display = "";

      if (value === null || value === undefined) {
        display = "";
      } else if (col === "encargado" && typeof value === "object") {
        // 👈 solo mostramos el nombre del encargado
        display = value.nombre || value.username || value.documento || value.id;
      } else if (typeof value === "object") {
        // Para otros objetos anidados, mostramos algo corto
        display =
          value.nombre ||
          value.descripcion ||
          value.id ||
          JSON.stringify(value);
      } else {
        display = value;
      }

      td.textContent = display;
      tr.appendChild(td);
    });

    tbody.appendChild(tr);
  });

  table.appendChild(thead);
  table.appendChild(tbody);
  wrapper.appendChild(table);
}


function escapeHtml(str) {
  return str
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}

function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error("No se pudo parsear el JWT:", e);
    return null;
  }
}

function getCurrentUser() {
  const token = getToken();
  if (!token) return null;

  const payload = parseJwt(token);
  if (!payload) return null;

  return {
    documento: payload.sub || null,
    role: payload.role || null,
    // en tu token pusiste "permissions" y "authorities"
    permissions: Array.isArray(payload.permissions)
      ? payload.permissions
      : (Array.isArray(payload.authorities) ? payload.authorities : []),
    exp: payload.exp || null
  };
}

