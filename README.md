# PRIZZ — Enterprise On-Demand Logistics Marketplace

> **Course:** Programming in Java (25CSE0202) · Chitkara University  
> **Evaluation:** CE-III: Project Milestone Evaluation (25 Marks)  
> **Team Size:** 4 members

---

## 1. Problem Statement *(Rubric: 3 marks)*

Logistics companies and independent contractors waste time and money on opaque, phone-based job allocation.  
**PRIZZ** is a reverse-auction marketplace where:
- **Customers** post service requests (pallet moves, crane lifts, emergency warehousing).
- **Service Providers** bid competitively — the lowest qualifying bid wins.
- The platform manages scheduling, pricing, payment, and audit trails automatically.

The problem is relevant (logistics is a multi-billion-dollar domain), feasible within a semester Java project, and sufficiently specific to demonstrate all required Java concepts.

---

## 2. Objectives & Scope *(Rubric: 2 marks)*

### Measurable Objectives
1. Allow customers to post service requests with a budget ceiling.
2. Enable providers to submit price bids; rank them automatically (lowest price = highest priority).
3. Dispatch requests using a priority-aware queue (standard FIFO, emergency preemption).
4. Persist users and requests to an SQLite database via JDBC.
5. Export bid records to CSV and re-import them for auditing.

### Scope Boundaries
| **In Scope** | **Out of Scope** |
|---|---|
| Complete Interactive Console UI (11 Menu Options) | Web or mobile UI |
| SQLite JDBC persistence & atomic transactions | Cloud databases, Hibernate, Spring |
| In-memory concurrency simulation (wait/notify, threads) | Real network socket communication |
| CSV file I/O with try-with-resources | PDF or Excel reporting |

---

## 3. Project Design & Architecture *(Rubric: 9 marks)*

### Package Structure

```
java-prizz/src/
├── model/          Domain entities and enums (pure data classes)
│   ├── User.java             Abstract base user (OOP)
│   ├── Customer.java         Extends User; Comparable<Customer>
│   ├── ServiceProvider.java  Extends User; holds Set<String> skills
│   ├── Bid.java              Price proposal; Comparable<Bid>
│   ├── Booking.java          Confirmed contract entity
│   ├── ServiceRequest.java   Job posting with budget and status
│   └── RequestStatus.java    Enum: OPEN, ACCEPTED, IN_PROGRESS, etc.
│
├── exception/      Custom checked exceptions (error hierarchy)
│   ├── PrizzAppException.java          Root checked exception
│   ├── DatabaseOperationException.java JDBC errors
│   ├── InvalidBidException.java        Bad bid data
│   └── InsufficientFundsException.java wallet balance check
│
├── dao/            Data Access Objects — JDBC persistence layer
│   ├── GenericDAO.java       Generic CRUD interface <T, ID>
│   ├── DBConnectionManager.java Singleton; auto-creates tables on startup
│   ├── UserDAO.java          CRUD for Customer / ServiceProvider
│   ├── RequestDAO.java       CRUD for ServiceRequest
│   └── TransactionManager.java  Atomic escrow payment (commit/rollback)
│
├── service/        Business logic layer
│   ├── PricingService.java   2D array surge-pricing calculator
│   └── AnalyticsService.java Stream API: filter/map/reduce/groupingBy
│
├── collections/    Collections Framework demonstrations
│   ├── BidComparators.java       Static Comparator constants (lambda + method ref)
│   ├── BidPriorityQueue.java     PriorityQueue wrapper with Iterator demo
│   └── RequestDispatcherQueue.java  Deque: FIFO standard + LIFO emergency
│
├── concurrency/    Multi-threading demonstrations
│   ├── BidBuffer.java        Bounded buffer (wait/notifyAll)
│   ├── BidProducer.java      Implements Runnable — generates bids
│   ├── BidConsumer.java      Extends Thread — drains bids from buffer
│   └── AsyncAuditService.java   ExecutorService thread pool for audit logs
│
├── util/           Reusable utility classes
│   ├── InputValidator.java   Validated console input (int, double, email, non-empty)
│   ├── CsvFileHandler.java   BufferedWriter/Reader CSV export & import
│   ├── ReportFormatter.java  StringBuilder-based ASCII table formatter
│   └── BidFilter.java        @FunctionalInterface for lambda-based filtering
│
└── ui/
    └── Main.java             Entry point — 11-option interactive menu & 1-click test suite
```

