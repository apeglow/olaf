package de.mobile.olaf.webapp.support.soy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.servlet.view.RedirectView;

import de.mobile.olaf.api.IpUsedEventType;
import de.mobile.olaf.client.Client;

public class ContactController extends AbstractFormController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Client olafClient;
	
	public ContactController(Client olafClient) {
		this.olafClient = olafClient;
	}

	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		String ip = request.getRemoteAddr();
		if (ip != null) {
			olafClient.sendMessage(ip, IpUsedEventType.CONTACT);
		}
		logger.info("Sent ip {} to olaf.", ip);
		return new ModelAndView(new RedirectView("contact.html"));
	}

	@Override
	protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors) throws Exception {
		ModelAndView mav = new ModelAndView("contact:olaf.contact.show");
		
		return mav;
	}

}
