# Author: Tushar Jaiswal
# Creation Date: 2025-07-29

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

class MultiListIterator(IteratorInterface):
    class State:
        def __init__(self, outer_index, inner_state):
            self.outer_index = outer_index  # which iterator we're on
            self.inner_state = inner_state  # the state of that inner iterator

    def __init__(self, iterators):
        self.iterators = iterators
        self.state = self.State(0, None)
        if self.iterators:
            self.state.inner_state = self.iterators[0].get_state()

    def __iter__(self):
        return self

    def __next__(self):
        while self.state.outer_index < len(self.iterators):
            current_iter = self.iterators[self.state.outer_index]
            try:
                current_iter.set_state(self.state.inner_state)
                value = next(current_iter)
                self.state.inner_state = current_iter.get_state()
                return value
            except StopIteration:
                self.state.outer_index += 1
                print("self.state.outer_index", self.state.outer_index)
                if self.state.outer_index < len(self.iterators):
                    self.iterators[self.state.outer_index].set_state(ListIterator.State(0))
                    self.state.inner_state = self.iterators[self.state.outer_index].get_state()
        raise StopIteration("No more elements across all iterators")

    def get_state(self):
        return self.State(self.state.outer_index, ListIterator.State(self.state.inner_state.index))

    def set_state(self, state):
        self.state = state

def test_multi_list_iteration():
    it1 = ListIterator([1, 2])
    it2 = ListIterator([3])
    it3 = ListIterator([4, 5])

    multi = MultiListIterator([it1, it2, it3])

    assert next(multi) == 1
    saved = multi.get_state()
    assert next(multi) == 2
    assert next(multi) == 3

    multi.set_state(saved)
    assert next(multi) == 2
    print(multi.state.outer_index, multi.state.inner_state.index)
    assert next(multi) == 3
    print(multi.state.outer_index, multi.state.inner_state.index)
    assert next(multi) == 4
    assert next(multi) == 5

    try:
        next(multi)
    except StopIteration:
        print("All MultiListIterator values consumed correctly")

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
    test_multi_list_iteration()
