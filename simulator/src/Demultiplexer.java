import br.ufmg.iot.XBeeSerialMock;


public class Demultiplexer {
	private Sink sink;
	private Node nodeB;
	private XBeeSerialMock mock;
	private int BETA = 30;
	
	public Demultiplexer(Sink sink, Node nodeB, XBeeSerialMock mock) {
		this.sink = sink;
		this.nodeB = nodeB;
		this.mock = mock;
	}
	
	public void close() {
		mock.close();
	}
	
	public void receiveMessages() {
		int countNoMsg = 0;
		while (countNoMsg < BETA) {
			if (mock.hasPacket()) {
				countNoMsg = 0;
				XBeeSerialMock.MockMessageIn msg = mock.getPacket();
				if (msg.myAddressL == 0x00) {
					sink.handleMessage(msg);
				} else if (msg.myAddressL == 0x01) {
					nodeB.handleMessage(msg);
				} else { //broadcast
					sink.handleMessage(msg);
					nodeB.handleMessage(msg);
				}
			} else {
				countNoMsg = 1;
			}
			try {
				Thread.sleep(1000);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}	
}
