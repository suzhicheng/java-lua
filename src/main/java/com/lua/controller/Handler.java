package com.lua.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lua.platform.LuaPlatform;


@Controller
public class Handler {
	 	@RequestMapping(value="/",method=RequestMethod.POST)
	    @ResponseBody
	    String crypt(@RequestBody String req) throws UnsupportedEncodingException {
		 
			System.out.println(req);
			LuaPlatform platform = new LuaPlatform();
			String rep = platform.execute(req);
			System.out.println(req);
			return rep;
		 
	    }
}
