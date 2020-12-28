package com.example.bd_project.repository

import com.example.bd_project.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByName(name: String): Optional<User>

    fun findAllByRoleEquals(role: Int = 0) : List<User>
}