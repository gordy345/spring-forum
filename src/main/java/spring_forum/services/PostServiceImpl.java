package spring_forum.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.domain.User;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.rabbitMQ.Producer;
import spring_forum.repositories.PostRepository;

import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static spring_forum.utils.CacheKeys.*;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final CacheService cacheService;
    private final Producer producer;

    public PostServiceImpl(PostRepository postRepository, UserService userService, CacheService cacheService, Producer producer) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.cacheService = cacheService;
        this.producer = producer;
    }

    @Override
    @Transactional
    public Set<Post> findAll() {
        log.info("Finding all posts");
        Set<Post> posts = new LinkedHashSet<>();
        postRepository.findAll().forEach(posts::add);
        if (posts.size() == 0) {
            String message = "There are no posts now.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return posts;
    }

    @Override
    @Transactional
    public Set<Post> findPostsForUserByID(Long userId) {
        log.info("Finding posts for user with ID = " + userId);
        User user = userService.findByID(userId);
        Set<Post> posts = user.getPosts();
        if (posts.size() == 0) {
            String message = "There are no posts for this user.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return posts;
    }

    @Override
    @Transactional
    public Set<Post> findPostsByTag(String tag) {
        log.info("Finding posts with tag: " + tag);
        Set<Post> posts = findAll().stream()
                .filter(post -> post.getTags().contains(Tag.builder().tag(tag).build()))
                .collect(Collectors.toSet());
        if (posts.size() == 0) {
            String message = "There are no posts with this tag.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return posts;
    }

    @Override
    @Transactional
    public Post findPostByTitle(String title) {
        log.info("Finding post with title: " + title);
        Optional<Post> postOptional = postRepository.findPostByTitle(title);
        if (postOptional.isEmpty()) {
            String message = "Post with title \"" + title + "\" doesn't exist.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return postOptional.get();
    }

    @Override
    @Transactional
    public Post findByID(Long id) {
        log.info("Finding post with ID = " + id);
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            String message = "There is no post with ID = " + id;
            producer.send(message);
            throw new NotFoundException(message);
        }
        return postOptional.get();
    }

    @Override
    @Transactional
    public Post save(Post post) {
        log.info("Saving post with title: " + post.getTitle());
        if (postRepository.findPostByTitle(post.getTitle()).isPresent()) {
            throw new ExistsException("Post with title \"" + post.getTitle() + "\" already exists.");
        }
        cacheService.remove(ALL_POSTS,
                POSTS_FOR_USER + post.getPostOwner().getId());
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post update(Post post) {
        log.info("Updating post with ID = " + post.getId());
        Post postByID = findByID(post.getId());
        if (!postByID.getTitle().equals(post.getTitle()) &&
                postRepository.findPostByTitle(post.getTitle()).isPresent()) {
            throw new ExistsException("Post with title \"" + post.getTitle() + "\" already exists.");
        }
        cacheService.remove(ALL_POSTS,
                POSTS_FOR_USER + postByID.getPostOwner().getId(),
                POST_BY_ID + postByID.getId(),
                POST_BY_TITLE + postByID.getTitle());
        cacheService.remove(postByID.getTags()
                .stream()
                .map(tag -> POSTS_BY_TAG + tag.getTag())
                .collect(Collectors.toList()));
        postByID.setTitle(post.getTitle());
        postByID.setText(post.getText());
        return postByID;
    }

    @Override
    @Transactional
    public Post deleteByID(Long id) {
        log.info("Deleting post with ID = " + id);
        Post post = findByID(id);
        cacheService.remove(ALL_POSTS,
                POSTS_FOR_USER + post.getPostOwner().getId(),
                POST_BY_ID + post.getId(),
                POST_BY_TITLE + post.getTitle(),
                TAGS_FOR_POST + id,
                COMMENTS_FOR_POST + id);
        cacheService.remove(post.getTags()
                .stream()
                .map(tag -> POSTS_BY_TAG + tag.getTag())
                .collect(Collectors.toList()));
        postRepository.delete(post);
        return post;
    }

}
