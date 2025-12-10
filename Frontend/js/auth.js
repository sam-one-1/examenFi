// js/auth.js

document.addEventListener("DOMContentLoaded", () => {
    const sections = {
      login: document.getElementById("section-login"),
      register: document.getElementById("section-register"),
    };
  
    const tabLogin = document.getElementById("tab-login");
    const tabRegister = document.getElementById("tab-register");
  
    function showSection(name) {
      Object.entries(sections).forEach(([key, el]) => {
        if (!el) return;
        el.classList.toggle("section--active", key === name);
      });
  
      const isLogin = name === "login";
      tabLogin.classList.toggle("tab-btn--active", isLogin);
      tabLogin.setAttribute("aria-selected", String(isLogin));
  
      tabRegister.classList.toggle("tab-btn--active", !isLogin);
      tabRegister.setAttribute("aria-selected", String(!isLogin));
  
      renderAlert(""); // limpiar alertas
    }
  
    // Tabs de la parte superior
    document.querySelectorAll("[data-nav]").forEach((btn) => {
      btn.addEventListener("click", () => {
        const target = btn.getAttribute("data-nav");
        if (target === "login" || target === "register") {
          showSection(target);
        }
      });
    });
  
    tabLogin.addEventListener("click", () => showSection("login"));
    tabRegister.addEventListener("click", () => showSection("register"));
  
    // LOGIN
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
      loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const documento = document.getElementById("loginDocumento").value.trim();
        const password = document.getElementById("loginPassword").value.trim();
  
        if (!documento || !password) {
          renderAlert("Por favor, completa documento y contraseña.", "error");
          return;
        }
  
        try {
          const tokenResp = await apiFetch("/auth/login", {
            method: "POST",
            body: JSON.stringify({ documento, password }),
          });
  
          const jwt =
            typeof tokenResp === "string"
              ? tokenResp
              : tokenResp.token || tokenResp.jwt || "";
          if (!jwt) {
            throw new Error("La API no devolvió un token válido.");
          }
  
          setToken(jwt, documento);
          renderAlert("Login exitoso. Redirigiendo al panel...", "success");
          setTimeout(() => {
            window.location.href = "dashboard.html";
          }, 600);
        } catch (err) {
          renderAlert("Error al iniciar sesión: " + err.message, "error");
        }
      });
    }
  
    // REGISTER
    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
      registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const nombre = document.getElementById("regNombre").value.trim();
        const apellido = document.getElementById("regApellido").value.trim();
        const documento = document.getElementById("regDocumento").value.trim();
        const password = document.getElementById("regPassword").value.trim();

        if (!nombre || !apellido || !documento || !password) {
          renderAlert("Todos los campos son obligatorios.", "error");
          return;
        }

        // Solo enviamos nombre (combinando nombre y apellido)
        // El backend asignará automáticamente el rol USER
        const payload = {
          nombre: `${nombre} ${apellido}`.trim(),
          documento,
          password,
        };
  
        try {
          await apiFetch("/auth/register", {
            method: "POST",
            body: JSON.stringify(payload),
          });
          renderAlert(
            "Usuario registrado correctamente. Ahora podés iniciar sesión.",
            "success"
          );
          showSection("login");
        } catch (err) {
          renderAlert("Error al registrar usuario: " + err.message, "error");
        }
      });
    }
  
    // Si viene con ?auth=required, mostrar mensaje
    if (window.location.search.includes("auth=required")) {
      renderAlert("Necesitás iniciar sesión para acceder al panel.", "error");
    }
  
    // Estado inicial
    showSection("login");
  });
  