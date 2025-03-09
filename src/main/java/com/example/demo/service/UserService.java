package com.example.demo.service;

import com.example.demo.dto.ResponseUserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public boolean isIdNotValid(int id) {
        return id <= 0;
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public ResponseUserDto  getUserById(int id) {
        return userRepo.findById(id)
                .map(user -> new ResponseUserDto(user.getName(), user.getBalance(), user.getId()))
                .orElse(new ResponseUserDto("User not found", 0, id));
    }

    public boolean doesntExistsById(int id) {
        return !userRepo.existsById(id);
    }

    public void deleteById(int id) {
        userRepo.deleteById(id);
    }
}
