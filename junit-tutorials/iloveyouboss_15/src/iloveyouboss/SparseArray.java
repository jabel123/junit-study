package iloveyouboss;

public class SparseArray<T>
{
    public static final int INITIAL_SIZE = 1000;
    private int[] keys = new int[INITIAL_SIZE];
    private Object[] values = new Object[INITIAL_SIZE];
    private int size = 0;

    public void put(int key, T value)
    {
        if (value == null) return;
        int index = binarySearch(key, keys, size);
        if (index != -1 && keys[index] == key)
            values[index] = value;
        else
            insertAfter(key, value, index);
    }

    private void insertAfter (int key, T value, int index)
    {
        int[] newKeys = new int[INITIAL_SIZE];
        Object[] newValues = new Object[INITIAL_SIZE];
        copyFromBefore(index, newKeys, newValues);

        int newIndex = index + 1;
        newKeys[newIndex] = key;
        newValues[newIndex] = value;

        if (size - newIndex != 0)
            copyFromAfter(index, newKeys, newValues);
        keys = newKeys;
        values = newValues;
    }

    private void copyFromAfter (int index, int[] newKeys, Object[] newValues)
    {
        int start = index + 1;
        System.arraycopy(keys, start, newKeys, start + 1, size - start);
        System.arraycopy(values, start, newValues, start + 1, size - start);
    }

    private void copyFromBefore (int index, int[] newKeys, Object[] newValues)
    {
        int start = index + 1;
        System.arraycopy(keys, 0, newKeys, 0, index + 1 );
        System.arraycopy(values, 0, newValues, 0, index + 1);
    }

    public T get(int key)
    {
        int index = binarySearch(key, keys, size);
        if (index != -1 && keys[index] == key)
            return (T)values[index];
        return null;
    }
    private int binarySearch (int key, int[] keys, int size)
    {
        return 0;
    }

}
