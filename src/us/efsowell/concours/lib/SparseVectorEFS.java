/*
 * Copyright Edward F Sowell 2014
 */
package us.efsowell.concours.lib;

import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

/**
 *
 * @author Ed Sowell
 */
public class SparseVectorEFS {
    private int d;                   // dimension
    private ST<Integer, Double> st;  // the vector, represented by index-value pairs

   /**
     * Initializes a d-dimensional zero vector.
     * @param d the dimension of the vector
     */
    public SparseVectorEFS(int d) {
        this.d  = d;
        this.st = new ST<Integer, Double>();
    }

   /**
     * Sets the ith coordinate of this vector to the specified value.
     *
     * @param  i the index
     * @param  value the new value
     * @throws IndexOutOfBoundsException unless i is between 0 and d-1
     */
    public void put(int i, double value) {
        if (i < 0 || i >= d) throw new IndexOutOfBoundsException("Illegal index" + " i=" + i + " d = " + d);
        if (value == 0.0) st.delete(i);
        else              st.put(i, value);
    }

   /**
     * Returns the ith coordinate of this vector.
     *
     * @param  i the index
     * @return the value of the ith coordinate of this vector
     * @throws IndexOutOfBoundsException unless i is between 0 and d-1
     */
    public double get(int i) {
        if (i < 0 || i >= d) throw new IndexOutOfBoundsException("Illegal index");
        if (st.contains(i)) return st.get(i);
        else                return 0.0;
    }
    
    // returns the key or null 
    // Not sure if this is needed... the question is if the 
    public Integer getKey(int i) {
        double val = -1; // not used
        if (i < 0 || i >= d) throw new IndexOutOfBoundsException("Illegal index");
        if (st.contains(i)){
            val = st.get(i);
        } 
        
        return val!=0? i:null;
    }

   /**
     * Returns the number of nonzero entries in this vector.
     *
     * @return the number of nonzero entries in this vector
     */
    public int nnz() {
        return st.size();
    }

   /**
     * Returns the dimension of this vector.
     *
     * @return the dimension of this vector
     * @deprecated Replaced by {@link #dimension()}.
     */
    public int size() {
        return d;
    }

   /**
     * Returns the dimension of this vector.
     *
     * @return the dimension of this vector
     */
    public int dimension() {
        return d;
    }

    /**
     * Returns the inner product of this vector with the specified vector.
     *
     * @param  that the other vector
     * @return the dot product between this vector and that vector
     * @throws IllegalArgumentException if the lengths of the two vectors are not equal
     */
    public double dot(SparseVectorEFS that) {
        if (this.d != that.d) throw new IllegalArgumentException("Vector lengths disagree");
        double sum = 0.0;

        // iterate over the vector with the fewest nonzeros
        if (this.st.size() <= that.st.size()) {
            for (int i : this.st.keys())
                if (that.st.contains(i)) sum += this.get(i) * that.get(i);
        }
        else  {
            for (int i : that.st.keys())
                if (this.st.contains(i)) sum += this.get(i) * that.get(i);
        }
        return sum;
    }


    /**
     * Returns the inner product of this vector with the specified array.
     *
     * @param  that the array
     * @return the dot product between this vector and that array
     * @throws IllegalArgumentException if the dimensions of the vector and the array are not equal
     */
    public double dot(double[] that) {
        double sum = 0.0;
        for (int i : st.keys())
            sum += that[i] * this.get(i);
        return sum;
    }

    /**
     * Returns the magnitude of this vector.
     * This is also known as the L2 norm or the Euclidean norm.
     * 
     * @return the magnitude of this vector
     */
    public double magnitude() {
        return Math.sqrt(this.dot(this));
    }


    /**
     * Returns the Euclidean norm of this vector.
     *
     * @return the Euclidean norm of this vector
     * @deprecated Replaced by {@link #magnitude()}.
     */
    public double norm() {
        return Math.sqrt(this.dot(this));
    }

    /**
     * Returns the scalar-vector product of this vector with the specified scalar.
     *
     * @param  alpha the scalar
     * @return the scalar-vector product of this vector with the specified scalar
     */
    public SparseVectorEFS scale(double alpha) {
        SparseVectorEFS c = new SparseVectorEFS(d);
        for (int i : this.st.keys()) c.put(i, alpha * this.get(i));
        return c;
    }

    /**
     * Returns the sum of this vector and the specified vector.
     *
     * @param  that the vector to add to this vector
     * @return the sum of this vector and that vector
     * @throws IllegalArgumentException if the dimensions of the two vectors are not equal
     */
    public SparseVectorEFS plus(SparseVectorEFS that) {
        if (this.d != that.d) throw new IllegalArgumentException("Vector lengths disagree");
        SparseVectorEFS c = new SparseVectorEFS(d);
        for (int i : this.st.keys()) c.put(i, this.get(i));                // c = this
        for (int i : that.st.keys()) c.put(i, that.get(i) + c.get(i));     // c = c + that
        return c;
    }

   /**
     * Returns a string representation of this vector.
     * @return a string representation of this vector, which consists of the 
     *         the vector entries, separates by commas, enclosed in parentheses
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i : st.keys()) {
            s.append("(" + i + ", " + st.get(i) + ") ");
        }
        return s.toString();
    }


    /**
     * Unit tests the <tt>SparseVectorEFS</tt> data type.
     */
    public static void main(String[] args) {
        SparseVectorEFS a = new SparseVectorEFS(10);
        SparseVectorEFS b = new SparseVectorEFS(10);
        a.put(3, 0.50);
        a.put(9, 0.75);
        a.put(6, 0.11);
        a.put(6, 0.00);
        b.put(3, 0.60);
        b.put(4, 0.90);
        StdOut.println("a = " + a);
        StdOut.println("b = " + b);
        StdOut.println("a dot b = " + a.dot(b));
        StdOut.println("a + b   = " + a.plus(b));
    }

    
}
