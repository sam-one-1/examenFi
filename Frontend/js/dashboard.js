// js/dashboard.js

document.addEventListener("DOMContentLoaded", () => {
    // Si no hay token, redirigir a login
    if (!getToken()) {
      window.location.href = "index.html?auth=required";
      return;
    }

    // Función para cargar opciones en un select de bodegas
    async function cargarBodegasEnSelect(selectId, incluirOpcionVacia = true) {
      const select = document.getElementById(selectId);
      if (!select) {
        console.warn(`Select con ID "${selectId}" no encontrado`);
        return;
      }

      try {
        const bodegas = await apiFetch("/bodegas");
        if (!Array.isArray(bodegas)) {
          console.error("La respuesta de bodegas no es un array:", bodegas);
          return;
        }
        
        // Limpiar opciones existentes (excepto la primera si existe)
        if (incluirOpcionVacia) {
          const placeholder = select.querySelector('option[value=""]')?.textContent || "Selecciona una bodega";
          select.innerHTML = `<option value="">${placeholder}</option>`;
        } else {
          select.innerHTML = '';
        }
        
        if (bodegas.length === 0) {
          const option = document.createElement("option");
          option.value = "";
          option.textContent = "No hay bodegas disponibles";
          select.appendChild(option);
          return;
        }
        
        bodegas.forEach(bodega => {
          const option = document.createElement("option");
          option.value = bodega.id;
          option.textContent = `${bodega.nombre || "Sin nombre"} (ID: ${bodega.id})`;
          select.appendChild(option);
        });
      } catch (err) {
        console.error("Error al cargar bodegas:", err);
        const errorMsg = err.message || "Error desconocido al cargar bodegas";
        showAlert("Error al cargar bodegas: " + errorMsg, "error");
        // Agregar opción de error en el select
        select.innerHTML = `<option value="">Error al cargar bodegas</option>`;
      }
    }

    // Función para cargar opciones en un select de productos
    async function cargarProductosEnSelect(selectId, incluirOpcionVacia = true) {
      const select = document.getElementById(selectId);
      if (!select) {
        console.warn(`Select con ID "${selectId}" no encontrado`);
        return;
      }

      try {
        const productos = await apiFetch("/productos");
        if (!Array.isArray(productos)) {
          console.error("La respuesta de productos no es un array:", productos);
          return;
        }
        
        // Limpiar opciones existentes (excepto la primera si existe)
        if (incluirOpcionVacia) {
          const placeholder = select.querySelector('option[value=""]')?.textContent || "Selecciona un producto";
          select.innerHTML = `<option value="">${placeholder}</option>`;
        } else {
          select.innerHTML = '';
        }
        
        if (productos.length === 0) {
          const option = document.createElement("option");
          option.value = "";
          option.textContent = "No hay productos disponibles";
          select.appendChild(option);
          return;
        }
        
        productos.forEach(producto => {
          const option = document.createElement("option");
          option.value = producto.id;
          option.textContent = `${producto.nombre || "Sin nombre"} (ID: ${producto.id})`;
          select.appendChild(option);
        });
      } catch (err) {
        console.error("Error al cargar productos:", err);
        const errorMsg = err.message || "Error desconocido al cargar productos";
        showAlert("Error al cargar productos: " + errorMsg, "error");
        // Agregar opción de error en el select
        select.innerHTML = `<option value="">Error al cargar productos</option>`;
      }
    }

    // Función para cargar opciones en un select de usuarios
    async function cargarUsuariosEnSelect(selectId, incluirOpcionVacia = true) {
      const select = document.getElementById(selectId);
      if (!select) {
        console.warn(`Select con ID "${selectId}" no encontrado`);
        return;
      }

      // Si el select está oculto (por ejemplo, para usuarios USER), no intentar cargar
      const formGroup = select.closest('.form-group');
      if (formGroup && formGroup.style.display === 'none') {
        return;
      }

      try {
        const usuarios = await apiFetch("/api/usuarios");
        if (!Array.isArray(usuarios)) {
          console.error("La respuesta de usuarios no es un array:", usuarios);
          return;
        }
        
        // Limpiar opciones existentes (excepto la primera si existe)
        if (incluirOpcionVacia) {
          const placeholder = select.querySelector('option[value=""]')?.textContent || "Selecciona un encargado (opcional)";
          select.innerHTML = `<option value="">${placeholder}</option>`;
        } else {
          select.innerHTML = '';
        }
        
        if (usuarios.length === 0) {
          const option = document.createElement("option");
          option.value = "";
          option.textContent = "No hay usuarios disponibles";
          select.appendChild(option);
          return;
        }
        
        usuarios.forEach(usuario => {
          const option = document.createElement("option");
          option.value = usuario.id;
          option.textContent = `${usuario.nombre || usuario.documento} (ID: ${usuario.id})`;
          select.appendChild(option);
        });
      } catch (err) {
        console.error("Error al cargar usuarios:", err);
        const errorMsg = err.message || "Error desconocido al cargar usuarios";
        // Solo mostrar error si el select es visible
        if (select.offsetParent !== null) {
          showAlert("Error al cargar usuarios: " + errorMsg, "error");
        }
        // Agregar opción de error en el select
        select.innerHTML = `<option value="">Error al cargar usuarios</option>`;
      }
    }

    const currentUser = getCurrentUser();
    const role = currentUser?.role || "USER";
    const permissions = currentUser?.permissions || [];

    // mostrar info en el chip de usuario (si lo tienes)
    const userChip = document.getElementById("userChip");
    const userChipText = document.getElementById("userChipText");
    if (userChip && userChipText) {
      userChipText.textContent = `${currentUser?.documento || ""} (${role})`;
      userChip.hidden = false;
    }

    // Ocultar elementos solo para admin si no es admin
    const isAdmin = role === "ADMIN" || permissions.includes("READ_USERS") || permissions.includes("CREATE_USERS");
    if (!isAdmin) {
      document.querySelectorAll("[data-admin-only]").forEach((el) => {
        el.style.display = "none";
        el.setAttribute("aria-hidden", "true");
      });
    }

    // aplicar restricciones de acceso según permisos
    setupDashboardAccess(role, permissions);
  
    const logoutBtn = document.getElementById("logoutBtn");
  
    // Actualizar chip de usuario
    const doc = localStorage.getItem("userDocumento") || "Usuario logueado";
    if (userChip && userChipText) {
      userChipText.textContent = `Conectado: ${doc}`;
      userChip.hidden = false;
    }
    if (logoutBtn) {
      logoutBtn.hidden = false;
      logoutBtn.addEventListener("click", () => {
        setToken(null);
        showAlert("Sesión cerrada correctamente.", "success");
        setTimeout(() => {
          window.location.href = "index.html";
        }, 500);
      });
    }
  
    // Subtabs del dashboard
    const dashboardSubtabButtons = document.querySelectorAll("[data-subtab]");
    const dashboardSubtabs = {
      bodegas: document.getElementById("subtab-bodegas"),
      productos: document.getElementById("subtab-productos"),
      movimientos: document.getElementById("subtab-movimientos"),
      auditorias: document.getElementById("subtab-auditorias"),
      usuarios: document.getElementById("subtab-usuarios"),
      reportes: document.getElementById("subtab-reportes"),
    };
  

    // Función para mostrar información del usuario actual en la sección de movimientos
    function mostrarUsuarioActual() {
      const usuarioActualInfo = document.getElementById("usuarioActualInfo");
      const usuarioActualNombre = document.getElementById("usuarioActualNombre");
      
      if (!usuarioActualInfo || !usuarioActualNombre) return;

      const currentUser = getCurrentUser();
      if (currentUser) {
        const nombre = localStorage.getItem("userDocumento") || currentUser.documento || "Usuario";
        const rol = currentUser.role || "USER";
        usuarioActualNombre.textContent = `${nombre} (${rol})`;
        usuarioActualInfo.style.display = "block";
      } else {
        usuarioActualInfo.style.display = "none";
      }
    }

    function showDashboardSubtab(name) {
      // Validar que la sección existe
      if (!dashboardSubtabs[name]) {
        console.warn(`Sección "${name}" no encontrada`);
        return;
      }
      
      // Ocultar todas las secciones primero
      Object.entries(dashboardSubtabs).forEach(([key, el]) => {
        if (!el) return;
        el.hidden = true;
        el.style.display = 'none';
      });
      
      // Mostrar solo la sección seleccionada
      const selectedSection = dashboardSubtabs[name];
      if (selectedSection) {
        selectedSection.hidden = false;
        selectedSection.style.display = 'block';
      }
      
      // Actualizar el estado activo de los botones del navbar
      dashboardSubtabButtons.forEach((btn) => {
        const isActive = btn.getAttribute("data-subtab") === name;
        btn.classList.toggle("tab-btn--active", isActive);
        btn.setAttribute("aria-selected", String(isActive));
      });
      
      // Limpiar alertas (ya no necesario con SweetAlert2, pero mantenemos para compatibilidad)
      if (alertArea) alertArea.innerHTML = "";
      
      // Cargar opciones según la sección
      if (name === "bodegas") {
        cargarUsuariosEnSelect("bodEncargadoId");
        // Cargar bodegas automáticamente al mostrar la sección
        setTimeout(() => {
          cargarDatosBodegas();
        }, 300);
      } else if (name === "productos") {
        cargarBodegasEnSelect("prodBodegaId");
        // Cargar productos automáticamente al mostrar la sección
        setTimeout(() => {
          cargarDatosProductos();
          cargarProductosStockBajo();
        }, 300);
      } else if (name === "movimientos") {
        cargarBodegasEnSelect("movBodegaOrigen");
        cargarBodegasEnSelect("movBodegaDestino");
        cargarProductosEnSelect("movProductoId");
        mostrarUsuarioActual();
      }
      
      // Hacer scroll suave al inicio del área de contenido (no al inicio de la sección para evitar saltos)
      const contentBody = document.querySelector('.content-body');
      if (contentBody) {
        setTimeout(() => {
          contentBody.scrollTop = 0;
        }, 100);
      }
    }
  
    dashboardSubtabButtons.forEach((btn) => {
      btn.addEventListener("click", () => {
        const sub = btn.getAttribute("data-subtab");
        showDashboardSubtab(sub);
      });
    });
  
    // Función para renderizar tabla de bodegas con botón de cambiar encargado
    async function renderBodegasTable(bodegas) {
      const wrapper = document.getElementById("bodegasTableWrapper");
      if (!wrapper) return;

      wrapper.innerHTML = "";

      if (!Array.isArray(bodegas) || bodegas.length === 0) {
        const emptyMsg = document.createElement("p");
        emptyMsg.className = "section-subtitle";
        emptyMsg.textContent = "No hay bodegas para mostrar.";
        wrapper.appendChild(emptyMsg);
        return;
      }

      // Verificar si el usuario es ADMIN
      const currentUser = getCurrentUser();
      const isAdmin = currentUser && (currentUser.role === "ADMIN" || 
        (currentUser.permissions && (currentUser.permissions.includes("READ_USERS") || 
         currentUser.permissions.includes("CREATE_USERS"))));

      const table = document.createElement("table");
      const thead = document.createElement("thead");
      const tbody = document.createElement("tbody");

      // Encabezados
      const trHead = document.createElement("tr");
      const headers = ["ID", "Nombre", "Ubicación", "Capacidad", "Encargado"];
      if (isAdmin) {
        headers.push("Acción");
      }
      
      headers.forEach((header) => {
        const th = document.createElement("th");
        th.textContent = header;
        trHead.appendChild(th);
      });
      thead.appendChild(trHead);

      // Filas
      for (const bodega of bodegas) {
        const tr = document.createElement("tr");

        // ID
        const tdId = document.createElement("td");
        tdId.textContent = bodega.id || "";
        tr.appendChild(tdId);

        // Nombre
        const tdNombre = document.createElement("td");
        tdNombre.textContent = bodega.nombre || "";
        tr.appendChild(tdNombre);

        // Ubicación
        const tdUbicacion = document.createElement("td");
        tdUbicacion.textContent = bodega.ubicacion || "";
        tr.appendChild(tdUbicacion);

        // Capacidad
        const tdCapacidad = document.createElement("td");
        tdCapacidad.textContent = bodega.capacidad || "";
        tr.appendChild(tdCapacidad);

        // Encargado
        const tdEncargado = document.createElement("td");
        if (bodega.encargado) {
          tdEncargado.textContent = bodega.encargado.nombre || bodega.encargado.documento || "Sin nombre";
        } else {
          tdEncargado.textContent = "Sin encargado";
        }
        tr.appendChild(tdEncargado);

        // Botón de acción (solo para ADMIN)
        if (isAdmin) {
          const tdAccion = document.createElement("td");
          const btnCambiar = document.createElement("button");
          btnCambiar.className = "btn btn-sm btn-outline";
          btnCambiar.innerHTML = '<span class="icon">👤</span><span>Cambiar encargado</span>';
          btnCambiar.addEventListener("click", () => cambiarEncargadoBodega(bodega));
          tdAccion.appendChild(btnCambiar);
          tr.appendChild(tdAccion);
        }

        tbody.appendChild(tr);
      }

      table.appendChild(thead);
      table.appendChild(tbody);
      wrapper.appendChild(table);
    }

    // Función para cambiar el encargado de una bodega
    async function cambiarEncargadoBodega(bodega) {
      try {
        // Cargar lista de usuarios
        const usuarios = await apiFetch("/api/usuarios");
        
        if (!usuarios || usuarios.length === 0) {
          showAlert("No hay usuarios disponibles para asignar como encargado.", "error");
          return;
        }

        // Crear opciones para el select
        const opciones = usuarios.map(usuario => ({
          value: usuario.id,
          text: `${usuario.nombre} (${usuario.documento})`
        }));

        // Mostrar modal con SweetAlert2
        const { value: nuevoEncargadoId } = await Swal.fire({
          title: `Cambiar encargado de "${bodega.nombre}"`,
          html: `
            <p style="text-align: left; margin-bottom: 1rem;">Encargado actual: <strong>${
              bodega.encargado ? (bodega.encargado.nombre || bodega.encargado.documento) : "Sin encargado"
            }</strong></p>
            <label for="swal-select-encargado" style="display: block; text-align: left; margin-bottom: 0.5rem; font-weight: bold;">Nuevo encargado:</label>
            <select id="swal-select-encargado" class="swal2-input" style="width: 100%; padding: 0.5rem;">
              <option value="">Sin encargado (eliminar asignación)</option>
              ${opciones.map(op => `<option value="${op.value}">${op.text}</option>`).join('')}
            </select>
          `,
          showCancelButton: true,
          confirmButtonText: "Cambiar encargado",
          cancelButtonText: "Cancelar",
          confirmButtonColor: "#3085d6",
          cancelButtonColor: "#d33",
          preConfirm: () => {
            const select = document.getElementById("swal-select-encargado");
            return select ? select.value : null;
          },
          didOpen: () => {
            const select = document.getElementById("swal-select-encargado");
            if (select && bodega.encargado && bodega.encargado.id) {
              select.value = bodega.encargado.id;
            }
          }
        });

        if (nuevoEncargadoId === undefined || nuevoEncargadoId === null) {
          return; // Usuario canceló
        }

        // Preparar datos para actualizar
        const datosActualizados = {
          nombre: bodega.nombre,
          ubicacion: bodega.ubicacion,
          capacidad: bodega.capacidad,
          encargado: (nuevoEncargadoId && nuevoEncargadoId.trim() !== "") ? { id: Number(nuevoEncargadoId) } : null
        };

        // Realizar la actualización
        await apiFetch(`/bodegas/${bodega.id}`, {
          method: "PUT",
          body: JSON.stringify(datosActualizados)
        });

        showAlert("Encargado actualizado correctamente.", "success");
        
        // Recargar la tabla de bodegas
        await cargarDatosBodegas();
      } catch (err) {
        showAlert("Error al cambiar encargado: " + err.message, "error");
      }
    }

    // Función para cargar datos de bodegas
    async function cargarDatosBodegas() {
      try {
        const bodegas = await apiFetch("/bodegas");
        await renderBodegasTable(bodegas);
        showAlert("Bodegas cargadas correctamente.", "success");
      } catch (err) {
        showAlert("Error al cargar bodegas: " + err.message, "error");
      }
    }

    // Función para cargar datos de productos
    async function cargarDatosProductos() {
      try {
        const productos = await apiFetch("/productos");
        await renderProductosConStock(productos);
        showAlert("Productos cargados correctamente.", "success");
      } catch (err) {
        showAlert("Error al cargar productos: " + err.message, "error");
      }
    }

    // ===== BODEGAS =====
    const bodegaForm = document.getElementById("bodegaForm");
    const btnListBodegas = document.getElementById("btnListBodegas");
    const btnCargarBodegas = document.getElementById("btnCargarBodegas");

    if (bodegaForm) {
      bodegaForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const nombre = document.getElementById("bodNombre").value.trim();
        const ubicacion = document.getElementById("bodUbicacion").value.trim();
        const capacidad = document.getElementById("bodCapacidad").value;
        const encargadoId = document.getElementById("bodEncargadoId").value || null;

        if (!nombre || !ubicacion || !capacidad) {
          showAlert("Nombre, ubicación y capacidad son obligatorios.", "error");
          return;
        }

        const payload = {
          nombre,
          ubicacion,
          capacidad: Number(capacidad)
        };

        if (encargadoId) {
          // Enviamos un objeto Usuario con solo el id
          payload.encargado = { id: Number(encargadoId) };
        }

        try {
          await apiFetch("/bodegas", {
            method: "POST",
            body: JSON.stringify(payload),
          });
          showAlert("Bodega creada correctamente.", "success");
          bodegaForm.reset();
        } catch (err) {
          showAlert("Error al crear bodega: " + err.message, "error");
        }
      });
    }

  
    if (btnListBodegas) {
      btnListBodegas.addEventListener("click", async () => {
        await cargarDatosBodegas();
      });
    }

    if (btnCargarBodegas) {
      btnCargarBodegas.addEventListener("click", async () => {
        await cargarDatosBodegas();
      });
    }
  
    // ===== PRODUCTOS =====
    const productoForm = document.getElementById("productoForm");
    const btnListProductos = document.getElementById("btnListProductos");
    const btnStockBajo = document.getElementById("btnStockBajo");
    const btnCargarProductos = document.getElementById("btnCargarProductos");
  
    if (productoForm) {
      productoForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const nombre = document.getElementById("prodNombre").value.trim();
        const categoria = document
          .getElementById("prodCategoria")
          .value.trim();
        const bodegaId = document.getElementById("prodBodegaId").value ? Number(document.getElementById("prodBodegaId").value) : null;
        const stock = Number(document.getElementById("prodStock").value);
        const precio = Number(document.getElementById("prodPrecio").value);
  
        if (!bodegaId) {
          showAlert("Debes especificar una bodega para el producto.", "error");
          return;
        }

        try {
          await apiFetch("/productos", {
            method: "POST",
            body: JSON.stringify({ nombre, categoria, stock, precio, bodegaId }),
          });
          showAlert("Producto creado correctamente.", "success");
          productoForm.reset();
        } catch (err) {
          showAlert("Error al crear producto: " + err.message, "error");
        }
      });
    }
  
    // Función para renderizar productos con stock por bodega
    async function renderProductosConStock(productos) {
      const wrapper = document.getElementById("productosTableWrapper");
      if (!wrapper) return;

      wrapper.innerHTML = "";

      if (!Array.isArray(productos) || productos.length === 0) {
        const emptyMsg = document.createElement("p");
        emptyMsg.className = "section-subtitle";
        emptyMsg.textContent = "No hay productos para mostrar.";
        wrapper.appendChild(emptyMsg);
        return;
      }

      const table = document.createElement("table");
      const thead = document.createElement("thead");
      const tbody = document.createElement("tbody");

      // Encabezados
      const trHead = document.createElement("tr");
      ["ID", "Nombre", "Categoría", "Precio", "Stock por Bodega"].forEach((col) => {
        const th = document.createElement("th");
        th.textContent = col;
        trHead.appendChild(th);
      });
      thead.appendChild(trHead);

      // Filas
      for (const producto of productos) {
        const tr = document.createElement("tr");

        // ID
        const tdId = document.createElement("td");
        tdId.textContent = producto.id || "";
        tr.appendChild(tdId);

        // Nombre
        const tdNombre = document.createElement("td");
        tdNombre.textContent = producto.nombre || "";
        tr.appendChild(tdNombre);

        // Categoría
        const tdCategoria = document.createElement("td");
        tdCategoria.textContent = producto.categoria || "";
        tr.appendChild(tdCategoria);

        // Precio
        const tdPrecio = document.createElement("td");
        tdPrecio.textContent = producto.precio != null ? `$${producto.precio.toFixed(2)}` : "";
        tr.appendChild(tdPrecio);

        // Stock por Bodega
        const tdStock = document.createElement("td");
        try {
          const stockData = await apiFetch(`/productos/${producto.id}/stock`);
          if (stockData && stockData.length > 0) {
            const stockInfo = stockData.map(s => 
              `${s.bodegaNombre || `Bodega ${s.bodegaId}`}: ${s.cantidad || 0}`
            ).join(", ");
            tdStock.textContent = stockInfo || "Sin stock";
          } else {
            tdStock.textContent = "Sin stock asignado";
          }
        } catch (err) {
          tdStock.textContent = "Error al cargar stock";
        }
        tr.appendChild(tdStock);

        tbody.appendChild(tr);
      }

      table.appendChild(thead);
      table.appendChild(tbody);
      wrapper.appendChild(table);
    }

    if (btnListProductos) {
      btnListProductos.addEventListener("click", async () => {
        await cargarDatosProductos();
      });
    }

    if (btnCargarProductos) {
      btnCargarProductos.addEventListener("click", async () => {
        await cargarDatosProductos();
      });
    }
  
    if (btnStockBajo) {
      btnStockBajo.addEventListener("click", async () => {
        await cargarProductosStockBajo();
      });
    }
    
    // Función para cargar productos con stock bajo automáticamente
    async function cargarProductosStockBajo() {
      try {
        const productos = await apiFetch("/productos/stock-bajo?limite=10");
        await renderProductosStockBajo(productos);
      } catch (err) {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Error al cargar productos con stock bajo: ' + err.message,
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 3000
        });
      }
    }
    
    // Función para renderizar productos con stock bajo en el apartado dedicado
    async function renderProductosStockBajo(productos) {
      const wrapper = document.getElementById("productosStockBajoWrapper");
      if (!wrapper) return;

      wrapper.innerHTML = "";

      if (!Array.isArray(productos) || productos.length === 0) {
        const emptyMsg = document.createElement("p");
        emptyMsg.className = "section-subtitle";
        emptyMsg.style.color = "#28a745";
        emptyMsg.textContent = "✅ No hay productos con stock bajo. Todo está en orden.";
        wrapper.appendChild(emptyMsg);
        return;
      }

      const table = document.createElement("table");
      const thead = document.createElement("thead");
      const tbody = document.createElement("tbody");

      // Encabezados
      const trHead = document.createElement("tr");
      ["ID", "Nombre", "Categoría", "Stock", "Precio", "Stock por Bodega"].forEach((col) => {
        const th = document.createElement("th");
        th.textContent = col;
        trHead.appendChild(th);
      });
      thead.appendChild(trHead);

      // Filas
      for (const producto of productos) {
        const tr = document.createElement("tr");
        tr.style.backgroundColor = "#fff3cd"; // Fondo amarillo claro para destacar

        // ID
        const tdId = document.createElement("td");
        tdId.textContent = producto.id || "";
        tr.appendChild(tdId);

        // Nombre
        const tdNombre = document.createElement("td");
        tdNombre.textContent = producto.nombre || "";
        tr.appendChild(tdNombre);

        // Categoría
        const tdCategoria = document.createElement("td");
        tdCategoria.textContent = producto.categoria || "";
        tr.appendChild(tdCategoria);

        // Stock (global)
        const tdStock = document.createElement("td");
        tdStock.textContent = producto.stock !== null && producto.stock !== undefined ? producto.stock : "0";
        tdStock.style.fontWeight = "bold";
        tdStock.style.color = "#dc3545"; // Rojo para destacar
        tr.appendChild(tdStock);

        // Precio
        const tdPrecio = document.createElement("td");
        tdPrecio.textContent = producto.precio ? `$${producto.precio.toFixed(2)}` : "$0.00";
        tr.appendChild(tdPrecio);

        // Stock por Bodega
        const tdStockBodega = document.createElement("td");
        try {
          const stockInfo = await apiFetch(`/productos/${producto.id}/stock`);
          if (stockInfo && stockInfo.length > 0) {
            const stockText = stockInfo.map(s => `${s.bodegaNombre}: ${s.cantidad}`).join(", ");
            tdStockBodega.textContent = stockText;
          } else {
            tdStockBodega.textContent = "Sin stock en bodegas";
          }
        } catch (e) {
          tdStockBodega.textContent = "N/A";
        }
        tr.appendChild(tdStockBodega);

        tbody.appendChild(tr);
      }

      table.appendChild(thead);
      table.appendChild(tbody);
      wrapper.appendChild(table);
    }
  
    // ===== MOVIMIENTOS =====
    const movimientoForm = document.getElementById("movimientoForm");
    const btnListMovimientos = document.getElementById("btnListMovimientos");
    const movimientosFechaForm = document.getElementById("movimientosFechaForm");
    const movTipo = document.getElementById("movTipo");
    const movBodegaOrigenGroup = document.getElementById("movBodegaOrigenGroup");
    const movBodegaDestinoGroup = document.getElementById("movBodegaDestinoGroup");
    const movBodegaOrigen = document.getElementById("movBodegaOrigen");
    const movBodegaDestino = document.getElementById("movBodegaDestino");

    // Función para actualizar visibilidad de campos según tipo de movimiento
    function actualizarCamposMovimiento() {
      const tipo = movTipo.value;
      
      // Resetear valores
      movBodegaOrigen.value = "";
      movBodegaDestino.value = "";
      
      if (tipo === "SALIDA") {
        // Solo mostrar bodega origen
        movBodegaOrigenGroup.classList.remove("disabled");
        movBodegaDestinoGroup.classList.add("disabled");
        movBodegaOrigen.required = true;
        movBodegaDestino.required = false;
        movBodegaDestino.disabled = true;
        movBodegaOrigen.disabled = false;
      } else if (tipo === "ENTRADA") {
        // Solo mostrar bodega destino
        movBodegaOrigenGroup.classList.add("disabled");
        movBodegaDestinoGroup.classList.remove("disabled");
        movBodegaOrigen.required = false;
        movBodegaDestino.required = true;
        movBodegaOrigen.disabled = true;
        movBodegaDestino.disabled = false;
      } else if (tipo === "TRANSFERENCIA") {
        // Mostrar ambas
        movBodegaOrigenGroup.classList.remove("disabled");
        movBodegaDestinoGroup.classList.remove("disabled");
        movBodegaOrigen.required = true;
        movBodegaDestino.required = true;
        movBodegaOrigen.disabled = false;
        movBodegaDestino.disabled = false;
      } else {
        // Ninguna seleccionada
        movBodegaOrigenGroup.classList.remove("disabled");
        movBodegaDestinoGroup.classList.remove("disabled");
        movBodegaOrigen.required = false;
        movBodegaDestino.required = false;
        movBodegaOrigen.disabled = false;
        movBodegaDestino.disabled = false;
      }
    }

    // Escuchar cambios en el tipo de movimiento
    if (movTipo) {
      movTipo.addEventListener("change", actualizarCamposMovimiento);
      // Inicializar estado
      actualizarCamposMovimiento();
    }

    if (movimientoForm) {
      movimientoForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const tipoMovimiento = movTipo.value;
        const bodegaOrigenId = movBodegaOrigen.value || null;
        const bodegaDestinoId = movBodegaDestino.value || null;
        const productoId = document.getElementById("movProductoId").value;
        const cantidad = document.getElementById("movCantidad").value;

        if (!tipoMovimiento) {
          showAlert("Selecciona el tipo de movimiento.", "error");
          return;
        }
        if (!productoId || !cantidad) {
          showAlert("Producto y cantidad son obligatorios.", "error");
          return;
        }

        // Validaciones según tipo
        if (tipoMovimiento === "ENTRADA" && !bodegaDestinoId) {
          showAlert("Para una ENTRADA necesitás especificar la bodega destino.", "error");
          return;
        }
        if (tipoMovimiento === "SALIDA" && !bodegaOrigenId) {
          showAlert("Para una SALIDA necesitás especificar la bodega origen.", "error");
          return;
        }
        if (tipoMovimiento === "TRANSFERENCIA" && (!bodegaOrigenId || !bodegaDestinoId)) {
          showAlert(
            "Para una TRANSFERENCIA necesitás especificar bodega origen y destino.",
            "error"
          );
          return;
        }

        // Armamos el payload esperado por el backend
        const payload = {
          tipoMovimiento: tipoMovimiento,
          detalles: [
            {
              producto: { id: Number(productoId) },
              cantidad: Number(cantidad),
            },
          ],
        };

        if (bodegaOrigenId) {
          payload.bodegaOrigen = { id: Number(bodegaOrigenId) };
        }
        if (bodegaDestinoId) {
          payload.bodegaDestino = { id: Number(bodegaDestinoId) };
        }

        try {
          await apiFetch("/movimientoInventario", {
            method: "POST",
            body: JSON.stringify(payload),
          });
          showAlert("Movimiento creado correctamente.", "success");
          movimientoForm.reset();
          actualizarCamposMovimiento();
        } catch (err) {
          showAlert("Error al crear movimiento: " + err.message, "error");
        }
      });
    }

  
    // Función para renderizar tabla de movimientos con filas clicables
    function renderMovimientosTable(movimientos) {
      const wrapper = document.getElementById("movimientosTableWrapper");
      if (!wrapper) return;

      wrapper.innerHTML = "";

      if (!Array.isArray(movimientos) || movimientos.length === 0) {
        const emptyMsg = document.createElement("p");
        emptyMsg.className = "section-subtitle";
        emptyMsg.textContent = "No hay datos para mostrar.";
        wrapper.appendChild(emptyMsg);
        return;
      }

      const table = document.createElement("table");
      const thead = document.createElement("thead");
      const tbody = document.createElement("tbody");

      // Encabezados
      const trHead = document.createElement("tr");
      ["ID", "Fecha", "Tipo de Movimiento", "Usuario Responsable"].forEach((col) => {
        const th = document.createElement("th");
        th.textContent = col;
        trHead.appendChild(th);
      });
      thead.appendChild(trHead);

      // Filas con eventos de clic
      movimientos.forEach((movimiento) => {
        const tr = document.createElement("tr");
        tr.style.cursor = "pointer";
        tr.title = "Haz clic para ver detalles";

        // ID
        const tdId = document.createElement("td");
        tdId.textContent = movimiento.id || "";
        tr.appendChild(tdId);

        // Fecha
        const tdFecha = document.createElement("td");
        if (movimiento.fecha) {
          const fecha = new Date(movimiento.fecha);
          tdFecha.textContent = fecha.toLocaleString("es-ES");
        } else {
          tdFecha.textContent = "";
        }
        tr.appendChild(tdFecha);

        // Tipo de movimiento
        const tdTipo = document.createElement("td");
        tdTipo.textContent = movimiento.tipoMovimiento || "";
        tr.appendChild(tdTipo);

        // Usuario responsable
        const tdUsuario = document.createElement("td");
        if (movimiento.usuarioResponsable) {
          const usuario = movimiento.usuarioResponsable;
          tdUsuario.textContent = usuario.nombre || usuario.documento || `ID: ${usuario.id}`;
        } else {
          tdUsuario.textContent = "N/A";
        }
        tr.appendChild(tdUsuario);

        // Evento de clic para mostrar detalles
        tr.addEventListener("click", async () => {
          try {
            const movimientoCompleto = await apiFetch(`/movimientoInventario/${movimiento.id}`);
            mostrarDetallesMovimiento(movimientoCompleto);
          } catch (err) {
            showAlert("Error al cargar detalles del movimiento: " + err.message, "error");
          }
        });

        tbody.appendChild(tr);
      });

      table.appendChild(thead);
      table.appendChild(tbody);
      wrapper.appendChild(table);
    }

    // Función para mostrar detalles del movimiento en el modal
    function mostrarDetallesMovimiento(movimiento) {
      const modal = document.getElementById("movimientoDetalleModal");
      const content = document.getElementById("movimientoDetalleContent");
      
      if (!modal || !content) return;

      let html = "";

      // Información general
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">ID del Movimiento</div>';
      html += `<div class="modal-detail-value">${movimiento.id || "N/A"}</div>`;
      html += '</div>';

      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Fecha</div>';
      html += `<div class="modal-detail-value">${movimiento.fecha ? new Date(movimiento.fecha).toLocaleString("es-ES") : "N/A"}</div>`;
      html += '</div>';

      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Tipo de Movimiento</div>';
      html += `<div class="modal-detail-value">${movimiento.tipoMovimiento || "N/A"}</div>`;
      html += '</div>';

      // Usuario Responsable
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Usuario Responsable</div>';
      html += '<div class="modal-detail-value">';
      if (movimiento.usuarioResponsable) {
        const usuario = movimiento.usuarioResponsable;
        html += `<strong>Nombre:</strong> ${usuario.nombre || "N/A"}`;
        if (usuario.documento) {
          html += `<br><strong>Documento:</strong> ${usuario.documento}`;
        }
        if (usuario.role) {
          html += `<br><strong>Rol:</strong> ${usuario.role}`;
        }
      } else {
        html += "N/A";
      }
      html += '</div>';
      html += '</div>';

      // Bodegas
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Bodegas Involucradas</div>';
      html += '<div class="modal-detail-value">';
      if (movimiento.bodegaOrigen) {
        html += `<strong>Origen:</strong> ${movimiento.bodegaOrigen.nombre || "ID: " + movimiento.bodegaOrigen.id} (ID: ${movimiento.bodegaOrigen.id})`;
      }
      if (movimiento.bodegaOrigen && movimiento.bodegaDestino) {
        html += '<br>';
      }
      if (movimiento.bodegaDestino) {
        html += `<strong>Destino:</strong> ${movimiento.bodegaDestino.nombre || "ID: " + movimiento.bodegaDestino.id} (ID: ${movimiento.bodegaDestino.id})`;
      }
      if (!movimiento.bodegaOrigen && !movimiento.bodegaDestino) {
        html += "N/A";
      }
      html += '</div>';
      html += '</div>';

      // Detalles del movimiento (productos)
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Productos Movidos</div>';
      html += '<div class="modal-detail-value">';
      
      if (movimiento.detalles && movimiento.detalles.length > 0) {
        html += '<table style="width: 100%; margin-top: 0.5rem; font-size: 0.85rem;">';
        html += '<thead><tr><th>Producto</th><th>Cantidad</th></tr></thead>';
        html += '<tbody>';
        movimiento.detalles.forEach((detalle) => {
          const productoNombre = detalle.producto?.nombre || `ID: ${detalle.producto?.id || "N/A"}`;
          html += `<tr><td>${escapeHtml(productoNombre)}</td><td>${detalle.cantidad || 0}</td></tr>`;
        });
        html += '</tbody></table>';
      } else {
        html += "No hay detalles disponibles";
      }
      
      html += '</div>';
      html += '</div>';

      content.innerHTML = html;
      modal.style.display = "block";
    }

    // Cerrar modal
    const cerrarModalDetalle = document.getElementById("cerrarModalDetalle");
    const movimientoDetalleModal = document.getElementById("movimientoDetalleModal");
    
    if (cerrarModalDetalle) {
      cerrarModalDetalle.addEventListener("click", () => {
        if (movimientoDetalleModal) {
          movimientoDetalleModal.style.display = "none";
        }
      });
    }

    // Cerrar modal al hacer clic fuera
    if (movimientoDetalleModal) {
      movimientoDetalleModal.addEventListener("click", (e) => {
        if (e.target === movimientoDetalleModal) {
          movimientoDetalleModal.style.display = "none";
        }
      });
    }

    if (btnListMovimientos) {
      btnListMovimientos.addEventListener("click", async () => {
        try {
          const movimientos = await apiFetch("/movimientoInventario");
          renderMovimientosTable(movimientos);
          showAlert("Movimientos cargados correctamente.", "success");
        } catch (err) {
          showAlert("Error al listar movimientos: " + err.message, "error");
        }
      });
    }
  
    if (movimientosFechaForm) {
      movimientosFechaForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const desde = document.getElementById("movDesde").value;
        const hasta = document.getElementById("movHasta").value;
  
        if (!desde || !hasta) {
          showAlert("Elegí ambas fechas para filtrar.", "error");
          return;
        }
  
        try {
          const movimientos = await apiFetch(
            `/movimientoInventario/por-fecha?desde=${encodeURIComponent(
              desde
            )}&hasta=${encodeURIComponent(hasta)}`
          );
          renderMovimientosTable(movimientos);
          showAlert("Movimientos filtrados correctamente.", "success");
        } catch (err) {
          showAlert(
            "Error al filtrar movimientos: " + err.message,
            "error"
          );
        }
      });
    }
  
    // ===== AUDITORÍAS =====
    const auditoriaFiltroForm = document.getElementById("auditoriaFiltroForm");
    const btnAudTodas = document.getElementById("btnAudTodas");
  
    if (auditoriaFiltroForm) {
      auditoriaFiltroForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const doc = document.getElementById("audDocumento").value.trim();
        const tipo = document.getElementById("audTipoOperacion").value;
  
        let query = [];
        if (doc) query.push(`documento=${encodeURIComponent(doc)}`);
        if (tipo) query.push(`tipoOperacion=${encodeURIComponent(tipo)}`);
        const qs = query.length ? "?" + query.join("&") : "";
  
        try {
          const auditorias = await apiFetch("/auditorias/listar" + qs);
          renderAuditoriasTable(auditorias);
          showAlert("Auditorías cargadas correctamente.", "success");
        } catch (err) {
          showAlert("Error al listar auditorías: " + err.message, "error");
        }
      });
    }
  
    if (btnAudTodas) {
      btnAudTodas.addEventListener("click", async () => {
        try {
          const auditorias = await apiFetch("/auditorias");
          renderAuditoriasTable(auditorias);
          showAlert("Todas las auditorías cargadas.", "success");
        } catch (err) {
          showAlert("Error al listar auditorías: " + err.message, "error");
        }
      });
    }
    
    // Función para renderizar tabla de auditorías con filas clicables
    function renderAuditoriasTable(auditorias) {
      const wrapper = document.getElementById("auditoriasTableWrapper");
      if (!wrapper) return;

      wrapper.innerHTML = "";

      if (!Array.isArray(auditorias) || auditorias.length === 0) {
        const emptyMsg = document.createElement("p");
        emptyMsg.className = "section-subtitle";
        emptyMsg.textContent = "No hay datos para mostrar.";
        wrapper.appendChild(emptyMsg);
        return;
      }

      const table = document.createElement("table");
      const thead = document.createElement("thead");
      const tbody = document.createElement("tbody");

      // Encabezados
      const trHead = document.createElement("tr");
      ["ID", "Tipo Operación", "Fecha y Hora", "Entidad Afectada", "Usuario Responsable"].forEach((col) => {
        const th = document.createElement("th");
        th.textContent = col;
        trHead.appendChild(th);
      });
      thead.appendChild(trHead);

      // Filas con eventos de clic
      auditorias.forEach((auditoria) => {
        const tr = document.createElement("tr");
        tr.style.cursor = "pointer";
        tr.title = "Haz clic para ver detalles";

        // ID
        const tdId = document.createElement("td");
        tdId.textContent = auditoria.id || "";
        tr.appendChild(tdId);

        // Tipo Operación
        const tdTipo = document.createElement("td");
        tdTipo.textContent = auditoria.tipoOperacion || "";
        tr.appendChild(tdTipo);

        // Fecha y Hora
        const tdFecha = document.createElement("td");
        if (auditoria.fechaHora) {
          const fecha = new Date(auditoria.fechaHora);
          tdFecha.textContent = fecha.toLocaleString("es-ES");
        } else {
          tdFecha.textContent = "";
        }
        tr.appendChild(tdFecha);

        // Entidad Afectada
        const tdEntidad = document.createElement("td");
        tdEntidad.textContent = auditoria.entidadAfectada || "";
        tr.appendChild(tdEntidad);

        // Usuario Responsable
        const tdUsuario = document.createElement("td");
        if (auditoria.usuarioResponsable) {
          tdUsuario.textContent = auditoria.usuarioResponsable.nombre || auditoria.usuarioResponsable.documento || "N/A";
        } else {
          tdUsuario.textContent = "N/A";
        }
        tr.appendChild(tdUsuario);

        // Evento de clic para mostrar detalles
        tr.addEventListener("click", async () => {
          try {
            const auditoriaCompleta = await apiFetch(`/auditorias/${auditoria.id}`);
            mostrarDetallesAuditoria(auditoriaCompleta);
          } catch (err) {
            showAlert("Error al cargar detalles de auditoría: " + err.message, "error");
          }
        });

        tbody.appendChild(tr);
      });

      table.appendChild(thead);
      table.appendChild(tbody);
      wrapper.appendChild(table);
    }

    // Función para mostrar detalles de la auditoría en el modal
    function mostrarDetallesAuditoria(auditoria) {
      const modal = document.getElementById("auditoriaDetalleModal");
      const content = document.getElementById("auditoriaDetalleContent");
      if (!modal || !content) return;

      let html = "";

      // ID
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">ID de la Auditoría</div>';
      html += `<div class="modal-detail-value">${auditoria.id || "N/A"}</div>`;
      html += "</div>";

      // Tipo de Operación
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Tipo de Operación</div>';
      html += `<div class="modal-detail-value">${auditoria.tipoOperacion || "N/A"}</div>`;
      html += "</div>";

      // Fecha y Hora
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Fecha y Hora</div>';
      html += `<div class="modal-detail-value">${auditoria.fechaHora ? new Date(auditoria.fechaHora).toLocaleString("es-ES") : "N/A"}</div>`;
      html += "</div>";

      // Entidad Afectada
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Entidad Afectada</div>';
      html += `<div class="modal-detail-value">${auditoria.entidadAfectada || "N/A"}</div>`;
      html += "</div>";

      // Usuario Responsable
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Usuario Responsable</div>';
      html += '<div class="modal-detail-value">';
      if (auditoria.usuarioResponsable) {
        html += `${auditoria.usuarioResponsable.nombre || "N/A"} (${auditoria.usuarioResponsable.documento || "N/A"})`;
      } else {
        html += "N/A";
      }
      html += "</div>";
      html += "</div>";

      // Valores Anteriores
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Valores Anteriores</div>';
      html += '<div class="modal-detail-value">';
      if (auditoria.valoresAnteriores) {
        try {
          const valoresAnteriores = JSON.parse(auditoria.valoresAnteriores);
          html += `<pre style="background: #f5f5f5; padding: 1rem; border-radius: 4px; overflow-x: auto;">${escapeHtml(JSON.stringify(valoresAnteriores, null, 2))}</pre>`;
        } catch (e) {
          html += `<pre style="background: #f5f5f5; padding: 1rem; border-radius: 4px; overflow-x: auto;">${escapeHtml(auditoria.valoresAnteriores)}</pre>`;
        }
      } else {
        html += "N/A";
      }
      html += "</div>";
      html += "</div>";

      // Valores Nuevos
      html += '<div class="modal-detail-item">';
      html += '<div class="modal-detail-label">Valores Nuevos</div>';
      html += '<div class="modal-detail-value">';
      if (auditoria.valoresNuevos) {
        try {
          const valoresNuevos = JSON.parse(auditoria.valoresNuevos);
          html += `<pre style="background: #f5f5f5; padding: 1rem; border-radius: 4px; overflow-x: auto;">${escapeHtml(JSON.stringify(valoresNuevos, null, 2))}</pre>`;
        } catch (e) {
          html += `<pre style="background: #f5f5f5; padding: 1rem; border-radius: 4px; overflow-x: auto;">${escapeHtml(auditoria.valoresNuevos)}</pre>`;
        }
      } else {
        html += "N/A";
      }
      html += "</div>";
      html += "</div>";

      content.innerHTML = html;
      modal.style.display = "block";
    }

    // Cerrar modal de auditoría
    const cerrarModalAuditoria = document.getElementById("cerrarModalAuditoria");
    const auditoriaDetalleModal = document.getElementById("auditoriaDetalleModal");

    if (cerrarModalAuditoria) {
      cerrarModalAuditoria.addEventListener("click", () => {
        if (auditoriaDetalleModal) {
          auditoriaDetalleModal.style.display = "none";
        }
      });
    }

    // Cerrar modal al hacer clic fuera
    if (auditoriaDetalleModal) {
      auditoriaDetalleModal.addEventListener("click", (e) => {
        if (e.target === auditoriaDetalleModal) {
          auditoriaDetalleModal.style.display = "none";
        }
      });
    }
  
    // ===== REPORTES =====
    const btnReporteResumen = document.getElementById("btnReporteResumen");
    const reportesOutput = document.getElementById("reportesOutput");
  
    if (btnReporteResumen && reportesOutput) {
      btnReporteResumen.addEventListener("click", async () => {
        reportesOutput.innerHTML = "";
        try {
          const resumen = await apiFetch("/reportes/resumen");
  
          const stock = resumen.stockPorBodega || [];
          const productos = resumen.productosMasMovidos || [];
  
          let html = "";
  
          html += "<h4>Stock por bodega</h4>";
          if (stock.length) {
            html +=
              "<div class='table-wrapper'><table><thead><tr><th>Bodega</th><th>ID</th><th>Stock total</th></tr></thead><tbody>";
            stock.forEach((b) => {
              html += `<tr><td>${escapeHtml(
                b.bodegaNombre
              )}</td><td>${b.bodegaId}</td><td>${b.stockTotal}</td></tr>`;
            });
            html += "</tbody></table></div>";
          } else {
            html +=
              "<p class='section-subtitle'>No hay datos de stock por bodega.</p>";
          }
  
          html += "<h4 class='mt-2'>Productos más movidos</h4>";
          if (productos.length) {
            html +=
              "<div class='table-wrapper'><table><thead><tr><th>Producto</th><th>ID</th><th>Total movido</th></tr></thead><tbody>";
            productos.forEach((p) => {
              html += `<tr><td>${escapeHtml(
                p.productoNombre
              )}</td><td>${p.productoId}</td><td>${p.totalMovido}</td></tr>`;
            });
            html += "</tbody></table></div>";
          } else {
            html +=
              "<p class='section-subtitle'>No hay datos de productos movidos.</p>";
          }
  
          reportesOutput.innerHTML = html;
          showAlert("Reporte cargado correctamente.", "success");
        } catch (err) {
          showAlert("Error al obtener reporte: " + err.message, "error");
        }
      });
    }
  
    // ===== USUARIOS =====
    const btnListUsuarios = document.getElementById("btnListUsuarios");
    const cambiarRolForm = document.getElementById("cambiarRolForm");
    const usuariosRolForm = document.getElementById("usuariosRolForm");
    const btnCancelarCambioRol = document.getElementById("btnCancelarCambioRol");

    if (btnListUsuarios) {
      btnListUsuarios.addEventListener("click", async () => {
        try {
          const usuarios = await apiFetch("/api/usuarios");
          
          // Crear una tabla con botones para cambiar rol
          const wrapper = document.getElementById("usuariosTableWrapper");
          if (!wrapper) return;

          wrapper.innerHTML = "";

          if (!Array.isArray(usuarios) || usuarios.length === 0) {
            const emptyMsg = document.createElement("p");
            emptyMsg.className = "section-subtitle";
            emptyMsg.textContent = "No hay usuarios para mostrar.";
            wrapper.appendChild(emptyMsg);
            return;
          }

          const table = document.createElement("table");
          const thead = document.createElement("thead");
          const tbody = document.createElement("tbody");

          // Encabezados
          const trHead = document.createElement("tr");
          ["ID", "Nombre", "Documento", "Rol", "Acciones"].forEach((col) => {
            const th = document.createElement("th");
            th.textContent = col;
            trHead.appendChild(th);
          });
          thead.appendChild(trHead);

          // Filas
          usuarios.forEach((usuario) => {
            const tr = document.createElement("tr");

            // ID
            const tdId = document.createElement("td");
            tdId.textContent = usuario.id;
            tr.appendChild(tdId);

            // Nombre
            const tdNombre = document.createElement("td");
            tdNombre.textContent = usuario.nombre || "";
            tr.appendChild(tdNombre);

            // Documento
            const tdDocumento = document.createElement("td");
            tdDocumento.textContent = usuario.documento || "";
            tr.appendChild(tdDocumento);

            // Rol
            const tdRol = document.createElement("td");
            tdRol.textContent = usuario.role || "";
            tr.appendChild(tdRol);

            // Acciones
            const tdAcciones = document.createElement("td");
            const btnCambiarRol = document.createElement("button");
            btnCambiarRol.className = "btn btn-outline btn-sm";
            btnCambiarRol.textContent = "Cambiar Rol";
            btnCambiarRol.addEventListener("click", () => {
              document.getElementById("usuarioIdRol").value = usuario.id;
              document.getElementById("usuarioNombreRol").value = usuario.nombre || "";
              document.getElementById("usuarioRolActual").value = usuario.role || "";
              document.getElementById("nuevoRol").value = "";
              usuariosRolForm.style.display = "block";
              usuariosRolForm.scrollIntoView({ behavior: "smooth" });
            });
            tdAcciones.appendChild(btnCambiarRol);
            tr.appendChild(tdAcciones);

            tbody.appendChild(tr);
          });

          table.appendChild(thead);
          table.appendChild(tbody);
          wrapper.appendChild(table);
          showAlert("Usuarios cargados correctamente.", "success");
        } catch (err) {
          showAlert("Error al listar usuarios: " + err.message, "error");
        }
      });
    }

    if (cambiarRolForm) {
      cambiarRolForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const usuarioId = document.getElementById("usuarioIdRol").value;
        const nuevoRol = document.getElementById("nuevoRol").value;

        if (!usuarioId || !nuevoRol) {
          showAlert("Completa todos los campos.", "error");
          return;
        }

        try {
          await apiFetch(`/api/usuarios/${usuarioId}/rol`, {
            method: "PATCH",
            body: JSON.stringify({ nuevoRol: nuevoRol }),
          });
          showAlert("Rol cambiado correctamente. Recarga la lista de usuarios para ver los cambios.", "success");
          cambiarRolForm.reset();
          usuariosRolForm.style.display = "none";
        } catch (err) {
          showAlert("Error al cambiar rol: " + err.message, "error");
        }
      });
    }

    if (btnCancelarCambioRol) {
      btnCancelarCambioRol.addEventListener("click", () => {
        cambiarRolForm.reset();
        usuariosRolForm.style.display = "none";
      });
    }

    // Estado inicial del panel - asegurar que solo la primera sección esté visible
    // Primero ocultar todas las secciones
    Object.values(dashboardSubtabs).forEach((section) => {
      if (section) {
        section.hidden = true;
        section.style.display = 'none';
      }
    });
    
    // Luego mostrar solo la sección de bodegas (primera)
    const initialSection = dashboardSubtabs.bodegas;
    if (initialSection) {
      initialSection.hidden = false;
      initialSection.style.display = 'block';
    }
    
    // Activar el botón correspondiente
    const initialButton = document.querySelector('[data-subtab="bodegas"]');
    if (initialButton) {
      initialButton.classList.add('tab-btn--active');
      initialButton.setAttribute("aria-selected", "true");
    }
    
    // Llamar a la función para asegurar consistencia
    showDashboardSubtab("bodegas");
  });
  

  function setupDashboardAccess(role, permissions) {
    // Es admin si el rol es ADMIN o si tiene permisos de usuarios
    const isAdmin =
      role === "ADMIN" || permissions.includes("READ_USERS") || permissions.includes("CREATE_USERS");
  
    // permisos relacionados con movimientos
    const movimientoPerms = permissions.filter(p =>
      p.includes("MOVIMIENTO_INVENTARIO")
    );
  
    // Usuario "normal" = tiene solo permisos de movimiento inventario
    const isMovimientoOnly =
      !isAdmin &&
      movimientoPerms.length > 0 &&
      movimientoPerms.length === permissions.length;
  
    // Si es admin → no restringimos nada
    if (isAdmin) {
      return;
    }
  
    // Si es usuario USER (rol USER), permitir acceso a Bodegas, Productos y Movimientos
    // pero ocultar formularios de creación en Bodegas y Productos
    if (role === "USER") {
      // Ocultar elementos marcados como solo para admin (formularios de creación)
      const adminOnlyEls = document.querySelectorAll("[data-admin-only]");
      adminOnlyEls.forEach((el) => {
        el.style.display = "none";
        el.setAttribute("aria-hidden", "true");
      });

      // Actualizar subtítulos para indicar que solo pueden ver
      const bodegasSubtitle = document.getElementById("bodegasSubtitle");
      if (bodegasSubtitle) {
        bodegasSubtitle.textContent = "Listar y consultar bodegas (solo lectura).";
      }
      const productosSubtitle = document.getElementById("productosSubtitle");
      if (productosSubtitle) {
        productosSubtitle.textContent = "Listar y consultar productos (solo lectura).";
      }

      // Ocultar tabs de secciones no permitidas (Auditorías, Usuarios, Reportes)
      const tabButtons = document.querySelectorAll(".tab-btn[data-subtab]");
      tabButtons.forEach((btn) => {
        const target = btn.getAttribute("data-subtab");
        // Permitir solo: bodegas, productos, movimientos
        if (target !== "bodegas" && target !== "productos" && target !== "movimientos") {
          btn.style.display = "none";
        }
      });

      // Mensaje informativo
      showAlert(
        "Estás logueado como usuario USER. Puedes ver Bodegas y Productos (solo lectura), y crear Movimientos de inventario.",
        "info"
      );
      return;
    }
  
    // Si es usuario normal con permisos SOLO de movimientos (caso especial)
    if (isMovimientoOnly) {
      // 1. Ocultar tabs de otras secciones
      const tabButtons = document.querySelectorAll(".tab-btn[data-subtab]");
      tabButtons.forEach((btn) => {
        const target = btn.getAttribute("data-subtab"); // ej: "movimientos"
        if (target !== "movimientos") {
          btn.style.display = "none";
        }
      });

      // 2. Ocultar elementos marcados como solo para admin (si ya los tenías)
      const adminOnlyEls = document.querySelectorAll("[data-admin-only]");
      adminOnlyEls.forEach((el) => {
        el.style.display = "none";
        el.setAttribute("aria-hidden", "true");
      });

      // 3. Mostrar solo la sección de movimientos usando el sistema de navegación
      showDashboardSubtab("movimientos");

      // 4. Mensaje informativo
      showAlert(
        "Estás logueado como usuario con permisos SOLO sobre Movimientos. Las demás secciones están restringidas.",
        "info"
      );
    }
  }
  