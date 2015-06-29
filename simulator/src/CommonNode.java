import java.util.Timer;
import java.util.TimerTask;

import br.ufmg.iot.XBeeSerialMock;

public class CommonNode extends Node {
	public byte[] parent = null;
	private Timer timer;
	
	public CommonNode(String name, XBeeSerialMock mock) {
		super(name, mock);
	}
	
	public void handleHelloMessage(XBeeSerialMock.MockMessageIn helloMsg) throws InterruptedException {
		super.handleHelloMessage(helloMsg);
		if (parent == null) {
			parent = new byte[] {helloMsg.myAddressL, helloMsg.myAddressM};
			// enter data phase
			timer = new Timer();
			timer.schedule(new ResetTask(), Constants.beta);
			XBeeSerialMock.MockMessageOut newHelloMsg = new XBeeSerialMock.MockMessageOut(
					(byte)0x00, (byte)0x02, 
					helloMsg.payload); 
			mock.send(newHelloMsg);
			Thread.sleep(500);
			XBeeSerialMock.MockMessageOut ackMsg = new XBeeSerialMock.MockMessageOut(
					(byte)0x00, (byte)0x02, 
					new byte[] { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }); 
			mock.send(ackMsg);
			Thread.sleep(500);		
		}	
	}
		
    private class ResetTask extends TimerTask {
        public void run() {
        	parent = null;
            timer.cancel();
        }
    }
	
	public void handleReqMessage(XBeeSerialMock.MockMessageIn reqMsg) throws InterruptedException {
		super.handleReqMessage(reqMsg);
		if (parent != null && reqMsg.myAddressL == parent[0] && reqMsg.myAddressM == parent[1]) {
			XBeeSerialMock.MockMessageOut newReqMsg = new XBeeSerialMock.MockMessageOut(
					(byte)0x00, (byte)0x02, reqMsg.payload); 
			mock.send(newReqMsg);
			Thread.sleep(500);	
			XBeeSerialMock.MockMessageOut repMsg = new XBeeSerialMock.MockMessageOut(
					(byte)0x00, (byte)0x02, 
					new byte[] { 0x03, 0x01, 0x00, 0x02, parent[0], parent[1], 0x0A }); 
			mock.send(repMsg);
			Thread.sleep(500);			
		}
	}
	
	public void handleRepMsg(XBeeSerialMock.MockMessageIn repMsg) throws InterruptedException {
		super.handleRepMessage(repMsg);
		if (parent != null) {
			mock.send(new XBeeSerialMock.MockMessageOut((byte)0x00, (byte)0x02, repMsg.payload));	
			Thread.sleep(500);				
		}
	}
}
