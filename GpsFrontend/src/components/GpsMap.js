import * as React from 'react';
import Map, { Marker, Popup } from 'react-map-gl';
import maplibregl from 'maplibre-gl'; 
import 'maplibre-gl/dist/maplibre-gl.css';
import { GpsContext } from './GpsContext'; 
import { GpsMapPopup } from './GpsMapPopup';

export const GpsMap = () => {
  const { gpsData } = React.useContext(GpsContext);
  const [hoveredMarker, setHoveredMarker] = React.useState(null);
  const mapRef = React.useRef(null);

  const handleMarkerHover = (data) => {
    setHoveredMarker(data);
  };

  const handleMarkerLeave = () => {

    setHoveredMarker(null);
  };

  const lightenColor = (color, percent) => {
    var R = parseInt(color.substring(1,3),16);
    var G = parseInt(color.substring(3,5),16);
    var B = parseInt(color.substring(5,7),16);

    R = parseInt(R * (100 + percent) / 100);
    G = parseInt(G * (100 + percent) / 100);
    B = parseInt(B * (100 + percent) / 100);

    R = (R<255) ? R : 255;  
    G = (G<255) ? G : 255;  
    B = (B<255) ? B : 255;  

    R = Math.round(R)
    G = Math.round(G)
    B = Math.round(B)

    var RR = ((R.toString(16).length===1) ? "0" + R.toString(16) : R.toString(16));
    var GG = ((G.toString(16).length===1) ? "0" + G.toString(16) : G.toString(16));
    var BB = ((B.toString(16).length===1) ? "0" + B.toString(16) : B.toString(16));

    return "#" + RR + GG + BB;
  }
  
  const renderMarkers = () => {
    return gpsData?.map((data) => (
      <Marker
        key={data.deviceUuid}
        longitude={data.longitude}
        latitude={data.latitude}
        anchor="bottom">
        <div style={{
            position: 'relative',
            cursor: 'pointer',
            backgroundColor: data.color,
            width: '40px',
            height: '40px',
            borderRadius: '50%',
            zIndex: 1,
            animation: 'pulse 1.5s infinite',
          }}
          onMouseEnter={() => handleMarkerHover(data)}
          onMouseLeave={handleMarkerLeave} >
          <div className="circle-wave" style={{borderColor: lightenColor(data.color, 40)}}></div>
        </div>
      </Marker>
    ));
  };

  return (
    <div style={{ height: '100vh', display: 'flex', flexDirection: 'column' }}>
    <Map
      ref={mapRef}
      initialViewState={{
        longitude: 20,
        latitude: 50,
        zoom: 2,
      }}
      style={{ width: '100%', height: '100%' }}
      mapLib={maplibregl}
      mapStyle="https://demotiles.maplibre.org/style.json"
    >
      {renderMarkers()}
      {hoveredMarker && (
        <Popup longitude={hoveredMarker.longitude} latitude={hoveredMarker.latitude} anchor="top">
          <GpsMapPopup popUpData={hoveredMarker}/>
        </Popup>
      )}
    </Map>
    </div>
  );
};
