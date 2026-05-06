DATABASE STARTUP FIX
====================
Your app startup is failing because MySQL login is rejected.

Current console error:
Access denied for user 'root'@'localhost' (using password: YES)

Edit this file before running:
  src/main/resources/application.properties

Change these values to your real MySQL credentials:
  db.username=your_mysql_username
  db.password=your_mysql_password

Also create the database by running:
  sql/schema.sql

After changing properties:
1. Maven -> Update Project
2. Project -> Clean
3. Clean Tomcat server
4. Restart Tomcat
