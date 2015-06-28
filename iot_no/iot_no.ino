#include <XBee.h>
#include <IoTUFMG.h>
#include <SimpleTimer.h>

#define BETA 1
#define LED 13

IoTUFMG iot = IoTUFMG(MODE_MOCK, 7);
Rx16Response response = Rx16Response();
Tx16Request reply;
SimpleTimer timer = SimpleTimer();
int timerID; 
uint16_t parent;

void setup() {
  iot.setup();
  reset();
  timerID = timer.setTimeout(BETA, reset);
}

void reset() {
  parent = 0;
  digitalWrite(LED, LOW);
}

void broadcastMsg() {
  reply = Tx16Request(BROADCAST_ADDRESS, response.getData(), response.getDataLength());
  iot.send(reply);
  delay(500);
}

void sendAck() {
  uint8_t data[] = {0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
  reply = Tx16Request(parent, data, 7);
  iot.send(reply);
  delay(500);
}

void handleHelloMsg() {
  if (parent == 0) {
    parent = response.getRemoteAddress16();
    broadcastMsg();
    sendAck();
    timer.enable(timerID);
    timer.run();
    digitalWrite(LED, HIGH);
  }
}

void loop() {
  iot.readPacket();

  if (iot.isAvailable()) {
      iot.getRx16Response(&response);
      delay(500);
      handleHelloMsg();      
  }
  
  delay(500);
}
