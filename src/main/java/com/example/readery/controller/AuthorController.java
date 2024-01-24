package com.example.readery.controller;

import com.example.readery.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AuthorController {
    @Autowired
    AuthorRepository authorRepository;
}
