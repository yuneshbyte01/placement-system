const API_BASE = "/api";

// ✅ Role-based access check
function checkAccess(requiredRole) {
    const token = localStorage.getItem("jwt");
    const role = localStorage.getItem("role");

    if (!token) {
        alert("Please login first 🔑");
        window.location.href = "login.html";
        return;
    }

    if (role !== requiredRole) {
        alert("Access denied ❌ Redirecting...");
        if (role === "STUDENT") {
            window.location.href = "student.html";
        } else if (role === "ORGANIZATION") {
            window.location.href = "org.html";
        } else {
            window.location.href = "../index.html";
        }
    }
}

// ✅ Logout
function logout() {
    localStorage.removeItem("jwt");
    localStorage.removeItem("role");
    alert("Logged out 👋");
    window.location.href = "../index.html";
}

// ✅ Handle Login (role-based redirect)
const loginForm = document.getElementById("loginForm");
if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        const res = await fetch(`${API_BASE}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password }),
        });

        if (res.ok) {
            const data = await res.json();
            localStorage.setItem("jwt", data.token);
            localStorage.setItem("role", data.role);

            alert("Login successful ✅");

            if (data.role === "STUDENT") {
                window.location.href = "student.html";
            } else if (data.role === "ORGANIZATION") {
                window.location.href = "org.html";
            } else {
                window.location.href = "../index.html";
            }
        } else {
            alert("Login failed ❌");
        }
    });
}

// ✅ Handle Register
const registerForm = document.getElementById("registerForm");
if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const role = document.getElementById("role").value;

        const res = await fetch(`${API_BASE}/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, password, role }),
        });

        if (res.ok) {
            alert("Registration successful 🎉");
            window.location.href = "login.html";
        } else {
            const error = await res.text();
            alert("Registration failed ❌ " + error);
        }
    });
}
