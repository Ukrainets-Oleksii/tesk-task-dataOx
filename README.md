# TechTask

Author: Oleksii Ukrainets

## Requirements

- [Docker](https://docs.docker.com/get-docker/) must be installed
- [Docker Compose](https://docs.docker.com/compose/) must be installed

## How to Run

1. Clone the project locally:
   ```bash
   git clone <repository-url>
   ```
2. Open a terminal and navigate to the project root directory:
   ```bash
   cd <project-root-directory>
   ```
3. Build the project using Docker Compose:
   - Using Docker Compose V2 syntax:
     ```bash
     docker compose build
     ```
   - Using Docker Compose V1 syntax:
     ```bash
     docker-compose build
     ```
4. Start the project using Docker Compose:
   - Using Docker Compose V2 syntax:
     ```bash
     docker compose up
     ```
   - Using Docker Compose V1 syntax:
     ```bash
     docker-compose up
     ```
That’s it. After these steps, the project will be running inside Docker containers.

## Test Cases

There are 3 test controllers with 3 endpoints available.  
You can send requests via Postman to test the following cases:

### Case 1
The user accidentally sends a request to create N + 1 identical orders with price 1.  
Only one valid order should be created; the rest should return an error.

### Case 2
A user accidentally sends a request to create 10 identical orders with price increases from 10 to 100, in increments of 10.
If the buyer's profit at the time of sending is -970, only one valid order should be created; the rest should return an error.

### Case 3
The user sends a request to create N + 1 different orders.  
At the same moment, the client is made inactive (also via API).  
Only the orders that were processed before the client became inactive should be created.

**Note:** For Case 3, the response might return `500 Internal Server Error`, because the user can be deactivated after the program checks that the user is not deleted.  
However, the test case itself works as expected.
