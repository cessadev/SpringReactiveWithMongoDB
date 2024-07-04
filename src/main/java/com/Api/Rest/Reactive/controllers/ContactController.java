package com.Api.Rest.Reactive.controllers;

import com.Api.Rest.Reactive.documents.Contact;
import com.Api.Rest.Reactive.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/contacts")
    public Flux<Contact> findAllContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/contacts/byId/{id}")
    public Mono<ResponseEntity<Contact>> findContactById(@PathVariable String id) {
        return contactRepository.findById(id)
                .map(contact -> new ResponseEntity<>(contact, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/contacts/byEmail/{email}")
    public Mono<ResponseEntity<Contact>> findContactByEmail(@PathVariable String email) {
        return contactRepository.findByEmail(email)
                .map(contact -> new ResponseEntity<>(contact, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/contacts/save")
    public Mono<ResponseEntity<Contact>> saveContact(@RequestBody Contact contact) {
        return contactRepository.insert(contact)
                .map(contactCreated -> new ResponseEntity<>(contactCreated, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(contact, HttpStatus.NOT_ACCEPTABLE));
    }

    @PutMapping("contacts/update/{id}")
    public Mono<ResponseEntity<Contact>> updateContact(@PathVariable String id, @RequestBody Contact contact) {
        return contactRepository.findById(id)
                .flatMap(contactFound -> {
                    contactFound.setName(contact.getName());
                    contactFound.setEmail(contact.getEmail());
                    contactFound.setPhone(contact.getPhone());
                    return contactRepository.save(contactFound);
                })
                .map(updatedContact -> new ResponseEntity<>(updatedContact, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/contacts/deleteById/{id}")
    public Mono<ResponseEntity<Void>> deleteContact(@PathVariable String id) {
        return contactRepository.findById(id)
                .flatMap(contact -> contactRepository.delete(contact).then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}