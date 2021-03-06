package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.export.pstricks.GeoGebraToAsymptote;
import org.geogebra.common.export.pstricks.GeoGebraToPgf;
import org.geogebra.common.export.pstricks.GeoGebraToPstricks;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GImageIcon;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GeoFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.barycentric.AlgoCubicSwitch;
import org.geogebra.common.kernel.barycentric.AlgoKimberlingWeights;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.AlgoCubicSwitchInterface;
import org.geogebra.common.main.AlgoCubicSwitchParams;
import org.geogebra.common.main.AlgoKimberlingWeightsInterface;
import org.geogebra.common.main.AlgoKimberlingWeightsParams;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.AppConfigDefault;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.AppConfigGeometry;
import org.geogebra.common.main.settings.AppConfigGraphing;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.move.events.BaseEventPool;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.operations.Network;
import org.geogebra.common.move.operations.NetworkOperation;
import org.geogebra.common.move.views.OfflineView;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Language;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.export.GeoGebraToAsymptoteW;
import org.geogebra.web.html5.export.GeoGebraToPgfW;
import org.geogebra.web.html5.export.GeoGebraToPstricksW;
import org.geogebra.web.html5.factories.AwtFactoryW;
import org.geogebra.web.html5.factories.FactoryW;
import org.geogebra.web.html5.factories.FormatFactoryW;
import org.geogebra.web.html5.factories.UtilFactoryW;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.LoadingApplication;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.ViewsChangedListener;
import org.geogebra.web.html5.io.ConstructionException;
import org.geogebra.web.html5.io.MyXMLioW;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.kernel.GeoElementGraphicsAdapterW;
import org.geogebra.web.html5.kernel.UndoManagerW;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;
import org.geogebra.web.html5.move.googledrive.GoogleDriveOperation;
import org.geogebra.web.html5.sound.GTimerW;
import org.geogebra.web.html5.sound.SoundManagerW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.ImageWrapper;
import org.geogebra.web.html5.util.NetworkW;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.html5.util.SpreadsheetTableModelW;
import org.geogebra.web.html5.util.UUIDW;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.html5.util.debug.GeoGebraProfilerW;
import org.geogebra.web.plugin.WebsocketLogger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("javadoc")
public abstract class AppW extends App implements SetLabels {
	public static final String STORAGE_MACRO_KEY = "storedMacro";
	public static final String STORAGE_MACRO_ARCHIVE = "macroArchive";
	public static final String DEFAULT_APPLET_ID = "ggbApplet";

	private static final int LOWER_HEIGHT = 350;

	private DrawEquationW drawEquation;

	private NormalizerMinimal normalizerMinimal;
	private GgbAPIW ggbapi;
	private final LocalizationW loc;
	private ImageManagerW imageManager;
	private GgbFile currentFile = null;
	// random id to identify ggb files
	// eg so that GeoGebraTube can notice it's a version of the same file
	private int localID = -1;
	private long syncStamp;
	protected GoogleDriveOperation googleDriveOperation;

	private FontManagerW fontManager;
	private SpreadsheetTableModelW tableModel;
	private SoundManagerW soundManager;
	protected DialogManager dialogManager = null;

	protected MaterialsManagerI fm;
	private Material activeMaterial;

	protected final ArticleElement articleElement;

	protected EuclidianPanelWAbstract euclidianViewPanel;
	protected Canvas canvas;

	private final GLookAndFeelI laf;

	protected ArrayList<Widget> popups = new ArrayList<>();
	private boolean justClosedPopup = false;
	// protected GeoGebraFrame frame = null;

	private GlobalKeyDispatcherW globalKeyDispatcher;

	// when losing focus, remembering it so that ENTER can give focus back
	private static volatile Element lastActiveElement = null;
	// but not in case of anything important in any app has focus,
	// we shall set it to true in each of those cases, e.g. AV input bar too !!!
	private static boolean anyAppHasFocus = true;

	private ReaderTimer readerTimer;
	protected final String initialPerspective;
	private boolean headerVisible = !AppW.smallScreen();
	private boolean toolLoadedFromStorage;
	private Storage storage;
	private ScriptLoadCallback scriptCallback;
	WebsocketLogger webSocketLogger = null;
	private boolean keyboardNeeded;
	private String externalPath;
	private ArrayList<ViewsChangedListener> viewsChangedListener = new ArrayList<>();
	private GDimension preferredSize;
	private NetworkOperation networkOperation;
	private PageListControllerInterface pageController;

	/*
	 * True if showing the "alpha" in Input Boxes is allowed. (we can hide the
	 * symbol buttons with data-param-allowSymbolTable parameter)
	 */
	private boolean allowSymbolTables = true;
	private boolean allowStyleBar = true;
	private TimerSystemW timers;
	HashMap<String, String> revTranslateCommandTable = new HashMap<>();
	private ArrayList<FileLoadListener> fileLoadListeners = new ArrayList<>();
	private Runnable closeBroserCallback;
	private Runnable insertImageCallback;
	private ArrayList<MouseTouchGestureControllerW> euclidianHandlers = new ArrayList<>();

	Timer timeruc = new Timer() {
		@Override
		public void run() {
			updateConsBoundingBox();
		}
	};

