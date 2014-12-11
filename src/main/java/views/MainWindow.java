package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import model.GraphAdapter;
import presenters.DemoPresenter;
import presenters.DemoPresenter.Demonstrator;
import presenters.GraphPresenter;
import utils.MyButtonGroup;

public class MainWindow extends JApplet implements Demonstrator {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private DemoPresenter presenter;

	private GraphPresenter gPresenter;

	private Canvas canvas;

	private JTextArea eventArea;
	private JComboBox<Object> algorithms;

	/**
	 * Create the applet.
	 */
	public MainWindow() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			presenter = new DemoPresenter(this);
			initComponents();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initComponents() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

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

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int fcReturn = fc.showOpenDialog(MainWindow.this);
				if (fcReturn == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					gPresenter.setGraph(f);
				}
			}

		});
		mnFile.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int fcReturn = fc.showSaveDialog(MainWindow.this);
				if(fcReturn == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					gPresenter.saveGraph(f);
				}

			}

		});
		mnFile.add(mntmSave);

		JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);

		JToggleButton tglbtnVertex = new JToggleButton("Vertex");
		tglbtnVertex.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				gPresenter.setEditor(GraphPresenter.EditorOptions.VERTEX);
			}

		});
		toolBar.add(tglbtnVertex);

		JToggleButton tglbtnEdge = new JToggleButton("Edge");
		tglbtnEdge.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				gPresenter.setEditor(GraphPresenter.EditorOptions.EDGE);
			}

		});
		toolBar.add(tglbtnEdge);

		JToggleButton tglbtnEdit = new JToggleButton("Edit");
		tglbtnEdit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				gPresenter.setEditor(GraphPresenter.EditorOptions.EDIT);
			}

		});
		toolBar.add(tglbtnEdit);

		JToggleButton tglbtnRemove = new JToggleButton("Remove");
		tglbtnRemove.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				gPresenter.setEditor(GraphPresenter.EditorOptions.REMOVE);
			}

		});
		toolBar.add(tglbtnRemove);

		MyButtonGroup eGrp = new MyButtonGroup();
		eGrp.add(tglbtnVertex);
		eGrp.add(tglbtnEdge);
		eGrp.add(tglbtnEdit);
		eGrp.add(tglbtnRemove);

		toolBar.addSeparator();

		algorithms = new JComboBox<Object>(presenter.getAlgorithms());
		algorithms.setMaximumSize(new Dimension(100,22));
		algorithms.setPreferredSize(new Dimension(100,22));
		algorithms.setMinimumSize(new Dimension(100,22));
		algorithms.setSelectedIndex(0);
		toolBar.add(algorithms);

		JToggleButton btnDemo = new JToggleButton("Demo");
		btnDemo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				gPresenter.setEditor(GraphPresenter.EditorOptions.NONE);
				presenter.start(gPresenter.getGraph());

			}

		});

		eGrp.add(btnDemo);

		toolBar.add(btnDemo);

		JButton btnBwd = new JButton("bwd");
		btnBwd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				presenter.stepBackward();

			}

		});
		toolBar.add(btnBwd);

		JButton btnFwd = new JButton("fwd");
		btnFwd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				presenter.stepForward();

			}

		});
		toolBar.add(btnFwd);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0);
		splitPane.setEnabled(false);
		getContentPane().add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPaneL = new JScrollPane();
		splitPane.setLeftComponent(scrollPaneL);
		JScrollPane scrollPaneR = new JScrollPane();
		splitPane.setRightComponent(scrollPaneR);

		JPanel panel = new JPanel();
		scrollPaneR.setViewportView(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		eventArea = new JTextArea();
		eventArea.setForeground(SystemColor.controlShadow);
		eventArea.setBackground(SystemColor.control);
		eventArea.setEditable(false);
		eventArea.setRows(10);
		eventArea.setColumns(20);
		panel.add(eventArea);

		gPresenter = new GraphPresenter();
		canvas = new Canvas(gPresenter);
		canvas.setBackground(Color.WHITE);
		scrollPaneL.setViewportView(canvas);
		gPresenter.setView(this);
		gPresenter.setView(canvas);

		canvas.requestFocus();

		/*JTextArea eventsArea = new JTextArea();
		eventsArea.setBackground(SystemColor.menu);
		eventsArea.setColumns(20);
		eventsArea.setRows(50);
		eventsArea.setEditable(false);
		splitPane.setRightComponent(eventsArea);*/
	}

	public void addEvent(String ev) {
		eventArea.setText(eventArea.getText()+"\n"+ev);
	}

	public void setGraph(GraphAdapter graph) {
		gPresenter.setGraph(graph);

	}

	public String getSelectedAlgorithm() {
		return algorithms.getSelectedItem().toString();
	}

}
