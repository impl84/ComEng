
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * �G�R�[�N���C�A���g
 */
public class SimpleEchoClient
{
    /**
     * �G�R�[�N���C�A���g�𗘗p���邽�߂� main ���\�b�h
     */
    public static void main(String[] args)
    {
        // �����̐����m�F����D
        if ((args.length < 2) || (args.length > 3)) {
            System.out.println("Parameters: <Server> <Port> <Word>");
            return;
        }
        // �T�[�o��(�܂���IP�A�h���X)�ƃT�[�o�̃|�[�g�ԍ��C
        // �G�R�[���������������擾����D
        String servAddr   = args[0];
        int    servPort   = Integer.parseInt(args[1]);
        String echoString = args[2];
        
        SimpleEchoClient client = null;
        try {
            // �G�R�[�N���C�A���g�̃C���X�^���X�𐶐�����D
            client = new SimpleEchoClient(servAddr, servPort);
            
            // ��������T�[�o�֑��M���C�������������M����D
            client.processEchoString(echoString);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                // �G�R�[�N���C�A���g���I������D
                if (client != null) {
                    client.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    // �C���X�^���X�ϐ��F
    private Socket       echoSocket = null;
    private InputStream  in         = null;
    private OutputStream out        = null;
    
    /**
     * SimpleEchoClient �̃C���X�^���X�𐶐�����D
     */
    SimpleEchoClient(String servAddr, int servPort)
        throws IOException
    {
        // �T�[�o�Ƃ̃R�l�N�V�������m������D
        this.echoSocket = new Socket(servAddr, servPort);
        System.out.println("�T�[�o�Ƃ̃R�l�N�V�������m�����܂����D");
        
        // �\�P�b�g������o�̓X�g���[�����擾����D
        this.in = this.echoSocket.getInputStream();
        this.out = this.echoSocket.getOutputStream();
    }
    
    /**
     * �^����ꂽ��������T�[�o�֑��M���C ���M�����o�C�g���Ɠ����o�C�g���̃f�[�^����M����D
     */
    void processEchoString(String echoString)
        throws IOException
    {
        // ���M���镶������o�C�g�f�[�^�ɕϊ����C
        // ���̃o�C�g�f�[�^���T�[�o�֑��M����D
        byte[] byteBuffer = echoString.getBytes();
        this.out.write(byteBuffer);
        System.out.println("���M������F" + echoString);
        
        // ���̌�ɃT�[�o�����M���鑍�o�C�g��
        int totalBytesRcvd = 0;
        
        // �T�[�o�����M�������o�C�g�����C
        // ���M�����o�C�g�������ŗL������M�𑱂���D
        while (totalBytesRcvd < byteBuffer.length) {
            
            // �T�[�o����o�C�g�f�[�^����M����D
            int bytesRcvd = this.in.read(
                byteBuffer,		// �o�C�g�f�[�^���i�[����o�b�t�@
                totalBytesRcvd,	// �i�[����ꏊ�ƂȂ�I�t�Z�b�g�l
                byteBuffer.length - totalBytesRcvd	// ��M���ׂ��o�C�g��
            );
            // InputStream.read() �̖߂�l���m�F����D
            if (bytesRcvd >= 0) {
                // �߂�l�� 0 �ȏ�̏ꍇ�́C
                // ��M�����o�C�g����\���Ă���̂ŁC
                // �T�[�o�����M�������o�C�g���ɉ��Z����D
                totalBytesRcvd += bytesRcvd;
            }
            else if (bytesRcvd == -1) {
                // �߂�l�� -1 �̏ꍇ�́C
                // �X�g���[���̍Ō�ɓ��B���Ă��邱�Ƃ�\���D
                // �T�[�o���R�l�N�V������ؒf�����ꍇ -1 ���߂�l�ƂȂ�D
                // �����ł́C��M���ׂ��o�C�g�f�[�^�S�Ă���M����O�ɁC
                // �R�l�N�V�������ؒf���ꂽ���Ƃ��Ӗ�����D
                throw new IOException(
                    "�T�[�o���R�l�N�V������ؒf���܂����D"
                );
            }
            else {
                // �߂�l����L�ȊO�̏ꍇ�͒�`����Ă��Ȃ��D
                // �����C�߂�l�� -1 �����̏ꍇ�� Error �Ƃ��ď�������D
                throw new Error(
                    "InputStream.read() �� "
                        + bytesRcvd + " ��߂�l�Ƃ��ĕԂ��܂����D"
                );
            }
        }
        System.out.println("��M������F" + new String(byteBuffer));
    }
    
    /**
     * �G�R�[�N���C�A���g���I������D
     */
    void close()
        throws IOException
    {
        try {
            if (this.echoSocket != null) {
                this.echoSocket.close();
            }
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
        }
        finally {
            this.echoSocket = null;
            this.in = null;
            this.out = null;
        }
    }
}
