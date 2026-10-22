NOCH Clothing E-Commerce Application
A full-stack e-commerce application built by a team of six as a first-year group project at Brunel University London (Sep 2025 – Apr 2026).

Technologies
Java, JavaFX, SQLite, JUnit 5, Figma

My contribution
Developed the Orders component: basket, order placement, order history, admin status updates and returns

Wrote 31 JUnit 5 tests and 20 manual test cases

Documented 20 requirements and wrote the final implementation report

Designed screens in Figma and produced the shared SQL schema

How to run
Requirements

Java JDK 21
JavaFX SDK 21
Maven
An IDE such as Eclipse (used during development)
DB Browser for SQLite (optional, for viewing the database)
Steps

Download or clone this repository and unzip Assessment B.zip if the code is still inside it.
Open the project in Eclipse (File > Import > Existing Maven Projects).
Add JavaFX to the project. In Run Configurations, add these VM arguments, using the path to your own JavaFX SDK: --module-path "/path/to/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml
Run the main class: [name of the class, for example the one in the com.noch.noch package].
[If the app needs a database file, say where it is, for example "the SQLite database is nochmarketplace.sql, which creates the tables"].
[Add test login details only if they are demo accounts that you made up, never real passwords].
Running the tests Right-click the test folder and choose Run As > JUnit Test. [Check this matches how your tests are laid out.]

Features
Customer portal

Browse the product catalogue
Build an order in a basket and place it
View order history and order details
Edit an order and receive an order confirmation
Submit a return request
Admin / staff portal

View an order dashboard
Update order status
Manage customer returns
Technical

JavaFX/FXML screens with Java controllers
Data stored in an SQLite database
31 JUnit 5 tests across five data-access classes, plus 20 manual test cases
