package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "USERPASSWORD", nullable = false)
    private String password;

    @Column(name = "USEREMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "USERROLE", nullable = false)
    private String role = "CLIENT";

    @Column(name = "USERPHONE")
    private String phone;

    @Column(name = "CREATED_AT")
    private LocalDate createdAt;

    @Column(name = "STATUS", columnDefinition = "CHAR")
    private String status;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDate.now();
        if (status == null) status = "A";
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private ClientDetail clientDetail;
}
