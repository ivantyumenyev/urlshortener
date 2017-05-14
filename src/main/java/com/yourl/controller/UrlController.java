package com.yourl.controller;

import com.yourl.controller.dto.ShortenUrlRequest;
import com.yourl.service.urlstore.IUrlStoreService;
import com.yourl.service.userstore.IUserStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URL;


@Controller
public class UrlController {

    @Autowired
    private IUserStoreService userStoreService;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String showForm( ShortenUrlRequest request) {
        return "shortener";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void redirectToUrl(
            @PathVariable String id,
            @CookieValue(value = "userID", required = false, defaultValue = "") String userID,
            HttpServletResponse response) throws Exception {

        if (!userID.isEmpty()){
            IUrlStoreService urlStoreService = userStoreService.findUrlStoreServiceByUser(userID);

            if (urlStoreService != null) {
                final String url = urlStoreService.findUrlById(id);

                if (url != null) {
                    response.sendRedirect(url);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public ModelAndView shortenUrl(HttpServletRequest httpRequest,
                                   HttpServletResponse httpResponse,
                                   @Valid ShortenUrlRequest request,
                                   BindingResult bindingResult,
                                   @CookieValue(value = "userID", required = false, defaultValue = "") String userID
                                   ) {
        String url = request.getUrl();
        if (!isUrlValid(url)) {
            bindingResult.addError(new ObjectError("url", "Invalid url format: " + url));
        }

        ModelAndView modelAndView = new ModelAndView("shortener");
        if (!bindingResult.hasErrors()) {

            if (userID.isEmpty() || userStoreService.findUrlStoreServiceByUser(userID) == null) {
                userID = userStoreService.createUser();
                httpResponse.addCookie(new Cookie("userID", userID));
                System.out.println("New user with ID " + userID);

            } else {
                System.out.println("Old user with ID " + userID);
            }

            IUrlStoreService urlStoreService = userStoreService.findUrlStoreServiceByUser(userID);

            final String urlID = urlStoreService.storeURL(url);

            String requestUrl = httpRequest.getRequestURL().toString();
            String prefix = requestUrl.substring(0, requestUrl.indexOf(httpRequest.getRequestURI(),
                "http://".length()));

            modelAndView.addObject("shortenedUrl", prefix + "/" + urlID);
        }
        return modelAndView;
    }

    private boolean isUrlValid(String url) {
        boolean valid = true;
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            valid = false;
        }
        return valid;
    }

}
