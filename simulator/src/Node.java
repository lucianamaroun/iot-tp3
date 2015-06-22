import br.ufmg.iot.XBeeSerialMock;

public class Node {
	public static XBeeSerialMock routingMock;
	public static XBeeSerialMock requestMock;
	public static byte[] parent;
	
	public void receiveHello() {
		XBeeSerialMock.MockMessageIn messageIn = routingMock.getPacket();
		System.out.println("--- Hello Received:");
		System.out.println(messageIn);
		parent = new byte[] {messageIn.myAddressM, messageIn.myAddressL};
	}
	
	public void receiveAcks() {
		XBeeSerialMock.MockMessageIn messageIn = routingMock.getPacket();
		System.out.println("--- Acks Received:");
		System.out.println(messageIn);
		try {
			Thread.sleep(1000);
			while (routingMock.hasPacket()) {
				messageIn = routingMock.getPacket(); // Ack message
				System.out.println(messageIn);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		routingMock.close();
	}
	
	public void receiveRequests() {
		requestMock = new XBeeSerialMock("/dev/tty.usbserial-AI02CQ2J", 2);
		requestMock.initialize();
		try {
			while (requestMock.hasPacket()) {
				XBeeSerialMock.MockMessageIn messageIn = requestMock.getPacket(); // Rep message: problem 7 bytes?
				if (messageIn.myAddressM == parent[0] && messageIn.myAddressL == parent[1]) {
					requestMock.send(new XBeeSerialMock.MockMessageOut((byte)0x00, (byte)0x01, new byte[] { 0x03, 0x00, 0x00, 0x01, parent[0], parent[1], 0x0A })); // Req message			
				}
			}
			Thread.sleep(1000);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveReplies() {
		XBeeSerialMock.MockMessageIn messageIn = requestMock.getPacket();
		System.out.println(messageIn);
		System.out.println("--- Reps Received:");
		try {
			Thread.sleep(1000);
			while (requestMock.hasPacket()) {
				messageIn = requestMock.getPacket(); // Rep message: problem 7 bytes?
				if (messageIn.myAddressM == parent[0] && messageIn.myAddressL == parent[1]) {
					requestMock.send(new XBeeSerialMock.MockMessageOut((byte)0x00, (byte)0x01, messageIn.payload)); // Req message			
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		requestMock.close();
	}
}
