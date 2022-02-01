
import java.util.Collection;

/**
 * �R���\�[���ւ̃��O�o�͗p�N���X
 */
public class ConsoleLogger
    implements
        Logger
{
    /**
     * ��������R���\�[���֏o�͂���D
     */
    @Override
    public synchronized void println(String line)
    {
        System.out.println(line);
    }
    
    /**
     * �t�H�[�}�b�g���w�肵����������R���\�[���֏o�͂���D
     */
    @Override
    public synchronized void printf(String format, Object... args)
    {
        System.out.print(String.format(format, args));
    }
    
    /**
     * �s�̃��X�g���R���\�[���֏o�͂���D
     */
    @Override
    public synchronized void printlist(Collection<String> entry)
    {
        for (String line : entry) {
            System.out.println(line);
        }
        System.out.println();
    }
}
