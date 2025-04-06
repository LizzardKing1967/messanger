 function createUser() {
        const username = document.getElementById("username").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        const publicKey = document.getElementById("publicKey").value;

        fetch(`/users/create?username=${username}&email=${email}&password=${password}&publicKey=${publicKey}&status=1`, { method: 'POST' })
            .then(response => response.text())
            .then(alert);
    }

    function createGroupChat() {
        const groupChatName = document.getElementById("groupChatName").value;
        const publicKey = document.getElementById("groupPublicKey").value;

        fetch(`/chats/create?groupChatName=${groupChatName}&publicKey=${publicKey}`, { method: 'POST' })
            .then(response => response.text())
            .then(alert);
    }

    function loadUsers() {
        fetch('/users/all')
            .then(response => response.json())
            .then(users => {
                const userList = document.getElementById("userList");
                userList.innerHTML = users.map(user => `<div>${user.username} - ${user.email}</div>`).join('');
            });
    }

    function loadChats() {
        fetch('/chats/all')
            .then(response => response.json())
            .then(chats => {
                const chatList = document.getElementById("chatList");
                chatList.innerHTML = chats.map(chat => `<div>${chat.groupChatName}</div>`).join('');
            });
    }