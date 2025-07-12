# Author: Tushar Jaiswal
# Creation Date: 2025-07-12

# RateLimiter which implements a sliding time window where requests are allowed up to the configured number.

# Runtime Complexity: Each shouldAllowRequest check is O(ClientRequestsInAllowedTimeWindow)
# Space Complexity: Map is O(|Clients| * |Requests per client|)

# If we assume, requests come in chronological order, we can use Deque instead of Heap for faster operations

import heapq
from collections import defaultdict, deque

class Request:
    def __init__(self, client, timestamp):
        self.client = client
        self.timestamp = timestamp
        # Other info like requestID useful in realWorld but not needed for rateLimiter functionality
    
class RateLimiter:
    def __init__(self, window, allowed_request_count):
        self.time_window = window
        self.request_limit = allowed_request_count
        self.client_request_map = defaultdict(deque) # client -> deque of timestamps
    
    def should_allow_request(self, request):
        client = request.client
        timestamp = request.timestamp
        q = self.client_request_map[client]
        
        # Remove all requests that are outside the current allowed time window from the left of the deque
        while q and timestamp - q[0] >= self.time_window:
            q.popleft()
            
        q.append(timestamp)
        if len(q) > self.request_limit:
            return False
        else:
            return True

if __name__ == "__main__":
    limiter = RateLimiter(1, 1) # 1 request per second is allowed
    r1 = Request("a", 1)
    r2 = Request("a", 1)
    r3 = Request("a", 2)
    r4 = Request("b", 1)
    
    print(f"Client a Request r1 at time 1 is {limiter.should_allow_request(r1)}")
    print(f"Client a Request r2 at time 1 is {limiter.should_allow_request(r2)}")
    print(f"Client a Request r3 at time 2 is {limiter.should_allow_request(r3)}")
    print(f"Client b Request r4 at time 1 is {limiter.should_allow_request(r4)}")
