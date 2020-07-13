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
  
//   Fetches the previous scores on the quiz and prints them as Json string
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String scoresJson = retrieveScores();
    response.setContentType("application/json");
    response.getWriter().println(scoresJson);
  }


// Retrieves quiz question responses, calculates the score, and stores it in the datastore service
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
    
    // mark quiz results and store them for the user
    int score = handleResults(birthday,midName,sport,series,movies,song,career,school,food,siblings);
    storeScores(name, score);
    
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

  //   Retrieves the scores from the datastore service
  public String retrieveScores(){
    // get all scores from database
    Query query = new Query("Scores");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
        
    // creates a hashmap to store friend score information and convert to json
    Map<String, Long> scores = new HashMap<>();
    for (Entity entity : results.asIterable()) {
        scores.put(((String) entity.getProperty("name")), (long)entity.getProperty("score"));
    }
    Gson gson = new Gson();
    return gson.toJson(scores);
  }

  // Stores the user's score and name in the datastore service
  public void storeScores(String name, int score){
    Entity userScore = new Entity("Scores");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    userScore.setProperty("score", score);
    userScore.setProperty("name", name);
    datastore.put(userScore);
  }

  // Grades the user's performance on the quiz and returns the score
  public int handleResults(String[] a, String[] b, String[] c,String[] d, String[] e,String[] f,String[] g,String[] h,String[] i,String[] j){
    int score=0;
	if (a[0].equals("December 8th")){
        score++;
    }
    if (b[0].equals("Micah")){
        score++;
    }
    if (c[0].equals("Soccer")){
        score++;
    } 
    if (d[0].equals("The Last Kingdom")){
        score++;
    }   
    if (e[0].equals("1917")){
        score++;
    } 
    if (f[0].equals("Ville Mentality")){
        score++;
    }
    if (g[0].equals("Actor")){
        score++;
    }
    if (h[0].equals("Campion")){
        score++;
    } 
    if (i[0].equals("Jerk Chicken")){
        score++;
    } 
    if (j[0].equals("3")){
        score++;
    }
    return score; 
  }

}