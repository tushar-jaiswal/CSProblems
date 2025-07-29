# Author: Tushar Jaiswal
# Creation Date: 2025-07-28

# Implement a time-based KV store (similar to LeetCode 981).
# Requirements:
# ● set(key, value): Save a value with the current timestamp.
# ● get(key, timestamp): Return the most recent value at or before the given
# timestamp.
# Follow-ups:
# 1. How to handle multithreaded access?
# → Use ReadWriteLock per key.
# 2. What to do if the dataset becomes too large (OOM)?
# → Keep only the most recently accessed data in memory; move older versions to disk

# Runtime Complexity: O(1) for set; O(log n) for get, where b is the number of entries associated with the specific key
# Space Complexity: O(k + t), where k is the number of unique keys and t is the total number of set calls

from abc import ABC, abstractmethod
import pytest

class IteratorInterface(ABC):
    @abstractmethod
    def __init__(self):
        pass
    
    @abstractmethod
    def __iter__(self):
        pass
    
    @abstractmethod
    def __next__(self):
        pass
    
    @abstractmethod
    def get_state(self):
        pass
    
    @abstractmethod
    def set_state(self, state):
        pass
    
class ListIterator(IteratorInterface):
    class State: 
        def __init__(self, index):
            self.index = index
            
    def __init__(self, iterable):
        self.iterable = iterable
        self.state = self.State(0)
    
    def __iter__(self):
        return self
    
    def __next__(self):
        if self.state.index < len(self.iterable):
            self.state.index += 1
            return self.iterable[self.state.index - 1]
        raise StopIteration("No more elements to iterate over")
    
    def get_state(self):
        return self.State(self.state.index)
    
    def set_state(self, state):
        self.state = state
    
def test_list_iteration():
    l = [4,3,2,1]
    it = ListIterator(l)
    assert next(it) == 4
    state = it.get_state()
    assert next(it) == 3
    it.set_state(state)
    assert next(it) == 3
    assert next(it) == 2
    assert next(it) == 1
    with pytest.raises(StopIteration, match="No more elements to iterate over"):next(it)
    print("All tests passed")

if __name__ == "__main__":
    test_list_iteration()
