package com.project.messanger.repository;

import com.project.messanger.entity.ParticipantInChat;
import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ParticipantInChatRepository {
    private final JdbcTemplate jdbcTemplate;

    public ParticipantInChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<ParticipantInChat> findById_GroupChatName(String groupChatName) {
        String sql = "SELECT * FROM participant_in_chat WHERE group_chat_name = ?";
        return jdbcTemplate.query(sql, new ParticipantInChatRowMapper(), groupChatName);
    }


    public List<ParticipantInChat> findByRole(String role) {
        String sql = "SELECT * FROM participant_in_chat WHERE role_name = ?";
        return jdbcTemplate.query(sql, new ParticipantInChatRowMapper(), role);
    }


    public List<ParticipantInChat> findByUsernameContaining(String username) {
        String sql = "SELECT * FROM participant_in_chat WHERE username LIKE ?";
        return jdbcTemplate.query(sql, new ParticipantInChatRowMapper(), "%" + username + "%");
    }


    public List<ParticipantInChat> findByGroupChatNameAndRole(String groupChatName, String role_name) {
        String sql = "SELECT * FROM participant_in_chat WHERE group_chat_name = ? AND role_name = ?";
        return jdbcTemplate.query(sql, new ParticipantInChatRowMapper(), groupChatName, role_name);
    }


    public List<ParticipantInChat> findByDynamicQuery(String username, String role, String groupChat) {
        StringBuilder sql = new StringBuilder("SELECT * FROM participant_in_chat WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (groupChat != null && !groupChat.isEmpty()) {
            sql.append(" AND group_chat_name LIKE ?");
            params.add("%" + groupChat + "%");
        }
        if (username != null && !username.isEmpty()) {
            sql.append(" AND username LIKE ?");
            params.add("%" + username + "%");
        }
        if (role != null && !role.isEmpty()) {
            sql.append(" AND role_name = ?");
            params.add(role);
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), new ParticipantInChatRowMapper());
    }


    public void save(ParticipantInChat participant) {
        String sql = "INSERT INTO participant_in_chat (group_chat_name, username, role_name, " +
                "creation_date, public_key, join_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                participant.getId().getGroupChatName(),
                participant.getId().getUsername(),
                participant.getId().getRole_name(),
                participant.getCreationDate(),
                participant.getPublicKey(),
                participant.getJoinDate(),
                participant.getStatus());
    }


    public void deleteById(ParticipantInChatId id) {
        String sql = "DELETE FROM participant_in_chat WHERE group_chat_name = ? AND username = ?";
        jdbcTemplate.update(sql, id.getGroupChatName(), id.getUsername());
    }

    private static class ParticipantInChatRowMapper implements RowMapper<ParticipantInChat> {
        @Override
        public ParticipantInChat mapRow(ResultSet rs, int rowNum) throws SQLException {
            ParticipantInChatId id = new ParticipantInChatId();
            id.setGroupChatName(rs.getString("group_chat_name"));
            id.setUsername(rs.getString("username"));
            id.setRole_name(rs.getString("role_name"));

            ParticipantInChat participant = new ParticipantInChat();
            participant.setId(id);
            participant.setCreationDate(rs.getObject("creation_date", LocalDate.class));
            participant.setPublicKey(rs.getString("public_key"));
            participant.setJoinDate(rs.getObject("join_date", LocalDate.class));
            participant.setStatus(rs.getInt("status"));

            return participant;
        }
    }
}