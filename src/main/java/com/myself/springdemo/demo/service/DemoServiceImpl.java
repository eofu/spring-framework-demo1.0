package com.myself.springdemo.demo.service;

import com.myself.springdemo.annotation.Service;

@Service
public class DemoServiceImpl implements DemoService {
	@Override
	public String getName(String name) {
		return "my name is" + name;
	}
}
