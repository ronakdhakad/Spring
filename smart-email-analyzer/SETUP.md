# Smart Email Analyzer — Setup Guide

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Java | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| MySQL | 8.0+ | Running locally |
| Apache Tomcat | 10.x | Download from tomcat.apache.org |
| Gmail Account | Any | With IMAP + App Password enabled |

---

## Step 1 — MySQL Database Setup

```sql
-- Connect to MySQL as root
mysql -u root -p

-- Run the schema file
source /path/to/smart-email-analyzer/sql/schema.sql;

-- Verify
USE email_analyzer;
SHOW TABLES;
SELECT * FROM users;
```

---

## Step 2 — Gmail IMAP Configuration

> **Required for email fetching to work.**

1. Go to **Gmail → Settings → See all settings → Forwarding and POP/IMAP**
2. Enable **IMAP access**
3. Go to **Google Account → Security → 2-Step Verification** (must be ON)
4. Go to **Google Account → Security → App Passwords**
5. Create a new App Password for "Mail" on "Windows Computer"
6. Copy the 16-character app password (e.g. `abcd efgh ijkl mnop`)

Update the demo user's IMAP password in MySQL:
```sql
UPDATE users
SET imap_password = 'your16charapppassword'
WHERE email = 'demo@gmail.com';
```

Or update `sql/schema.sql` before running it.

---

## Step 3 — Configure Database Connection

Edit `src/main/webapp/WEB-INF/applicationContext.xml`:

```xml
<property name="url"
    value="jdbc:mysql://localhost:3306/email_analyzer?useSSL=false&serverTimezone=UTC"/>
<property name="username" value="root"/>       <!-- your MySQL username -->
<property name="password" value="root"/>       <!-- your MySQL password -->
```

---

## Step 4 — Build the Project

```bash
cd smart-email-analyzer
mvn clean package -DskipTests
```

This produces: `target/smart-email-analyzer.war`

---

## Step 5 — Deploy to Apache Tomcat 10

### Option A — Copy WAR manually
```bash
cp target/smart-email-analyzer.war $TOMCAT_HOME/webapps/
```

### Option B — Using Tomcat Manager
1. Open `http://localhost:8080/manager/html`
2. Under "Deploy" → "WAR file to deploy", upload `smart-email-analyzer.war`
3. Click **Deploy**

### Start Tomcat
```bash
$TOMCAT_HOME/bin/startup.sh      # Linux/Mac
$TOMCAT_HOME\bin\startup.bat     # Windows
```

---

## Step 6 — Access the Application

Open your browser:
```
http://localhost:8080/smart-email-analyzer/
```

### Demo Login Credentials
| Field | Value |
|-------|-------|
| Email | `demo@gmail.com` |
| Password | `demo123` |

---

## Application Pages

| URL | Page |
|-----|------|
| `/login` | Login page |
| `/inbox` | Email inbox |
| `/email/{id}` | Email detail + reply |
| `/filter` | Filter/sort/search |
| `/dashboard` | Analytics dashboard |
| `/rules` | Rule management |
| `/logout` | Logout |

---

## Project Structure

