package org.openhubframework.openhub.api.entity;

/**
 * Contains all states for {@link Node}
 *
 * @author Roman Havlicek
 * @see Node#state
 * @since 2.0.0
 */
public enum NodeState {

    /**
     * Node process new and existing (saved messages in ESB) messages.
     */
    RUN(true, true),

    /**
     * Node process only existing messages. New messages/requests are rejected.
     */
    PROCESS_EXISTING_MESSAGES(false, true),

    /**
     * Node not process existing messages and new messages.
     */
    STOPPED(false, false);

    /**
     * {@code true} node process new incoming messages, {@code false} - otherwise.
     */
    private final boolean processNewMessages;

    /**
     * {@code true} node process existing saved messages, {@code false} - otherwise.
     */
    private final boolean processExistingMessages;

    /**
     * New instance.
     *
     * @param processNewMessages      @code true} node process new incoming messages, {@code false} - otherwise
     * @param processExistingMessages {@code true} node process existing saved messages, {@code false} - otherwise
     */
    NodeState(boolean processNewMessages, boolean processExistingMessages) {
        this.processNewMessages = processNewMessages;
        this.processExistingMessages = processExistingMessages;
    }

    /**
     * Gets if {@link Node} in this state process new incoming messages.
     *
     * @return {@code true} node process new incoming messages, {@code false} - oterwise
     */
    public boolean isProcessNewMessages() {
        return processNewMessages;
    }

    /**
     * Gets if {@link Node} in this state process messages existing saved in ESB (from SEDA or
     * {@link MsgStateEnum#PARTLY_FAILED}, {@link MsgStateEnum#POSTPONED}, etc.).
     *
     * @return {@code true} node process existing saved messages, {@code false} - otherwise
     */
    public boolean isProcessExistingMessages() {
        return processExistingMessages;
    }
}
