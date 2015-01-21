package se.leafcoders.rosette.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import se.leafcoders.rosette.exception.NotFoundException;

@Controller
public class CatchAllController extends AbstractController {
	
	public CatchAllController() {
		super();
	}

	@RequestMapping(produces = "application/json;charset=UTF-8")
	public void catchAll() {
		throw new NotFoundException();
	}
}
