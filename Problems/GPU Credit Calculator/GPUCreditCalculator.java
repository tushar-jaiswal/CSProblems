//  Author: Tushar Jaiswal
//  Creation Date: 2025-07-26

// Implement a GPU credit calculator.

// class GPUCredit:
//         def addCredit(creditID: str, amount: int, timestamp: int, expiration: int) -> None：
//     # A credit is an offering of GPU balance that expires after some expiration-time. The credit can be used only during [timestamp, timestamp + expiration]. **Check with your interviewer whether this period is inclusive '[]' or exclusive '()'. Examples given were inclusive.** A credit can be repeatedly used until expiration.
       
//         def getBalance(timestamp: int) -> int | None: # return the balance remaining on the account at the timestamp, return None if there are no credit left. Note, balance cannot be negative. See edge case below.
         
//         def useCredit(timestamp: int, amount: int) -> None

// Example 1:
// gpuCredit = GPUCredit()
// gpuCredit.addCredit('microsoft', 10, 10, 30)
// gpuCredit.getBalance(0) # returns None
// gpuCredit.getBalance(10) # returns 10
// gpuCredit.getBalance(40) # returns 10
// gpuCredit.getBalance(41) # returns None

// Example 2:
// gpuCredit = GPUCredit()
// gpuCredit.addCredit('amazon', 40, 10, 50)
// gpuCredit.useCredit(30, 30)
// gpuCredit.getBalance(40) # returns 10
// gpuCredit.addCredit('google', 20, 60, 10)
// gpuCredit.getBalance(60) # returns 30
// gpuCredit.getBalance(61) # returns 20
// gpuCredit.getBalance(70) # returns 20
// gpuCredit.getBalance(71) # returns None

import java.util.*;

class GPUCredit {

    // Credit class to hold individual credit details
    private static class Credit {
        String creditId;
        int amount;
        int startTime;
        int endTime;

        public Credit(String creditId, int amount, int startTime, int expiration) {
            this.creditId = creditId;
            this.amount = amount;
            this.startTime = startTime;
            this.endTime = startTime + expiration;
        }

        public boolean isActiveAt(int timestamp) {
            return startTime <= timestamp && timestamp <= endTime;
        }

        public boolean isExpired(int timestamp) {
            return endTime < timestamp;
        }
    }

    private Deque<Credit> credits;

    public GPUCredit() {
        credits = new LinkedList<>();
    }

    public void addCredit(String creditId, int amount, int timestamp, int expiration) {
        credits.addLast(new Credit(creditId, amount, timestamp, expiration));
    }

    public Integer getBalance(int timestamp) {
        removeExpiredCredits(timestamp);
        int total = 0;
        for (Credit credit : credits) {
            if (credit.isActiveAt(timestamp)) {
                total += credit.amount;
            }
        }
        return total > 0 ? total : null;
    }

    public void useCredit(int timestamp, int amount) {
        removeExpiredCredits(timestamp);

        int available = 0;
        for (Credit credit : credits) {
            if (credit.isActiveAt(timestamp)) {
                available += credit.amount;
            }
        }

        if (available < amount) {
            return; // Not enough credit, do nothing
        }

        Deque<Credit> updatedCredits = new LinkedList<>();
        int remaining = amount;

        while (!credits.isEmpty()) {
            Credit credit = credits.pollFirst();
            if (credit.isActiveAt(timestamp)) {
                if (credit.amount <= remaining) {
                    remaining -= credit.amount;
                    credit.amount = 0;
                } else {
                    credit.amount -= remaining;
                    remaining = 0;
                }
            }

            if (credit.amount > 0 || !credit.isActiveAt(timestamp)) {
                updatedCredits.addLast(credit);
            }

            if (remaining == 0) break;
        }

        // Add remaining credits back
        while (!credits.isEmpty()) {
            updatedCredits.addLast(credits.pollFirst());
        }
        credits = updatedCredits;
    }

    private void removeExpiredCredits(int timestamp) {
        credits.removeIf(c -> c.isExpired(timestamp));
    }
}

public class Main {
    public static void main(String[] args) {
        // Example 1
        GPUCredit gpuCredit1 = new GPUCredit();
        gpuCredit1.addCredit("microsoft", 10, 10, 30);
        assert gpuCredit1.getBalance(0) == null;
        assert gpuCredit1.getBalance(10) == 10;
        assert gpuCredit1.getBalance(40) == 10;
        assert gpuCredit1.getBalance(41) == null;

        // Example 2
        GPUCredit gpuCredit2 = new GPUCredit();
        gpuCredit2.addCredit("amazon", 40, 10, 50);
        gpuCredit2.useCredit(30, 30);
        assert gpuCredit2.getBalance(40) == 10;
        gpuCredit2.addCredit("google", 20, 60, 10);
        assert gpuCredit2.getBalance(60) == 30;
        assert gpuCredit2.getBalance(61) == 20;
        assert gpuCredit2.getBalance(70) == 20;
        assert gpuCredit2.getBalance(71) == null;

        System.out.println("All test cases passed.");
    }
}
