package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres")
    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial (!@#$%^&*()_+-=[]{};':\"\\|,.<>/?)"
    )
    @Column(name = "USERPASSWORD", nullable = false)
    private String password;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(name = "USEREMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "USERROLE", nullable = false)
    private String role = "CLIENT";

    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
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

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = true) 
    private ClientDetail clientDetail;

}
