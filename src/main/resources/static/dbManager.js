
window.DBManager = class DBManager {
    constructor(dbName = "SecureChatDB", version = 1, storeDefinitions = []) {
        this.dbName = dbName;
        this.version = version;
        this.storeDefinitions = storeDefinitions; // см. формат ниже
        this.db = null;
    }

    async open() {
        if (this.db) return this.db;

        return new Promise((resolve, reject) => {
            const request = indexedDB.open(this.dbName, this.version);

            request.onupgradeneeded = (event) => {
                const db = event.target.result;
                this.storeDefinitions.forEach(def => {
                    if (!db.objectStoreNames.contains(def.name)) {
                        const store = db.createObjectStore(def.name, {
                            keyPath: def.keyPath,
                            autoIncrement: def.autoIncrement || false
                        });

                        // Индексы
                        if (Array.isArray(def.indexes)) {
                            def.indexes.forEach(index => {
                                store.createIndex(index.name, index.keyPath, {
                                    unique: index.unique || false,
                                    multiEntry: index.multiEntry || false
                                });
                            });
                        }
                    }
                });
            };

            request.onsuccess = (event) => {
                this.db = event.target.result;
                resolve(this.db);
            };

            request.onerror = () => reject(request.error);
        });
    }

    async getStore(storeName, mode = "readonly") {
        const db = await this.open();
        if (!db.objectStoreNames.contains(storeName)) {
            throw new Error(`Object store "${storeName}" not found`);
        }
        return db.transaction([storeName], mode).objectStore(storeName);
    }

    async put(storeName, item) {
        const store = await this.getStore(storeName, "readwrite");
        return new Promise((resolve, reject) => {
            const request = store.put(item);
            request.onsuccess = () => resolve(request.result); // return key or id
            request.onerror = () => reject(request.error);
        });
    }

    async get(storeName, key) {
        const store = await this.getStore(storeName, "readonly");
        return new Promise((resolve, reject) => {
            const request = store.get(key);
            request.onsuccess = () => resolve(request.result);
            request.onerror = () => reject(request.error);
        });
    }

    async getByIndex(storeName, indexName, query) {
        const store = await this.getStore(storeName, "readonly");
        const index = store.index(indexName);
        return new Promise((resolve, reject) => {
            const request = index.get(query);
            request.onsuccess = () => resolve(request.result);
            request.onerror = () => reject(request.error);
        });
    }

    async delete(storeName, key) {
        const store = await this.getStore(storeName, "readwrite");
        return new Promise((resolve, reject) => {
            const request = store.delete(key);
            request.onsuccess = () => resolve();
            request.onerror = () => reject(request.error);
        });
    }

    async clear(storeName) {
        const store = await this.getStore(storeName, "readwrite");
        return new Promise((resolve, reject) => {
            const request = store.clear();
            request.onsuccess = () => resolve();
            request.onerror = () => reject(request.error);
        });
    }
}
