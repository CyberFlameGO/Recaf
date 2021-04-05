package me.coley.recaf.ui.window;

import com.panemu.tiwulfx.control.dock.DetachableTabPane;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import me.coley.recaf.BuildConfig;
import me.coley.recaf.ui.control.LoggingTextArea;
import me.coley.recaf.ui.panel.DockingRootPane;
import me.coley.recaf.ui.panel.WelcomePanel;
import me.coley.recaf.ui.panel.WorkspacePanel;

/**
 * Main window for Recaf.
 *
 * @author Matt Coley
 */
public class MainWindow extends WindowBase {
	private final WorkspacePanel workspacePanel = new WorkspacePanel();
	private final DockingRootPane dockingRootPane = new DockingRootPane();

	/**
	 * Create the window.
	 */
	public MainWindow() {
		init();
		setTitle("Recaf " + BuildConfig.VERSION);
	}

	@Override
	protected Scene createScene() {
		// Content
		SplitPane initialSplit;
		dockingRootPane.setPrefWidth(1080);
		dockingRootPane.createLockedTab("Workspace", workspacePanel);
		initialSplit = dockingRootPane.createNewSplit(Orientation.HORIZONTAL, 0.30);
		dockingRootPane.createTab("Welcome", new WelcomePanel());
		dockingRootPane.createNewSplit(Orientation.VERTICAL, 0.76);
		dockingRootPane.createLockedTab("Logging", LoggingTextArea.getInstance());
		// Mark main content region for new tabs
		DetachableTabPane contentWrapper = (DetachableTabPane) initialSplit.getItems().get(1);
		dockingRootPane.setRecentTabPane(contentWrapper);
		// TODO: Make it so the workspace panel does not scale when dropped into a new location
		//SplitPane.setResizableWithParent(workspacePanel, Boolean.FALSE);
		//SplitPane.setResizableWithParent(horizontal, Boolean.FALSE);
		return new Scene(dockingRootPane);
	}

	/**
	 * @return Docking panel.
	 */
	public DockingRootPane getDockingRootPane() {
		return dockingRootPane;
	}

	/**
	 * @return Panel representing the current workspace.
	 */
	public WorkspacePanel getWorkspacePanel() {
		return workspacePanel;
	}
}
