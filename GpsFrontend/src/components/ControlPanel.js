import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { GpsSignals } from './GpsSignals';
import { Badge } from 'react-bootstrap';
import { useContext } from 'react';
import { GpsContext } from './GpsContext';

export const ControlPanel = () => {
  const { gpsData, isConnected } = useContext(GpsContext);

  return (
      <Container style={{position: 'absolute', left: '50%', transform: 'translateX(-50%)', zIndex: '100', textAlign:"center", padding: "10px 60px", maxWidth: "80%"}}>
        <Row className="d-flex align-items-center justify-content-center">
            <Col className="shadow  d-flex align-items-center justify-content-center" style={{color: "#4a4a4a", backgroundColor:'#f7f7f7', borderTopRightRadius: '8px', borderTopLeftRadius: '8px', padding:'4px'}} md="auto"> 
              <Container>
                <h3>Websocket connection status {isConnected ?  <Badge bg="" style={{backgroundColor: '#0f8a23'}}>Connected</Badge> : <Badge bg="danger">Disconnected</Badge>}</h3>
              </Container>
             </Col>
        </Row>
          <Row>
            <GpsSignals gpsData={gpsData}/>
          </Row>
      </Container>
  )
}