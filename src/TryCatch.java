
public class TryCatch
{
    public static void main(String[] args)
    {
        int result = 1;
        try {
            System.out.println("> try catch finally �̎���");
            result /= 0;
        }
        catch (ArithmeticException ex) {
            System.out.println("> catch �u���b�N�̒� (ArithmeticException)");
            //return;
            //throw ex;
            //System.exit(-1);
            //throw new IOException("��O�����H");
        }
        catch (Exception ex) {
            System.out.println("> catch �u���b�N�̒� (Exception)");
        }
        finally {
            System.out.println("> finally �u���b�N�̒�");
        }
        System.out.println("> main ���\�b�h�̍Ō�F" + result);
    }
}
