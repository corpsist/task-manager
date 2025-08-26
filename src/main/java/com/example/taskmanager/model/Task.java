package com.example.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import jakarta.websocket.Decoder.Text;

import java.time.Instant;

@Data                       //Lombok - generates getters,setters,toString,equals,hashCode automatically
@NoArgsConstructor          //generate no args constructor
@AllArgsConstructor         //generate all args constructor
@Builder                    //lets us create Tasks objects with builder pattern
@Entity                     //tells JPA this is a table
@Table(name = "tasks")      //optional: table name in DB
@EntityListeners(AuditingEntityListener.class) //Hook entity into Auditing
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // primary key, auto-increment (works with H2 and most DBs)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean completed;  

    // Auditing fields

    @CreatedDate //set once on insert
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate //set on Insert + every update
    @Column(nullable = false)
    private Instant updatedAt;
}
