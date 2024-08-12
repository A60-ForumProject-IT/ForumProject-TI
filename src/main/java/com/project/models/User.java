package com.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements Comparable<User> {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @JsonIgnore
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn (name = "role_id")
    private Role role;

    @JsonIgnore
    @Column(name = "is_blocked")
    private boolean isBlocked;

    @JsonIgnore
    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @JsonIgnore
    @ManyToMany(mappedBy = "usersWhoLikedPost", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Post> likedPosts;

    @JsonIgnore
    @ManyToMany(mappedBy = "usersWhoDislikedPost", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Post> dislikedPosts;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "users_avatars",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "avatar_id"))
    private Avatar avatar;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PhoneNumber> phoneNumbers;

    public User() {
    }


    @Override
    public int compareTo(User o) {
        return Integer.compare(this.id, o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}