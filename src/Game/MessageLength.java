package Game;
public interface MessageLength {

	int byteLength();
	long maxLength();
	long bytesToLength(byte[] bytes);
	byte[] lengthToBytes(long length);

}