package com.project.messanger.repository;

import com.project.messanger.entity.ParticipantInChat;
import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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

    public List<ParticipantInChat> findByUsername(String username) {
        return jdbcTemplate.query(
                "SELECT * FROM participant_in_chat WHERE username = ?",
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
                "INSERT INTO participant_in_chat (group_chat_name, username, role_name, join_date, status, encrypted_aes_key, key_version)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                participant.getId().getGroupChatName(),
                participant.getId().getUsername(),
                participant.getRole_name(),
                participant.getJoinDate(),
                participant.getStatus(),
                participant.getEncryptedKey(),
                participant.getKeyVersion()
        );
    }

    public void deleteById(ParticipantInChatId id) {
        jdbcTemplate.update(
                "CALL delete_participant(?, ?)",  // Изменено с SELECT на CALL
                id.getGroupChatName(),
                id.getUsername()
        );
    }

    public String findEncryptedAesKeyForParticipant(String username, String groupChatname) {
        username = username.trim();
        groupChatname = groupChatname.replace('\u00A0', ' ').trim();

        try {
            return jdbcTemplate.queryForObject(
                    "SELECT encrypted_aes_key FROM participant_in_chat WHERE username = ? AND group_chat_name = ? LIMIT 1",
                    String.class,
                    username,
                    groupChatname
            );
        } catch (EmptyResultDataAccessException e) {
            return null; // или бросить исключение
        }
    }

    private static class ParticipantInChatRowMapper implements RowMapper<ParticipantInChat> {
        @Override
        public ParticipantInChat mapRow(ResultSet rs, int rowNum) throws SQLException {
            ParticipantInChatId id = new ParticipantInChatId();
            id.setGroupChatName(rs.getString("group_chat_name"));
            id.setUsername(rs.getString("username"));

            ParticipantInChat participant = new ParticipantInChat();
            participant.setId(id);
            participant.setRole_name(rs.getString("role_name"));
            participant.setJoinDate(rs.getObject("join_date", LocalDate.class));
            participant.setStatus(rs.getInt("status"));
            participant.setEncryptedKey(rs.getString("encrypted_aes_key"));
            participant.setKeyVersion(rs.getString("key_version"));


            return participant;
        }
    }
}