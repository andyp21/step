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



// Servlet which retrieves user String word entries, reverses them and returns the 2 most recent entries
@WebServlet("/rev")
public class DataServletReverse extends HttpServlet {
  
//   Sends array of previous recent entries to be fetched
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    //   sort entries by most recent
    Query query = new Query("Entries").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // transfer entity elements to array
    ArrayList<String> entries = new ArrayList<String>();
    for (Entity entity : results.asIterable()) {
      String word = (String) entity.getProperty("word");
      entries.add(word);
    }

    Gson gson = new Gson();
    String json = gson.toJson(entries);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

// Retrieves word entries and reverses them
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the input from the form.
    String text = getParameter(request, "text-input", "");
    long timestamp = System.currentTimeMillis();

    Entity taskEntity = new Entity("Entries");
    taskEntity.setProperty("word", text);
    taskEntity.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    // Reverses String text
    StringBuilder textReverse = new StringBuilder(text); 
    textReverse = textReverse.reverse(); 

    // Respond with the result.
    response.setContentType("text/html;");
    response.getWriter().println(textReverse);
  }


  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
