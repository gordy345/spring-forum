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
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final PostService postService;
    private final Producer producer;

    public TagServiceImpl(TagRepository tagRepository, PostService postService, Producer producer) {
        this.tagRepository = tagRepository;
        this.postService = postService;
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
        if (tag.getPost().getTags().contains(tag)) {
            throw new ExistsException("Tag you're trying to save already exists for post with ID = "
                    + tag.getPost().getId());
        }
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag update(Tag tag) {
        log.info("Updating tag with ID = " + tag.getId());
        Tag tagByID = findByID(tag.getId());
        if (tag.getPost().getTags().contains(tag)) {
            throw new ExistsException("Tag you're trying to save already exists for post with ID = "
                    + tag.getPost().getId());
        }
        tagByID.setTag(tag.getTag());
        tagByID.setPost(tag.getPost());
        return tagByID;
    }

    @Override
    @Transactional
    public void deleteByID(Long id) {
        log.info("Deleting tag with ID = " + id);
        Tag tag = findByID(id);
        tagRepository.delete(tag);
    }
}
