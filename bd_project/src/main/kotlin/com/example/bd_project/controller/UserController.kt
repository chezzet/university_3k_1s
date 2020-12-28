package com.example.bd_project.controller

import com.example.bd_project.model.User
import com.example.bd_project.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val repository: UserRepository
) {
    @PostMapping
    fun saveUser(@RequestBody user: User) : ResponseEntity<User> {
        return ResponseEntity.ok(
            repository.findByName(user.name).orElseGet {
                repository.save(user)
            }
        )
    }

    @GetMapping
    fun getAllUserInfo() : ResponseEntity<List<User>> {
        return ResponseEntity.ok(repository.findAllByRoleEquals())
    }
}