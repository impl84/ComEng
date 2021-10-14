
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * �\�P�b�g���Ɠ����̃X���b�h�𗘗p���ĘA���f�[�^�𑗐M����@�\�̎���
 */
class MultiThreadUploader
{
	// �C���X�^���X�ϐ��F
	private String servAddr = null;	// �T�[�o��IP�A�h���X(�܂��̓z�X�g��)
	private int servPort = 0;		// �T�[�o�̃|�[�g�ԍ�
	private int totalSize = 0;		// ���M���鑍�f�[�^�T�C�Y
	private int bufferSize = 0;		// ���M�o�b�t�@�T�C�Y
	private int numSockets = 0;		// �\�P�b�g��
	
	/**
	 * MultiThreadUploader �̃C���X�^���X�𐶐�����D
	 */
	MultiThreadUploader(String servAddr, int servPort, int totalSize, int bufferSize, int numSockets)
	{
		// ���L�̃C���X�^���X�ϐ����C�����ŏ���������D
		this.servAddr = servAddr;
		this.servPort = servPort;
		this.totalSize = totalSize;
		this.bufferSize = bufferSize;
		this.numSockets = numSockets;
	}
	
	/**
	 * ���M�f�[�^�T�C�Y���̑��M���������s����D
	 */
	void upload()
		throws	InterruptedException,
				ExecutionException
	{
		// �\�P�b�g���̑��M�f�[�^�T�C�Y�����߂�D
		// �����M�f�[�^�T�C�Y���\�P�b�g���Ŋ������]�肪����ꍇ��z�肵�C
		// ���̗]����܂ޑ��M�f�[�^�T�C�Y�����߂Ă����D
		int sizeWithoutRemainder = this.totalSize / this.numSockets;
		int sizeWithRemainder = sizeWithoutRemainder + this.totalSize % this.numSockets;
		
		// �\�P�b�g���Ɠ����̃X���b�h�𗘗p���邽�߂̃X���b�h�v�[���𐶐�����D
		int numThreads = this.numSockets;
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		
		// ���M�������\�b�h�̊������m�F���邽�߂� Future �C���X�^���X�̃��X�g�𐶐�����D
		List<Future<Integer>> futureList = new ArrayList<Future<Integer>>();
		
		// �\�P�b�g���Ɠ����̃X���b�h�֑��M�������\�b�h��n�����߂̃��[�v
		for (int i = 0; i < this.numSockets; i++) {
			// �X���b�h�v�[�����̃X���b�h�ŏ������鑗�M�f�[�^�T�C�Y�����߂�D
			// �Ō�̃X���b�h�̂݁C��L�̗]����܂ރT�C�Y�̑��M�f�[�^����������D
			int sizePerSocket;
			if (i == this.numSockets - 1) {
				sizePerSocket = sizeWithRemainder;
			}
			else {
				sizePerSocket = sizeWithoutRemainder;
			}
			// �X���b�h�v�[�����̃X���b�h�֑��M�������\�b�h��n���D
			Future<Integer> future = threadPool.submit(
				() -> uploadPerSocket(sizePerSocket),
				Integer.valueOf(sizePerSocket)
			);
			// �X���b�h�v�[�����̃X���b�h�Ŏ�������鑗�M�������\�b�h��
			// �������m�F���邽�߂� Future �C���X�^���X�����X�g�֒ǉ�����D
			futureList.add(future);
		}
		try {
			// Future �C���X�^���X�̃��X�g�𑖍����C
			// �Y������X���b�h�ɂ����鑗�M�����̊������m�F����D
			for(Future<Integer> future : futureList) {
				// ���M�������\�b�h���� RuntimeException ���������Ă����ꍇ�C
				// ���L get ���\�b�h�� ExecutionException �𓊂���D
				Integer size = future.get();
					// InterruptedException, ExecutionException
				
				// ���M�������\�b�h�����ʂƂ��ĕԂ��C
				// �\�P�b�g���̑��M�f�[�^�T�C�Y�̒l���m�F����D
				if ((size != sizeWithoutRemainder) && (size != sizeWithRemainder)) {
					throw new Error("���M�������\�b�h���\�����ʌ��ʂ�Ԃ��܂����D");
				}
			}
		}
		finally {
			// �X���b�h�v�[�����I��������D
			shutdownAndAwaitTermination(threadPool);
		}
	}
	
	/**
	 * �\�P�b�g���̑��M���������s����D
	 * ���̃��\�b�h�̓X���b�h�v�[�����̃X���b�h����Ă΂��D
	 */
	private void uploadPerSocket(int sizePerSocket)
		throws RuntimeException
	{
		// �^�����Ă���o�b�t�@�T�C�Y +4�o�C�g���̑��M�o�b�t�@�𐶐����C�����ŏ���������D
		int allocSize = this.bufferSize + 4;
		byte[] sendBuffer = new byte[allocSize];
		Random rand = new Random(System.currentTimeMillis());
		rand.nextBytes(sendBuffer);
		
		// �\�P�b�g���̑��M�f�[�^�T�C�Y�𑗐M�o�b�t�@�̐擪 4�o�C�g�֊i�[����D
		byte[] bytes = ByteBuffer.allocate(4).putInt(sizePerSocket).array();
		System.arraycopy(bytes, 0, sendBuffer, 0, 4);
		
		Socket socket = null;
		try {
			// �\�P�b�g�𐶐����C���o�̓X�g���[�����擾����D
			socket = new Socket(this.servAddr, this.servPort);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			
			boolean isFirstData = true;		// �ŏ��ɑ��M����f�[�^���ۂ��������t���O
			int remainder = sizePerSocket;	// ���M���ׂ��f�[�^�̎c��
			
			// �\�P�b�g���̑��M�f�[�^���C�o�b�t�@�T�C�Y���̃f�[�^���ɃT�[�o�֑��M����D
			for (;;) {
				// ���M�f�[�^�������߂�D
				int sendLength
					= remainder > this.bufferSize ? this.bufferSize : remainder;  
				
				// �T�[�o�փf�[�^�𑗐M����D
				if (isFirstData) {
					// �ŏ��ɑ��M����f�[�^�́C
					// �\�P�b�g���̑��M�f�[�^�T�C�Y���擪 4�o�C�g�Ɋi�[����Ă���f�[�^�D
					out.write(sendBuffer, 0, sendLength);
					isFirstData = false;
				}
				else {
					// �ŏ��ɑ��M����f�[�^�ȊO�́C�S�ė������i�[����Ă���f�[�^�D
					out.write(sendBuffer, 4, sendLength);
				}
				out.flush();
				
				// ���M���ׂ��f�[�^�̎c�ʂ� 0 �ł���΃��[�v�𔲂���D
				remainder -= sendLength;
				if (remainder <= 0) {
					break;
				}
			}
			// �T�[�o�����R�l�N�V������ؒf����܂ő҂D
			byte[] recvBuffer = new byte[4];
			while (in.read(recvBuffer) != -1) {
				;
			}
		}
		catch (IOException ex) {
			// ��O�����������ꍇ�C���̗�O���܂� RuntimeException �𓊂���D
			throw new RuntimeException(ex);
		}
		finally {
			// �\�P�b�g�����D
			try {
				if (socket != null) {
					socket.close();
				}
			}
			catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	
	/**
	 * ExecutorService ��2�i�K�ŃV���b�g�_�E������D
	 */
	private void shutdownAndAwaitTermination(ExecutorService threadPool)
	{
		// �ŏ��� shutdown ���\�b�h���Ăяo���Ē��M�^�X�N�����ۂ���D
		threadPool.shutdown();
		try {
			// ���s���̃^�X�N�̏I����҂D
			if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				// InterruptedException
				
				// ���s���̃^�X�N�̏I���O�ɑ҂����Ԃ��؂ꂽ�̂ŁC
				// ���s���̃^�X�N���ׂĂ̒�~�����݁C�ҋ@���̃^�X�N�̏������~����D
				// ���̏�ŁC�ēx�C���s���̃^�X�N�̏I����҂D
				threadPool.shutdownNow();
				if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
					// InterruptedException
					
					// ���s���̃^�X�N�̏I���O�ɑ҂����Ԃ��؂ꂽ�D
					// RuntimeException �𓊂��āC�V���b�g�_�E���������I������D
					throw new RuntimeException(
						"ExecutorService �𐳏�ɏI���ł��܂���ł����D"
					);
				}
			}
		}
		catch (InterruptedException ex) {
			// InterruptedException �����������D
			// ���s���̃^�X�N���ׂĂ̒�~�����݁C�ҋ@���̃^�X�N�̏������~����D
			threadPool.shutdownNow();
			
			// ���݂̃X���b�h�̊��荞�݃X�e�[�^�X��ێ�����D
			Thread.currentThread().interrupt();
		}
	}
}
