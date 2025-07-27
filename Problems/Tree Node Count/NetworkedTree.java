//  Author: Tushar Jaiswal
//  Creation Date: 2025-07-26

// Given an n-ary tree representing a cluster of nodes, with one designated root node. Each node in the tree can only communicate with its parent or its children, except for the root node, which can receive messages from an external source. The task is to send a message to the root node, prompting it to count the total number of nodes in the tree and print the result.

// You are required to implement a Node class with the following APIs:

// receiveMessage(from_node_id, message): A method to handle incoming messages.
// sendMessage(to_node_id, message): A method to send messages to other nodes. The implementation of this method is not required; it can simply be called.

// You can assume that when a node receives a message via sendMessage, it will automatically execute the receiveMessage() method. Initially, the root node will receive a call to receiveMessage(null, message).

// Follow-up:
// If there is a possibility of network failure, resulting in retries, how can we ensure that nodes are not counted multiple times? In other words, how do we ensure idempotency.

import java.util.*;

class Node {
    int nodeId;
    Node parent;
    List<Node> children;
    int totalCount = 1; // includes self
    int pendingCount = 0;
    Set<String> receivedMessages = new HashSet<>();

    Map<Integer, Node> network; // Simulates the communication network
    Integer finalResult = null; // Used for testing

    public Node(int nodeId) {
        this.nodeId = nodeId;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        child.parent = this;
        this.children.add(child);
    }

    public void sendMessage(int toNodeId, String message) {
        if (network != null && network.containsKey(toNodeId)) {
            network.get(toNodeId).receiveMessage(this.nodeId, message);
        }
    }

    public void receiveMessage(Integer fromNodeId, String message) {
        String msgId = fromNodeId + "|" + message;
        if (receivedMessages.contains(msgId)) {
            return; // Idempotency: don't process same message twice
        }
        receivedMessages.add(msgId);

        if (message.equals("COUNT")) {
            if (children.isEmpty()) {
                if (parent != null) {
                    sendMessage(parent.nodeId, "COUNT_RESULT:1");
                }
            } else {
                pendingCount = children.size();
                for (Node child : children) {
                    sendMessage(child.nodeId, "COUNT");
                }
            }
        } else if (message.startsWith("COUNT_RESULT:")) {
            int count = Integer.parseInt(message.split(":")[1]);
            totalCount += count;
            pendingCount--;

            if (pendingCount == 0) {
                if (parent != null) {
                    sendMessage(parent.nodeId, "COUNT_RESULT:" + totalCount);
                } else {
                    // Root node
                    System.out.println("Total nodes in the tree: " + totalCount);
                    finalResult = totalCount;
                }
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        // Tree:
        //      1
        //    / | \
        //   2  3  4
        //       |
        //       5
        int[][] edges = {
            {1, 2},
            {1, 3},
            {1, 4},
            {3, 5}
        };

        Map<Integer, Node> nodes = buildTree(edges);

        // Register the network
        for (Node node : nodes.values()) {
            node.network = nodes;
        }

        Node root = nodes.get(1);

        // Simulate external message
        root.receiveMessage(null, "COUNT");

        // Assertion test
        if (!Objects.equals(root.finalResult, 5)) {
            throw new AssertionError("Expected 5 nodes, got: " + root.finalResult);
        }

        System.out.println("All test cases passed.");
    }

    public static Map<Integer, Node> buildTree(int[][] edges) {
        Map<Integer, Node> nodes = new HashMap<>();
        for (int[] edge : edges) {
            int parentId = edge[0];
            int childId = edge[1];
            nodes.putIfAbsent(parentId, new Node(parentId));
            nodes.putIfAbsent(childId, new Node(childId));
            nodes.get(parentId).addChild(nodes.get(childId));
        }
        return nodes;
    }
}
