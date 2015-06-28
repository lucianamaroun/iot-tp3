import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import br.ufmg.iot.XBeeSerialMock;


public class MainSimulator {
	private static boolean route = false;
	private static Timer timer;
	
	public static void main(String[] args) {
		XBeeSerialMock mock = new XBeeSerialMock("/dev/ttyUSB0", 7);
		mock.initialize();
		Sink sink = new Sink("Sink", mock);
		CommonNode nodeB = new CommonNode("Node B", mock);
		Demultiplexer demux = new Demultiplexer(sink, nodeB, mock);
		
		String answer;
		while (true) {
			if (!route) {
				System.out.println("Digite Y para iniciar a Fase de Roteamento ou N para sair.");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				try {
					answer = reader.readLine().trim();
					if (answer.equals("Y")) {
						sink.startRouting();
					} else {
						break;
					}
				} catch(Exception e) {
					e.printStackTrace();
					break;
				}	
			} else {
				System.out.println("Digite Y para enviar uma nova requisição ou N para sair.");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				try {
					answer = reader.readLine().trim();
					if (answer.equals("Y")) {
						sink.startRequest();
						timer = new Timer();
						timer.schedule(new ResetTask(), Constants.beta);
					} else {
						break;
					}
				} catch(Exception e) {
					e.printStackTrace();
					break;
				}
			}
			demux.receiveMessages();
			try {
				Thread.sleep(1000); // avoid run too rapidly between iterations
			} catch(Exception e) {
				e.printStackTrace();
				break;
			}
		}
		mock.close();
	}
	
	private static class ResetTask extends TimerTask {
        public void run() {
        	route = false;
        	timer.cancel();
        }
    }
	
}