import * as React from 'react';
import 'maplibre-gl/dist/maplibre-gl.css';
import { Card, Col, Container, Row } from 'react-bootstrap';

export const GpsMapPopup = ({popUpData}) => {
  
  const convertToDMS = (decimalDegree) => {
    const degrees = Math.floor(decimalDegree);
    const minutes = (decimalDegree - degrees) * 60; 
    const minutesRounded = Math.round(minutes * 100) / 100;
    return `${degrees}° ${minutesRounded}'`;
  };

  const getContrastYIQ = (hexcolor) => {
    var r = parseInt(hexcolor.substring(1,3),16);
    var g = parseInt(hexcolor.substring(3,5),16);
    var b = parseInt(hexcolor.substring(5,7),16);
    var yiq = ((r*299)+(g*587)+(b*114))/1000;
    return (yiq >= 128) ? 'black' : 'white';
}

  return (
    <Card bg={'Info'} key={'Info'} text={ 'dark'} style={{ width: '18rem', fontWeight:'bold'}} className="shadow">
    <Card.Header style={{ 
      backgroundColor: popUpData.color, 
      color: getContrastYIQ(popUpData.color),
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      }}>
        {popUpData.deviceUuid}
    </Card.Header>
    <Card.Body>
    <Container className="text-center" fluid>
      <Row className="mb-3" style={{color:"#2e2e2e", fontWeight:"bold"}}>
        <Col xs={12} sm={6}>Lat: {convertToDMS(popUpData.latitude)}</Col>
        <Col xs={12} sm={6}>Log: {convertToDMS(popUpData.longitude)}</Col>
      </Row>
      <Row className="mb-3" style={{color:"#2e2e2e", fontStyle:"italic"}}>
        <Col xs={12}>{popUpData.timestamp}</Col>
      </Row>
    </Container>
    </Card.Body>
  </Card>
  );
};
