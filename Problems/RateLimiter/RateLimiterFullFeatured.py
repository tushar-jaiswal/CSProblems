# Author: Tushar Jaiswal
# Creation Date: 2025-07-13

# RateLimiter which implements a sliding time window where requests are allowed up to the configured number.

# This supports multiple time windows such as the following.
# Allow:
#     max 1 request per second
#     max 5 requests per minute
#     max 100 requests per hour

# Support for bursting and cooldown
# 1. Bursting: Allow more requests in a short time than the steady rate (e.g., allow 5 instant requests, then enforce 1/s rate).
# 2. Cooldown: If a client exceeds the limit, block further requests for a cooldown period (e.g., 10 seconds).

# Runtime Complexity: Each shouldAllowRequest check is O(|Rules| * MaxAllowedRequestsAmongstRules)
# Space Complexity: Map is O(|Clients| * |Rules| * |MaxAllowedRequestsAmongstRules|)

# If we assume, requests come in chronological order, we can use Deque instead of Heap for faster operations

from collections import defaultdict, deque

class Request:
    def __init__(self, client, timestamp):
        self.client = client
        self.timestamp = timestamp
        # Other info like requestID useful in realWorld but not needed for rateLimiter functionality
        
class RateLimitRule:
    def __init__(self, window_size, max_requests, burst_capacity=None, cooldown_period=None):
        self.window_size = window_size                # In seconds
        self.max_requests = max_requests              # Steady rate
        self.burst_capacity = burst_capacity or max_requests
        self.cooldown_period = cooldown_period        # Optional cooldown in seconds
    
class RateLimiter:
    def __init__(self, rules):
        """
        rules: list of RateLimitRule objects
        """
        self.rules = rules
        
        # client -> window -> deque[timestamps]
        self.client_window_map = defaultdict(lambda: defaultdict(deque))  
        
        # client -> window -> cooldown_end_timestamp
        self.client_cooldown_map = defaultdict(dict)  


    def should_allow_request(self, request):
        client = request.client
        timestamp = request.timestamp

        for rule in self.rules:
            window = rule.window_size
            max_requests = rule.max_requests
            burst_capacity = rule.burst_capacity
            cooldown = rule.cooldown_period

            # Check cooldown
            cooldown_until = self.client_cooldown_map[client].get(window)
            if cooldown_until and timestamp < cooldown_until:
                return False  # In cooldown

            q = self.client_window_map[client][window]

            # Remove expired requests
            while q and timestamp - q[0] >= window:
                q.popleft()

            if len(q) >= burst_capacity:
                # Trigger cooldown if configured
                if cooldown:
                    self.client_cooldown_map[client][window] = timestamp + cooldown
                return False

        # Passed all checks; add timestamp
        for rule in self.rules:
            window = rule.window_size
            self.client_window_map[client][window].append(timestamp)

        return True


# Example usage
if __name__ == "__main__":
    rules = [
        RateLimitRule(window_size=1, max_requests=1, burst_capacity=2, cooldown_period=5),     # 2 requests/sec burst, cooldown 5s
        RateLimitRule(window_size=60, max_requests=10, burst_capacity=15, cooldown_period=10), # burst of 15/min, 10s cooldown
    ]

    limiter = RateLimiter(rules)

    test_requests = [
        Request("user1", 1),
        Request("user1", 1),
        Request("user1", 1),
        Request("user1", 2),  # In cooldown
        Request("user1", 7),  # After cooldown
        Request("user1", 8),
    ]

    for r in test_requests:
        result = limiter.should_allow_request(r)
        print(f"Request at time {r.timestamp} from {r.client} is {'allowed' if result else 'blocked'}")
