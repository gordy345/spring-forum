package spring_forum.utils;

public class CacheKeys {

    private CacheKeys() {
    }

    public static final String ALL_USERS = "allUsers";
    public static final String USER_BY_ID = "userById_";
    public static final String USER_BY_NAME = "userByName_";
    public static final String AVATAR_FOR_USER = "avatarForUser_";

    public static final String ALL_POSTS = "allPosts";
    public static final String POSTS_FOR_USER = "postsForUser_";
    public static final String POSTS_BY_TAG = "postsByTag_";
    public static final String POST_BY_ID = "postById_";
    public static final String POST_BY_TITLE = "postByTitle_";

    public static final String COMMENT_BY_ID = "commentById_";
    public static final String COMMENTS_FOR_POST = "commentsForPost_";

    public static final String TAGS_FOR_POST = "tagsForPost_";
    public static final String TAG_BY_ID = "tagById_";
}