### Data Flow Diagram

```
Customer Input (Console)
        │
        ▼
   ui.Main  ──────────────────────────────────────────────────────────┐
        │                                                              │
        ├──► service.PricingService    (2D array calculation)          │
        ├──► collections.RequestDispatcherQueue  (Deque FIFO/LIFO)    │
        ├──► concurrency.BidBuffer + BidProducer + BidConsumer         │
        │         (Producer-Consumer, wait/notifyAll)                  │
        ├──► collections.BidPriorityQueue  (PriorityQueue ranking)    │
        ├──► service.AnalyticsService  (Stream API)                    │
        ├──► util.CsvFileHandler  (File I/O)                           │
        ├──► exception.*  (Custom checked exceptions)                  │
        └──► dao.*  (JDBC → SQLite database)                           │
                                                                       │
   AsyncAuditService (background thread pool) ◄──────────────────────┘
```

### Data Model (SQLite Tables)

| Table | Key Columns |
|---|---|
| `users` | user_id PK, name, email, role (CUSTOMER/PROVIDER), wallet_balance, rating, loyalty_points |
| `service_requests` | request_id PK, customer_id FK, title, description, budget, status |
| `bookings` | booking_id PK, request_id FK, customer_id FK, provider_id FK, final_amount, payment_completed |

Tables are created automatically on first run via `CREATE TABLE IF NOT EXISTS` in `DBConnectionManager`.

---

## 4. Integration of Course Concepts *(Rubric: 6 marks)*

| Java Concept | Where Demonstrated | Class(es) |
|---|---|---|
| **OOP: Abstraction** | `User` is abstract; forces subclasses to implement `getProfileSummary()` | `model.User` |
| **OOP: Inheritance** | `Customer` and `ServiceProvider` extend `User` | `model.Customer`, `model.ServiceProvider` |
| **OOP: Polymorphism** | `getProfileSummary()` returns different output for each subclass | All User subclasses |
| **OOP: Encapsulation** | All fields private; exposed only via getters/setters | All model classes |
| **Interfaces** | `GenericDAO<T,ID>`, `Comparable<Bid>`, `Runnable` (BidProducer) | `dao.GenericDAO`, `model.Bid`, `concurrency.BidProducer` |
| **Enums** | `RequestStatus` with 7 lifecycle states | `model.RequestStatus` |
| **Generics** | `GenericDAO<T,ID>`, `BidFilter` functional interface | `dao.GenericDAO`, `util.BidFilter` |
| **Wrapper Classes** | `Integer`, `Double`, `Boolean` used throughout | All model classes |
| **Collections: ArrayList** | `liveBids` list in Main | `ui.Main` |
| **Collections: HashMap** | `customerRegistry` keyed by userId | `ui.Main` |
| **Collections: HashSet** | `uniqueCategories`, `skillSet` in ServiceProvider | `ui.Main`, `model.ServiceProvider` |
| **Collections: PriorityQueue** | Automatic bid ranking by price | `collections.BidPriorityQueue` |
| **Collections: ArrayDeque** | FIFO + emergency LIFO dispatch | `collections.RequestDispatcherQueue` |
| **Iterator pattern** | Explicit `Iterator<Bid>` in `BidPriorityQueue` | `collections.BidPriorityQueue` |
| **Comparator** | 4 static Comparator constants (lambda + method ref) | `collections.BidComparators` |
| **2D Arrays** | Surge pricing matrix `double[][]` | `service.PricingService` |
| **Stream: filter** | Filter bids under a price ceiling | `service.AnalyticsService` |
| **Stream: map + reduce** | Total market volume calculation | `service.AnalyticsService` |
| **Stream: groupingBy** | Average bid per request | `service.AnalyticsService` |
| **Stream: summaryStatistics** | Count / min / max / avg in one pass | `service.AnalyticsService` |
| **Functional Interface** | `@FunctionalInterface BidFilter` used with lambda | `util.BidFilter` |
| **Custom Exceptions** | 4 checked exceptions with hierarchy | `exception.*` |
| **Exception Chaining** | `DatabaseOperationException` wraps `SQLException` | `exception.DatabaseOperationException` |
| **Input Validation** | `InputValidator` retries until valid input received | `util.InputValidator` |
| **File I/O** | CSV export/import with `BufferedWriter`/`BufferedReader` | `util.CsvFileHandler` |
| **StringBuilder** | Report card and table assembly | `util.ReportFormatter` |
| **JDBC** | `PreparedStatement`, `ResultSet`, `Connection` | `dao.UserDAO`, `dao.RequestDAO` |
| **JDBC Transactions** | `setAutoCommit(false)`, `commit()`, `rollback()` | `dao.TransactionManager` |
| **Singleton pattern** | `DBConnectionManager` — one instance across app | `dao.DBConnectionManager` |
| **Concurrency: Runnable** | `BidProducer implements Runnable` | `concurrency.BidProducer` |
| **Concurrency: Thread** | `BidConsumer extends Thread`, `join()` | `concurrency.BidConsumer` |
| **Concurrency: wait/notify** | Bounded buffer synchronization | `concurrency.BidBuffer` |
| **Concurrency: ExecutorService** | Thread pool for async audit tasks | `concurrency.AsyncAuditService` |
| **volatile keyword** | Thread-safe singleton creation | `dao.DBConnectionManager` |

