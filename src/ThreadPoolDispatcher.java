
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * �X���b�h�v�[���𗘗p����f�B�X�p�b�`���p�̃N���X
 */
class ThreadPoolDispatcher
	implements	Dispatcher
{
	// �N���X�ϐ��i�萔�j�F
	static final int NUM_THREADS = 8;	// �X���b�h�v�[�����̃X���b�h��
	
	/**
	 * �X���b�h�v�[���ɂ��f�B�X�p�b�`�������J�n����D
	 */
	public void startDispatching(
		ServerSocket servSock, ProtocolFactory factory, Logger logger
	) {
		// (NUM_THREADS - 1) �̃X���b�h�𐶐��E�J�n����D
		for (int i = 0; i < (NUM_THREADS - 1); i++) {
			// dispatchLoop() ���\�b�h���Ăяo�����߂̃X���b�h�𐶐�����D
			// ��Thread �̃R���X�g���N�^�ɂ̓����_���𗘗p���Ă���D
			//   ���̃����_���́C���� Runnable �C���X�^���X�Ɠ����F
			//		new Runnable() {
			//			public void run() {
			//				dispatchLoop(servSock, factory, logger);
			//			}
			//		}
			Thread thread = new Thread(
				() -> dispatchLoop(servSock, factory, logger)
			);
			// dispatchLoop() ���\�b�h���Ăяo�����߂̃X���b�h���J�n����D
			thread.start();
		}
		// main �X���b�h(���̏��������s���Ă���X���b�h)��
		// NUM_THREADS �Ԗڂ̃X���b�h�Ƃ��āC
		// dispatchLoop() ���\�b�h���Ăяo���D
		dispatchLoop(servSock, factory, logger);
	}
	
	/**
	 * �N���C�A���g����̗v�����J��Ԃ���������D
	 */
	private void dispatchLoop(
		ServerSocket servSock, ProtocolFactory factory, Logger logger
	) {
		// ���̃��\�b�h���Ăяo���Ă���X���b�h�����擾����D
		String threadName = Thread.currentThread().getName();
		
		// ���̃��\�b�h���Ăяo���Ă���X���b�h�������O�ɏo�͂���D
		logger.printf("�����J�n�i�X���b�h���F%s�j\n", threadName);

		// �N���C�A���g����̗v�����J��Ԃ���������D
		while (true) {
			try {
				// �N���C�A���g�Ƃ̃R�l�N�V�����̊m����҂D
				Socket clntSock = servSock.accept();
				
				// �v���g�R�������p�̃C���X�^���X�𐶐�����D
				Runnable protocol = factory.createProtocol(
					clntSock, logger
				);
				// �v���g�R���̏��������̃X���b�h�Ŏ��s����D
				protocol.run();
			}
			catch (IOException ex) {
				logger.printf("��O�����i%s�j�F%s\n",
					threadName, ex.getMessage()
				);
			}
		}
	}
}
