class News {
  constructor(title, content, author){
    this.title = title;
    this.content = content;
    this.author = author;
  }
}

//reference to news
var newsRef = firebase.database().ref("news").orderByChild("date");


//listen for changes in the children
newsRef.on("child_added", function(){
  getNews();
});
newsRef.on("child_changed", function(){
  getNews();
});
newsRef.on("child_removed", function(){
  getNews();
});

//empty array to hold all the news
var newsArray = [];

//read all the news from database
function getNews(){
  newsRef.once("value", function(snapshot){
    var snapShotArr = [];
    snapshot.forEach(function(childSnapshot){
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
      snapShotArr.push(new News(childKey, childData.content, childData.author));
    });
    newsArray = snapShotArr;
    displayNews();
  });
}

//display all the news in a table
function displayNews(){
  var table = document.getElementById("newsTable");
  table.innerHTML = "";
  newsArray.forEach(function(n){
    var row = table.insertRow(0);
    var title = row.insertCell(0);

    var row2 = table.insertRow(1);
    var content = row2.insertCell(0);

    var row3 = table.insertRow(2);
    var author = row3.insertCell(0);

    var row4 = table.insertRow(3);
    var br = row4.insertCell(0);

    title.innerHTML = "Title: " + n.title;
    content.innerHTML = "Content: " + n.content;
    author.innerHTML = "Author: " + n.author;
    br.innerHTML = "--------";
  });
}

//clear all news
function clearNews(){
  newsRef.remove();
}

//remove oldest news
function removeOldestNews(){
  var index = 0;
  var node = newsArray[index].title;
  firebase.database().ref("news").child(node).remove();
  newsArray.splice(index, 1);
}

//remove latest news
function removeLatestNews(){
  var index = newsArray.length-1;
  var node = newsArray[index].title;
  firebase.database().ref("news").child(node).remove();
  newsArray.splice(index, 1);
}




//post a new article
function createNews(){
  var title = document.getElementById("title").value;
  var content = document.getElementById("content").value;
  var author = document.getElementById("author").value;
  newsArray.push(new News(title, content, author));
  var title = document.getElementById("title").value = "";
  var content = document.getElementById("content").value = "";
  var author = document.getElementById("author").value = "";
  writeNews();
}

//write the news into the database
function writeNews(){
  var updates = {};
  var timestamp = new Date().getTime()
  var n = newsArray[newsArray.length-1];
  var data = {
    content: n.content,
    author: n.author,
    date: timestamp
  }
  updates[n.title] = data;
  
  firebase.database().ref("news").update(updates);
}

function checkAdmin(user){
  var adminRef = firebase.database().ref("admin");
  adminRef.once("value", function(snapshot){
    snapshot.forEach(function(users){
      if(user.uid == users.key){
        displayTools();
      }
    });
  });
}

function displayTools(){

  var container = document.getElementById("toolsContainer");

  var pTitle = document.createElement("P");
  pTitle.textContent = "Otsikko:";

  var pContent = document.createElement("P");
  pContent.textContent = "Sisältö:";

  var pAuthor = document.createElement("P");
  pAuthor.textContent = "Tekijä: (valinnainen)";

  var iTitle = document.createElement("INPUT");
  iTitle.className = "inputField";
  iTitle.id = "title";
  iTitle.type = "text";
  iTitle.placeholder = "title";

  var iContent = document.createElement("INPUT");
  iContent.className = "inputField";
  iContent.id = "content";
  iContent.type = "text";
  iContent.placeholder = "content";

  var iAuthor = document.createElement("INPUT");
  iAuthor.className = "inputField";
  iAuthor.id = "author";
  iAuthor.type = "text";
  iAuthor.placeholder = "author";

  var createNewsButton = document.createElement("BUTTON");
  createNewsButton.textContent = "Luo uutinen";
  createNewsButton.addEventListener("click", createNews);

  var clearNewsButton = document.createElement("BUTTON");
  clearNewsButton.textContent = "Poista kaikki";
  clearNewsButton.onclick = function(){clearNews()};

  var removeOldestButton = document.createElement("BUTTON");
  removeOldestButton.textContent = "Poista vanhin";
  removeOldestButton.onclick = function(){removeOldestNews()};

  var removeLatestButton = document.createElement("BUTTON");
  removeLatestButton.textContent = "Poista uusin";
  removeLatestButton.onclick = function(){removeLatestNews()};

  var br = document.createElement("BR");

  container.appendChild(pTitle);
  container.appendChild(iTitle);
  container.appendChild(pContent);
  container.appendChild(iContent);
  container.appendChild(pAuthor);
  container.appendChild(iAuthor);
  container.appendChild(br);
  

  container.appendChild(createNewsButton);
  container.appendChild(clearNewsButton);
  container.appendChild(removeOldestButton);
  container.appendChild(removeLatestButton);
}
