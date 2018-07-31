var overlay;
Suurpeli.prototype = new google.maps.OverlayView();

// Initialize the map and the custom overlay.

function initMap() {
  var map = new google.maps.Map(document.getElementById('map'), {
    zoom: 17,
    center: {lat: 61.81550458885635, lng: 25.170653930688445},
    mapTypeId: 'satellite'
  });

  
   //vanhat koordinaatit
   
  var bounds = new google.maps.LatLngBounds(
    new google.maps.LatLng(61.813649878776054, 25.16612636187142),
    //jos tarvii vaihda 2 ensimmäinen 3.
    new google.maps.LatLng(61.81735408832565, 25.17486736193234));
   
    srcImage = `SP18_paino_svg.png`;


  map.addListener('click', function(e) {

    
        var location = e.latLng;
        marker.push(new google.maps.Marker({position: location,
           map: map,
            draggable: true,
          icon: pinSymbol("#FFF")}));
      }); 
      

  overlay = new Suurpeli(bounds, srcImage, map);
}
/** @constructor */
function Suurpeli(bounds, image, map) {

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
}

/**
 * onAdd is called when the map's panes are ready and the overlay has been
 * added to the map.
 */
Suurpeli.prototype.onAdd = function() {

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

Suurpeli.prototype.draw = function() {
  var overlayProjection = this.getProjection();
  var sw = overlayProjection.fromLatLngToDivPixel(this.bounds_.getSouthWest());
  var ne = overlayProjection.fromLatLngToDivPixel(this.bounds_.getNorthEast());

  // Resize
  var div = this.div_;
  div.style.left = sw.x + 'px';
  div.style.top = ne.y + 'px';
  div.style.width = (ne.x - sw.x) + 'px';
  div.style.height = (sw.y - ne.y) + 'px';
};
Suurpeli.prototype.onRemove = function() {
  this.div_.parentNode.removeChild(this.div_);
  this.div_ = null;
};
 // Set the visibility to 'hidden' or 'visible'.
 Suurpeli.prototype.hide = function() {
  if (this.div_) {
    // The visibility property must be a string enclosed in quotes.
    this.div_.style.visibility = 'hidden';
  }
};

Suurpeli.prototype.show = function() {
  if (this.div_) {
    this.div_.style.visibility = 'visible';
  }
};

Suurpeli.prototype.toggle = function() {
  if (this.div_) {
    if (this.div_.style.visibility === 'hidden') {
      this.show();
    } else {
      this.hide();
    }
  }
};


google.maps.event.addDomListener(window, 'load', initMap);