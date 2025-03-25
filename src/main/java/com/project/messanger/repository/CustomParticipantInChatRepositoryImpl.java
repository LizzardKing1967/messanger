package com.project.messanger.repository;

import com.project.messanger.entity.ParticipantInChat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;
import java.util.List;

@ComponentScan
@Repository
public class CustomParticipantInChatRepositoryImpl implements CustomParticipantInChatRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<ParticipantInChat> findByDynamicQuery(String username, String role, String groupChat) {
        // Базовый запрос

        if (entityManager == null)
        System.out.println("EntityManager is null: " + (entityManager == null));
        StringBuilder queryString = new StringBuilder("SELECT p FROM ParticipantInChat p WHERE 1=1");

        if (groupChat != null && !groupChat.isEmpty()) {
            queryString.append(" AND p.id.groupChatName LIKE :groupChat");
        }
        // Добавляем условия, если параметры не null
        if (username != null && !username.isEmpty()) {
            queryString.append(" AND p.id.username LIKE :username");
        }
        if (role != null && !role.isEmpty()) {
            queryString.append(" AND p.id.role_name = :role");
        }

        // Создаем запрос
        Query query = entityManager.createQuery(queryString.toString(), ParticipantInChat.class);

        // Устанавливаем параметры, если они есть
        if (username != null && !username.isEmpty()) {
            query.setParameter("username", "%" + username + "%");
        }
        if (groupChat != null && !groupChat.isEmpty()) {
            query.setParameter("groupChat", groupChat);
        }
        if (role != null && !role.isEmpty()) {
            query.setParameter("role", role);
        }

        // Выполняем запрос и возвращаем результат
        return query.getResultList();
    }
}