---

## 5. Code Quality & Documentation *(Rubric: 5 marks)*

### Naming Conventions
- **Classes:** PascalCase (`BidPriorityQueue`, `AnalyticsService`)
- **Methods:** camelCase (`calculateAdjustedBudget`, `exportBidsToCsv`)
- **Variables:** camelCase (`liveBids`, `customerRegistry`)
- **Packages:** lowercase (`model`, `dao`, `service`, `util`)
- **Constants:** UPPER_SNAKE_CASE (`BY_PRICE_ASC`, `DB_URL`)

### How to Compile & Run

**Prerequisites:** Java JDK 11 or higher installed.

```bash
# From the project root (PRIZZ/)

# 1. Compile all source files
javac -d java-prizz/bin $(find java-prizz/src -name "*.java")

# Windows PowerShell equivalent:
$files = Get-ChildItem -Path java-prizz/src -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
& "C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -d java-prizz/bin @files

# 2. Run the application
java -cp java-prizz/bin ui.Main

# Windows PowerShell equivalent:
& "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp java-prizz/bin ui.Main
```

> **Note on JDBC / SQLite:** The JDBC demos in dao/ require `sqlite-jdbc.jar` on the classpath.  
> Without it, `DBConnectionManager` prints a warning and continues. All 8 console menu options  
> (which use in-memory data) run successfully without any JAR file.

---

## 6. Test Evidence *(Rubric: 5 marks — Code Quality & Documentation)*

### Menu 1 — OOP & 2D Array Pricing Matrix
```
Input:  1
Output: Customer Profile: Alice Walker (ID: 101) | Balance: $1500.00 | Loyalty Points: 250
        0 (Standard)  :    1.00x    1.15x    1.30x
        1 (Urgent)    :    1.25x    1.40x    1.65x
        2 (Emergency) :    1.75x    1.95x    2.25x
        Base $500.00 -> Emergency/Metro: $875.00
```

