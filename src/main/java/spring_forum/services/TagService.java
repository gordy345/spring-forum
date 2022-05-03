package spring_forum.services;

import spring_forum.domain.Tag;

import java.util.List;
import java.util.Set;

public interface TagService extends CrudService<Tag, Long> {

    Tag findTagByValue(String tagValue);

    Set<Tag> findTagsForPostByID(Long postID);

    Tag deleteTagForPost(Long tagId, Long postId);

    void deleteAll(List<Tag> tags);

}
