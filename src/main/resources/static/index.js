const chatSubscriptions = {};

function switchTab(tabId) {
  document.querySelectorAll('.tab-content').forEach(tab => {
    tab.classList.add('hidden');
  });
  document.getElementById(tabId).classList.remove('hidden');
}

async function logout() {
    try {
        const response = await fetch('/auth/logout', {
            method: 'POST',
            credentials: 'include' // Для отправки куки
        });

        if (response.ok) {
            window.location.href = '/auth.html';
            localStorage.clear();
        } else {
            console.error('Logout failed');
        }
    } catch (error) {
        console.error('Error during logout:', error);
    }
}

function switchTab(tabName) {
  document.querySelectorAll(".tab-content").forEach(section => section.classList.add("hidden"));
  document.getElementById(tabName).classList.remove("hidden");
}

function logout() {
  fetch("/auth/logout", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
    credentials: "include" // если используешь куки
  })
    .then(response => {
      if (response.ok) {
        localStorage.removeItem("token");
        window.location.href = "/auth";
      } else {
        alert("Ошибка при выходе из аккаунта");
      }
    })
    .catch(error => {
      console.error("Ошибка:", error);
      alert("Произошла ошибка");
    });
}
    // Заглушка: получить друзей

async function fetchFriends() {
  const response = await fetch("/api/friends", {
    headers: { "Authorization": `Bearer ${localStorage.getItem("token")}` }
  });
  const friends = await response.json();
  renderUserList(friends, "friendList");
}

function renderUserList(users, containerId) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";

  if (!users.length) {
    container.innerHTML = "<li>Ничего не найдено</li>";
    return;
  }

  users.forEach(user => {
    const li = document.createElement("li");
    li.innerHTML = `
      <span>${user.name} ${user.lastName} (${user.username}) - ${user.email}</span>
      ${containerId === "searchResults" ? `<button onclick="addFriend('${user.id}')">Добавить</button>` : ""}
    `;
    container.appendChild(li);
  });
}

    // Поиск пользователей
async function searchUsers() {
  const query = document.getElementById("searchInput").value;
  const onlineOnly = document.getElementById("onlineOnly").checked;

  const params = new URLSearchParams({ query, onlineOnly });
  const response = await fetch(`/api/users/search?${params}`, {
    headers: { "Authorization": `Bearer ${localStorage.getItem("token")}` }
  });

  const results = await response.json();
  renderUserList(results, "searchResults");
}

