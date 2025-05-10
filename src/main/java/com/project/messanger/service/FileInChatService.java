package com.project.messanger.service;

import com.project.messanger.entity.FileInChat;
import com.project.messanger.repository.FileInChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileInChatService {

    private final FileInChatRepository fileInChatRepository;

    // Добавление файла в чат
//    @Transactional
//    public FileInChat sendTextMessage(String username, String groupChatName) {
//        FileInChat fileInChat = new FileInChat();
//        fileInChat.setUsername(username);
//        fileInChat.setFileName(fileName);
//        fileInChat.setGroupChatName(groupChatName);
//        fileInChat.setCreationDate(LocalDateTime.now());
//        fileInChat.setPublicKey(publicKey);
//        fileInChat.setEncryptedKey(encryptedKey);
//
//        fileInChatRepository.save(fileInChat);
//        return fileInChat;
//    }

    // Получение всех файлов в чате
//    public List<FileInChat> getFilesInChat(String groupChatName) {
//        return fileInChatRepository.findByGroupChat(groupChatName);
//    }
//
//    // Получение файлов пользователя в чате
//    public List<FileInChat> getUserFilesInChat(String username, String groupChatName) {
//        return fileInChatRepository.findByUserInChat(username, groupChatName);
//    }

    // Удаление файла из чата
//    @Transactional
//    public void removeFileFromChat(String username, String fileName, String groupChatName) {
//        fileInChatRepository.delete(username, fileName, groupChatName);
//    }
}