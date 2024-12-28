import React, { createContext, useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';

export const GpsContext = createContext();

export const GpsProvider = ({ children }) => {
  const [gpsData, setGpsData] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const stompClientRef = useRef(null);
  const colorMapRef = useRef({});
  const BASE_PATH = process.env.REACT_APP_BACKEND || 'localhost:8992';

  const getRandomColor = () => {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
      color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
  };

  const getColorForUuid = (deviceUuid) => {
    if (!colorMapRef.current[deviceUuid]) {
      const color = getRandomColor();
      colorMapRef.current[deviceUuid] = color;  
  
      return color;
    }
    return colorMapRef.current[deviceUuid]; 
  };

  useEffect(() => {
    const stompClient = new Client({
      brokerURL: `ws://${BASE_PATH}/gs-guide-websocket`,
      reconnectDelay: 5000,
      onConnect: (frame) => {
        setIsConnected(true);
        stompClient.subscribe('/topic/gps', (message) => {
          let gpsData = JSON.parse(message.body); 
          gpsData = gpsData.map(item => ({
            ...item, 
            color: getColorForUuid(item.deviceUuid) 
          }));
        
          setGpsData(gpsData);
        });
      },
      onWebSocketError: (error) => {
        console.error('WebSocket Error:', error);
      },
      onStompError: (frame) => {
        console.error('STOMP Error:', frame.headers['message']);
        console.error('Additional details:', frame.body);
      },
    });

    stompClientRef.current = stompClient; 
    stompClient.activate();

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
        setIsConnected(false);
        console.log('Disconnected');
      }
    };
  }, []);

  return (
    <GpsContext.Provider value={{ gpsData, isConnected }}>
      {children}
    </GpsContext.Provider>
  );
};
