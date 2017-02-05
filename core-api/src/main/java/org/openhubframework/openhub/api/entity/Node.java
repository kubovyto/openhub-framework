package org.openhubframework.openhub.api.entity;

import javax.annotation.Nullable;
import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.common.HumanReadable;

/**
 * Contains information about one node in cluster.
 *
 * @author Roman Havlicek
 * @since 2.0.0
 */
@Entity
@Table(name = "node",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_node_code", columnNames = {"code"}),
                @UniqueConstraint(name = "uq_node_name", columnNames = {"name"})})
public class Node implements HumanReadable {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);

    /**
     * Identifier.
     */
    @Id
    @Column(name = "node_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long nodeId;

    /**
     * Unique node code.
     */
    @Column(name = "code", length = 256, nullable = false, unique = true)
    private String code;

    /**
     * Unique node name.
     */
    @Column(name = "name", length = 256, nullable = false, unique = true)
    private String name;

    /**
     * Description.
     */
    @Column(name = "description", length = 2056, nullable = true)
    private String description;

    /**
     * Node state.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 64, nullable = false)
    private NodeState state;

    /**
     * New instance only for JPA.
     */
    protected Node() {
    }

    /**
     * New instance with {@link NodeState#RUN} state.
     *
     * @param code code
     * @param name name
     */
    public Node(String code, String name) {
        this(code, name, NodeState.RUN);
    }

    /**
     * New instance.
     *
     * @param code  code
     * @param name  name
     * @param state state
     */
    public Node(String code, String name, NodeState state) {
        Assert.hasText(code, "code must not be empty");
        Assert.hasText(name, "name must not be empty");
        Assert.notNull(state, "state must not be null");

        this.code = code;
        this.name = name;
        this.state = state;
    }

    //-------------------------------------------------- STATE ---------------------------------------------------------

    /**
     * Node process all messages (existing and new) {@link NodeState#RUN}.
     */
    public void run() {
        setState(NodeState.RUN);
    }

    /**
     * Node process only existing messages (new message will be rejected)
     * {@link NodeState#PROCESS_EXISTING_MESSAGES}.
     */
    public void processOnlyExistingMessage() {
        setState(NodeState.PROCESS_EXISTING_MESSAGES);
    }

    /**
     * Node is stopped (new message will be rejected and existing is not processing)
     * {@link NodeState#STOPPED}.
     */
    public void stop() {
        setState(NodeState.STOPPED);
    }

    /**
     * Is node for this instance stopping?
     *
     * @return {@code true} if node is in "stopping mode" otherwise {@code false}
     * @see NodeState#STOPPED
     */
    public boolean isStopped() {
        return getState().equals(NodeState.STOPPED);
    }

    /**
     * Is node process new messages.
     *
     * @return {@code true} node process new messages, {@code false} - otherwise
     * @see NodeState#RUN
     */
    public boolean isProcessNewMessages() {
        return getState().isProcessNewMessages();
    }

    /**
     * Is node process existing message (from SEDA or {@link MsgStateEnum#PARTLY_FAILED},
     * {@link MsgStateEnum#POSTPONED}, etc.).
     *
     * @return {@code true} process existing message, {@code false} - otherwise
     * @see NodeState#PROCESS_EXISTING_MESSAGES
     */
    public boolean isProcessExistingMessages() {
        return getState().isProcessExistingMessages();
    }

    //--------------------------------------------------- SET / GET ----------------------------------------------------

    /**
     * Gets node identifier.
     *
     * @return node identifier, {@code NULL} - node is not persisted
     */
    @Nullable
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * Gets code of this node.
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets code of this node.
     *
     * @param code code
     */
    public void setCode(String code) {
        Assert.hasText(code, "code must not be empty");

        this.code = code;
    }

    /**
     * Gets name of this node.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name ot this node.
     *
     * @param name name
     */
    public void setName(String name) {
        Assert.hasText(name, "name must not be empty");

        this.name = name;
    }

    /**
     * Gets description.
     *
     * @return description, {@code NULL} - node has no description
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Sets description
     *
     * @param description description, {@code NULL} - node has no description
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * Gets state of this node.
     *
     * @return state
     */
    public NodeState getState() {
        return state;
    }

    /**
     * Sets state of this node.
     *
     * @param state state
     */
    private void setState(NodeState state) {
        Assert.notNull(state, "state must not be null");

        this.state = state;

        LOG.info("State of node {} was changed to {}", toHumanString(), getState());
    }

    //--------------------------------------------- TOSTRING / HASH / EQUALS -------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        return new EqualsBuilder()
                .append(getNodeId(), node.getNodeId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getNodeId())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("nodeId", nodeId)
                .append("code", code)
                .append("name", name)
                .append("description", StringUtils.substring(description, 100))
                .append("state", state)
                .toString();
    }

    @Override
    public String toHumanString() {
        return "(code = " + getCode() + ")";
    }
}