### Menu 2 — Deque Dispatcher
```
Input:  2
Output: Dispatched -> Request #502 (Emergency - goes FIRST, LIFO)
        Dispatched -> Request #501 (Standard - FIFO order)
        Dispatched -> Request #503 (Standard - FIFO order)
```

### Menu 3 — Producer-Consumer Concurrency
```
Input:  3
Output: [PRODUCER] Added Bid #1 ($247.32) | Buffer size: 1
        [CONSUMER] Took Bid #1 | Thread: [BidConsumerWorker]
        ... (5 bids produced and consumed concurrently)
        Auction complete. Total bids collected: 5
```

### Menu 5 — Stream Analytics
```
Input:  5
Output: Bids under $300.0: [Bid #1 -> $280.00, Bid #3 -> $180.00]
        Total Market Volume (reduce): $810.00
        Avg Bid Per Request (groupingBy): {501=315.0, 502=180.0}
        Stats: Count=3, Min=$180.00, Max=$350.00, Avg=$270.00
```

### Menu 7 — Exception Handling
```
Input:  7
Output: CAUGHT InvalidBidException: [PRIZZ_ERR_INVALID_BID] Bid amount must be positive...
        CAUGHT InsufficientFundsException: Customer #102 balance ($850.00) below $5000.00
        InputValidator demo: retries until 1–10 entered
```

### Edge Cases Tested
| Scenario | Behaviour |
|---|---|
| Negative bid amount | `InvalidBidException` thrown and caught |
| Payment exceeding wallet balance | `InsufficientFundsException` thrown and caught |
| Non-numeric menu input | `NumberFormatException` caught; re-prompts user |
| Empty CSV file read | Returns empty list, no crash |
| `InputValidator.readInt()` with "abc" | Prints error; keeps prompting |

---

## 7. Current Status & Pending Work

### ✅ Completed
- Full package structure with 8 packages, 30 classes
- All 7 syllabus concepts demonstrated and working
- Auto-creating JDBC tables on startup
- Validated input via `InputValidator`
- CSV export and import working
- Concurrency: producer-consumer with wait/notify
- README with rubric-aligned documentation

### 🔧 Pending / Future Enhancements
- Add SQLite JAR to classpath for full JDBC live demo
- Add a `BookingDAO` for Booking entity persistence
- Interactive bid entry (let user type bid amounts, not just demo seeds)
- Unit tests with JUnit 5

---

## 8. Viva Preparation Guide

| Question | Answer |
|---|---|
| What is the entry point? | `ui.Main.main()` — starts the app and shows the 8-option menu |
| Why is User abstract? | Forces Customer and ServiceProvider to implement `getProfileSummary()` — that is Abstraction |
| How does PriorityQueue work? | Internally uses a min-heap; `poll()` always removes the smallest element (cheapest bid) |
| What is the difference between wait() and sleep()? | `wait()` releases the lock; `sleep()` holds it. Use `wait()` for inter-thread coordination |
| Why use PreparedStatement not Statement? | PreparedStatement uses `?` placeholders — prevents SQL injection and is faster for repeated queries |
| What is a functional interface? | An interface with exactly one abstract method; enables lambda expressions |
| Why use try-with-resources? | Automatically closes streams/connections even if an exception is thrown |
| What does `volatile` do in DBConnectionManager? | Ensures the `instance` field is read from main memory, not a thread-local CPU cache |
| How does TransactionManager prevent partial updates? | `setAutoCommit(false)` groups all SQL into one unit; `rollback()` undoes everything if any step fails |
| How is a Deque different from a Queue? | Deque supports insertion/removal at both ends; standard Queue is FIFO only |

---

## 9. Team Member Work Distribution (4 Members)

Each team member is assigned a minimum of 7 classes forming a logical, cohesive sub-system.