	Scheduler.ScheduledCommand sucCallback = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			// 0.5 seconds is good for the user and maybe for the computer
			// too
			timeruc.schedule(500);
		}
	};

	/**
	 * @param ae
	 *            {@link ArticleElement}
	 * @param dimension
	 *            int
	 * @param laf
	 *            (null for webSimple) {@link GLookAndFeelI}
	 */
	protected AppW(ArticleElement ae, int dimension, GLookAndFeelI laf) {
		super(getVersion(ae, dimension, laf));
		if ("graphing".equals(ae.getDataParamAppName())) {
			this.initialPerspective = "1";
		} else if ("geometry".equals(ae.getDataParamAppName())) {
			this.initialPerspective = "2";
		} else {
			this.initialPerspective = ae.getDataParamPerspective();
		}
		setPrerelease(ae.getDataParamPrerelease());

		// laf = null in webSimple
		setUndoRedoEnabled(ae.getDataParamEnableUndoRedo()
				&& (laf == null || laf.undoRedoSupported()));

		if (ae.getDataParamPerspective().startsWith("exam")) {
			setNewExam();
			ae.setAttribute("data-param-perspective", "");
		}
		this.loc = new LocalizationW(dimension);
		this.articleElement = ae;
		ArticleElement.addNativeFocusHandler(articleElement, this);
		this.laf = laf;

		getTimerSystem();
		this.showInputTop = InputPosition.algebraView;
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				fitSizeToScreen();
				windowResized();
			}
		});
	}

	protected void fitSizeToScreen() {
		if (getArticleElement().getDataParamFitToScreen()) {
			updateHeaderVisible();
			getGgbApi().setSize(Window.getClientWidth(),
					getArticleElement().computeHeight());
			if (has(Feature.HELP_AND_SHORTCUTS_IMPROVEMENTS)) {
				getAccessibilityManager().focusMenu();
			}
		}
	}

	/**
	 * Update the visibility of external header
	 */
	public void updateHeaderVisible() {
		Element header = Dom.querySelector("GeoGebraHeader");
		if (header != null) {
			boolean visible = !AppW.smallScreen();
			header.getStyle().setDisplay(visible ? Display.FLEX : Display.NONE);
			if (headerVisible != visible) {
				headerVisible = visible;
				onHeaderVisible();
			}
			getAppletFrame().updateArticleHeight();
		}
	}

	public void removeHeader() {
		Element header = Dom.querySelector("GeoGebraHeader");
		if (header != null) {
			header.removeFromParent();
			getArticleElement().setAttribute("data-param-marginTop", "0");
			fitSizeToScreen();
		}
	}

	/**
	 * Called if header visibility is changed.
	 */
	public void onHeaderVisible() {
		// TODO listener (?)
	}

	public static boolean smallScreen() {
		return Window.getClientWidth() < 600 || Window.getClientHeight() < 600;
	}

	private static Versions getVersion(ArticleElement ae, int dimension,
			GLookAndFeelI laf2) {
		return laf2 == null ? Versions.WEB_FOR_BROWSER_SIMPLE
				: laf2.getVersion(dimension, ae.getDataParamAppName());
	}

	protected final void windowResized() {
		for (MouseTouchGestureControllerW mtg : this.euclidianHandlers) {
			mtg.calculateEnvironment();
		}
		if (this.getGuiManager() != null) {
			getGuiManager().setPixelRatio(getPixelRatio());
			getGuiManager().updateFullscreen();

		}

	}

	@Override
	public final void resetUniqueId() {
		uniqueId = UUIDW.randomUUID().toString();
	}

	/**
	 * @return id of local saved file
	 */
	public int getLocalID() {
		return this.localID;
	}

	/**
	 * sets ID of local saved file
	 * 
	 * @param id
	 *            int
	 */
	public void setLocalID(int id) {
		this.localID = id;
	}

	@Override
	public final DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationW();
		}

		return drawEquation;
	}

	@Override
	public final SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManagerW(this);
		}
		return soundManager;
	}

	@Override
	public GgbAPIW getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new GgbAPIW(this);
		}
		return ggbapi;
	}

	/**
	 * @return {@link Canvas}
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	@Override
	public final NormalizerMinimal getNormalizer() {
		if (normalizerMinimal == null) {
			normalizerMinimal = new NormalizerMinimal();
		}

		return normalizerMinimal;
	}

	/**
	 * inits factories
	 */
	protected void initFactories() {

		if (FormatFactory.getPrototype() == null) {
			FormatFactory.setPrototypeIfNull(new FormatFactoryW());
		}

		if (AwtFactory.getPrototype() == null) {
			AwtFactory.setPrototypeIfNull(new AwtFactoryW());
		}

		if (StringUtil.getPrototype() == null) {
			StringUtil.setPrototypeIfNull(new StringUtil());
		}

		if (CASFactory.getPrototype() == null) {
			CASFactory.setPrototype((CASFactory) GWT.create(CASFactory.class));
		}

		if (UtilFactory.getPrototype() == null) {
			UtilFactory.setPrototypeIfNull(new UtilFactoryW());
		}

	}

	protected void afterCoreObjectsInited() {
		// TODO: abstract?
	}

	@Override
	final public GlobalKeyDispatcherW getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
			if (articleElement != null && articleElement.getDataParamApp()) {
				globalKeyDispatcher.setFocused(true);
			}
		}
		return globalKeyDispatcher;
	}

	/**
	 * @return a new instance of {@link GlobalKeyDispatcherW}
	 */
	private GlobalKeyDispatcherW newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcherW(this);
	}

	@Override
	public EuclidianViewW getEuclidianView1() {
		return (EuclidianViewW) euclidianView;
	}

	public TimerSystemW getTimerSystem() {
		if (timers == null) {
			timers = new TimerSystemW(this);
		}
		return timers;
	}

	@Override
	protected ScriptManager newScriptManager() {
		return new ScriptManagerW(this);
	}

	// ================================================
	// native JS
	// ================================================

	@Override
	public void callAppletJavaScript(String fun, Object[] args) {
		if (args == null || args.length == 0) {
			JsEval.callNativeJavaScript(fun);
		} else if (args.length == 1) {
			Log.debug("calling function: " + fun + "(" + args[0].toString()
					+ ")");
			JsEval.callNativeJavaScript(fun, args[0].toString());
		} else {
			JsArrayString jsStrings = (JsArrayString) JavaScriptObject
					.createArray();
			for (Object obj : args) {
				jsStrings.push(obj.toString());
			}
			JsEval.callNativeJavaScriptMultiArg(fun, jsStrings);
		}

	}

	public void callAppletJavaScript(String fun, Object arg0, Object arg1) {
		if (arg0 == null && arg1 == null) {
			JsEval.callNativeJavaScript(fun);
		} else if (arg0 != null && arg1 == null) {
			// Log.debug("calling function: " + fun + "(" + arg0.toString()
			// + ")");
			JsEval.callNativeJavaScript(fun, arg0.toString());
		} else if (arg0 != null && arg1 != null) {
			JsEval.callNativeJavaScriptMultiArg(fun, arg0.toString(),
					arg1.toString());
		}

	}

	@Override
	public boolean loadXML(String xml) throws Exception {
		getXMLio().processXMLString(xml, true, false);
		return true;
	}

	@Override
	public MyXMLioW createXMLio(Construction cons) {
		return new MyXMLioW(cons.getKernel(), cons);
	}

	void doSetLanguage(String lang) {
		getLocalization().setLanguage(lang);

		// make sure digits are updated in all numbers
		getKernel().updateConstructionLanguage();

		// update display & Input Bar Dictionary etc
		setLabels();

		notifyLocalizationLoaded();
		// importatnt for accessibility
		getFrameElement().setLang(lang == null ? "" : lang.replace("_", "-"));
	}

	public void notifyLocalizationLoaded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLanguage(final String browserLang) {
		final String lang = Language
				.getClosestGWTSupportedLanguage(browserLang).getLocaleGWT();
		if (scriptCallback != null) {
			scriptCallback.cancel();
		}
		if (lang != null && lang.equals(loc.getLocaleStr())) {
			Log.debug("Language is already " + loc.getLocaleStr());
			setLabels();
			notifyLocalizationLoaded();
			return;
		}
		if (lang == null || "".equals(lang)) {
			Log.warn("language being set to empty string");
			setLanguage("en");
			return;
		}

		Log.debug("setting language to:" + lang + ", browser lang:"
				+ browserLang);

		if (Browser.supportsSessionStorage()
				&& LocalizationW.loadPropertiesFromStorage(lang,
						GeoGebraConstants.VERSION_STRING)) {
			doSetLanguage(lang);
		} else {
			// load keys (into a JavaScript <script> tag)
			ScriptElement script = Document.get().createScriptElement();
			String url = GWT.getModuleBaseURL();
			if (url.startsWith(GeoGebraConstants.CDN_APPS + "latest")) {
				url = GeoGebraConstants.CDN_APPS
						+ GeoGebraConstants.VERSION_STRING + "/web3d/";
			}
			script.setSrc(url + "js/properties_keys_" + lang + ".js");
			scriptCallback = new ScriptLoadCallback() {
				private boolean canceled = false;

				@Override
				public void onLoad() {
					if (canceled) {
						Log.debug("Async language file load canceled.");
						return;
					}
					// force reload
					doSetLanguage(lang);

					updateKeyboardLanguage();

					if (Browser.supportsSessionStorage()) {
						LocalizationW.savePropertiesToStorage(lang,
								GeoGebraConstants.VERSION_STRING);
					}
				}

				@Override
				public void onError() {
					if (canceled) {
						Log.debug("Async language file load canceled.");
						return;
					}
					LocalizationW.loadPropertiesFromStorage(lang, "");
					doSetLanguage(lang);
				}

				@Override
				public void cancel() {
					canceled = true;

				}

			};
			ResourcesInjector.addLoadHandler(script, scriptCallback);
			Document.get().getBody().appendChild(script);

		}
	}

	void updateKeyboardLanguage() {
		if (getGuiManager() != null) {
			getGuiManager().updateKeyboardLanguage();
		}
	}

	/**
	 * @param language
	 *            language ISO code
	 * @param country
	 *            country or country_variant
	 */
	public void setLanguage(String language, String country) {

		if (language == null || "".equals(language)) {
			Log.warn("error calling setLanguage(), setting to English (US): "
					+ language + "_" + country);
			setLanguage("en");
			return;
		}

		if (country == null || "".equals(country)) {
			setLanguage(language);
			return;
		}
		this.setLanguage(language + "_" + country);
	}

	@Override
	public LocalizationW getLocalization() {
		return loc;
	}

	/**
	 * Translates localized command name into internal TODO check whether this
	 * differs from translateCommand somehow and either document it or remove
	 * this method
	 * 
	 * @param cmd
	 *            localized command name
	 * @return internal command name
	 */
	@Override
	final public String getInternalCommand(String cmd) {
		initTranslatedCommands();
		String s;
		String cmdLower = StringUtil.toLowerCaseUS(cmd);
		Commands[] values = Commands.values();
		if (revTranslateCommandTable.isEmpty()) {// we should clear this cache
													// on language change!
			for (Commands c : values) {// and fill it now if needed
				s = Commands.englishToInternal(c).name();

				// make sure that when si[] is typed in script, it's changed to
				// Si[] etc
				String lowerCaseCmd = StringUtil
						.toLowerCaseUS(getLocalization().getCommand(s));
				revTranslateCommandTable.put(lowerCaseCmd, s);
			}
		}
		return revTranslateCommandTable.get(cmdLower);
		// return null;
	}

	@Override
	protected void fillCommandDict() {
		super.fillCommandDict();
		revTranslateCommandTable.clear();
	}

	/**
	 * This method checks if the command is stored in the command properties
	 * file as a key or a value.
	 * 
	 * @param command
	 *            : a value that should be in the command properties files (part
	 *            of Internationalization)
	 * @return the value "command" after verifying its existence.
	 */
	@Override
	final public String getReverseCommand(String command) {

		if (loc.getLanguage() == null) {
			// keys not loaded yet
			return command;
		}

		return super.getReverseCommand(command);
	}

	public void loadGgbFile(GgbFile archiveContent, boolean asSlide) throws Exception {
		AlgebraSettings algebraSettings = getSettings().getAlgebra();
		algebraSettings.setModeChanged(false);

		loadFile(archiveContent, asSlide);

		if (!algebraSettings.isModeChanged()) {
			algebraSettings.setTreeMode(SortMode.TYPE);
		}
	}

	/**
	 * @param dataUrl
	 *            the data url to load the ggb file
	 */
	public void loadGgbFileAsBase64Again(String dataUrl) {
		prepareReloadGgbFile();
		ViewW view = new ViewW(null, this);
		view.processBase64String(dataUrl);
	}

	public void loadGgbFileAsBinaryAgain(JavaScriptObject binary) {
		prepareReloadGgbFile();
		ViewW view = new ViewW(null, this);
		view.processBinaryString(binary);
	}

	private void prepareReloadGgbFile() {
		getImageManager().reset();
	}

	private void loadFile(GgbFile archiveContent, final boolean asSlide)
			throws Exception {
		if (getPageController() != null
				&& getPageController().loadSlides(archiveContent)) {
			return;
		}

		beforeLoadFile();

		GgbFile archive = archiveContent.duplicate();

		// Handling of construction and macro file
		final String construction = archive.remove(MyXMLio.XML_FILE);
		final String macros = archive.remove(MyXMLio.XML_FILE_MACRO);
		final String defaults2d = archive.remove(MyXMLio.XML_FILE_DEFAULTS_2D);
		final String defaults3d = is3D() ?

				archive.remove(MyXMLio.XML_FILE_DEFAULTS_3D) : null;

		String libraryJS = archive.remove(MyXMLio.JAVASCRIPT_FILE);

		// Construction (required)
		if (construction == null && macros == null) {
			throw new ConstructionException(
					"File is corrupt: No GeoGebra data found");
		}

		if (construction != null) {
			// ggb file: remove all macros from kernel before processing
			kernel.removeAllMacros();
		}

		// Macros (optional)
		// moved after the images are loaded, because otherwise
		// exception might come for macros which use images
		// if (macros != null) {
		// // macros = DataUtil.utf8Decode(macros);
		// // //DataUtil.utf8Decode(macros);
		// getXMLio().processXMLString(macros, true, true);
		// }

		// Library JavaScript (optional)
		if (libraryJS == null) { // TODO: && !isGGTfile)
			kernel.resetLibraryJavaScript();
		} else {
			kernel.setLibraryJavaScript(libraryJS);
		}

		if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
				maybeProcessImage(entry.getKey(), entry.getValue());
			}
		}

		if (construction == null) {
			if (macros != null) {
				getXMLio().processXMLString(macros, true, true);
			}

			setCurrentFile(archiveContent);
			afterLoadFileAppOrNot(asSlide);
			if (!hasMacroToRestore()) {
				getGuiManager().refreshCustomToolsInToolBar();
			}
			getGuiManager().updateToolbar();
			return;
		}
		Runnable afterImages = new Runnable() {

			public void run() {
				try {
					setHideConstructionProtocolNavigation();
					Log.debug("images loaded");
					// Macros (optional)
					if (macros != null) {
						// macros = DataUtil.utf8Decode(macros);
						// //DataUtil.utf8Decode(macros);
						getXMLio().processXMLString(macros, true, true);
					}

					getXMLio().processXMLString(construction, true, false);
					// defaults (optional)
					if (defaults2d != null) {
						getXMLio().processXMLString(defaults2d, false, true);
					}
					if (defaults3d != null) {
						getXMLio().processXMLString(defaults3d, false, true);
					}
					afterLoadFileAppOrNot(asSlide);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		if (!getImageManager().hasImages()) {
			afterImages.run();
			setCurrentFile(archiveContent);
			// getKernel().setNotifyViewsActive(true);
		} else {
			// on images do nothing here: wait for callback when images loaded.
			getImageManager().triggerImageLoading(this, afterImages);
			setCurrentFile(archiveContent);

		}
	}

	/**
	 * Sets viewId for each macro
	 * 
	 * @param toolbar3D
	 *            toolbar string for 3D View that includes its macros as well.
	 */
	public void setMacroViewIds(String toolbar3D) {
		int macroCount = kernel.getMacroNumber();
		for (int i = 0; i < macroCount; i++) {
			Macro macro = kernel.getMacro(i);
			if (macro.getViewId() == null) {
				int macroMode = EuclidianConstants.MACRO_MODE_ID_OFFSET + i;
				Log.debug("[MM] " + macro);
				if (toolbar3D.contains(String.valueOf(macroMode))) {
					macro.setViewId(VIEW_EUCLIDIAN3D);
				} else {
					macro.setViewId(VIEW_EUCLIDIAN);
				}
			}
		}
	}

	/**
	 * Prepare for loading file
	 */
	public void beforeLoadFile() {
		startCollectingRepaints();
		// make sure the image manager will not wait for images from the *old*
		// file
		if (this.getImageManager() != null) {
			this.getImageManager().reset();
		}
		getEuclidianView1().setReIniting(true);
		if (hasEuclidianView2EitherShowingOrNot(1)) {
			getEuclidianView2(1).setReIniting(true);
		}
	}

	public void setCurrentFile(GgbFile file) {
		if (currentFile == file) {
			return;
		}

		currentFile = file;
	}

	@Override
	public void resetCurrentFile() {
		setCurrentFile(null);
	}

	/**
	 * @return controller for page control panel
	 */
	public PageListControllerInterface getPageController() {
		return pageController;
	}

	/**
	 * @param pageController
	 *            {@link PageListControllerInterface}
	 */
	public void setPageController(PageListControllerInterface pageController) {
		this.pageController = pageController;
	}

	/**
	 * @return current .zip file as hashmap
	 */
	public HashMap<String, String> getCurrentFile() {
		return currentFile;
	}

	@Override
	public void reset() {
		if (currentFile != null) {
			try {
				loadGgbFile(currentFile, false);
			} catch (Exception e) {
				clearConstruction();
			}
		} else {
			clearConstruction();
		}
	}

	private void maybeProcessImage(String filename0, String content) {
		String fn = filename0.toLowerCase();
		if (fn.equals(MyXMLio.XML_FILE_THUMBNAIL)) {
			return; // Ignore thumbnail
		}

		FileExtensions ext = StringUtil.getFileExtension(fn);

		// Ignore non image files
		if (!ext.isImage()) {
			return;
		}
		String filename = filename0;
		// bug in old versions (PNG saved with wrong extension)
		// change BMP, TIFF, TIF -> PNG
		if (!ext.isAllowedImage()) {
			filename = StringUtil.changeFileExtension(filename,
					FileExtensions.PNG);
		}

		// for file names e.g. /geogebra/main/nav_play.png in GeoButtons
		// Log.debug("filename2 = " + filename);
		// Log.debug("ext2 = " + ext);

		if (ext.equals(FileExtensions.SVG)) {
			// IE11/12 seems to require SVG to be base64 encoded
			addExternalImage(filename, StringUtil.svgMarker
					+ Browser.encodeBase64(content));
		} else {
			addExternalImage(filename, content);
		}
	}

	public void addExternalImage(String filename, String src) {
		getImageManager().addExternalImage(filename, src);
	}

	@Override
	public final ImageManagerW getImageManager() {
		return imageManager;
	}

	protected void initImageManager() {
		imageManager = new ImageManagerW();
	}

	@Override
	public final void setXML(String xml, boolean clearAll) {
		if (clearAll) {
			setCurrentFile(null);
		}

		try {
			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);
			getXMLio().processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
		} catch (Exception e) {
			e.printStackTrace();
			localizeAndShowError("LoadFileFailed");
		}
	}

	@Override
	public boolean clearConstruction() {
		// if (isSaved() || saveCurrentFile()) {
		kernel.clearConstruction(true);

		kernel.initUndoInfo();
		resetMaxLayerUsed();
		setCurrentFile(null);
		setMoveMode();

		return true;

		// }
		// return false;
	}

	@Override
	public final MyImage getExternalImageAdapter(String fileName, int width,
			int height) {
		ImageElement im = getImageManager().getExternalImage(fileName, this);
		if (im == null) {
			return null;
		}
		if (width != 0 && height != 0) {
			im.setWidth(width);
			im.setHeight(height);
		}
		return new MyImageW(im, fileName.toLowerCase().endsWith(".svg"));
	}

	@Override
	public final UndoManager getUndoManager(Construction cons) {
		return new UndoManagerW(cons);
	}

	@Override
	public final GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new GeoElementGraphicsAdapterW(this);
	}

	@Override
	public final void runScripts(final GeoElement geo1, final String string) {
		invokeLater(new Runnable() {
			@Override
			public void run() {
				geo1.runClickScripts(string);
			}
		});
	}

	@Override
	public void invokeLater(final Runnable runnable) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				runnable.run();
			}
		});
	}

	@Override
	public final CASFactory getCASFactory() {
		return CASFactory.getPrototype();
	}

	@Override
	public final void fileNew() {
		// clear all
		// triggers the "do you want to save" dialog
		// so must be called first
		if (!clearConstruction()) {
			return;
		}

		resetUniqueId();
		setLocalID(-1);
		resetActiveMaterial();

		if (getGoogleDriveOperation() != null) {
			getGoogleDriveOperation().resetStorageInfo();
		}
		resetUI();
	}

	protected void resetUI() {
		kernel.getInputPreviewHelper().clear();
		clearInputBar();
		if (!has(Feature.INITIAL_PORTRAIT) && isPortrait()) {
			adjustViews(false, false);
		}

	}

	/**
	 * @param macro
	 *            Macro need to be stored.
	 * @param writeBack
	 *            Is it a new one or a modification.
	 */
	public void storeMacro(Macro macro, boolean writeBack) {
		createStorage();
		if (storage == null) {
			return;
		}

		String b64 = getGgbApi().getMacrosBase64();

		storage.setItem(STORAGE_MACRO_ARCHIVE, b64);

		storage.setItem(STORAGE_MACRO_KEY, macro.getToolName());

		if (writeBack) {
			return;
		}

		// Storage.addStorageEventHandler(new StorageEvent.Handler() {
		//
		// public void onStorageChange(StorageEvent event) {
		// if (STORAGE_MACRO_KEY.equals(event.getKey())) {
		// Log.debug("[STORAGE] '" + STORAGE_MACRO_KEY
		// + "' has changed.");
		// }
		// }
		// });

	}

	protected void createStorage() {
		if (storage == null) {
			storage = Storage.getSessionStorageIfSupported();
		}

	}

	protected boolean hasMacroToRestore() {
		createStorage();
		if (storage != null) {
			StorageMap map = new StorageMap(storage);
			if (map.containsKey(STORAGE_MACRO_ARCHIVE)) {
				return true;
			}
		}

		return false;

	}

	protected void restoreMacro() {

		createStorage();
		if (storage != null) {
			StorageMap map = new StorageMap(storage);
			if (map.containsKey(STORAGE_MACRO_ARCHIVE)) {
				getKernel().removeAllMacros();
				String b64 = storage.getItem(STORAGE_MACRO_ARCHIVE);
				getGgbApi().setBase64(b64);
			}
		}

	}

	protected boolean openMacroFromStorage() {
		createStorage();

		if (storage != null) {
			StorageMap map = new StorageMap(storage);
			if (map.containsKey(STORAGE_MACRO_KEY)) {
				String macroName = storage.getItem(STORAGE_MACRO_KEY);
				try {
					// Log.debug("[STORAGE] restoring macro " + macroName);
					openMacro(macroName);
					Window.setTitle(macroName);
					setToolLoadedFromStorage(true);
					return true;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * sets the timestamp of last synchronization with ggbTube
	 * 
	 * @param syncStamp
	 *            long
	 */
	public void setSyncStamp(long syncStamp) {
		this.syncStamp = syncStamp;
	}

	/**
	 * @return timestamp of last synchronization with ggbTube
	 */
	public long getSyncStamp() {
		return this.syncStamp;
	}

	/**
	 * @return GoogleDriveOperation
	 */
	public GoogleDriveOperation getGoogleDriveOperation() {
		return googleDriveOperation;
	}

	public boolean openFile(JavaScriptObject fileToHandle,
			JavaScriptObject callback) {
		resetPerspectiveParam();
		return doOpenFile(fileToHandle, callback);
	}

	public void resetPerspectiveParam() {
		if (getArticleElement() != null) {
			getArticleElement().setAttribute("data-param-perspective", "");
		}

	}

	/**
	 * Opens the ggb or ggt file
	 * 
	 * @param fileToHandle
	 * @param callback
	 * @return returns true, if fileToHandle is ggb or ggt file, otherwise
	 *         returns false. Note that If the function returns true, it's don't
	 *         mean, that the file opening was successful, and the opening
	 *         finished already.
	 */
	public native boolean doOpenFile(JavaScriptObject fileToHandle,
			JavaScriptObject callback) /*-{
		var ggbRegEx = /\.(ggb|ggt|ggs|csv|off)$/i;
		if (!fileToHandle.name.toLowerCase().match(ggbRegEx))
			return false;

		var appl = this;
		var reader = new FileReader();
		reader.onloadend = function(ev) {
			if (reader.readyState === reader.DONE) {
				var fileStr = reader.result;
				if (fileToHandle.name.toLowerCase().match(/\.(ggb|ggt|ggs)$/i)) {

					appl.@org.geogebra.web.html5.main.AppW::loadGgbFileAsBase64Again(Ljava/lang/String;)(fileStr);
				}
				if (fileToHandle.name.toLowerCase().match(/\.(csv)$/i)) {
					appl.@org.geogebra.web.html5.main.AppW::openCSV(Ljava/lang/String;)(atob(fileStr.substring(fileStr.indexOf(",")+1)));
				}
				if (fileToHandle.name.toLowerCase().match(/\.(off)$/i)) {
					appl.@org.geogebra.web.html5.main.AppW::openOFF(Ljava/lang/String;)(atob(fileStr.substring(fileStr.indexOf(",")+1)));
				}
				if (callback != null)
					callback();
			}
		};
		reader.readAsDataURL(fileToHandle);
		return true;
	}-*/;

	/**
	 * NEVER CALLED??
	 */
	public void addFileLoadListener(FileLoadListener f) {
		this.fileLoadListeners.add(f);
	}

	/**
	 * Notify listeners about loaded file, see
	 * {@link #addFileLoadListener(FileLoadListener)}
	 */
	public final void notifyFileLoaded() {
		for (FileLoadListener listener : fileLoadListeners) {
			listener.onFileLoad();
		}

	}

	@Override
	public double getMillisecondTime() {
		return GeoGebraProfilerW.getMillisecondTimeNative();
	}

	// public native void copyBase64NonWebApp(String str) /*-{
	// var userAgent = $wnd.navigator.userAgent.toLowerCase();
	// if ((userAgent.indexOf('msie') > -1)
	// || (userAgent.indexOf('trident') > -1)) {
	// // It is a good question what shall we do in Internet Explorer?
	// // Security settings may block clipboard, new browser tabs,
	// window.prompt, alert
	// // Use a custom alert! but this does not seem to work either
	//
	// //this.@org.geogebra.web.html5.main.GlobalKeyDispatcherW::showConfirmDialog(Ljava/lang/String;)(str);
	// // alternative, better than nothing, but not always working
	// //if ($wnd.clipboardData) {
	// // $wnd.clipboardData.setData('Text', str);
	// //}
	//
	// // then just do the same as in other cases, for now
	// if ($wnd.prompt) {
	// $wnd.prompt('Base64', str);
	// } else {
	// this.@org.geogebra.web.html5.main.AppW::showConfirmDialog(Ljava/lang/String;Ljava/lang/String;)("Base64",
	// str);
	// }
	// } else {
	// // otherwise, we should do the following:
	// if ($wnd.prompt) {
	// $wnd.prompt('Base64', str);
	// } else {
	// this.@org.geogebra.web.html5.main.AppW::showConfirmDialog(Ljava/lang/String;Ljava/lang/String;)("Base64",
	// str);
	// }
	// }
	// }-*/;

	/**
	 * @param str
	 *            string to copy
	 */
	public void copyBase64ToClipboardChromeWebAppCase(String str) {
		// This should do nothing in webSimple!
	}

	/**
	 * @param title
	 *            confirmation dialog title
	 * @param mess
	 *            message
	 */
	public void showConfirmDialog(String title, String mess) {
		// This should do nothing in webSimple!
	}

	/**
	 * @param id
	 *            material ID
	 * @param onError
	 *            callback for errors
	 */
	public void openMaterial(String id, Runnable onError) {
		// only with GUI

	}

	/**
	 * @return OfflineOperation event flow
	 */
	public NetworkOperation getNetworkOperation() {
		return networkOperation;
	}

	/**
	 * Initialize online/offline state listener
	 */
	protected void initNetworkEventFlow() {

		Network network = new NetworkW();

		networkOperation = new NetworkOperation(network);
		BaseEventPool offlineEventPool = new BaseEventPool(networkOperation,
				false);
		NetworkW.attach("offline", offlineEventPool);
		BaseEventPool onlineEventPool = new BaseEventPool(networkOperation,
				true);
		NetworkW.attach("online", onlineEventPool);
		OfflineView ov = new OfflineView();
		networkOperation.setView(ov);
	}

	public void setAllowSymbolTables(boolean allowST) {
		allowSymbolTables = allowST;
	}

	/**
	 * @return true, if alpha buttons may be visible in input boxes.
	 */
	public boolean isAllowedSymbolTables() {
		return allowSymbolTables;
	}

	/**
	 * @param flag
	 *            whether stylebar can be shown also when menubar is hidden
	 */
	public void setAllowStyleBar(boolean flag) {
		allowStyleBar = flag;
	}

	/**
	 * @return whether it's allowed to show stylebar even if menubar closed
	 */
	public boolean isStyleBarAllowed() {
		return allowStyleBar;
	}

	@Override
	public AlgoKimberlingWeightsInterface getAlgoKimberlingWeights() {
		if (getKimberlingw() != null) {
			return getKimberlingw();
		}

		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				setKimberlingw(new AlgoKimberlingWeights());
				setKimberlingWeightFunction(getKimberlingw());
				getKernel().updateConstruction();
			}

			@Override
			public void onFailure(Throwable reason) {
				Log.warn("AlgoKimberlingWeights loading failure");
			}
		});
		return getKimberlingw();
	}

	public native void setKimberlingWeightFunction(
			AlgoKimberlingWeightsInterface kimberlingw) /*-{
		$wnd.geogebraKimberlingWeight = function(obj) {
			return kimberlingw.@org.geogebra.common.main.AlgoKimberlingWeightsInterface::weight(Lorg/geogebra/common/main/AlgoKimberlingWeightsParams;)(obj);
		}
	}-*/;

	@Override
	public native double kimberlingWeight(
			AlgoKimberlingWeightsParams kparams) /*-{

		if ($wnd.geogebraKimberlingWeight) {
			return $wnd.geogebraKimberlingWeight(kparams);
		}

		// should not execute!
		return 0;

	}-*/;

	@Override
	public AlgoCubicSwitchInterface getAlgoCubicSwitch() {
		if (getCubicw() != null) {
			return getCubicw();
		}

		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				setCubicw(new AlgoCubicSwitch());
				setCubicSwitchFunction(getCubicw());
				getKernel().updateConstruction();
			}

			@Override
			public void onFailure(Throwable reason) {
				Log.debug("AlgoKimberlingWeights loading failure");
			}
		});
		return getCubicw();
	}

	public native void setCubicSwitchFunction(
			AlgoCubicSwitchInterface cubicw) /*-{
		$wnd.geogebraCubicSwitch = function(obj) {
			return cubicw.@org.geogebra.common.main.AlgoCubicSwitchInterface::getEquation(Lorg/geogebra/common/main/AlgoCubicSwitchParams;)(obj);
		}
	}-*/;

	@Override
	public native String cubicSwitch(AlgoCubicSwitchParams kparams) /*-{

		if ($wnd.geogebraCubicSwitch) {
			return $wnd.geogebraCubicSwitch(kparams);
		}

		// should not execute!
		return 0;

	}-*/;

	@Override
	public CommandDispatcher getCommandDispatcher(Kernel k) {
		CommandDispatcher cmd = new CommandDispatcherW(k);
		if (!enableGraphing()) {
			cmd.setEnabled(false);
		}
		return cmd;
	}

	/**
	 * @param viewId
	 * @return the plotpanel euclidianview
	 */
	public EuclidianViewW getPlotPanelEuclidianView(int viewId) {
		if (getGuiManager() == null) {
			return null;
		}
		return (EuclidianViewW) getGuiManager().getPlotPanelView(viewId);
	}

	public boolean isPlotPanelEuclidianView(int viewID) {
		if (getGuiManager() == null) {
			return false;
		}
		return getGuiManager().getPlotPanelView(viewID) != null;
	}

	/**
	 * Loads an image and puts it on the canvas (this happens on webcam input)
	 * On drag&drop or insert from URL this would be called too, but that would
	 * set security exceptions
	 * 
	 * @param url
	 *            - the data url of the image
	 * @param clientx
	 *            - desired position on the canvas (x) - unused
	 * @param clienty
	 *            - desired position on the canvas (y) - unused
	 */
	public void urlDropHappened(String url, int clientx, int clienty) {

		// Filename is temporarily set until a better solution is found
		// TODO: image file name should be reset after the file data is
		// available

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(url);

		// with dummy extension, maybe gif or jpg in real
		String imgFileName = zip_directory + ".png";

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = org.geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zip_directory + '/' + fn;

		doDropHappened(imgFileName, url);
		if (insertImageCallback != null) {
			this.insertImageCallback.run();
		}
	}

	/**
	 * Loads an image and puts it on the canvas (this happens by drag & drop)
	 * 
	 * @param imgFileName
	 *            - the file name of the image
	 * @param fileStr
	 *            - the image data url
	 * @param notUsed
	 *            - not used
	 * @param loc
	 */
	public void imageDropHappened(String imgFileName, String fileStr,
			String notUsed) {

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(fileStr);

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = org.geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		fn = zip_directory + '/' + fn;

		doDropHappened(fn, fileStr);
	}

	/**
	 * @param pt
	 *            drop location TODO make sure it's used
	 */
	private void doDropHappened(final String imgFileName, String fileStr) {

		Construction cons = getKernel().getConstruction();
		getImageManager().addExternalImage(imgFileName, fileStr);
		final GeoImage geoImage = new GeoImage(cons);
		getImageManager().triggerSingleImageLoading(imgFileName, geoImage);

		final ImageWrapper img = new ImageWrapper(
				getImageManager().getExternalImage(imgFileName, this));
		img.attachNativeLoadHandler(getImageManager(), new ImageLoadCallback() {
			@Override
			public void onLoad() {
				geoImage.setImageFileName(imgFileName,
						img.getElement().getWidth(),
						img.getElement().getHeight());
				getGuiManager().setImageCornersFromSelection(geoImage);
				if (getImageManager().isPreventAuxImage()) {
					geoImage.setAuxiliaryObject(false);
				}
				setDefaultCursor();
			}
		});
	}

	/**
	 * Opens the image file
	 * 
	 * @param fileToHandle
	 *            javascript handle for the file
	 * @param callback
	 *            load callback
	 * @return returns true, if fileToHandle image file, otherwise return false.
	 *         Note that If the function returns true, it's don't mean, that the
	 *         file opening was successful, and the opening finished already.
	 */
	public native boolean openFileAsImage(JavaScriptObject fileToHandle,
			JavaScriptObject callback) /*-{
		var imageRegEx = /\.(png|jpg|jpeg|gif|bmp)$/i;
		if (!fileToHandle.name.toLowerCase().match(imageRegEx))
			return false;

		var appl = this;
		var reader = new FileReader();
		reader.onloadend = function(ev) {
			if (reader.readyState === reader.DONE) {
				var fileStr = reader.result;
				var fileName = fileToHandle.name;
				appl.@org.geogebra.web.html5.main.AppW::imageDropHappened(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(fileName, fileStr, fileStr);
				if (callback != null) {
					callback();
				}
			}
		};
		reader.readAsDataURL(fileToHandle);
		return true;
	}-*/;

	/**
	 * @return the id of the articleelement
	 */
	public String getArticleId() {
		return articleElement.getId();
	}

	/**
	 * @param articleid
	 *            the article id added by scriptManager
	 * 
	 *            this method is called by scriptmanager after ggbOnInit
	 */
	public static native void appletOnLoad(String articleid) /*-{
		if (typeof $wnd.ggbAppletOnLoad === "function") {
			$wnd.ggbAppletOnLoad(articleid);
		}
	}-*/;

	protected GOptionPaneW getOptionPane() {
		return getGuiManager() != null ? getGuiManager().getOptionPane()
				: new GOptionPaneW(getPanel(), this);
	}

	@Override
	public void setActiveView(int evID) {
		if (getGuiManager() != null) {
			getGuiManager().setActiveView(evID);
		}
	}

	public final ClientInfo getClientInfo() {
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setModel(getLoginOperation().getModel());
		clientInfo.setLanguage(getLocalization().getLanguage());
		clientInfo.setWidth((int) getWidth());
		clientInfo.setHeight((int) getHeight());
		clientInfo.setType(getClientType());
		clientInfo.setId(getClientID());
		return clientInfo;
	}

	/**
	 * Initializes the user authentication
	 */
	public void initSignInEventFlow(LogInOperation op, boolean mayLogIn) {

		// Initialize the signIn operation
		loginOperation = op;
		if (getNetworkOperation().isOnline()) {
			if (this.getLAF() != null && this.getLAF().supportsGoogleDrive()) {
				initGoogleDriveEventFlow();
			}
			if (mayLogIn) {
				loginOperation.performTokenLogin();
			}
		} else {
			loginOperation.startOffline();
		}
	}

	protected void initGoogleDriveEventFlow() {
		// overriden in AppW
	}

	public void addViewsChangedListener(ViewsChangedListener l) {
		viewsChangedListener.add(l);
	}

	public void fireViewsChangedEvent() {
		for (ViewsChangedListener l : viewsChangedListener) {
			l.onViewsChanged();
		}
	}

	@Override
	public FontManager getFontManager() {
		return fontManager;
	}

	/**
	 * Initializes the application, seeds factory prototypes, creates Kernel and
	 * MyXMLIO
	 * 
	 */
	protected void initCommonObjects() {
		initFactories();

		// Online - Offline event handling begins here
		initNetworkEventFlow();
	}

	/**
	 * 
	 * @param this_app
	 *            application
	 * @return a kernel
	 */
	protected Kernel newKernel(App this_app) {
		return new Kernel(this_app, new GeoFactory());
	}

	/**
	 * Initializes Kernel, EuclidianView, EuclidianSettings, etc..
	 * 
	 * @param undoActive
	 *            whether undo manager should be initialized
	 * @param this_app
	 *            app for creating kernel
	 */
	protected void initCoreObjects(final App this_app) {
		kernel = newKernel(this_app);

		// init settings
		settings = companion.newSettings();
		// if (has(Feature.AV_EXTENSIONS)) {
		// settings.getAlgebra().setTreeMode(SortMode.ORDER.ordinal());
		// }
		fontManager = new FontManagerW();
		setFontSize(16, false);
		initEuclidianViews();

		initImageManager();

		setFontSize(16, true);
		// setLabelDragsEnabled(false);

		getScriptManager();// .ggbOnInit();//this is not called here because we
		// have to delay it
		// until the canvas is first drawn

		FileDropHandlerW.registerDropHandler(getFrameElement(), this);
		setViewsEnabled();
	}

	private void setViewsEnabled() {
		if (getArticleElement().getDataParamEnableCAS(false)
				|| !getArticleElement().getDataParamEnableCAS(true)) {
			getSettings().getCasSettings().setEnabled(
					getArticleElement().getDataParamEnableCAS(false));
		}
		if (getArticleElement().getDataParamEnable3D(false)
				|| !getArticleElement().getDataParamEnable3D(true)) {

			if (getSettings().supports3D()) {

				getSettings().getEuclidian(-1).setEnabled(
						getArticleElement().getDataParamEnable3D(false));
			}
		}

		if (getArticleElement().getDataParamEnableGraphing(false)
				|| !getArticleElement().getDataParamEnableGraphing(true)) {

			boolean enableGraphing = getArticleElement()
					.getDataParamEnableGraphing(false);
			getSettings().getEuclidian(1).setEnabled(enableGraphing);
			getSettings().getEuclidian(2).setEnabled(enableGraphing);
			kernel.getAlgebraProcessor().setCommandsEnabled(enableGraphing);
		}

	}

	/**
	 * @return preferred size
	 */
	public GDimension getPreferredSize() {
		if (preferredSize == null) {
			return new GDimensionW(800, 600);
		}
		return preferredSize;
	}

	@Override
	public void setPreferredSize(GDimension size) {
		preferredSize = size;
	}

	/**
	 * @return element of the AppFrame / GeoGebraFrame
	 */
	public abstract Element getFrameElement();

	@Override
	public void setWaitCursor() {
		if (getDialogManager() instanceof LoadingApplication) {
			((LoadingApplication) getDialogManager()).showLoadingAnimation();
		}
		RootPanel.get().addStyleName("cursor_wait");
	}

	@Override
	public void setDefaultCursor() {
		if (getDialogManager() instanceof LoadingApplication) {
			((LoadingApplication) getDialogManager()).hideLoadingAnimation();
		}
		resetCursor();
	}

	/**
	 * Set default cursor for whole body
	 */
	public void resetCursor() {
		RootPanel.get().removeStyleName("cursor_wait");
	}

	private void updateContentPane(boolean updateComponentTreeUI) {
		if (initing) {
			return;
		}

		addMacroCommands();

		// used in AppWapplet
		buildApplicationPanel();

		// update sizes
		euclidianView.updateSize();
		// needed: GGB-624
		if (hasEuclidianView2(1)) {
			getEuclidianView2(1).updateSize();
		}

		// update layout
		if (updateComponentTreeUI) {
			updateTreeUI();
		}

		// reset mode and focus
		set1rstMode();

	}

	/**
	 * Updates the GUI of the main component.
	 */
	public void updateContentPane() {
		updateContentPane(true);
	}

	/**
	 * @return current look and feel
	 */
	public GLookAndFeelI getLAF() {
		return laf;
	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		if (tableModel == null) {
			tableModel = new SpreadsheetTableModelW(this, SPREADSHEET_INI_ROWS,
					SPREADSHEET_INI_COLS);
		}
		return tableModel;
	}

	protected abstract void updateTreeUI();

	public void buildApplicationPanel() {
		// overridden in AppWApplet
	}

	public void appSplashCanNowHide() {
		// only with GUI
	}

	/**
	 * Called from GuiManager, implementation depends on subclass
	 * 
	 * @return toolbar object
	 */
	public ToolBarInterface getToolbar() {
		return null;
	}

	// methods used just from AppWapplet (and AppWsimple)
	/**
	 * 
	 * @param w
	 *            last selected view
	 * @param el
	 *            target element
	 */
	public void focusLost(View w, Element el) {
		// other things are handled in subclasses of AppW
		// anyAppHasFocus = false;
		if (el != null) {
			setLastActive(el);
		}
	}

	/**
	 * 
	 * @param w
	 *            selected view
	 * @param el
	 *            target element
	 */
	public void focusGained(View w, Element el) {
		// this is used through the super keyword
		// anyAppHasFocus = true;
		if (el != null) {
			setLastActive(el);
		}
	}

	public void setCustomToolBar() {
		// only needed in AppWFull
	}

	/**
	 * @return whether EV1 is the only visible view
	 */

	public boolean onlyGraphicsViewShowing() {
		if (!isUsingFullGui() || getGuiManager() == null) {
			return true;
		}

		return getGuiManager().getLayout().isOnlyVisible(App.VIEW_EUCLIDIAN);
	}

	@Override
	public boolean isUsingFullGui() {
		return useFullGui;
	}

	@Override
	public GuiManagerInterfaceW getGuiManager() {
		return null;
	}

	// ========================================================
	// Getters/Setters
	// ========================================================

	@Override
	public boolean isHTML5Applet() {
		return true;
	}

	public ArticleElement getArticleElement() {
		return articleElement;
	}

	@Override
	public boolean isApplet() {
		return !getArticleElement().getDataParamApp();
	}

	public Material getActiveMaterial() {
		return this.activeMaterial;
	}

	public void setActiveMaterial(Material mat) {
		this.activeMaterial = mat;
	}

	private void resetActiveMaterial() {
		this.activeMaterial = null;
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showEvAxes,
			boolean showEvGrid) {

		return euclidianView = newEuclidianView(euclidianViewPanel,
				getEuclidianController(), showEvAxes, showEvGrid, 1,
				getSettings().getEuclidian(1));
	}

	/**
	 * 
	 * @param evPanel
	 * @param ec
	 * @param showEvAxes
	 * @param showEvGrid
	 * @param id
	 * @param settings
	 *            view settings
	 * @return new euclidian view
	 */
	public EuclidianViewW newEuclidianView(EuclidianPanelWAbstract evPanel,
			EuclidianController ec, boolean[] showEvAxes, boolean showEvGrid,
			int id, EuclidianSettings evSettings) {
		return new EuclidianViewW(evPanel, ec, id, evSettings);
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel1) {
		return new EuclidianControllerW(kernel1);

	}

	@Override
	public DialogManager getDialogManager() {
		return dialogManager;
	}

	@Override
	public Factory getFactory() {

		return new FactoryW(this);
	}

	public void restoreCurrentUndoInfo() {
		if (isUndoActive()) {
			kernel.restoreCurrentUndoInfo();
			// isSaved = false;
		}
	}

	// ===================================================
	// Views
	// ===================================================

	public EuclidianPanelWAbstract getEuclidianViewpanel() {
		return euclidianViewPanel;
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		return (getGuiManager() != null)
				&& getGuiManager().hasEuclidianView2EitherShowingOrNot(idx);
	}

	@Override
	public EuclidianViewW getEuclidianView2(int idx) {

		if (getGuiManager() == null) {
			return null;
		}

		return (EuclidianViewW) getGuiManager().getEuclidianView2(idx);
	}

	@Override
	public EuclidianView getActiveEuclidianView() {
		if (getGuiManager() == null) {
			return getEuclidianView1();
		}
		return getGuiManager().getActiveEuclidianView();
	}

	@Override
	public boolean isShowingEuclidianView2(int idx) {
		return (getGuiManager() != null)
				&& getGuiManager().hasEuclidianView2(idx)
				&& getGuiManager().getEuclidianView2(idx).isShowing();
	}

	@Override
	public EuclidianViewW createEuclidianView() {
		return (EuclidianViewW) this.euclidianView;
	}

	@Override
	public AlgebraView getAlgebraView() {
		if (getGuiManager() == null) {
			return null;
		}
		return getGuiManager().getAlgebraView();
	}

	@Override
	public boolean showView(int view) {
		if (getGuiManager() == null) {
			return (view == App.VIEW_EUCLIDIAN);
		}
		return getGuiManager().showView(view);
	}

	// ========================================================
	// Languages
	// ========================================================

	/**
	 * Checks for GeoGebraLangUI in URL, then in cookie, then checks browser
	 * language
	 */
	public String getLanguageFromCookie() {
		String lCookieValue = articleElement.getDataParamApp()
				? Location.getParameter("lang") : "";
		if (StringUtil.empty(lCookieValue)) {
			lCookieValue = Cookies.getCookie("GeoGebraLangUI");
		}

		Storage localStorage = Storage.getLocalStorageIfSupported();

		if (StringUtil.empty(lCookieValue) && localStorage != null) {
			lCookieValue = localStorage.getItem("GeoGebraLangUI");
		}
		if (StringUtil.empty(lCookieValue)) {
			lCookieValue = Browser.navigatorLanguage();
		}
		return lCookieValue;
	}

	@Override
	public void setLabels() {
		if (initing) {
			return;
		}
		if (getGuiManager() != null) {
			getGuiManager().setLabels();
		}
		// if (rbplain != null) {
		kernel.updateLocalAxesNames();
		kernel.setViewsLabels();
		// }
		updateCommandDictionary();
		String key = getConfig().getAppTitle();
		getArticleElement().setAttribute("aria-label",
				getLocalization().getMenu(key));
		setAltText();
	}

	@Override
	public boolean letRedefine() {
		// AbstractApplication.debug("implementation needed"); // TODO
		// Auto-generated
		return true;
	}

	// ============================================
	// IMAGES
	// ============================================

	private static String createImageSrc(String ext, String base64) {
		String dataUrl = "data:image/" + ext + ";base64," + base64;
		return dataUrl;
	}

	public ImageElement getRefreshViewImage() {
		ImageElement imgE = ImageManagerW
				.getInternalImage(GuiResourcesSimple.INSTANCE.viewRefresh());
		attachNativeLoadHandler(imgE);
		return imgE;
	}

	public ImageElement getPlayImage() {
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.icons_play_circle());
	}

	public ImageElement getPauseImage() {
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.icons_play_pause_circle());
	}

	public ImageElement getPlayImageHover() {
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.icons_play_circle_hover());
	}

	public ImageElement getPauseImageHover() {
		return ImageManagerW.getInternalImage(
				GuiResourcesSimple.INSTANCE.icons_play_pause_circle_hover());
	}

	// ============================================
	// XML
	// ============================================

	@Override
	protected int getWindowWidth() {
		if (getWidth() > 0) {
			return (int) getWidth();
		}
		return 800;
	}

	@Override
	protected int getWindowHeight() {
		if (getHeight() > 0) {
			return (int) getHeight();
		}
		return 600;
	}

	@Override
	protected void getLayoutXML(StringBuilder sb, boolean asPreference) {

		if (getGuiManager() == null) {
			initGuiManager();
		}
		if (getGuiManager() != null) {
			getGuiManager().getLayout().getXml(sb, asPreference);
		}
	}

	// ============================================
	// FONTS
	// ============================================

	@Override
	public GFont getPlainFontCommon() {
		return new GFontW("normal");
	}

	// ============================================
	// CURSORS
	// ============================================

	@Override
	public void updateUI() {
		if (getGuiManager() != null) {
			getGuiManager().setPixelRatio(getPixelRatio());
		}
		// Log.debug("updateUI: implementation needed for GUI"); // TODO
	}

	// ========================================
	// EXPORT & GEOTUBE
	// ========================================

	public final void copyEVtoClipboard(EuclidianViewW ev) {
		String image = ev.getExportImageDataUrl(3, true);
		String title = ev.getApplication().getKernel().getConstruction()
				.getTitle();
		title = "".equals(title) ? "GeoGebraImage" : title;
		getFileManager().exportImage(image, title, "png");
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		Log.debug("unimplemented");
	}

	// ========================================
	// MISC
	// ========================================

	@Override
	public GeoElementSelectionListener getCurrentSelectionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showURLinBrowser(final String pageUrl) {
		getFileManager().open(pageUrl, "_blank", "");
		Log.debug("opening: " + pageUrl);

		// assume showURLinBrowserWaiterFixedDelay is called before
	}

	@Override
	public void initGuiManager() {
		// this should not be called from AppWsimple!
		// this method should be overridden in
		// AppWapplet and AppWapplication!
	}

	@Override
	public void exitAll() {
		Log.debug("unimplemented");
	}

	/**
	 * This is used for LaTeXes in GeoGebraWeb (DrawText, DrawEquationWeb)
	 */
	@Override
	public void scheduleUpdateConstruction() {

		// set up a scheduler in case 0.5 seconds would not be enough for the
		// computer
		Scheduler.get().scheduleDeferred(sucCallback);
	}

	protected void updateConsBoundingBox() {
		boolean force = kernel.getForceUpdatingBoundingBox();
		kernel.setForceUpdatingBoundingBox(true);
		kernel.getConstruction().updateConstructionLaTeX();
		kernel.notifyRepaint();
		kernel.setForceUpdatingBoundingBox(force);
	}

	public String getDataParamId() {
		return getArticleElement().getDataParamId();

	}

	/**
	 * File loading callback
	 */
	public abstract void afterLoadFileAppOrNot(boolean asSlide);

	/**
	 * Returns the tool name and tool help text for the given tool as an HTML
	 * text that is useful for tooltips.
	 * 
	 * @param mode
	 *            : tool ID
	 */
	@Override
	public String getToolTooltipHTML(int mode) {
		String toolTipHtml = super.getToolTooltipHTML(mode);
		return toolTipHtml;
	}

	public void recalculateEnvironments() {
		if (getGuiManager() != null) {
			getGuiManager().recalculateEnvironments();

		}
		if (getEuclidianView1() != null) {
			getEuclidianView1().getEuclidianController().calculateEnvironment();
		}

	}

	@Override
	public void updateViewSizes() {
		// TODO Auto-generated method stub
	}

	public void registerPopup(Widget widget) {
		popups.add(widget);
	}

	public void centerAndResizePopups() {
		// to be overridden in AppWFull
	}

	@Override
	public void closePopups() {
		closePopupsNoTooltips();
		ToolTipManagerW.hideAllToolTips();

		if (!isUnbundled() && getGuiManager() != null
				&& getGuiManager().hasAlgebraView()) {
			getAlgebraView().resetItems(false);
		}

		if (getActiveEuclidianView() != null) {
			getActiveEuclidianView().getEuclidianController()
					.setObjectMenuActive(false);
		}
	}

	public void closePopupsNoTooltips() {
		justClosedPopup = false;
		for (Widget widget : popups) {
			justClosedPopup = true;
			widget.setVisible(false);
		}
		popups.clear();
	}

	public void addAsAutoHidePartnerForPopups(Element el) {
		for (int i = 0; i < popups.size(); i++) {

			Widget popup = popups.get(i);
			if (popup instanceof GPopupPanel
					&& ((GPopupPanel) popup).isModal()) {
				((GPopupPanel) popup).addAutoHidePartner(el);
			}
		}
	}

	public boolean hasPopup() {
		return popups.size() > 0;
	}

	public boolean wasPopupJustClosed() {
		return justClosedPopup;
	}

	public void clearJustClosedPopup() {
		justClosedPopup = false;
	}

	public void unregisterPopup(Widget widget) {
		popups.remove(widget);
	}

	public String getClientType() {
		if (getLAF() == null) {
			return "web";
		}
		return getLAF().getType();
	}

	public String getClientID() {
		return getArticleElement().getDataClientID();
	}

	public boolean isShowToolbar() {
		if (this.articleElement == null) {
			return false;
		}
		return this.articleElement.getDataParamShowToolBar(false)
				|| this.articleElement.getDataParamApp();
	}

	public int getWidthForSplitPanel(int fallback) {
		int ret = getAppletWidth(); // border already excluded

		// if it is not 0, there will be some scaling later
		if (ret <= 0) {
			ret = fallback;

			// empirical hack to make room for the toolbar always
			if (showToolBar() && ret < 598) {
				ret = 598; // 2: border
				// maybe this has to be put outside the "if"?
			}
		}
		return ret;
	}

	public int getHeightForSplitPanel(int fallback) {
		// border excluded
		int windowHeight = getAppletHeight();
		// but we want to know the available height for the rootPane
		// so we either use the above as a heuristic,
		// or we should substract the height(s) of
		// toolbar, menubar, and input bar;
		// heuristics come from GeoGebraAppFrame
		if (showAlgebraInput()
				&& getInputPosition() != InputPosition.algebraView) {
			windowHeight -= GLookAndFeelI.COMMAND_LINE_HEIGHT;
		}
		if (showToolBar() && !isUnbundledOrWhiteboard()) {
			windowHeight -= GLookAndFeelI.TOOLBAR_HEIGHT;
		}
		// menubar height is always 0
		if (windowHeight <= 0) {
			windowHeight = fallback;
		}
		return windowHeight;
	}

	protected void initUndoInfoSilent() {
		getScriptManager().disableListeners();
		kernel.initUndoInfo();
		getScriptManager().enableListeners();
	}

	@Override
	public boolean supportsView(int viewID) {
		if (viewID == App.VIEW_CAS && !getLAF().isSmart()) {
			if (!Browser.supportsJsCas()) {
				return false;
			}
		}

		if (viewID == App.VIEW_CAS) {
			return (getSettings().getCasSettings().isEnabled())
					&& getArticleElement().getDataParamEnableCAS(true)
					&& getCASFactory().isEnabled();
		}

		if (getLAF() != null
				&& getLAF().examSupported(has(Feature.EXAM_TABLET))) {
			if (viewID == App.VIEW_EUCLIDIAN) {
				return getSettings().getEuclidian(1).isEnabled();
			} else if (viewID == App.VIEW_EUCLIDIAN2) {
				return getSettings().getEuclidian(2).isEnabled();
			}
		}

		return viewID != App.VIEW_EUCLIDIAN3D;
	}

	@Override
	public int getGUIFontSize() {
		return 14;
	}

	public void updateToolBar() {
		if (!showToolBar || isIniting()) {
			return;
		}

		if (getGuiManager() != null) {
			getGuiManager().updateToolbar();
		}

		if (isUnbundled()) {
			return;
		}
		set1rstMode();
	}

	@Override
	public void updateApplicationLayout() {
		Log.debug("updateApplicationLayout: Implementation needed...");
	}

	@Override
	public void setShowToolBar(boolean toolbar, boolean help) {
		if (toolbar) {
			// JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE
			// .propertiesKeysJS());
		}
		super.setShowToolBar(toolbar, help);
	}

	// methods used just from AppWapplication
	public int getOWidth() {
		return 0;
	}

	public int getOHeight() {
		return 0;
	}

	public String getAppletId() {
		return articleElement.getDataParamId();
	}

	/**
	 * @return frame widget
	 */
	public abstract HasAppletProperties getAppletFrame();

	/**
	 * @return whether the focus was lost
	 */
	private static native Element nativeLoseFocus(Element element) /*-{
		var active = $doc.activeElement;
		if (active
				&& ((active === element) || (active
						.compareDocumentPosition(element) & $wnd.Node.DOCUMENT_POSITION_CONTAINS))) {
			active.blur();
			return active;
		}
		return null;
	}-*/;

	@Override
	public void loseFocus() {
		// probably this is called on ESC, so the reverse
		// should happen on ENTER
		Element ret = nativeLoseFocus(articleElement);
		if (ret != null) {
			setLastActive(ret);
			setAnyAppFocused(false);
			getGlobalKeyDispatcher().setFocused(false);
		}
	}

	private static void setAnyAppFocused(boolean b) {
		anyAppHasFocus = b;
	}

	/**
	 * @return whether we can focus on ENTER
	 */
	private static native boolean nativeGiveFocusBack() /*-{
		var active = $doc.activeElement;
		if (active && (active !== $doc.body)) {

			//if SVG clicked, getClassName returns non-string

			if (typeof active.className == "string"
					&& active.className.match(/geogebraweb-dummy-invisible/)) {
				// actually, ESC focuses this, does not blur!
				return true;
			}
			// this shall execute the default ENTER action on
			// that element, to be independent (e.g. click on links!)
			// OR if it is part of GeoGebra, then we shall also not
			// support selecting GeoGebra again by ENTER
			//return false; // behold the other return false;

			// not doing more checks, don't do any action, which is safe
			return false;
		}
		// blurred, probably, so it's safe to focus on ENTER
		return true;
	}-*/;

	/**
	 * Let's say this is the pair of loseFocus, so that only loseFocus can lose
	 * focus from ALL applets officially (i.e. "ESC"), and from each part of
	 * each applet (e.g. input bar, Graphics view, etc), while only
	 * giveFocusBack can give focus back to an applet removed by the loseFocus
	 * method - to avoid hidden bugs.
	 * 
	 * What if focus is received by some other method than ENTER (pair of ESC)?
	 * I think let's allow it, but if ENTER comes next, then we should adjust
	 * our knowledge about it (otherwise, it should have been watched in the
	 * entire codebase, which is probably worse, for there are possibilities of
	 * errors). This way just these two methods shall be checked.
	 */
	public static void giveFocusBack() {
		if (anyAppHasFocus) {
			// here we are sure that ENTER should not do anything
			return;
		}

		// update for the variable in this case, must be made anyway
		// just it is a question whether this shall also mean a focus?
		// BUT only when nativeGiveFocusBack is changed, and this
		// variable also filled perfectly
		// anyAppHasFocus = true;

		// here we could insert static aggregates of relevant
		// variables like getGlobalKeyDispatcher().InFocus
		// ... but what if e.g. the input bar has focus?

		// then we can easily check for $doc.activeElement,
		// whether it means blur=OK
		if (nativeGiveFocusBack()) {
			if (lastActiveElement != null) {
				anyAppHasFocus = true;
				lastActiveElement.focus();
			}
		}
	}

	@Override
	public boolean isScreenshotGenerator() {
		return this.articleElement.getDataParamScreenshotGenerator();
	}

	// ========================================================
	// INITIALIZING
	// ========================================================

	@Override
	public void setScrollToShow(boolean b) {
		if (getGuiManager() != null) {
			getGuiManager().setScrollToShow(b);
		}
	}

	/**
	 * Overwritten for applets, full app and for touch
	 * 
	 * @return {@link MaterialsManagerI}
	 */
	public MaterialsManagerI getFileManager() {
		return null;
	}

	// public ToolTipManagerW getToolTipManager(){
	// if(toolTipManager == null){
	// toolTipManager = new ToolTipManagerW(this);
	// }
	// return toolTipManager;
	// }

	// ========================================================
	// Undo/Redo
	// ========================================================

	@Override
	public final void storeUndoInfo() {
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			setUnsaved();
		}
	}

	// ========================================================
	// FILE HANDLING
	// ========================================================

	protected void clearInputBar() {
		if (isUsingFullGui() && showAlgebraInput() && getGuiManager() != null) {
			AlgebraInput ai = (getGuiManager().getAlgebraInput());
			if (ai != null) {
				ai.setText("");
			}
		}
	}

	// ================================================
	// ERROR HANDLING
	// ================================================

	@Override
	public void showError(String key, String error) {

		String translatedError = getLocalization().getError(key);

		// in case translations not available
		// eg webSimple
		if ("InvalidInput".equals(translatedError)) {
			translatedError = "Invalid Input";
		}

		showErrorDialog(translatedError + ":\n" + error);
	}

	public void showMessage(final String message) {
		getOptionPane().showConfirmDialog(this, message,
				GeoGebraConstants.APPLICATION_NAME + " - "
						+ getLocalization().getMenu("Info"),
				GOptionPane.DEFAULT_OPTION, GOptionPane.INFORMATION_MESSAGE,
				null);
	}

	public void showMessage(final String message, final String title) {
		getOptionPane().showConfirmDialog(this, message, title,
				GOptionPane.DEFAULT_OPTION, GOptionPane.INFORMATION_MESSAGE,
				null);
	}

	public void showMessage(final String message, final String title,
			String buttonText, AsyncOperation<String[]> handler) {
		HTML content = new HTML(message);
		content.addStyleName("examContent");
		ScrollPanel scrollPanel = new ScrollPanel(content);
		scrollPanel.addStyleName("examScrollPanel");
		getOptionPane().showConfirmDialog(this, scrollPanel, title,
				GOptionPane.DEFAULT_OPTION, GOptionPane.INFORMATION_MESSAGE,
				buttonText, null, handler);

	}

	@Override
	public void showErrorDialog(final String msg) {

		if (!isErrorDialogsActive()) {
			return;
		}
		if (this.getErrorHandler() != null) {
			this.getErrorHandler().showError(msg);
			return;
		}

	}

	@Override
	public ErrorHandler getDefaultErrorHandler() {
		return new ErrorHandlerW(this);
	}

	@Override
	public void showError(String s) {
		showErrorDialog(s);
	}

	@Override
	public boolean freeMemoryIsCritical() {
		// can't access available memory etc from JavaScript
		return false;
	}

	@Override
	public long freeMemory() {
		return 0;
	}

	@Override
	public void evalJavaScript(App app, String script0, String arg) {

		// TODO: maybe use sandbox?
		String script = script0;
		String ggbApplet = getDataParamId();

		script = "document.ggbApplet= document." + ggbApplet
				+ "; ggbApplet = document." + ggbApplet + ";" + script;

		// script = "ggbApplet = document.ggbApplet;"+script;

		// add eg arg="A"; to start
		if (arg != null) {
			script = "arg=\"" + arg + "\";" + script;
		}
		JsEval.evalScriptNative(script);
	}

	public static int getAbsoluteLeft(Element element) {
		return element.getAbsoluteLeft();
	}

	public static int getAbsoluteRight(Element element) {
		return element.getAbsoluteRight();
	}

	public static int getAbsoluteTop(Element element) {
		return element.getAbsoluteTop();
	}

	public static int getAbsoluteBottom(Element element) {
		return element.getAbsoluteBottom();
	}

	public void attachNativeLoadHandler(ImageElement img) {
		addNativeLoadHandler(img, getActiveEuclidianView());
	}

	private native void addNativeLoadHandler(ImageElement img,
			EuclidianView view) /*-{
		img
				.addEventListener(
						"load",
						function() {
							view.@org.geogebra.web.html5.euclidian.EuclidianViewW::updateBackground()();
						});
	}-*/;

	// ============================================
	// LAYOUT & GUI UPDATES
	// ============================================

	@Override
	public final boolean showAlgebraInput() {
		return showAlgebraInput;
	}

	@Override
	public double getWidth() {
		if (getFrameElement() == null) {
			return 0;
		}
		return getFrameElement().getOffsetWidth();
	}

	@Override
	public double getHeight() {
		if (getFrameElement() == null) {
			return 0;
		}
		return getFrameElement().getOffsetHeight();
	}

	public boolean needsSmallKeyboard() {
		return (getHeight() > 0 && getHeight() < LOWER_HEIGHT)
				|| (getHeight() == 0 && getArticleElement()
						.getDataParamHeight() < LOWER_HEIGHT);
	}

	@Override
	public void updateMenubar() {
		// getGuiManager().updateMenubar();
		Log.debug(
				"AppW.updateMenubar() - implementation needed - just finishing"); // TODO
		// Auto-generated
	}

	@Override
	public void updateStyleBars() {

		if (!isUsingFullGui() || isIniting()) {
			return;
		}

		if (getEuclidianView1().hasStyleBar()) {
			getEuclidianView1().getStyleBar().updateStyleBar();
		}

		if (hasEuclidianView2(1) && getEuclidianView2(1).hasStyleBar()) {
			getEuclidianView2(1).getStyleBar().updateStyleBar();
		}

		if (has(Feature.DYNAMIC_STYLEBAR)) {
			updateDynamicStyleBars();
		}

	}

	@Override
	public void updateDynamicStyleBars() {
		if (getEuclidianView1().hasDynamicStyleBar()) {
			getEuclidianView1().getDynamicStyleBar().updateStyleBar();
		}

		if (hasEuclidianView2(1) && getEuclidianView2(1).hasDynamicStyleBar()) {
			getEuclidianView2(1).getDynamicStyleBar().updateStyleBar();
		}
	}

	public void updateCenterPanel() {
		// only needed with GUI
	}

	/**
	 * @param ggwGraphicsViewWidth
	 * 
	 *            Resets the width of the Canvas converning the Width of its
	 *            wrapper (splitlayoutpanel center)
	 */
	public void ggwGraphicsViewDimChanged(int width, int height) {
		// Log.debug("dim changed" + getSettings().getEuclidian(1));
		if (width > 0 && height > 0) {
			getSettings().getEuclidian(1).setPreferredSize(
					AwtFactory.getPrototype().newDimension(width, height));
		}

		// simple setting temp.
		appCanvasHeight = height;
		appCanvasWidth = width;
		// Log.debug("syn size");
		getEuclidianView1().synCanvasSize();
		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
	}

	/**
	 * Resets the width of the Canvas converning the Width of its wrapper
	 * (splitlayoutpanel center)
	 *
	 * @param width
	 *            , height
	 */
	public void ggwGraphicsView2DimChanged(int width, int height) {
		if (width > 0 && height > 0) {
			getSettings().getEuclidian(2).setPreferredSize(
					AwtFactory.getPrototype().newDimension(width, height));
		}
		// simple setting temp.
		// appCanvasHeight = height;
		// appCanvasWidth = width;

		getEuclidianView2(1).synCanvasSize();
		getEuclidianView2(1).doRepaint2();
		stopCollectingRepaints();
	}

	/**
	 * Resets the width of the Canvas converning the Width of its wrapper
	 * (splitlayoutpanel center)
	 *
	 * @param width
	 *            in pixels
	 * @param height
	 *            in pixels
	 */
	public void ggwGraphicsView3DDimChanged(int width, int height) {
		// only used for 3D
	}

	@Override
	public void ensureTimerRunning() {
		this.getTimerSystem().ensureRunning();

	}

	@Override
	public void showCustomizeToolbarGUI() {
		showBrowser(getCustomizeToolbarGUI());
	}

	protected HeaderPanel getCustomizeToolbarGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overwritten for AppWapplet/AppWapplication
	 * 
	 * @param bg
	 */
	public void showBrowser(HeaderPanel bg) {
		// TODO
	}

	/**
	 * shows the on-screen keyboard (or e.g. a show-keyboard-button)
	 * 
	 * @param textField
	 *            keyboard listener
	 * @param forceShow
	 *            whether it must appear now
	 */
	public boolean showKeyboard(MathKeyboardListener textField,
			boolean forceShow) {
		return false; // Overwritten in subclass
	}

	/**
	 * update the on-screen keyboard
	 * 
	 * @param field
	 *            after the update the input of the keyboard is written into
	 *            this field
	 */
	public void updateKeyBoardField(MathKeyboardListener field) {
		// Overwritten in subclass - nothing to do here
	}

	/**
	 * hide the on-screen keyboard (if it is visible)
	 */
	public void hideKeyboard() {
		// Overwritten in subclass - nothing to do here
	}

	public boolean isOffline() {
		return !getNetworkOperation().isOnline();
	}

	public boolean isToolLoadedFromStorage() {
		return toolLoadedFromStorage;
	}

	public void setToolLoadedFromStorage(boolean toolLoadedFromStorage) {
		this.toolLoadedFromStorage = toolLoadedFromStorage;
	}

	public void setCloseBrowserCallback(Runnable runnable) {
		this.closeBroserCallback = runnable;

	}

	public void onBrowserClose() {
		if (this.closeBroserCallback != null) {
			this.closeBroserCallback.run();
			this.closeBroserCallback = null;
		}

	}

	public void addInsertImageCallback(Runnable runnable) {
		this.insertImageCallback = runnable;
	}

	public boolean isMenuShowing() {
		return false;
	}

	/**
	 * @param heightDiff
	 *            height difference
	 */
	public void addToHeight(int heightDiff) {
		// for applets with keyboard only
	}

	@Override
	public SensorLogger getSensorLogger() {
		if (webSocketLogger == null) {
			webSocketLogger = new WebsocketLogger(getKernel());
		}
		return webSocketLogger;
	}

	public void setKeyboardNeeded(boolean b) {
		this.keyboardNeeded = b;
	}

	public boolean isKeyboardNeeded() {
		return keyboardNeeded;
	}

	/**
	 * @param perspID
	 *            perspective id
	 */
	public void showStartTooltip(int perspID) {
		// probably needed in full version only
	}

	public double getAbsLeft() {
		return this.getFrameElement().getAbsoluteLeft();
	}

	public double getAbsTop() {
		return this.getFrameElement().getAbsoluteTop();
	}

	public boolean enableFileFeatures() {
		return this.articleElement.getDataParamEnableFileFeatures();
	}

	public void setPrerelease(String prereleaseStr) {
		this.canary = false;
		this.prerelease = false;

		if ("canary".equals(prereleaseStr)) {
			canary = true;
			prerelease = true;
		} else if ("true".equals(prereleaseStr)) {
			this.prerelease = true;

		}

	}

	/**
	 * @param el
	 *            article element
	 * @return true if prerelease
	 * 
	 *         Remove this function if GGB-2051 released. Used only in GGB-2051
	 */
	public static boolean isPrerelease(ArticleElement el) {
		String p = el.getDataParamPrerelease();
		return "true".equals(p) || "canary".equals(p);
	}

	@Override
	public void hideMenu() {
		// for applets with menubar
	}

	public void setExternalPath(String path) {
		this.externalPath = path;
		String title = "";
		if (getKernel() != null && getKernel().getConstruction() != null
				&& getKernel().getConstruction().getTitle() == null
				|| "".equals(getKernel().getConstruction().getTitle())) {
			int lastSlash = Math.max(path.lastIndexOf('/'),
					path.lastIndexOf('\\'));
			title = path.substring(lastSlash + 1).replace(".ggb", "");
			getKernel().getConstruction().setTitle(title);
		}
		getFileManager().setFileProvider(Provider.LOCAL);
	}

	/**
	 * @param runnable
	 *            callback for after file is saved
	 */
	public void checkSaved(Runnable runnable) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param base64
	 *            CSV content
	 */
	public void openCSV(String base64) {
		// TODO Auto-generated method stub

	}

	protected void updateNavigationBars() {
		if (showConstProtNavigationNeedsUpdate == null) {
			return;
		}

		for (int key : showConstProtNavigationNeedsUpdate.keySet()) {
			getGuiManager().getLayout().getDockManager().getPanel(key)
					.updateNavigationBar();
		}

	}

	@Override
	public boolean useShaders() {
		return true;
	}

	/**
	 * @param response
	 *            text of OFF file
	 **/
	public void openOFF(String response) {
		// only makes sense in 3D

	}

	/**
	 * 
	 * @return Pixel ratio including external transforms
	 */
	public double getPixelRatio() {
		return Browser.getPixelRatio() * articleElement.readScaleX();
	}

	public void addWindowResizeListener(MouseTouchGestureControllerW mtg) {
		this.euclidianHandlers.add(mtg);
	}

	public boolean showToolBarHelp() {
		return getArticleElement().getDataParamShowToolBarHelp(true);
	}

	public Panel getPanel() {
		return RootPanel.get();
	}

	private Timer altTextTimer = new Timer() {

		@Override
		public void run() {
			getEuclidianView1().setAltText();
			if (hasEuclidianView2(1)) {
				getEuclidianView2(1).setAltText();
			}
			if (isEuclidianView3Dinited()) {
				((EuclidianViewWInterface) getEuclidianView3D()).setAltText();
			}
		}
	};

	@Override
	public void setAltText() {
		altTextTimer.schedule(700);
	}

	@Override
	public GImageIcon wrapGetModeIcon(int mode) {
		return null;
	}

	@Override
	public boolean showAutoCreatedSlidersInEV() {
		return false;
	}

	public boolean allowStylebar() {
		return (!isApplet()
				|| getArticleElement().getDataParamShowMenuBar(false)
				|| getArticleElement().getDataParamAllowStyleBar(false))
				&& enableGraphing();
	}

	public boolean enableGraphing() {
		return getArticleElement().getDataParamEnableGraphing(true);
	}

	public boolean isStartedWithFile() {
		return getArticleElement().getDataParamFileName().length() > 0
				|| getArticleElement().getDataParamBase64String().length() > 0
				|| getArticleElement().getDataParamTubeID().length() > 0
				|| this.getArticleElement().getDataParamJSON().length() > 0
				|| (getArticleElement().getDataParamApp()
						&& Location.getParameter("state") != null);
	}

	@Override
	public GTimer newTimer(GTimerListener listener, int delay) {
		return new GTimerW(listener, delay);
	}

	private class ReaderTimer extends Timer {

		GeoNumeric geo;

		protected ReaderTimer() {
			// make protected
		}

		@Override
		public void run() {
			ScreenReader.readText(geo, AppW.this);
		}

		public void setGeo(GeoNumeric geo0) {
			geo = geo0;
		}

	}

	@Override
	public void readLater(GeoNumeric geo) {
		if (!kernel.getConstruction().isFileLoading()
				&& (!articleElement.preventFocus()
						|| getGlobalKeyDispatcher().isFocused())) {
			if (readerTimer == null) {
				readerTimer = new ReaderTimer();
			}
			readerTimer.setGeo(geo);
			readerTimer.schedule(700);
		}
	}

	public void updateMaterialURL(int i, String sharingKey, String title) {
		setTubeId(i);
		if (articleElement.getDataParamApp() && sharingKey != null) {
			String appName = articleElement.getDataParamAppName();
			if (StringUtil.empty(appName)) {
				appName = "classic";
			}
			Browser.changeUrl("/" + appName + "/" + sharingKey);
			if (!StringUtil.empty(title)) {
				Browser.changeMetaTitle(title);
			}
		}

	}

	public void updateRounding() {
		setRounding(getArticleElement().getDataParamRounding());
	}

	/*
	 * public Runnable showPerspectivesPopupRunnable() { MainMenu menu =
	 * (MainMenu) laf.getMenuBar(this); return new Runnable() { public void
	 * run() { menu.getPerspectivesMenuW().showPerspectivesPopup(); } }; }
	 */

	public void showPerspectivesPopup() {
		// overridden in AppWFull
	}

	public void closePerspectivesPopup() {
		// only for GUI

	}

	/**
	 * @param index
	 *            perspective ID
	 */
	public void setActivePerspective(int index) {
		// only for GUI

	}

	public void addFocusToApp() {

		if (!GlobalKeyDispatcherW.getIsHandlingTab()) {
			getGlobalKeyDispatcher().setFocused(true);
			return;
		}

		// add focus to AV if visible
		AlgebraView av = getAlgebraView();
		boolean visible = av != null && av.isShowing();
		if (visible) {
			((Widget) av).getElement().focus();
			focusGained(av, ((Widget) av).getElement());
			return;
		}

		// focus -> EV
		EuclidianViewW ev = getEuclidianView1();
		visible = ev != null && ev.isShowing();
		if (visible) {
			ev.getCanvas().getElement().focus();
			ev.focusGained();
		}
	}

	public void resetViewsEnabled() {
		// reset cas and 3d settings for restart of exam
		// needed for GGB-1015
		this.getSettings().getCasSettings().resetEnabled();
		this.getSettings().getEuclidian(-1).resetEnabled();
		getSettings().getEuclidian(1).resetEnabled();
		getSettings().getEuclidian(2).resetEnabled();
		setViewsEnabled();

	}

	public boolean isPerspectivesPopupVisible() {
		return false;
	}

	/**
	 * @param p
	 *            perspective to be forced after settings
	 */
	public void loadPreferences(Perspective p) {
		// GeoGebraPreferencesW.getPref().loadForApp(app, p);

	}

	@Override
	public void ensureEvSizeSet(EuclidianSettings evSet) {

		GDimension gd = evSet.getPreferredSize();
		if (gd.getWidth() == 0 || gd.getHeight() == 0) {
			// border already excluded
			int width = getAppletWidth();
			int height = getAppletHeight();
			if (width == 0 || height == 0) {
				// setting a standard size, like in
				// compabilityLayout
				// fixing a real bug of height 0
				width = 598; // 2: border
				height = 438; // 2: border
			}
			evSet.setPreferredSize(
					AwtFactory.getPrototype().newDimension(width, height));
		}

	}

	@Override
	public void setNewExam() {
		setExam(new ExamEnvironmentW(this));
	}

	/**
	 * 
	 * @return 9999 (or 200 in web)
	 */
	@Override
	public int getMaxSpreadsheetRowsVisible() {
		return Kernel.MAX_SPREADSHEET_ROWS_WEB;
	}

	/**
	 * 
	 * @return 9999 (or 200 in web)
	 */
	@Override
	public int getMaxSpreadsheetColumnsVisible() {
		return Kernel.MAX_SPREADSHEET_COLUMNS_WEB;
	}

	@Override
	public GeoGebraToPstricks newGeoGebraToPstricks() {
		return new GeoGebraToPstricksW(this);
	}

	@Override
	public GeoGebraToAsymptote newGeoGebraToAsymptote() {
		return new GeoGebraToAsymptoteW(this);
	}

	@Override
	public GeoGebraToPgf newGeoGebraToPgf() {
		return new GeoGebraToPgfW(this);
	}

	public static native Element getHiddenTextArea() /*-{
		var hiddenTextArea = $doc.getElementById('hiddenCopyPasteTextArea');
		if (!hiddenTextArea) {
			hiddenTextArea = $doc.createElement("textarea");
			hiddenTextArea.id = 'hiddenCopyPasteTextArea';
			hiddenTextArea.style.position = 'absolute';
			hiddenTextArea.style.width = '10px';
			hiddenTextArea.style.height = '10px';
			hiddenTextArea.style.zIndex = '100';
			hiddenTextArea.style.left = '-1000px';
			hiddenTextArea.style.top = '0px';
			$doc.getElementsByTagName('body')[0].appendChild(hiddenTextArea);
		}
		//hiddenTextArea.value = '';
		return hiddenTextArea;
	}-*/;

	/**
	 * https://jsfiddle.net/alvaroAV/a2pt16yq/ works in IE11, Chrome, Firefox,
	 * Edge
	 * 
	 * this method doesn't always work in Edge, Firefox as needs to be run from
	 * eg button
	 * 
	 * @param value
	 *            text to copy
	 */
	public static native void copyToSystemClipboardNative(String value) /*-{
		var copyFrom = @org.geogebra.web.html5.main.AppW::getHiddenTextArea()();
		copyFrom.value = value;
		copyFrom.select();
		$doc.execCommand('copy');
	}-*/;

	@Override
	public void copyTextToSystemClipboard(String text) {
		Log.debug("copying to clipboard " + text);
		copyToSystemClipboardNative(text);
	}

	public void copyTextToSystemClipboard(String text, Runnable notify) {
		Log.debug("copying to clipboard " + text);
		copyToSystemClipboardNative(text);

		if (notify != null) {
			notify.run();
		}
	}

	private static void setLastActive(Element e) {
		lastActiveElement = e;
	}

	public void toggleMenu() {
		// only with GUI
	}

	public int getActivePerspective() {
		return 0;
	}

	@Override
	public boolean nativeCAS() {
		return Browser.externalCAS();
	}

	public void onUnhandledClick() {
		// TODO Auto-generated method stub

	}

	public final void updateCenterPanelAndViews() {
		updateCenterPanel();
		updateViewSizes();
	}

	public void updateSplitPanelHeight() {
		// implemented in subclass
	}

	@Override
	public boolean isUnbundled() {
		return AppConfigDefault
				.isUnbundledOrWhiteboard(articleElement.getDataParamAppName())
				&& !"whiteboard".equals(articleElement.getDataParamAppName());

	}

	@Override
	public AppConfig getConfig() {
		if ("graphing".equals(articleElement.getDataParamAppName())) {
			return new AppConfigGraphing();
		}
		if ("geometry".equals(articleElement.getDataParamAppName())) {
			return new AppConfigGeometry();
		}
		if ("3d".equals(articleElement.getDataParamAppName())) {
			return new AppConfigGeometry();
		}
		return new AppConfigDefault();
	}

	@Override
	public boolean isUnbundledGraphing() {
		return "graphing".equals(articleElement.getDataParamAppName());
	}

	@Override
	public boolean isUnbundledGeometry() {
		return "geometry".equals(articleElement.getDataParamAppName());
	}

	public boolean isUnbundled3D() {
		return "3d".equals(articleElement.getDataParamAppName());
	}

	public void ensureStandardView() {
		// only with GUI
	}

	/**
	 * 
	 * @return true if app is on portrait mode;
	 */
	@Override
	public boolean isPortrait() {
		return getWidth() < getHeight();
	}

	/**
	 * Open share dialog (native or HTML popup)
	 */
	public void share() {
		// only with GUI
	}

	@Override
	public void exportSbToFile(String ext, StringBuilder sb) {
		String url = "data:text/plain;charset=utf-8,"
				+ URL.encodePathSegment(sb.toString());
		dispatchEvent(new org.geogebra.common.plugin.Event(
				EventType.OPEN_DIALOG, null, "export3D"));
		getFileManager().showExportAsPictureDialog(url, getExportTitle(), ext,
				"Export", this);
	}

	public String getFileExtension() {
		return getPageController() == null ? ".ggb" : ".ggs";
	}

	@Override
	public void handleImageExport(String base64image) {

		if (base64image.startsWith(StringUtil.pdfMarker)) {
			openInLightbox(base64image, true);
		} else if (base64image.startsWith("JVBER")) {
			// PDF without header
			openInLightbox(StringUtil.pdfMarker + base64image, true);
		} else if (base64image.startsWith("<svg")) {
			// svg

			// can't use data:image/svg+xml;utf8 in IE11 / Edge
			openInLightbox(Browser.encodeSVG(base64image), false);
		} else if (base64image.startsWith("iVBOR")) {
			// PNG
			openInLightbox(StringUtil.pngMarker + base64image, false);
		} else {
			// PNG or SVG with header
			openInLightbox(base64image, false);
		}
	}

	public native boolean openInLightbox(String imageBase64, boolean pdf) /*-{

		//closeLightbox();
		var div = $wnd.document.getElementById("myLightboxDiv");
		if (div) {
			$wnd.document.body.removeChild(div);
		}

		div = $wnd.document.createElement("div");
		div.id = "myLightboxDiv";
		div.style.width = "80%";
		div.style.height = "80%";
		div.style.position = "fixed";
		div.style.top = "10%";
		div.style.left = "10%";
		div.style.border = "7px solid rgba(0, 0, 0, 0.5)";
		div.style.background = "#FFF";
		if (!pdf) {
			div.style["background-image"] = "url('" + imageBase64 + "')";
			div.style["background-size"] = "contain";
			div.style["background-repeat"] = "no-repeat";
			div.style["background-position"] = "center";
			div.style.zIndex = 100000000;
		}

		div.onclick = function() {
			$wnd.document.body.removeChild(div);
		};

		$wnd.document.body.appendChild(div);

		// now add transparent image over it
		// so that "Save image as..." works
		// remove this if you don't need it
		var elem;

		if (pdf) {
			elem = $wnd.document.createElement("iframe");
			elem.style.position = "relative";
		} else {
			elem = $wnd.document.createElement("img");
			elem.style.opacity = 0;
		}
		elem.src = imageBase64;
		elem.style.height = "100%";
		elem.style.width = "100%";
		div.appendChild(elem);
	}-*/;

	public String getSlideID() {
		return getPageController() == null ? null
				: getPageController().getSlideID();
	}


}
