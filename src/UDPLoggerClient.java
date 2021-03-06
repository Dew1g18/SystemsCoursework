import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPLoggerClient {
	
	private final int loggerServerPort;
	private final int processId;
	private final int timeout;
	private DatagramSocket sender;

	/**
	 * @param loggerServerPort the UDP port where the Logger process is listening o
	 * @param processId the ID of the Participant/Coordinator, i.e. the TCP port where the Participant/Coordinator is listening on
	 * @param timeout the timeout in milliseconds for this process 
	 */
	public UDPLoggerClient(int loggerServerPort, int processId, int timeout) {
		this.loggerServerPort = loggerServerPort;
		this.processId = processId;
		this.timeout = timeout;
		try {
			this.sender = new DatagramSocket();
		}catch (IOException e){
			System.out.println("LOGGER CLIENT INITIALISATION FAIL");
			e.printStackTrace();
		}
	}
	
	public int getLoggerServerPort() {
		return loggerServerPort;
	}

	public int getProcessId() {
		return processId;
	}
	
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sends a log message to the Logger process
	 * 
	 * @param message the log message
	 * @throws IOException
	 */
	public void logToServer(String message) throws IOException {

		//Not threaded
		for (int i=0;i<2;i++){
			try{
				if (receive(log(message))) {
					break;
				}
			}catch (IOException e){
				continue;
			}
		}
		receive(log(message));


	}

//
//	private void tryLog(String message){
//		for (int i=0;i<3;i++){
//			try{
//				if (receive(log(message))) {
//					break;
//				}
//			}catch (IOException e){
//				continue;
//			}
//		}
//	}

	/**
	 * 	Checklist:
	 * 		-send messages via udp to the logger server
	 * 		-receive ACK reciepts
	 * 		-send duplicate if ACk not received in time
	 *
	 * 		-Hold the same connection? Probably not, close at the end of this method as it will be called
	 * 			every time someone wants to log something to the server
	 * 		-Do this in a new thread every time it is run?
	 * 			I say this because I don't want it holding up other processes, will have to look into how
	 * 			the loggers work to know if this is necessary.
	 */

	private boolean receive(DatagramSocket socket) throws IOException{
		byte[] buff = new byte[256];
		try{
			Thread.sleep(10);
		}catch(InterruptedException e){
			//never gets caught
		}
		socket.setSoTimeout(getTimeout());
		socket.receive(new DatagramPacket(buff, buff.length));
//		System.out.println("ACK");
		return true;
	}


	private DatagramSocket log(String message) throws IOException {
		byte[] buf = message.getBytes();
		sender.send(new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(),getLoggerServerPort()));
		return sender;
	}
}
