
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;

/**
 * �e��v���g�R���N���p�̃N���X
 */
public class ProtocolLauncher
{
	// �N���X�ϐ��i�萔�j�F
	static private final int BACKLOG = 128;	// TCP�R�l�N�V�����v�������p�̃L���[�̒���
	
	/**
	 * �����Ŏw�肳�ꂽ�v���g�R���ƃf�B�X�p�b�`���ɂ��C
	 * �T�[�o���̃v���g�R���������J�n����D
	 */
	public static void main(String[] args)
	{
		// �����̐����m�F����D
		if (args.length != 3) {
			System.err.println("Parameter(s): <Port> <Protocol> <Dispatcher>");
			return;
		}
		// �������牺�L(a)�`(c)���擾����D
		// (a) �T�[�o�̃|�[�g�ԍ�
		// (b) ���p����v���g�R���t�@�N�g���N���X���̐ړ���
		// (c) ���p����f�B�X�p�b�`���N���X���̐ړ���
		int servPort			= Integer.parseInt(args[0]);
		String factoryName		= args[1] + "ProtocolFactory";
		String dispatcherName	= args[2] + "Dispatcher";
		
		try {
			// TCP�̃R�l�N�V�����v�����������邽�߂̃\�P�b�g�𐶐�����D
			ServerSocket servSock = new ServerSocket(servPort, BACKLOG);
				// ��O�FIOException
			
			// �v���g�R���t�@�N�g���̃C���X�^���X���擾����D
			// ��������\���̂����O�͈ȉ��̒ʂ�...(A)
			//  forName():
			//		ClassNotFoundException
			//  getDeclaredConstructor():
			//		NoSuchMethodException
			//  newInstance():
			//		InstantiationException, 
			//		IllegalAccessException,
			//		InvocationTargetException
			ProtocolFactory factory
				= (ProtocolFactory)Class.forName(factoryName)
				.getDeclaredConstructor()
				.newInstance();
			
			// �f�B�X�p�b�`���̃C���X�^���X���擾����D
			// ��������\���̂����O�́C��L(A)�Ɠ����D
			Dispatcher dispatcher
				= (Dispatcher)Class.forName(dispatcherName)
				.getDeclaredConstructor()
				.newInstance();
			
			// �R���\�[���o�͗p�� Logger �𐶐�����D
			Logger logger = new ConsoleLogger();
			
			// �f�B�X�p�b�`�����Ńv���g�R���t�@�N�g���𗘗p���C
			// �T�[�o���̃v���g�R���������J�n����D
			dispatcher.startDispatching(servSock, factory,logger);
		}
		catch (IOException ex) {
			System.err.println("ServerSocket �̐����Ɏ��s���܂����F" + ex.getMessage());
		}
		catch (	ClassNotFoundException
			|	NoSuchMethodException
			|	InstantiationException
			|	IllegalAccessException
			|	InvocationTargetException ex
		) {
			System.err.println("�C���X�^���X�̐����Ɏ��s���܂����F" + ex.getMessage());
		}
	}
}
