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
           try {
               // Проверяем, можно ли экспортировать ключ
               if (!aesKey.extractable) {
                   throw new Error('Ключ не может быть экспортирован (extractable: false)');
               }

               // Экспортируем ключ
               const exportedKey = await crypto.subtle.exportKey("raw", aesKey);
               const exportedKeyBuffer = new Uint8Array(exportedKey);
               const exportedKeyBase64 = btoa(String.fromCharCode(...exportedKeyBuffer));

               // Сохраняем в IndexedDB
               return new Promise((resolve, reject) => {
                   const request = indexedDB.open("ChatKeysDB", 1);

                   request.onupgradeneeded = (event) => {
                       const db = event.target.result;
                       if (!db.objectStoreNames.contains("keys")) {
                           db.createObjectStore("keys");
                       }
                   };

                   request.onsuccess = (event) => {
                       const db = event.target.result;
                       const tx = db.transaction("keys", "readwrite");
                       const store = tx.objectStore("keys");

                       store.put(exportedKeyBase64, chatName);

                       tx.oncomplete = () => resolve();
                       tx.onerror = () => reject(tx.error);
                   };

                   request.onerror = () => reject(request.error);
               });
           } catch (error) {
               console.error('Ошибка сохранения ключа:', error);
               throw error;
           }
       }

function storePrivateKeyInIndexedDB(username, privateKey) {
    return new Promise((resolve, reject) => {
        const request = indexedDB.open("SecureChatDB", 1);

        request.onupgradeneeded = function (event) {
            const db = event.target.result;
            if (!db.objectStoreNames.contains("keys")) {
                db.createObjectStore("keys", { keyPath: "username" });
            }
        };

        request.onsuccess = function (event) {
            const db = event.target.result;
            const transaction = db.transaction(["keys"], "readwrite");
            const store = transaction.objectStore("keys");

            store.put({ username, privateKey });

            transaction.oncomplete = () => resolve();
            transaction.onerror = () => reject(transaction.error);
        };

        request.onerror = function () {
            reject(request.error);
        };
    });
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
    console.log("[getPrivateKey] Requested username:", username); // Логирование

    return new Promise((resolve, reject) => {
        const request = indexedDB.open("SecureChatDB", 1);

        request.onerror = (event) => {
            console.error("DB open error:", event.target.error);
            reject(new Error("Database error"));
        };

        request.onsuccess = (event) => {
            const db = event.target.result;

            // Экстренная проверка структуры БД
            if (!db.objectStoreNames.contains("keys")) {
                console.error("ObjectStore 'keys' missing");
                reject(new Error("Database structure error"));
                return;
            }

            const tx = db.transaction("keys", "readonly");
            tx.onerror = (txEvent) => {
                console.error("Transaction error:", txEvent.target.error);
                reject(new Error("Transaction failed"));
            };

            const store = tx.objectStore("keys");
            const getRequest = store.get(username);

            getRequest.onsuccess = () => {
                if (getRequest.result?.privateKey) {
                    console.log("Key found for user:", username);
                    resolve(getRequest.result.privateKey);
                } else {
                    console.error("Key not found for:", username);
                    reject(new Error("Key not found"));
                }
            };

            getRequest.onerror = (getEvent) => {
                console.error("Get operation error:", getEvent.target.error);
                reject(new Error("Key retrieval error"));
            };
        };
    });
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
    return new Promise((resolve, reject) => {
        const request = indexedDB.open("ChatKeysDB", 1);

        request.onsuccess = (event) => {
            const db = event.target.result;
            const tx = db.transaction("keys", "readonly");
            const store = tx.objectStore("keys");
            const getRequest = store.get(chatName);

            getRequest.onsuccess = async () => {
                if (!getRequest.result) {
                    reject(new Error("AES key not found in IndexedDB"));
                    return;
                }

                try {
                    const keyBuffer = base64ToArrayBuffer(getRequest.result);
                    const aesKey = await crypto.subtle.importKey(
                        "raw",
                        keyBuffer,
                        { name: "AES-GCM" },
                        true,
                        ["decrypt"]
                    );
                    resolve(aesKey);
                } catch (e) {
                    reject(e);
                }
            };

            getRequest.onerror = () => reject(getRequest.error);
        };

        request.onerror = () => reject(request.error);
    });
}


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