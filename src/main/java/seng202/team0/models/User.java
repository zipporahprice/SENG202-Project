package seng202.team0.models;

/**
 * Class for User model object.
 *
 * @author Angelica Silva
 */
public class User {
    private int id;
    private final String username; //final means can only be assigned once
    private final String password;
    // TODO think about making usernames unique, and passwords encrypted text

    /**
     * Constructor, creates new User object.
     *
     * @param id user id
     * @param username username
     * @param password plaintext
     */
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor without id param, creates new User object.
     *
     * @param username username
     * @param password plaintext
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Sets User id for database.
     *
     * @param id id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets user's id.
     *
     * @return user id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets user's username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets user's password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }
}
