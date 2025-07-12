// Author: Tushar Jaiswal
// Creation Date: 2025-07-12

// RateLimiter which implements a sliding time window where requests are allowed up to the configured number.

/*
Runtime Complexity: Each shouldAllowRequest check is O(log(ClientRequestsInAllowedTimeWindow))
Space Complexity: Map is O(|Clients| * |Requests per client|)
*/

public class RateLimiter {
    int timeWindow;
    int requestLimit;
    Map<String, PriorityQueue<Request>> clientRequestMap; 
    
    public RateLimiter(int window, int allowedRequestCount) {
        timeWindow = window;
        requestLimit = allowedRequestCount;
        clientRequestMap = new HashMap<String, PriorityQueue<Request>>();
    }
    
    public boolean shouldAllowRequest(Request request) {
        String client = request.client;
        int timestamp = request.timestamp;
        if(!clientRequestMap.containsKey(client)) {
            clientRequestMap.put(client, new PriorityQueue<Request>());
        }
        PriorityQueue<Request> pq = clientRequestMap.get(client);
        
        // Remove all requests that are outside the current allowed time window
        while(pq.size() > 0 && request.timestamp - pq.peek().timestamp >= timeWindow) {
            pq.poll();
        }
        
        pq.add(request);
        if(pq.size() > requestLimit) {
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

class Request implements Comparable<Request> {
    String client;
    int timestamp;
    // Other info like requestID useful in realWorld but not needed for rateLimiter functionality
    
    public Request(String client, int timestamp) {
        this.client = client;
        this.timestamp = timestamp;
    }
    
    @Override
    public int compareTo(Request e) {
        return this.timestamp - e.timestamp;
    }
}
