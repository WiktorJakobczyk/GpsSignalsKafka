import './App.css';
import { GpsMap } from "./components/GpsMap";
import { ControlPanel } from "./components/ControlPanel";
import Container from 'react-bootstrap/Container';
import { GpsProvider } from './components/GpsContext';

function App() {
  return (  
      <GpsProvider>

        <ControlPanel style={{ position:'absolute'}}/>
        <GpsMap style={{ position:'absolute'}}/>

      </GpsProvider>
  
  );
}

export default App;
