package com.bekaku.api.spring.controller.socket;

import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
/*
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    Logger logger = LoggerFactory.getLogger(ChatController.class);

//    @MessageMapping(ConstantData.WEB_SOCKET_ENDPOINT)
//    @SendTo("/topic/chat")
//    public OutputMessage send(final Message message) throws Exception {
//        logger.info("ChatController > send");
//        final String time = new SimpleDateFormat("HH:mm").format(new Date());
//        return new OutputMessage(message.getFrom(), message.getText(), time);
//    }

//    @MessageMapping("/_websocket/chatGroup/{roomId}")
//    @SendTo("/topic/chatGroup/{roomId}")
//    public OutputMessage sendChatGroup(final Message message, @DestinationVariable String roomId) throws Exception {
//        logger.info("ChatController > sendChatGroup > roomId :{}", roomId);
//        final String time = new SimpleDateFormat("HH:mm").format(new Date());
//        return new OutputMessage(message.getFrom(), message.getText(), time);
//    }

    @MessageMapping("/_websocket/chatGroup/{roomId}")
    public void send(final Message message, @DestinationVariable String roomId) throws Exception {
        logger.info("ChatController > send");
        this.broadcastChat(message, "/topic/chatGroup/" + roomId);
    }
    public void broadcastChat(@Payload Message message, String topic) {
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        this.simpMessagingTemplate.convertAndSend(topic, new OutputMessage(message.getFrom(), message.getText(), time));
    }

    @MessageMapping("/_websocket/chatwithuser")
    public void send(SimpMessageHeaderAccessor sha, @Payload Message message) {
        assert sha.getUser() != null;
//        String message = "Hello from " + sha.getUser().getName();
        message.setText(message.getText() + " From " + sha.getUser().getName());
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/queue/messages", new OutputMessage(message.getFrom(), message.getText(), time));
    }
*/
}