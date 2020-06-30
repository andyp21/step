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
import java.util.ArrayList; // import the ArrayList class


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<String> flavors = new ArrayList<String>();
    flavors.add("Cookies and Cream");  
    flavors.add("Vanilla");  
    flavors.add("Mint Chocolate Chip");  

    String json = convertToJson(flavors);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
    /**
   * Converts Array List instance into a JSON string using String concatentation.
   */
  private String convertToJson(ArrayList<String> flavors) {
    int count = 1;
    String json = "{";
    for (String i : flavors) {
        if (count!=1)
            json += ", ";
            
        json += "\"favorite"+count+"\": ";
        json += "\"" + i + "\"";
        count++;
    }
    json += "}";
    
    return json;
  }
}
