# Author: Tushar Jaiswal
# Creation Date: 2025-07-26

# Given an n-ary tree representing a cluster of nodes, with one designated root node. Each node in the tree can only communicate with its parent or its children, except for the root node, which can receive messages from an external source. The task is to send a message to the root node, prompting it to count the total number of nodes in the tree and print the result.

# You are required to implement a Node class with the following APIs:

# receiveMessage(from_node_id, message): A method to handle incoming messages.
# sendMessage(to_node_id, message): A method to send messages to other nodes. The implementation of this method is not required; it can simply be called.

# You can assume that when a node receives a message via sendMessage, it will automatically execute the receiveMessage() method. Initially, the root node will receive a call to receiveMessage(null, message).

# Follow-up:
# If there is a possibility of network failure, resulting in retries, how can we ensure that nodes are not counted multiple times? In other words, how do we ensure idempotency.

class Node:
    def __init__(self, node_id):
        self.node_id = node_id
        self.parent = None
        self.children = []
        self.total_count = 1  # includes self
        self.pending_count = 0
        self.received_from = set()
        self.network = None  # will be set later

    def add_child(self, child):
        child.parent = self
        self.children.append(child)

    def sendMessage(self, to_node_id, message):
        if self.network:
            self.network[to_node_id].receiveMessage(self.node_id, message)

    def receiveMessage(self, from_node_id, message):
        if message == "COUNT":
            # Avoid processing the same request multiple times
            message_id = (from_node_id, message)
            if message_id in self.received_from:
                return
            self.received_from.add(message_id)

            # Leaf node
            if not self.children:
                if self.parent:
                    self.sendMessage(self.parent.node_id, f"COUNT_RESULT:{1}")
            else:
                self.pending_count = len(self.children)
                for child in self.children:
                    self.sendMessage(child.node_id, "COUNT")

        elif message.startswith("COUNT_RESULT:"):
            count = int(message.split(":")[1])
            self.total_count += count
            self.pending_count -= 1

            if self.pending_count == 0:
                if self.parent:
                    self.sendMessage(self.parent.node_id, f"COUNT_RESULT:{self.total_count}")
                else:
                    print(f"Total nodes in the tree: {self.total_count}")
                    self.final_result = self.total_count  # for testing

def build_tree(edges):
    nodes = {}
    for parent_id, child_id in edges:
        if parent_id not in nodes:
            nodes[parent_id] = Node(parent_id)
        if child_id not in nodes:
            nodes[child_id] = Node(child_id)
        nodes[parent_id].add_child(nodes[child_id])
    return nodes

            
def main():
    # Create tree:
    #         1
    #       / | \
    #      2  3  4
    #         |
    #         5

    edges = [
        (1, 2),
        (1, 3),
        (1, 4),
        (3, 5),
    ]

    nodes = build_tree(edges)

    # Register the full network into each node for sendMessage simulation
    for node in nodes.values():
        node.network = nodes

    root = nodes[1]

    # Simulate sending "COUNT" from external source
    root.receiveMessage(None, "COUNT")

    # Assert final count is correct
    assert root.final_result == 5  # Nodes: 1,2,3,4,5

    print("All tests passed.")

if __name__ == "__main__":
    main()
