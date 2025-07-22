# Author: Tushar Jaiswal
# Creation Date: 2025-07-22

# Design a spreadsheet where each cell can contain a value or a formula referencing other cells, handling updates and cyclic dependencies.

# Key Concepts:
#     Dependency Graphs: Tracking cell dependencies to update values accordingly.
#     Cycle Detection: Preventing infinite loops due to circular references.
#     Memoization: Caching computed values for efficiency.

# Design a spreadsheet system where each cell can:
# ● Hold a direct integer value
# ● Be the sum of two other cells
# Features:
# ● setCell(key, cell)
# ● getCellValue(key)
# Additional Requirements:
# ● Updating a cell must update dependency relationships.
# ● Detect cycles (e.g., A → B → A) and throw an exception.
# ● Use caching to improve read performance.
# ● Invalidate dependent caches when a cell changes.

import pytest

class Cell:
    def __init__(self, key: str, val: int = None, child1: str = None, child2: str = None):
        self.key = key
        self.val = val
        self.child1 = child1
        self.child2 = child2
        self.cells_where_in_formula = set()
    
class Spreadsheet:
    def __init__(self):
        self.graph = {}
    
    def get(self, key: str) -> int:
        return self.graph[key].val
    
    def set(self, key: str, val: int = None, child1: str = None, child2: str = None):
        if key not in self.graph:
            self.graph[key] = Cell(key)
        cell = self.graph[key]
        
        if cell.child1 and cell.child2:
            # Remove dependency of this cell
            self.graph[cell.child1].cells_where_in_formula.remove(key)
            self.graph[cell.child2].cells_where_in_formula.remove(key)
            cell.child1 = None
            cell.child2 = None
        
        old_val = cell.val
        if val:
            new_val = val
            if child1 or child2: 
                raise ValueError("When val is set, child1 or child2 cannot be set")
        elif child1 and child2:
            new_val = self.get(child1) + self.get(child2)
            # Add key of this cell to cells in formula to mark dependency
            cell.child1 = child1
            cell.child2 = child2
            self.graph[cell.child1].cells_where_in_formula.add(key)
            self.graph[cell.child2].cells_where_in_formula.add(key)
        else:
            raise ValueError("Both child1 and child2 must be passed as arguments")
        cell.val = new_val
        self.update_cells_where_in_formula(cell, old_val, new_val)
    
    def update_cells_where_in_formula(self, cell: Cell, old_val: int, new_val: int):
        for dependent_cell_key in cell.cells_where_in_formula:
            dependent_cell = self.graph[dependent_cell_key]
            dependent_old_val = dependent_cell.val
            dependent_cell.val -= old_val
            dependent_cell.val += new_val
            dependent_new_val = dependent_cell.val
            self.update_cells_where_in_formula(dependent_cell, dependent_old_val, dependent_new_val)
        
if __name__ == "__main__":
    spreadsheet = Spreadsheet()
    
    spreadsheet.set("a", 1)
    spreadsheet.set("b", 5)
    assert spreadsheet.get("a") == 1
    assert spreadsheet.get("b") == 5
    spreadsheet.set("c", child1 = "a", child2 = "b")
    assert spreadsheet.get("c") == 6
    spreadsheet.set("d", child1 = "a", child2 = "c")
    assert spreadsheet.get("d") == 7
    with pytest.raises(ValueError, match = "Both child1 and child2 must be passed as arguments"): spreadsheet.set("d", child1 = "a")
    with pytest.raises(ValueError, match = "When val is set, child1 or child2 cannot be set"): spreadsheet.set("d", 1, child1 = "a")
    print("All tests passed")
