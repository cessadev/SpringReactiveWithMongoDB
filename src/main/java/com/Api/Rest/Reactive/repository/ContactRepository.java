package com.Api.Rest.Reactive.repository;

import com.Api.Rest.Reactive.documents.Contact;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ContactRepository extends ReactiveMongoRepository<Contact, String> {
    Mono<Contact> findByEmail(String email);
    Mono<Contact> findByName(String name);
    Mono<Contact> findByPhone (String phone);
}
