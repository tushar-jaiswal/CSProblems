# Author: Tushar Jaiswal
# Creation Date: 2025-07-26

# Implement an in-memory database system that mimics basic SQL functionality.

# You will need to create a table given a list of column names. You should support: add row, query row by a single column name, query row by multiple column names (support AND and OR), greater or less than query and order by

# Note: you don't have to handle SQL statements parsing.

# Design a simple in-memory database.
# Requirements:
# ● insert(table, row)
# ● query(table, columns, where_conditions=[], order_by=[])
# Supported features:
# ● Where by single or multiple columns
# ● AND condition
# ● ORDER BY one or more columns
# ● Greater-than and less-than conditions
# ● No SQL parsing; define your own APIs
# ● All fields are treated as String

# Solution:
# Created Database which supports
# * Table Creation
# * Table Deletion
# * Row Insertion
# * Row deletion based on conditions all joined by either AND or OR
# * Query of any number of columns with
#   * Optional where conditions all joined by either AND or OR
#   * Optional order by supporting multiple column sorting in ascending or descending order

# Runtime Complexity: 
#  Database init: O(1)
#  create_table: O(|columns|)
#  delete_table: O(1)
#  insert: O(1)
#  delete: O(|rows| * |where_conditions|)
#  query: O((|rows| * log(|rows|)) + |rows| * |where_conditions|)
# Space Complexity: O(values of all tables)

import pytest
from enum import Enum

class Table:
    def __init__(self, name: str, columns: list[str]):
        self.name = name
        self.columns = columns
        self.rows = []
        self.col_map = {v : i for i, v in enumerate(columns)}
        
class Operator(Enum):
    EQ = 0
    GREATER_THAN = 1
    LESS_THAN = 2
    AND = 3
    OR = 4
        
class WhereCondition:
    def __init__(self, col: str, op: Operator, val: str):
        self.col = col
        self.op = op
        self.val = val
        
class SortingOrder(Enum):
    ASC = 1
    DESC = 2
        
class OrderByCondition:
    def __init__(self, cols: list[str], order: SortingOrder = SortingOrder.ASC):
        self.cols = cols
        self.order = order

class Database:
    def __init__(self):
        self.tables = {}
    
    def create_table(self, name: str, columns: list[str]):
        if name in self.tables:
            raise ValueError("A table with this name already exists")
        
        self.tables[name] = Table(name, columns)
        
    def delete_table(self, name: str):
        if name not in self.tables:
            raise ValueError("A table with this name doesn't exist")
            
        self.tables.pop(name)
        
    def insert(self, name: str, col_vals: list[str]):
        if name not in self.tables:
            raise ValueError("A table with this name doesn't exist")
        if len(col_vals) != len(self.tables[name].columns):
            raise ValueError("Number of columns doens't match this table's schema")
        
        self.tables[name].rows.append(col_vals)
        
    def delete(self, name: str, where_conditions: list[WhereCondition], joining_op: Operator = Operator.AND):
        if name not in self.tables:
            raise ValueError("A table with this name doesn't exist")
            
        table = self.tables[name]
        deletion_rows = []
        for row in table.rows:
            if self.should_include_row_on_conditions(table, row, where_conditions, joining_op):
                # add selected row for deletion
                deletion_rows.append(row)
        
        for row in deletion_rows:
            table.rows.remove(row)
            
    def query(self, name: str, cols: list[str], where_conditions: list[WhereCondition] = None, joining_op: Operator = Operator.AND, order_by_condition: OrderByCondition = None) -> list[list[str]]:
        if name not in self.tables:
                raise ValueError("A table with this name doesn't exist")
        
        table = self.tables[name]
        sorted_rows = table.rows
        
        if order_by_condition:
            order_col_indexes = [table.col_map[col] for col in order_by_condition.cols]
            # sorts in asc/desc by cols in order_by_condition
            if order_by_condition.order == SortingOrder.ASC:
                sorted_rows = sorted(sorted_rows, key=lambda row: [row[i] for i in order_col_indexes]) 
            else:
                sorted_rows = sorted(sorted_rows, key=lambda row: [row[i] for i in order_col_indexes], reverse=True) 
        
        result = []
        for row in sorted_rows:
            if not where_conditions or self.should_include_row_on_conditions(table, row, where_conditions, joining_op):
                result_column_indexes = [table.col_map[col] for col in cols]
                result.append([row[index] for index in result_column_indexes])
        
        return result
    
    def should_include_row_on_conditions(self, table: Table, row: list[str], where_conditions: list[WhereCondition], joining_op: Operator) -> bool:
        if joining_op == Operator.AND:
            for condition in where_conditions: # following logic does AND of WHERE conditions
                col_id = table.col_map[condition.col]
                if condition.op == Operator.EQ: # exclude rows where col is != val
                    if row[col_id] != condition.val:
                        return False
                elif condition.op == Operator.GREATER_THAN:
                    if row[col_id] <= condition.val: # exclude rows where col is not > val i.e. col <= val
                        return False
                elif condition.op == Operator.LESS_THAN:
                    if row[col_id] >= condition.val: # exclude rows where col is not < val i.e. col >= val
                        return False
            return True
        else:
            for condition in where_conditions: # following logic does OR of WHERE conditions
                col_id = table.col_map[condition.col]
                if condition.op == Operator.EQ: #
                    if row[col_id] == condition.val:
                        return True
                elif condition.op == Operator.GREATER_THAN:
                    if row[col_id] > condition.val: 
                        return True
                elif condition.op == Operator.LESS_THAN:
                    if row[col_id] < condition.val:
                        return True
            return False
            
