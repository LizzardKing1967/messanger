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
        return jdbcTemplate.query(
                "SELECT * FROM find_participants_by_chat(?)",
                new ParticipantInChatRowMapper(),
                groupChatName
        );
    }



    public List<ParticipantInChat> findByRole(String role) {
        return jdbcTemplate.query(
                "SELECT * FROM find_participants_by_role(?)",
                new ParticipantInChatRowMapper(),
                role
        );
    }

    public List<ParticipantInChat> findByUsernameContaining(String username) {
        return jdbcTemplate.query(
                "SELECT * FROM find_participants_by_username(?)",
                new ParticipantInChatRowMapper(),
                username
        );
    }


    public List<ParticipantInChat> findByGroupChatNameAndRole(String groupChatName, String role_name) {
        return jdbcTemplate.query(
                "SELECT * FROM find_participants_by_chat_and_role(?, ?)",
                new ParticipantInChatRowMapper(),
                groupChatName, role_name
        );
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
        jdbcTemplate.update(
                "CALL save_participant(?, ?, ?, ?, ?, ?, ?)",  // Изменено с SELECT на CALL
                participant.getId().getGroupChatName(),
                participant.getId().getUsername(),
                participant.getId().getRole_name(),
                participant.getCreationDate(),
                participant.getPublicKey(),
                participant.getJoinDate(),
                participant.getStatus()
        );
    }

    public void deleteById(ParticipantInChatId id) {
        jdbcTemplate.update(
                "CALL delete_participant(?, ?)",  // Изменено с SELECT на CALL
                id.getGroupChatName(),
                id.getUsername()
        );
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