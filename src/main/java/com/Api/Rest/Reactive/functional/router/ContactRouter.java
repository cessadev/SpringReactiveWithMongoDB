package com.Api.Rest.Reactive.functional.router;

import com.Api.Rest.Reactive.functional.handler.ContactHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ContactRouter {

    @Bean
    public RouterFunction<ServerResponse> routerContact(ContactHandler contactHandler) {
        return RouterFunctions
                .route(GET("/functional/handler/contacts"), contactHandler::getAllContacts)
                .andRoute(GET("/functional/handler/contacts/byId/{id}"), contactHandler::getContactById)
                .andRoute(GET("/functional/handler/contacts/byEmail/{email}"), contactHandler::getContactByEmail)
                .andRoute(POST("/functional/handler/contacts/save"), contactHandler::createContact)
                .andRoute(PUT("/functional/handler/contacts/update/{id}"), contactHandler::updateContact)
                .andRoute(DELETE("/functional/handler/contacts/deleteById/{id}"), contactHandler::deleteContact);
    }

}
