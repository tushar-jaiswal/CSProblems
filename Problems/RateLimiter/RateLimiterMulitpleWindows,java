// Author: Tushar Jaiswal
// Creation Date: 2025-07-13

// RateLimiter which implements a sliding time window where requests are allowed up to the configured number.

 // This supports multiple time windows such as the following.
 // Allow:
 //     max 1 request per second
 //     max 5 requests per minute
 //     max 100 requests per hour

/*
Runtime Complexity: Each shouldAllowRequest check is O(|Rate Limits| * MaxAllowedRequestsAmongstRateLimits)
Space Complexity: Map is O(|Clients| * |Windows| * |Requests per client|)
*/

// If we assume, requests come in chronological order, we can use Deque instead of Heap for faster operations

public class RateLimiter {
    // List of rate limits: each entry is a (windowInSeconds, maxRequests) pair
    private List<RateLimitRule> rateLimitRules;
    
    // client -> window size -> queue of timestamps
    private Map<String, Map<Integer, Deque<Integer>>> clientWindowMap;
    
    public RateLimiter(List<RateLimitRule> rateLimitRules) {
        this.rateLimitRules = rateLimitRules;
        this.clientWindowMap = new HashMap<>();
    }
    
    public boolean shouldAllowRequest(Request request) {
        String client = request.client;
        int timestamp = request.timestamp;
        
        clientWindowMap.putIfAbsent(client, new HashMap<>());
        Map<Integer, Deque<Integer>> windowMap = clientWindowMap.get(client);

        // First, check all rules
        for (RateLimitRule rule : rateLimitRules) {
            int window = rule.windowSize;
            int limit = rule.maxRequests;

            windowMap.putIfAbsent(window, new LinkedList<>());
            Deque<Integer> q = windowMap.get(window);

            // Remove old timestamps outside the window
            while (!q.isEmpty() && timestamp - q.peekFirst() >= window) {
                q.pollFirst();
            }

            if (q.size() >= limit) {
                return false;
            }
        }

        // If all checks passed, add timestamp to each window's queue
        for (RateLimitRule rule : rateLimitRules) {
            int window = rule.windowSize;
            windowMap.get(window).addLast(timestamp);
        }

        return true;
    }
    
    public static void main(String[] args) {
        // Define rules: 1/s, 5/min, 100/hour
        List<RateLimitRule> rules = Arrays.asList(
            new RateLimitRule(1, 1),
            new RateLimitRule(60, 5),
            new RateLimitRule(3600, 100)
        );

        RateLimiter limiter = new RateLimiter(rules);

        List<Request> requests = Arrays.asList(
            new Request("user1", 1),
            new Request("user1", 1),
            new Request("user1", 2),
            new Request("user1", 3),
            new Request("user1", 4),
            new Request("user1", 5),
            new Request("user1", 6)
        );

        for (Request r : requests) {
            boolean allowed = limiter.shouldAllowRequest(r);
            System.out.printf("Request at time %d from %s is %s%n",
                    r.timestamp, r.client, allowed ? "allowed" : "blocked");
        }
    }
}

// Helper class to represent a rate limit rule
class RateLimitRule {
    int windowSize;     // in seconds
    int maxRequests;

    public RateLimitRule(int windowSize, int maxRequests) {
        this.windowSize = windowSize;
        this.maxRequests = maxRequests;
    }
}

class Request {
    String client;
    int timestamp;
    // Other info like requestID useful in realWorld but not needed for rateLimiter functionality
    
    public Request(String client, int timestamp) {
        this.client = client;
        this.timestamp = timestamp;
    }
}
