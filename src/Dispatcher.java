
import java.net.ServerSocket;

/**
 * �f�B�X�p�b�`���p�̃C���^�[�t�F�[�X
 */
public interface Dispatcher
{
    /**
     * �f�B�X�p�b�`�������J�n����D
     */
    public void startDispatching(
        ServerSocket servSock, ProtocolFactory protoFactory, Logger logger
    );
}
