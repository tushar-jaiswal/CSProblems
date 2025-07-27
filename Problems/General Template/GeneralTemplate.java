//  Author: Tushar Jaiswal
//  Creation Date: 2025-07-27

// You have a toy language grammar with primitives, tuples, generics, and functions

// Primitives:
// char, int, float

// Generices:
// uppercase alpha with number e.g. T1, T2 …

// Tuples
// list of Primitives or Generic or Tuple: [ int, T1, char ]
// With tuple nested [int, char, [ int, T1] ]

// Implement Node class to represent Primitives & Tuples (empty class provided)
// Constructor input is value (optional str) and children (optional list of Node)
// Value passed if primitive
// children passed if tuple

// Functions
// Functions have params and return like this: [int; [int, T1]; T2] -> [char, T2, [float, float]]
// Param is list of Node
// Return is a tuple

// Implement Function class to represent functions (empty class provided)
// Constructor takes params (list of Node) and return (Node)

// P1: Implement to_str for Function & Node

// P2: Implement infer_return(function, param) -> return
// Given function & param values like, return actual return type after substituting out generics (T1, T2 etc)
// Must raise error if there is type mismatch or conflict

// Example
// Func: [T1, T2, int, T1] -> [T1, T2]
// Params: [int, char, int, int]
// Should return [int, char]
// If params was [int, int, int, int] then raise error for type mismatch (int vs char)
// If params was [int, int, int, char] then raise error for type conflict

import java.util.*;

class Node {
    String value;
    List<Node> children;

    public Node(String value) {
        this.value = value;
        this.children = null;
    }

    public Node(List<Node> children) {
        this.children = children;
        this.value = null;
    }

    public boolean isPrimitive() {
        return value != null && (value.equals("int") || value.equals("char") || value.equals("float"));
    }

    public boolean isGeneric() {
        return value != null && Character.isUpperCase(value.charAt(0));
    }

    public boolean isTuple() {
        return children != null;
    }

    @Override
    public String toString() {
        if (isTuple()) {
            List<String> parts = new ArrayList<>();
            for (Node child : children) {
                parts.add(child.toString());
            }
            return "[" + String.join(", ", parts) + "]";
        }
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) return false;
        Node other = (Node) obj;

        if (this.isTuple() && other.isTuple()) {
            if (this.children.size() != other.children.size()) return false;
            for (int i = 0; i < this.children.size(); i++) {
                if (!this.children.get(i).equals(other.children.get(i))) return false;
            }
            return true;
        } else {
            return Objects.equals(this.value, other.value);
        }
    }
}

class Function {
    List<Node> params;
    Node returnType;

    public Function(List<Node> params, Node returnType) {
        this.params = params;
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        List<String> paramStrs = new ArrayList<>();
        for (Node param : params) {
            paramStrs.add(param.toString());
        }
        return "[" + String.join("; ", paramStrs) + "] -> " + returnType.toString();
    }
}

class InferenceEngine {

    public static Node inferReturn(Function func, List<Node> inputParams) {
        if (func.params.size() != inputParams.size()) {
            throw new RuntimeException("Parameter count mismatch");
        }

        Map<String, Node> genericMap = new HashMap<>();

        for (int i = 0; i < func.params.size(); i++) {
            unify(func.params.get(i), inputParams.get(i), genericMap);
        }

        return substitute(func.returnType, genericMap);
    }

    private static void unify(Node formal, Node actual, Map<String, Node> genericMap) {
        if (formal.isGeneric()) {
            String gName = formal.value;
            if (genericMap.containsKey(gName)) {
                if (!genericMap.get(gName).equals(actual)) {
                    throw new RuntimeException("Type conflict for " + gName);
                }
            } else {
                genericMap.put(gName, actual);
            }
        } else if (formal.isPrimitive()) {
            if (!formal.equals(actual)) {
                throw new RuntimeException("Type mismatch: expected " + formal.value + " got " + actual.value);
            }
        } else if (formal.isTuple()) {
            if (!actual.isTuple() || formal.children.size() != actual.children.size()) {
                throw new RuntimeException("Tuple mismatch");
            }
            for (int i = 0; i < formal.children.size(); i++) {
                unify(formal.children.get(i), actual.children.get(i), genericMap);
            }
        } else {
            throw new RuntimeException("Unknown node type");
        }
    }

    private static Node substitute(Node node, Map<String, Node> genericMap) {
        if (node.isGeneric()) {
            String gName = node.value;
            if (!genericMap.containsKey(gName)) {
                throw new RuntimeException("Unbound generic: " + gName);
            }
            return genericMap.get(gName);
        } else if (node.isTuple()) {
            List<Node> newChildren = new ArrayList<>();
            for (Node child : node.children) {
                newChildren.add(substitute(child, genericMap));
            }
            return new Node(newChildren);
        } else {
            return new Node(node.value); // primitive
        }
    }
}

public class Main {
    public static void main(String[] args) {
        // Function: [T1, T2, int, T1] -> [T1, T2]
        Function func = new Function(
                Arrays.asList(
                        new Node("T1"),
                        new Node("T2"),
                        new Node("int"),
                        new Node("T1")
                ),
                new Node(Arrays.asList(new Node("T1"), new Node("T2")))
        );

        List<Node> goodParams = Arrays.asList(
                new Node("int"),
                new Node("char"),
                new Node("int"),
                new Node("int")
        );

        Node inferred = InferenceEngine.inferReturn(func, goodParams);
        System.out.println("Inferred return: " + inferred); // [int, char]

        // Error: type conflict
        try {
            List<Node> conflictParams = Arrays.asList(
                    new Node("int"),
                    new Node("int"),
                    new Node("int"),
                    new Node("char")
            );
            InferenceEngine.inferReturn(func, conflictParams);
        } catch (RuntimeException e) {
            System.out.println("Expected conflict: " + e.getMessage());
        }

        // Error: type mismatch
        try {
            List<Node> mismatchParams = Arrays.asList(
                    new Node("int"),
                    new Node("char"),
                    new Node("int"),
                    new Node("char")
            );
            InferenceEngine.inferReturn(func, mismatchParams);
        } catch (RuntimeException e) {
            System.out.println("Expected mismatch: " + e.getMessage());
        }
        
        System.out.println("All tests passed.");
    }
}
