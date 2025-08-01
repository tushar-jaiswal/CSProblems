# Author: Tushar Jaiswal
# Creation Date: 2025-07-20

# Implement a function called “cd”, which given a PWD and an input path, 
# outputs the directory the operating system should switch to:
# i.e.
# fun cd(pwd: String, input: String): String
# e.g.

# cd("/home/bugs", ".") -> "/home/bugs"
# cd("/home/bugs", "bunny") -> "/home/bugs/bunny"
# cd("/home/bugs", "../daffy") -> "/home/daffy"

# Part 2: 
# 1. Extend the `cd` function to accept a symbolic link map. 
# 2. After each directory change, check if the new path exists in the `symlinkMap`.
# 3. If a symbolic link is found, replace the path with its mapped value.
# 4. Hint: any edge cases we need to consider here?

# fun cd(pwd: String, input: String, symlinkmap: Map<String, String>): String
# e.g.

# cd("/home/bugs", "lola/../basketball", {"/home/bugs/lola":"/home/lola"}) -> "/home/lola/basketball"
# /home/bugs/lola/basketball -> "/home/lola/basketball"
# "/home/bugs/basketball"
# cd -P 

# Further requirements:
# Add symbolic link support, given a dictionary mapping.
# Prioritize longer matches for symbolic links.
# Detect cycles (e.g., A → B → A) using DFS and throw exceptions.
# Add ~ home directory symbol support.
# After each change, reapply the symbolic link map.
# The final path does not have to be the shortest one.

# Runtime Complexity:
# * Normalize path is O(len(path))
# * Cycle detection is O(len(sym_link_map))
# Overall cd complexity is max of the above two
# Space Complexity:
# * Normalize path is O(len(path))
# * Cycle detection is O(len(sym_link_map))
# Overall cd complexity is max of the above two

# Clarification Questions
# Are pwd and new_dir valid or we need to check - validation functions
# Are pwd and new_dir absolute path or relative path - will need normalization for relative paths

import pytest

class ChangeDirectory:
    def __init__(self):
        pass
    
    def cd(self, pwd: str, new_dir: str, sym_link_map: dict = None) -> str:
        # try:
        self.validate_pwd(pwd)
        # self.validate_path(new_dir)
        
        if new_dir.startswith("/"):
            new_path = new_dir
        else:
            new_path = pwd + "/" + new_dir
        
        new_path = self.normalize_path(new_path)
        
        # check_dir_existence(pwd, new_dir) # checks whether each subdirectory in new_dir exists under pwd
        
        if sym_link_map:
            if self.has_cycle(sym_link_map):
                raise ValueError("Symbolic Link map has a cycle")
            prioritized_sym_links = sorted(sym_link_map.keys(), key=len, reverse=True)
            for link in prioritized_sym_links:
                if link in new_path:
                    new_path = new_path.replace(link, sym_link_map[link])
                    break
        return new_path
    
    def validate_pwd(self, pwd: str):
        if not pwd:
            raise ValueError("Current directory cannot be empty")
        elif pwd[0] != "/":
            raise ValueError("Current directory must start with root i.e. '/'")
#         validate_path(pwd)
    
#     def validate_path(path: str): # additional path validations such as unsupported characters

    def normalize_path(self, path: str) -> str:
        stack = []
        for directory in path.split("/"):
            if not directory or directory == ".":
                continue
            elif directory == "..":
                if stack:
                    stack.pop()
            elif directory == "~":
                stack.clear()
                stack.append("home")
                stack.append("user")
            else:
                stack.append(directory)
                
        return "/" + "/".join(stack)
    
    def has_cycle(self, graph: dict) -> bool:
        for node in graph:
            visited = {key: False for key in graph}
            if self.dfs(graph, node, visited):
                return True
        return False
    
    def dfs(self, graph: dict, node: str, visited: dict) -> bool:
        visited[node] = True
        destination = graph[node]
        if destination in graph:
            if(visited[destination] or self.dfs(graph, destination, visited)):
                return True        
        return False
           
        
if __name__ == "__main__":
    change_directory = ChangeDirectory()
    assert change_directory.cd("/home/bugs", ".") == "/home/bugs"
    assert change_directory.cd("/home/bugs", "..") == "/home"
    assert change_directory.cd("/home/bugs", "bunny") == "/home/bugs/bunny"
    assert change_directory.cd("/home/bugs", "../daffy") == "/home/daffy"
    assert change_directory.cd("/home/bugs", "/daffy") == "/daffy"
    assert change_directory.cd("/home/bugs", "/////daffy") == "/daffy"
    assert change_directory.cd("/home/bugs", "/bug/../daffy") == "/daffy"
    assert change_directory.cd("/home/bugs", "~") == "/home/user"
    with pytest.raises(ValueError, match = "Current directory must start with root i.e. '/'"):change_directory.cd("home/bugs", "daffy")
    with pytest.raises(ValueError, match = "Current directory cannot be empty"):change_directory.cd("", "daffy")
    assert change_directory.cd("/home", ".", {"a":"b", "b":"c"}) == "/home"
    with pytest.raises(ValueError, match = "Symbolic Link map has a cycle"):change_directory.cd("/home", ".", {"a":"b", "b":"c", "c":"b"}) == "/home"
    assert change_directory.cd("/home/bugs", "bunny", {"/home/bugs":"/home", "/home/bugs/bunny":"/home/bugs"}) == "/home/bugs"
    print("All tests passed")
