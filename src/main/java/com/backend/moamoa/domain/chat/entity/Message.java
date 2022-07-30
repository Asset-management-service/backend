package com.backend.moamoa.domain.chat.entity;

import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Message implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "message_id")
    private Long id;

    private String message;

    @Embedded
    private TimeEntity timeEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }

    @Builder
    public Message(Long id, String message, User sender, User receiver, ChatRoom chatRoom) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.chatRoom = chatRoom;
    }


}
