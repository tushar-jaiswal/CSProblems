# Author: Tushar Jaiswal
# Creation Date: 2025-07-25

# # Implement a KVStore class that supports storing and retrieving key-value pairs, as well as persisting data to the filesystem and restoring it. The class should include the following methods:
# Things to note:
#     Both key and value are strings and may contain any characters, including newlines.
#     You must not use Python’s built-in serialization libraries (such as json).
#     The main focus of the problem is implementing your own serialization/deserialization methods.

# 2nd question How to write multiple files when a file cannot exceed one KB.

import pytest

class KVStore:
    FILENAME = "kvstore"
    
    def __init__(self):
        self.store = {}
        
    def getValue(self, key: str) -> str: 
        return self.store[key]
    
    def setValue(self, key: str, val: str):
        self.store[key] = val
        
    def clear(self):
        self.store.clear()
        
    def persist_to_disk(self):
        encoding = self.encode()
        
        with open(KVStore.FILENAME, "w") as file:
            file.write(encoding)
    
    def encode(self) -> str:
        encoding = ""
        
        for key, val in self.store.items():
            encoding += str(len(key)) + "." + key
            encoding += str(len(val)) + "." + val
            
        return encoding
        
    def restore_from_disk(self):
        with open(KVStore.FILENAME, "r") as file:
            encoding = file.read()
        self.decode(encoding)
            
    def decode(self, encoding: str):
        i = 0
        while i < len(encoding):
            delimiter_index = encoding.find(".", i)
            key_length = int(encoding[i:delimiter_index])
            i = delimiter_index + 1 + key_length
            key = encoding[delimiter_index + 1:i]
            
            delimiter_index = encoding.find(".", i)
            val_length = int(encoding[i:delimiter_index])
            i = delimiter_index + 1 + val_length
            val = encoding[delimiter_index + 1:i]
            
            self.store[key] = val
    
if __name__ == "__main__":
    kvstore = KVStore()
    
    kvstore.setValue("a", "1")
    assert kvstore.getValue("a") == "1"
    with pytest.raises(KeyError):kvstore.getValue("b")
    kvstore.setValue("bas", "123")
    kvstore.persist_to_disk()
    kvstore.clear()
    kvstore.restore_from_disk()
    assert kvstore.getValue("a") == "1"
    assert kvstore.getValue("bas") == "123"
    
    print("All tests passed")
