package com.slivkam.graphdemonstrator.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import com.google.inject.Inject;
import com.slivkam.graphdemonstrator.model.GraphAdapter;
import com.slivkam.graphdemonstrator.presenters.DemoPresenter;
import com.slivkam.graphdemonstrator.presenters.DemoPresenter.Demonstrator;
import com.slivkam.graphdemonstrator.presenters.GraphPresenter;
import com.slivkam.graphdemonstrator.presenters.GraphPresenter.GraphEditor;
import com.slivkam.graphdemonstrator.presenters.Presenter.PresenterFactory;
import com.slivkam.graphdemonstrator.presenters.Presenter.View;
import com.slivkam.graphdemonstrator.swingcomponents.Canvas;
import com.slivkam.graphdemonstrator.swingcomponents.Canvas.CanvasConnector;
import com.slivkam.graphdemonstrator.swingcomponents.CanvasObject;
import com.slivkam.graphdemonstrator.utils.MyButtonGroup;


public class GraphDemonstratorView implements Demonstrator, GraphEditor, CanvasConnector, View {

    private DemoPresenter demoPresenter;

    private GraphPresenter graphPresenter;

    private PresenterFactory presenterFactory;

    private Canvas canvas;
    private JTextArea eventArea;
    private JComboBox<Object> algorithms;

    @Inject
    public GraphDemonstratorView(PresenterFactory presenterFactory) {
        this.canvas = new Canvas(this);
        this.demoPresenter = presenterFactory.createDemoPresenter(this);
        this.graphPresenter = presenterFactory.createGraphPresenter(this);
        this.presenterFactory = presenterFactory;
    }

    @Override
    public PresenterFactory getPresenterFactory() {
        return this.presenterFactory;
    }

    @Override
    public void canvasMousePressed(Point point) {
        this.graphPresenter.startPoint(point);
    }

    @Override
    public void canvasMouseReleased(Point point) {
        this.graphPresenter.endPoint(point);
    }

    @Override
    public void canvasMouseDragged(Point point) {
        this.graphPresenter.possibleEndPoint(point);
    }

    @Override
    public void addEvent(String ev) {
        this.eventArea.setText(this.eventArea.getText()+"\n"+ev);
        this.eventArea.setCaretPosition(this.eventArea.getDocument().getLength());
    }

    @Override
    public void setGraph(GraphAdapter graph) {
        this.graphPresenter.setGraph(graph);

    }

    @Override
    public String getSelectedAlgorithm() {
        return this.algorithms.getSelectedItem().toString();
    }

    @Override
    public void clearEvents() {
        this.eventArea.setText("");

    }

    @Override
    public void drawObject(CanvasObject o) {
        this.canvas.drawObject(o);

    }

    @Override
    public void editObject(CanvasObject o) {
        this.canvas.editObject(o);

    }

    @Override
    public void removeObjectCloseTo(Point p) {
        this.canvas.removeObjectCloseTo(p);

    }

    @Override
    public void removeLast() {
        this.canvas.removeLast();

    }

    @Override
    public void removeObject(CanvasObject o) {
        this.canvas.removeObject(o);
    }

    @Override
    public List<CanvasObject> getObjects() {
        return this.canvas.getObjects();
    }

    @Override
    public void setObjects(List<CanvasObject> objects) {
        this.canvas.setObjects(objects);

    }

    @Override
    public void clean() {
        this.canvas.clean();

    }

