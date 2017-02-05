package org.openhubframework.openhub.spi.node;

import java.util.List;

import org.openhubframework.openhub.api.entity.Node;

/**
 * Service for manipulating with {@link Node}.
 *
 * @author Roman Havlicek
 * @see Node
 * @since 2.0.0
 */
public interface NodeService {

    /**
     * Insert new {@link Node}.
     *
     * @param node node
     */
    void insertNode(Node node);

    /**
     * Update existing {@link Node}.
     *
     * @param node node
     */
    void updateNode(Node node);

    /**
     * Update existing {@link Node}.
     *
     * @param node node
     */
    void deleteNode(Node node);

    /**
     * Get all {@link Node}s.
     *
     * @return all nodes
     */
    List<Node> getAllNodes();

    /**
     * Gets node by identifier.
     *
     * @param nodeId node identifier
     * @return found node
     */
    Node getNodeById(Long nodeId);

    /**
     * Get actual node for this application server instance.
     *
     * @return node for this server instance
     */
    Node getActualNode();
}
