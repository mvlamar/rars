package rars.riscv.dump;

import rars.Globals;
import rars.riscv.hardware.AddressErrorException;
import rars.riscv.hardware.Memory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import rars.ProgramStatement;

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
        
        String[] fileNames ={file+"text.mif", file+"data.mif"};//, file+"ktext.mif", file+"kdata.mif"};
        int[] sizes={4096,2048,2048,1024}; // Pré-defined DE2-70 Size of memory blocks in Words
        int[] addrs={
            Memory.textBaseAddress, 
            Memory.dataBaseAddress,
            //Memory.kernelTextBaseAddress,
            //Memory.kernelDataBaseAddress
        };
        
    for (int tipo=0; tipo < fileNames.length; tipo++){
        
        PrintStream out = new PrintStream(new FileOutputStream(fileNames[tipo]));
        String string;
        try {
            string = "DEPTH = " + Integer.toString(sizes[tipo]) + ";";
            out.println(string);
            out.println("WIDTH = 32;");
            out.println("ADDRESS_RADIX = HEX;");
            out.println("DATA_RADIX = HEX;");
            out.println("CONTENT");
            out.println("BEGIN");
            for (int address = addrs[tipo],waddr=0; address <= addrs[tipo]+sizes[tipo]*Memory.WORD_LENGTH_BYTES; address += Memory.WORD_LENGTH_BYTES,waddr++) {
                Integer temp = Globals.memory.getRawWordOrNull(address);
                if (temp == null)
                    break;

                String addr = Integer.toHexString(waddr);
                while (addr.length() < 8) {
                    addr = '0' + addr;
                }
                
                String data = Integer.toHexString(temp);
                while (data.length() < 8) {
                    data = '0' + data;
                }
                
                string = addr + " : " + data + ";";
                if (tipo==0 || tipo==2) {
                    ProgramStatement ps = Globals.memory.getStatement(address);
                    string += "   % " + ps.getSourceLine() + ": " + ps.getSource()+" %";
                }
                out.println(string);
            }
            out.println("END;");
        } finally {
            out.close();
        }

    }
    }
}