```
smart-email-analyzer/
├── pom.xml
├── sql/
│   └── schema.sql
├── src/
│   └── main/
│       ├── java/com/emailanalyzer/
│       │   ├── controller/
│       │   │   ├── LoginController.java
│       │   │   ├── InboxController.java
│       │   │   ├── EmailDetailController.java
│       │   │   ├── FilterController.java
│       │   │   ├── RulesController.java
│       │   │   └── DashboardController.java
│       │   ├── service/
│       │   │   ├── UserService.java / impl/UserServiceImpl.java
│       │   │   ├── EmailService.java / impl/EmailServiceImpl.java
│       │   │   └── RuleService.java  / impl/RuleServiceImpl.java
│       │   ├── dao/
│       │   │   ├── UserDAO.java      / impl/UserDAOImpl.java
│       │   │   ├── EmailDAO.java     / impl/EmailDAOImpl.java
│       │   │   ├── TagDAO.java       / impl/TagDAOImpl.java
│       │   │   ├── RuleDAO.java      / impl/RuleDAOImpl.java
│       │   │   └── UrgencyScoreDAO.java / impl/UrgencyScoreDAOImpl.java
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   ├── Email.java
│       │   │   ├── Tag.java
│       │   │   ├── Rule.java
│       │   │   └── UrgencyScore.java
│       │   ├── util/
│       │   │   ├── RuleEngine.java
│       │   │   ├── ImapEmailFetcher.java
│       │   │   └── SessionHelper.java
│       │   └── scheduler/
│       │       └── EmailFetchScheduler.java
│       ├── resources/
│       └── webapp/
│           ├── index.jsp
│           ├── resources/css/style.css
│           └── WEB-INF/
│               ├── web.xml
│               ├── applicationContext.xml
│               ├── dispatcher-servlet.xml
│               └── views/
│                   ├── nav.jsp
│                   ├── login.jsp
│                   ├── inbox.jsp
│                   ├── emailDetail.jsp
│                   ├── filter.jsp
│                   ├── rules.jsp
│                   ├── dashboard.jsp
│                   └── error.jsp
```

---

## System Flow

```
Gmail IMAP
    │
    ▼
EmailFetchScheduler  (every 5 minutes via @Scheduled)
    │
    ▼
ImapEmailFetcher     (IMAP SSL → fetch 50 newest messages)
    │
    ▼
Deduplication        (check message_id in emails table)
    │
    ▼
RuleEngine           (apply keyword + sender rules from DB)
    │   └── compute urgency score
    │   └── assign tags
    ▼
MySQL (via Hibernate 6)
    │   emails, email_tags, urgency_scores tables
    ▼
Spring MVC Controllers
    │
    ▼
JSP Views → Browser
```

---

## How the Urgency Scoring Works

The Rule Engine processes each email against all active rules:

| Rule Type | Condition | Score |
|-----------|-----------|-------|
| KEYWORD | "urgent" in subject/body | +30 |
| KEYWORD | "payment" in subject/body | +25 |
| KEYWORD | "meeting" in subject/body | +15 |
| SENDER | boss@company.com | +20 |
| KEYWORD | "unsubscribe" | -5 (spam signal) |

Final score is clamped to **0 – 100**.

Score interpretation:
- **70–100** → 🔴 HIGH urgency
- **40–69**  → 🟡 MEDIUM urgency
- **0–39**   → ⚫ LOW urgency

Users can **manually override** any score on the Email Detail page.

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| Login fails | Check `users` table has correct email/password |
| No emails fetched | Verify IMAP App Password in DB; check Tomcat logs |
| `ClassNotFoundException` for driver | Ensure `mysql-connector-j` is in WAR |
| `HibernateException` | Check DB credentials in `applicationContext.xml` |
| IMAP `AuthenticationFailedException` | Re-generate Gmail App Password |
| JSP 404 | Confirm Tomcat 10 is used (Jakarta EE, not javax) |
| `NoSuchBeanDefinitionException` | Ensure component-scan covers all packages |

### Enable detailed logging
Add to Tomcat's `conf/logging.properties`:
```
com.emailanalyzer.level = FINE
```

Or configure Logback in `src/main/resources/logback.xml`.

---

## Notes on Production Hardening

> This project is a **demonstration** build. For production:

1. **Hash passwords** using BCrypt (`spring-security-crypto` dependency only)
2. **Use environment variables** for DB credentials instead of hard-coding in XML
3. **Enable HTTPS** on Tomcat with a valid SSL certificate
4. **Rate-limit** the login endpoint
5. **Validate and sanitize** all user inputs
6. **Store IMAP passwords encrypted** (e.g. AES-256)
