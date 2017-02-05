package org.openhubframework.openhub.core.common.dao;

import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.exception.MultipleDataFoundException;
import org.openhubframework.openhub.api.exception.NoDataFoundException;

/**
 * JPA implementation of {@link NodeDao} operation with {@link Node}.
 *
 * @author Roman Havlicek
 * @since 2.0.0
 */
@Repository
public class NodeDaoJpaImpl implements NodeDao {

    @PersistenceContext(unitName = DbConst.UNIT_NAME)
    private EntityManager em;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void insert(Node node) {
        Assert.notNull(node, "node must not be null");
        Assert.isNull(node.getNodeId(), "only new node can be insert");

        em.persist(node);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void update(Node node) {
        Assert.notNull(node, "node must not be null");
        Assert.notNull(node.getNodeId(), "only new node can be update");

        em.merge(node);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void delete(Node node) {
        Assert.notNull(node, "node must not be null");
        Assert.notNull(node.getNodeId(), "only new node can be delete");

        em.remove(node);
    }

    @Override
    public List<Node> getAllNodes() {
        String sqlQuery = "SELECT n FROM " + Node.class.getName() + " n ORDER BY n.name";
        TypedQuery<Node> query = em.createQuery(sqlQuery, Node.class);
        return query.getResultList();
    }

    @Override
    public Node getNodeById(Long nodeId) {
        Assert.notNull(nodeId, "nodeId must not be null");

        Node result = em.find(Node.class, nodeId);
        if (result == null) {
            throw new NoDataFoundException(Node.class.getSimpleName() + " not found by identifier '" + nodeId + "'.");
        }
        return result;
    }

    @Nullable
    @Override
    public Node findNodeByCode(String code) {
        Assert.hasText(code, "code must not be empty");

        String sqlQuery = "SELECT n FROM " + Node.class.getName() + " n WHERE n.code = :code";
        TypedQuery<Node> query = em.createQuery(sqlQuery, Node.class);
        query.setParameter("code", code);

        List<Node> result = query.getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            throw new MultipleDataFoundException("For code " + code + " found more then one nodes ("
                    + result.size() + ").");
        }
    }
}
