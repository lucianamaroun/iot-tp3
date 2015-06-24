import java.io.BufferedReader;
import java.io.InputStreamReader;

import br.ufmg.iot.XBeeSerialMock;


public class MainSimulator {
	
	public static void main(String[] args) {
		XBeeSerialMock mock = new XBeeSerialMock("/dev/tty.usbserial-AI02CQ2J", 7);
		mock.initialize();
		Sink sink = new Sink("Sink", mock);
		CommonNode nodeB = new CommonNode("Node B", mock);
		Demultiplexer demux = new Demultiplexer(sink, nodeB, mock);
		
		String answer;
		while (true) {
			System.out.println("Digite Y para enviar uma nova requisição ou N para sair.");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				answer = reader.readLine().trim();
				if (answer.equals("Y")) {
					sink.startRouting();
					demux.receiveMessages();	
				} else {
					break;
				}
			} catch(Exception e) {
				e.printStackTrace();
				break;
			}
			mock.close();
		}
	}	
	
}