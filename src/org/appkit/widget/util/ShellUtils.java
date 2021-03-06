package org.appkit.widget.util;

import org.appkit.util.SmartExecutor;
import org.appkit.util.Throttle;
import org.appkit.util.prefs.PrefStore;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

/**
 * various utility-functions for working with {@link Shell}s
 *
 */
public final class ShellUtils {

	//~ Methods --------------------------------------------------------------------------------------------------------

	/** returns the Point a shell need to be located at to be in the center of the screen */
	public static Point getCenterPosition(final Shell shell) {

		/* Position in the middle of screen (and a little up) */
		Rectangle monitorBounds = shell.getDisplay().getPrimaryMonitor().getBounds();
		Rectangle shellBounds   = shell.getBounds();
		int x				    = monitorBounds.x + ((monitorBounds.width - shellBounds.width) / 2);
		int y				    = (monitorBounds.y + ((monitorBounds.height - shellBounds.height) / 2)) - 150;

		return new Point(x, y);
	}

	/**
	 * restores the size and position of a shell, tracks and saves changes
	 *
	 * @param prefStore the prefStore used to load and save size and position
	 * @param executor used to create a {@link Throttle} to the save function
	 * @param memoryKey prefStore key to save to
	 */
	public static void rememberSizeAndPosition(final PrefStore prefStore, final SmartExecutor executor,
											   final Shell shell, final String memoryKey, final int defaultWidth,
											   final int defaultHeight, final int defaultX, final int defaultY) {
		new ShellMemory(
			prefStore,
			executor,
			shell,
			memoryKey,
			defaultWidth,
			defaultHeight,
			defaultX,
			defaultY,
			false,
			false);
	}

	/**
	 * restores the size of a shell, tracks and saves changes.
	 *
	 * @param prefStore the prefStore used to load and save size and position
	 * @param executor used to create a {@link Throttle} to the save function
	 * @param memoryKey prefStore key to save to
	 */
	public static void rememberSize(final PrefStore prefStore, final SmartExecutor executor, final Shell shell,
									final String memoryKey, final int defaultWidth, final int defaultHeight) {
		new ShellMemory(prefStore, executor, shell, memoryKey, defaultWidth, defaultHeight, 0, 0, false, true);
	}
}