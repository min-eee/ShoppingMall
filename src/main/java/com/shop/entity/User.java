package com.shop.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    private String picture;

    private String role = "ROLE_USER";

    public User(String name, String email, String picture){
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public User update(String name, String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }
}
