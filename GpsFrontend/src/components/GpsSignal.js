import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { CheckCircleFill } from 'react-bootstrap-icons';
import { XCircleFill } from 'react-bootstrap-icons';
import { SquareFill } from 'react-bootstrap-icons';
import { TrashFill } from 'react-bootstrap-icons';
import { Button } from 'react-bootstrap';
import axios from 'axios';

export const GpsSignal = ({ signal }) => {

  const convertToDMS = (decimalDegree) => {
    const degrees = Math.floor(decimalDegree); 
    const minutes = (decimalDegree - degrees) * 60; 
    const minutesRounded = Math.round(minutes * 100) / 100; 
    return `${degrees}° ${minutesRounded}'`;
  };

  const handleAddClick = async (uuid) => {
    try {
      const BASE_PATH = process.env.REACT_APP_BACKEND || 'localhost:8991';
      await axios.delete(`http://${BASE_PATH}/api/producer/gps/device/${uuid}`, {});
    } catch (error) {
      console.error(error);
    }
  };

  const timestamp = new Date(signal.timestamp);
  const isRecent = signal.status === 'ACTIVE';
  return (
<Container style={{borderRadius: "8px", textAlign: "center", padding:"5px"}}>
  <Row>
    <Col className="d-flex justify-content-center align-items-center" xs={1}><SquareFill color={signal.color} size={32}/></Col>
    <Col className="d-flex justify-content-center align-items-center" xs={3}>{signal.deviceUuid}</Col>
    <Col className="d-flex justify-content-center align-items-center" xs={2}>{convertToDMS(signal.latitude)}</Col>
    <Col className="d-flex justify-content-center align-items-center" xs={2}>{convertToDMS(signal.longitude)}</Col>
    <Col className="d-flex justify-content-center align-items-center" xs={2}>{timestamp.toLocaleString()}</Col>
    <Col className="d-flex justify-content-center align-items-center" xs={1}>{isRecent ? <CheckCircleFill color="#0f8a23" size={32} /> : <XCircleFill color="red" size={32} />}</Col>
    <Col className="d-flex justify-content-center align-items-center" xs={1}><Button onClick={() => handleAddClick(signal.deviceUuid)} variant='danger'><TrashFill/></Button></Col>
  </Row>
</Container>

  )
}