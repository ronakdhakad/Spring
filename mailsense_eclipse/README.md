# MailSense — Smart Inbox Intelligence

Multi-user email intelligence system built with **pure Spring MVC (no Spring Boot)**.

## Tech Stack
| Layer | Tech |
|-------|------|
| Framework | Spring MVC 5.3.x |
| Security | Spring Security 5.8.x |
| ORM | Hibernate 5.6.x + HikariCP |
| DB | MySQL 8.0 |
| Frontend | JSP + JSTL + Bootstrap 5 |
| Email | JavaMail / Jakarta Mail |
| Build | Maven → WAR |
| Server | Apache Tomcat 10.x |
| Java | Java 17 |

---

## How to Import in Eclipse

### Step 1 — Import as Maven Project
1. Open Eclipse → **File → Import**
2. Select **Maven → Existing Maven Projects**
3. Browse to the `mailsense` folder
4. Click **Finish**
5. Eclipse will download all dependencies automatically

### Step 2 — Add Tomcat Server in Eclipse
1. **Window → Preferences → Server → Runtime Environments**
2. Click **Add** → Select **Apache Tomcat v10.x**
3. Browse to your Tomcat installation folder
4. Click **Finish**

### Step 3 — Add Project to Tomcat
1. **Window → Show View → Servers**
2. Right-click the Tomcat server → **Add and Remove**
3. Move `mailsense` to the right panel → **Finish**

---

## Database Setup

```sql
-- Run as MySQL root:
CREATE DATABASE mailsense_db CHARACTER SET utf8mb4;
CREATE USER 'mailsense_user'@'localhost' IDENTIFIED BY 'YourPassword123';
GRANT ALL PRIVILEGES ON mailsense_db.* TO 'mailsense_user'@'localhost';
FLUSH PRIVILEGES;
```

Then run the schema:
```bash
mysql -u mailsense_user -p mailsense_db < src/main/resources/schema.sql
```

---

## Configure application.properties

Edit `src/main/resources/application.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/mailsense_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=mailsense_user
db.password=YourPassword123

# Must be exactly 32 characters
aes.secret.key=MailSense@SecretKey#2024#Change!

# 'update' for dev, 'validate' for prod
hibernate.hbm2ddl.auto=update
hibernate.show_sql=true
```

---

## Gmail App Password Setup

1. Go to [myaccount.google.com/security](https://myaccount.google.com/security)
2. Enable **2-Step Verification** first
3. Search for **App passwords** → Create one for "Mail"
4. Copy the 16-character code (remove spaces)
5. Use it on the registration page

---

## Run the Project

In Eclipse:
1. Right-click `mailsense` project → **Run As → Run on Server**
2. Select your Tomcat server → **Finish**
3. Open: `http://localhost:8080/mailsense`

---

## URL Map

| URL | Description |
|-----|-------------|
| `/auth/register` | Create account |
| `/auth/login` | Login |
| `/inbox` | Email inbox |
| `/emails/{id}` | Email detail |
| `/rules` | Manage tag rules |
| `/senders` | Whitelist/blacklist |
| `/analytics` | Stats dashboard |

---

## Default Admin Login
- Email: `admin@mailsense.com`
- Password: `Admin@1234`

---

## Project Structure
```
src/main/java/com/mailsense/
  controller/     → Spring MVC controllers
  service/        → Business logic
  service/impl/   → Implementations
  dao/            → DAO interfaces
  dao/impl/       → Hibernate implementations
  entity/         → JPA entities (11 tables)
  dto/            → Data Transfer Objects
  security/       → Spring Security handlers
  scheduler/      → @Scheduled jobs
  util/           → AES encryption, email parser

src/main/webapp/WEB-INF/
  web.xml                  → Servlet config
  applicationContext.xml   → DataSource, Hibernate, Scheduler
  dispatcher-servlet.xml   → Spring MVC config
  security-context.xml     → Spring Security config
  views/                   → JSP pages

src/main/resources/
  application.properties   → DB, AES key, scheduler config
  schema.sql               → MySQL DDL + seed data
  logback.xml              → Logging config
```

---

## Security Features
- BCrypt password hashing (strength 12)
- AES-256 CBC encrypted Gmail App Passwords
- CSRF protection on all forms
- Session fixation protection
- Account locking after 5 failed attempts
- Ownership checks on all DB queries
- Audit logging for all sensitive actions
