class Lippu {
    constructor(latlng, active) {
        this.latlng = latlng;
        this.active = active;
    }
}

//reference to "liput"
var lippuRef = firebase.database().ref('liput');

//listen for changes
lippuRef.on('child_added', function(){
  getLippuData();
});

lippuRef.on('child_changed', function(){
  getLippuData();
});

lippuRef.on('child_removed', function(){
  getLippuData();
});

//create an empty array for all lippu objects
var lippuarray = [];

//get all children and populate table
function getLippuData(){
  lippuRef.once('value', function(snapshot){
    document.getElementById("lippuData").innerHTML = "";
    //placeholder array
    var snapShotArr = [];
    snapshot.forEach(function(childSnapshot){
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
      snapShotArr.push(new Lippu(childData.LatLng, childData.active));
      document.getElementById("lippuData").innerHTML += 
      "<tr>" + "<th>" + childKey + "</th>" + "</tr>" + 
      "<tr>" + 
      "<td>" + "LatLng:" + "</td>" + 
      "<td>" + childData.LatLng + "</td>" + 
      "</tr>" + 
      "<tr>" + 
      "<td>" + "isActive?:" + "</td>" + 
      "<td>" + childData.active + "</td>" + 
      "</tr>";
    });
    console.log("snapShotArr len: " + snapShotArr.length);
    //assign placeholder array to the instance variable array
    lippuarray = snapShotArr;
    console.log("lippuarr len: " + lippuarray.length);
  });
}

//add a flag
function pushTest(){
  var r1 = Math.floor(Math.random() * 100);
  var r2 = Math.floor(Math.random() * 100);
  var r = Math.random();
  lippuarray.push(new Lippu(r1 + ", " + r2, false));
  
  writeLippuData(lippuarray);
}

//remove a flag
function removeFlag(){
  var index = parseInt(lippuarray.length-1);
  console.log("len before: " + lippuarray.length);
  lippuarray.splice(index, 1);
  console.log("len after: " + lippuarray.length);
  writeLippuData(lippuarray);
}

function clearFlags(){
  lippuRef.remove();
}

//write all lippu objects into the database (from lippuarray)
function writeLippuData(lippuarr){
  var i = 1;
  var updates = {};
  lippuarr.forEach(function(l){
    var data = {
      LatLng: l.latlng,
      active: l.active
    }
    updates['lippu' + i] = data;
    i++;
  });
  
  firebase.database().ref('liput').set(updates);
}

//update all changes to the database
function updateFlagData(){
  var i = 1;
  var updates = {};
  lippuarray.forEach(function(l){
    var data = {
      LatLng: l.latlng,
      active: l.active
    }
    updates['lippu' + i] = data;
    i++;
  });
  //minimize writes by using update and not set
  firebase.database().ref('liput').update(updates);
}

//activates the next objective
function activateNextFlag(){
  for(var i = 0; i < lippuarray.length; i++){
    if(lippuarray[i].active == false){
      console.log("found it at index: " + i);
      lippuarray[i].active = true;
      break;
    }
  }
  updateFlagData();
}

//deactivate last active objective in the list
function deactivateLastFlag(){
  console.log("len: " + lippuarray.length);
  for(var i = 0; i < lippuarray.length; i++){
    if(lippuarray[i].active == false){
      console.log("found it at index: " + i);
      lippuarray[i-1].active = false;
      break;
    }
    //very last index if all were true
    if(i == lippuarray.length-1){
      lippuarray[i].active = false;
    }
  }
  updateFlagData();
}