    public void initComponents(final MainWindow window) {

        // initialization

        // toolbar
        JToolBar toolBar = new JToolBar();
        MyButtonGroup eGrp = new MyButtonGroup();
        JToggleButton tglbtnVertex = new JToggleButton("Vertex");
        JToggleButton tglbtnEdge = new JToggleButton("Edge");
        JToggleButton tglbtnEdit = new JToggleButton("Edit");
        JToggleButton tglbtnRemove = new JToggleButton("Remove");
        JButton btnClear = new JButton("Clear");
        final JToggleButton btnDemo = new JToggleButton("Demo");
        this.algorithms = new JComboBox<Object>(this.demoPresenter.getAlgorithms());
        this.algorithms.setMaximumSize(new Dimension(120,22));
        this.algorithms.setPreferredSize(new Dimension(120,22));
        this.algorithms.setMinimumSize(new Dimension(120,22));
        this.algorithms.setSelectedIndex(0);
        JButton btnBwd = new JButton();
        btnBwd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("icons/bwd.png")));
        JButton btnFwd = new JButton();
        btnFwd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("icons/fwd.png")));
        final JToggleButton tglbtnMyGraph = new JToggleButton("My Graph");

        eGrp.add(tglbtnVertex);
        eGrp.add(tglbtnEdge);
        eGrp.add(tglbtnEdit);
        eGrp.add(tglbtnRemove);
        eGrp.add(btnDemo);

        toolBar.add(tglbtnVertex);
        toolBar.add(tglbtnEdge);
        toolBar.add(tglbtnEdit);
        toolBar.add(tglbtnRemove);
        toolBar.add(btnClear);
        toolBar.addSeparator();
        toolBar.add(btnDemo);
        toolBar.add(this.algorithms);
        toolBar.add(btnBwd);
        toolBar.add(btnFwd);
        toolBar.add(tglbtnMyGraph);
        window.getContentPane().add(toolBar, BorderLayout.NORTH);

        // workspace
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.75);
        splitPane.setEnabled(false);

        JScrollPane scrollPaneL = new JScrollPane();
        JScrollPane scrollPaneR = new JScrollPane();

        splitPane.setLeftComponent(scrollPaneL);
        splitPane.setRightComponent(scrollPaneR);

        // events on the right
        JPanel panel = new JPanel();
        scrollPaneR.setViewportView(panel);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        this.eventArea = new JTextArea();
        this.eventArea.setForeground(SystemColor.BLACK);
        this.eventArea.setBackground(SystemColor.control);
        this.eventArea.setEditable(false);
        panel.add(this.eventArea);

        // graph on the left

        this.canvas.setBackground(Color.WHITE);
        scrollPaneL.setViewportView(this.canvas);
        this.canvas.requestFocus();

        //gPresenter.setView(this);
        //gPresenter.setView(canvas);
        window.getContentPane().add(splitPane, BorderLayout.CENTER);

        // Actions

        // Set file filter to xml only - graphML
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".xml")
                        || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "GraphML XML files";
            }

        });

        // Open file
        window.mntmOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!btnDemo.isSelected()) {
                    int fcReturn = fc.showOpenDialog(window);
                    if (fcReturn == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        GraphDemonstratorView.this.graphPresenter.setGraph(f);
                    }
                } else {
                    GraphDemonstratorView.this.demoPresenter.populateDialog( "Can't manipulate graph in demonstration mode.",null);
                }
            }

        });

        // Save file
        window.mntmSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!btnDemo.isSelected()) {
                    int fcReturn = fc.showSaveDialog(window);
                    if (fcReturn == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        GraphDemonstratorView.this.graphPresenter.saveGraph(f);
                    }
                } else {
                    GraphDemonstratorView.this.demoPresenter.populateDialog("Can't manipulate graph in demonstration mode.", null);
                }

            }

        });

        // Creating vertex - stop demonstration if running
        tglbtnVertex.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                GraphDemonstratorView.this.graphPresenter.setEditor(GraphPresenter.EditorOptions.VERTEX);
                GraphDemonstratorView.this.demoPresenter.stopDemo();
                tglbtnMyGraph.setSelected(false);
            }

        });

        // Creating edge - stop demonstration if running
        tglbtnEdge.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                GraphDemonstratorView.this.graphPresenter.setEditor(GraphPresenter.EditorOptions.EDGE);
                GraphDemonstratorView.this.demoPresenter.stopDemo();
                tglbtnMyGraph.setSelected(false);
            }

        });

        // Editing - stop demonstration if running
        tglbtnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                GraphDemonstratorView.this.graphPresenter.setEditor(GraphPresenter.EditorOptions.EDIT);
                GraphDemonstratorView.this.demoPresenter.stopDemo();
                tglbtnMyGraph.setSelected(false);
            }

        });

        // Removing - stop demonstration if running
        tglbtnRemove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                GraphDemonstratorView.this.graphPresenter.setEditor(GraphPresenter.EditorOptions.REMOVE);
                GraphDemonstratorView.this.demoPresenter.stopDemo();
                tglbtnMyGraph.setSelected(false);
            }

        });

        // Clear workspace - stop demonstration and unselect demo button
        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GraphDemonstratorView.this.demoPresenter.stopDemo();
                btnDemo.setSelected(false);
                GraphDemonstratorView.this.graphPresenter.setGraph(new GraphAdapter());
            }

        });

        // Demonstration start - initialize algorithm with graph
        btnDemo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    GraphDemonstratorView.this.graphPresenter.setEditor(GraphPresenter.EditorOptions.NONE);
                    GraphDemonstratorView.this.demoPresenter.start(GraphDemonstratorView.this.graphPresenter.getGraph());
                } catch (InstantiationException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

        // Demonstration step back
        btnBwd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(!tglbtnMyGraph.isSelected()) {
                    GraphDemonstratorView.this.demoPresenter.stepBackward();
                }

            }

        });

        // Demonstration step forward
        btnFwd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(!tglbtnMyGraph.isSelected()) {
                    GraphDemonstratorView.this.demoPresenter.stepForward();
                }

            }

        });

        // Show graph that is used in algorithm demonstration
        tglbtnMyGraph.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnDemo.isSelected()) {
                    GraphDemonstratorView.this.demoPresenter.showStarterGraph();
                }

            }

        });


    }

}
