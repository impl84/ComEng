
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * ���M�o�b�t�@�T�C�Y�ƃ\�P�b�g����ω������C �A���f�[�^�𑗐M����N���C�A���g���̋@�\�̎���
 */
public class UploadExperiment
{
    // �N���X�ϐ��i�萔�j�F
    private static final int KILO_BYTE = 1024;
    private static final int MEGA_BYTE = KILO_BYTE * 1024;
    private static final int GIGA_BYTE = MEGA_BYTE * 1024;
    
    // ���M���鑍�f�[�^�T�C�Y�Ƒ��M���s��
    private static final int TOTAL_SIZE = 1 * MEGA_BYTE;
    private static final int TRY_COUNT  = 10;
    
    // ���M�o�b�t�@�T�C�Y�̔z��
    private static final int[] BUF_SIZE_ARRAY = {
        8, 16, 32, 64, 128, 256
    };
    
    // �����ɗ��p����\�P�b�g���̔z��
    private static final int[] NUM_SOCKS_ARRAY = {
        1, 2, 3, 4, 5, 6, 7, 8
    };
    
    /**
     * �T�[�o�ւ̃f�[�^���M�����p�N���C�A���g�𗘗p���邽�߂� main ���\�b�h
     */
    public static void main(String[] args)
    {
        // �����̐����m�F����D
        if (args.length != 2) {
            System.out.println("Parameters: <Server> <Port>");
            return;
        }
        try {
            // �T�[�o��(�܂���IP�A�h���X)�ƃT�[�o�̃|�[�g�ԍ�����������擾����D
            String servAddr = args[0];
            int    servPort = Integer.parseInt(args[1]);
            
            // �R���\�[���ւ̃��O�o�͗p�̃C���X�^���X�𐶐�����D
            ConsoleLogger clog = new ConsoleLogger();
            
            // �t�@�C���ւ̃��O�o�͗p�̃C���X�^���X�𐶐�����D
            FileLogger flog = new FileLogger(
                String.format("log_%d.txt", System.currentTimeMillis())
            );
            // IOException
            
            // �f�[�^���M�����p�N���C�A���g�̃C���X�^���X�𐶐�����D
            UploadExperiment client = new UploadExperiment(
                servAddr, servPort, clog, flog
            );
            
            // �T�[�o�ւ̃A�b�v���[�h���J��Ԃ��C���ʂ��o�͂���D
            client.execute();
        }
        catch (Exception ex) {
            System.out.println("��O�����F" + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // �C���X�^���X�ϐ��F
    private final String        servAddr;   // �T�[�o��IP�A�h���X(�܂��̓z�X�g��)
    private final int           servPort;   // �T�[�o�̃|�[�g�ԍ�
    private final ConsoleLogger clog;       // �R���\�[���ւ̏o�͗p Logger
    private final FileLogger    flog;       // �t�@�C���ւ̏o�͗p Logger
    
    /**
     * UploadExperiment �̃C���X�^���X�𐶐�����D
     */
    UploadExperiment(
        String servAddr, int servPort, ConsoleLogger clog, FileLogger flog
    )
    {
        this.servAddr = servAddr;
        this.servPort = servPort;
        this.clog = clog;
        this.flog = flog;
    }
    
    /**
     * �T�[�o�ւ̃A�b�v���[�h���J��Ԃ��C���ʂ��o�͂���D
     */
    void execute()
    {
        // �o�b�t�@�T�C�Y�ƃ\�P�b�g����ω������C
        // TOTAL_SIZE �o�C�g���̃f�[�^�� TRY_COUNT ��T�[�o�փA�b�v���[�h���C
        // �o�b�t�@�T�C�Y�ƃ\�P�b�g�����̃A�b�v���[�h����(TRY_COUNT �񕪂̔z��)���擾����D
        Object[][] timesArray = uploadAll();
        
        // �o�b�t�@�T�C�Y�ƃ\�P�b�g�����̃X���[�v�b�g(TRY_COUNT �񕪂̔z��)���i�[����z��
        Object[][] throughputsArray = new Object[BUF_SIZE_ARRAY.length][NUM_SOCKS_ARRAY.length];
        
        // ���σX���[�v�b�g���i�[����z��ƃX���[�v�b�g�̕W���΍����i�[����z��
        int[][] averages_mbps = new int[BUF_SIZE_ARRAY.length][NUM_SOCKS_ARRAY.length];
        int[][] stdDevs_mbps  = new int[BUF_SIZE_ARRAY.length][NUM_SOCKS_ARRAY.length];
        
        // �o�b�t�@�T�C�Y�̔z��ƃ\�P�b�g���̔z��𑖍�����D
        for (int bs = 0; bs < BUF_SIZE_ARRAY.length; bs++) {
            for (int ns = 0; ns < NUM_SOCKS_ARRAY.length; ns++) {
                
                // �o�ߎ���(ms)�̔z����擾���C�X���[�v�b�g(mbps)�����߂�D
                long[] elapsedTimes_ms  = (long[])timesArray[bs][ns];
                int[]  throughputs_mbps = calcThroughput(elapsedTimes_ms);
                throughputsArray[bs][ns] = throughputs_mbps;
                
                // ���σX���[�v�b�g�ƃX���[�v�b�g�̕W���΍������߂�D
                averages_mbps[bs][ns] = calcAverage(throughputs_mbps);
                stdDevs_mbps[bs][ns] = calcStdDev(
                    averages_mbps[bs][ns], throughputs_mbps
                );
            }
        }
        // ���L(a)�`(c)���t�@�C���֏o�͂���D
        // (a) ���σX���[�v�b�g
        // (b) �X���[�v�b�g�̕W���΍�
        // (c) �o�b�t�@�T�C�Y�ƃ\�P�b�g�����̃X���[�v�b�g(TRY_COUNT �񕪂̒l)
        writeResults(averages_mbps);
        writeResults(stdDevs_mbps);
        writeAllThroughputs(throughputsArray);
        
        // �t�@�C���ւ̃��O�o�͂��I������D
        this.flog.close();
    }
    
    /**
     * �o�b�t�@�T�C�Y�ƃ\�P�b�g����ω������C TOTAL_SIZE �o�C�g���̃f�[�^�� TRY_COUNT ��T�[�o�փA�b�v���[�h����D
     */
    private Object[][] uploadAll()
    {
        // �o�b�t�@�T�C�Y�ƃ\�P�b�g������
        // �A�b�v���[�h���Ԃ̔z��(TRY_COUNT��)���i�[���邽�߂̔z��𐶐�����D
        Object[][] timesArray = new Object[BUF_SIZE_ARRAY.length][NUM_SOCKS_ARRAY.length];
        
        // �o�b�t�@�T�C�Y�̔z��ƃ\�P�b�g���̔z��𑖍�����D
        for (int bs = 0; bs < BUF_SIZE_ARRAY.length; bs++) {
            for (int ns = 0; ns < NUM_SOCKS_ARRAY.length; ns++) {
                
                // �o�b�t�@�T�C�Y�ƃ\�P�b�g���̔z��̗v�f�ł���
                // �o�b�t�@�T�C�Y�ƃ\�P�b�g�����擾����D
                int bufferSize = BUF_SIZE_ARRAY[bs];
                int numSockets = NUM_SOCKS_ARRAY[ns];
                
                // �擾�����o�b�t�@�T�C�Y�ƃ\�P�b�g���ŁC
                // TOTAL_SIZE �o�C�g���̃f�[�^�� TRY_COUNT ��
                // �T�[�o�փA�b�v���[�h���C
                // ���̊e��̌o�ߎ��Ԃ��擾����D
                long[] elapsedTimes_ms = upload(bufferSize, numSockets);
                
                // �o�ߎ��Ԃ̔z���ێ����Ă����D
                timesArray[bs][ns] = elapsedTimes_ms;
            }
        }
        // �S�v�����ʂ��܂ޔz���Ԃ��D
        return timesArray;
    }
    
    /**
     * �^����ꂽ�o�b�t�@�T�C�Y�ƃ\�P�b�g���ŁC TOTAL_SIZE �o�C�g���̃f�[�^�� TRY_COUNT ��T�[�o�փA�b�v���[�h����D
     */
    private long[] upload(int bufferSize, int numSockets)
    {
        // �o�ߎ��Ԃ��L�^���邽�߂̔z��𐶐����C�v�f�� -1 �ŏ���������D
        long[] elapsedTimes_ms = new long[TRY_COUNT];
        for (int i = 0; i < TRY_COUNT; i++) {
            elapsedTimes_ms[i] = -1;
        }
        // �^����ꂽ�o�b�t�@�T�C�Y�ƃ\�P�b�g���ɂ����鏈���̊T�����R���\�[���֏o�͂���D
        this.clog.printf("%s [", getDescription(bufferSize, numSockets));
        
        // �o�ߎ��Ԃ̗݌v�ƁC����ɃA�b�v���[�h�ł�����
        long totalTime_ms = 0;
        int  count        = 0;
        
        // ��O�������̃��b�Z�[�W���i�[���Ă���������̃��X�g
        ArrayList<String> entry = new ArrayList<String>();
        
        // �T�[�o�ւ̃f�[�^�A�b�v���[�h�� TRY_COUNT ��J��Ԃ��D
        for (int i = 0; i < TRY_COUNT; i++) {
            try {
                // �T�[�o�փf�[�^���A�b�v���[�h���邽�߂̃C���X�^���X�𐶐����C
                // �A�b�v���[�h�J�n�������擾������CTOTAL_SIZE ���̑��M���������s����D
                MultiThreadUploader uploader = new MultiThreadUploader(
                    this.servAddr, this.servPort, TOTAL_SIZE, bufferSize,
                    numSockets
                );
                // �A�b�v���[�h�J�n�������擾����D
                long startTime_ms = System.currentTimeMillis();
                
                // �A�b�v���[�h���������s����D
                uploader.upload();
                // InterruptedException, ExecutionException
                
                // �A�b�v���[�h�I���������擾���C�o�ߎ��Ԃƌo�ߎ��Ԃ̗݌v�����߂�D
                long endTime_ms = System.currentTimeMillis();
                elapsedTimes_ms[i] = endTime_ms - startTime_ms;
                if (elapsedTimes_ms[i] == 0) {
                    // �~���b�̐��x�ł͌o�ߎ��Ԃ� 0ms �ƂȂ�ꍇ������D
                    // ���̏ꍇ�́C�ŏ��l�ł��� 1ms �o�߂������̂Ƃ���D 
                    elapsedTimes_ms[i] = 1;
                }
                totalTime_ms += elapsedTimes_ms[i];
                
                // ����ɃA�b�v���[�h�ł����񐔂��J�E���g���C
                // �A�b�v���[�h�I�������� "o" ���R���\�[���֏o�͂���D
                count++;
                this.clog.printf("o");
            }
            catch (InterruptedException | ExecutionException ex) {
                // ��O�����������D
                // �A�b�v���[�h����������ɏI�����Ă��Ȃ����Ƃ����� "x" ��
                // �R���\�[���֏o�͂��C��O���b�Z�[�W�����X�g�֒ǉ����Ă����D
                this.clog.printf("x");
                entry.add("  " + ex.getMessage());
            }
        }
        // ����ɃA�b�v���[�h�ł����񐔂��m�F���C1��ł�����ɃA�b�v���[�h�ł��Ă���΁C
        // TOTAL_SIZE ���̃A�b�v���[�h���Ԃ̕��ς����߂ăR���\�[���֏o�͂���D
        if (count > 0) {
            double average_sec = (totalTime_ms / count) / 1000.0;
            this.clog.printf("], average time: %6.3f sec.\n", average_sec);
        }
        else {
            this.clog.println("]");
        }
        // ��O���������Ă����ꍇ�͂��̃��b�Z�[�W���R���\�[���֏o�͂���D
        if (entry.size() > 0) {
            this.clog.printlist(entry);
        }
        // �o�ߎ��Ԃ̔z���Ԃ��D
        return elapsedTimes_ms;
    }
    
    /**
     * �^����ꂽ�o�b�t�@�T�C�Y�ƃ\�P�b�g���ɂ����鏈���̊T����Ԃ��D
     */
    private String getDescription(int bufferSize, int numSockets)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(
            String.format(
                "%s (%,6d x %,7d + %-4d), %3d",
                toKiloByte(TOTAL_SIZE),
                bufferSize,
                TOTAL_SIZE / bufferSize,
                TOTAL_SIZE % bufferSize,
                numSockets
            )
        );
        if (numSockets <= 1) {
            sb.append(" socket ");
        }
        else {
            sb.append(" sockets");
        }
        return sb.toString();
    }
    
    /**
     * �o�C�g�P�ʂ̐��l���C�P�ʕt���̕�����ɕϊ�����D
     */
    private String toKiloByte(int val_byte)
    {
        String result = null;
        
        if (val_byte >= GIGA_BYTE) {
            result = new String(val_byte / GIGA_BYTE + "GB");
        }
        else if (val_byte >= MEGA_BYTE) {
            result = new String(val_byte / MEGA_BYTE + "MB");
        }
        else if (val_byte >= KILO_BYTE) {
            result = new String(val_byte / KILO_BYTE + "KB");
        }
        else {
            result = new String(val_byte + "B");
        }
        return result;
    }
    
    /**
     * �o�ߎ��Ԃ���X���[�v�b�g�����߂�D
     */
    private int[] calcThroughput(long[] elapsedTimes_ms)
    {
        // �X���[�v�b�g���i�[���邽�߂̔z��𐶐����C�v�f�� -1 �ŏ���������D
        int[] throughputs_mbps = new int[TRY_COUNT];
        for (int i = 0; i < TRY_COUNT; i++) {
            throughputs_mbps[i] = -1;
        }
        // �o�ߎ��Ԃ���X���[�v�b�g�����߂�D
        for (int i = 0; i < TRY_COUNT; i++) {
            long elapsedTime_ms = elapsedTimes_ms[i];
            if (elapsedTime_ms == -1) {
                continue;
            }
            long throughput_bps = 1000 * (TOTAL_SIZE * 8 / elapsedTime_ms);
            throughputs_mbps[i] = (int)Math.round(throughput_bps / 1000000.0);
        }
        // ���߂��X���[�v�b�g�̔z���Ԃ��D
        return throughputs_mbps;
    }
    
    /**
     * ���σX���[�v�b�g�����߂�D
     */
    private int calcAverage(int[] throughputs_mbps)
    {
        // ���σX���[�v�b�g
        int average_mbps = 0;
        
        int sum   = 0;
        int count = 0;
        
        // �X���[�v�b�g�̕��ς����߂�D
        for (int i = 0; i < TRY_COUNT; i++) {
            int throughput_mbps = throughputs_mbps[i];
            if (throughput_mbps == -1) {
                continue;
            }
            sum += throughput_mbps;
            count++;
        }
        // 1��ȏ�C�v���ɐ������Ă��邱�Ƃ��m�F������ŁC
        // ���σX���[�v�b�g�����߂�D
        if (count > 0) {
            average_mbps = sum / count;
        }
        // ���σX���[�v�b�g��Ԃ��D
        return average_mbps;
    }
    
    /**
     * �X���[�v�b�g�̕W���΍������߂�D
     */
    private int calcStdDev(int average_mbps, int[] throughputs_mbps)
    {
        // �X���[�v�b�g�̕W���΍�
        int stdDev_mbps = 0;
        
        int sum   = 0;
        int count = 0;
        
        // �X���[�v�b�g�̕W���΍������߂�D
        for (int i = 0; i < TRY_COUNT; i++) {
            int throughput_mbps = throughputs_mbps[i];
            if (throughput_mbps == -1) {
                continue;
            }
            sum += Math.pow(throughput_mbps - average_mbps, 2);
            count++;
        }
        // 1��ȏ�C�v���ɐ������Ă��邱�Ƃ��m�F������ŁC
        // �W���΍������߂�D
        if (count > 0) {
            stdDev_mbps = (int)Math.sqrt(sum / count);
        }
        // �X���[�v�b�g�̕W���΍���Ԃ��D
        return stdDev_mbps;
    }
    
    /**
     * �o�b�t�@�T�C�Y�ƃ\�P�b�g�����̎������ʂ��t�@�C���֏o�͂���D
     */
    private void writeResults(int[][] results)
    {
        for (int ns = 0; ns < NUM_SOCKS_ARRAY.length; ns++) {
            this.flog.printf("\t%d", NUM_SOCKS_ARRAY[ns]);
        }
        this.flog.printf("\n");
        
        for (int bs = 0; bs < BUF_SIZE_ARRAY.length; bs++) {
            this.flog.printf("%d", BUF_SIZE_ARRAY[bs]);
            
            for (int ns = 0; ns < NUM_SOCKS_ARRAY.length; ns++) {
                this.flog.printf("\t%d", results[bs][ns]);
            }
            this.flog.printf("\n");
        }
        this.flog.printf("\n");
    }
    
    /**
     * �o�b�t�@�T�C�Y�ƃ\�P�b�g�����̃X���[�v�b�g(TRY_COUNT �񕪂̒l)���t�@�C���֏o�͂���D
     */
    private void writeAllThroughputs(Object[][] throughputsArray)
    {
        // �o�b�t�@�T�C�Y�̔z��ƃ\�P�b�g���̔z��𑖍�����D
        for (int bs = 0; bs < BUF_SIZE_ARRAY.length; bs++) {
            for (int ns = 0; ns < NUM_SOCKS_ARRAY.length; ns++) {
                this.flog.printf("%d\t%d", BUF_SIZE_ARRAY[bs], NUM_SOCKS_ARRAY[ns]);
                
                int[] throughputs_mbps = (int[])throughputsArray[bs][ns];
                for (int i = 0; i < TRY_COUNT; i++) {
                    this.flog.printf("\t%d", throughputs_mbps[i]);
                }
                this.flog.printf("\n");
            }
        }
    }
}
