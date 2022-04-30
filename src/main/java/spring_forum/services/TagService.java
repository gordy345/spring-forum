package spring_forum.services;

import spring_forum.domain.Tag;

import java.util.Set;

public interface TagService extends CrudService<Tag, Long> {

    Set<Tag> findTagsForPostByID(Long postID);

    Tag deleteTagForPost(Long tagId, Long postId);

}
