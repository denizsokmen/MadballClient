package Game;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import Packet.Packets;

public abstract class NetworkTCP implements Runnable {

	public static InetAddress IP;
	public DatagramSocket sock;
	private final MessageLength messageLength = new TwoByteMessageLength();
	public SocketChannel socketChannel;
	public ByteBuffer readBuffers;

	private enum State {
		STOPPED, STOPPING, RUNNING
	}

	static {
		InetAddress temp;
		try {
			temp = InetAddress.getByName("localhost");
		} catch (Exception e) {
			temp = null;
		}
		IP = temp;
	}
	private final int byteLength = 2;

	private final AtomicReference<State> state = new AtomicReference<State>(
			State.STOPPED);

	@Override
	public void run() {
		if (!state.compareAndSet(State.STOPPED, State.RUNNING)) {
			connected(true);
			return;
		}

		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true);
			socketChannel.connect(new InetSocketAddress(IP, 2593));
			socketChannel.socket().setTcpNoDelay(true);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		readBuffers=ByteBuffer.allocate(512);
		try {
			connected(false);
			while (state.get() == State.RUNNING) {
				for (ByteBuffer message : readIncomingMessage(socketChannel)) {
					messageReceived(Packets.fromByteArray(message.array()));
				}
			}
		} catch (ClosedByInterruptException ie) {

		} catch (ConnectException ce) {
			throw new RuntimeException(ce.getMessage());
		} catch (SocketException se) {

		} catch (IOException ioe) {
			throw new RuntimeException("Client failure: " + ioe.getMessage());
		} finally {
			try {
				socketChannel.close();
				state.set(State.STOPPED);
				disconnected();
			} catch (Exception e) {
			}
		}
	}

	public boolean stop() {
		if (state.compareAndSet(State.RUNNING, State.STOPPING)) {
			
			return true;
		}
		return false;
	}

	public byte[] lengthToBytes(long len) {
		if (len < 0 || len > 65535) {
			throw new IllegalStateException("");
		}
		return new byte[] { (byte) ((len >>> 8) & 0xff), (byte) (len & 0xff) };
	}

	public long bytesToLength(byte[] bytes) {
		if (bytes.length != 2) {
			throw new IllegalStateException("");
		}
		return ((0xFF & ((int) bytes[0])) << 8) + (int) (bytes[1] & 0xFF);
	}

	public synchronized boolean write(Packets pk) {
		byte[] buffer = pk.toByteArray();
		int len = buffer.length;
		byte[] lengthBytes = lengthToBytes(len);
		try {
			byte[] outBuffer = new byte[len + byteLength];
			System.arraycopy(lengthBytes, 0, outBuffer, 0, byteLength);
			System.arraycopy(buffer, 0, outBuffer, byteLength, len);
			//out.get().write(outBuffer);
			socketChannel.write(ByteBuffer.wrap(outBuffer));
			return true;
		} catch (Exception e) {
			stop();
			return false;
		}
	}
	
	private List<ByteBuffer> readIncomingMessage(SocketChannel sok)
			throws IOException {

		
		if (sok.read(readBuffers) == -1) {
			throw new IOException("Read on closed key");
		}

		readBuffers.flip();

		List<ByteBuffer> result = new ArrayList<ByteBuffer>();

		ByteBuffer msg = readMessage(readBuffers);
		while (msg != null) {
			result.add(msg);
			msg = readMessage(readBuffers);
		}

		return result;
	}

	
	//birleþik mesajlarý parçala
	private ByteBuffer readMessage(ByteBuffer readBuffer) {
		int bytesToRead;
		if (readBuffer.remaining() > messageLength.byteLength()) {
			byte[] lengthBytes = new byte[messageLength.byteLength()];
			readBuffer.get(lengthBytes);
			bytesToRead = (int) messageLength.bytesToLength(lengthBytes);
			if ((readBuffer.limit() - readBuffer.position()) < bytesToRead) {
				if (readBuffer.limit() == readBuffer.capacity()) {
					int oldCapacity = readBuffer.capacity();
					ByteBuffer tmp = ByteBuffer.allocate(bytesToRead
							+ messageLength.byteLength());
					readBuffer.position(0);
					tmp.put(readBuffer);
					readBuffer = tmp;
					readBuffer.position(oldCapacity);
					readBuffer.limit(readBuffer.capacity());
					readBuffers = readBuffer;
					return null;
				} else {
					readBuffer.position(readBuffer.limit());
					readBuffer.limit(readBuffer.capacity());
					return null;
				}
			}
		} else {
			readBuffer.position(readBuffer.limit());
			readBuffer.limit(readBuffer.capacity());
			return null;
		}
		byte[] resultMessage = new byte[bytesToRead];
		readBuffer.get(resultMessage, 0, bytesToRead);
		int remaining = readBuffer.remaining();
		readBuffer.limit(readBuffer.capacity());
		readBuffer.compact();
		readBuffer.position(0);
		readBuffer.limit(remaining);
		return ByteBuffer.wrap(resultMessage);
	}

	/**
	 * Serverdan tam bir paket geldiðinde
	 */
	protected abstract void messageReceived(Packets packets);

	/**
	 * servera baðlanýldýðýnda, zaten baðlý olunan bir servera tekrar
	 * baðlanýlýrsa true olacak
	 */
	protected abstract void connected(boolean alreadyConnected);

	/**
	 * server ile baðlantý kesildiðinde
	 */
	protected abstract void disconnected();
}
