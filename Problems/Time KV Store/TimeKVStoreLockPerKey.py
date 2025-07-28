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
import threading
from bisect import bisect_right
from collections import defaultdict

class RealTimeKVStoreThreadSafe:
    # Each key has its own threading.Lock.
    # set() and get() acquire a simple mutex lock per key.
    # Ensures no two threads can read/write the same key simultaneously.
    # But blocks all operations (even reads) for that key when locked.

    def __init__(self):
        self.store = defaultdict(list)        # key -> list of (timestamp, value)
        self.locks = defaultdict(threading.Lock)  # key -> Lock

    def set(self, key: str, value: str) -> None:
        ts = time.time()
        with self._get_lock(key):
            self.store[key].append((ts, value))

    def get(self, key: str, timestamp: float) -> str | None:
        with self._get_lock(key):
            if key not in self.store:
                return None

            arr = self.store[key]
            idx = bisect_right(arr, (timestamp, chr(127)))
            if idx == 0:
                return None
            return arr[idx - 1][1]

    def _get_lock(self, key: str) -> threading.Lock:
        # Lock creation is thread-safe because defaultdict lazily creates a new Lock on missing key
        return self.locks[key]


def test_real_time_kv():
    kv = RealTimeKVStoreThreadSafe()
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
    
def test_threadsafe_kv():
    kv = RealTimeKVStoreThreadSafe()

    def writer(key, value):
        for i in range(5):
            now = time.time()
            kv.set(key, value + str(i))
            print(f"Write {key},{value}{i} at {now:.2f}")
            time.sleep(0.1)

    def reader(key):
        for _ in range(5):
            now = time.time()
            result = kv.get(key, now)
            print(f"Read {key} at {now:.2f}: {result}")
            time.sleep(0.1)

    t1 = threading.Thread(target=writer, args=("a", "apple"))
    t2 = threading.Thread(target=reader, args=("a",))
    t3 = threading.Thread(target=writer, args=("b", "banana"))
    t4 = threading.Thread(target=reader, args=("b",))

    t1.start()
    t2.start()
    t3.start()
    t4.start()
    t1.join()
    t2.join()
    t3.join()
    t4.join()

    print("Multithreaded test passed")

if __name__ == "__main__":
    test_real_time_kv()
    test_threadsafe_kv()
