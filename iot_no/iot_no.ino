#include <XBee.h>
#include <IoTUFMG.h>
#include <SimpleTimer.h>

#define BETA 30000
#define WAIT 0
#define ROUT 1
#define REQ 2
#define LED 13
#define DELAY 25


IoTUFMG iot = IoTUFMG(MODE_NORMAL, 7);
Rx16Response response = Rx16Response();
Tx16Request reply;
SimpleTimer timer;
uint16_t parent;
uint8_t n_children;
uint16_t my_address;


/* 
 *  Setup function of this instance. Executed at beginning of execution only.
 */
void setup() {
  iot.setup();
  reset();
  my_address = (uint16_t) 0x00;
  my_address = (my_address << 8) | 0x11;
}

/* 
 * Resets initial configuration.
 */
void reset() {
  parent = 0;
  n_children = 0;
  digitalWrite(LED, LOW);
}

/*
 * Sends the received message as broadcast.
 */
void broadcastMsg() {
  reply = Tx16Request(BROADCAST_ADDRESS, response.getData(), response.getDataLength());
  iot.send(reply);
  delay(DELAY);
}

/*
 * Sends the received message to the parent.
 */
void forwardMsg() {
  reply = Tx16Request(parent, response.getData(), response.getDataLength());
  iot.send(reply);
  delay(DELAY);
}

/*
 * Sends and acknoledgment message to the recently set parent.
 */
void sendAck() {
  uint8_t data[] = { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
  reply = Tx16Request(parent, data, 7);
  iot.send(reply);
  delay(DELAY);
}

/* 
 *  Handles a Hello message. Only considered if the parent was not set.
 *  Then, the routing phase is started and the parent is set. The message is broadcasted and
 *  and acknoledgement is sent to the parent.
 */
void handleHelloMsg() {
  if (parent == 0) {
    parent = response.getRemoteAddress16();
    broadcastMsg();
    sendAck();
    digitalWrite(LED, HIGH);
    timer.setTimeout(BETA, reset);
  } // otherwise, ignore
}

/*
 * Handles an acknoledgment message.
 */
void handleAckMsg() {
   n_children++;
}

/* 
 *  Handles a request message. The message is broadcasted and a reply is sent. Also, if
 *  it is the beginning of the request fase and sets a timer to interrupt
 *  this fase after BETA seconds.
 */
void handleReqMsg() {
  if (response.getRemoteAddress16() == parent) {
    broadcastMsg();
    sendRepMsg();
  }
}

/* 
 *  Sends a reply message with a reading value. A dummy value, 0x0A, is sent, for any 
 *  required measure. Sent only by unicast to the parent.
 */
void sendRepMsg() {
  uint8_t metric = response.getData()[1];
  uint8_t data[] = { 0x03, metric, my_address & 0xff, my_address >> 8, parent & 0xff, parent >> 8, 0x0A };
  reply = Tx16Request(parent, data, 7);
  iot.send(reply);
}

/*
 * Handles a reply message. Just forwards to the parent in the path to the sink.
 */
void handleRepMsg() {
  if (parent != 0) {
    forwardMsg();
  }
}

/*
 * Main loop. Executed continuously.
 */
void loop() {
  iot.readPacket();

  if (iot.isAvailable()) {
      iot.getRx16Response(&response);
      delay(DELAY);

      uint8_t type = response.getData()[0];

      if (type == 0x00) {
        handleHelloMsg();
      } else if (type == 0x01) {
        handleAckMsg();
      } else if (type == 0x02) {
        handleReqMsg();
      } else if (type == 0x03) {
        handleRepMsg();
      }
  }
  timer.run();
  
  delay(DELAY);
}
