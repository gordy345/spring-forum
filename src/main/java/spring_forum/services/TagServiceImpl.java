package spring_forum.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.repositories.TagRepository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final PostService postService;

    public TagServiceImpl(TagRepository tagRepository, PostService postService) {
        this.tagRepository = tagRepository;
        this.postService = postService;
    }

    @Override
    @Transactional
    public Set<Tag> findTagsForPostByID(Long postID) {
        Post post = postService.findByID(postID);
        log.info("Finding tags for post with ID = " + postID);
        Set<Tag> tags = post.getTags();
        if (tags.size() == 0) {
            //todo impl exception handling
            throw new NotFoundException("There are no tags for this post.");
        }
        return tags;
    }

    @Override
    @Transactional
    public Tag findByID(Long id) {
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isEmpty()) {
            //todo impl exception handling
            throw new NotFoundException("Tag with ID = " + id + " doesn't exist.");
        }
        return tagOptional.get();
    }

    @Override
    @Transactional
    public Tag save(Tag tag) {
        log.info("Saving tag: " + tag.getTag());
        if (tag.getPost().getTags().contains(Tag.builder().tag(tag.getTag()).build())) {
            //todo impl exception handling
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
        if (tag.getPost().getTags().contains(Tag.builder().tag(tag.getTag()).build())) {
            //todo impl exception handling
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
