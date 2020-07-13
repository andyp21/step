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

let map;

/** Creates a map that allows users to add markers. */
function createMap() {
  map = new google.maps.Map(document.getElementById('map'),{center: {lat: 18, lng: -76.8923}, zoom: 10});

  fetchMarkers();
}

/** Fetches markers from the backend and adds them to the map. */
function fetchMarkers() {
  fetch('/markers').then(response => response.json()).then((markers) => {
    markers.forEach(
        (marker) => {
            const pic = document.createElement('img');
            pic.src =marker.content;
            pic.style.cssText="width:400px;height:400px;"
            createMarkerForDisplay(marker.lat, marker.lng, pic);
    });
    
  });
}

/** Creates a marker that shows a read-only info window when clicked. */
function createMarkerForDisplay(lat, lng, content) {
  const marker = new google.maps.Marker({position: {lat: lat, lng: lng}, map: map});
  const infoWindow = new google.maps.InfoWindow({content: content});
  marker.addListener('click', () => {
        infoWindow.close(map);
        infoWindow.open(map, marker);
  });
  marker.setIcon('images/prof.png');
}
