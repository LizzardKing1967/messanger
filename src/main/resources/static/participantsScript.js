// Загрузка списка чатов при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
    loadChats();
});

// Загрузка списка чатов
function loadChats() {
    fetch('/chats/all')
        .then(response => response.json())
        .then(chats => {
            const chatSelector = document.getElementById("chatSelector");
            chatSelector.innerHTML = '<option value="">Выберите чат</option>';
            chats.forEach(chat => {
                const option = document.createElement('option');
                option.value = chat.groupChatName;
                option.textContent = chat.groupChatName;
                chatSelector.appendChild(option);
            });
        })
        .catch(error => console.error('Ошибка при загрузке чатов:', error));
}

// Загрузка участников выбранного чата
document.getElementById("chatSelector").addEventListener('change', function (event) {
    const groupChatName = event.target.value;
    if (groupChatName) {
        loadUsersForAdd(groupChatName);
        loadParticipantsWithFilter(groupChatName); // Загружаем участников при выборе чата
    } else {
        document.getElementById("participantList").innerHTML = '';
        document.getElementById("username").innerHTML = '<option value="">Выберите пользователя</option>';
        document.getElementById("removeUsername").innerHTML = '<option value="">Выберите пользователя</option>';
    }
});

// Загрузка участников чата с фильтрацией
function loadParticipantsWithFilter(groupChatName, role = '', username = '') {
    let url = `/participants/search?groupChatName=${groupChatName}`;
    if (role) {
        url += `&role=${role}`;
    }
    if (username) {
        url += `&username=${username}`;
    }

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при загрузке участников');
            }
            return response.json();
        })
        .then(participants => {
            const participantList = document.getElementById("participantList");
            participantList.innerHTML = participants.map(participant => `
                <div class="user-item">
                    ${participant.id.username} ${participant.creationDate}
                </div>
            `).join('');
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Не удалось загрузить участников чата');
        });
}

// Применение фильтра при изменении роли или вводе имени
document.getElementById("roleFilter").addEventListener('change', function () {
    applyFilters();
});

document.getElementById("searchInput").addEventListener('input', function () {
    applyFilters();
});

function applyFilters() {
    const groupChatName = document.getElementById("chatSelector").value;
    const role = document.getElementById("roleFilter").value;
    const username = document.getElementById("searchInput").value;

    if (groupChatName) {
        loadParticipantsWithFilter(groupChatName, role, username);
    }
}

// Загрузка пользователей для добавления в чат
function loadUsersForAdd(groupChatName) {
    fetch('/users/all')
        .then(response => response.json())
        .then(users => {
            const userSelect = document.getElementById("username");
            userSelect.innerHTML = '<option value="">Выберите пользователя</option>';
            users.forEach(user => {
                const option = document.createElement('option');
                option.value = user.username;
                option.textContent = user.username;
                userSelect.appendChild(option);
            });

            const removeUserSelect = document.getElementById("removeUsername");
            removeUserSelect.innerHTML = '<option value="">Выберите пользователя</option>';
            users.forEach(user => {
                const option = document.createElement('option');
                option.value = user.username;
                option.textContent = user.username;
                removeUserSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Ошибка при загрузке пользователей:', error));
}

// Добавление пользователя в чат
document.getElementById("addUserForm").addEventListener('submit', function (event) {
    event.preventDefault();

    const groupChatName = document.getElementById("chatSelector").value;
    const username = document.getElementById("username").value;
    const role = document.getElementById("role").value;
    const publicKey = document.getElementById("publicKey").value;
    const status = document.getElementById("status").value;

    fetch(`/participants/add?groupChatName=${groupChatName}&username=${username}&role=${role}&publicKey=${publicKey}&status=${status}`, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(message => {
        alert(message);
        loadParticipantsWithFilter(groupChatName); // Обновляем список участников после добавления
    })
    .catch(error => console.error('Ошибка:', error));
});

// Удаление пользователя из чата
document.getElementById("removeUserForm").addEventListener('submit', function (event) {
    event.preventDefault();

    const groupChatName = document.getElementById("chatSelector").value;
    const username = document.getElementById("removeUsername").value;

    fetch(`/participants/remove?groupChatName=${groupChatName}&username=${username}`, {
        method: 'DELETE'
    })
    .then(response => response.text())
    .then(message => {
        alert(message);
        loadParticipantsWithFilter(groupChatName); // Обновляем список участников после удаления
    })
    .catch(error => console.error('Ошибка:', error));
});