async function addFriend(userId) {
  const response = await fetch("/api/friends/invite", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${localStorage.getItem("token")}`
    },
    body: JSON.stringify({ userId })
  });

  const result = await response.json();
  alert(result.message || "Пользователь приглашён в друзья");
  searchUsers();
}


document.addEventListener("DOMContentLoaded", () => {
  fetchUserChats(); // Загружаем чаты при старте
 // fetchFriends();   // Твой текущий вызов
});



async function fetchUserChats() {
    try {
        const response = await fetch('/chats/myChats');
        const chats = await response.json();
        const chatList = document.getElementById("chatList");
        chatList.innerHTML = "";

        if (chats.length === 0) {
            chatList.innerHTML = "<p>У вас пока нет чатов</p>";
            return;
        }

        const username = await getCurrentUsername();

        for (const chat of chats) {
            const chatItem = document.createElement("div");
            chatItem.className = "chat-item";

            chatItem.addEventListener("click", () => {
                openChatMessagesModal(chat.name);
            });

            // Попробуем расшифровать lastMessage, если он есть
            let decryptedLastMessage = "";
            if (chat.lastMessage != null) {
                try {
                    const encryptedData = JSON.parse(chat.lastMessage);
                    const encryptedKey = await fetchChatEncryptedAesKey(chat.name, username);
                    const aesKey = await decryptChatAesKey(encryptedKey, username);
                    decryptedLastMessage = await decryptMessage(
                        encryptedData.ciphertext,
                        encryptedData.iv,
                        aesKey
                    );
                } catch (e) {
                    console.warn(`Не удалось расшифровать сообщение для чата "${chat.name}":`, e);
                    decryptedLastMessage = "Не удалось расшифровать";
                }
            }

            chatItem.innerHTML = `
            <div class="chat-title">${chat.name}${chat.groupType ? ' (группа)' : ''}</div>
            <div class="chat-last-message">
                <span class="author">${chat.lastMessageSender || "Нет сообщений"}:</span>
                <span class="message">${escapeHtml(decryptedLastMessage)}</span>
            </div>
        `;

            chatList.appendChild(chatItem);
        }

    } catch (err) {
        console.error("Ошибка при загрузке чатов:", err);
    }
}

window.updateLastMessagePreview = async function updateLastMessagePreview(msg) {
    try {
        const chatName = msg.groupChatName;
        const encryptedData = JSON.parse(msg.encryptedContent);
        const username = window.currentUser || await getCurrentUsername();
        const encryptedKey = await fetchChatEncryptedAesKey(chatName, username);
        const aesKey = await decryptChatAesKey(encryptedKey, username);
        const decryptedMessage = await decryptMessage(encryptedData.ciphertext, encryptedData.iv, aesKey);

        // Найдём нужный элемент
        const chatItems = document.querySelectorAll('.chat-item');
        for (const item of chatItems) {
            const title = item.querySelector('.chat-title')?.textContent.trim();
            if (title?.startsWith(chatName)) {
                const authorEl = item.querySelector('.chat-last-message .author');
                const msgEl = item.querySelector('.chat-last-message .message');
                if (authorEl) authorEl.textContent = msg.senderUsername + ":";
                if (msgEl) msgEl.textContent = decryptedMessage;
                break;
            }
        }

    } catch (e) {
        console.error("Ошибка обновления превью сообщения:", e);
    }
}

let selectedUsernames = new Set();


function openCreateChatModal() {
    document.getElementById('createChatModal').style.display = 'flex';
    fetchUsers();
}

function closeCreateChatModal() {
    document.getElementById('createChatModal').style.display = 'none';  // Скрываем модальное окно
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('closeModal').addEventListener('click', closeCreateChatModal);
});

function fetchUsers() {
    fetch("/users/allExceptCurrent")
    .then(response => response.json())
    .then(users => {
        const userList = document.getElementById("userList");
        userList.innerHTML = "";

        users.forEach(user => {
            const div = document.createElement("div");
            div.textContent = user.username;
            div.dataset.username = user.username;

            div.onclick = () => {
                const username = div.dataset.username;
                if (selectedUsernames.has(username)) {
                    selectedUsernames.delete(username);
                    div.classList.remove("selected");
                } else {
                    selectedUsernames.add(username);
                    div.classList.add("selected");
                }
            };

            userList.appendChild(div);
        });
    })
    .catch(err => {
        console.error("Ошибка при загрузке пользователей:", err);
        alert("Не удалось загрузить список пользователей");
    });
}

async function getCurrentUsername() {
    const response = await fetch("/me", {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
        }
    });
    if (!response.ok) throw new Error("Не удалось получить имя пользователя");
    return await response.text();
}

document.addEventListener("DOMContentLoaded", async () => {
    try {
        const username = await getCurrentUsername();
        const profileBtn = document.getElementById("profileBtn");
        if (profileBtn) {
            profileBtn.textContent = username;
        }
    } catch (error) {
        console.error("Ошибка при получении имени пользователя:", error);
    }
});

async function createChat(event) {
    event.preventDefault();

    const chatName = document.getElementById("chatName").value;
    const selectedUsers = Array.from(selectedUsernames); // Исправление

    const creatorUsername = await getCurrentUsername();

    if (!selectedUsers.includes(creatorUsername)) {
        selectedUsers.push(creatorUsername);
    }

    const aesKey = await window.crypto.subtle.generateKey(
        { name: "AES-GCM", length: 256 },
        true,
        ["encrypt", "decrypt"]
    );

    await saveAesKeyToIndexedDB(chatName, aesKey);

    const encryptedKeys = {};
    const rawAesKey = await crypto.subtle.exportKey("raw", aesKey);

    for (const username of selectedUsers) {
        const response = await fetch(`/keys/${username}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`
            }
        });

        if (!response.ok) {
            throw new Error(`Не удалось получить ключ пользователя ${username}`);
        }

        const base64Key = await response.text();
        const publicKey = await importRsaPublicKey(base64Key, "RSA-OAEP");

        const encrypted = await crypto.subtle.encrypt(
            { name: "RSA-OAEP" },
            publicKey,
            rawAesKey
        );

        encryptedKeys[username] = btoa(String.fromCharCode(...new Uint8Array(encrypted)));
    }

    const chatData = {
        chatName: chatName,
        encryptedKeys
    };

    const response = await fetch("/chats/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`
        },
        body: JSON.stringify(chatData)
    });

    if (response.ok) {
        alert("Чат успешно создан!");
        toggleCreateChatForm();
    } else {
        const error = await response.text();
        alert("Ошибка: " + error);
    }
    closeCreateChatModal();
}



function openChatMessagesModal(chatName) {
    window.currentChat = chatName;
    document.getElementById('chatMessagesTitle').textContent = `Чат: ${chatName}`;
    document.getElementById('chatMessagesModal').style.display = 'block';
    document.getElementById('modalOverlay').style.display = 'block';
    loadChatMessages(chatName);
}

