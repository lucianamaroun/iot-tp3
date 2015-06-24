import br.ufmg.iot.XBeeSerialMock;


public class Node {
	protected String name;
	protected XBeeSerialMock mock;
	
	public Node(String name, XBeeSerialMock mock) {
		this.name = name;
		this.mock = mock;
	}

	public void handleMessage(XBeeSerialMock.MockMessageIn msg) {
		switch(msg.payload[0]) {
		case 0x00:
			handleHelloMessage(msg);
			break;
		case 0x01:
			handleAckMessage(msg);
			break;
		case 0x02:
			handleReqMessage(msg);
			break;
		case 0x03:
			handleRepMessage(msg);
		}
	}
	
	public void handleHelloMessage(XBeeSerialMock.MockMessageIn helloMsg) {
		System.out.println("--- Hello Received by " + name + ":");
		System.out.println(helloMsg);
	}

	public void handleAckMessage(XBeeSerialMock.MockMessageIn ackMsg) {
		System.out.println("--- Ack Received by " + name + ":");
		System.out.println(ackMsg);
	}
	
	public void handleReqMessage(XBeeSerialMock.MockMessageIn reqMsg) {
		System.out.println("--- Req Received by " + name + ":");
		System.out.println(reqMsg);
	}

	public void handleRepMessage(XBeeSerialMock.MockMessageIn reqMsg) {
		System.out.println("--- Rep Received by " + name + ":");
		System.out.println(reqMsg);
	}
}
