# Author: Tushar Jaiswal
# Creation Date: 2025-07-28

# Data Structure Implementation

# The question seems somewhat similar to implementing an LRU (Least Recently Used) cache. It requires implementing basic functions such as:
#     void addKey(string key): To add a key to the structure.
#     int getCountForKey(string key): To retrieve the count for a given key.

# Additionally, it involves maintaining a max heap to keep track of all entries that have appeared, assigning a specific priority to each.

# At first glance, the problem appears somewhat simple, but the interviewer continuously probes deeper. For instance, they might ask about how to handle changes in priority.
        
# Design a cache that supports priority based on access count:
# ● AddKey(key)
# ● GetCountForKey(key)
# ● Maintain a Max Heap to track most accessed keys.
# Requirements:
# ● Update heap whenever counts change.
# ● Tie-break using key ordering when counts are equal.

# Tushar's explanation for need of our own heapify up/down methods:
# We need to implement our own methods to heapify up/down since there is no way to 
# track an element in heap if we add/remove its count and call heapify on standard heapq.
# It will move to a different position and our key_index_map will lose track of it.
# So we won't be able to do future updates to it. 
# Example code below shows where it doesn't work.
# def add_key(self, key: str):
#         if key not in self.key_index_map:
#             heapq.heappush(self.max_heap, Item(key, 1))
#             self.key_index_map[key] = len(self.max_heap) - 1
#         else:
#             index = self.key_index_map[key]
#             item = self.max_heap[index]
#             item.add()
#             # We lose track of item index in heap at the below step and can't update key_index_map. 
#             # We also lose track of indexes of all other elements whose positions are updated in heapify.
#             heapq.heapify(self.max_heap)

# We can update the item count in the list and create our own methods to heapify 
# up/down to ensure we can track position of items in the heap list correctly as their position changes.

class Item: # Item class defines item ordering
    def __init__(self, key: str, count: int):
        self.key = key
        self.count = count
        
    def __lt__(self, other):
        # For max heap with lexicographic ordering on key also maxHeap i.e. "b" is max element instead of "a" 
        # For max heap on count with ascending order lexicographic ordering, reverse both < and > below
        if self.count == other.count:
            return self.key < other.key
        return self.count > other.count
    
    def add(self):
        self.count += 1
        
class MaxHeap: #MaxHeap class defines our custom maxHeap which allows us to track position of items
    def __init__(self):
        self.heap = []
        self.key_index_map = {} # map to retrieve the index of item in max_heap so it can be retrieved/updated
    
    def add(self, item: Item):
        self.heap.append(item)
        self.key_index_map[item.key] = len(self.heap) - 1
        self.heapify_up(item.key)
    
    def get(self, key: str) -> Item:
        index = self.key_index_map[key]
        return self.heap[index]
    
    def heapify_up(self, key: str):
        index = self.key_index_map[key]
        parent_index = self.getParentIndex(index)
        
        while parent_index >= 0 and self.heap[index] > self.heap[parent_index]:
            self.swap(index, parent_index)
            index = parent_index
        
    def getParentIndex(self, index) -> int:
        return -1 if index == 0 else (index - 1) // 2
    
    def swap(self, index1, index2):
        self.key_index_map[self.heap[index1].key], self.key_index_map[self.heap[index2].key] = index2, index1
        self.heap[index1], self.heap[index2] = self.heap[index2], self.heap[index1]
        
    def peek(self):
        if len(self.heap) == 0:
            raise ValueError("Heap is empty!")
        return self.heap[0]
        
        
class Cache:
    def __init__(self):
        self.max_heap = MaxHeap()
        
    def add_key(self, key: str):
        if key not in self.max_heap.key_index_map:
            self.max_heap.add(Item(key, 1))
        else:
            item = self.max_heap.get(key)
            item.add()
            self.max_heap.heapify_up(key)
            
    def get_count_for_key(self, key: str) -> int:
        return self.max_heap.get(key).count
    
    def get_max_item(self) -> Item:
        return self.max_heap.peek()

if __name__ == "__main__":
    c = Cache()
    c.add_key("1")
    c.add_key("2")
    assert c.get_max_item().key == "2"
    # assert c.get_max_item().count == 1
    c.add_key("2")
    assert c.get_max_item().key == "2"
    assert c.get_max_item().count == 2
    c.add_key("3")
    c.add_key("3")
    assert c.get_count_for_key("3") == 2
    c.add_key("3")
    assert c.get_max_item().key == "3"
    assert c.get_max_item().count == 3
    
    print("All tests passed")
