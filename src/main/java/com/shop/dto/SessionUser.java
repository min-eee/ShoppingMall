package com.shop.dto;

import com.shop.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SessionUser implements Serializable {

    private String name;

    private String email;

    private String picture;

    public SessionUser(Member member){
        this.name = member.getName();
        this.email = member.getEmail();
        this.picture = member.getPicture();
    }

    public SessionUser(){

    }

}
