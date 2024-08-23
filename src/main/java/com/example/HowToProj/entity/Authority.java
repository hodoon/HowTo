package com.example.HowToProj.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "authority")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;

    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