function closeChatMessagesModal() {
    document.getElementById('chatMessagesModal').style.display = 'none';
    document.getElementById('modalOverlay').style.display = 'none';
    document.getElementById('chatMessagesContainer').innerHTML = '';
}

async function sendMessage(event) {
    event.preventDefault();

    const messageInput = document.getElementById('messageInput');
    const messageText = messageInput.value.trim();
    if (!messageText) return;

    const chatTitle = document.getElementById('chatMessagesTitle').textContent.replace('Чат: ', '');
    const chatName = chatTitle;

    try {
        const username = await getCurrentUsername();
        const encryptedAesKey = await fetchChatEncryptedAesKey(chatName, username);
        const aesKey = await decryptChatAesKey(encryptedAesKey, username);
        const { ciphertextBase64, ivBase64 } = await encryptMessage(messageText, aesKey);

        const payload = {
            groupChatName: chatName,
            senderUsername: username,
            encryptedContent: JSON.stringify({
                ciphertext: ciphertextBase64,
                iv: ivBase64
            })
        };

        sendWebSocketMessage(payload);
        messageInput.value = '';

    } catch (error) {
        console.error('Ошибка при отправке сообщения:', error);
        alert('Не удалось отправить сообщение');
    }
}

async function fetchChatEncryptedAesKey(chatName) {
const response = await fetch(`/keys/getEncryptedChatKey/${chatName}`, {
    headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
});

    if (!response.ok) {
        throw new Error('Не удалось получить ключ чата');
    }

    return await response.text();
}

    async function loadChatMessages(chatName) {
        try {
            const username = await getCurrentUsername();
            const container = document.getElementById('chatMessagesContainer');
            container.innerHTML = '<div class="loading">Загрузка сообщений...</div>';

            // 1. Получаем зашифрованный AES-ключ чата
            const encryptedAesKey = await fetchChatEncryptedAesKey(chatName);

            // 2. Дешифруем AES-ключ чата своим приватным RSA-ключом
            const aesKey = await decryptChatAesKey(encryptedAesKey, username);

            // 3. Сохраняем ключ в IndexedDB для этого чата
            await saveAesKeyToIndexedDB(chatName, aesKey);

            // 4. Получаем историю сообщений
            const response = await fetch(`/messages/${chatName}/history`);
            if (!response.ok) throw new Error('Ошибка загрузки сообщений');

            const messages = await response.json();
            container.innerHTML = '';

            // 5. Дешифруем и отображаем каждое сообщение
            for (const msg of messages) {
                try {
                    // Десериализуем JSON внутри encryptedContent
                    //msg.encryptedContent = JSON.parse(msg.encryptedContent);

                    const messageElement = await createMessageElement(msg, aesKey);
                    container.appendChild(messageElement);
                } catch (e) {
                    console.error('Ошибка дешифровки сообщения:', e);
                    container.appendChild(createErrorMessageElement(msg));
                }
            }
        } catch (error) {
            console.error('Ошибка загрузки чата:', error);
            container.innerHTML = '<div class="error">Ошибка загрузки сообщений</div>';
        }
    }

async function createMessageElement(msg, aesKey) {
    const encryptedData = JSON.parse(msg.encryptedContent);

    const decryptedText = await decryptMessage(
        encryptedData.ciphertext,
        encryptedData.iv,
        aesKey
    );

    // Создание контейнера для одного сообщения
    const messageItem = document.createElement('div');
    messageItem.className = 'message-item';

    // Имя отправителя
    const senderEl = document.createElement('div');
    senderEl.className = 'message-sender';
    senderEl.textContent = msg.senderUsername;

    // Текст сообщения
    const textEl = document.createElement('div');
    textEl.className = 'message-text';
    textEl.textContent = decryptedText;

    // Время отправки
    const timeEl = document.createElement('div');
    timeEl.className = 'message-time';
    timeEl.textContent = formatDateTime(msg.sendDate);

    // Добавление элементов в messageItem
    messageItem.appendChild(senderEl);
    messageItem.appendChild(textEl);
    messageItem.appendChild(timeEl);

    // Разделитель между сообщениями
    const divider = document.createElement('div');
    divider.className = 'message-divider';

    // Обёртка, если нужно вставлять оба элемента (сообщение и разделитель)
    const wrapper = document.createDocumentFragment();
    wrapper.appendChild(messageItem);
    wrapper.appendChild(divider);

    return wrapper;
}

function createErrorMessageElement(msg) {
    const element = document.createElement('div');
    element.className = 'message error';
    element.innerHTML = `
        <div class="message-header">
            <span class="sender">${msg.username}</span>
            <span class="time">${formatDateTime(msg.sentAt)}</span>
        </div>
        <div class="message-content">Не удалось расшифровать сообщение</div>
    `;
    return element;
}

function formatDateTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString();
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}