package spring_forum.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.rabbitMQ.Producer;
import spring_forum.repositories.TagRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static spring_forum.utils.CacheKeys.POSTS_BY_TAG;
import static spring_forum.utils.CacheKeys.TAGS_FOR_POST;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final PostService postService;
    private final CacheService cacheService;
    private final Producer producer;

    public TagServiceImpl(TagRepository tagRepository, PostService postService, CacheService cacheService, Producer producer) {
        this.tagRepository = tagRepository;
        this.postService = postService;
        this.cacheService = cacheService;
        this.producer = producer;
    }

    @Override
    @Transactional
    public Set<Tag> findTagsForPostByID(Long postID) {
        log.info("Finding tags for post with ID = " + postID);
        Post post = postService.findByID(postID);
        Set<Tag> tags = post.getTags();
        if (tags.size() == 0) {
            String message = "There are no tags for this post.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return tags;
    }

    @Override
    @Transactional
    public Tag deleteTagForPost(Long tagId, Long postId) {
        log.info("Deleting tag with ID = " + tagId + " for post with ID = " + postId);
        Tag tag = findByID(tagId);
        Post post = postService.findByID(postId);
        post.getTags().remove(tag);
        if (tag.getPosts().size() == 1) {
            tagRepository.deleteById(tagId);
        }
        cacheService.remove(TAGS_FOR_POST + post.getId(), POSTS_BY_TAG + tag.getTag());
        return tag;
    }

    @Override
    @Transactional
    public Tag findByID(Long id) {
        log.info("Finding tag with ID = " + id);
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isEmpty()) {
            String message = "Tag with ID = " + id + " doesn't exist.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return tagOptional.get();
    }

    @Override
    @Transactional
    public Tag save(Tag tag) {
        log.info("Saving tag: " + tag.getTag());
        Post post = tag.getPosts().iterator().next();
        if (post.getTags().contains(tag)) {
            throw new ExistsException("Tag you're trying to save already exists for post with ID = "
                    + post.getId());
        }
        cacheService.remove(TAGS_FOR_POST + post.getId(),
                POSTS_BY_TAG + tag.getTag());
        Tag savedTag = tagRepository.save(tag);
        post.getTags().add(savedTag);
        return savedTag;
    }

    @Override
    @Transactional
    public Tag update(Tag tag) {
        log.info("Updating tag with ID = " + tag.getId());
        Tag tagByID = findByID(tag.getId());
        Post post = tag.getPosts().iterator().next();
        if (!tagByID.getPosts().contains(post)) {
            String message = "Post with ID = " + post.getId() + " doesn't contain this tag";
            producer.send(message);
            throw new NotFoundException(message);
        }
        if (post.getTags().contains(tag)) {
            throw new ExistsException("Tag you're trying to save already exists for post with ID = "
                    + post.getId());
        }
        cacheService.remove(TAGS_FOR_POST + post.getId(),
                POSTS_BY_TAG + tag.getTag(), POSTS_BY_TAG + tagByID.getTag());
        tagByID.setTag(tag.getTag());
        return tag;
    }

    @Override
    @Transactional
    public Tag deleteByID(Long id) {
        log.info("Deleting tag with ID = " + id);
        Tag tag = findByID(id);
        List<String> keysList = tag.getPosts()
                .stream()
                .map(post -> TAGS_FOR_POST + post.getId())
                .collect(Collectors.toList());
        cacheService.remove(keysList);
        cacheService.remove(POSTS_BY_TAG + tag.getTag());
        tagRepository.delete(tag);
        return tag;
    }
}
