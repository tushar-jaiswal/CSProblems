# Author: Tushar Jaiswal
# Creation Date: 2025-07-12

# RateLimiter which implements a sliding time window where requests are allowed up to the configured number.

# Runtime Complexity: Each shouldAllowRequest check is O(log(ClientRequestsInAllowedTimeWindow))
# Space Complexity: Map is O(|Clients| * |Requests per client|)

import heapq
from collections import defaultdict

class Request:
    def __init__(self, client, timestamp):
        self.client = client
        self.timestamp = timestamp
        # Other info like requestID useful in realWorld but not needed for rateLimiter functionality
        
    # For use in priority queue (heapq), we need to define less than
    def __lt__(self, other):
        return self.timestamp < other.timestamp
    
class RateLimiter:
    def __init__(self, window, allowed_request_count):
        self.time_window = window
        self.request_limit = allowed_request_count
        self.client_request_map = defaultdict(list) #map from client to list (heap)
    
    def should_allow_request(self, request):
        client = request.client
        timestamp = request.timestamp
        pq = self.client_request_map[client]
        
        # Remove all requests that are outside the current allowed time window
        while pq and timestamp - pq[0].timestamp >= self.time_window:
            heapq.heappop(pq)
            
        heapq.heappush(pq, request)
        if len(pq) > self.request_limit:
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
