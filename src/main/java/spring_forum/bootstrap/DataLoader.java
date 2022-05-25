package spring_forum.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import spring_forum.domain.Comment;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.domain.User;
import spring_forum.domain.enums.Gender;
import spring_forum.domain.enums.NameColor;
import spring_forum.repositories.PostRepository;
import spring_forum.repositories.TagRepository;
import spring_forum.repositories.UserRepository;
import spring_forum.repositories.VerificationTokenRepository;
import spring_forum.services.CacheService;

import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Profile({"test", "default"})
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final CacheService cacheService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Loading Data");
        List<User> users = getUsers();
        userRepository.saveAll(users);
        postRepository.saveAll(getPosts(users));
    }

    @Transactional
    @PreDestroy
    void onStop() {
        removeData();
    }

    private List<User> getUsers() {
        User dan = new User();
        dan.setEmail("gogo@ya.ru");
        dan.setPassword(passwordEncoder.encode("dan"));
        dan.setModerator(true);
        dan.setName("Danya");
        dan.setPhoneNumber("+79875643232");
        dan.setGender(Gender.M);
        dan.setCountry("Russia");
        dan.setLanguage("ru");
        dan.setEnabled(true);
        dan.setNameColor(NameColor.BLUE);

        User kirill = new User();
        kirill.setEmail("kirill113@gmail.com");
        kirill.setPassword(passwordEncoder.encode("kir"));
        kirill.setModerator(false);
        kirill.setName("Kirill");
        kirill.setPhoneNumber("+79875467387");
        kirill.setGender(Gender.M);
        kirill.setCountry("Russia");
        kirill.setLanguage("ru");
        kirill.setNameColor(NameColor.BLACK);

        return List.of(dan, kirill);
    }

    private List<Post> getPosts(List<User> users) {

        Post firstPost = new Post();
        firstPost.setText("It is my first post!");
        firstPost.setTitle("First post");
        firstPost.setPostOwner(users.get(0));

        Post secondPost = new Post();
        secondPost.setText("This is the second post!");
        secondPost.setTitle("Second post");
        secondPost.setPostOwner(users.get(1));

        Tag firstTag = new Tag();
        firstTag.getPosts().add(firstPost);
        firstTag.setTag("firstTag");

        Tag secondTag = new Tag();
        secondTag.getPosts().add(firstPost);
        secondTag.setTag("secondTag");

        Comment comment = new Comment();
        comment.setPost(firstPost);
        comment.setText("First comment!");
        comment.setCommentOwner(users.get(0));

        firstPost.getTags().add(firstTag);
        firstPost.getTags().add(secondTag);
        firstPost.getComments().add(comment);

        secondPost.getTags().add(secondTag);

        return List.of(firstPost, secondPost);
    }

    private void removeData() {
        log.info("Removing data..");
        verificationTokenRepository.deleteAll();
        postRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
        cacheService.deleteAllKeys();
    }
}
