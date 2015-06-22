
public class MainSimulator {
	public static Sink sink;
	public static Node nodeB;
	
	public static void main(String[] args) {
		sink = new Sink();
		nodeB = new Node();
		sink.startRouting();
		sink.receiveAcks();
		nodeB.receiveHello();
		nodeB.receiveAcks();
		sink.startRequest();
		nodeB.receiveRequests();
		nodeB.receiveReplies();
		sink.receiveReplies();
	}	
	
}