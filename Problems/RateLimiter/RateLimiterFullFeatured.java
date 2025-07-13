// Author: Tushar Jaiswal
// Creation Date: 2025-07-13

// RateLimiter which implements a sliding time window where requests are allowed up to the configured number.

// This supports multiple time windows such as the following.
// Allow:
//     max 1 request per second
//     max 5 requests per minute
//     max 100 requests per hour

//  Support for bursting and cooldown
//  1. Bursting: Allow more requests in a short time than the steady rate (e.g., allow 5 instant requests, then enforce 1/s rate).
//  2. Cooldown: If a client exceeds the limit, block further requests for a cooldown period (e.g., 10 seconds).

//  Runtime Complexity: Each shouldAllowRequest check is O(|Rules| * MaxAllowedRequestsAmongstRules)
//  Space Complexity: Map is O(|Clients| * |Rules| * |MaxAllowedRequestsAmongstRules|)

// If we assume, requests come in chronological order, we can use Deque instead of Heap for faster operations

public class RateLimiter {
    private final List<RateLimitRule> rules;
    
    // client -> window size -> queue of timestamps
    private final Map<String, Map<Integer, Deque<Integer>>> clientWindowMap;
    
    // client -> window size -> cooldown_end_timestamp
    private final Map<String, Map<Integer, Integer>> clientCooldownMap;

    
    public RateLimiter(List<RateLimitRule> rules) {
        this.rules = rules;
        this.clientWindowMap = new HashMap<>();
        this.clientCooldownMap = new HashMap<>();
    }

    public boolean shouldAllowRequest(Request request) {
        String client = request.client;
        int timestamp = request.timestamp;

        clientWindowMap.putIfAbsent(client, new HashMap<>());
        clientCooldownMap.putIfAbsent(client, new HashMap<>());
        Map<Integer, Deque<Integer>> windowMap = clientWindowMap.get(client);
        Map<Integer, Integer> cooldownMap = clientCooldownMap.get(client);

        for (RateLimitRule rule : rules) {
            int window = rule.windowSize;
            int burst = rule.burstCapacity;
            Integer cooldown = rule.cooldownPeriod;

            // Check if in cooldown
            Integer cooldownUntil = cooldownMap.get(window);
            if (cooldownUntil != null && timestamp < cooldownUntil) {
                return false;
            }

            windowMap.putIfAbsent(window, new LinkedList<>());
            Deque<Integer> q = windowMap.get(window);

            // Remove expired entries
            while (!q.isEmpty() && timestamp - q.peekFirst() >= window) {
                q.pollFirst();
            }

            if (q.size() >= burst) {
                // If cooldown is configured, set it
                if (cooldown != null) {
                    cooldownMap.put(window, timestamp + cooldown);
                }
                return false;
            }
        }

        // Passed all rules, add timestamp to queues
        for (RateLimitRule rule : rules) {
            int window = rule.windowSize;
            windowMap.get(window).addLast(timestamp);
        }

        return true;
    }

    // === Example usage ===
    public static void main(String[] args) {
        List<RateLimitRule> rules = Arrays.asList(
            new RateLimitRule(1, 1, 2, 5),      // 2 burst/sec, cooldown 5s
            new RateLimitRule(60, 10, 15, 10)   // 15 burst/min, cooldown 10s
        );

        RateLimiter limiter = new RateLimiter(rules);

        List<Request> requests = Arrays.asList(
            new Request("user1", 1),
            new Request("user1", 1),
            new Request("user1", 1),  // should block + cooldown
            new Request("user1", 2),  // still blocked (cooldown)
            new Request("user1", 7),  // after cooldown
            new Request("user1", 8)
        );

        for (Request r : requests) {
            boolean allowed = limiter.shouldAllowRequest(r);
            System.out.println(String.format("Request at time %d from %s is %s",
                r.timestamp, r.client, allowed ? "allowed" : "blocked"));
        }
    }
}

class Request {
    String client;
    int timestamp;

    public Request(String client, int timestamp) {
        this.client = client;
        this.timestamp = timestamp;
    }
}

class RateLimitRule {
    int windowSize;        // in seconds
    int maxRequests;
    int burstCapacity;
    Integer cooldownPeriod;  // in seconds (nullable)

    public RateLimitRule(int windowSize, int maxRequests, Integer burstCapacity, Integer cooldownPeriod) {
        this.windowSize = windowSize;
        this.maxRequests = maxRequests;
        this.burstCapacity = (burstCapacity != null) ? burstCapacity : maxRequests;
        this.cooldownPeriod = cooldownPeriod;
    }
}
