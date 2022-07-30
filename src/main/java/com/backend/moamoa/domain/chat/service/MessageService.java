package com.backend.moamoa.domain.chat.service;

import com.backend.moamoa.domain.chat.dto.request.MessageRequest;
import com.backend.moamoa.domain.chat.entity.ChatRoom;
import com.backend.moamoa.domain.chat.entity.Message;
import com.backend.moamoa.domain.chat.repository.ChatRoomRepository;
import com.backend.moamoa.domain.chat.repository.MessageRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final UserUtil userUtil;

    @Transactional
    public void sendMessage(MessageRequest messageRequest){
        User user = userUtil.findCurrentUser();
        chatRoomRepository.save(ChatRoom.builder()

                .build())
        //TODO verify overlap chatroom
    }

}
