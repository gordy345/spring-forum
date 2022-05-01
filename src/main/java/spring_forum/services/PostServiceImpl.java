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
import spring_forum.repositories.TagRepository;

import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static spring_forum.utils.CacheKeys.*;
import static spring_forum.utils.ExceptionMessages.*;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final TagRepository tagRepository;
    private final CacheService cacheService;
    private final Producer producer;

    public PostServiceImpl(PostRepository postRepository, UserService userService, TagRepository tagRepository, CacheService cacheService, Producer producer) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.tagRepository = tagRepository;
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
            producer.send(NO_POSTS);
            throw new NotFoundException(NO_POSTS);
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
            String message = NO_POSTS_FOR_USER + userId;
            producer.send(message);
            throw new NotFoundException(message);
        }
        return posts;
    }

    @Override
    @Transactional
    public Set<Post> findPostsByTag(String tag) {
        log.info("Finding posts with tag: " + tag);
        log.info("Finding tag with tag value = " + tag);
        Tag foundTag = tagRepository.findTagByTag(tag);
        String errorMessage = NO_POSTS_WITH_TAG + tag;
        if (foundTag == null) {
            producer.send(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        Set<Post> posts = foundTag.getPosts();
        if (posts.size() == 0) {
            producer.send(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        return posts;
    }

    @Override
    @Transactional
    public Post findPostByTitle(String title) {
        log.info("Finding post with title: " + title);
        Optional<Post> postOptional = postRepository.findPostByTitle(title);
        if (postOptional.isEmpty()) {
            String message = POST_NOT_FOUND_BY_TITLE + title;
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
            String message = POST_NOT_FOUND_BY_ID + id;
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
            throw new ExistsException(POST_EXISTS_WITH_TITLE + post.getTitle());
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
            throw new ExistsException(POST_EXISTS_WITH_TITLE + post.getTitle());
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
                COMMENTS_FOR_POST + id,
                COMMENTS_COUNT_FOR_POST + id);
        cacheService.remove(post.getTags()
                .stream()
                .map(tag -> POSTS_BY_TAG + tag.getTag())
                .collect(Collectors.toList()));
        List<Tag> tagsToDelete = post.getTags()
                .stream()
                .filter(tag -> tag.getPosts().size() == 1)
                .collect(Collectors.toList());
        post.getTags().clear();
        postRepository.delete(post);
        tagRepository.deleteAll(tagsToDelete);
        return post;
    }

}
