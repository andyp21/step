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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Marker;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Handles fetching and saving markers data. */
@WebServlet("/markers")
public class MarkerServlet extends HttpServlet {

  /** Responds with a JSON array containing marker data. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    // Manually add list of markers for display
    Collection<Marker> markers = new ArrayList<>();
    Marker marker = new Marker(18.004360804462404, -76.78550655093456, "images/jamaicanflag.jpg");
    markers.add(marker);
    marker = new Marker(17.984360804462404, -76.78550655093456, "images/andrew-5.jpg");
    markers.add(marker);
    marker = new Marker(18.005436080446240, -76.88550655093456, "images/andrew-6.jpg");
    markers.add(marker);
    marker = new Marker(17.994360804462404, -76.68550655093456, "images/andrew-8.jpg");
    markers.add(marker);
    marker = new Marker(18.104360804462404, -76.58550655093456, "images/andrew-7.jpg");
    markers.add(marker);
    marker = new Marker(40.349614967214706, -74.65486065729337, "images/andrew-11.jpg");
    markers.add(marker);
    marker = new Marker(40.949614967214706, -73.85486065729337, "images/andrew-1.jpg");
    markers.add(marker);
    marker = new Marker(39.849614967214706, -75.05486065729337, "images/andrew-2.jpg");
    markers.add(marker);

    Gson gson = new Gson();
    String json = gson.toJson(markers);

    response.getWriter().println(json);
  }

}
