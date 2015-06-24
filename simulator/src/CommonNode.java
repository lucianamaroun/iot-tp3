import br.ufmg.iot.XBeeSerialMock;

public class CommonNode extends Node {
	public byte[] parent = null;
	public XBeeSerialMock mock; 

	public CommonNode(String name, XBeeSerialMock mock) {
		super(name, mock);
	}
	
	public void handleHelloMessage(XBeeSerialMock.MockMessageIn helloMsg) {
		super.handleHelloMessage(helloMsg);
		if (parent == null)
			parent = new byte[] {helloMsg.myAddressM, helloMsg.myAddressL};
	}
	
	public void handleReqMsg(XBeeSerialMock.MockMessageIn reqMsg) {
		if (reqMsg.myAddressM == parent[0] && reqMsg.myAddressL == parent[1]) {
			XBeeSerialMock.MockMessageOut repMsg = new XBeeSerialMock.MockMessageOut(
					(byte)0x00, (byte)0x01, 
					new byte[] { 0x03, 0x00, 0x00, 0x01, parent[0], parent[1], 0x0A }); 
			mock.send(repMsg);			
		}
	}
	
	public void handleRepMsg(XBeeSerialMock.MockMessageIn repMsg) {
		mock.send(new XBeeSerialMock.MockMessageOut(parent[0], parent[1], repMsg.payload));			
	}
}
