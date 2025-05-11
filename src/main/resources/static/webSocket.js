let currentChat = null;
let stompClient = null;


document.addEventListener("DOMContentLoaded", () => {
    fetch("/me")
        .then(response => {
            if (!response.ok) throw new Error("Ошибка при получении имени пользователя");
            return response.text();
        })
        .then(username => {
            connectWebSocket(username);
        })
        .catch(error => console.error("Ошибка WebSocket инициализации:", error));
});

window.sendWebSocketMessage = function(payload) {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(payload));
    } else {
        console.error("WebSocket не подключен.");
    }
};



function handleIncomingMessage(msg) {
    const chatName = msg.groupChatName;
    const activeChat = document.getElementById('chatMessagesTitle')?.textContent.replace("Чат: ", "");

    if (chatName === activeChat) {
        const encryptedData = JSON.parse(msg.encryptedContent);
        getCurrentUsername().then(async username => {
            const aesKey = await decryptChatAesKey(await fetchChatEncryptedAesKey(chatName), username);
            const messageElement = await createMessageElement(msg, aesKey);
            document.getElementById('chatMessagesContainer').appendChild(messageElement);
        });
    }
}

function connectWebSocket(username) {
     const socket = new SockJS('/ws');
     stompClient = Stomp.over(socket);

     stompClient.connect({}, () => {
         console.log(`WebSocket подключен для ${username}`);

         // Подписка на обновления чатов
         stompClient.subscribe('/user/queue/chats', (message) => {
             const chat = JSON.parse(message.body);
             console.log("Получен новый чат:", chat);
             addChatItem(chat);
         });

         // Подписка на новые сообщения
         stompClient.subscribe('/user/queue/messages', (message) => {
             const msg = JSON.parse(message.body);
             console.log("Получено новое сообщение:", msg);
             handleIncomingMessage(msg);
         });
     }, (error) => {
         console.error("Ошибка подключения WebSocket:", error);
         setTimeout(() => connectWebSocket(username), 5000); // Переподключение через 5 сек
     });
 }

function addChatItem(chat) {
    const chatList = document.getElementById("chatList");

    const chatItem = document.createElement("div");
    chatItem.className = "chat-item";

    chatItem.innerHTML = `
        <div class="chat-title">${chat.name}${chat.groupType ? ' (группа)' : ''}</div>
        <div class="chat-last-message">
            <span class="author">${chat.lastMessageSender || "Нет сообщений"}:</span>
            <span class="message">${chat.lastMessage || ""}</span>
        </div>
    `;

    // Вставляем в начало, чтобы новый чат был сверху
    chatList.prepend(chatItem);
}