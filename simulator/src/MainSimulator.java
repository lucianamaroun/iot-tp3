import br.ufmg.iot.XBeeSerialMock;

public class MainSimulator {
	
	public static void main(String[] args) {
		XBeeSerialMock mock = new XBeeSerialMock("/dev/ttyUSB0", 7);
		Sink sink = new Sink("Sink", mock);
		CommonNode nodeB = new CommonNode("Node B", mock);
		Demultiplexer demux = new Demultiplexer(sink, nodeB, mock);
		mock.initialize();
		try {
			Thread.sleep(1000);
			sink.startRouting();
			demux.receiveMessages();
			sink.startRequest();
			demux.receiveMessages();
			sink.startRequest();
			demux.receiveMessages();
		} catch(Exception e) {
			e.printStackTrace();
		}
		mock.close();
	}
}