if __name__ == "__main__":
    db = Database()
    db.create_table("birthdays", ["name", "birthdate"])
    with pytest.raises(ValueError, match="A table with this name already exists"):db.create_table("birthdays", ["name", "birthdate"])
    db.delete_table("birthdays")
    with pytest.raises(ValueError, match="A table with this name doesn't exist"):db.delete_table("birthdays")
    with pytest.raises(ValueError, match="A table with this name doesn't exist"):db.insert("birthdays", ["Ada", "1815-12-10"])
    db.create_table("birthdays", ["name", "birthdate"])
    with pytest.raises(ValueError, match="Number of columns doens't match this table's schema"):db.insert("birthdays", ["Ada", "1815-12-10", "test"])
    db.insert("birthdays", ["AS", "2025-12-10"])
    db.insert("birthdays", ["Zd", "2025-1-10"])
    db.insert("birthdays", ["Bs", "2024-12-10"])
    
    cond1 = WhereCondition("name", Operator.EQ, "AS")
    cond2 = WhereCondition("birthdate", Operator.GREATER_THAN, "2026-01-01")
    cond3 = WhereCondition("birthdate", Operator.LESS_THAN, "2025-12-31")
    with pytest.raises(ValueError, match="A table with this name doesn't exist"):db.delete("table", [cond1])
    with pytest.raises(ValueError, match="A table with this name doesn't exist"):db.query("test", ["name"])
    assert db.query("birthdays", ["name"]) == [['AS'], ['Zd'], ['Bs']]
    assert db.query("birthdays", ["name", "birthdate"]) == [["AS", "2025-12-10"], ["Zd", "2025-1-10"], ["Bs", "2024-12-10"]]
    assert db.query("birthdays", ["birthdate", "name"]) == [['2025-12-10', 'AS'], ['2025-1-10', 'Zd'],['2024-12-10', 'Bs']]
    
    assert db.query("birthdays", ["birthdate"], [cond1]) == [['2025-12-10']]
    assert db.query("birthdays", ["birthdate"], [cond2]) == []
    assert db.query("birthdays", ["birthdate"], [cond3]) == [['2025-12-10'], ['2025-1-10'], ['2024-12-10']]
    assert db.query("birthdays", ["birthdate", "name"], [cond3]) == [['2025-12-10', 'AS'], ['2025-1-10', 'Zd'], ['2024-12-10', 'Bs']]
    assert db.query("birthdays", ["birthdate"], [cond1, cond3]) == [['2025-12-10']]
    assert db.query("birthdays", ["birthdate", "name"], [cond1, cond3], Operator.OR) == [['2025-12-10', 'AS'], ['2025-1-10', 'Zd'], ['2024-12-10', 'Bs']]
    
    assert db.query("birthdays", ["name", "birthdate"], order_by_condition=OrderByCondition(["birthdate"])) == [["Bs", "2024-12-10"], ["Zd", "2025-1-10"], ["AS", "2025-12-10"]]
    assert db.query("birthdays", ["name", "birthdate"], order_by_condition=OrderByCondition(["name"])) == [["AS", "2025-12-10"], ["Bs", "2024-12-10"], ["Zd", "2025-1-10"]]
    db.insert("birthdays", ["Ds", "2025-1-10"])
    assert db.query("birthdays", ["name", "birthdate"], order_by_condition=OrderByCondition(["birthdate","name"])) == [['Bs', '2024-12-10'], ['Ds', '2025-1-10'], ['Zd', '2025-1-10'], ['AS', '2025-12-10']]
    assert db.query("birthdays", ["name", "birthdate"], order_by_condition=OrderByCondition(["name"], SortingOrder.DESC)) == [['Zd', '2025-1-10'], ['Ds', '2025-1-10'], ['Bs', '2024-12-10'], ['AS', '2025-12-10']]
    
    cond4 = WhereCondition("birthdate", Operator.EQ, "2025-1-10")
    db.delete("birthdays", [cond4])
    assert db.query("birthdays", ["birthdate", "name"]) == [['2025-12-10', 'AS'], ['2024-12-10', 'Bs']]
    db.delete("birthdays", [cond4, cond1])
    assert db.query("birthdays", ["birthdate", "name"]) == [['2025-12-10', 'AS'], ['2024-12-10', 'Bs']]
    db.delete("birthdays", [cond4, cond1], Operator.OR)
    assert db.query("birthdays", ["birthdate", "name"]) == [['2024-12-10', 'Bs']]
    
    print("All tests passed")
