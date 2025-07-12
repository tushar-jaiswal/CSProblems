// Author: Tushar Jaiswal
// Creation Date: 2025-07-12

// RateLimiter which implements a sliding time window where requests are allowed up to the configured number.

/*
Runtime Complexity: Each shouldAllowRequest check is O(ClientRequestsInAllowedTimeWindow)
Space Complexity: Map is O(|Clients| * |Requests per client|)
*/

// If we assume, requests come in chronological order, we can use Deque instead of Heap for faster operations

public class RateLimiter {
    int timeWindow;
    int requestLimit;
    Map<String, Deque<Integer>> clientRequestMap; 
    
    public RateLimiter(int window, int allowedRequestCount) {
        timeWindow = window;
        requestLimit = allowedRequestCount;
        clientRequestMap = new HashMap<>();
    }
    
    public boolean shouldAllowRequest(Request request) {
        String client = request.client;
        int timestamp = request.timestamp;
        if(!clientRequestMap.containsKey(client)) {
            clientRequestMap.put(client, new ArrayDeque<Integer>());
        }
        Deque<Integer> q = clientRequestMap.get(client);
        
        // Remove all requests that are outside the current allowed time window
        while(q.size() > 0 && request.timestamp - q.peekFirst() >= timeWindow) {
            q.pollFirst();
        }
        
        q.addLast(timestamp);
        if(q.size() > requestLimit) {
            return false;
        } else {
            return true;
        }
        
    }
    
    public static void main(String[] args) {
        System.out.println("Hello World!");
        RateLimiter limiter = new RateLimiter(1, 1); // 1 request per second is allowed
        Request r1 = new Request("a", 1);
        Request r2 = new Request("a", 1);
        Request r3 = new Request("a", 2);
        Request r4 = new Request("b", 1);
        
        System.out.println(String.format("Client a Request r1 at time 1 is %b",  limiter.shouldAllowRequest(r1)));
        System.out.println(String.format("Client a Request r2 at time 1 is %b",  limiter.shouldAllowRequest(r2)));
        System.out.println(String.format("Client a Request r3 at time 2 is %b",  limiter.shouldAllowRequest(r3)));
        System.out.println(String.format("Client b Request r4 at time 2 is %b",  limiter.shouldAllowRequest(r4)));
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
