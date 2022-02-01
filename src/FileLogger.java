
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * �t�@�C���ւ̃��O�o�͗p�N���X
 */
public class FileLogger
    implements
        Logger
{
    // �C���X�^���X�ϐ��F
    PrintWriter out = null;	// �t�@�C���ւ̃��O�o�͗p PrintWriter
    
    /**
     * ��������t�@�C���֏o�͂���D
     */
    public FileLogger(String filename)
        throws IOException
    {
        boolean autoFlush = true;
        this.out = new PrintWriter(
            new FileWriter(filename), autoFlush
        );
    }
    
    /**
     * ��������t�@�C���֏o�͂���D
     */
    @Override
    public synchronized void println(String line)
    {
        this.out.println(line);
    }
    
    /**
     * �t�H�[�}�b�g���w�肵����������t�@�C���֏o�͂���D
     */
    @Override
    public synchronized void printf(String format, Object... args)
    {
        this.out.print(String.format(format, args));
    }
    
    /**
     * �s�̃��X�g���t�@�C���֏o�͂���D
     */
    @Override
    public synchronized void printlist(Collection<String> entry)
    {
        for (String line : entry) {
            this.out.println(line);
        }
        this.out.println();
    }
    
    /**
     * �t�@�C���ւ̃��O�o�͂��I������D
     */
    public synchronized void close()
    {
        try {
            if (this.out != null) {
                this.out.close();
            }
        }
        finally {
            this.out = null;
        }
    }
}
