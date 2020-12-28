package com.example.bd_project.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "usr", schema = "public")
data class User(
    @Id val id: UUID,
    @Column(name="name", unique = true) val name: String,
    val role: Int,
    val password: String
)
