package spring_forum;

import spring_forum.domain.Comment;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.domain.User;

import java.util.Collections;

public class TestConstants {

    public static final Long NEGATIVE_ID = -1L;
    public static final String PLUG = "Test";
    public static final User USER = User.builder().id(1L).name("Dan").email("gogo@ya.ru").build();
    public static final Post POST = Post.builder().id(1L).title(PLUG).text(PLUG)
            .postOwner(USER).build();
    public static final Comment COMMENT = Comment.builder().id(1L).text(PLUG)
            .post(POST).build();
    public static final Tag TAG = Tag.builder().id(1L).tag(PLUG)
            .posts(Collections.singleton(POST)).build();


}
