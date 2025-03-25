package com.project.messanger.repository;

import com.project.messanger.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }


    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
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


    public void save(User user) {
        String sql = "INSERT INTO \"user\" (username, email, password_hash, date_of_registration, public_key, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getDateOfRegistration(),
                user.getPublicKey(),
                user.getStatus());
    }


    public List<User> findAll() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setDateOfRegistration(
                    rs.getDate("date_of_registration") != null ?
                            rs.getDate("date_of_registration").toLocalDate().atStartOfDay() :
                            null
            );
            user.setPublicKey(rs.getString("public_key"));
            user.setStatus(rs.getInt("status"));
            return user;
        }
    }
}