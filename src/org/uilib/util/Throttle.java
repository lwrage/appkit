package org.uilib.util;

import java.util.concurrent.TimeUnit;

public interface Throttle {

	//~ Methods --------------------------------------------------------------------------------------------------------

	void throttle(final String throttleName, final long delay, final TimeUnit timeUnit, final Runnable runnable);
}