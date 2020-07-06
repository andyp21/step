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
import java.util.ArrayList; 
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

// Servlet which retrieves user String word entries, reverses them and returns the 2 most recent entries
@WebServlet("/delete-data")
public class DataServletDelete extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.getWriter().println();
  }

// Retrieves word entries and reverses them
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    UserService userService = UserServiceFactory.getUserService();
    String id = userService.getCurrentUser().getUserId();

    // Get the input from the form.
    String name = getParameter(request, "delete", "");
        
    if (name.equals("Anonymous")){
        // response.setContentType("text/html");
        // response.getWriter().println("Cannot delete anonymous");
        return;
    }
    Filter namefilter = new FilterPredicate("name", FilterOperator.EQUAL, name);
    Filter useridFilter = new FilterPredicate("id", Query.FilterOperator.EQUAL, id);
    Query query = new Query("Entries").setFilter(namefilter).setFilter(useridFilter);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);


    for (Entity entity : results.asIterable()) {
        datastore.delete(entity.getKey());
    }
    
    // Respond with the result.
    response.setContentType("text/html;");
    response.getWriter().println("deleted All");

    // Redirect back to the HTML page.
    response.sendRedirect("/comments.html");
  }


  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null || value.length() == 0) {
      return defaultValue;
    }
    return value;
  }

}
