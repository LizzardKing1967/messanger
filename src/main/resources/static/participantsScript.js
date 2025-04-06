 // Загрузка списка чатов при загрузке страницы
    document.addEventListener('DOMContentLoaded', () => {
        loadChats();
        loadAllUsers();

        // Обработчик для кнопки показа/скрытия панели добавления
        document.getElementById('toggleAddPanel').addEventListener('click', function() {
            const panel = document.getElementById('addUserPanel');
            panel.style.display = panel.style.display === 'block' ? 'none' : 'block';
        });
    });

    // Загрузка списка чатов
    function loadChats() {
        fetch('/chats/all')
            .then(response => response.json())
            .then(chats => {
                const chatSelector = document.getElementById("chatSelector");
                chatSelector.innerHTML = '';
                chats.forEach(chat => {
                    const option = document.createElement('option');
                    option.value = chat.groupChatName;
                    option.textContent = chat.groupChatName;
                    chatSelector.appendChild(option);
                });

                // Если есть чаты, загружаем участников первого чата
                if (chats.length > 0) {
                    loadParticipantsWithFilter(chats[0].groupChatName);
                }
            })
            .catch(error => console.error('Ошибка при загрузке чатов:', error));
    }

    // Загрузка всех пользователей для формы добавления
    function loadAllUsers() {
        fetch('/users/all')
            .then(response => response.json())
            .then(users => {
                const userSelect = document.getElementById("username");
                userSelect.innerHTML = ''; // Убираем стандартное сообщение
                users.forEach(user => {
                    const option = document.createElement('option');
                    option.value = user.username;
                    option.textContent = user.username;
                    userSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Ошибка при загрузке пользователей:', error));
    }

    // Загрузка участников выбранного чата
    document.getElementById("chatSelector").addEventListener('change', function (event) {
        const groupChatName = event.target.value;
        if (groupChatName) {
            loadParticipantsWithFilter(groupChatName);
        } else {
            document.getElementById("participantList").innerHTML = '';
        }
    });

    // Загрузка участников чата с фильтрацией
    function loadParticipantsWithFilter(groupChatName, filters = {}) {
        let url = `/participants/search?groupChatName=${groupChatName}`;

        // Добавляем параметры фильтрации
        if (filters.role) url += `&role=${filters.role}`;
        if (filters.login) url += `&username=${filters.login}`;
        if (filters.email) url += `&email=${filters.email}`;
        if (filters.date) url += `&date=${filters.date}`;

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
                        <strong>Логин:</strong> ${participant.id.username}<br>
                        <strong>Роль:</strong> ${participant.id.role_name}<br>
                        <div class="delete-popup" onclick="confirmRemoveUser('${groupChatName}', '${participant.id.username}', event)">
                            Удалить
                        </div>
                    </div>
                `).join('');
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Не удалось загрузить участников чата');
            });
    }

    // Подтверждение удаления пользователя
    function confirmRemoveUser(groupChatName, username, event) {
        event.stopPropagation(); // Останавливаем всплытие события

        if (confirm(`Вы уверены, что хотите удалить пользователя ${username} из чата?`)) {
            removeUser(groupChatName, username);
        }
    }

    // Удаление пользователя
    function removeUser(groupChatName, username) {
        fetch(`/participants/remove?groupChatName=${groupChatName}&username=${username}`, {
            method: 'DELETE'
        })
        .then(response => response.text())
        .then(message => {
            alert(message);
            loadParticipantsWithFilter(groupChatName); // Обновляем список участников после удаления
        })
        .catch(error => console.error('Ошибка:', error));
    }

    // Применение фильтров при изменении значений
    document.getElementById("roleFilter").addEventListener('change', applyFilters);
    document.getElementById("searchLogin").addEventListener('input', applyFilters);

    function applyFilters() {
        const groupChatName = document.getElementById("chatSelector").value;
        if (!groupChatName) return;

        const filters = {
            role: document.getElementById("roleFilter").value,
            login: document.getElementById("searchLogin").value,
        };

        loadParticipantsWithFilter(groupChatName, filters);
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
            document.getElementById("addUserPanel").style.display = 'none'; // Скрываем панель после добавления
            loadParticipantsWithFilter(groupChatName); // Обновляем список участников после добавления
        })
        .catch(error => console.error('Ошибка:', error));
    });