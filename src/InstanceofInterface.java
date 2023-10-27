
class InstanceofInterface
{
    public final static int NUMMAMMALS = 4;
    
    public static void main(String args[])
    {
        // �M���ނ̔z��
        Mammal mammals[] = new Mammal[NUMMAMMALS];
        
        mammals[0] = new Bear();        // �F
        mammals[1] = new Elephant();    // ��
        mammals[2] = new Horse();       // �n
        mammals[3] = new Lion();        // ���C�I��
        
        // ��蕨�̋@�\���������Ă���M���ނ𗘗p����D
        for (int i = 0; i < NUMMAMMALS; i++) {
            if (mammals[i] instanceof Vehicle) {
                Vehicle v = (Vehicle)mammals[i];
                v.drive();
            }
        }
    }
}

interface Vehicle
{
    void drive();
}

abstract class Mammal
{
}

class Bear
    extends
        Mammal
{
}

class Elephant
    extends
        Mammal
    implements
        Vehicle
{
    @Override
    public void drive()
    {
        System.out.println("Elephant.drive()");
    }
}

class Horse
    extends
        Mammal
    implements
        Vehicle
{
    @Override
    public void drive()
    {
        System.out.println("Horse.drive()");
    }
}

class Lion
    extends
        Mammal
{
}
