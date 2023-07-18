package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "items")
@Data
@Builder
@EqualsAndHashCode(exclude = {"name", "description", "owner", "comments", "bookings"})
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User owner;

    @OneToMany
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Set<Comment> comments;

    @OneToMany
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Set<Booking> bookings;
}
