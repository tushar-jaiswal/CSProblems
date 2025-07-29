# Author: Tushar Jaiswal
# Creation Date: 2025-07-29

# Multiple file iterator
# Not tested

from abc import ABC, abstractmethod
import pytest

class IteratorInterface(ABC):
    @abstractmethod
    def __iter__(self): pass

    @abstractmethod
    def __next__(self): pass

    @abstractmethod
    def get_state(self): pass

    @abstractmethod
    def set_state(self, state): pass

class FileLineIterator(IteratorInterface):
    class State:
        def __init__(self, offset: int):
            self.offset = offset

        def to_dict(self):
            return {"offset": self.offset}

        @staticmethod
        def from_dict(data):
            return FileLineIterator.State(data["offset"])

    def __init__(self, filepath):
        self.filepath = filepath
        self.file = open(filepath, "r")
        self.state = FileLineIterator.State(0)
        self.file.seek(self.state.offset)

    def __iter__(self):
        return self

    def __next__(self):
        self.file.seek(self.state.offset)
        line = self.file.readline()
        if not line:
            raise StopIteration
        self.state.offset = self.file.tell()
        return line.strip()

    def get_state(self):
        return self.state

    def set_state(self, state):
        self.state = state
        self.file.seek(state.offset)

    def close(self):
        self.file.close()

import json

class MultiFileIterator(IteratorInterface):
    class State:
        def __init__(self, file_index, inner_state):
            self.file_index = file_index
            self.inner_state = inner_state

        def to_dict(self):
            return {
                "file_index": self.file_index,
                "inner_state": self.inner_state.to_dict()
            }

        @staticmethod
        def from_dict(data, file_iterators):
            file_index = data["file_index"]
            inner_state = file_iterators[file_index].State.from_dict(data["inner_state"])
            return MultiFileIterator.State(file_index, inner_state)

    def __init__(self, filepaths):
        self.file_iterators = [FileLineIterator(path) for path in filepaths]
        self.state = MultiFileIterator.State(0, self.file_iterators[0].get_state())

    def __iter__(self):
        return self

    def __next__(self):
        while self.state.file_index < len(self.file_iterators):
            current_iter = self.file_iterators[self.state.file_index]
            current_iter.set_state(self.state.inner_state)
            try:
                line = next(current_iter)
                self.state.inner_state = current_iter.get_state()
                return line
            except StopIteration:
                self.state.file_index += 1
                if self.state.file_index < len(self.file_iterators):
                    self.file_iterators[self.state.file_index].set_state(FileLineIterator.State(0))
                    self.state.inner_state = self.file_iterators[self.state.file_index].get_state()
        raise StopIteration("All files exhausted")

    def get_state(self):
        return self.state

    def set_state(self, state):
        self.state = state

    def save_state(self, filepath):
        with open(filepath, "w") as f:
            json.dump(self.state.to_dict(), f)

    def load_state(self, filepath):
        with open(filepath, "r") as f:
            data = json.load(f)
            state = MultiFileIterator.State.from_dict(data, self.file_iterators)
            self.set_state(state)

    def close(self):
        for f in self.file_iterators:
            f.close()


if __name__ == "__main__":
    files = ["file1.txt", "file2.txt", "file3.txt"]

    # Create iterator
    mfi = MultiFileIterator(files)

    # Try consuming first 5 lines
    for _ in range(5):
        try:
            print(next(mfi))
        except StopIteration:
            break

    # Save state
    mfi.save_state("state.json")

    # You can now safely stop here...

    # Resume later
    resumed = MultiFileIterator(files)
    resumed.load_state("state.json")
    for line in resumed:
        print("Resumed:", line)

    mfi.close()
    resumed.close()
