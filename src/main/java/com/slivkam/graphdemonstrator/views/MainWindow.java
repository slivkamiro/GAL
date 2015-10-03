package com.slivkam.graphdemonstrator.views;

import javax.swing.JApplet;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.slivkam.graphdemonstrator.ViewsModule;

public class MainWindow extends JApplet {
    public MainWindow() {
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private GraphDemonstratorView demoView;

    public JMenuItem mntmOpen;
    public JMenuItem mntmSave;
    public JMenu mnFile;


    /*
     * Entry point.
     * (non-Javadoc)
     * @see java.applet.Applet#init()
     */
    @Override
    public void init() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            Injector injector = Guice.createInjector(new ViewsModule());

            this.demoView = injector.getInstance(GraphDemonstratorView.class);

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    MainWindow.this.initComponents();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("createGUI didn't complete successfully");
        }
    }

    private void initComponents() {

        // initialization

        // menu
        JMenuBar menuBar = new JMenuBar();
        this.mnFile = new JMenu("File");
        this.mntmOpen = new JMenuItem("Open");
        this.mntmSave = new JMenuItem("Save");

        menuBar.add(this.mnFile);
        this.mnFile.add(this.mntmOpen);
        this.mnFile.add(this.mntmSave);
        this.setJMenuBar(menuBar);

        this.demoView.initComponents(this);

    }
}
