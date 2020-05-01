import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class UDPLoggerClient {
	
	private final int loggerServerPort;
	private final int processId;
	private final int timeout;

	/**
	 * @param loggerServerPort the UDP port where the Logger process is listening o
	 * @param processId the ID of the Participant/Coordinator, i.e. the TCP port where the Participant/Coordinator is listening on
	 * @param timeout the timeout in milliseconds for this process 
	 */
	public UDPLoggerClient(int loggerServerPort, int processId, int timeout) {
		this.loggerServerPort = loggerServerPort;
		this.processId = processId;
		this.timeout = timeout;
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

//		new Thread(()-> log(message)).start();
		//This threaded version of the log doesnt work at the moment becuae I have to work out how to get it to throw the IOException.
//		Thread thread = new Thread(() -> log(message))
		log(message);

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

		// YOUR IMPLEMENTATION HERE!!
		
	}

	private void log(String message) throws IOException {
		byte[] buf = "ACK".getBytes();
		DatagramSocket socket = new DatagramSocket(loggerServerPort);
//		ServerSocket.send(new DatagramPacket(buf, buf.length, socket.getInetAddress(), socket.getLocalPort()));
	}
}
