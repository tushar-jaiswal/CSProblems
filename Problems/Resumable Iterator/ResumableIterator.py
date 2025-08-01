# Author: Tushar Jaiswal
# Creation Date: 2025-07-28

# Create an iterator that can pause and resume its state, extending to handle multiple files and asynchronous operations.

# Key Concepts:
#     State Management: Implementing get_state and set_state methods.
#     Composite Iterators: Managing multiple iterators concurrently.
#     Asynchronous Programming: Utilizing coroutines for async iteration.

# Requirements:
# ● Implement an abstract IteratorInterface
# ● Implement get_state() and set_state(state) for restoring iteration
# ● No hasNext() is allowed; caller should handle StopIteration
# ● Write tests that verify resuming works correctly

# Steps:
# 1. Define the interface
# 2. Implement a List-based resumable iterator
# 3. Implement a multiple file resumable iterator
# 4. Upgrade to async (Coroutine) iterator


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
