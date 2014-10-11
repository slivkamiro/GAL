package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import presenters.DemoPresenter;
import presenters.DemoPresenter.Demonstrator;
import presenters.GraphPresenter;

public class MainWindow extends JApplet implements Demonstrator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DemoPresenter presenter;
	
	private GraphPresenter gPresenter;
	
	private Canvas canvas;

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
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
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
		
		ButtonGroup eGrp = new ButtonGroup();
		eGrp.add(tglbtnVertex);
		eGrp.add(tglbtnEdge);
		eGrp.add(tglbtnEdit);
		eGrp.add(tglbtnRemove);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0);
		splitPane.setEnabled(false);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		gPresenter = new GraphPresenter();
		canvas = new Canvas(gPresenter);
		canvas.setBackground(Color.WHITE);
		scrollPane.setViewportView(canvas);
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

}
