package com.spring.dao;

import com.spring.model.User;

public interface UserDao {
	void addUser(User user);
	void deleteUser(int id);
	void updateUser(User user);
	User getUser(int id);
}
