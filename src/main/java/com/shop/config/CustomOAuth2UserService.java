package com.shop.config;

import com.shop.constant.Role;
import com.shop.dto.SessionUser;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;


@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {


    @Autowired
    private HttpSession httpSession;

    private String loginName;
    @Autowired
    private MemberRepository memberRepository;

    public String LoginUserName(){
        return loginName;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        // 자료형은 부모고 객체형은 자식이다. 업캐스팅
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);
        // DefaultOAuth2UserService 에 있는 loadUser를 빼서 request넣어서 oAuth2User로 뺴줌.

        // 현재 진행중인 서비스를 구부하기 위해 문자열로 받음. oAuth2UserRequest.getClinetRegistration().getRegistrationId()에 값이 들어있다.
        // {registrationId='naver'} 이런 식으로 보면 된다.
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        //OAuth2 로그인 시 키 값이 된다. 구글은 키 값이 "sub" 이고, 네이버는 "response"이고, 카카오는 "id" 이다. 각각 다르므로 따로 변수로 받아 넣어주어야함.
        //builder 패턴
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        //oAuth2 로그인을 통해 가져온 OAuth2User의 attribute를 담아주는  of 메소드
        //여기가 핵심 속성을 넘김. OAuthAttributes의 of라는 매소드에 받음
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = saveOrUpdate(attributes);
        httpSession.setAttribute("member", new SessionUser(member));

        loginName = attributes.getName();

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                ,attributes.getAttributes()
                ,attributes.getNameAttributeKey());
        // 네이버 등 로그인 누르면 바로 로그인 되는데 그 떄 는 singletone으로 ROLE_USER 를 쓰고, 엔티티 ROLE_USER DB에 저장하는 용도
    }

    //혹시 이미 저장된 정보라면, update 처리
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member checkMember = memberRepository.findByEmail(attributes.getEmail());

        Member member;

        if(checkMember == null){
            member = new Member();
            member.setEmail(attributes.getEmail());
            member.setPicture(attributes.getPicture());
            member.setName(attributes.getName());
            member.setRole(Role.USER);
            member.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm")));
        }
        else{
            member = checkMember.update(attributes.getName(), attributes.getPicture());
        }

        return memberRepository.save(member);
    }


}
