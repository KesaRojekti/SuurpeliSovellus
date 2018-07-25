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
    var overlay;
    
    /*SuurpeliZone.prototype = new google.maps.OverlayView();*/

    function initMap() { 
      console.log(marker.length);
console.log(i);
    map = new google.maps.Map(
      document.getElementById('map'), {zoom: 17,
       center: start,
      mapTypeId: `satellite`});
     /* 
      var bounds = new google.maps.LatLngBounds(
        new google.maps.LatLng(25.173700920129477, 25.167800060296713),
        new google.maps.LatLng(61.81690049832376, 25.173700920129477));
        var image = `https://paintball.fi/data/media/0/0/Ning_Media/blogs/1/1559_blogs.jpg`;

        overlay = new SuurpeliZone(bounds, image, map);
*/
      map.addListener('click', function(e) {

        var location = e.latLng;
        marker.push(new google.maps.Marker({position: location,
           map: map,
            draggable: true,
          icon: pinSymbol("#FFF")}));
          marker[0].setIcon(pinSymbol("#FF0"));
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
     
      var r1 = marker[i].getPosition().lat();
      var r2 = marker[i].getPosition().lng();
      i++;
      console.log(marker.length,i,  r1, r2);
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
i= 0;
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

//activates the next objective
function activateNextFlag(){
  for(var i = 0; i < lippuarray.length; i++){
    if(lippuarray[i].active == false){
      console.log("found it at index: " + i);

        marker[i].setIcon(pinSymbol("#0F0"));


        if(marker[i+1] != undefined){
          marker[i + 1].setIcon(pinSymbol("#FF0"));
        }
        
      
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
      
      if(lippuarray[i - 1] != undefined){
        lippuarray[i-1].active = false;

      }
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



 /**
  * 
  * 
  * 
  * This is for overlay if needed
  */
/*
    function SuurpeliZone(bounds, image, map) {

      // Initialize all properties.
      this.bounds_ = bounds;
      this.image_ = image;
      this.map_ = map;
      // Define a property to hold the image's div. We'll
      // actually create this div upon receipt of the onAdd()
      // method so we'll leave it null for now.
      this.div_ = null;
      // Explicitly call setMap on this overlay.
      this.setMap(map);
    }*/
/*
SuurpeliZone.prototype.onAdd = function() {
  var div = document.createElement('div');
  div.style.borderStyle = 'none';
  div.style.borderWidth = '0px';
  div.style.position = 'absolute';

  // Create the img element and attach it to the div.
  var img = document.createElement('img');
  img.src = this.image_;
  img.style.width = '100%';
  img.style.height = '100%';
  img.style.position = 'absolute';
  div.appendChild(img);

  this.div_ = div;

  // Add the element to the "overlayLayer" pane.
  var panes = this.getPanes();
  panes.overlayLayer.appendChild(div);
};

SuurpeliZone.prototype.draw = function() {

  // We use the south-west and north-east
  // coordinates of the overlay to peg it to the correct position and size.
  // To do this, we need to retrieve the projection from the overlay.
  var overlayProjection = this.getProjection();

  // Retrieve the south-west and north-east coordinates of this overlay
  // in LatLngs and convert them to pixel coordinates.
  // We'll use these coordinates to resize the div.
  var sw = overlayProjection.fromLatLngToDivPixel(this.bounds_.getSouthWest());
  var ne = overlayProjection.fromLatLngToDivPixel(this.bounds_.getNorthEast());

  // Resize the image's div to fit the indicated dimensions.
  var div = this.div_;
  div.style.left = sw.x + 'px';
  div.style.top = ne.y + 'px';
  div.style.width = (ne.x - sw.x) + 'px';
  div.style.height = (sw.y - ne.y) + 'px';
};
    
SuurpeliZone.prototype.onRemove = function() {
  this.div_.parentNode.removeChild(this.div_);
};

// Set the visibility to 'hidden' or 'visible'.
SuurpeliZone.prototype.hide = function() {
  if (this.div_) {
    // The visibility property must be a string enclosed in quotes.
    this.div_.style.visibility = 'hidden';
  }
};

SuurpeliZone.prototype.show = function() {
  if (this.div_) {
    this.div_.style.visibility = 'visible';
  }
};

SuurpeliZone.prototype.toggle = function() {
  if (this.div_) {
    if (this.div_.style.visibility === 'hidden') {
      this.show();
    } else {
      this.hide();
    }
  }
};

SuurpeliZone.prototype.toggleDOM = function() {
  if (this.getMap()) {
    // Note: setMap(null) calls OverlayView.onRemove()
    this.setMap(null);
  } else {
    this.setMap(this.map_);
  }
};*/

/*google.maps.event.addDomListener(window, 'load', initMap);*/
    