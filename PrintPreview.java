import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
public class PrintPreview implements ActionListener, ChangeListener {
    JFrame f;
    EasyPrint pb;
    JLabel viewing;
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            int scale = (int)source.getValue();
            pb.zoom(72*scale/100);
            adjust();
        }
    }
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Previous")) {
            pb.viewPreviousPage();
            viewing.setText("Viewing Page "+pb.getViewingPage()+" / "+pb.getTotalPage());
            viewing.validate();
        } else if (cmd.equals("Next")) {
            pb.viewNextPage();
            viewing.setText("Viewing Page "+pb.getViewingPage()+" / "+pb.getTotalPage());
            viewing.validate();
        } else if (cmd.equals("Print Current")) {
            pb.print(pb.getViewingPage());
        } else if (cmd.equals("Print Multiple")) {
            String s = JOptionPane.showInputDialog("Page List[1-2,4,5]","1-"+pb.getTotalPage());
            if (s == null) return;
            boolean[] f = new boolean[pb.getTotalPage()+1];
            for (int i = 0; i < f.length; i++)
                f[i] = true; // not to print
            StringTokenizer st = new StringTokenizer(s,",");
            while (st.hasMoreTokens()) {
                StringTokenizer t = new StringTokenizer(st.nextToken(),"-");
                while (t.hasMoreTokens()) {
                    try {
                        int start = Integer.parseInt(t.nextToken());
                        if (t.hasMoreTokens()) {
                            int stop = Integer.parseInt(t.nextToken());
                            for (int i = start; i <= stop; i++)
                                if (i>=0 && i < f.length)
                                    f[i] = false; // to print

                        } else {
                            if (start >= 0 && start < f.length)
                                f[start] = false; // to print
                        }
                    } catch(Exception err) {
                    }
                }
            }
            pb.print(f);
        } else if (cmd.equals("Print All")) {
            pb.print();
        } else if (cmd.equals("Save")) {
            JFileChooser chooser = new JFileChooser(".");
            JFrame f = new JFrame("Load EasyPrint");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("EasyPrint Object(.pb)", "pb");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(f);
            f.dispose();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                pb.save(chooser.getSelectedFile());
            }
        }
    }
    void addConstraint(Container container, Component component,
        int grid_x, int grid_y, int grid_width, int grid_height,
        int fill, int anchor, double weight_x, double weight_y,
        int top, int left, int bottom, int right) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = grid_x; c.gridy = grid_y;
        c.gridwidth = grid_width; c.gridheight = grid_height;
        c.fill = fill; c.anchor = anchor;
        c.weightx = weight_x; c.weighty = weight_y;
        c.insets = new Insets(top,left,bottom,right);
        ((GridBagLayout)container.getLayout()).setConstraints(component,c);
        container.add(component);
    }
    public PrintPreview(EasyPrint pb) {
        this.pb = pb;
        Font font = new Font("monospaced", Font.PLAIN, 14);
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration keys = defaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if ((key instanceof String) && (((String)key).endsWith(".font")))
                defaults.put(key, font);
        }
        JButton b;
        f = new JFrame(pb.jobTitle);
        Container container = f.getContentPane();
        container.setLayout(new GridBagLayout());
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JScrollPane jsp = new JScrollPane(pb.getPrintCanvas());
        addConstraint(container, jsp,0, 0, 1, 1,
            GridBagConstraints.BOTH, GridBagConstraints.CENTER,
            1,1,0,0,0,0);
        JSlider roomin = new JSlider(JSlider.HORIZONTAL, 50, 400, 100);
        roomin.addChangeListener(this);
        //Turn on labels at major tick marks.
        roomin.setMajorTickSpacing(50);
        roomin.setMinorTickSpacing(10);
        roomin.setPaintTicks(true);
        roomin.setPaintLabels(true);
        addConstraint(container, viewing = new JLabel(""),0,1,1,1,
            GridBagConstraints.NONE, GridBagConstraints.CENTER,
            0,0,0,0,0,0);
        addConstraint(container, roomin,0,2,1,1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,
            1,0,0,0,0,0);
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(2,4));
        addConstraint(container, buttons,0,3,1,1,
            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER,
            1,0,0,0,0,0);
        ((JButton)buttons.add(b=new JButton("Previous"))).addActionListener(this);
        ((JButton)buttons.add(b=new JButton("Next"))).addActionListener(this);
        ((JButton)buttons.add(b=new JButton("Print Current"))).addActionListener(this);
        ((JButton)buttons.add(b=new JButton("Print Multiple"))).addActionListener(this);
        ((JButton)buttons.add(b=new JButton("Print All"))).addActionListener(this);
        ((JButton)buttons.add(b=new JButton("Save"))).addActionListener(this);
        f.setVisible(true);
        pb.preview();
        adjust();
        viewing.setText("Viewing Page "+pb.getViewingPage()+" / "+pb.getTotalPage());
        viewing.validate();
    }
    private void adjust() {
        Dimension pre = f.getSize();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension prefer = pb.getPreferredSize();
        screen.height -= 40; // don't cover work line
        prefer.width += 34; // reserve room prevent scroll bar
        prefer.height += 200; // reserve room for buttons and slider
        int x = prefer.width > screen.width ? screen.width : prefer.width;
        int y = prefer.height > screen.height ? screen.height : prefer.height;
        if (pre.width == x && pre.height == y)
            f.pack();
        f.setSize(x, y);
    }
}
