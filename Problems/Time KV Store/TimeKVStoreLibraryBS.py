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

import time
from bisect import bisect_right
from collections import defaultdict

class RealTimeKVStore:
    def __init__(self):
        self.store = defaultdict(list)  # key -> list of (timestamp, value)

    def set(self, key: str, value: str) -> None:
        ts = time.time()
        self.store[key].append((ts, value))

    def get(self, key: str, timestamp: float) -> str | None:
        if key not in self.store:
            return None

        arr = self.store[key]
        idx = bisect_right(arr, (timestamp, chr(127)))
        if idx == 0:
            return None
        return arr[idx - 1][1]

def test_real_time_kv():
    kv = RealTimeKVStore()
    kv.set("a", "v1")
    time.sleep(1)
    t1 = time.time()
    kv.set("a", "v2")
    time.sleep(1)
    t2 = time.time()
    kv.set("a", "v3")

    assert kv.get("a", t1 - 0.1) == "v1"
    assert kv.get("a", t1 + 0.1) == "v2"
    assert kv.get("a", t2 + 0.5) == "v3"
    assert kv.get("a", t2 - 0.5) == "v2"
    assert kv.get("a", t1) == "v1"
    assert kv.get("a", t1 - 2) is None

    print("All tests passed")

if __name__ == "__main__":
    test_real_time_kv()
