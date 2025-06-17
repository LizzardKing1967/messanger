
const secureDB = new window.DBManager("SecureChatDB", 3, [
    { name: "keys", keyPath: "username" }
]);

const chatKeysDB = new window.DBManager("ChatKeysDB", 2, [
    { name: "aesKeys", keyPath: "chatName" }
]);



window.generateAndStoreKeys = async function generateAndStoreKeys(username) {
    const keyPair = await window.crypto.subtle.generateKey(
        {
            name: "RSA-OAEP",
            modulusLength: 2048,
            publicExponent: new Uint8Array([1, 0, 1]),
            hash: "SHA-256"
        },
        true,
        ["encrypt", "decrypt"]
    );

    const publicKeyDer = await window.crypto.subtle.exportKey("spki", keyPair.publicKey);
    const privateKey = keyPair.privateKey;

    const publicKeyBase64 = arrayBufferToBase64(publicKeyDer);

    // Сохраняем приватный ключ в IndexedDB
    await storePrivateKeyInIndexedDB(username, privateKey);

    return {
        publicKeyBase64,
        privateKey
    };
}

window.saveAesKeyToIndexedDB = async function saveAesKeyToIndexedDB(chatName, aesKey) {
    if (!aesKey.extractable) {
        throw new Error('AES ключ должен быть extractable для хранения');
    }

    const raw = await crypto.subtle.exportKey("raw", aesKey);
    const exportedKeyBase64 = arrayBufferToBase64(raw);

    await chatKeysDB.put("aesKeys", { chatName, exportedKeyBase64 });
};

async function storePrivateKeyInIndexedDB(username, privateKey) {
    await secureDB.put("keys", { username, privateKey });
}

function arrayBufferToBase64(buffer) {
    const bytes = new Uint8Array(buffer);
    let binary = '';
    bytes.forEach(b => binary += String.fromCharCode(b));
    return window.btoa(binary);
}

function base64ToArrayBuffer(base64) {
  const binary = window.atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
}



window.importRsaPublicKey = async function importRsaPublicKey(base64, algorithm) {
  const binaryDer = base64ToArrayBuffer(base64);
  return window.crypto.subtle.importKey(
    "spki",
    binaryDer,
    { name: algorithm, hash: "SHA-256" },
    true,
    ["encrypt"]
  );
}

async function getPrivateKeyFromIndexedDB(username) {
    const record = await secureDB.get("keys", username);
    if (!record || !record.privateKey) {
        throw new Error("Private key not found for user: " + username);
    }
    return record.privateKey;
}

window.decryptMessage = async function decryptMessage(ciphertextBase64, ivBase64, aesKey) {
    const ciphertext = base64ToArrayBuffer(ciphertextBase64);
    const iv = base64ToArrayBuffer(ivBase64);

    const decrypted = await crypto.subtle.decrypt(
        {
            name: "AES-GCM",
            iv: iv
        },
        aesKey,
        ciphertext
    );

    return new TextDecoder().decode(decrypted);
}

window.getAesKeyFromIndexedDB = async function getAesKeyFromIndexedDB(chatName) {
    const record = await chatKeysDB.get("aesKeys", chatName);
    if (!record) throw new Error("AES key not found for chat: " + chatName);

    const keyBuffer = base64ToArrayBuffer(record.exportedKeyBase64);
    return await crypto.subtle.importKey("raw", keyBuffer, { name: "AES-GCM" }, true, ["decrypt"]);
};


window.decryptChatAesKey = async function decryptChatAesKey(encryptedKeyBase64, username) {
    const encryptedKeyBuffer = base64ToArrayBuffer(encryptedKeyBase64);

    const privateKey = await getPrivateKeyFromIndexedDB(username);
    if (!privateKey) throw new Error("Private key not found");

    const aesKeyRaw = await window.crypto.subtle.decrypt(
        {
            name: "RSA-OAEP"
        },
        privateKey,
        encryptedKeyBuffer
    );

    return await window.crypto.subtle.importKey(
        "raw",
        aesKeyRaw,
        { name: "AES-GCM" },
        true,
        ["encrypt", "decrypt"]
    );
}

window.encryptMessage = async function encryptMessage(messageText, aesKey) {
    const iv = window.crypto.getRandomValues(new Uint8Array(12));
    const encodedMessage = new TextEncoder().encode(messageText);

    const ciphertext = await crypto.subtle.encrypt(
        {
            name: "AES-GCM",
            iv: iv
        },
        aesKey,
        encodedMessage
    );

    return {
        ciphertextBase64: arrayBufferToBase64(ciphertext),
        ivBase64: arrayBufferToBase64(iv)
    };
}