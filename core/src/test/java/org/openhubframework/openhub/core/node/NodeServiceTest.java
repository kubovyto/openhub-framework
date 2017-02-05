package org.openhubframework.openhub.core.node;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.entity.NodeState;
import org.openhubframework.openhub.api.exception.NoDataFoundException;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.spi.node.NodeService;

/**
 * Test suite for {@link NodeService}.
 *
 * @author Roman Havlicek
 * @see NodeService
 * @see NodeServiceImpl
 * @since 2.0.0
 */
public class NodeServiceTest extends AbstractCoreDbTest {

    @Autowired
    private NodeService nodeService;

    @Value("${" + CoreProps.NODE_ACTUAL_INSTANCE_CODE + "}")
    private String actualNodeCode;

    /**
     * Test method {@link NodeService#insertNode(Node)}.
     */
    @Test
    public void testInsertNode() {
        Node firstNode = new Node("codeFirst", "nameFirst");
        Node secondNode = new Node("codeSecond", "nameSecond", NodeState.STOPPED);
        secondNode.setDescription("descriptionSecond");

        nodeService.insertNode(firstNode);
        nodeService.insertNode(secondNode);

        //with actual node create after start is three
        assertThat(nodeService.getAllNodes().size(), is(2));

        Node testNode = nodeService.getNodeById(firstNode.getNodeId());
        assertThat(testNode.getCode(), is("codeFirst"));
        assertThat(testNode.getName(), is("nameFirst"));
        assertThat(testNode.getDescription(), nullValue());
        assertThat(testNode.getState(), is(NodeState.RUN));

        testNode = nodeService.getNodeById(secondNode.getNodeId());
        assertThat(testNode.getCode(), is("codeSecond"));
        assertThat(testNode.getName(), is("nameSecond"));
        assertThat(testNode.getDescription(), is("descriptionSecond"));
        assertThat(testNode.getState(), is(NodeState.STOPPED));
    }

    /**
     * Test method {@link NodeService#updateNode(Node)}.
     */
    @Test
    public void testUpdateNode() {
        Node node = new Node("code", "name", NodeState.STOPPED);
        node.setDescription("description");

        //insert node
        nodeService.insertNode(node);

        node.setCode("codeUpdated");
        node.setName("nameUpdated");
        node.setDescription("descriptionUpdated");
        node.run();
        nodeService.updateNode(node);

        Node testNode = nodeService.getNodeById(node.getNodeId());
        assertThat(testNode.getCode(), is("codeUpdated"));
        assertThat(testNode.getName(), is("nameUpdated"));
        assertThat(testNode.getDescription(), is("descriptionUpdated"));
        assertThat(testNode.getState(), is(NodeState.RUN));
    }

    /**
     * Test method {@link NodeService#deleteNode(Node)}.
     */
    @Test
    @Transactional
    public void testDeleteNode() {
        Node node = new Node("code", "name", NodeState.STOPPED);
        node.setDescription("description");

        //insert node
        nodeService.insertNode(node);

        //test if found this node
        node = nodeService.getNodeById(node.getNodeId());

        //delete node
        nodeService.deleteNode(node);

        try {
            nodeService.getNodeById(node.getNodeId());
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(NoDataFoundException.class));
        }
    }

    /**
     * Test method {@link NodeService#getAllNodes()}.
     */
    @Test
    public void testGetAllNodes() {
        Node firstNode = new Node("CodeFirst", "NameFirst");
        Node secondNode = new Node("CodeSecond", "NameSecond");

        nodeService.insertNode(firstNode);
        nodeService.insertNode(secondNode);

        List<Node> allNodes = nodeService.getAllNodes();

        assertThat(allNodes.size(), is(2));

        assertThat(allNodes.get(0).getCode(), is("CodeFirst"));
        assertThat(allNodes.get(0).getName(), is("NameFirst"));
        assertThat(allNodes.get(0).getDescription(), nullValue());
        assertThat(allNodes.get(0).getState(), is(NodeState.RUN));

        assertThat(allNodes.get(1).getCode(), is("CodeSecond"));
        assertThat(allNodes.get(1).getName(), is("NameSecond"));
        assertThat(allNodes.get(1).getDescription(), nullValue());
        assertThat(allNodes.get(1).getState(), is(NodeState.RUN));
    }

    /**
     * Test method {@link NodeService#getNodeById(Long)}.
     */
    @Test
    public void testGetNodeById() {
        Node node = new Node("code", "name", NodeState.STOPPED);
        node.setDescription("description");

        nodeService.insertNode(node);

        Node testNode = nodeService.getNodeById(node.getNodeId());
        assertThat(testNode.getNodeId(), is(node.getNodeId()));
        assertThat(testNode.getCode(), is("code"));
        assertThat(testNode.getName(), is("name"));
        assertThat(testNode.getDescription(), is("description"));
        assertThat(testNode.getState(), is(NodeState.STOPPED));
    }

    /**
     * Test method {@link NodeService#getActualNode()}.
     */
    @Test
    public void testGetActualNode() {
        //actual node for tests is defined in configuration file

        Node testNode = nodeService.getActualNode();
        assertThat(testNode.getCode(), is(actualNodeCode));
        assertThat(testNode.getName(), is(actualNodeCode));
        assertThat(testNode.getDescription(), nullValue());
        assertThat(testNode.getState(), is(NodeState.RUN));
    }
}
