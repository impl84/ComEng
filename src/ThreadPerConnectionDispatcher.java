
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * �R�l�N�V�������ɐV�����X���b�h���p����f�B�X�p�b�`���p�̃N���X
 */
class ThreadPerConnectionDispatcher
	implements	Dispatcher
{
	/**
	 * �R�l�N�V�������ɐV�����X���b�h�����蓖�Ă���@��p����
	 * �f�B�X�p�b�`�������J�n����D
	 */
	public void startDispatching(
		ServerSocket servSock, ProtocolFactory factory, Logger logger
	) {
		// �N���C�A���g�Ƃ̃R�l�N�V�������ɃX���b�h�𐶐����C
		// �v���g�R���̏������J�n����D
		while (true) {
			try {
				// �N���C�A���g�Ƃ̃R�l�N�V�����̊m����҂D
				Socket clntSock = servSock.accept();
				
				// �v���g�R�������p�̃C���X�^���X�𐶐�����D
				Runnable protocol = factory.createProtocol(
					clntSock, logger
				);
				// ���������C���X�^���X�� run() ���\�b�h���Ăяo��
				// �X���b�h�𐶐����C�v���g�R���̏������J�n����D
				Thread thread = new Thread(protocol);
				thread.start();
				
				// �������J�n�����X���b�h�������O�ɏo�͂���D
				logger.printf("�����J�n�i�X���b�h���F%s�j\n",
					thread.getName()
				);
			}
			catch (IOException ex) {
				logger.printf("��O�����F%s\n", ex.getMessage());
			}
		}
	}
}
