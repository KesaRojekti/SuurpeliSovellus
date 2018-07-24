class Lippu {
    constructor(latlng, active) {
        this.latlng = latlng;
        this.active = active;
    }
    
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
 var marker = [];
 function pushTest(){
   marker = new google.maps.Marker({position: start, map: map, draggable: true});
  writeLippuData(lippuarray);
}
 //Google maps
    var map, i =0;
    var start = {lat: 61.81555994553038, lng: 25.17069664313044};
    function initMap() { 
    map = new google.maps.Map(
      document.getElementById('map'), {zoom: 17,
       center: start,
      mapTypeId: `satellite`});
      
      map.addListener('click', function(e) {

        if(marker.length == 1){
          marker[0].setIcon(pinSymbol("#FF0"));
        }
        var location = e.latLng;
        marker.push(new google.maps.Marker({position: location,
           map: map,
            draggable: true,
          icon: pinSymbol("#FFF")}));
      }); 
      
}
//Symbol for marker icon
function pinSymbol(color) {
  return {
      path: 'M 4,8 -1,-2 V -50 H 1 V -2 z M 1,-45 H 30 V -20 H 1 z',
      fillColor: color,
      fillOpacity: 1,
      strokeColor: '#000',
      strokeWeight: 2,
      scale: 1,
 };
}


    function UpdateFlags(){
    
      if(marker.length == 0){
        var r1 = marker[1].getPosition().lat();
      var r2 = marker[1].getPosition().lng();
      }else{
        var r1 = marker[i].getPosition().lat();
        var r2 = marker[i].getPosition().lng();
      }
      i++;
      console.log(marker.length, r1, r2);
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
  for (var i = 0; i < marker.length; i++) {
    marker[i].setMap(null);
}
marker.length = 1;
console.log(marker.length);
};


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
      marker[i].setIcon(pinSymbol("#0F0"));
      marker[i + 1].setIcon(pinSymbol("#FF0"));
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
      marker[i].setIcon(pinSymbol("#FFF"));
      break;
    }
    //very last index if all were true
    if(i == lippuarray.length-1){
      lippuarray[i].active = false;
    }
  }
  updateFlagData();
}
