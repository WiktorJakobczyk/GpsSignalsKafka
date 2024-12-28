import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { GpsSignal } from './GpsSignal';
import axios from 'axios';
import { Accordion, Button } from 'react-bootstrap';
import { PinMap } from 'react-bootstrap-icons';

export const GpsSignals = ({ gpsData }) => {

  const handleAddClick = async () => {
    try {
      const BASE_PATH = process.env.REACT_APP_BACKEND || 'localhost:8991';
      await axios.post(`http://${BASE_PATH}/api/producer/gps/device`, {});
    } catch (error) {
      console.error('Error during POST:', error);
    }
  };

  return (
      <Container className ="shadow" style={{ color: "#4a4a4a", backgroundColor:'#f7f7f7', borderRadius: '8px', textAlign:"center", paddingTop: '8px'}}>
        <Accordion>
          <Accordion.Header className="d-flex align-items-center text-white" 
          style={{
            color: "#d2d2d2", 
            backgroundColor:'#ebeded', 
            border: 'solid', 
            borderColor:'#0f8a23',
            borderWidth:'2px',
            borderRadius: '8px', 
          }}>
            <PinMap size={24} className="mr-2" />
            <span>GPS Signals</span>
          </Accordion.Header>
          <Accordion.Body>
            <Row>
              <Col className="d-flex justify-content-center align-items-center"  xs={1}>Color</Col>
              <Col className="d-flex justify-content-center align-items-center" xs={3}>Device uuid</Col>
              <Col className="d-flex justify-content-center align-items-center" xs={2}>Latitude</Col>
              <Col className="d-flex justify-content-center align-items-center" xs={2}>Longitude</Col>
              <Col className="d-flex justify-content-center align-items-center" xs={2}>Signal Time (Last)</Col>
              <Col className="d-flex justify-content-center align-items-center" xs={1}>Active</Col>
              <Col className="d-flex justify-content-center align-items-center" xs={1}></Col>
            </Row>
            <hr/>
            {gpsData && gpsData.length > 0 
            ? (gpsData.map((signal, index) => (<Row key={index}><GpsSignal signal={signal} /></Row>))) 
            : (<p>No GPS data available</p>)}
          </Accordion.Body>
        </Accordion>
        <Row className="justify-content-center" style={{padding: '5px'}}>
          <Col className="d-flex justify-content-center align-items-center" xs={5}>
            <Button variant = 'custom' className="w-100" onClick={handleAddClick}>Add random signal</Button>
          </Col>
        </Row>
      </Container>
  )
}