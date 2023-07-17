package ru.practicum.shareit.user.model;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@Builder
@EqualsAndHashCode(exclude = {"name", "email"})
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
}
