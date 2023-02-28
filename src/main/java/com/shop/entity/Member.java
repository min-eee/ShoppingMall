package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;

import com.shop.dto.MemberModifyDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.crypto.password.PasswordEncoder;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Member extends BaseEntity{

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="email", unique = true)
    private String email;

    private String password;

    private String name;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String address;

    private String picture;

    @CreatedDate
    @Column(updatable = false)
    private String createTime;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setPhone(memberFormDto.getPhone());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return member;
    }

    @PrePersist
    public void saveTime(){
        this.createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm"));
    }

    public void updateMember(MemberModifyDto memberModifyDto){
        this.name = memberModifyDto.getName();
        this.phone = memberModifyDto.getPhone();
        this.email = memberModifyDto.getEmail();
        this.address = memberModifyDto.getAddress();
    }

    public Member(String name, String email, String picture){
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public Member update(String name, String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }

}
