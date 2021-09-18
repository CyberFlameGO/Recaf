package me.coley.recaf.presentation;

import me.coley.recaf.Controller;
import me.coley.recaf.RecafConstants;
import me.coley.recaf.RecafUI;
import me.coley.recaf.config.ConfigRegistry;
import me.coley.recaf.config.Configs;
import me.coley.recaf.ui.prompt.WorkspaceIOPrompts;
import me.coley.recaf.ui.util.Lang;
import me.coley.recaf.util.AccessPatcher;
import me.coley.recaf.util.JFXInjection;
import me.coley.recaf.util.JFXUtils;
import me.coley.recaf.util.Threads;
import me.coley.recaf.util.logging.Logging;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * JavaFX based GUI presentation.
 *
 * @author Matt Coley
 */
public class GuiPresentation implements Presentation {
	private static final Logger logger = Logging.get(GuiPresentation.class);
	private Controller controller;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		// Patch JDK restrictions
		AccessPatcher.patch();
		// Setup JavaFX
		JFXInjection.ensureJavafxSupport();
		JFXUtils.initializePlatform();
		// Load translations
		Lang.initialize();
		// Setup config
		Configs.containers().forEach(ConfigRegistry::register);
		try {
			ConfigRegistry.load();
			// Use loaded values
			WorkspaceIOPrompts.setupLocations();
		} catch (IOException ex) {
			logger.error("Failed to load stored config values", ex);
		}
		// Open UI
		JFXUtils.runSafe(() -> {
			try {
				RecafUI.initialize(controller);
				RecafUI.getWindows().getMainWindow().show();
			} catch (Throwable ex) {
				logger.error("Recaf crashed due to an unhandled error." +
						"Please open a bug report: " + RecafConstants.URL_BUG_REPORT, ex);
				JFXUtils.shutdownSafe();
				System.exit(-1);
			}
		});
		// Intercept / log uncaught exceptions
		Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
			logger.error("Uncaught exception on thread '" + thread.getName() + "'", exception);
			if (exceptionHandler != null)
				exceptionHandler.uncaughtException(thread, exception);
		});
		// Shutdown handler
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				ConfigRegistry.save();
			} catch (IOException ex) {
				logger.error("Failed to save config values", ex);
			}
			Threads.shutdown();
		}));
	}

	@Override
	public WorkspacePresentation workspaceLayer() {
		return new GuiWorkspacePresentation(controller);
	}
}