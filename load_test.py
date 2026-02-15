import threading
import requests
import time
import random

API_URL = "http://localhost:8080/journal"
TOTAL_REQUESTS = 1000  # Total requests to simulate
CONCURRENT_THREADS = 50 # Number of concurrent users

def simulate_user(user_id):
    try:
        # 1. Health Check
        response = requests.get(f"{API_URL}/public/health-check", timeout=5)
        if response.status_code == 200:
            print(f"User {user_id}: Health Check OK")
        else:
            print(f"User {user_id}: Health Check FAILED ({response.status_code})")

        # 2. Random delay to simulate real user behavior
        time.sleep(random.uniform(0.1, 0.5))

        # 3. Create User (Optional - commented out to avoid polluting DB too much, or use unique names)
        # username = f"loadtest_user_{user_id}_{random.randint(1, 100000)}"
        # requests.post(f"{API_URL}/public/create-user", json={"username": username, "password": "password", "email": f"{username}@example.com"})

    except Exception as e:
        print(f"User {user_id}: Error - {e}")

threads = []
start_time = time.time()

print(f"Starting load test with {TOTAL_REQUESTS} requests...")

for i in range(TOTAL_REQUESTS):
    t = threading.Thread(target=simulate_user, args=(i,))
    threads.append(t)
    t.start()
    
    # Limit number of active threads
    if len(threads) >= CONCURRENT_THREADS:
        for t in threads:
            t.join()
        threads = []

# Wait for remaining threads
for t in threads:
    t.join()

end_time = time.time()
print(f"\nLoad test completed in {end_time - start_time:.2f} seconds.")
