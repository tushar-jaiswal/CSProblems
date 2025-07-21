//  Author: Tushar Jaiswal
//  Creation Date: 2025-07-20

//  Implement a function called “cd”, which given a PWD and an input path, 
//  outputs the directory the operating system should switch to:
//  i.e.
//  fun cd(pwd: String, input: String): String
//  e.g.

//  cd("/home/bugs", ".") -> "/home/bugs"
//  cd("/home/bugs", "bunny") -> "/home/bugs/bunny"
//  cd("/home/bugs", "../daffy") -> "/home/daffy"

//  Part 2: 
//  1. Extend the `cd` function to accept a symbolic link map. 
//  2. After each directory change, check if the new path exists in the `symlinkMap`.
//  3. If a symbolic link is found, replace the path with its mapped value.
//  4. Hint: any edge cases we need to consider here?

//  fun cd(pwd: String, input: String, symlinkmap: Map<String, String>): String
//  e.g.

//  cd("/home/bugs", "lola/../basketball", {"/home/bugs/lola":"/home/lola"}) -> "/home/lola/basketball"
//  /home/bugs/lola/basketball -> "/home/lola/basketball"
//  "/home/bugs/basketball"
//  cd -P 

//  Further requirements:
//  Add symbolic link support, given a dictionary mapping.
//  Prioritize longer matches for symbolic links.
//  Detect cycles (e.g., A → B → A) using DFS and throw exceptions.
//  Add ~ home directory symbol support.
//  After each change, reapply the symbolic link map.
//  The final path does not have to be the shortest one.

//  Further requirements:
//  Add symbolic link support, given a dictionary mapping.
//  Prioritize longer matches for symbolic links.
//  Detect cycles (e.g., A → B → A) using DFS and throw exceptions.
//  Add ~ home directory symbol support.
//  After each change, reapply the symbolic link map.
//  The final path does not have to be the shortest one.

//  Runtime Complexity:
//  * Normalize path is O(len(path))
//  * Cycle detection is O(len(sym_link_map))
//  Overall cd complexity is max of the above two
//  Space Complexity:
//  * Normalize path is O(len(path))
//  * Cycle detection is O(len(sym_link_map))
//  Overall cd complexity is max of the above two

//  Clarification Questions
//  Are pwd and new_dir valid or we need to check - validation functions
//  Are pwd and new_dir absolute path or relative path - will need normalization for relative paths

public class ChangeDirectory {
    public String cd(String pwd, String newDir) {
        validatePwd(pwd);
        // validatePath(newDir);
        
        String newPath;
        if (newDir.charAt(0)== '/') {
            newPath = newDir;
        } else {
            newPath = pwd + "/" + newDir;
        }
        
        newPath = normalizePath(newPath);
        
        // checkDirExistence(pwd, newDir) // checks whether each subdirectory in new_dir exists under pwd
        
        return newPath;
    }
    
    public String cd(String pwd, String newDir, Map<String,String> symLinkMap) {
        String newPath = cd(pwd, newDir);
        
        if (hasCycle(symLinkMap)) {
            throw new IllegalArgumentException("Symbolic Link map has a cycle");
        }
        List<String> prioritizedSymLinks = new ArrayList<>(symLinkMap.keySet());
        prioritizedSymLinks.sort(Comparator.comparingInt(String::length).reversed());
        for(String link : prioritizedSymLinks) {
            if (newPath.contains(link)) {
                newPath = newPath.replace(link, symLinkMap.get(link));
                break;
            }
        }
        return newPath;
    }
    
    private void validatePwd(String pwd) {
        if (pwd.equals("")) {
            throw new IllegalArgumentException("Current directory cannot be empty");
        } else if (pwd.charAt(0) != '/') {
            throw new IllegalArgumentException("Current directory must start with root i.e. '/'");
        }
    }

    private String normalizePath(String path) {
        StringBuilder result = new StringBuilder();

        String[] directories = path.split("/");

        Stack<String> stack = new Stack<String>();
        for(String directory : directories) {
            if (directory.equals(".") || directory.equals("")) {
                continue;
            } else if (directory.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (directory.equals("~")) {
                    stack.clear();
                    stack.push("home");
                    stack.push("user");
            } else {
                stack.push(directory);
            }
        }

        if (stack.isEmpty()) {
            return "/";
        }

        for (String directory : stack) {
            result.append("/");
            result.append(directory);
        }
        
        return result.toString();
    }
    
    private boolean hasCycle(Map<String, String> graph) {
        for (String node : graph.keySet()) {
            Map<String, Boolean> visited = new HashMap<>();
            for (String key : graph.keySet()) {
                visited.put(key, false);
            }
            if (dfs(graph, node, visited)) {
                return true;
            }
        }
        return false;
    }

    private boolean dfs(Map<String, String> graph, String node, Map<String, Boolean> visited) {
        visited.put(node, true);
        String destination = graph.get(node);
        if (graph.containsKey(destination)) {
            if (visited.get(destination) || dfs(graph, destination, visited)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        var changeDirectory = new ChangeDirectory();

        assert changeDirectory.cd("/home/bugs", ".").equals("/home/bugs");
        assert changeDirectory.cd("/home/bugs", "..").equals("/home");
        assert changeDirectory.cd("/home/bugs", "bunny").equals("/home/bugs/bunny");
        assert changeDirectory.cd("/home/bugs", "../daffy").equals("/home/daffy");
        assert changeDirectory.cd("/home/bugs", "/daffy").equals("/daffy");
        assert changeDirectory.cd("/home/bugs", "/////daffy").equals("/daffy");
        assert changeDirectory.cd("/home/bugs", "/bug/../daffy").equals("/daffy");
        assert changeDirectory.cd("/home/bugs", "~").equals("/home/user");
        
        try {
            changeDirectory.cd("home/bugs", "daffy");
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Current directory must start with root i.e. '/'");
        }

        try {
            changeDirectory.cd("", "daffy");
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Current directory cannot be empty");
        }
        
        assert changeDirectory.cd("/home", ".", Map.of("a", "b", "b", "c")).equals("/home");
        try {
            changeDirectory.cd("/home", ".", Map.of("a", "b", "b", "c", "c", "b"));
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Symbolic Link map has a cycle");
        }
        
        assert changeDirectory.cd("/home/bugs", "bunny", 
            Map.of("/home/bugs", "/home", "/home/bugs/bunny", "/home/bugs")).equals("/home/bugs");
        
        System.out.println("All tests passed.");
    }
}
