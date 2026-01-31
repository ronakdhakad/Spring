package com.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.spring.config.AppConfig;
import com.spring.dao.UserDao;
import com.spring.model.User;

public class Main {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		
		UserDao userDao = context.getBean(UserDao.class);
		
		userDao.addUser(new User(111,"Andrew Anderson","andrew@gmail.com","Indore"));
		userDao.addUser(new User(112,"Peter Parker","peter@gmail.com","Dewas"));
		userDao.addUser(new User(113,"Jackson Jack","jackson@gmail.com","Ujjain"));
		userDao.addUser(new User(114,"Mathew Math","mathew@gmail.com","Nagda"));
		userDao.addUser(new User(115,"Simon Sim","simon@gmail.com","Jabalpur"));
		System.out.println("\nData inserted successfully");
		
		userDao.updateUser(new User(114,"Peter Parker updated","peter123@gmail.com","Dewas MP"));
		System.out.println("\nData updated successfully");
		
		userDao.deleteUser(110);
		System.out.println("\nData deleted successfully");
		
		User user = userDao.getUser(115);
		System.out.println("\nUser : "+user);
		System.out.println("\nData retrived successfully");

	}

}
