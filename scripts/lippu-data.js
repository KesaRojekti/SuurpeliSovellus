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
     //checks markers
     if(lippuarray.length > 0){
      writeLippuData();
    }
  });
}

//add a flag
var flagsArrayCont;
 var marker = [];
 function pushTest(){
   marker = new google.maps.Marker({position: start, map: map, draggable: true});
  writeLippuData(lippuarray);
}
 //Google maps
    var map, count =0;
    var start = {lat: 61.81550458885635, lng: 25.170653930688445};
    var overlay;
    
//Symbol for marker icon
function pinSymbol(color) {
  return {
      path: 'M 0,0 -1,-2 V -45 H 1 V -2 z M 1,-45 H 30 V -20 H 1 z',
      fillColor: color,
      fillOpacity: 1,
      strokeColor: '#000',
      strokeWeight: 2,
      scale: 1,
 };
}
//updateflags() from button update flag postions
    function UpdateFlags(){
      // gets lat and lng
      var r1 = marker[count].getPosition().lat();
      var r2 = marker[count].getPosition().lng();
      count++;
      console.log(marker.length,count,  r1, r2);
      lippuarray.push(new Lippu(r1 + ", " + r2, false));
      writeLippuData(lippuarray);
    }
//remove a flag
var rm = 1;
function removeFlag(){
  var index = parseInt(lippuarray.length-1);
  console.log("len before: " + lippuarray.length);
  lippuarray.splice(index, 1);
  console.log("len after: " + lippuarray.length);
  writeLippuData(lippuarray);
}

//clears the lippuRef and all the markers from the map
function clearFlags(){
  lippuRef.remove();
  flagsArrayCont
  for (var i = 0; i < marker.length; i++) {
    marker[i].setMap(null);
}
count = 0;
marker = [];
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

//activates the next objective. also changes the color of marker
function activateNextFlag(){
  var l = 0;
  for(var i = 0; i < lippuarray.length; i++){
    
   if(lippuarray[i].active == false){
    lippuarray[i].active = true;

    marker[i].setIcon(pinSymbol("#FF0"));
    if(marker[i - 1] != undefined){
      marker[i - 1].setIcon(pinSymbol("#0F0")); 
    }
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
      
      if(lippuarray[i - 1] != undefined){
        lippuarray[i-1].active = false;

      }
      if(marker[i - 1] != undefined){
        marker[i - 1].setIcon(pinSymbol("#FFF"));
        marker[i - 2].setIcon(pinSymbol("#FF0"));
      }else if(marker[i - 1] == undefined){
        marker[i].setIcon(pinSymbol("#FF0"));
      }
      break;
    }
    //very last index if all were true
    if(i == lippuarray.length-1){
      lippuarray[i].active = false;
    }
  }
  updateFlagData();
}
