package org.openhubframework.openhub.core.common.dao;

import java.util.List;
import javax.annotation.Nullable;

import org.openhubframework.openhub.api.entity.Node;

/**
 * Dao interface for operation with {@link Node}.
 *
 * @author Roman Havlicek
 * @since 2.0.0
 */
public interface NodeDao {

    /**
     * Insert new {@link Node}.
     *
     * @param node node
     */
    void insert(Node node);

    /**
     * Update existing {@link Node}.
     *
     * @param node node
     */
    void update(Node node);

    /**
     * Update existing {@link Node}.
     *
     * @param node node
     */
    void delete(Node node);

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
     * Find node by {@link Node#code}.
     *
     * @param code code
     * @return found node, {@code NULL} - no node found by codeø
     */
    @Nullable
    Node findNodeByCode(String code);
}
