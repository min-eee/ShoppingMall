package com.shop.controller;

import com.shop.dto.CartItemDto;
import com.shop.dto.MemberFormDto;
//import com.shop.dto.MemberHelpDto;
import com.shop.dto.MemberHelpDto;
import com.shop.dto.MemberModifyDto;
import com.shop.repository.MemberRepository;
import com.shop.service.MailService;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.shop.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.validation.BindingResult;


import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @GetMapping(value = "/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        if(!memberFormDto.getPassword().equals(memberFormDto.getCheckPw())){
            bindingResult.rejectValue("checkPw","passwordInCorrect","패스워드가 일지하지 않습니다.");
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember() {
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/help")
    public String helpMember(Model model) {
        model.addAttribute("email", null);
        return "member/help";
    }


    @PostMapping(value = "/help")
    public String helpResult(MemberHelpDto memberHelpDto, Model model) {

        Member member = memberService.searchEmail(memberHelpDto);

        if (member != null) {
            model.addAttribute("email", member.getEmail());
        } else {
            model.addAttribute("email", "None");
        }
        return "member/help";
    }

    @GetMapping(value = "/memberDtl")
    public String memberProfile(Model model, Authentication authentication) {
        String email = authentication.getName();

        Member member = memberService.selectMember(email);
        model.addAttribute("memberName", member.getName());
        model.addAttribute("memberPhone", member.getPhone());
        model.addAttribute("memberAddress", member.getAddress());

        return "member/memberDtl";
    }

//    @PostMapping(value = "/checkedEmail")
//    public @ResponseStatus ResponseEntity checkeEmail(@RequestBody @Valid Map<String, String> Email, BindingResult bindingResult){
//        if(bindingResult.hasErrors()){
//            StringBuilder sb = new StringBuilder();
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//
//            for (FieldError fieldError : fieldErrors) {
//                sb.append(fieldError.getDefaultMessage());
//            }
//
//            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
//        }
//
//        Member checked;
//        boolean result;
//
//        try {
//            checked = memberService.existsEmail(Email.get("email"));
//
//            if(checked != null) {
//                result = true;
//            }
//            else {
//                result = false;
//            }
//        }
//        catch (Exception e){
//            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity<Boolean>(result, HttpStatus.OK);
//    }

    @GetMapping(value ="/memberDtl/memberModify")
    public String memberGet(Model model, Authentication authentication) {
        String email = authentication.getName();
        MemberModifyDto memberModifyDto = memberService.getMember(email);
        model.addAttribute("memberModifyDto", memberModifyDto);
        return "member/memberModify";

    }

    @PostMapping(value ="/memberDtl/memberModify")
    public String memberUpdate(Model model, MemberModifyDto memberModifyDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "members/memberDtl";
        }

        try{
            memberService.updateMember(memberModifyDto);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", "회원정보 수정 중 에러가 발생하였습니다.");
            return "member/memberDtl";
        }
        return "redirect:/members/memberDtl";
    }

    @PostMapping("/{email}/emailConfirm")
    public @ResponseBody ResponseEntity emailConfirm(@PathVariable("email") String email) throws Exception{

        String confirm = mailService.sendSimpleMessage(email);

        return new ResponseEntity<String>("confirm", HttpStatus.OK);
    }

}