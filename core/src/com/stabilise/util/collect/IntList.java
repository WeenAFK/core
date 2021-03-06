package com.stabilise.util.collect;

import java.util.Arrays;
import java.util.function.IntConsumer;

/**
 * This class provides a thin wrapper for a resizable int array. Iteration
 * should be performed as:
 * 
 * <pre>for(int i = 0; i < list.size(); i++) {
 *     doSomething(list.get(i));
 * }
 * </pre>
 */
public class IntList {
    
    private int[] data;
    private int size;
    
    
    /**
     * Creates a new IntList with an internal array size of 8.
     */
    public IntList() {
        this(8);
    }
    
    /**
     * Creates a new IntList.
     * 
     * @param initialLength The initial length of the internal array.
     * 
     * @throws NegativeArraySizeException if initialLength < 0.
     */
    public IntList(int initialLength) {
        data = new int[initialLength];
        size = 0;
    }
    
    /**
     * Returns the size of this list.
     */
    public int size() {
        return size;
    }
    
    /**
     * Returns the length of the backing array.
     */
    public int capacity() {
        return data.length;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Adds an int to this list. If necessary, the backing array will be
     * resized.
     */
    public void add(int i) {
        if(size == data.length)
            data = Arrays.copyOf(data, 2*size + 1);
        data[size++] = i;
    }
    
    /**
     * Adds an int to this list via an insertion sort (i.e. the given int will
     * be inserted in the place of the first entry greater than it, shifting
     * all succeeding entries to the right to make room, if necessary).
     */
    public void addSorted(int i) {
        if(size == data.length)
            data = Arrays.copyOf(data, 2*size + 1);
        int idx = 0;
        while(idx < size && data[idx] <= i)
            idx++;
        if(idx == size)
            data[size++] = i;
        else {
            System.arraycopy(data, idx, data, idx+1, size-idx);
            data[idx] = i;
            size++;
        }
    }
    
    /**
     * Sets the element at the specified index, ignoring the size of this list.
     * 
     * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >=
     * capacity()}.
     */
    public void set(int index, int i) {
        data[index] = i;
    }
    
    /**
     * Gets the element at the specified index.
     * 
     * @throws ArrayIndexOutOfBoundsException if {@code index < 0 || index >=
     * capacity()}.
     */
    public int get(int index) {
        return data[index];
    }
    
    /**
     * Removes the specified int from this list, shifting all succeeding
     * entries to the left if necessary.
     * 
     * @return true if the entry was removed; false if it was not present
     */
    public boolean remove(int i) {
        int idx = 0;
        while(idx < size && data[idx] != i)
            idx++;
        if(idx == size)
            return false;
        else {
            size--;
            System.arraycopy(data, idx+1, data, idx, size-idx);
            return true;
        }
    }
    
    /**
     * Clears this list.
     */
    public void clear() {
        size = 0;
    }
    
    /**
     * Iterates over this list as if by:
     * 
     * <pre>for(int i = 0; i < size; i++)
     *      action.accept(data[i]);
     * </pre>
     * 
     * and then clears this list.
     */
    public void clear(IntConsumer action) {
        if(size == 0) return;
        for(int i = 0; i < size; i++)
            action.accept(data[i]);
        size = 0;
    }
    
    /**
     * Iterates over this list as if by:
     * 
     * <pre>for(int i = 0; i < size; i++)
     *      action.accept(data[i]);
     * </pre>
     */
    public void interate(IntConsumer action) {
        for(int i = 0; i < size; i++)
            action.accept(data[i]);
    }
    
}
