package com.shop.dto;


import com.shop.entity.Item;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.ui.ModelMap;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MemberModifyDto {

    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phone;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    private String email;

}

