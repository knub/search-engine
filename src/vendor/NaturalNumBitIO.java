import java.io.IOException;

/** This class includes several static "utility" methods for doing
**  bit-oriented I/O using BitInputStream and BitOutputStream objects.
**
**  @author R. McCloskey
**  @version Fall 2005
*/

public class NaturalNumBitIO {


   /** Reads from input a sequence of bits of the specified length and
   **  returns the natural number encoded by that sequence (assuming
   **  standard binary representation).
   **  @return the natural number encoded by the sequence of bits
   **          (of the specified length) read from input
   */
   //  pre:  len >= 0  &&  input has at least len more bits
   //  post: a bit string of the specified length has been read from input
   //        and the natural number represented thereby has been returned.
   // 
   public static int readNatural(BitInputStream input, int len) 
      throws IOException
   {
      int result = 0;
      while (len != 0) {
         result = (2 * result) + input.readBit();
         len--;
      }
      return result;
   }

   /** This is a tail recursive alternative to the readNatural() method.
   **  @return the natural number encoded by the sequence of bits
   **          (of the specified length) read from input
   */
   //  pre:  len >= 0  &&  input has at least len more bits
   //  post: a bit string of the specified length has been read from input
   //        and the natural number represented thereby has been returned.
   //
   public static int readNaturalRec(BitInputStream input, int len)
      throws IOException
   {
      return readNaturalRecAux(input, len, 0);
   }

   private static int readNaturalRecAux(BitInputStream input,
                                       int len,
                                       int accumArg) throws IOException
   {
      int result;
      if (len == 0)
         { result = accumArg; }
      else {
         int bit = input.readBit();
         result =  readNaturalRecAux(input, len-1, (2 * accumArg) + bit);
      }
      return result;
   }



   /** Writes the specified natural # using the specified # of bits and
   **  in standard binary notation.
   */
   //  pre:  k >= 0  &&  len >= floor(lg k) + 1  (taking lg 0 = -1)
   //        That is, len bits should be sufficient to express k
   //  post: the binary representation of k of length len will have been
   //        written to output
   //
   public static void writeNatural(BitOutputStream output, int k, int len)
      throws IOException
   {
      if (len == 0) { }
      else {
         writeNatural(output, k / 2, len - 1);
         output.writeBit(k % 2);
      }
   }


   /** Reads a positive integer encoded via Elias's Gamma scheme, in
   **  which k is encoded by its min-length binary representation
   **  preceded by a string of zeros of length one less.
   **  Examples: 6 is encoded by 00110, 9 by 0001001, 1 by 1.
   */
   //  pre: input begins with an Elias Gamma encoding of some positive integer,
   //       which is a min-length binary representation of that integer
   //       preceded by a string of zero's of length one less.  
   //       Example: 6 is encoded by 00110, 9 by 0001001, 1 by 1.
   //  post: the positive integer mentioned in the precondition will have
   //        been read from input and its value returned.
   //
   public static int readEliasGamma(BitInputStream input) throws IOException {
      int result;
      int counter = 0;
      int bit = input.readBit();
      while (bit == 0) {
         counter++;
         bit = input.readBit();
      }
      // counter = # of 0's preceding first 1
      result = 1;
      for (int i=0; i != counter; i++) {
         result = (2 * result) + input.readBit();
      }
      return result;
   }


   /** Writes the specified positive integer encoded using Elias's Gamma
   **  scheme.
   */
   //  pre:  k > 0
   //  post: The Elias gamma code for k (the minimum-length binary rep. for k
   //        preceded by one fewer 0's than that length) will have been
   //        written to output
   //
   public static void writeEliasGamma(BitOutputStream output, int k)
      throws IOException
   {
      if (k == 1)
         { output.writeBit(1); }
      else {
         output.writeBit(0);
         writeEliasGamma(output, k / 2);
         output.writeBit(k % 2);
      }
   }

}
