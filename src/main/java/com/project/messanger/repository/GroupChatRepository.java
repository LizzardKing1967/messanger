package com.project.messanger.repository;

import com.project.messanger.dto.GroupChatDTO;
import com.project.messanger.entity.GroupChat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class GroupChatRepository {
    private final JdbcTemplate jdbcTemplate;

    public GroupChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByGroupChatName(String groupChatName) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) > 0 FROM group_chat WHERE group_chat_name = ?",
                Boolean.class,
                groupChatName
        );
    }

    public void save(GroupChat groupChat) {
        jdbcTemplate.update(
                "INSERT INTO group_chat (group_chat_name, creation_date) VALUES (?, ?)",
                groupChat.getGroupChatName(),
                groupChat.getCreationDate()
        );
    }

    public List<GroupChat> findAll() {
        return jdbcTemplate.query(
                "SELECT * SELECT * FROM group_chat",
                new GroupChatRowMapper()
        );
    }

    public List<GroupChatDTO> findAllByUsername(String username) {
        return jdbcTemplate.query(
                "SELECT * FROM get_all_group_chats_by_user(?)",
                new GroupChatDtoRowMapper(),
                username
        );
    }

    public List<GroupChatDTO> findAllGroupChatDTO() {
        return jdbcTemplate.query(
                "SELECT * FROM get_all_group_chats()",
                new GroupChatDtoRowMapper()
        );
    }

    public Optional<GroupChat> findByGroupChatName(String groupChatName) {
        return jdbcTemplate.query(
                "SELECT * FROM group_chat WHERE group_chat_name = ?",
                new GroupChatRowMapper(),
                groupChatName
        ).stream().findFirst();
    }

    private static class GroupChatRowMapper implements RowMapper<GroupChat> {
        @Override
        public GroupChat mapRow(ResultSet rs, int rowNum) throws SQLException {
            GroupChat groupChat = new GroupChat();
            groupChat.setGroupChatName(rs.getString("group_chat_name"));
            groupChat.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
            return groupChat;
        }
    }

    private static class GroupChatDtoRowMapper implements RowMapper<GroupChatDTO> {
        @Override
        public GroupChatDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            GroupChatDTO dto = new GroupChatDTO();
            dto.setName(rs.getString("group_chat_name"));
            dto.setGroupType(rs.getObject("group_type") != null ? rs.getBoolean("group_type") : null);
            dto.setLastMessageSender(rs.getString("last_message_sender"));
            dto.setLastMessage(rs.getString("last_message"));

            java.sql.Date lastDate = rs.getDate("last_message_date");
            dto.setLastMessageDate(lastDate != null ? lastDate.toLocalDate().atStartOfDay() : null);

            return dto;
        }
    }
}