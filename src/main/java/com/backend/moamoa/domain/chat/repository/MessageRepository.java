package com.backend.moamoa.domain.chat.repository;

import com.backend.moamoa.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
