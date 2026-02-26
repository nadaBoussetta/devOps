/**
 * Script pour la page d'authentification.
 */

document.addEventListener('DOMContentLoaded', () => {
    // Gestion des onglets
    const tabButtons = document.querySelectorAll('.tab-btn');
    const authForms = document.querySelectorAll('.auth-form');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tab = button.getAttribute('data-tab');
            
            tabButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');

            authForms.forEach(form => form.classList.remove('active'));
            document.getElementById(`${tab}-form`).classList.add('active');
        });
    });

    // Gestion de l'inscription
    const registerForm = document.getElementById('register');
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username = document.getElementById('register-username').value;
        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;
        const messageDiv = document.getElementById('register-message');

        try {
            await AuthAPI.register(username, email, password);
            messageDiv.className = 'message success';
            messageDiv.textContent = 'Inscription réussie ! Vous pouvez maintenant vous connecter.';
            registerForm.reset();

            setTimeout(() => {
                document.querySelector('.tab-btn[data-tab="login"]').click();
            }, 2000);
        } catch (error) {
            messageDiv.className = 'message error';
            messageDiv.textContent = error.message;
        }
    });

    // Gestion de la connexion
    const loginForm = document.getElementById('login');
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;
        const messageDiv = document.getElementById('login-message');

        try {
            await AuthAPI.login(username, password);
            messageDiv.className = 'message success';
            messageDiv.textContent = 'Connexion réussie ! Redirection...';

            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1000);
        } catch (error) {
            messageDiv.className = 'message error';
            messageDiv.textContent = 'Identifiants invalides';
        }
    });

    document.querySelectorAll(".toggle-password").forEach(button => {
        button.addEventListener("click", () => {
            const inputId = button.getAttribute("data-target");
            const input = document.getElementById(inputId);
            const icon = button.querySelector("i");

            if (input.type === "password") {
                input.type = "text";
                icon.classList.replace("fa-eye", "fa-eye-slash");
            } else {
                input.type = "password";
                icon.classList.replace("fa-eye-slash", "fa-eye");
            }
        });
    });
});