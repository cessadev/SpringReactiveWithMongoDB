package com.Api.Rest.Reactive.functional.handler;

import com.Api.Rest.Reactive.documents.Contact;
import com.Api.Rest.Reactive.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ContactHandler {
    @Autowired
    private ContactRepository contactRepository;

    private final Mono<ServerResponse> response404 = ServerResponse.notFound().build();
    private final Mono<ServerResponse> response406 = ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build();

    // Get all the contacts
    public Mono<ServerResponse> getAllContacts(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contactRepository.findAll(), Contact.class);
    }

    // Get a contact through the ID
    public Mono<ServerResponse> getContactById(ServerRequest request) {
        String id = request.pathVariable("id");

        return contactRepository.findById(id)
                .flatMap(contact ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(contact)))
                .switchIfEmpty(response404);
    }

    // Get a contact through the Email
    public Mono<ServerResponse> getContactByEmail(ServerRequest request) {
        String email = request.pathVariable("email");

        return contactRepository.findByEmail(email)
                .flatMap(contact ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(contact)))
                .switchIfEmpty(response404);
    }

    // Persist a contact in the database
    public Mono<ServerResponse> createContact(ServerRequest request) {
        Mono<Contact> contactMono = request.bodyToMono(Contact.class);

        return contactMono
                .flatMap(contact -> contactRepository.save(contact)
                        .flatMap(contactSaved -> {
                            try {
                                return ServerResponse.created(new URI("/functional/handler/contacts/save"))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(contactSaved));
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        }))
                .switchIfEmpty(response406);
    }

    // Update a contact through their ID
    public Mono<ServerResponse> updateContact(ServerRequest request) {
        Mono<Contact> contactMono = request.bodyToMono(Contact.class);
        String id = request.pathVariable("id");

        Mono<Contact> contactUpdated = contactMono.flatMap(
                contact -> contactRepository.findById(id)
                        .flatMap(oldContact -> {
                            oldContact.setName(contact.getName());
                            oldContact.setEmail(contact.getEmail());
                            oldContact.setPhone(contact.getPhone());
                            return contactRepository.save(oldContact);
                        }));

        return contactUpdated.flatMap(contact ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(contact)))
                .switchIfEmpty(response404);
    }

    // Delete a contact through their ID
    public Mono<ServerResponse> deleteContact(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Void> contactDeleted = contactRepository.deleteById(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(contactDeleted, Void.class);
    }

}
