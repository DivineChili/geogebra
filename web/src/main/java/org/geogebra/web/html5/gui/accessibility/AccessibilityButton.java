package org.geogebra.web.html5.gui.accessibility;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.TabHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CustomButton;

public class AccessibilityButton implements AccessibilityInterface {
	private CustomButton button;
	private List<TabHandler> tabHandlers;
	private boolean ignoreTab=false;
	
	public AccessibilityButton(CustomButton button) {
		this.button = button;
		tabHandlers = new ArrayList<>();
	}
	
	@Override
	public void addTabHandler(TabHandler handler) {
		tabHandlers.add(handler);
	}

	public boolean handleBrowserEvent(Event event, App app) {
		int eventGetType = DOM.eventGetType(event);
		if (eventGetType == Event.ONKEYDOWN) {
			char keyCode = (char) event.getKeyCode();
			if (keyCode == '\t') {
				onTabPressed(event);
				return true;
			}

			if (app.has(Feature.HELP_AND_SHORTCUTS)
					&& !app.has(Feature.HELP_AND_SHORTCUTS_IMPROVEMENTS)
					&& app.getGlobalKeyDispatcher().handleCommonKeys(
					event.getKeyCode(),
					event.getCtrlKey(), event.getAltKey())) {
				event.stopPropagation();
				event.preventDefault();
				return true;
			}
		}
		return false;
	}

	private void onTabPressed(Event event) {
		for (TabHandler h : tabHandlers) {
			if (h.onTab(button, event.getShiftKey())) {
				event.stopPropagation();
				event.preventDefault();
			}
		}
	}

	@Override
	public void ignoreTab() {
		ignoreTab = true;
	}

	@Override
	public void setAltText(String text) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Call it at the end of button's onAttach()
	 * to prevent -1 turn automatically to 0.
	 */
	public void correctTabIndex() {
		if (ignoreTab) {
			button.getElement().setTabIndex(-1);
		}
	}

	@Override
	public void focusInput(boolean force) {
		if (button instanceof AccessibilityInterface) {
			((AccessibilityInterface) button).focusInput(force);
		}

	}
}
