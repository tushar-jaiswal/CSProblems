# You're building a system to track balances for the OpenAI API credit purchasing system.
# API users can purchase credit grants, specified by an ID, that are active at a timestamp and that let them use the API.
# Unused credit grant balances expire at a certain time.
# However, there's a kink in our system: requests can arrive out of order because our system is built on a really unstable network.
# For example, a request to subtract credits can arrive at the system before the request to add credits, even though the request to add credits has a lower timestamp.


# Your task is to implement the Credits class, which should support the following operations:
# - Granting credits, subtracting credits, and getting the credit balance for a user
# - The ability to handle requests that arrive out of order
# - Showing users their balance at a certain time in the past (for audit purposes)


# Some guidelines/hints:
# - Do NOT worry about memory or performance concerns. Write simple, working code. No fancy data structures are needed here.
# - All timestamps can be represented as ints for simplicity and are unique (no two actions will happen at the same timestamp).
# - Subtract from grants expiring soonest first.
# - Hint: Because requests are out of order, you may not want to do computation at the time of a request. You'll need to store the request and then do the computation when you get a request to get the balance.
# - You will need to subtract from multiple grants: ex. if you have two grants of 2 credits each, and you subtract 3 credits, you should subtract 2 from one grant and 1 from the other.
# - The balance is guaranteed to reconcile eventually (i.e. if you subtract more credits than a user has, they will eventually get a grant that will bring their balance back to positive). You can assume that the balance will never go negative.
# - You can assume that the expiration timestamp is always greater than the timestamp of the grant.


# Here's an example class that you'll want to fill in:

class Credits:
    def __init__(self):
        self.credits = []
        self.orders = []


    def create_grant(self, grant_id: str, amount: int, expiration_timestamp: int, timestamp: int) -> None:
        self.credits.append((grant_id, amount, expiration_timestamp, timestamp))
        # track usage between start and end time
  
    def subtract(self, amount: int, timestamp: int) -> None:
        self.orders.append((amount, timestamp))


    def get_balance(self, timestamp: int) -> int:
        credit_copy = [(grant_id, amount, expiration_timestamp, timestamp) for (grant_id, amount, expiration_timestamp, timestamp) in self.credits]
        orders_copy = [(amount, timestamp) for (amount, timestamp) in self.orders]
        orders_copy = sorted(orders_copy, key = lambda x: x[1])
        credit_copy = sorted(credit_copy, key = lambda x: x[2])
        
        for i, (grant_id, credit, end, start) in enumerate(credit_copy):
            if start > timestamp:
                break

            for j, (deduct, deduct_time) in enumerate(orders_copy):
                if deduct_time > timestamp or deduct_time > end:
                    break
                if start <= deduct_time <= end:
                    if deduct <= credit:
                        credit_copy[i] = (grant_id, credit - deduct, end, start)
                        orders_copy[j] = (0, deduct_time)
                    else:
                        credit_copy[i] = (grant_id, 0, end, start)
                        orders_copy[j] = (deduct - credit, deduct_time)
                            
        balance = 0
        for grant_id, credit, end, start in credit_copy:
            if start <= timestamp < end:
                balance += credit
        
        return balance


# Make sure you understand these test cases in detail before writing any code.
# Test case 1 - basic subtraction


credits = Credits()
credits.subtract(amount=1, timestamp=30)
credits.create_grant(grant_id="a", amount=1, timestamp=10, expiration_timestamp=100)
assert credits.get_balance(timestamp=10) == 1
assert credits.get_balance(timestamp=30) == 0
assert credits.get_balance(timestamp=20) == 1
assert credits.get_balance(timestamp=20) == 1


# # Explanation
# # 10: 1 (a)
# # 20: 1 (a)
# # 30: 0 (a -1)


# # Test case 2 - expiration


credits = Credits()
credits.subtract(amount=1, timestamp=30)
credits.create_grant(grant_id="a", amount=2, timestamp=10, expiration_timestamp=100)
assert credits.get_balance(timestamp=10) == 2
assert credits.get_balance(timestamp=20) == 2
assert credits.get_balance(timestamp=30) == 1
assert credits.get_balance(timestamp=100) == 0


# # # Explanation
# # # 10: 2 (a)
# # # 20: 2 (a)
# # # 30: 1 (a -1)
# # # 100: 0 (the remainder of a expired)


# # # Test case 3 - subtracting from soonest expiring grants first
credits = Credits()
credits.create_grant(grant_id="a", amount=3, timestamp=10, expiration_timestamp=60)
assert credits.get_balance(10) == 3
credits.create_grant(grant_id="b", amount=2, timestamp=20, expiration_timestamp=40)
credits.subtract(amount=1, timestamp=30)
credits.subtract(amount=3, timestamp=50)
assert credits.get_balance(10) == 3
assert credits.get_balance(20) == 5
assert credits.get_balance(30) == 4
assert credits.get_balance(40) == 3 # minimize expiring. Subtract expiration from usage
assert credits.get_balance(50) == 0


# # Explanation
# # 10: 3 (a)
# # 20: 5 (a=3, b=2)
# # 30: 4 (subtract 1 from b, so b=1), since it expires first, a=3
# # 40: 3 (b expired)
# # 50: 0 (subtract 3 from a)


# # Test case 4 - subtract from many grants
credits = Credits()
credits.create_grant(grant_id="a", amount=3, timestamp=10, expiration_timestamp=60)
credits.create_grant(grant_id="b", amount=2, timestamp=20, expiration_timestamp=80)
credits.subtract(amount=4, timestamp=30)
assert credits.get_balance(10) == 3
assert credits.get_balance(20) == 5
assert credits.get_balance(30) == 1
assert credits.get_balance(70) == 1


# # Explanation
# # 10: 3 (a)
# # 20: 5 (a=3, b=2)
# # 30: 1 (subtract 3 from a, 1 from b)
# # 70: 1 (a expired, b=1)

