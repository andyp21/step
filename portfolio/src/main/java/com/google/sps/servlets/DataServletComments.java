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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


// Servlet which retrieves user String word entries, reverses them and returns the 2 most recent entries
@WebServlet("/comments")
public class DataServletComments extends HttpServlet {
  
  //   Sends array of previous recent entries to be fetched
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    //   sort entries by most recent
    Query query = new Query("Entries").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // transfer entity elements to array
    ArrayList<String> commentList = new ArrayList<String>();
    ArrayList<String> nameList = new ArrayList<String>();

    for (Entity entity : results.asIterable()) {
      String name = (String) entity.getProperty("name");
      String comment = (String) entity.getProperty("comment");

      nameList.add(name);
      commentList.add(comment);
    }

    String json = convertToJson(commentList,nameList);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

// Retrieves word entries and reverses them
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the input from the form.
    String name = getParameter(request, "name", "Anonymous");
    String text = getParameter(request, "comment", "");
    long timestamp = System.currentTimeMillis();
    UserService userService = UserServiceFactory.getUserService();
    String id = userService.getCurrentUser().getUserId();

    Entity users = new Entity("Entries",id);
    users.setProperty("id", id);
    users.setProperty("name", name);
    users.setProperty("comment", text);
    users.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(users);


    // Respond with the result.
    response.setContentType("text/html;");
    response.getWriter().println(text);

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

/**
   * Converts a  instance into a JSON string using manual String concatentation.
   */
  private String convertToJson(ArrayList<String> comments, ArrayList<String> names) {
    Gson gson = new Gson();
    String comJson = gson.toJson(comments);
    String nameJson = gson.toJson(names);

    String json = "{";
    json += "\"names\":";
    json += nameJson;
    json += ", ";
    json += "\"comments\":";
    json += comJson;
    json += "}";
    return json;
  }
}
