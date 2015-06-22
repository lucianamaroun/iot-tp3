#include <XBee.h>
#include <IoTUFMG.h>
#include <SimpleTimer.h>


#define BETA 30000
#define WAIT 0
#define ROUT 1
#define REQ 2
#define BROADCAST_ADDR 0x00 << 8 & 0xFFFF


IoTUFMG iot = IoTUFMG(MODE_MOCK, 7);
Rx16Response response = Rx16Response();
Tx16Request reply;
SimpleTimer timer;
uint16_t parent;
uint8_t n_children;
uint8_t state;
uint16_t my_address;

/* 
 * Resets initial configuration.
 */
void reset() {
  state = WAIT;
  parent = NULL;
  n_children = 0;
}

/* 
 *  Setup function of this instance. Executed at beginning of execution only.
 */
void setup() {
  iot.setup();
  reset();
  uint8_t cmd[] = {'M', 'Y'};
  AtCommandRequest atRequestMY = AtCommandRequest(cmd);
  RemoteAtCommandResponse atResponse = RemoteAtCommandResponse();
  XBee xbee = XBee();
  xbee.send(atRequestMY);
  xbee.getResponse().getAtCommandResponse(atResponse);
  my_address = atResponse.getValue()[0] << 8 & atResponse.getValue()[1];
}

/*
 * Sends the received message as broadcast.
 */
void broadcastMsg() {
  reply = Tx16Request(BROADCAST_ADDR, response.getData(), sizeof(response.getData()));
  iot.send(reply);
}

/*
 * Sends the received message to the parent.
 */
void forwardMsg() {
  reply = Tx16Request(parent, response.getData(), sizeof(response.getData()));
  iot.send(reply);  
}

/* 
 *  Handles a Hello message. Only considered if the parent was not set and the previous 
 *  state was WAIT, that is, not a routing discovery period nor a request transmission one.
 *  Then, the routing phase is started and the parent is set. The message is broadcasted and
 *  and acknoledgement is sent to the parent.
 */
void handleHelloMsg() {
  if (parent == NULL && state == WAIT) {
    state = ROUT;
    parent = response.getRemoteAddress16();
    broadcastMsg();
    sendAck();
  } // otherwise, ignore
}

/*
 * Sends and acknoledgment message to the recently set parent.
 */
void sendAck() {
  uint8_t data[] = { 0x01 };
  reply = Tx16Request(parent, data, sizeof(data));
  iot.send(reply);
}

/*
 * Handles an acknoledgment message. Only counts children if state is routing, otherwise ignores.
 */
void handleAckMsg() {
  if (state == ROUT) {
    n_children++;
  }
}

/* 
 *  Sends a reply message with a reading value. A dummy value, 0xAA, is sent, for any 
 *  required measure. Sent only by unicast to the parent.
 */
void sendRepMsg() {
  uint8_t measure = response.getData()[1];
  uint8_t data[] = { 0x03, measure, my_address >> 8, my_address & 0xff, parent >> 8, parent & 0xff, 0xAA };
  reply = Tx16Request(parent, response.getData(), sizeof(response.getData()));
  iot.send(reply);
}

/* 
 *  Handles a request message. The message is broadcasted and a reply is sent. Also, if
 *  it is the beginning of the request fase, changes the state and sets a timer to interrupt
 *  this fase after BETA seconds.
 */
void handleReqMsg() {
  if (state != WAIT && response.getRemoteAddress16() == parent) {
    if (state == ROUT) {
      timer.setTimeout(BETA, reset);
      state = REQ;
    }
    broadcastMsg();
    sendRepMsg();
  }
}

/*
 * Handles a reply message. Just forwards to the parent in the path to the sink.
 */
void handleRepMsg() {
  if (state == REQ) {
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
  
  delay(200);
}
