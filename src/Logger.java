
import java.util.Collection;

/**
 * ���O�o�͗p�̃C���^�[�t�F�[�X
 */
public interface Logger
{
	/**
	 * ��������o�͂���D
	 */
	public void println(String line);
	
	/**
	 * �t�H�[�}�b�g���w�肵����������o�͂���D
	 */
	public void printf(String format, Object... args);
	
	/**
	 * �s�̃��X�g���o�͂���D
	 */
	public void printlist(Collection<String> entry);
}
