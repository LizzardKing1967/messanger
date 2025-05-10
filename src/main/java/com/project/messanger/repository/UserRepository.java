package com.project.messanger.repository;

import com.project.messanger.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT EXISTS (SELECT 1 FROM \"user\" WHERE username = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, username));
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT EXISTS (SELECT 1 FROM \"user\" WHERE email = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, email));
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM \"user\" WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void saveUser(String username, String email, String passwordHash,
                         String publicKey, int status, String name, String lastName) {
        String sql = "INSERT INTO \"user\" (username, email, password_hash, date_of_registration, " +
                "status, key_version, rsa_public_key, name, last_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int key_version = 1;
        jdbcTemplate.update(sql, username, email, passwordHash, LocalDate.now(),
                status, key_version, publicKey, name, lastName);
    }

    public User save(String username, String email, String passwordHash,
                     String publicKey, int status, String name, String lastName) {
        String sql = "INSERT INTO \"user\" (username, email, password_hash, date_of_registration, " +
                "status, key_version, rsa_public_key, name, last_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int key_version = 1;
        jdbcTemplate.update(sql, username, email, passwordHash, LocalDate.now(),
                status, key_version, publicKey, name, lastName);
        return new User(username, email, passwordHash, LocalDate.now(),
                publicKey, key_version, status, name, lastName);
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public String getPublicKeyByUsername(String username) {
        String sql = "SELECT rsa_public_key FROM public.\"user\" WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<User> findAllExceptCurrent(String currentUsername) {
        String sql = "SELECT * FROM \"user\" WHERE username <> ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), currentUsername);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));

            if (rs.getMetaData().getColumnType(rs.findColumn("date_of_registration")) == java.sql.Types.DATE) {
                user.setDateOfRegistration(rs.getDate("date_of_registration").toLocalDate());
            } else if (rs.getMetaData().getColumnType(rs.findColumn("date_of_registration")) == java.sql.Types.TIMESTAMP) {
                user.setDateOfRegistration(rs.getTimestamp("date_of_registration").toLocalDateTime().toLocalDate());
            }

            user.setPublicKey(rs.getString("rsa_public_key"));
            user.setStatus(rs.getInt("status"));
            user.setName(rs.getString("name"));
            user.setLastName(rs.getString("last_name"));
            return user;
        }
    }
}