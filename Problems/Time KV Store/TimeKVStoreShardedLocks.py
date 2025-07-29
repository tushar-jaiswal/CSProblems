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

import concurrent.futures
import threading
import time
from bisect import bisect_right
from collections import defaultdict

class ReadWriteLock:
    # supports multiple readers or one writer at a time
    def __init__(self):
        self.lock = threading.Condition(threading.Lock())
        self.readers = 0
        
    def acquire_read(self):
        with self.lock:
            self.readers += 1
    
    def release_read(self):
        with self.lock:
            self.readers -= 1
            if self.readers == 0:
                self.lock.notify_all()
                
    def acquire_write(self):
        self.lock.acquire()
        while self.readers > 0:
            self.lock.wait()
    
    def release_write(self):
        self.lock.release()

class KVStoreShard:
# Each shard (a subset of keys) has one ReadWriteLock.
# TimeMapShard handles a portion of all keys (via hashing).
# Allows:
# Multiple threads to read simultaneously (non-blocking reads).
# Only one writer at a time per shard, blocking all readers temporarily.

# Why Use ReadWriteLock?
#     High read throughput.
#     Threads reading the same shard don't block each other unless a write is active.
#     Only write operations block all reads temporarily.

# Why Use Sharding?
#     Instead of managing one global lock or per-key locks (which could be too many), we divide the key space into buckets (shards).
#     Each shard handles multiple keys but still limits contention.

    def __init__(self):
        self.store = defaultdict(list)        # key -> list of (timestamp, value)
        self.lock = ReadWriteLock()

    def set(self, key: str, value: str) -> None:
        self.lock.acquire_write()
        try:
            ts = time.time()
            self.store[key].append((ts, value))
        finally:
            self.lock.release_write()

    def get(self, key: str, timestamp: float) -> str | None:
        now = time.time()
        if timestamp > now:
            time.sleep(timestamp - now)
        
        self.lock.acquire_read()
        try:
            if key not in self.store:
                return None

            arr = self.store[key]
            idx = bisect_right(arr, (timestamp, chr(127)))
            if idx == 0:
                return None
            return arr[idx - 1][1]
        finally:
            self.lock.release_read()

    def _get_lock(self, key: str) -> threading.Lock:
        # Lock creation is thread-safe because defaultdict lazily creates a new Lock on missing key
        return self.locks[key]
    
class KVStore:
    SHARDING_SIZE = 16
    
    def __init__(self):
        self.maps = [KVStoreShard() for _ in range(KVStore.SHARDING_SIZE)]

    def get_shard(self, key):
        return self.maps[hash(key) % KVStore.SHARDING_SIZE]
    
    def set(self, key, val):
        self.get_shard(key).set(key, val)
    
    def get(self, key, timestamp):
        return self.get_shard(key).get(key, timestamp)

def test_real_time_kv():
    kv = KVStore()
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
    kv = KVStore()

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

def run_simulation():
    kv = KVStore()

    def writer_task(key, value):
        for i in range(5):
            now = time.time()
            kv.set(key, value + str(i))
            print(f"Write {key},{value}{i} at {now:.2f}")
            time.sleep(0.1)

    def reader_task(key):
        for _ in range(5):
            now = time.time()
            result = kv.get(key, now)
            print(f"Read {key} at {now:.2f}: {result}")
            time.sleep(0.1)
            
    keys = ["a", "b", "c"]
    values = ["apple", "banana", "cherry"]

    with concurrent.futures.ThreadPoolExecutor(max_workers=6) as executor:
        futures = []
        for i in range(3):
            futures.append(executor.submit(writer_task, keys[i], values[i]))
            futures.append(executor.submit(reader_task, keys[i]))

        # Optional: wait for all tasks
        for f in futures:
            f.result()
    print("Tests using ThreadPoolExecutor passed")

if __name__ == "__main__":
    test_real_time_kv()
    test_threadsafe_kv()
