package com.grandats.api.givedeefive.configuration;

import com.grandats.api.givedeefive.vo.SocketUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class SocketUserInterceptor implements ChannelInterceptor {

    private static final String EMAIL = "email";
    Logger logger = LoggerFactory.getLogger(SocketUserInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Instantiate an object for retrieving the STOMP headers
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // Check that the object is not null
        assert accessor != null;
        // If the frame is a CONNECT frame
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // retrieve the username from the headers
            final String email = accessor.getFirstNativeHeader(EMAIL);
            logger.info("SocketUserInterceptor > preSend >email :{}", email);
            if (email != null) {
                accessor.setUser(new SocketUser(email));
            }
        }
        return message;
    }
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        // Instantiate an object for retrieving the STOMP headers
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        // Check that the object is not null
//        assert accessor != null;
//        // If the frame is a CONNECT frame
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
//            if (raw instanceof Map) {
//                Object name = ((Map) raw).get("username");
//                if (name instanceof ArrayList) {
//                    accessor.setUser(new SocketUser(((ArrayList<String>) name).get(0).toString()));
//                }
//            }
//        }
//        return message;
//    }
}
