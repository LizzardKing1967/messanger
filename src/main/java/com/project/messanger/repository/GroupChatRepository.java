package com.project.messanger.repository;

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
        String sql = "SELECT COUNT(*) FROM group_chat WHERE group_chat_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, groupChatName);
        return count != null && count > 0;
    }

    public void save(GroupChat groupChat) {
        String sql = "INSERT INTO group_chat (group_chat_name, creation_date, public_key) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                groupChat.getGroupChatName(),
                groupChat.getCreationDate(),
                groupChat.getPublicKey());
    }

    public List<GroupChat> findAll() {
        String sql = "SELECT * FROM group_chat";
        return jdbcTemplate.query(sql, new GroupChatRowMapper());
    }

    private static class GroupChatRowMapper implements RowMapper<GroupChat> {
        @Override
        public GroupChat mapRow(ResultSet rs, int rowNum) throws SQLException {
            GroupChat groupChat = new GroupChat();
            groupChat.setGroupChatName(rs.getString("group_chat_name"));
            groupChat.setCreationDate(
                    rs.getDate("creation_date") != null ?
                            rs.getDate("creation_date").toLocalDate().atStartOfDay() :
                            null
            );
            groupChat.setPublicKey(rs.getString("public_key"));
            return groupChat;
        }
    }
}