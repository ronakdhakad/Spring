# Smart Email Analyzer with Urgency Scoring

Traditional Java 17 web application built with **Spring MVC (non-Boot)**, **Hibernate 6**, **MySQL 8**, **JSP**, **Jakarta Mail (IMAP)**, and deployable on **Apache Tomcat 10**.

---

## 1) Tech Stack

- Java 17
- Spring Framework 6 (Core + MVC + ORM)
- Hibernate ORM 6+
- Jakarta Persistence API
- MySQL 8
- JSP + JSTL (Jakarta)
- Jakarta Mail (IMAP)
- Apache Tomcat 10
- Maven WAR project

---

## 2) Main Features

- User registration with:
  - name
  - email
  - application password (for app login)
  - Google App Password (for Gmail IMAP)
- Login / logout
- User-specific inbox
- Email detail page with cleaned content
- Filter by tag and sort by urgency score
- Rule management:
  - keyword to tag mapping
  - sender importance score
  - adjustable scoring weights
- Dashboard:
  - total emails today
  - total urgent emails
  - most common tag
- Automatic IMAP synchronization every 5 minutes for logged-in users

---

## 3) Project Structure

```text
smart-email-analyzer/
├── pom.xml
├── README.md
├── sql/
│   └── schema.sql
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── smartemailanalyzer/
        │           ├── config/
        │           │   ├── AppInitializer.java
        │           │   ├── RootConfig.java
        │           │   ├── SessionCleanupListener.java
        │           │   └── WebConfig.java
        │           ├── controller/
        │           │   ├── AuthController.java
        │           │   ├── DashboardController.java
        │           │   ├── HomeController.java
        │           │   ├── InboxController.java
        │           │   └── RuleController.java
        │           ├── dto/
        │           │   ├── DashboardStats.java
        │           │   ├── EmailAnalysisResult.java
        │           │   ├── KeywordRuleForm.java
        │           │   ├── LoginForm.java
        │           │   ├── RegistrationForm.java
        │           │   └── SenderRuleForm.java
        │           ├── entity/
        │           │   ├── EmailMessage.java
        │           │   ├── KeywordRule.java
        │           │   ├── ScoreWeight.java
        │           │   ├── SenderRule.java
        │           │   └── User.java
        │           ├── interceptor/
        │           │   └── AuthInterceptor.java
        │           ├── repository/
        │           │   ├── EmailRepository.java
        │           │   ├── KeywordRuleRepository.java
        │           │   ├── ScoreWeightRepository.java
        │           │   ├── SenderRuleRepository.java
        │           │   └── UserRepository.java
        │           ├── service/
        │           │   ├── DashboardService.java
        │           │   ├── EmailFetchService.java
        │           │   ├── EmailService.java
        │           │   ├── MailContentService.java
        │           │   ├── RuleEngineService.java
        │           │   ├── RuleService.java
        │           │   └── UserService.java
        │           └── util/
        │               └── PasswordUtil.java
        ├── resources/
        │   └── application.properties
        └── webapp/
            ├── resources/
            │   └── css/
            │       └── style.css
            └── WEB-INF/
                └── views/
                    ├── common/
                    │   ├── footer.jspf
                    │   └── header.jspf
                    ├── dashboard.jsp
                    ├── emailDetail.jsp
                    ├── filter.jsp
                    ├── inbox.jsp
                    ├── login.jsp
                    ├── register.jsp
                    └── rules.jsp
```

---

## 4) MySQL Setup

1. Start MySQL 8.
2. Create the schema by executing:

```sql
SOURCE /absolute/path/to/smart-email-analyzer/sql/schema.sql;
```

3. Update DB values in:

```text
src/main/resources/application.properties
```

Set:

```properties
db.url=jdbc:mysql://localhost:3306/smart_email_analyzer?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=your_password
```

---

## 5) Gmail / IMAP Setup

For each Gmail account used in the project:

1. Turn on IMAP in Gmail settings.
2. Generate a **Google App Password**.
3. Register in the application using:
   - your Gmail address
   - any app login password for this project
   - the generated Google App Password

> Note: The project stores the Gmail App Password in the database so the IMAP scheduler can log in again. For a production system, encrypt it before storage.

---

## 6) Build the WAR

Run from the project root:

```bash
mvn clean package
```

Generated artifact:

```text
target/smart-email-analyzer.war
```

---

## 7) Tomcat 10 Deployment

1. Install Apache Tomcat 10.
2. Stop Tomcat if it is already running.
3. Copy the generated WAR file to:

```text
<TOMCAT_HOME>/webapps/
```

4. Start Tomcat.
5. Open the application:

```text
http://localhost:8080/smart-email-analyzer/
```

---

## 8) How the Email Sync Works

- On successful login, the app immediately fetches new emails for that user.
- A scheduler also runs every 5 minutes.
- Only users marked as logged in are synchronized.
- IMAP UID values are stored so duplicate emails are not inserted.

---

## 9) Default Urgency Logic

Built-in keyword recognition:

- URGENT → urgent, asap, immediately, action required, deadline
- PAYMENT → payment, invoice, due, billing, receipt
- MEETING → meeting, interview, schedule, call, appointment
- PROMOTION → offer, sale, discount, promo, coupon

The total urgency score is calculated from:

- built-in tag weights
- user-defined keyword boosts
- sender importance rule boosts
- recency bonus
- subject priority bonus

All scores are capped to a range of **0 to 100**.

---

## 10) Notes

- This is intentionally built without Spring Boot.
- Authentication is session-based and intentionally lightweight.
- No Spring Security is used, as requested.
- JSP is used for all UI pages.
- The project is structured for a Java Full Stack trainee and is heavily commented for learning.
