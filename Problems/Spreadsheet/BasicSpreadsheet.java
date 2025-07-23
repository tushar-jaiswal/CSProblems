//  Author: Tushar Jaiswal
//  Creation Date: 2025-07-22

//  Design a spreadsheet where each cell can contain a value or a formula referencing other cells, handling updates and cyclic dependencies.

//  Key Concepts:
//      Dependency Graphs: Tracking cell dependencies to update values accordingly.
//      Cycle Detection: Preventing infinite loops due to circular references.
//      Memoization: Caching computed values for efficiency.

//  Design a spreadsheet system where each cell can:
//  ● Hold a direct integer value
//  ● Be the sum of two other cells
//  Features:
//  ● setCell(key, cell)
//  ● getCellValue(key)
//  Additional Requirements:
//  ● Updating a cell must update dependency relationships.
//  ● Detect cycles (e.g., A → B → A) and throw an exception.
//  ● Use caching to improve read performance.
//  ● Invalidate dependent caches when a cell changes.

//  Runtime Complexity:
//  Here, n refers to the number of cells in the current Spreadsheet.
//  * init is O(1)
//  * set is O(n)!
//     * This cell could be in formula for all other cells i.e. n. 
//     * Those cells themselves could be in formula for other cells except the cells that are in their formula i.e. n!
//  * get is O(1)
//  Space Complexity: 
//  The space required will be O((n)^2) in the worst case. 
//  * O(n) space will be required for the Excel Form itself. 
//  * For each cell in this form, the cells_where_in_formula list can contain O(n) cells.

public class Spreadsheet {
    HashMap<String, Cell> graph;
    
    public Spreadsheet() {
        graph = new HashMap<String, Cell>();
    }
    
    public int get(String key) {
        if (!graph.containsKey(key)) {
            throw new IllegalArgumentException(String.format("Cell %s doesn't exist", key));
        }
        return graph.get(key).val;
    }
    
    public void set(String key, int val) {
        if (!graph.containsKey(key)) {
            graph.put(key, new Cell(key));
        }
        Cell cell = graph.get(key);
        
        if (cell.child1 != null && cell.child2 != null) {
            // Remove dependency of this cell
            removeDependencyOfCell(cell);
        }
        
        int oldVal = cell.val;
        int newVal = val;
        cell.val = newVal;
        
        var visited = new HashSet<String>();
        visited.add(key);
        updateCellsWhereInFormula(cell, oldVal, newVal);
    }
    
    public void set(String key, String child1, String child2) {
        if (!graph.containsKey(key)) {
            graph.put(key, new Cell(key));
        }
        Cell cell = graph.get(key);
        
        if (child1 == null || child2 == null) {
            throw new IllegalArgumentException("Both child1 and child2 must be passed as arguments");
        }
        
        if (cell.child1 != null && cell.child2 != null) {
            // Remove dependency of this cell
            removeDependencyOfCell(cell);
        }
        
        // Add key of this cell to cells in formula to mark dependency
        cell.child1 = child1;
        cell.child2 = child2;
        graph.get(cell.child1).cellsWhereInFormula.add(key);
        graph.get(cell.child2).cellsWhereInFormula.add(key);
        
        int oldVal = cell.val;
        int newVal = get(child1) + get(child2);
        cell.val = newVal;
        
        updateCellsWhereInFormula(cell, oldVal, newVal);
    }
    
    private void updateCellsWhereInFormula(Cell cell, int oldVal, int newVal) {
        for (String dependentCellKey : cell.cellsWhereInFormula) {
            Cell dependentCell = graph.get(dependentCellKey);
            int dependentOldVal = dependentCell.val;
            dependentCell.val -= oldVal;
            dependentCell.val += newVal;
            int dependentNewVal = dependentCell.val;
            updateCellsWhereInFormula(dependentCell, dependentOldVal, dependentNewVal);
        }
    }
    
    private void removeDependencyOfCell(Cell cell) {
        graph.get(cell.child1).cellsWhereInFormula.remove(cell.key);
        graph.get(cell.child2).cellsWhereInFormula.remove(cell.key);
        cell.child1 = null;
        cell.child2 = null;
    }
    
    public static void main(String[] args) {
        Spreadsheet spreadsheet = new Spreadsheet();
    
        spreadsheet.set("a", 1);
        spreadsheet.set("b", 5);
        assert spreadsheet.get("a") == 1;
        assert spreadsheet.get("b") == 5;
        try {
            spreadsheet.get("c");
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Cell c doesn't exist");
        }
        spreadsheet.set("c", "a", "b");
        assert spreadsheet.get("c") == 6;
        spreadsheet.set("d", "a", "c");
        assert spreadsheet.get("d") == 7;
        try {
            spreadsheet.set("d", null, "a");
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("Both child1 and child2 must be passed as arguments");
        }

        System.out.println("All tests passed.");
    }
}

class Cell {
    String key;
    int val;
    String child1;
    String child2;
    HashSet<String> cellsWhereInFormula;
    
    public Cell(String key) {
        this.key = key;
        cellsWhereInFormula = new HashSet<String>();
    }
}
