
let isLogin = true;

function toggleForm() {
    isLogin = !isLogin;
    document.getElementById("form-title").innerText = isLogin ? "Авторизация" : "Регистрация";
    document.querySelector("button").innerText = isLogin ? "Войти" : "Зарегистрироваться";
    document.querySelector(".switch-link").innerText = isLogin ? "Нет аккаунта? Зарегистрироваться" : "Уже есть аккаунт? Войти";
    document.getElementById("register-fields").style.display = isLogin ? "none" : "block";
    document.getElementById("message").innerText = "";
}


async function submitForm() {
    clearFieldErrors();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const email = isLogin ? null : document.getElementById("email").value;
    const firstName = isLogin ? null : document.getElementById("first-name").value;
    const lastName = isLogin ? null : document.getElementById("last-name").value;

    if (!username || !password || (!isLogin && (!email || !firstName || !lastName))) {
        showError("Пожалуйста, заполните все обязательные поля");
        return;
    }

    const endpoint = isLogin ? "/auth/authenticate" : "/auth/register";

    let rsaPublicKeyBase64 = null;

    if (!isLogin) {
        const { publicKeyBase64, privateKey } = await generateAndStoreKeys(username);
        rsaPublicKeyBase64 = publicKeyBase64;
    }

    const payload = isLogin
        ? { username, password }
        : {
            username,
            email,
            password,
            name: firstName,
            lastName,
            rsaPublicKey: rsaPublicKeyBase64
        };

    try {
        const response = await fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload),
            credentials: 'include'
        });

        const data = await response.json();

        if (response.ok) {
            document.getElementById("message").className = "message success";
            document.getElementById("message").innerText = isLogin
                ? "Успешный вход!"
                : "Регистрация успешна! Перенаправление...";

            if (isLogin) {
                if (data.token) {
                    localStorage.setItem('token', data.token);
                }
                window.location.href = "/index";
            } else {
                setTimeout(() => toggleForm(), 1500);
            }

        } else if (response.status === 400) {
            showFieldErrors(data);
        } else {
            showError(data.error || "Произошла ошибка.");
        }

    } catch (error) {
        showError("Ошибка сети. Попробуйте снова.");
    }
}

function showFieldErrors(errors) {
    Object.entries(errors).forEach(([field, message]) => {
        const errorElement = document.getElementById(`error-${field}`);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    });
        setTimeout(() => {
            clearFieldErrors();
        }, 5000);
}

function clearFieldErrors() {
    const errorElements = document.querySelectorAll(".error-field");
    errorElements.forEach(elem => {
        elem.textContent = "";
        elem.style.display = "none";
    });
}
function showError(message) {
    const errorElement = document.getElementById('error-message');
    errorElement.textContent = message;
    errorElement.style.display = 'block';

    setTimeout(() => {
        errorElement.style.display = 'none';
    }, 5000);
}