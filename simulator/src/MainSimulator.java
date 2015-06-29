import br.ufmg.iot.XBeeSerialMock;

public class MainSimulator {
	
	public static void main(String[] args) {
		XBeeSerialMock mock = new XBeeSerialMock("/dev/ttyUSB0", 7);
		Sink sink = new Sink("Sink", mock);
		CommonNode nodeB = new CommonNode("Node B", mock);
		Demultiplexer demux = new Demultiplexer(sink, nodeB, mock);
		mock.initialize();
		try {
			// Scenario: Two routings, one with 3 and the other with 3 requests
			Thread.sleep(1000);
			sink.startRouting();
			demux.receiveMessages();
			for (int i = 0; i < 3; i++) {
				sink.startRequest();
				demux.receiveMessages();
			}
			Thread.sleep(20000); // Wait for operation phase of Nodes to be over
			sink.startRouting();
			demux.receiveMessages();
			for (int i = 0; i < 2; i++) {
				sink.startRequest();
				demux.receiveMessages();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		mock.close();
	}
}