// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

// Servlet which logs the user out of their google account
@WebServlet("/logout")
public class DataServletLogout extends HttpServlet {
  
//   Fetch logout link is user is logged in
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()){
        // Redirect back to the HTML page.
        response.sendRedirect("/index.html");
    }
    else{
        // Return logout link
        String logoutUrl = userService.createLogoutURL("/logout");
        out.println(logoutUrl);
    }
  }

}
