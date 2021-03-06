package org.geogebra.web.web.cas.view;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.FormatConverterImpl;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.inputfield.AbstractSuggestionDisplay;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.inputfield.InputSuggestions;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * ReTeX editor for CAS
 *
 */
public class CASLaTeXEditor extends FlowPanel
 implements CASEditorW,
		MathKeyboardListener,
		MathFieldListener, BlurHandler {
	/** suggestions */
	InputSuggestions sug;
	private final MathFieldW mf;
	/** keyboard connector */
	RetexKeyboardListener retexListener;
	private AppWFull app;
	private CASTableW table;
	private CASTableControllerW controller;
	private boolean autocomplete = true;
	private Widget dummy;
	private Canvas canvas;

	/**
	 * @param table
	 *            table
	 * @param app
	 *            application
	 * @param controller
	 *            controller
	 */
	public CASLaTeXEditor(CASTableW table, final AppW app,
			final CASTableControllerW controller) {
		this.app = (AppWFull) app;
		this.table = table;
		this.controller = controller;
		canvas = Canvas.createIfSupported();
		mf = new MathFieldW(new FormatConverterImpl(app.getKernel()), this,
				canvas, this,
				app.has(Feature.MOW_DIRECT_FORMULA_CONVERSION),
				app.getGlobalKeyDispatcher().getFocusHandler());
		retexListener = new RetexKeyboardListener(canvas, mf);
		mf.setOnBlur(this);
		add(mf);
		dummy = new Label(
				app.getLocalization().getMenu("InputLabel") + Unicode.ELLIPSIS);
		dummy.getElement().getStyle().setMarginLeft(5, Unit.PX);
		this.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		updateWidth();
	}

	private void updateWidth() {
		int width = ((DockPanelW) app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_CAS)).getOffsetWidth() - 35;
		if (width > 0) {
			this.getElement().getStyle().setWidth(width, Unit.PX);
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		// autocommitting empty text produces $1
		if (!isSuggesting()) {
			onEnter(false);
		}
	}

	@Override
	public int getInputSelectionEnd() {
		return 0;
	}

	@Override
	public int getInputSelectionStart() {
		return 0;
	}

	@Override
	public String getInputSelectedText() {
		return null;
	}

	@Override
	public String getText() {
		if (mf == null) {
			return "";
		}
		GeoGebraSerializer s = new GeoGebraSerializer();
		return s.serialize(mf.getFormula());
	}

	@Override
	public String getLaTeX() {
		if (mf == null) {
			return "";
		}
		TeXSerializer s = new TeXSerializer();
		return s.serialize(mf.getFormula());
	}

	@Override
	public String getInput() {
		return getText();
	}

	@Override
	public void setInputSelectionStart(int selStart) {
		// not needed
	}

	@Override
	public void setInputSelectionEnd(int selEnd) {
		// not needed
	}

	@Override
	public void clearInputText() {
		setText("");
	}

	@Override
	public void setInput(String string) {
		if (getWidget(0) != mf.asWidget()) {
			setWidget(mf.asWidget());
		}
		setText(string);
	}

	private void setWidget(Widget asWidget) {
		insert(asWidget, 0);
	}

	@Override
	public void setText(String text0) {
		// removeDummy();
		if (mf != null) {
			Parser parser = new Parser(mf.getMetaModel());
			MathFormula formula;
			try {
				formula = parser.parse(text0);
				mf.setFormula(formula);
			} catch (ParseException e) {
				Log.warn("Problem parsing: " + text0);
				e.printStackTrace();
			}
		}
		// updateLineHeight();
	}

	@Override
	public GeoCasCell getCellEditorValue(int index) {
		if (table != null) {
			return table.getGeoCasCell(index);
		}
		return null;
	}

	@Override
	public void setLabels() {
		// not needed
	}

	@Override
	public void setFocus(boolean focus, boolean scheduled) {
		remove(focus ? dummy : mf);
		if (focus) {
			updateWidth();
		}
		setWidget(focus ? mf.asWidget()
				: dummy);
		mf.setFocus(focus);
	}

	@Override
	public void onEnter(boolean keepFocus) {
		if (sug != null && sug.needsEnterForSuggestion()) {
			return;
		}
		// got here by blur: do not use previous cell ref
		if (!keepFocus && StringUtil.empty(getText())) {
			this.setFocus(false, false);
			return;
		}
		this.controller.handleEnterKey(false, false, app, keepFocus);
	}

	@Override
	public void resetInput() {
		// not needed
	}

	@Override
	public void setAutocomplete(boolean b) {
		this.autocomplete = b;
	}

	@Override
	public void setLaTeX(String plain, String latex) {
		// not needed
	}

	@Override
	public void ensureEditing() {
		final GuiManagerInterfaceW gui = app.getGuiManager();
		gui.setOnScreenKeyboardTextField(this.retexListener);
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(this, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				doClickStart();
				gui.setActivePanelAndToolbar(App.VIEW_CAS);
			}
		});
		setFocus(true, false);
	}

	/**
	 * Click start callback
	 */
	protected void doClickStart() {
		setFocus(true, false);
		app.showKeyboard(retexListener);
		// prevent that keyboard is closed on clicks (changing
		// cursor position)
		CancelEventTimer.keyboardSetVisible();
	}

	@Override
	public boolean getAutoComplete() {
		return autocomplete;
	}

	@Override
	public List<String> resetCompletions() {
		return getInputSuggestions().resetCompletions();
	}

	@Override
	public List<String> getCompletions() {
		return getInputSuggestions().getCompletions();
	}

	@Override
	public void insertString(String text) {
		new MathFieldProcessing(mf).autocomplete(text);
	}

	@Override
	public ArrayList<String> getHistory() {
		return null;
	}

	@Override
	public boolean isSuggesting() {
		return sug != null && sug.isSuggesting();
	}

	@Override
	public void requestFocus() {
		if (getWidget(0) != mf.asWidget()) {
			setWidget(mf.asWidget());
		}
		mf.requestViewFocus();
	}

	@Override
	public Widget toWidget() {
		return this;
	}

	@Override
	public void autocomplete(String text) {
		GuiManagerW.makeKeyboardListener(retexListener, app).insertString(text);
	}

	@Override
	public void updatePosition(AbstractSuggestionDisplay sugPanel) {
		sugPanel.setPositionRelativeTo(this);
	}

	@Override
	public boolean isForCAS() {
		return true;
	}

	@Override
	public String getCommand() {
		return mf == null ? "" : mf.getCurrentWord();
	}

	private InputSuggestions getInputSuggestions() {
		if (sug == null) {
			sug = new InputSuggestions(app, this);
		}
		return sug;
	}

	@Override
	public void onEnter() {
		// TODO or onEnter(false) ?
		onEnter(true);
	}

	@Override
	public void onKeyTyped() {
		getInputSuggestions().popupSuggestions();
		onCursorMove();
	}

	@Override
	public boolean needsAutofocus() {
		return true;
	}

	@Override
	public void onCursorMove() {
		MathFieldW.scrollParent(this, 20);
	}

	@Override
	public void onUpKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyUp();
		}
	}

	@Override
	public void onDownKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyDown();
		}
	}

	@Override
	public String serialize(MathSequence selectionText) {
		return GeoGebraSerializer.serialize(selectionText);
	}

	@Override
	public void onInsertString() {
		mf.setFormula(GeoGebraSerializer.reparse(this.mf.getFormula()));
	}

	@Override
	public boolean onEscape() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasFocus() {
		return mf.hasFocus();
	}

	@Override
	public App getApplication() {
		return app;
	}

	@Override
	public void onTab(boolean shiftDown) {
		// not needed
	}

	public void insertInput(String input) {
		mf.insertString(input);
	}

	public void adjustCaret(HumanInputEvent<?> event) {
		mf.adjustCaret(EventUtil.getTouchOrClickClientX(event),
				EventUtil.getTouchOrClickClientY(event));
	}
}
