package firstspider.UI;

import firstspider.network.AddressCatcher;
import javax.swing.JOptionPane;

public class SpiderFrame extends javax.swing.JFrame {

    private SpiderPanel sp = null;
    private AddressCatcher ac = null;

    public SpiderFrame() {
        sp = new SpiderPanel();
        ac = sp.getAc();
        initComponents();
        setContentPane(sp);
        System.out.println("_______________________________________________________________________________________________________________________________________________________________________________");
        System.out.println("\tFind me : htttp://shellcottage.me\n\tAuthor: Haoxuan WANG");
        System.out.println("\tReleased under the MIT License. ");
        System.out.println("\t\t\t\t\tWelcome. 欢迎使用。 ");
        System.out.println("");
        System.out.println("_______________________________________________________________________________________________________________________________________________________________________________");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("ICE Backup Tool | ICE备份工具");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(this.getToolkit().getImage("img/favicon.jpg"));
        setPreferredSize(new java.awt.Dimension(1073, 803));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
//GEN-FIRST:event_formWindowClosing
        if (ac.flag) {
            System.exit(0);
        } else {
            Object[] options = {"Yes,close now \n 是的，现在关闭",
                "Cancel,  \n 取消 "};
            int n = JOptionPane.showOptionDialog(this,
                    "The programme is running. A sudden close will result in the failure of this backup?\n"
                    + "程序正在运行,突然关闭将导致备份失败。",
                    "Close now 现在关闭？",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
            if (n == 0) {
                System.exit(1);
            }

        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */

        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SpiderFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SpiderFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SpiderFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SpiderFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new SpiderFrame().setVisible(true);

        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
