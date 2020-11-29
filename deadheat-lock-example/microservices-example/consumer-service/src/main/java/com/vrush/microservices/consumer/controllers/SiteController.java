package com.vrush.microservices.consumer.controllers;

import com.vrush.microservices.consumer.service.RoomService;
import com.vrush.microservices.consumer.utils.Template;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SiteController {

	private final RoomService roomService;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("rooms", roomService.findAll());
		return Template.HOME;
	}

}