### Member 1: Core OOP & Exception Architecture (8 Classes)
- **Assigned Classes:**
  1. `model.User` (Abstract base class demonstrating Abstraction & Encapsulation)
  2. `model.Customer` (Subclass extending User; implements `Comparable<Customer>`)
  3. `model.ServiceProvider` (Subclass extending User; manages `Set<String>` skills)
  4. `model.ServiceRequest` (Job posting data model with encapsulation)
  5. `model.RequestStatus` (Enum state machine for request lifecycle)
  6. `exception.PrizzAppException` (Root checked custom application exception)
  7. `exception.InvalidBidException` (Domain exception for bid validation)
  8. `exception.InsufficientFundsException` (Business exception for wallet checks)
- **Viva Key Points:** Inheritance (`extends`), abstract methods and polymorphism, Encapsulation with private fields, Enum type safety, Custom checked exception hierarchy, constructor chaining (`super`).

### Member 2: Persistence, Database & Transaction Architecture (8 Classes)
- **Assigned Classes:**
  1. `dao.GenericDAO` (Generic interface parameterized with `<T, ID>`)
  2. `dao.DBConnectionManager` (Singleton pattern, thread safety, auto table initialization)
  3. `dao.UserDAO` (CRUD implementation using `PreparedStatement` & `ResultSet`)
  4. `dao.RequestDAO` (CRUD implementation with Enum string conversion)
  5. `dao.TransactionManager` (Atomic multi-table escrow payment with commit & rollback)
  6. `exception.DatabaseOperationException` (Exception chaining wrapping low-level `SQLException`)
  7. `model.Booking` (Confirmed contract entity linking customer, provider, and request)
  8. `model.Bid` (Price proposal entity; natural ordering by price via `Comparable<Bid>`)
- **Viva Key Points:** JDBC connection lifecycle, PreparedStatement parameterization preventing SQL injection, Double-checked locking Singleton with `volatile`, ACID transaction management (`setAutoCommit(false)`, `commit()`, `rollback()`), Generic DAO design.

### Member 3: Collections, Ranking & Business Services (7 Classes)
- **Assigned Classes:**
  1. `collections.BidComparators` (Multiple Comparator constants using lambdas & method references)
  2. `collections.BidPriorityQueue` (Min-heap priority ranking with explicit Iterator traversal)
  3. `collections.RequestDispatcherQueue` (ArrayDeque supporting FIFO dispatch & LIFO emergency preemption)
  4. `service.PricingService` (Surge tariff calculations using 2D primitive arrays `double[][]`)
  5. `service.AnalyticsService` (Stream API: `filter`, `map`, `reduce`, `groupingBy`, `averagingDouble`, `summaryStatistics`)
  6. `util.BidFilter` (`@FunctionalInterface` enabling dynamic lambda filtering)
  7. `util.ReportFormatter` (Console report & ASCII table generation using `StringBuilder`)
- **Viva Key Points:** Collections framework (`PriorityQueue`, `ArrayDeque`, `List`, `Map`, `Set`), Stream pipelines and reduction operations, Lambda expressions and functional interfaces, Memory efficiency using `StringBuilder`.

### Member 4: Concurrency, Input Validation & System Integration (7 Classes)
- **Assigned Classes:**
  1. `concurrency.BidBuffer` (Thread-safe bounded circular buffer using `wait()` and `notifyAll()`)
  2. `concurrency.BidProducer` (Concurrent bid generator implementing `Runnable`)
  3. `concurrency.BidConsumer` (Worker thread extending `Thread`, using `join()` & thread interruption)
  4. `concurrency.AsyncAuditService` (Asynchronous audit logger using `ExecutorService` thread pool)
  5. `util.InputValidator` (Defensive console input validation with regex and retry loops)
  6. `util.CsvFileHandler` (CSV file persistence with `try-with-resources`, `BufferedReader`/`BufferedWriter`)
  7. `ui.Main` (Application entry point and interactive 8-option console orchestrator)
- **Viva Key Points:** Multithreading approaches (`implements Runnable` vs `extends Thread`), Producer-Consumer coordination with `wait()` and `notifyAll()`, Thread pool lifecycle and graceful shutdown, try-with-resources automatic closing, Robust user input validation.

