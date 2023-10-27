
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * ���[���̑��M����
 */
public class MailSendingTest
{
    // SMTP�T�[�o�̃|�[�g�ԍ�
    private static final int SMTP_PORT = 25;
    
    // ���M���郁�[���� Subject �Ɩ{��
    private static final String SUBJECT = "�d�q���[���̑��M�e�X�g";
    private static final String[] DATA_LINES = {
        "SMTP(Simple Mail Transfer Protocol)�̎d�l�ɉ����A",
        "SMTP�T�[�o�Ƒo�����̒ʐM���s�����ƂŁA",
        "�d�q���[���𑗐M�ł��܂��B"
    };
    
    /**
     * ExperimentalSmtpSocket �𗘗p���ă��[���𑗐M����D
     */
    public static void main(String args[])
    {
        // �����̐����m�F����D
        if ((args.length < 2) || (args.length > 3)) {
            System.out.println("Parameters: <SMTP Server> <From> <To>");
            return;
        }
        String smtpServer = args[0];    // SMTP�T�[�o��(�܂���IP�A�h���X)
        String from       = args[1];    // ���M�����[���A�h���X
        String to         = args[2];    // ���惁�[���A�h���X
        
        ExperimentalSmtpSocket smtpSocket = null;
        try {
            // ExperimentalSmtpSocket �̃C���X�^���X�𐶐�����D
            smtpSocket = new ExperimentalSmtpSocket(smtpServer, SMTP_PORT);
            
            // ���[���𑗐M����D
            smtpSocket.sendMail(from, to, SUBJECT, DATA_LINES);
        }
        catch (UnknownHostException ex) {
            // ExperimentalSmtpSocket �̃C���X�^���X�������ɗ�O�����������D
            ex.printStackTrace();
        }
        catch (IOException ex) {
            // ���[�����M���ɗ�O�����������D
            ex.printStackTrace();
        }
        finally {
            try {
                // ���p���� ExperimentalSmtpSocket ���I������D
                if (smtpSocket != null) {
                    smtpSocket.close();
                }
            }
            catch (IOException ex) {
                // ExperimentalSmtpSocket �I���������ɗ�O�����������D
                ex.printStackTrace();
            }
        }
    }
}

/**
 * SMTP�̎����p�\�P�b�g
 */
class ExperimentalSmtpSocket
{
    private final Socket socket;        // SMTP�T�[�o�Ƃ�TCP�R�l�N�V�����̒[�_�ƂȂ�\�P�b�g
    private final BufferedReader reader;// �s�P�ʂŎ�M���������邽�߂� BufferedReader
    private final PrintWriter writer;   // �s�P�ʂő��M���������邽�߂� PrintWriter
    
    /**
     * ExperimentalSmtpSocket �̃C���X�^���X�𐶐�����D
     */
    ExperimentalSmtpSocket(String smtpServer, int smtpPort)
        throws UnknownHostException,
            IOException
    {
        // SMTP�T�[�o�Ƃ�TCP�R�l�N�V�������m�����C
        // ���̒[�_�ƂȂ�\�P�b�g�̃C���X�^���X�𐶐�����D
        this.socket = new Socket(
            InetAddress.getByName(smtpServer), smtpPort
        );
        // �s�P�ʂŎ�M���������邽�߂� BufferedReader �𐶐�����D
        this.reader = new BufferedReader(
            new InputStreamReader(this.socket.getInputStream())
        );
        // �s�P�ʂő��M���������邽�߂� PrintWriter �𐶐�����D
        this.writer = new PrintWriter(
            new OutputStreamWriter(this.socket.getOutputStream()),
            true    // auto flush �@�\��L���ɂ���D
        );
    }
    
    /**
     * SMTP�̎菇�ɉ����ă��[���𑗐M����D
     */
    void sendMail(String from, String to, String subject, String[] dataLines)
        throws IOException
    {
        // SMTP�T�[�o�Ƃ�TCP�R�l�N�V�����m������ɁA
        // �T�[�o���瑗�M����Ă��郁�b�Z�[�W����M����D
        recvLine();
        
        // �ʐM�J�n�R�}���h�𑗐M���C���̉�������M����D
        String localHostName = InetAddress.getLocalHost().getHostName();
        sendLine("HELO " + localHostName);
        recvLine();
        
        // ���M�҂̃��[���A�h���X�𑗐M���C���̉�������M����D
        sendLine("MAIL From:<" + from + ">");
        recvLine();
        
        // ��M�҂̃��[���A�h���X�𑗐M���C���̉�������M����D
        sendLine("RCPT TO:<" + to + ">");
        recvLine();
        
        // �d�q���[�����M�J�n�R�}���h�𑗐M���C���̉�������M����D
        sendLine("DATA");
        recvLine();
        
        // �d�q���[���̃w�b�_�s�𑗐M����D
        sendLine("To: " + to);
        sendLine("Subject: " + subject);
        
        // �w�b�_�s�Ɩ{���𕪂����s�𑗐M����D
        sendLine("");
        
        // �d�q���[���̖{���𑗐M����D
        for (String line : dataLines) {
            sendLine(line);
        }
        // �d�q���[�����M�I���R�}���h(".")�𑗐M���C���̉�������M����D
        sendLine(".");
        recvLine();
        
        // �I���R�}���h�𑗐M���C���̉�������M����D
        sendLine("QUIT");
        recvLine();
    }
    
    /**
     * ExperimentalSmtpSocket ���I������D
     */
    void close()
        throws IOException
    {
        try {
            // �s�P�ʂő���M���������邽�߂ɗ��p���Ă���
            // PrintWriter �� BufferedReader ���I������D
            this.writer.close();
            this.reader.close();
        }
        catch (IOException ex) {
            // BufferedReader �� close ���\�b�h�ŗ�O�����������D
            ex.printStackTrace();
        }
        finally {
            // SMTP�T�[�o�Ƃ�TCP�R�l�N�V������
            // �[�_�Ƃ��ė��p���Ă����\�P�b�g���I������D
            this.socket.close();
        }
    }
    
    // ���[���T�[�o�� 1�s���M���A���M���� 1�s��W���o�͂֏o�͂���D
    private void sendLine(String line)
    {
        this.writer.println(line);
        System.out.println(line);
    }
    
    // ���[���T�[�o���� 1�s��M���C��M���� 1�s��W���o�͂֏o�͂���D
    private void recvLine()
        throws IOException
    {
        String line = this.reader.readLine();
        System.out.println(line);
    }
}
