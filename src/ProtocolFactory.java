
import java.net.Socket;

/**
 * �v���g�R���t�@�N�g���p�̃C���^�[�t�F�[�X
 */
public interface ProtocolFactory
{
    /**
     * �v���g�R�������p�̃C���X�^���X�𐶐����C Runnable �C���^�[�t�F�[�X�Ƃ��ĕԂ��D
     */
    public Runnable createProtocol(Socket clntSock, Logger logger);
}
