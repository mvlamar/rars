package rars.riscv.dump;

import rars.Globals;
import rars.riscv.hardware.AddressErrorException;
import rars.riscv.hardware.Memory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Intel's Hex memory initialization format
 *
 * @author Leo Alterman
 * @version July 2011
 */

public class AMifDumpFormat extends AbstractDumpFormat {

    /**
     * Constructor.  File extention is "hex".
     */
    public AMifDumpFormat() {
        super("MIF format", "MIF", "Memory Initialization Format File", "mif");
    }

    /**
     * Write MIPS memory contents according to the Memory Initialization File
     * (MIF) specification.
     *
     * @param file         File in which to store MIPS memory contents.
     * @param firstAddress first (lowest) memory address to dump.  In bytes but
     *                     must be on word boundary.
     * @param lastAddress  last (highest) memory address to dump.  In bytes but
     *                     must be on word boundary.  Will dump the word that starts at this address.
     * @throws AddressErrorException if firstAddress is invalid or not on a word boundary.
     * @throws IOException           if error occurs during file output.
     */
    public void dumpMemoryRange(File file, int firstAddress, int lastAddress)
            throws AddressErrorException, IOException {
        PrintStream out = new PrintStream(new FileOutputStream(file));
        String string = null;
        try {
            string = "DEPTH = " + Integer.toString(lastAddress-firstAddress+4) + ";";
            out.println(string);
            out.println("WIDTH = 32;");
            out.println("ADDRESS_RADIX = HEX;");
            out.println("DATA_RADIX = HEX;");
            out.println("CONTENT");
            out.println("BEGIN");
            for (int address = firstAddress; address <= lastAddress; address += Memory.WORD_LENGTH_BYTES) {
                Integer temp = Globals.memory.getRawWordOrNull(address);
                if (temp == null)
                    break;

                String addr = Integer.toHexString(address - firstAddress);
                while (addr.length() < 8) {
                    addr = '0' + addr;
                }
                
                String data = Integer.toHexString(temp);
                while (data.length() < 8) {
                    data = '0' + data;
                }
                
                string = addr+" : "+data+";";
                
                out.println(string);
            }
            out.println("END;");
        } finally {
            out.close();
        }

    }
}
