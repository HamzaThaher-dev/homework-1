# Vehicle Rental Management System

A Java console application for managing cars, motorcycles, trucks, customers, rentals, returns, and maintenance.

## Requirements

- Java Development Kit (JDK) 17 or newer
- A terminal or command prompt

## Compile and run

From the project root:

```sh
javac -d out src/*.java
java -cp out Main
```

The program opens a numbered menu. Use it to add and search vehicles, register and update customers, rent and return vehicles, inspect rental history and revenue, and manage maintenance.

## Rental pricing

- Car: daily rate, with optional insurance at $15 per day.
- Motorcycle: daily rate, with optional helmet at $5 per day.
- Truck: daily rate plus a fixed $50 service fee per rental.

## Project contents

- `src/` — Java source files.
- `docs/Vehicle-Rental-UML.pdf` — the supplied class diagram, with its filename corrected for this project.
- `REPORT.pdf` — the three-page design report.
- `docs/screenshots/` — menu and sample rental workflow captures.

Build output is written to `out/` and ignored by Git.
