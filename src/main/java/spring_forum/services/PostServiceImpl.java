package spring_forum.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.domain.User;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.repositories.PostRepository;

import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public PostServiceImpl(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Set<Post> findAll() {
        log.info("Finding all posts");
        Set<Post> posts = new LinkedHashSet<>();
        postRepository.findAll().forEach(posts::add);
        if (posts.size() == 0) {
            throw new NotFoundException("There are no posts now.");
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
            // todo add exceptions handling
            throw new NotFoundException("There are no posts for this user.");
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
            // todo add exceptions handling
            throw new NotFoundException("There are no posts with this tag.");
        }
        return posts;
    }

    @Override
    @Transactional
    public Post findPostByTitle(String title) {
        log.info("Finding post with title: " + title);
        Optional<Post> postOptional = postRepository.findPostByTitle(title);
        if (postOptional.isEmpty()) {
            // todo add exceptions handling
            throw new NotFoundException("Post with title \"" + title + "\" doesn't exist.");
        }
        return postOptional.get();
    }

    @Override
    @Transactional
    public Post findByID(Long id) {
        log.info("Finding post with ID = " + id);
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            // todo add exceptions handling
            throw new NotFoundException("There is no post with ID = " + id);
        }
        return postOptional.get();
    }

    @Override
    @Transactional
    public Post save(Post post) {
        log.info("Saving post with title: " + post.getTitle());
        if (postRepository.findPostByTitle(post.getTitle()).isPresent()) {
            //impl exceptions handling
            throw new ExistsException("Post with title \"" + post.getTitle() + "\" already exists.");
        }
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post update(Post post) {
        log.info("Updating post with ID = " + post.getId());
        Post postByID = findByID(post.getId());
        if (!postByID.getTitle().equals(post.getTitle()) &&
                postRepository.findPostByTitle(post.getTitle()).isPresent()) {
            //impl exceptions handling
            throw new ExistsException("Post with title \"" + post.getTitle() + "\" already exists.");
        }
        postByID.setTitle(post.getTitle());
        postByID.setText(post.getText());
        return postByID;
    }

    @Override
    @Transactional
    public void deleteByID(Long id) {
        log.info("Deleting post with ID = " + id);
        Post post = findByID(id);
        postRepository.delete(post);
    }

}