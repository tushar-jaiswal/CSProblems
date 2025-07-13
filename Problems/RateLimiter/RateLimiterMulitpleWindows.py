# Author: Tushar Jaiswal
# Creation Date: 2025-07-12

# RateLimiter which implements a sliding time window where requests are allowed up to the configured number.

# This supports multiple time windows such as the following.
# Allow:
#     max 1 request per second
#     max 5 requests per minute
#     max 100 requests per hour

# Runtime Complexity: Each shouldAllowRequest check is O(log(ClientRequestsInAllowedTimeWindow))
# Space Complexity: Map is O(|Clients| * |Requests per client|)

# If we assume, requests come in chronological order, we can use Deque instead of Heap for faster operations

from collections import defaultdict, deque

class Request:
    def __init__(self, client, timestamp):
        self.client = client
        self.timestamp = timestamp
        # Other info like requestID useful in realWorld but not needed for rateLimiter functionality
    
class RateLimiter:
    def __init__(self, rate_limits):
        """
        rate_limits: list of (window_size_in_seconds, max_requests_allowed)
        Example: [(1, 1), (60, 5), (3600, 100)]
        """
        self.rate_limits = rate_limits
        # {client: {window: deque of timestamps}}
        self.client_window_map = defaultdict(lambda: defaultdict(deque))

    def should_allow_request(self, request):
        client = request.client
        timestamp = request.timestamp

        for window, limit in self.rate_limits:
            q = self.client_window_map[client][window]

            # Remove outdated timestamps
            while q and timestamp - q[0] >= window:
                q.popleft()

            # Check limit
            if len(q) >= limit:
                return False

        # All windows passed, now add to each
        for window, _ in self.rate_limits:
            self.client_window_map[client][window].append(timestamp)

        return True


# Example usage
if __name__ == "__main__":
    # Define 3 rate limits: 1/s, 5/min, 100/hour
    limiter = RateLimiter([
        (1, 1),       # 1 request per second
        (60, 5),      # 5 requests per minute
        (3600, 100)   # 100 requests per hour
    ])

    requests = [
        Request("user1", 1),
        Request("user1", 1),
        Request("user1", 2),
        Request("user1", 3),
        Request("user1", 4),
        Request("user1", 5),
        Request("user1", 6),
    ]

    for r in requests:
        allowed = limiter.should_allow_request(r)
        print(f"Request at time {r.timestamp} from {r.client} is {'allowed' if allowed else 'blocked'}")
