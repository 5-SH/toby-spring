package com.spring.toby;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class TobyController {
	
	private static final Logger logger = LoggerFactory.getLogger(TobyController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		try {
			User user = new User();
			user.setId("whiteship");
			user.setName("백기선");
			user.setPassword("married");

			UserDao dao = new UserDao();
			dao.add(user);

			logger.info("{} 등록 성공", user.getId());

			User user2 = dao.get(user.getId());
			logger.info("{}", user2.getName());
			logger.info("{}", user2.getPassword());
			logger.info("{} 조회 성공", user2.getId());

			model.addAttribute("user", user2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "home";
	}
	
}