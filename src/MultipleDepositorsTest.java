/**
 * 10�l�̗a���҂����ꂼ��1���~��1�̋�s�����֗a������@�\�̎���
 */
public class MultipleDepositorsTest
{
    // �a���҂̐l��
    private final static int NUM_OF_DEPOSITORS = 10;
    
    // 10�l�̗a���҂����ꂼ��1���~��1�̋�s�����֗a������D
    public static void main(String args[])
    {
        // ��s������1��������D
        Account account = new Account();
        
        // �a���Җ��̗a�����������s����X���b�h�̔z��𐶐�����D
        Thread threads[] = new Thread[10];
        
        // �a����1�l�ɂ�1�̃X���b�h�����蓖�āC
        // ���ꂼ��̃X���b�h�ɂ����ėa���������J�n����D
        for (int i = 0; i < NUM_OF_DEPOSITORS; i++) {
            // �a���҂ƁC�a�����������s���邽�߂̃X���b�h�𐶐�����D
            Depositor depositor = new Depositor(account);
            threads[i] = new Thread(depositor);
            
            // �X���b�h�̏������J�n����D
            // �������J�n�����X���b�h��������́C
            // �a���҂̗a�������irun ���\�b�h�j���Ăяo�����D
            // �����ŌĂяo�� start ���\�b�h�͑������A����D
            threads[i].start();
        }
        // ���������S�ẴX���b�h�̏I����҂D
        for (Thread thread : threads) {
            try {
                thread.join();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        // �a�����ʂ�\������D
        System.out.println(account.getBalance());
    }
}

/**
 * ��s����
 */
class Account
{
    // �����c��   
    private int balance = 0;
    
    // �a������
    void deposit(int amount)
    {
        // �����c���ɗa���z��������D
        balance += amount;
    }
    
    // �����c�����擾����D
    int getBalance()
    {
        return balance;
    }
}

/**
 * �a����
 */
class Depositor
    implements
        Runnable
{
    // ��s����
    private final Account account;
    
    // �a���҂𐶐�����D
    Depositor(Account account)
    {
        // �^����ꂽ��s������ێ�����D
        this.account = account;
    }
    
    // ��s�����֗a������D
    //
    // Runnable �C���^�[�t�F�[�X�ɂ����� run ���\�b�h�̎����F
    // ���̗a���҂ɑΉ�����X���b�h����Ă΂�邱�Ƃ�z�肵�Ă���D
    @Override
    public void run()
    {
        // ��s�����ւ̗a���� 10000 ��J��Ԃ��D
        for (int i = 0; i < 10000; i++) {
            // ��s������ 1�~���a������D
            this.account.deposit(1);
        }
    }
}
