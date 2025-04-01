package com.project.messanger.repository;

import com.project.messanger.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT EXISTS (SELECT 1 FROM public.get_user_by_login(?))";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, username));
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT EXISTS (SELECT 1 FROM public.get_user_by_email(?))";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, email));
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM public.get_user_by_login(?)";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM public.get_user_by_email(?)";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void saveUser(String username, String email, String passwordHash, String publicKey, int status) {
        String sql = "CALL public.save_user(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, username, email, passwordHash, publicKey, status);
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
            if (rs.getMetaData().getColumnType(rs.findColumn("date_of_registration")) == java.sql.Types.DATE) {
                user.setDateOfRegistration(rs.getDate("date_of_registration").toLocalDate());
            } else if (rs.getMetaData().getColumnType(rs.findColumn("date_of_registration")) == java.sql.Types.TIMESTAMP) {
                user.setDateOfRegistration(rs.getTimestamp("date_of_registration").toLocalDateTime().toLocalDate());
            } else {
                // Обработка других типов или ошибка
                throw new SQLException("Unsupported column type for date_of_registration");
            }
            user.setPublicKey(rs.getString("public_key"));
            user.setStatus(rs.getInt("status"));
            return user;
        }
    }
}
