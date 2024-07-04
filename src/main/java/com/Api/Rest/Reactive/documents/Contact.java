package com.Api.Rest.Reactive.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contact")
public class Contact {

    @Id
    private String id;
    private String name;
    private String email;
    private String phone;

}
