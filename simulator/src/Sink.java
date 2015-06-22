import br.ufmg.iot.XBeeSerialMock;


public class Sink {
	public static XBeeSerialMock routingMock;
	public static XBeeSerialMock requestMock;
	
	public void startRouting() {
		routingMock = new XBeeSerialMock("/dev/tty.usbserial-AI02CQ2J", 1);
		routingMock.initialize();
		try {
			Thread.sleep(1000);
			routingMock.send(new XBeeSerialMock.MockMessageOut((byte)0x00, (byte)0x00, new byte[] { 0x00 })); // Hello message			
		} catch(Exception e) {
			e.printStackTrace();
		}
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
	
	public void startRequest() {
		requestMock = new XBeeSerialMock("/dev/tty.usbserial-AI02CQ2J", 2);
		requestMock.initialize();
		try {
			Thread.sleep(1000);
			requestMock.send(new XBeeSerialMock.MockMessageOut((byte)0x00, (byte)0x00, new byte[] { 0x02, 0x00 })); // Req message			
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
				System.out.println(messageIn);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		requestMock.close();
	}
}
