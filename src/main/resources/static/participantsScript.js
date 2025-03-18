document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
    loadChats();
});

// Загрузка списка пользователей
function loadUsers() {
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
        })
        .catch(error => console.error('Ошибка при загрузке пользователей:', error));
}

// Загрузка списка чатов
function loadChats() {
    fetch('/chats/all')
        .then(response => response.json())
        .then(chats => {
            const chatSelect = document.getElementById("groupChatName");
            chatSelect.innerHTML = '<option value="">Выберите чат</option>';
            chats.forEach(chat => {
                const option = document.createElement('option');
                option.value = chat.groupChatName;
                option.textContent = chat.groupChatName;
                chatSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Ошибка при загрузке чатов:', error));
}

// Добавление пользователя
document.getElementById("addUserForm").addEventListener('submit', function (event) {
    event.preventDefault();

    const groupChatName = document.getElementById("groupChatName").value;
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
    })
    .catch(error => console.error('Ошибка:', error));
});


// Удаление пользователя
document.getElementById("removeUserForm").addEventListener('submit', function (event) {
    event.preventDefault();

    const groupChatName = document.getElementById("removeGroupChatName").value;
    const username = document.getElementById("removeUsername").value;

    fetch(`/participants/remove?groupChatName=${groupChatName}&username=${username}`, {
        method: 'DELETE'
    })
    .then(response => response.text())
    .then(message => {
        alert(message);
    })
    .catch(error => console.error('Ошибка:', error));
});

// Поиск пользователей
document.getElementById("searchInput").addEventListener('input', function (event) {
    const username = event.target.value;

    if (username.length >= 2) {
        fetch(`/participants/search?username=${username}`)
            .then(response => response.json())
            .then(users => {
                const searchResults = document.getElementById("searchResults");
                searchResults.innerHTML = users.map(user => `
                    <div class="user-item">
                        ${user.id.username} (${user.id.groupChatName})
                    </div>
                `).join('');
            })
            .catch(error => console.error('Ошибка:', error));
    } else {
        document.getElementById("searchResults").innerHTML = '';
    }
});

// Загрузка всех участников чата
document.getElementById("loadParticipantsButton").addEventListener('click', function () {
    const groupChatName = document.getElementById("chatNameInput").value;

    fetch(`/participants/all?groupChatName=${groupChatName}`)
        .then(response => response.json())
        .then(participants => {
            const participantList = document.getElementById("participantList");
            participantList.innerHTML = participants.map(participant => `
                <div class="user-item">
                    ${participant.id.username} (${participant.id.groupChatName})
                </div>
            `).join('');
        })
        .catch(error => console.error('Ошибка:', error));
});