
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * EchoProtocol �̃C���X�^���X�𐶐����邽�߂̃t�@�N�g���N���X
 */
public class EchoProtocolFactory
    implements
        ProtocolFactory
{
    /**
     * EchoProtocol �̃C���X�^���X�𐶐����C Runnable �C���^�[�t�F�[�X�Ƃ��ĕԂ��D
     */
    @Override
    public Runnable createProtocol(Socket clntSock, Logger logger)
    {
        return new EchoProtocol(clntSock, logger);
    }
}

/**
 * �G�R�[�v���g�R���̃T�[�o���̋@�\�����������N���X
 */
class EchoProtocol
    implements
        Runnable
{
    // �N���X�ϐ��i�萔�j�F
    static public final int BUFSIZE = 256;	// �G�R�[�f�[�^�i�[�p�o�b�t�@�T�C�Y
    
    // �C���X�^���X�ϐ��F
    private Socket clntSock = null;	// �N���C�A���g�ƒʐM���邽�߂̃\�P�b�g
    private Logger logger   = null;	// ���O�o�͗p�� Logger �C���X�^���X
    
    /**
     * EchoProtocol �̃C���X�^���X�𐶐�����D
     */
    public EchoProtocol(Socket clntSock, Logger logger)
    {
        this.clntSock = clntSock;
        this.logger = logger;
    }
    
    /**
     * �N���C�A���g����f�[�^����M���C�����f�[�^���N���C�A���g�֑���Ԃ��D
     */
    @Override
    public void run()
    {
        // ���̃X���b�h�ɂ����鏈�����ʂ�ێ����邽�߂�
        // ������̃��X�g�i���O���X�g�j�𐶐�����D
        ArrayList<String> logList = new ArrayList<String>();
        
        // �X���b�h���C�N���C�A���g�̃A�h���X�ƃ|�[�g�ԍ���
        // ���O���X�g�ɒǉ�����D
        logList.add("���X���b�h�F" + Thread.currentThread().getName());
        logList.add(
            "�E�N���C�A���g�F"
                + this.clntSock.getInetAddress().getHostAddress() + "�C"
                + this.clntSock.getPort()
        );
        
        try {
            // �\�P�b�g������o�̓X�g���[�����擾����D
            InputStream  in  = this.clntSock.getInputStream();
            OutputStream out = this.clntSock.getOutputStream();
            
            // �G�R�[�f�[�^�i�[�p�o�b�t�𐶐�����D
            byte[] echoBuffer = new byte[BUFSIZE];
            
            // ��M�������b�Z�[�W�̃T�C�Y�ƁC
            // �N���C�A���g�ւ̑����M�o�C�g��
            int recvMsgSize      = 0;
            int totalBytesEchoed = 0;
            
            // �N���C�A���g�����M�����f�[�^��
            // �G�R�[�f�[�^�i�[�p�o�b�t�Ɋi�[���C
            // ���̃f�[�^�����̂܂܃N���C�A���g�֑���Ԃ��D
            // �R�l�N�V�������ؒf�����܂ŁC���̏������J��Ԃ��D
            while ((recvMsgSize = in.read(echoBuffer)) != -1) {
                out.write(echoBuffer, 0, recvMsgSize);
                totalBytesEchoed += recvMsgSize;
            }
            // �����M�o�C�g�������O���X�g�ɒǉ�����D
            logList.add("�E�����M�o�C�g���F" + totalBytesEchoed);
        }
        catch (IOException e) {
            logList.add("�E��O�����F" + e.getMessage());
        }
        finally {
            try {
                // �\�P�b�g�����D
                this.clntSock.close();
            }
            catch (IOException ex) {
                logList.add("�E��O�����F" + ex.getMessage());
            }
        }
        // ���O���X�g���܂Ƃ߂ďo�͂���D
        logger.printlist(logList);
    }
}
