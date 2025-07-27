# Author: Tushar Jaiswal
# Creation Date: 2025-07-27

# You have a toy language grammar with primitives, tuples, generics, and functions

# Primitives:
# char, int, float

# Generices:
# uppercase alpha with number e.g. T1, T2 …

# Tuples
# list of Primitives or Generic or Tuple: [ int, T1, char ]
# With tuple nested [int, char, [ int, T1] ]

# Implement Node class to represent Primitives & Tuples (empty class provided)
# Constructor input is value (optional str) and children (optional list of Node)
# Value passed if primitive
# children passed if tuple

# Functions
# Functions have params and return like this: [int; [int, T1]; T2] -> [char, T2, [float, float]]
# Param is list of Node
# Return is a tuple

# Implement Function class to represent functions (empty class provided)
# Constructor takes params (list of Node) and return (Node)

# P1: Implement to_str for Function & Node

# P2: Implement infer_return(function, param) -> return
# Given function & param values like, return actual return type after substituting out generics (T1, T2 etc)
# Must raise error if there is type mismatch or conflict

# Example
# Func: [T1, T2, int, T1] -> [T1, T2]
# Params: [int, char, int, int]
# Should return [int, char]
# If params was [int, int, int, int] then raise error for type mismatch (int vs char)
# If params was [int, int, int, char] then raise error for type conflict

from typing import List, Optional

class Node:
    def __init__(self, value: Optional[str] = None, children: Optional[List["Node"]] = None):
        self.value = value
        self.children = children or []

    def is_primitive(self):
        return self.value in {"int", "char", "float"}

    def is_generic(self):
        return self.value and self.value[0].isupper()

    def is_tuple(self):
        return bool(self.children)

    def __repr__(self):
        return self.to_str()

    def to_str(self) -> str:
        if self.is_tuple():
            return "[" + ", ".join(child.to_str() for child in self.children) + "]"
        else:
            return self.value

class Function:
    def __init__(self, params: List[Node], ret: Node):
        self.params = params
        self.ret = ret

    def to_str(self) -> str:
        param_str = "; ".join(param.to_str() for param in self.params)
        return f"[{param_str}] -> {self.ret.to_str()}"

def unify(formal: Node, actual: Node, generic_map: dict):
    if formal.is_generic():
        if formal.value in generic_map:
            # Check consistency
            if not match(generic_map[formal.value], actual):
                raise TypeError(f"Type conflict for {formal.value}")
        else:
            generic_map[formal.value] = actual
    elif formal.is_primitive():
        if not match(formal, actual):
            raise TypeError(f"Type mismatch: expected {formal.value}, got {actual.value}")
    elif formal.is_tuple():
        if not actual.is_tuple() or len(formal.children) != len(actual.children):
            raise TypeError("Tuple mismatch")
        for f_child, a_child in zip(formal.children, actual.children):
            unify(f_child, a_child, generic_map)
    else:
        raise TypeError("Unknown node type")

def match(a: Node, b: Node) -> bool:
    if a.is_tuple() and b.is_tuple():
        if len(a.children) != len(b.children):
            return False
        return all(match(c1, c2) for c1, c2 in zip(a.children, b.children))
    return a.value == b.value and not a.is_tuple() and not b.is_tuple()

def infer_return(func: Function, params: List[Node]) -> Node:
    if len(func.params) != len(params):
        raise TypeError("Parameter count mismatch")

    generic_map = {}

    for formal, actual in zip(func.params, params):
        unify(formal, actual, generic_map)

    return substitute(func.ret, generic_map)

def substitute(node: Node, generic_map: dict) -> Node:
    if node.is_generic():
        if node.value not in generic_map:
            raise TypeError(f"Unbound generic {node.value}")
        return generic_map[node.value]
    elif node.is_tuple():
        return Node(children=[substitute(child, generic_map) for child in node.children])
    else:
        return Node(value=node.value)

def main():
    # [T1, T2, int, T1] -> [T1, T2]
    func = Function(
        [
            Node("T1"),
            Node("T2"),
            Node("int"),
            Node("T1"),
        ],
        Node(children=[Node("T1"), Node("T2")])
    )

    params = [Node("int"), Node("char"), Node("int"), Node("int")]
    result = infer_return(func, params)
    print("Inferred Return:", result)
    assert result.to_str() == "[int, char]"

    # Mismatch example (char instead of int)
    try:
        bad_params = [Node("int"), Node("int"), Node("int"), Node("char")]
        infer_return(func, bad_params)
    except TypeError as e:
        print("Expected error:", e)

    # Conflict example (same generic maps to int and char)
    try:
        conflict_params = [Node("int"), Node("char"), Node("int"), Node("char")]
        infer_return(func, conflict_params)
    except TypeError as e:
        print("Expected conflict:", e)
    
    print("All tests passed")

if __name__ == "__main__":
    main()
