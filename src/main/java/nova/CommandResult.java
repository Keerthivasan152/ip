package nova;

/**
 * The outcome of one user command: the text to show the user, and whether the
 * reply reports a problem. The GUI uses the flag to style errors differently
 * from ordinary replies.
 *
 * @param text the reply text
 * @param isError whether the reply reports a problem the user should notice
 */
public record CommandResult(String text, boolean isError) {
    /**
     * Creates the result of a command that worked.
     *
     * @param text the reply text
     * @return a successful result
     */
    public static CommandResult ok(String text) {
        return new CommandResult(text, false);
    }

    /**
     * Creates the result of a command that failed or that warns the user.
     *
     * @param text the reply text
     * @return a failed result
     */
    public static CommandResult error(String text) {
        return new CommandResult(text, true);
    }
}
