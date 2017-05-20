package com.yourl.controller;

import com.yourl.controller.dto.ShortenUrlRequest;
import com.yourl.service.urlstore.IUrlStoreService;
import com.yourl.service.userstore.IUserStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
import java.io.IOException;
import java.net.*;


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
            bindingResult.addError(new FieldError("url","url", "Invalid url: " + url));
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

            if (urlStoreService != null) {
                final String urlId = urlStoreService.storeURL(url);
                String requestUrl = httpRequest.getRequestURL().toString();

                modelAndView.addObject("shortenedUrl", requestUrl + urlId);
            }

        }
        return modelAndView;
    }

    private boolean isUrlValid(String url) {
        boolean valid = true;
        try {
//            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.1", 8080));
            final URLConnection connection = new URL(url).openConnection();
            connection.connect();
        } catch (IOException e) {
            valid = false;
        }
        return valid;
    }
}
