package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.dto.MemberHelpDto;
import com.shop.dto.MemberModifyDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;


import java.util.HashMap;


@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

    @Transactional(readOnly = true)
    public Member searchEmail(MemberHelpDto memberHelpDto){
        Member member1 = memberRepository.findByName(memberHelpDto.getSearchName());
        Member member2 = memberRepository.findByPhone(memberHelpDto.getSearchPhone());
        if(member1 != null && member2 != null && member1.getEmail().equals(member2.getEmail())){
            return member1;
        }
        else{
            return null;
        }
    }

    public Member selectMember(String email){

        return memberRepository.findByEmail(email);
    }

    public HashMap<String, Object> emailOverlap(String email){
        HashMap<String, Object> check = new HashMap<>();
        check.put("result", memberRepository.existsByEmail(email));
        return check;
    }

    public Member existsEmail (String check){
        return memberRepository.findByEmail(check);
    }

    public MemberModifyDto getMember(String email){
        Member member = memberRepository.findByEmail(email);

        MemberModifyDto memberModifyDto = new MemberModifyDto();
        memberModifyDto.setName(member.getName());
        memberModifyDto.setPhone(member.getPhone());
        memberModifyDto.setEmail(member.getEmail());
        memberModifyDto.setAddress(member.getAddress());

        return memberModifyDto;
    }

    public void updateMember(MemberModifyDto memberModifyDto){
        Member member = memberRepository.findByEmail(memberModifyDto.getEmail());
        member.updateMember(memberModifyDto);
    }

}