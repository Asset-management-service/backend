package com.backend.moamoa.domain.chat.controller;

import com.backend.moamoa.domain.chat.dto.request.MessageRequest;
import com.backend.moamoa.domain.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/send")
    public void chat(MessageRequest messageRequest) {
        messageService.sendMessage(messageRequest);
        messagingTemplate.convertAndSend("/topic/chat/" + messageRequest.getReceiverId(), messageRequest);
    }

}
