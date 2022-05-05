package com.XiangQi.XiangQiBE.Configurations;

import com.XiangQi.XiangQiBE.Components.WebsocketAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// lobbies/{id} is for subscribing to singular lobby message
// lobbies is for subscribing to lobby create message

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private WebsocketAuthInterceptor authInterceptor;
    @Autowired
    private CorseConfig corseConfig;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // lobbies/{id}/move or lobbies/{id}
        var origins = corseConfig.allowedOrigins.toArray(new String[0]);
        registry.addEndpoint("/ws").setAllowedOrigins(origins);
        registry.addEndpoint("/ws").setAllowedOrigins(origins).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for sending message
        registry.setApplicationDestinationPrefixes("/ws");

        // Brokers to subscribe to
        registry.enableSimpleBroker("/lobbies");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authInterceptor);
    }
}
