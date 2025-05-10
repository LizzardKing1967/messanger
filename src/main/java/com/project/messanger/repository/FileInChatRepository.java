package com.project.messanger.repository;


import com.project.messanger.entity.FileInChat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FileInChatRepository {
    private final JdbcTemplate jdbcTemplate;

    public FileInChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Сохранение файла в чате
//    public void save(FileInChat fileInChat) {
//        jdbcTemplate.update(
//                "INSERT INTO file_in_chat (username, file_name, group_chat_name, creation_date, public_key, encrypted_key) " +
//                        "VALUES (?, ?, ?, ?, ?, ?)",
//                fileInChat.getUsername(),
//                fileInChat.getFileName(),
//                fileInChat.getGroupChatName(),
//                fileInChat.getCreationDate(),
//                fileInChat.getPublicKey(),
//                fileInChat.getEncryptedKey()
//        );
//    }

    // Поиск файлов по чату
//    public List<FileInChat> findByGroupChat(String groupChatName) {
//        return jdbcTemplate.query(
//                "SELECT * FROM file_in_chat WHERE group_chat_name = ?",
//                new FileInChatRowMapper(),
//                groupChatName
//        );
//    }

    // Поиск файлов по пользователю в чате
//    public List<FileInChat> findByUserInChat(String username, String groupChatName) {
//        return jdbcTemplate.query(
//                "SELECT * FROM file_in_chat WHERE username = ? AND group_chat_name = ?",
//                new FileInChatRowMapper(),
//                username, groupChatName
//        );
//    }

    // Удаление файла из чата
    public void delete(String username, String fileName, String groupChatName) {
        jdbcTemplate.update(
                "DELETE FROM file_in_chat WHERE username = ? AND file_name = ? AND group_chat_name = ?",
                username, fileName, groupChatName
        );
    }

//    private static class FileInChatRowMapper implements RowMapper<FileInChat> {
//        @Override
//        public FileInChat mapRow(ResultSet rs, int rowNum) throws SQLException {
//            FileInChat file = new FileInChat();
//            file.setUsername(rs.getString("username"));
//            file.setFileName(rs.getString("file_name"));
//            file.setGroupChatName(rs.getString("group_chat_name"));
//            file.setCreationDate(rs.getObject("creation_date", LocalDateTime.class));
//            file.setPublicKey(rs.getString("public_key"));
//            file.setEncryptedKey(rs.getString("encrypted_key"));
//            return file;
//        }
//    }
}
