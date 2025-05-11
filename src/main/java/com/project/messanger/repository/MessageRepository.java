package com.project.messanger.repository;

import com.project.messanger.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final JdbcTemplate jdbcTemplate;

    // Маппер для преобразования ResultSet в объект Message
    private static final RowMapper<Message> MESSAGE_ROW_MAPPER = (rs, rowNum) -> new Message(
            rs.getString("username"),
            rs.getString("group_chat_name"),
            rs.getString("message_text"),
            rs.getTimestamp("sent_at").toLocalDateTime(),
            rs.getInt("status")
    );

    // Сохранение сообщения с использованием RETURNING и jdbcTemplate
    public Message save(Message message) {
        String sql = """
        INSERT INTO message (username, group_chat_name, message_text, sent_at, status)
        VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, message.getSenderUsername());
            ps.setString(2, message.getGroupChatName());
            ps.setString(3, message.getEncryptedTextContent());
            ps.setTimestamp(4, Timestamp.valueOf(message.getSendDate()));
            ps.setInt(5, message.getStatus());
            return ps;
        });

        // Поскольку нет RETURNING, возвращаем просто исходный объект (или можно сделать доп. SELECT при необходимости)
        return message;
    }

    // Получение всех сообщений по имени группового чата
    public List<Message> findByGroupChatName(String groupChatName) {
        String sql = """
            SELECT username, group_chat_name, message_text, sent_at, status
            FROM message
            WHERE group_chat_name = ?
            ORDER BY sent_at ASC 
            """;

        return jdbcTemplate.query(sql, MESSAGE_ROW_MAPPER, groupChatName);
    }
}
