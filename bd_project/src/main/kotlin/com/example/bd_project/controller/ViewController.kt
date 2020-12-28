package com.example.bd_project.controller

import com.example.bd_project.dto.RoomDTO
import com.example.bd_project.model.Room
import com.example.bd_project.repository.RoomRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import java.util.*

@Controller
class ViewController(
    private val roomRepository: RoomRepository
){
    @GetMapping("index")
    fun view(model: Model) : String {
        model.addAttribute("all", roomRepository.findAllByDeletedIsFalse())
        model.addAttribute("roomDTO", RoomDTO())
        return "index"
    }

    @PostMapping("view")
    fun process(
        @ModelAttribute("roomDTO") roomDTO: RoomDTO,
        bindingResult: BindingResult, model: Model
    ) : String {
        roomRepository.save(
            Room(
                UUID.randomUUID(),
                roomDTO.name,
                roomDTO.description,
                roomDTO.capacity.toInt(),
                Date()
            )
        )
        return "index"
    }
}