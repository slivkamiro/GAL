package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import model.GraphAdapter;
import presenters.DemoPresenter;
import presenters.DemoPresenter.Demonstrator;
import presenters.GraphPresenter;
import presenters.Presenter.Dialogs;
import utils.MyButtonGroup;

public class MainWindow extends JApplet implements Demonstrator {
	public MainWindow() {
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private DemoPresenter presenter;

	private GraphPresenter gPresenter;

	private Canvas canvas;

	private JTextArea eventArea;
	private JComboBox<Object> algorithms;

	@Override
	public void init() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			presenter = new DemoPresenter(getDemonstrator());
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					initComponents();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("createGUI didn't complete successfully");
		}
	}

	private Demonstrator getDemonstrator() {
		return this;
	}

	private void initComponents() {

		// initialization

		// menu
		JMenuBar menuBar = new JMenuBar();
		JMenu mnFile = new JMenu("File");
		JMenuItem mntmOpen = new JMenuItem("Open");
		JMenuItem mntmSave = new JMenuItem("Save");

		menuBar.add(mnFile);
		mnFile.add(mntmOpen);
		mnFile.add(mntmSave);
		setJMenuBar(menuBar);

		// toolbar
		JToolBar toolBar = new JToolBar();
		MyButtonGroup eGrp = new MyButtonGroup();
		JToggleButton tglbtnVertex = new JToggleButton("Vertex");
		JToggleButton tglbtnEdge = new JToggleButton("Edge");
		JToggleButton tglbtnEdit = new JToggleButton("Edit");
		JToggleButton tglbtnRemove = new JToggleButton("Remove");
		JButton btnClear = new JButton("Clear");
		final JToggleButton btnDemo = new JToggleButton("Demo");
		algorithms = new JComboBox<Object>(presenter.getAlgorithms());
		algorithms.setMaximumSize(new Dimension(120,22));
		algorithms.setPreferredSize(new Dimension(120,22));
		algorithms.setMinimumSize(new Dimension(120,22));
		algorithms.setSelectedIndex(0);
		JButton btnBwd = new JButton();
		btnBwd.setIcon(new ImageIcon(this.getClass().getResource("icons/bwd.png")));
		JButton btnFwd = new JButton();
		btnFwd.setIcon(new ImageIcon(this.getClass().getResource("icons/fwd.png")));
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
		toolBar.add(algorithms);
		toolBar.add(btnBwd);
		toolBar.add(btnFwd);
		toolBar.add(tglbtnMyGraph);
		getContentPane().add(toolBar, BorderLayout.NORTH);

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

		eventArea = new JTextArea();
		eventArea.setForeground(SystemColor.BLACK);
		eventArea.setBackground(SystemColor.control);
		eventArea.setEditable(false);
		panel.add(eventArea);

		// graph on the left
		gPresenter = new GraphPresenter();
		canvas = new Canvas(gPresenter);
		canvas.setBackground(Color.WHITE);
		scrollPaneL.setViewportView(canvas);
		//gPresenter.setView(this);
		gPresenter.setView(canvas);

		canvas.requestFocus();
		getContentPane().add(splitPane, BorderLayout.CENTER);

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
		mntmOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!btnDemo.isSelected()) {
					int fcReturn = fc.showOpenDialog(MainWindow.this);
					if (fcReturn == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						gPresenter.setGraph(f);
					}
				} else {
					presenter.populateDialog(Dialogs.MESSAGE, "Can't manipulate graph in demonstration mode.");
				}
			}

		});

		// Save file
		mntmSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!btnDemo.isSelected()) {
					int fcReturn = fc.showSaveDialog(MainWindow.this);
					if (fcReturn == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						gPresenter.saveGraph(f);
					}
				} else {
					presenter.populateDialog(Dialogs.MESSAGE, "Can't manipulate graph in demonstration mode.");
				}

			}

		});

		// Creating vertex - stop demonstration if running
		tglbtnVertex.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				gPresenter.setEditor(GraphPresenter.EditorOptions.VERTEX);
				presenter.stopDemo();
				tglbtnMyGraph.setSelected(false);
			}

		});

		// Creating edge - stop demonstration if running
		tglbtnEdge.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				gPresenter.setEditor(GraphPresenter.EditorOptions.EDGE);
				presenter.stopDemo();
				tglbtnMyGraph.setSelected(false);
			}

		});

		// Editing - stop demonstration if running
		tglbtnEdit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				gPresenter.setEditor(GraphPresenter.EditorOptions.EDIT);
				presenter.stopDemo();
				tglbtnMyGraph.setSelected(false);
			}

		});

		// Removing - stop demonstration if running
		tglbtnRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				gPresenter.setEditor(GraphPresenter.EditorOptions.REMOVE);
				presenter.stopDemo();
				tglbtnMyGraph.setSelected(false);
			}

		});

		// Clear workspace - stop demonstration and unselect demo button
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.stopDemo();
				btnDemo.setSelected(false);
				gPresenter.setGraph(new GraphAdapter());
			}

		});

		// Demonstration start - initialize algorithm with graph
		btnDemo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					gPresenter.setEditor(GraphPresenter.EditorOptions.NONE);
					presenter.start(gPresenter.getGraph());
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
					presenter.stepBackward();
				}

			}

		});

		// Demonstration step forward
		btnFwd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!tglbtnMyGraph.isSelected()) {
					presenter.stepForward();
				}

			}

		});

		// Show graph that is used in algorithm demonstration
		tglbtnMyGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnDemo.isSelected()) {
					presenter.showStarterGraph();
				}

			}

		});


	}

	@Override
	public void addEvent(String ev) {
		eventArea.setText(eventArea.getText()+"\n"+ev);
		eventArea.setCaretPosition(eventArea.getDocument().getLength());
	}

	@Override
	public void setGraph(GraphAdapter graph) {
		gPresenter.setGraph(graph);

	}

	@Override
	public String getSelectedAlgorithm() {
		return algorithms.getSelectedItem().toString();
	}

	@Override
	public void clearEvents() {
		eventArea.setText("");

	}

}
