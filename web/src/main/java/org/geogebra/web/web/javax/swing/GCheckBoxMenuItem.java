package org.geogebra.web.web.javax.swing;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Menu item with a checkbox (for new UI use the checkmark version)
 */
public class GCheckBoxMenuItem {

	private CheckBox checkBox;
	private AriaMenuItem menuItem;
	private HorizontalPanel itemPanel;

	// true if menu has no checkbox, but ON/OFF label.
	private boolean toggle = false;
	private boolean selected;
	private App app;
	private boolean isHtml;
	private String text;
	private boolean forceCheckbox = false;
	// public GCheckBoxMenuItem(SafeHtml html, final ScheduledCommand cmd) {
	// super(html, cmd);
	// checkBox = new CheckBox(html);
	// checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>(){
	// public void onValueChange(ValueChangeEvent<Boolean> event) {
	// cmd.execute();
	// }});
	// setHTML(checkBox.toString());
	// }

	/*
	 * text should be shown when the item is selected
	 */
	private String textSelected;
	/*
	 * text should be shown when the item is NON selected
	 */
	private String textNonSelected;

	public GCheckBoxMenuItem(String text,
			boolean isHtml, App app) {

		// It's didn't work, becase when I clicked on the label of the checkbox,
		// the command of menuitem didn't run, so I added the html-string for
		// the MenuItem
		// in an another way (see below)
		// checkBox = new CheckBox(html);

		this.text = text;
		this.isHtml = isHtml;
		this.app = app;

		this.toggle = app.has(Feature.WHITEBOARD_APP)
				&& app.has(Feature.MOW_CONTEXT_MENU);
		itemPanel = new HorizontalPanel();

		checkBox = new CheckBox();
		if (!app.isUnbundled()) {
			itemPanel.add(checkBox);
		}
		checkBox.setVisible(!isToggleMenu());

		setText(text);
		if (app.isUnbundled()) {
			itemPanel.add(checkBox);
			checkBox.addStyleName("matCheckBox");
		}
	}

	public GCheckBoxMenuItem(String text, final ScheduledCommand cmd,
			boolean isHtml, App app) {
		this(text, isHtml, app);
		setCommand(cmd);
	}

	public GCheckBoxMenuItem(String text, String textSel, String textNonSel,
			final ScheduledCommand cmd, boolean isHtml, App app) {
		this(text, isHtml, app);
		textSelected = textSel;
		textNonSelected = textNonSel;
		setCommand(cmd);
	}

	public void setCommand(ScheduledCommand cmd) {
		menuItem = new AriaMenuItem(itemPanel.toString(), true, cmd);
	}

	/**
	 * @param sel
	 *            whether this should be selected
	 * @param menu
	 *            parent menu to update
	 */
	public void setSelected(boolean sel, AriaMenuBar menu) {
		selected = sel;
		if (textSelected != null) {
			setText(sel ? textSelected : textNonSelected);
		} else if (isToggleMenu()) {
			itemPanel.clear();
			String txt = app.getLocalization()
					.getMenu(selected ? "On" : "Off");
			setText(text + " " + txt);
		} else {
			checkBox.setValue(sel);
		}
		String html = itemPanel.toString();
		menuItem.setHTML(html);
	}

	private boolean isToggleMenu() {
		return toggle && !forceCheckbox;
	}

	/**
	 * 
	 * @return true if check box is checked
	 */
	public boolean isSelected() {
		return isToggleMenu() ? selected : checkBox.getValue();
	}

	/**
	 * @return wrapped item
	 */
	public AriaMenuItem getMenuItem() {
		return menuItem;
	}

	private void setText(String text) {
		Widget w = isHtml ? new HTML(text) : new Label(text);
		itemPanel.add(w);
	}

	public boolean isForceCheckbox() {
		return forceCheckbox;
	}

	public void setForceCheckbox(boolean forceCheckbox) {
		this.forceCheckbox = forceCheckbox;
		checkBox.setVisible(!isToggleMenu());
	}
}
