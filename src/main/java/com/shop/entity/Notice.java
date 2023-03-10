package com.shop.entity;

import com.shop.constant.NoticeStatus;

import com.shop.dto.NoticeFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import javax.persistence.*;


@Entity
@Table(name="notice")
@Getter
@Setter
@ToString
public class Notice extends BaseEntity{

    @Id
    @Column(name="notice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //번호

    @Column(name="notice_title", nullable = false)
    private String noticeTitle;

    @Lob
    @Column(name="notice_content", nullable = false)
    private String noticeContent;

    @Enumerated(EnumType.STRING)
    private NoticeStatus noticeStatus;



    //공지 수정
    public void updateNotice(NoticeFormDto noticeFormDto){
        this.noticeTitle = noticeFormDto.getNoticeTitle();
        this.noticeContent = noticeFormDto.getNoticeContent();
        this.noticeStatus=noticeFormDto.getNoticeStatus();
    }


}
