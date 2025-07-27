# Author: Tushar Jaiswal
# Creation Date: 2025-07-27

# Implement a GPU credit calculator.

# class GPUCredit:
#         def addCredit(creditID: str, amount: int, timestamp: int, expiration: int) -> None：
#     # A credit is an offering of GPU balance that expires after some expiration-time. The credit can be used only during [timestamp, timestamp + expiration]. **Check with your interviewer whether this period is inclusive '[]' or exclusive '()'. Examples given were inclusive.** A credit can be repeatedly used until expiration.
       
#         def getBalance(timestamp: int) -> int | None: # return the balance remaining on the account at the timestamp, return None if there are no credit left. Note, balance cannot be negative. See edge case below.
         
#         def useCredit(timestamp: int, amount: int) -> None

# Example 1:
# gpuCredit = GPUCredit()
# gpuCredit.addCredit('microsoft', 10, 10, 30)
# gpuCredit.getBalance(0) # returns None
# gpuCredit.getBalance(10) # returns 10
# gpuCredit.getBalance(40) # returns 10
# gpuCredit.getBalance(41) # returns None

# Example 2:
# gpuCredit = GPUCredit()
# gpuCredit.addCredit('amazon', 40, 10, 50)
# gpuCredit.useCredit(30, 30)
# gpuCredit.getBalance(40) # returns 10
# gpuCredit.addCredit('google', 20, 60, 10)
# gpuCredit.getBalance(60) # returns 30
# gpuCredit.getBalance(61) # returns 20
# gpuCredit.getBalance(70) # returns 20
# gpuCredit.getBalance(71) # returns None

from collections import deque

class GPUCredit:
    def __init__(self):
        # Store credits as (creditID, amount, start_time, end_time)
        self.credits = deque()

    def addCredit(self, creditID: str, amount: int, timestamp: int, expiration: int) -> None:
        end_time = timestamp + expiration
        self.credits.append((creditID, amount, timestamp, end_time))

    def getBalance(self, timestamp: int) -> int | None:
        self._remove_expired(timestamp)
        total = 0
        for _, amount, start, end in self.credits:
            if start <= timestamp <= end:
                total += amount
        return total if total > 0 else None

    def useCredit(self, timestamp: int, amount: int) -> None:
        self._remove_expired(timestamp)
        available = 0
        for _, amt, start, end in self.credits:
            if start <= timestamp <= end:
                available += amt
        if available < amount:
            return  # Not enough credit, ignore

        new_credits = deque()
        remaining = amount
        while self.credits:
            creditID, amt, start, end = self.credits.popleft()
            if start <= timestamp <= end:
                if amt <= remaining:
                    remaining -= amt
                    amt = 0
                else:
                    amt -= remaining
                    remaining = 0
            # Append only if there's remaining credit or it's not active
            if amt > 0 or not (start <= timestamp <= end):
                new_credits.append((creditID, amt, start, end))
            if remaining == 0:
                break
        # Add back remaining credits
        self.credits.extendleft(reversed(new_credits))

    def _remove_expired(self, timestamp: int):
        # Remove all credits whose end_time < timestamp
        self.credits = deque(
            (cid, amt, start, end) for cid, amt, start, end in self.credits if end >= timestamp
        )

def main():
    # Example 1
    gpuCredit = GPUCredit()
    gpuCredit.addCredit('microsoft', 10, 10, 30)
    assert gpuCredit.getBalance(0) is None
    assert gpuCredit.getBalance(10) == 10
    assert gpuCredit.getBalance(40) == 10
    assert gpuCredit.getBalance(41) is None

    # Example 2
    gpuCredit = GPUCredit()
    gpuCredit.addCredit('amazon', 40, 10, 50)
    gpuCredit.useCredit(30, 30)  # use 30 of 40
    assert gpuCredit.getBalance(40) == 10
    gpuCredit.addCredit('google', 20, 60, 10)
    assert gpuCredit.getBalance(60) == 30
    assert gpuCredit.getBalance(61) == 20
    assert gpuCredit.getBalance(70) == 20
    assert gpuCredit.getBalance(71) is None

    print("All test cases passed.")

if __name__ == "__main__":
    main()
