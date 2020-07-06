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


// Retrieves list of 2 most recent entries
function getComment(num) {
    fetch('/comments')  // sends a request to /data
    .then(response => response.json()) // parses the response as JSON
    .then((review) => { // now we can reference the fields
    const commentListElement = document.getElementById('reviews');
    commentListElement.innerHTML = '';

    var i;
    var numComments =Math.min(review.comments.length,num);
    
    for (i =0; i<numComments; i++){
            commentListElement.appendChild(createCommentElement(review.comments[i]))
            commentListElement.appendChild(createNameElement('—\ ' + review.names[i]))
    }
    });
}
/** Creates an <h6> element for the name of the commentor. */
function createNameElement(text) {
  const divElement = document.createElement('h6');
  divElement.innerText = text;
  divElement.className = "name";
  return divElement;
}

/** Creates an <h5> element for the comment. */
function createCommentElement(text) {
  const divElement = document.createElement('p');
  divElement.innerText = text;
  divElement.className = "comment";
  return divElement;
}
