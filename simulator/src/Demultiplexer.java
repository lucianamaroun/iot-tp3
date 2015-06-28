import java.util.Timer;
import java.util.TimerTask;

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
	
	public boolean receiveMessages() throws InterruptedException {
		boolean any = false;
		while (mock.hasPacket()) {
			any = true;
			XBeeSerialMock.MockMessageIn msg = mock.getPacket();
			Thread.sleep(500);
			if (msg.remoteAddressM == 0x00 && msg.remoteAddressL == 0x01) {
				sink.handleMessage(msg);
			} else { 
				if (msg.remoteAddressM == 0x00 && msg.remoteAddressL == 0x02) {
					nodeB.handleMessage(msg);
				} else if (msg.remoteAddressM == -1 && msg.remoteAddressL == -1) { // broadcast
					sink.handleMessage(msg);
					nodeB.handleMessage(msg);
				}
			}
		}
		return any;
	}	
}
