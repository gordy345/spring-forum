package spring_forum.utils;

public class ExceptionMessages {

    public static final String NO_USERS = "There are no users now.";
    public static final String USER_NOT_FOUND_BY_EMAIL = "User with this email doesn't exist. Email value: ";
    public static final String USER_NOT_FOUND_BY_ID = "User with this ID doesn't exist. ID value: ";
    public static final String USER_EXISTS_WITH_EMAIL = "User with this email already exists. Email value: ";
    public static final String AVATAR_NOT_FOUND = "Avatar not found.";

    public static final String NO_POSTS = "There are no posts now.";
    public static final String NO_POSTS_FOR_USER = "There are no posts for this user. User ID value: ";
    public static final String NO_POSTS_WITH_TAG = "There are no posts with this tag. Tag value: ";
    public static final String POST_NOT_FOUND_BY_TITLE = "Post with this title doesn't exist. Title value: ";
    public static final String POST_NOT_FOUND_BY_ID = "Post with this ID doesn't exist. ID value: ";
    public static final String POST_EXISTS_WITH_TITLE = "Post with this title already exists. Title value: ";

    public static final String NO_COMMENTS_FOR_POST = "There are no comments for this post. Post ID value: ";
    public static final String COMMENT_NOT_FOUND_BY_ID = "Comment with this ID doesn't exist. ID value: ";

    public static final String NO_TAGS_FOR_POST = "There are no tags for this post. Post ID value: ";
    public static final String TAG_NOT_FOUND_BY_ID = "Tag with this ID doesn't exist. ID value: ";
    public static final String TAG_NOT_FOUND_BY_VALUE = "Tag with this value doesn't exist. Tag value: ";
    public static final String TAG_EXISTS_FOR_POST = "Tag you're trying to save already exists for post with ID = ";
    public static final String POST_DOESNT_CONTAIN_TAG = "This post doesn't contain this tag. Post ID value: ";

    public static final String TOKEN_EXPIRED = "Token is expired.";
}
