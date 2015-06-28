import br.ufmg.iot.XBeeSerialMock;


public class Demultiplexer {
	private Sink sink;
	private Node nodeB;
	private XBeeSerialMock mock;
	
	public Demultiplexer(Sink sink, Node nodeB, XBeeSerialMock mock) {
		this.sink = sink;
		this.nodeB = nodeB;
		this.mock = mock;
	}
	
	public void close() {
		mock.close();
	}
	
	public void receiveMessages() {
		while (mock.hasPacket()) {
			XBeeSerialMock.MockMessageIn msg = mock.getPacket();
			if (msg.myAddressL == 0x00) {
				sink.handleMessage(msg);
			} else if (msg.myAddressL == 0x01) {
				nodeB.handleMessage(msg);
			} else { //broadcast
				sink.handleMessage(msg);
				nodeB.handleMessage(msg);
			}
		}
	}	
}
