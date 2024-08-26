package com.systemwerx.PassGen;

import javafx.application.Platform;
import javafx.stage.Stage;
import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;

public class PassGenGUITimerThread extends Thread {
	int countdown = 0, count = 0;
	Stage app = null, actApp = null;
	boolean cancel = false;
	boolean halt = false;
	String title = null;
	boolean traceActive = false;

	PassGenGUITimerThread(Stage iapp, Stage iActApp, int time) {
		super();
		app = iapp;
		actApp = iActApp;
		countdown = time;
		title = app.getTitle();
	}

	public void run() {
		count = countdown;

		while (count > 0) {
			try {
				Log.debug("Sleeping for " + app.getClass().getName());
				sleep(1000);
			} catch (Exception ex) {
				Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
				ExceptionUtil.printStackTrace(ex);
			}

			if (cancel) {
				Log.debug("Cancel detected - exiting for " + app.getClass().getName());
				return;
			}

			if (halt) {
				if (traceActive)
					Log.debug("Thread halted for " + app.getClass().getName());
				continue;
			}

			count--;

			if (count < 20) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						app.setTitle(" (Window closes in " + count + " seconds)");
					}
				});
				Log.debug("Window closes in " + count + " for " + app.getClass().getName());
			}
		}

		// Close the window and display caller of required
		Log.debug("Closing window " + app.getClass().getName());

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				app.close();
			}
		});

		if (actApp != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					actApp.show();
				}
			});
			Log.debug("Display window " + app.getClass().getName());
		}

		return;
	}

	public boolean cancel() {
		Log.debug("cancel() " + app.getClass().getName());
		cancel = true;
		return true;
	}

	public boolean reset() {
		if (traceActive)
			Log.debug("reset() " + app.getClass().getName());
		count = countdown;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				app.setTitle(title);
			}
		});

		return true;
	}

	public void halt() {
		Log.debug("halt() " + app.getClass().getName());
		halt = true;
		return;
	}

	public void restart() {
		Log.debug("restart() " + app.getClass().getName());
		halt = false;
		reset();
		return;
	}

}