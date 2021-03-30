package com.myself.springdemo.demo.controller;

import com.myself.springdemo.annotation.AutoWired;
import com.myself.springdemo.annotation.Controller;
import com.myself.springdemo.annotation.RequestMapping;
import com.myself.springdemo.annotation.RequestParam;
import com.myself.springdemo.demo.service.DemoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Controller
@RequestMapping("/demo")
public class DemoController {

	@AutoWired
	private DemoService demoService;

	@RequestMapping("/query.json")
	public void query(HttpServletResponse response, HttpServletRequest request, @RequestParam("name") String name) {
		String result = demoService.getName(name);

		try {
			response.getWriter().write(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("edit.json")
	public void edit(HttpServletResponse response, HttpServletRequest request, Integer id) {

	}
}
