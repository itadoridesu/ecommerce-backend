package com.dmdm.ecommerce_backend.Entity;

import com.dmdm.ecommerce_backend.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @Column(unique = true, nullable = false) // Unique and required
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false) // Required
    private String password;

    @ElementCollection(fetch = FetchType.EAGER) // One user -> many roles (stored in a separate table)
//    @CollectionTable(                           // Optional customization
//            name = "user_role",             // Custom join table name
//            joinColumns = @JoinColumn(name = "user_id") // Join via user ID
//    )
    @Enumerated(EnumType.STRING)               // Store enum name, not its ordinal
    private Set<Role> role;

    // user houwa field li dayrin elih ManyToOne
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();
}
