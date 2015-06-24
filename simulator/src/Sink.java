import java.util.ArrayList;

import br.ufmg.iot.XBeeSerialMock;


public class Sink extends Node {
	
	public ArrayList<byte[]> replies = new ArrayList<byte[]>(); 
	
	public Sink(String name, XBeeSerialMock mock) {
		super(name, mock);
	}
	
	public void startRouting() {
		XBeeSerialMock.MockMessageOut helloMsg = new XBeeSerialMock.MockMessageOut(
				(byte)0x00, (byte)0x00,
				new byte[] { 0x00 });
		mock.send(helloMsg); // Hello message
	}
		
	public void handleRepMsg(XBeeSerialMock.MockMessageIn repMsg) {
		replies.add(new byte[] {repMsg.remoteAddressM, repMsg.remoteAddressL, repMsg.payload[6]});			
	}
}
