import java.awt.*;
import java.io.*;
public class PrintSample implements PageListener {
    public void pageCreated(EasyPrint pb) {
        pb.drawImage(new File("ssyu.jpg"), 4, 8, 1.3, 2);
    }
    public static void main(String[] argv) throws Exception {
        EasyPrint pb = new EasyPrint(new PrintSample());
        pb.setHeader("這是第#page頁的表頭", "monospaced",  Font.PLAIN, 18/72.0);
        pb.setFooter("#page / #total", "monospaced",  Font.PLAIN, 10/72.0);
        pb.setFont("monospaced", Font.PLAIN, 12/72.0);
        //pb.setPrinter("\\\\ccserver1\\資管M351U");
        pb.drawString("VERTICAL", 1, 8, 3, EasyPrint.VERTICAL);
        pb.drawString("VERTICAL", 1.8, 8, 3, EasyPrint.VBOTH);
        pb.drawString("VERTICAL", 2.6, 8, 3, EasyPrint.VSPREAD);
        pb.drawString("VERTICAL", 3.4, 8, 3, EasyPrint.VCENTER);
        pb.setPageColumn(new double[][]{{0.3,1,3.7,6},{4.3,1,3.7,6}});
        for (int i = 0; i < 10; i++) {
            pb.drawParagraph("測試中的多欄位處理,一個中英夾雜段落(paragraph)可以橫跨多個大小不同的欄。\nJava is a programming language originally developed by James Gosling at Sun Microsystems (which has since merged into Oracle Corporation) and released in 1995 as a core component of Sun Microsystems' Java platform.\n此文字中可以用換行符號強迫換行，除最後一行是左切齊外,其它都採兩側對齊。");
        }
        pb.addTable("1", "測試表1", 0, new String[]{"靠右欄","靠左欄","A Centered Column","兩側\n100學年","分散"}, new double[]{0.7,0.7,0.7,0.8,0.7},new boolean[]{false,false,true,true,true},new int[]{EasyPrint.RIGHT,EasyPrint.LEFT,EasyPrint.CENTER,EasyPrint.BOTH,EasyPrint.SPREAD});
        for (int i = 0; i < 10; i++)
            pb.addRow("1", new String[]{Double.toString(i+1),"Row "+(i+1),"Row "+(i+1),"第"+(i+1),"分"+(i+1)});
        pb.addRow("1", "String|C 1|C 2|C3|Last");
        pb.closeTable("1");
        pb.drawParagraph("The language derives much of its syntax from C and C++ but has a simpler object model and fewer low-level facilities. Java applications are typically compiled to bytecode (class file) that can run on any Java Virtual Machine (JVM) regardless of computer architecture.", EasyPrint.BOTH);
        pb.drawParagraph("The original and reference implementation Java compilers, virtual machines, and class libraries were developed by Sun from 1995. As of May 2007, in compliance with the specifications of the Java Community Process, Sun relicensed most of its Java technologies under the GNU General Public License. Others have also developed alternative implementations of these Sun technologies, such as the GNU Compiler for Java and GNU Classpath.");
        pb.drawParagraph("Sun Microsystems released the first public implementation as Java 1.0 in 1995. It promised \"Write Once, Run Anywhere\" (WORA), providing no-cost run-times on popular platforms. Fairly secure and featuring configurable security, it allowed network- and file-access restrictions. Major web browsers soon incorporated the ability to run Java applets within web pages, and Java quickly became popular. With the advent of Java 2 (released initially as J2SE 1.2 in December 1998–1999), new versions had multiple configurations built for different types of platforms. For example, J2EE targeted enterprise applications and the greatly stripped-down version J2ME for mobile applications (Mobile Java). J2SE designated the Standard Edition. In 2006, for marketing purposes, Sun renamed new J2 versions as Java EE, Java ME, and Java SE, respectively.");
        pb.addTable("2", "客製化表頭測試表", 0, null, new double[]{0.6,0.7,0.7,0.7,0.7},new boolean[]{false,false,true,true,true},new int[]{EasyPrint.RIGHT,EasyPrint.LEFT,EasyPrint.CENTER,EasyPrint.BOTH,EasyPrint.SPREAD});
        pb.addTableHeadCell("2", "日期", 0, 0, 1, 2, EasyPrint.CENTER);
        pb.addTableHeadCell("2", "早餐", 1, 0, 2, 1, EasyPrint.CENTER);
        pb.addTableHeadCell("2", "午餐", 3, 0, 2, 1, EasyPrint.CENTER);
        pb.addTableHeadCell("2", "葷數量/長表頭", 1, 1, 1, 1, EasyPrint.CENTER);
        pb.addTableHeadCell("2", "素數量", 2, 1, 1, 1, EasyPrint.CENTER);
        pb.addTableHeadCell("2", "葷數量", 3, 1, 1, 1, EasyPrint.CENTER);
        pb.addTableHeadCell("2", "素數量", 4, 1, 1, 1, EasyPrint.CENTER);
        for (int i = 0; i < 10; i++)
            pb.addRow("2", new String[]{Double.toString(i+1),"Row "+(i+1),"Row "+(i+1),"第"+(i+1),"row"+(i+1)});
        for (int i = 0; i < 10; i++) {
            pb.addRowCell("2", "總日數", 0, 0, 1, 2, EasyPrint.CENTER);
            pb.addRowCell("2", "早餐小計", 1, 0, 2, 1, EasyPrint.CENTER);
            pb.addRowCell("2", "午餐小計", 3, 0, 2, 1, EasyPrint.CENTER);
            pb.addRowCell("2", "葷總量", 1, 1, 1, 1, EasyPrint.CENTER);
            pb.addRowCell("2", "素總量", 2, 1, 1, 1, EasyPrint.CENTER);
            pb.addRowCell("2", "葷總量", 3, 1, 1, 1, EasyPrint.CENTER);
            pb.addRowCell("2", "素總量", 4, 1, 1, 1, EasyPrint.CENTER);
            pb.closeRow("2");
        }
        pb.closeTable("2");
        pb.drawParagraph("");
        pb.addTable(0, "Column1|Column2|Column3|Column4", "^1.0|<0.7|0.8~|1.2");
        for (int i = 0; i < 10; i++) {
            pb.addRow("String|C 1|C 2|C3");
        }
        for (int i = 0; i < 10; i++) {
            pb.addRow("<String|>C 1|^C 2|C3");
        }
        new PrintPreview(pb);
    }
}
