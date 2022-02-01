
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * UploadProtocol �̃C���X�^���X�𐶐����邽�߂̃t�@�N�g��
 */
public class UploadProtocolFactory
    implements
        ProtocolFactory
{
    /**
     * UploadProtocol �̃C���X�^���X�𐶐����CRunnable �C���^�[�t�F�[�X�Ƃ��ĕԂ��D
     */
    @Override
    public Runnable createProtocol(Socket clntSock, Logger logger)
    {
        return new UploadProtocol(clntSock, logger);
    }
}

/**
 * �A���f�[�^����M����T�[�o���̋@�\�̎���
 */
class UploadProtocol
    implements
        Runnable
{
    // �N���X�ϐ��i�萔�j�F
    static private final int BUF_SIZE = 8192;	// ��M�o�b�t�@�T�C�Y
    
    // �C���X�^���X�ϐ��F
    private Socket clntSock = null;	// �N���C�A���g�ƒʐM���邽�߂̃\�P�b�g
    private Logger logger   = null;	// ���O�o�͗p�� Logger �C���X�^���X
    
    /**
     * UploadProtocol �̃C���X�^���X�𐶐�����D
     */
    public UploadProtocol(Socket clntSock, Logger logger)
    {
        this.clntSock = clntSock;
        this.logger = logger;
    }
    
    /**
     * �N���C�A���g���v������T�C�Y���̃f�[�^����M����D
     */
    @Override
    public void run()
    {
        try {
            // �\�P�b�g������̓X�g���[�����擾����
            InputStream in = clntSock.getInputStream();
            // IOException
            
            // ���̃\�P�b�g�����M����ŏ��� 4�o�C�g�ɂ́C
            // �\�P�b�g���Ɏ�M���ׂ��f�[�^�T�C�Y���i�[����Ă���̂ŁC��������߂�D
            byte[] fourBytes = new byte[4];
            int    bytesRcvd = in.read(fourBytes);
            // IOException
            if (bytesRcvd != 4) {
                throw new IOException("�\�P�b�g���Ɏ�M���ׂ��f�[�^�T�C�Y�̎擾�Ɏ��s���܂����D");
            }
            int totalSizePerSocket = ByteBuffer.wrap(fourBytes).getInt();
            
            // �N���C�A���g�����M�����S�f�[�^�T�C�Y���i�[����ϐ���p�ӂ���D
            // ���Ɏ�M���� 4(�o�C�g)�������l�Ƃ��đ�����Ă����D
            int totalBytesRcvd = 4;
            
            // ��M�o�b�t�@�𐶐�����D
            byte[] recvBuffer = new byte[BUF_SIZE];
            
            // ���̃\�P�b�g�𗘗p���Ď�M���ׂ��f�[�^��S�Ď�M����܂ŁC
            // ��M�������J��Ԃ��D
            while (totalBytesRcvd < totalSizePerSocket) {
                // �f�[�^����M����D
                bytesRcvd = in.read(recvBuffer);
                // IOException
                
                // �N���C�A���g���\�P�b�g������ꍇ�̓��[�v�𔲂���D
                if (bytesRcvd == -1) {
                    break;
                }
                // �\�P�b�g���Ɏ�M���ׂ��f�[�^�T�C�Y�ɁC
                // �����M�����f�[�^�̃o�C�g����������D
                totalBytesRcvd += bytesRcvd;
            }
        }
        catch (IOException ex) {
            this.logger.println("��O�����F" + ex.getMessage());
        }
        finally {
            try {
                // �\�P�b�g�����D
                this.clntSock.close();
                // IOException
            }
            catch (IOException ex) {
                this.logger.println("��O�����F" + ex.getMessage());
            }
        }
    }
}
