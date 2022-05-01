package spring_forum;

import spring_forum.domain.*;
import spring_forum.dtos.*;

import java.util.Collections;

public class TestConstants {

    public static final Long NEGATIVE_ID = -1L;
    public static final String PLUG = "Test";
    public static final User USER = User.builder().id(1L).name("Dan").email("gogo@ya.ru")
            .isModerator(true).gender(Gender.M).phoneNumber("+7").country("country").imageUrl("url")
            .password("password").language("language").enabled(true).rating(0).build();

    public static final UserDTO USER_DTO = UserDTO.builder()
            .id(USER.getId()).name(USER.getName()).email(USER.getEmail())
            .isModerator(USER.isModerator()).gender(USER.getGender())
            .phoneNumber(USER.getPhoneNumber()).country(USER.getCountry())
            .language(USER.getLanguage()).enabled(USER.isEnabled()).rating(USER.getRating())
            .build();

    public static final User USER_EMPTY = User.builder().build();

    public static final Post POST = Post.builder().id(1L).title(PLUG).text(PLUG)
            .postOwner(USER).build();

    public static final PostDTO POST_DTO = PostDTO.builder().id(1L).title(PLUG).text(PLUG).build();

    public static final Post POST_EMPTY = Post.builder().build();

    public static final Comment COMMENT = Comment.builder().id(1L).text(PLUG)
            .commentOwner(USER).post(POST).build();

    public static final CommentDTO COMMENT_DTO = CommentDTO.builder().id(1L).text(PLUG)
            .commentOwnerID(USER.getId()).postID(POST.getId()).build();

    public static final Comment COMMENT_EMPTY = Comment.builder().build();

    public static final Tag TAG = Tag.builder().id(1L).tag(PLUG)
            .posts(Collections.singleton(POST)).build();

    public static final TagDTO TAG_DTO = TagDTO.builder().id(1L).tag(PLUG).build();

    public static final Tag TAG_EMPTY = Tag.builder().build();

    public static final RegisterDTO REGISTER_DTO = RegisterDTO.builder().name(USER.getName())
            .email(USER.getEmail()).isModerator(USER.isModerator())
            .gender(USER.getGender()).phoneNumber(USER.getPhoneNumber())
            .password(USER.getPassword()).country(USER.getCountry())
            .language(USER.getLanguage()).build();

    public static final VerificationToken VERIFICATION_TOKEN = new VerificationToken(PLUG, USER);


}
