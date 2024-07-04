package com.Api.Rest.Reactive.repository;

import com.Api.Rest.Reactive.documents.Contact;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContactRepositoryTests {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeAll
    public void setUp() {
        // Limpiar la base de datos de prueba antes de las pruebas
        reactiveMongoTemplate.dropCollection(String.valueOf(Contact.class)).then().block();
    }

    @Test
    public void testSaveContact() {
        Contact contact = new Contact();
        contact.setName("Juan Perez");
        contact.setEmail("juan.perez@email.com");
        contact.setPhone("123-456-7890");

        Mono<Contact> savedContactMono = contactRepository.save(contact);

        StepVerifier.create(savedContactMono)
                .expectNextMatches(savedContact ->
                        savedContact.getId() != null && // El ID se genera automáticamente
                                savedContact.getName().equals("Juan Perez") &&
                                savedContact.getEmail().equals("juan.perez@email.com") &&
                                savedContact.getPhone().equals("123-456-7890")
                )
                .verifyComplete();
    }

    @Test
    public void testFindByEmail() {
        String email = "juan.perez@email.com";

        StepVerifier.create(contactRepository.findByEmail(email))
                .expectNextMatches(contact -> contact.getEmail().equals(email))
                .verifyComplete();
    }

    @Test
    public void testFindByNameExisting() {
        String name = "Juan Perez";

        contactRepository.findByName(name)
                .subscribe(contact -> {
                    assertEquals(name, contact.getName());
                });
    }

    @Test
    public void testFindByNameNonExistent() {
        String name = "NoExistente";

        StepVerifier.create(contactRepository.findByName(name))
                .verifyComplete(); // No se encontró coincidencia, se espera Mono.empty()
    }

    @Test
    public void testFindByPhoneExisting() {
        String phone = "123-456-7890";

        contactRepository.findByPhone(phone)
                .subscribe(contact -> {
                    assertEquals(phone, contact.getPhone());
                });
    }

    @Test
    public void testFindByPhoneNonExistent() {
        String phone = "0";

        StepVerifier.create(contactRepository.findByPhone(phone))
                .verifyComplete();
    }

    @Test
    public void testFindAll() {
        Flux<Contact> allContacts = contactRepository.findAll();

        StepVerifier.create(allContacts)
                .expectNextCount(4) // En este caso, solo hay 4 en la base de datos
                .verifyComplete();
    }

    @Test
    public void testFindByIdExisting() {
        String id = "660dfba8062f8664c1986a4c";

        StepVerifier.create(contactRepository.findById(id))
                .expectNextMatches(contact -> contact.getId().equals(id))
                .verifyComplete();
    }

    @Test
    public void testFindByIdNonExistent() {
        String id = "1";

        StepVerifier.create(contactRepository.findById(id))
                .verifyComplete();
    }

    @Test
    public void testDeleteById() {
        String id = "65e0e7590198913db3603c8f";

        StepVerifier.create(contactRepository.deleteById(id))
                .verifyComplete(); // Se espera que se complete sin errores

        // Verifica que el contacto ya no esté en la base de datos
        StepVerifier.create(contactRepository.findById(id))
                .expectNextCount(0)
                .verifyComplete();
    }

    @AfterAll
    public void tearDown() {
        // Limpiar la base de datos de prueba después de las pruebas
        reactiveMongoTemplate.dropCollection(String.valueOf(Contact.class)).then().block();
    }

}
