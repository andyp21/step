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
import java.util.Arrays;
import java.util.ArrayList; 
import java.io.PrintWriter;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Servlet which logs the user into their google account
@WebServlet("/correct")
public class ServletRanking extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // get all scores from database
    Query query = new Query("Scores");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
        
    // creates a hashmap to store friend score information and convert to json
    Map<String, Long> friendScores = new HashMap<>();
    for (Entity entity : results.asIterable()) {
        friendScores.put(((String) entity.getProperty("name")), (long)entity.getProperty("score"));
    }

    response.setContentType("application/json");
    Gson gson = new Gson();

    String json = gson.toJson(friendScores);
    response.getWriter().println(json);
  }


// Retrieves word entries and reverses them
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    String[] defaultValue = new String[] {""};
    // Get the input from the form.
    String name = getParameter(request, "name", "Anon");
    String[] birthday = getParameterValues(request, "bday", defaultValue);
    String[] midName = getParameterValues(request,"midName",defaultValue);
    String[] sport = getParameterValues(request,"sport",defaultValue);
    String[] series = getParameterValues(request,"series",defaultValue);
    String[] movies = getParameterValues(request,"movies",defaultValue);
    String[] song = getParameterValues(request,"song",defaultValue);
    String[] career = getParameterValues(request,"career",defaultValue);
    String[] school = getParameterValues(request,"school",defaultValue);
    String[] food = getParameterValues(request,"food",defaultValue);
    String[] siblings = getParameterValues(request,"siblings",defaultValue);
    int score =0; 

    Entity userScore = new Entity("Scores");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	if (birthday[0].equals("December 8th")){
        score++;
    }
    if (midName[0].equals("Micah")){
        score++;
    }
    if (sport[0].equals("Soccer")){
        score++;
    } 
    if (series[0].equals("The Last Kingdom")){
        score++;
    }   
    if (movies[0].equals("1917")){
        score++;
    } 
    if (song[0].equals("Ville Mentality")){
        score++;
    }
    if (career[0].equals("Actor")){
        score++;
    }
    if (school[0].equals("Campion")){
        score++;
    } 
    if (food[0].equals("Jerk Chicken")){
        score++;
    } 
    if (siblings[0].equals("3")){
        score++;
    } 
    userScore.setProperty("score", score);
    userScore.setProperty("name", name);
    datastore.put(userScore);
    response.sendRedirect("/quiz.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String[] getParameterValues(HttpServletRequest request, String field, String[] defaultValue) {
    String[] value = request.getParameterValues(field);
    if (value == null) {
      return defaultValue;
    }
    return value;
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