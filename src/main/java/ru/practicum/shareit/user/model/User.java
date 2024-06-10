package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @ToString.Exclude
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    List<Item> items = new ArrayList<>();

}