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

// Servlet which retrieves client String comment entries, stores them on server, and displays them on wall 
@WebServlet("/comments")
public class DataServletComments extends HttpServlet {
  
  //   Sends array of previous recent entries to be fetched
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comments = retrieveComments();
    response.setContentType("application/json;");
    response.getWriter().println(comments);
  }

  // Retrieves comment entry and stores it in the datastore service
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the input from the form.
    String name = getParameter(request, "name", "Anonymous");
    String comment = getParameter(request, "comment", "");
    storeComment(name,comment);

    // Respond with the result.
    response.setContentType("text/html;");
    response.getWriter().println(comment);

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
    
  //  Retrieves the user's comments from the datastore service and returns it as a Json String of their name and the comment
  public String retrieveComments(){
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
    return convertToJson(commentList,nameList);
  }

  //   Stores the user's comments in the datastore service
  public void storeComment(String name, String comment){

    long timestamp = System.currentTimeMillis();
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String id = userService.getCurrentUser().getUserId();

    Entity userComments = new Entity("Entries",id);
    userComments.setProperty("id", id);
    userComments.setProperty("name", name);
    userComments.setProperty("comment", comment);
    userComments.setProperty("timestamp", timestamp);
    datastore.put(userComments);
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
