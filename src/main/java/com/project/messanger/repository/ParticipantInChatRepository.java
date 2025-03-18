package com.project.messanger.repository;

import com.project.messanger.entity.ParticipantInChat;
import com.project.messanger.entity.compoundKeys.ParticipantInChatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantInChatRepository extends JpaRepository<ParticipantInChat, ParticipantInChatId> {

    // Найти всех участников в чате по имени чата
    List<ParticipantInChat> findById_GroupChatName(String groupChatName);

    // Найти всех участников с ролью "user"
    @Query("SELECT p FROM ParticipantInChat p WHERE p.id.role_name = :role_name")
    List<ParticipantInChat> findByRole(@Param("role_name") String role);

    // Динамический поиск по нику пользователя
    @Query("SELECT p FROM ParticipantInChat p WHERE p.id.username LIKE %:username%")
    List<ParticipantInChat> findByUsernameContaining(@Param("username") String username);
}