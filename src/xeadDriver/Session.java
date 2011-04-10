package xeadDriver;

/*
 * Copyright (c) 2011 WATANABE kozo <qyf05466@nifty.com>,
 * All rights reserved.
 *
 * This file is part of XEAD Driver.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the XEAD Project nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLEditorKit;
import java.net.URI;
import org.apache.xerces.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import com.lowagie.text.pdf.BaseFont;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.security.*;

public class Session extends JFrame {
	private static final long serialVersionUID = 1L;
	private static ResourceBundle res = ResourceBundle.getBundle("xeadDriver.Res");
	private String systemName = "";
	private String sessionID = "";
	private String sessionStatus = "";
	private boolean noErrorsOccured = true;
	private String databaseUser = "";
	private String userID = "";
	private String userName = "";
	private String userEmployeeNo = "";
	private String userMenus = "";
	private String userTable = "";
	private String variantsTable = "";
	private String userVariantsTable = "";
	private String sessionTable = "";
	private String sessionDetailTable = "";
	private String numberingTable = "";
	private String calendarTable = "";
	private String taxTable = "";
	private String currencyTable = "";
	private String currencyDetailTable = "";
	private String databaseDisconnect = "";
	private String menuIDUsing = "";
	private String imageFileFolder = "";
	private File outputFolder = null;
	private String welcomePageURL = "";
	private String dateFormat = "";
	private int sqProgram = 0;
	private String currentFolder = "";
	private String loginScript = "";

	private Image imageTitle;
	private Dimension screenSize = new Dimension(0,0);
	private Connection connection = null;
	private Connection connectionForAutoNumber = null;
	private DatabaseMetaData databaseMetaData = null;
	private String[] menuIDArray = new String[20];
	private String[] menuCaptionArray = new String[20];
	private String[] helpURLArray = new String[20];
	private MenuOption[][] menuOptionArray = new MenuOption[20][20];
	private JButton[] jButtonMenuOptionArray = new JButton[20];

	private JLabel jLabelUser = new JLabel();
	private JLabel jLabelSession = new JLabel();
	private GridLayout gridLayoutMenuButtons = new GridLayout();
	private JPanel jPanelTop = new JPanel();
	private JPanel jPanelMenu = new JPanel();
	private JPanel jPanelMenuCenter = new JPanel();
	private JPanel jPanelMenuTopMargin = new JPanel();
	private JPanel jPanelMenuLeftMargin = new JPanel();
	private JPanel jPanelMenuRightMargin = new JPanel();
	private JPanel jPanelMenuBottomMargin = new JPanel();
	private JSplitPane jSplitPane1 = new JSplitPane();
	private JSplitPane jSplitPane2 = new JSplitPane();
	private JScrollPane jScrollPaneNews = new JScrollPane();
	private JScrollPane jScrollPaneMenu = new JScrollPane();
	private JTabbedPane jTabbedPaneMenu = new JTabbedPane();
	private JEditorPane jEditorPaneNews = new JEditorPane();
	private HTMLEditorKit editorKit = new HTMLEditorKit();
	private ImageIcon imageIcon = null;
	private JTextArea jTextAreaMessages = new JTextArea();
	private JScrollPane jScrollPaneMessages = new JScrollPane();
	private XFCalendar xFCalendar;
	private Calendar calendar = GregorianCalendar.getInstance();

	private org.w3c.dom.Document domDocument;
	private Desktop desktop = Desktop.getDesktop();
	private DigestAdapter digestAdapter = null;
	private LoginDialog loginDialog = null;
	private ModifyPasswordDialog modifyPasswordDialog = null;
	private Function function = new Function(this);
	private SortableDomElementListModel sortingList;
	private NodeList functionList = null;
	private NodeList tableList = null;
	private ArrayList<String> offDateList = new ArrayList<String>();
	private HashMap<String, BaseFont> baseFontMap = new HashMap<String, BaseFont>();
	private HashMap<String, String> attributeMap = new HashMap<String, String>();

	private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	private Bindings globalScriptBindings = scriptEngineManager.getBindings();

	public Session(String[] args) {
		String fileName = "";
		String loginUser = "";
		String loginPassword = "";
		//
		try {
			//
			if (args.length >= 1) {
				fileName =  args[0];
			}
			if (args.length == 3) {
				loginUser =  args[1];
				loginPassword =  args[2];
			}
			//
			///////////////////////////////////////
			// Use these steps for quick testing //
			///////////////////////////////////////
			//loginUser =  "00000";
			//loginPassword =  "0000000000";
			//fileName = "C:/XeadFramework/SalesOrosi/SalesOrosi.xeaf";
			//fileName = "C:/XeadFramework/Skeleton/Skeleton.xeaf";
			//
			if (fileName.equals("")) {
				JOptionPane.showMessageDialog(null, res.getString("SessionError1"));
				System.exit(0);
			} else {
				//
				loginDialog = setupVariantsAndShowLoginDialog(fileName, loginUser, loginPassword);
				if (loginDialog == null) {
					System.exit(0);
				} else {
					if (loginDialog.userIsValidated()) {
						//
						userID = loginDialog.getUserID();
						userName = loginDialog.getUserName();
						userEmployeeNo = loginDialog.getUserEmployeeNo();
						userMenus = loginDialog.getUserMenus();
						//
						setupSessionAndMenus();
						//
						this.setVisible(true);
						//
					}else {
						closeSession();
						System.exit(0);
					}
				}
			}
		} catch(ScriptException e) {
			JOptionPane.showMessageDialog(null, res.getString("FunctionError0") + "\n" + e.getMessage());
			noErrorsOccured = false;
			closeSession();
			System.exit(0);
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Login failed. " + e.getMessage());
			noErrorsOccured = false;
			closeSession();
			System.exit(0);
		}
	}
	
	LoginDialog setupVariantsAndShowLoginDialog(String fileName, String user, String password) throws Exception {
		String wrkStr= "";
        File xeafFile = new File(fileName);
		currentFolder = xeafFile.getParent();
		try {
			DOMParser parser = new DOMParser();
			parser.parse(new InputSource(new FileInputStream(fileName)));
			domDocument = parser.getDocument();
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(this, res.getString("SessionError2") + fileName + res.getString("SessionError3"));
			return null;
		}
		NodeList nodeList = domDocument.getElementsByTagName("System");
		org.w3c.dom.Element element = (org.w3c.dom.Element)nodeList.item(0);

		systemName = element.getAttribute("Name");
		welcomePageURL = element.getAttribute("WelcomePageURL");
		dateFormat = element.getAttribute("DateFormat");
		calendar.setLenient(false);
        wrkStr = element.getAttribute("ImageFileFolder"); 
		if (wrkStr.equals("")) {
			imageFileFolder = currentFolder + File.separator;
		} else {
			if (wrkStr.contains("<CURRENT>")) {
				imageFileFolder = wrkStr.replace("<CURRENT>", currentFolder) + File.separator;
			} else {
				imageFileFolder = wrkStr + File.separator;
			}
		}
		wrkStr = element.getAttribute("OutputFolder"); 
		if (wrkStr.equals("")) {
			outputFolder = null;
		} else {
			if (wrkStr.contains("<CURRENT>")) {
				wrkStr = wrkStr.replace("<CURRENT>", currentFolder);
			}
			outputFolder = new File(wrkStr);
			if (!outputFolder.exists()) {
				outputFolder = null;
			}
		}
		
		userTable = element.getAttribute("UserTable");
		variantsTable = element.getAttribute("VariantsTable");
		userVariantsTable = element.getAttribute("UserVariantsTable");
		numberingTable = element.getAttribute("NumberingTable");
		sessionTable = element.getAttribute("SessionTable");
		sessionDetailTable = element.getAttribute("SessionDetailTable");
		taxTable = element.getAttribute("TaxTable");
		calendarTable = element.getAttribute("CalendarTable");
		currencyTable = element.getAttribute("CurrencyTable");
		currencyDetailTable = element.getAttribute("CurrencyDetailTable");

		loginScript = XFUtility.substringLinesWithTokenOfEOL(element.getAttribute("LoginScript"), "\n");
		functionList = domDocument.getElementsByTagName("Function");
		tableList = domDocument.getElementsByTagName("Table");

		databaseUser = element.getAttribute("DatabaseUser");
		String databaseName = element.getAttribute("DatabaseName");
		if (databaseName.contains("<CURRENT>")) {
			databaseName = databaseName.replace("<CURRENT>", currentFolder);
		}
		try {
			connection = DriverManager.getConnection(databaseName, databaseUser, element.getAttribute("DatabasePassword"));
			connection.setAutoCommit(false);
			connectionForAutoNumber = DriverManager.getConnection(databaseName, databaseUser, element.getAttribute("DatabasePassword"));
			connectionForAutoNumber.setAutoCommit(true);
		} catch (Exception e) {
			if (e.getMessage().contains("java.net.ConnectException") && databaseName.contains("jdbc:derby://")) {
				JOptionPane.showMessageDialog(this, res.getString("SessionError4") + systemName + res.getString("SessionError5"));
			} else {
				JOptionPane.showMessageDialog(this, res.getString("SessionError6") + databaseName + res.getString("SessionError7") + e.getMessage());
			}
			return null;
		}
		databaseDisconnect = element.getAttribute("DatabaseDisconnect");
		databaseMetaData = connection.getMetaData();

		org.w3c.dom.Element fontElement;
		BaseFont baseFont;
		NodeList printFontList = domDocument.getElementsByTagName("PrintFont");
		for (int i = 0; i < printFontList.getLength(); i++) {
			fontElement = (org.w3c.dom.Element)printFontList.item(i);
			wrkStr = fontElement.getAttribute("ID");
			try {
				baseFont = BaseFont.createFont(fontElement.getAttribute("PDFFontName"), fontElement.getAttribute("PDFEncoding"), false);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, res.getString("SessionError8") + fontElement.getAttribute("FontName") + res.getString("SessionError9"));
				baseFont = BaseFont.createFont("Times-Roman", "Cp1252", false);
			}
			baseFontMap.put(wrkStr, baseFont);
		}

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	 	imageTitle = Toolkit.getDefaultToolkit().createImage(xeadDriver.Session.class.getResource("title.png"));
		this.setIconImage(imageTitle);
		this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.setPreferredSize(new Dimension(screenSize.width - 40, screenSize.height - 30));
		this.setTitle(systemName);
	    this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		jTabbedPaneMenu.setFont(new java.awt.Font("SansSerif", 0, 14));
		jTabbedPaneMenu.addKeyListener(new Session_keyAdapter(this));
		jTabbedPaneMenu.requestFocus();
		jTabbedPaneMenu.addChangeListener(new Session_jTabbedPaneMenu_changeAdapter(this));
		jLabelUser.setFont(new java.awt.Font("SansSerif", 0, 14));
		jLabelSession.setFont(new java.awt.Font("SansSerif", 0, 14));

		jEditorPaneNews.setBorder(BorderFactory.createEtchedBorder());
		jEditorPaneNews.setEditable(false);
		jEditorPaneNews.setContentType("text/html");
		jEditorPaneNews.addHyperlinkListener(new Session_jEditorPane_actionAdapter(this));
		jEditorPaneNews.setFocusable(false);
		editorKit.install(jEditorPaneNews);
		jEditorPaneNews.setEditorKit(editorKit);
		boolean isValidURL = false;
		if (!welcomePageURL.equals("")) {
			try {
				jEditorPaneNews.setPage(welcomePageURL);
				jScrollPaneNews.getViewport().add(jEditorPaneNews, null);
				isValidURL = true;
			} catch (Exception ex) {
			}
		}
		if (!isValidURL) {
			String defaultImageFileName = currentFolder + File.separator + "WelcomePageDefaultImage.jpg"; 
			File imageFile = new File(defaultImageFileName);
			if (imageFile.exists()) {
				imageIcon = new ImageIcon(defaultImageFileName);
				Image image1 = imageIcon.getImage();
				int maxWidth = screenSize.width - 30;
				Image image2 = image1.getScaledInstance(maxWidth, -1, Image.SCALE_SMOOTH);
				imageIcon.setImage(image2);
				JLabel labelImage = new JLabel("", imageIcon, JLabel.CENTER);
				jScrollPaneNews.getViewport().add(labelImage, null);
			} else {
				if (welcomePageURL.equals("")) {
					jEditorPaneNews.setText(res.getString("SessionError10"));
				} else {
					jEditorPaneNews.setText(res.getString("SessionError11") + welcomePageURL + res.getString("SessionError12"));
				}
			}
		}

		jScrollPaneMenu.getViewport().add(jPanelMenu, null);
		jPanelMenuTopMargin.setPreferredSize(new Dimension(15, 15));
		jPanelMenuTopMargin.setOpaque(false);
		jPanelMenuLeftMargin.setPreferredSize(new Dimension(40, 40));
		jPanelMenuLeftMargin.setOpaque(false);
		jPanelMenuRightMargin.setPreferredSize(new Dimension(40, 40));
		jPanelMenuRightMargin.setOpaque(false);
		jPanelMenuBottomMargin.setPreferredSize(new Dimension(10, 10));
		jPanelMenuBottomMargin.setOpaque(false);
		jPanelMenuCenter.setOpaque(false);
		jPanelMenu.setLayout(new BorderLayout());
		jPanelMenu.add(jPanelMenuTopMargin, BorderLayout.NORTH);
		jPanelMenu.add(jPanelMenuLeftMargin, BorderLayout.WEST);
		jPanelMenu.add(jPanelMenuRightMargin, BorderLayout.EAST);
		jPanelMenu.add(jPanelMenuBottomMargin, BorderLayout.SOUTH);
		jPanelMenu.add(jPanelMenuCenter, BorderLayout.CENTER);
		jPanelMenuCenter.setLayout(gridLayoutMenuButtons);
		gridLayoutMenuButtons.setColumns(2);
		gridLayoutMenuButtons.setRows(10);
		gridLayoutMenuButtons.setHgap(80);
		gridLayoutMenuButtons.setVgap(15);
		for (int i = 0; i < 20; i++) {
			jButtonMenuOptionArray[i] = new JButton();
			jButtonMenuOptionArray[i].setFont(new java.awt.Font("SansSerif", 0, 16));
			jButtonMenuOptionArray[i].addActionListener(new Session_jButton_actionAdapter(this));
			jButtonMenuOptionArray[i].addKeyListener(new Session_keyAdapter(this));
		}
		jPanelMenuCenter.add(jButtonMenuOptionArray[0]);
		jPanelMenuCenter.add(jButtonMenuOptionArray[10]);
		jPanelMenuCenter.add(jButtonMenuOptionArray[1]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[11]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[2]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[12]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[3]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[13]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[4]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[14]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[5]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[15]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[6]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[16]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[7]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[17]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[8]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[18]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[9]);
	    jPanelMenuCenter.add(jButtonMenuOptionArray[19]);

		jPanelTop.setPreferredSize(new Dimension(10, 30));
		jPanelTop.setBorder(BorderFactory.createLoweredBevelBorder());
	    jPanelTop.setLayout(new BorderLayout());
	    jPanelTop.add(jLabelUser, BorderLayout.WEST);
	    jPanelTop.add(jLabelSession, BorderLayout.EAST);
	    jScrollPaneMessages.setPreferredSize(new Dimension(10, 40));
	    jScrollPaneMessages.getViewport().add(jTextAreaMessages, null);
		jTextAreaMessages.setEditable(false);
		jTextAreaMessages.setBorder(BorderFactory.createEtchedBorder());
	    jTextAreaMessages.setFont(new java.awt.Font("SansSerif", 0, 14));
		jTextAreaMessages.setText(res.getString("SessionMessage"));
		jTextAreaMessages.setFocusable(false);
	    jTextAreaMessages.setLineWrap(true);
		this.getContentPane().setFocusable(false);
		this.getContentPane().add(jPanelTop, BorderLayout.NORTH);
		this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
		this.pack();
	    jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    jSplitPane2.add(jScrollPaneNews, JSplitPane.TOP);
	    jSplitPane2.add(jTabbedPaneMenu, JSplitPane.BOTTOM);
	    jSplitPane2.setDividerLocation(screenSize.height - 635);
	    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    jSplitPane1.add(jSplitPane2, JSplitPane.TOP);
	    jSplitPane1.add(jScrollPaneMessages, JSplitPane.BOTTOM);
	    jSplitPane1.setDividerLocation(screenSize.height - 120);

	    for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
		    	menuOptionArray[i][j] = null;
			}
	    }

        digestAdapter = new DigestAdapter("MD5");
	    modifyPasswordDialog = new ModifyPasswordDialog(this);; 

	    ///////////////////////////////////////////////
	    // Show Login Dialog and Return Login Object //
	    ///////////////////////////////////////////////
		return new LoginDialog(this, user, password);
	}
	
	void setupSessionAndMenus() throws ScriptException, Exception {
		sessionID = this.getNextNumber("NRSESSION");
		sessionStatus = "ACT";
		InetAddress ip = InetAddress.getLocalHost();
		//
		try {
			Statement statement = connection.createStatement();
			String insertSQL = "insert into " + sessionTable
			+ " (NRSESSION, IDUSER, DTLOGIN, TXIPADDRESS, KBSESSIONSTATUS) values ("
			+ "'" + sessionID + "'," + "'" + userID + "'," + "CURRENT_TIMESTAMP," + "'" + ip.toString() + "','" + sessionStatus + "')";
			statement.executeUpdate(insertSQL);
			connection.commit();
			//
			String sql = "select * from " + calendarTable;
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				offDateList.add(result.getString("DTOFF"));
			}
			result.close();
			//
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//
		jLabelUser.setText("User " + userName);
		jLabelSession.setText("Session " + sessionID);
		//
		NodeList menuList = domDocument.getElementsByTagName("Menu");
		sortingList = XFUtility.getSortedListModel(menuList, "ID");
		if (userMenus.equals("ALL")) {
			buildMenuWithID("");
		} else {
			StringTokenizer workTokenizer = new StringTokenizer(userMenus, "," );
			while (workTokenizer.hasMoreTokens()) {
				buildMenuWithID(workTokenizer.nextToken());
			}
		}
		//
		setupOptionsOfMenuWithTabNo(0);
		//
		this.validate();
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//
		//////////////////////////////////////////////////
		// Setup Script Bindings and Run "Login Script" //
		//////////////////////////////////////////////////
	    globalScriptBindings.put("session", new XFSessionForScript(this));
		ScriptEngine scriptEngine = getScriptEngineManager().getEngineByName("js");
		if (!loginScript.equals("")) {
			scriptEngine.eval(loginScript);
		}
	    //
		xFCalendar = new XFCalendar(this);
	}

	ScriptEngineManager getScriptEngineManager() {
		return scriptEngineManager;
	}
	
	public BaseFont getBaseFontWithID(String id) {
		return baseFontMap.get(id);
	}
	
	void buildMenuWithID(String id) {
		int optionNo = 0;
		NodeList optionList = null;
		org.w3c.dom.Element menuElement = null;
		org.w3c.dom.Element optionElement = null;
		org.w3c.dom.Element workElement = null;
		int menuIndex;
		//
		for (int i = 0; i < sortingList.getSize(); i++) {
			menuElement = (org.w3c.dom.Element)sortingList.getElementAt(i);
			if (menuElement.getAttribute("ID").equals(id) || id.equals("")) {
				if (jTabbedPaneMenu.getTabCount() == 0) {
					jTabbedPaneMenu.addTab(menuElement.getAttribute("Name"), null, jScrollPaneMenu);
				} else {
					jTabbedPaneMenu.addTab(menuElement.getAttribute("Name"), null, null);
				}
				menuIndex = jTabbedPaneMenu.getTabCount() - 1;
				menuIDArray[menuIndex] = menuElement.getAttribute("ID");
				menuCaptionArray[menuIndex] = menuElement.getAttribute("Name");
				helpURLArray[menuIndex] = menuElement.getAttribute("HelpURL");
				//
				optionList = menuElement.getElementsByTagName("Option");
				for (int j = 0; j < optionList.getLength(); j++) {
					optionElement = (org.w3c.dom.Element)optionList.item(j);
					optionNo = Integer.parseInt(optionElement.getAttribute("Index"));
					org.w3c.dom.Element functionElement = null;
					for (int k = 0; k < functionList.getLength(); k++) {
						workElement = (org.w3c.dom.Element)functionList.item(k);
						if (workElement.getAttribute("ID").equals(optionElement.getAttribute("FunctionID"))) {
							functionElement = workElement;
							break;
						}
					}
					//menuOptionArray[menuIndex][optionNo] = new MenuOption(functionElement, optionElement.getAttribute("OptionName"), optionElement.getAttribute("Parameters"));
					menuOptionArray[menuIndex][optionNo] = new MenuOption(functionElement, optionElement.getAttribute("OptionName"));
				}
				//menuOptionArray[menuIndex][19] = new MenuOption(null, "LOGOUT", "");
				menuOptionArray[menuIndex][19] = new MenuOption(null, "LOGOUT");
				//
				if (!id.equals("")) {
					break;
				}
			}
		}
		
	}
	
	public String getCurrentMenuID() {
		return menuIDArray[jTabbedPaneMenu.getSelectedIndex()];
	}

	public void browseHelp() {
		if (helpURLArray[jTabbedPaneMenu.getSelectedIndex()].equals("")) {
			JOptionPane.showMessageDialog(null, res.getString("SessionError16") + menuCaptionArray[jTabbedPaneMenu.getSelectedIndex()] + res.getString("SessionError17"));
		} else {
			try {
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				desktop.browse(new URI(helpURLArray[jTabbedPaneMenu.getSelectedIndex()]));
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, res.getString("SessionError18") + menuCaptionArray[jTabbedPaneMenu.getSelectedIndex()] + res.getString("SessionError19") + ex.getMessage());
			} catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, res.getString("SessionError18") + menuCaptionArray[jTabbedPaneMenu.getSelectedIndex()] + res.getString("SessionError19") + ex.getMessage());
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, res.getString("SessionError18") + menuCaptionArray[jTabbedPaneMenu.getSelectedIndex()] + res.getString("SessionError19") + ex.getMessage());
			} finally {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	
	void setupOptionsOfMenuWithTabNo(int tabNumber) {
		if (tabNumber > -1) {
			menuIDUsing = menuIDArray[tabNumber];
			for (int i = 0; i < 20; i++) {
				if (menuOptionArray[tabNumber][i] == null) {
					jButtonMenuOptionArray[i].setVisible(false);
				} else {
					jButtonMenuOptionArray[i].setText(menuOptionArray[tabNumber][i].getMenuOptionName());
					jButtonMenuOptionArray[i].setVisible(true);
				}
			}
		}
	}

	public java.util.Date getDateOnCalendar(Component compo, java.util.Date defaultDate) {
		return xFCalendar.getDateOnCalendar(compo, defaultDate);
	}

	public String getTimeStamp() {
		return XFUtility.getUserExpressionOfUtilDate(null, "", true);
	}

	public String getToday() {
		return getToday("");
	}

	public String getToday(String dateFormat) {
		return XFUtility.getUserExpressionOfUtilDate(null, dateFormat, false);
	}

	public String getTodayTime(String dateFormat) {
		return XFUtility.getUserExpressionOfUtilDate(null, dateFormat, true);
	}

	public String getThisMonth() {
		SimpleDateFormat dfm = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		return dfm.format(cal.getTime());
	}
	
	public String getErrorOfAccountDate(String dateValue) {
		String message = "";
		try {
			int yyyyMSeq = getSystemVariantInteger("ALLOWED_FISCAL_MONTH");
			if (yyyyMSeq > 200000 && yyyyMSeq < 210000) {
				String date = dateValue.replace("/", "");
				date = date.replace("-", "");
				if (date.length() >= 8) {
					date = date.substring(0, 8);
					int yyyy = getFYearOfDate(date);
					int mSeq = getMSeqOfDate(dateValue);
					int yyyyMSeqTarget = yyyy * 100 + mSeq;
					if (yyyyMSeqTarget < yyyyMSeq) {
						message = res.getString("FunctionError49");
					}
				}
			}
		} catch (NumberFormatException e) {
		} 
		return message;
	}


	public String getOffsetDate(String dateFrom, int offset, int countType) {
		String offsetDate = "";
		Date workDate;
		SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		//
		dateFrom = dateFrom.replaceAll("-", "").trim();
		//
        int y = Integer.parseInt(dateFrom.substring(0,4));
        int m = Integer.parseInt(dateFrom.substring(4,6));
        int d = Integer.parseInt(dateFrom.substring(6,8));
		Calendar cal = Calendar.getInstance();
		cal.set(y, m-1, d);
		//
		if (countType == 0) {
			cal.add(Calendar.DATE, offset);
		}
		//
		if (countType == 1) {
			for (int i = 0; i < offset; i++) {
				cal.add(Calendar.DATE, 1);
				workDate = cal.getTime();
				if (offDateList.contains(dfm.format(workDate))) {
					offset++;
				}
			}
		}
		//
		workDate = cal.getTime();
		offsetDate = dfm.format(workDate);
		//
		return offsetDate;
	}

	public int getDaysBetweenDates(String strDateFrom, String strDateThru, int countType) {
		int days = 0;
		int y, m, d;
		Date dateFrom, dateThru;
		SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		//
		strDateFrom = strDateFrom.replaceAll("-", "").trim();
        y = Integer.parseInt(strDateFrom.substring(0,4));
        m = Integer.parseInt(strDateFrom.substring(4,6));
        d = Integer.parseInt(strDateFrom.substring(6,8));
		cal.set(y, m-1, d, 0, 0, 0);
		dateFrom = cal.getTime();
		//
		strDateThru = strDateThru.replaceAll("-", "").trim();
        y = Integer.parseInt(strDateThru.substring(0,4));
        m = Integer.parseInt(strDateThru.substring(4,6));
        d = Integer.parseInt(strDateThru.substring(6,8));
		cal.set(y, m-1, d, 0, 0, 0);
		dateThru = cal.getTime();
		//
		if (countType == 0) {
			long diff = dateThru.getTime() - dateFrom.getTime();
			days = (int)(diff / 86400000); 
		}
		//
		if (countType == 1) {
			//
			if (dateThru.getTime() == dateFrom.getTime()) {
				days = 0;
			}
			//
			if (dateThru.getTime() > dateFrom.getTime()) {
		        y = Integer.parseInt(strDateFrom.substring(0,4));
		        m = Integer.parseInt(strDateFrom.substring(4,6));
		        d = Integer.parseInt(strDateFrom.substring(6,8));
				cal.set(y, m-1, d, 0, 0, 0);
				long timeThru = dateThru.getTime();
				long timeWork = dateFrom.getTime();
				while (timeThru > timeWork) {
					cal.add(Calendar.DATE, 1);
					if (!offDateList.contains(dfm.format(cal.getTime()))) {
						days++;
					}
					timeWork = cal.getTime().getTime();
				}
			}
			//
			if (dateThru.getTime() < dateFrom.getTime()) {
		        y = Integer.parseInt(strDateThru.substring(0,4));
		        m = Integer.parseInt(strDateThru.substring(4,6));
		        d = Integer.parseInt(strDateThru.substring(6,8));
				cal.set(y, m-1, d, 0, 0, 0);
				long timeWork = dateThru.getTime();
				long timeFrom = dateFrom.getTime();
				while (timeFrom > timeWork) {
					cal.add(Calendar.DATE, 1);
					if (!offDateList.contains(dfm.format(cal.getTime()))) {
						days++;
					}
					timeWork = cal.getTime().getTime();
				}
				days = days * -1;
			}
		}
		//
		return days;
	}

	public boolean isValidDate(String date) {
		boolean result = true;
		String argDate = date.replaceAll("-", "").trim();
        int y = Integer.parseInt(argDate.substring(0,4));
        int m = Integer.parseInt(argDate.substring(4,6));
        int d = Integer.parseInt(argDate.substring(6,8));
		try {
			calendar.set(y, m-1, d);
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public boolean isOffDate(String date) {
		return offDateList.contains(date);
	}

	public String getNextNumber(String numberID) {
		String wrkStr, nextNumber = "";
		int wrkInt, wrkSum, count;
		//
		try {
			//
			Statement statement = connectionForAutoNumber.createStatement();
			String fetchSQL = "select * from " + numberingTable + " where IDNUMBER = '" + numberID + "'";
			//
			count = 0;
			while (count == 0) {
				//
				ResultSet result = statement.executeQuery(fetchSQL);
				if (result.next()) {
					//
					// Format Number Value //
					DecimalFormat decimalFormat = new DecimalFormat();
					StringBuffer buf = new StringBuffer();
					for (int i = 0; i < result.getInt("NRNUMDIGIT"); i++) {
						buf.append("0");
					}
					decimalFormat.applyPattern(buf.toString());
					int number = result.getInt("NRCURRENT");
					String outputdata = decimalFormat.format(number);
					if (result.getString("FGWITHCD").equals("T")) {
						wrkSum = 0;
						for (int i = 0; i < outputdata.length(); i++) {
							wrkStr = outputdata.substring(i, i+1);
							wrkInt = Integer.parseInt(wrkStr);
							if ((wrkInt % 2) == 1) {
								wrkInt = wrkInt * 3;
							}
							wrkSum = wrkSum + wrkInt;
						}
						wrkInt = wrkSum % 10;
						wrkInt = 10 - wrkInt; //Check Digit with Modulus 10//
						nextNumber = result.getString("TXPREFIX").trim() + outputdata + wrkInt;
					} else {
						nextNumber = result.getString("TXPREFIX").trim() + outputdata;
					}
					//
					// Update Numbering table record //
					number++;
					if (result.getInt("NRNUMDIGIT") == 1 && number == 10) {
						number = 0;
					}
					if (result.getInt("NRNUMDIGIT") == 2 && number == 100) {
						number = 10;
					}
					if (result.getInt("NRNUMDIGIT") == 3 && number == 1000) {
						number = 100;
					}
					if (result.getInt("NRNUMDIGIT") == 4 && number == 10000) {
						number = 1000;
					}
					if (result.getInt("NRNUMDIGIT") == 5 && number == 100000) {
						number = 10000;
					}
					if (result.getInt("NRNUMDIGIT") == 6 && number == 1000000) {
						number = 100000;
					}
					if (result.getInt("NRNUMDIGIT") == 7 && number == 10000000) {
						number = 1000000;
					}
					if (result.getInt("NRNUMDIGIT") == 8 && number == 100000000) {
						number = 10000000;
					}
					if (result.getInt("NRNUMDIGIT") == 9 && number == 1000000000) {
						number = 100000000;
					}
					//
					//String updateSQL = "update " + numberingTable + " set NRCURRENT = " + number + " where IDNUMBER = '" + numberID + "'";
					String updateSQL = "update " + numberingTable + 
					" set NRCURRENT = " + number +
					", UPDCOUNTER = " + (result.getInt("UPDCOUNTER") + 1) +
					" where IDNUMBER = '" + numberID + "'" +
					" and UPDCOUNTER = " + result.getInt("UPDCOUNTER");
					//
					count = statement.executeUpdate(updateSQL); //Auto commit connection //
					if (count == 0) {
						// Set thread asleep for a while(10-110 milisec) to avoid update emulation.//
						wrkInt = (int)(Math.random() * 100.0) % 10;
						Thread.sleep(10 + wrkInt * 10);
					}
				} else {
					JOptionPane.showMessageDialog(null, res.getString("SessionError13") + numberID + res.getString("SessionError14"));
					break;
				}
				//
				result.close();
			}
			//
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return nextNumber;
	}
	
	public String getSystemVariantString(String itemID) {
		String strValue = "";
		try {
			Statement statement = this.getConnection().createStatement();
			ResultSet result = statement.executeQuery("select * from " + variantsTable + " where IDVARIANT = '" + itemID + "'");
			if (result.next()) {
				strValue = result.getString("TXVALUE").trim();
				if (strValue.contains("<CURRENT>")) {
					strValue = strValue.replace("<CURRENT>", currentFolder);
				}
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strValue;
	}
	
	public int getSystemVariantInteger(String itemID) {
		String strValue = "";
		int intValue = 0;
		try {
			Statement statement = this.getConnection().createStatement();
			ResultSet result = statement.executeQuery("select * from " + variantsTable + " where IDVARIANT = '" + itemID + "'");
			if (result.next()) {
				strValue = result.getString("TXVALUE").trim();
				if (result.getString("TXTYPE").trim().equals("NUMBER")) {
					intValue = Integer.parseInt(strValue);
				}
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return intValue;
	}
	
	public float getSystemVariantFloat(String itemID) {
		String strValue = "";
		float floatValue = (float)0.0;
		try {
			Statement statement = this.getConnection().createStatement();
			ResultSet result = statement.executeQuery("select * from " + variantsTable + " where IDVARIANT = '" + itemID + "'");
			if (result.next()) {
				strValue = result.getString("TXVALUE").trim();
				if (result.getString("TXTYPE").trim().equals("NUMBER")) {
					floatValue = Float.parseFloat(strValue);
				}
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return floatValue;
	}

	public float getCurrencyRate(String currencyCode, String date) {
		float rateReturn = 0;
		float rateAnnual = 0;
		ResultSet result;
		//
		try {
			Statement statement = this.getConnection().createStatement();
			result = statement.executeQuery("select * from " + 
						currencyTable + " where CDCURRENCY = '" + currencyCode + "'");
			if (result.next()) {
				rateAnnual = result.getFloat("VLANNUALRATE");
			}
			result.close();
			//
			if (date.equals("")) {
				rateReturn = rateAnnual;
			} else {
				date = date.replaceAll("-", "");
				if (date.length() == 8) {
					String yyyymm = date.substring(0, 6);
					result = statement.executeQuery("select * from " +
							currencyDetailTable + " where CDCURRENCY = '" + currencyCode + "' and DTYYYYMM = '" + yyyymm + "'");
					if (result.next()) {
						rateReturn = result.getFloat("VLMONTHLYRATE");
					}
					result.close();
				}
				if (rateReturn == 0) {
					rateReturn = rateAnnual;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//
		return rateReturn;
	}

	public int getTaxAmount(String date, int amount) {
		int fromDate = 0;
		int taxAmount = 0;
		//
		if (!date.equals("") && date != null) {
			int targetDate = Integer.parseInt(date.replaceAll("-", ""));
			float rate = 0;
			//
			try {
				Statement statement = this.getConnection().createStatement();
				ResultSet result = statement.executeQuery("select * from " + taxTable + " order by DTSTART");
				while (result.next()) {
					fromDate = Integer.parseInt(result.getString("DTSTART").replaceAll("-", ""));
					if (targetDate >= fromDate) {
						rate = result.getFloat("VLTAXRATE");
					}
				}
				result.close();
				//
				taxAmount = (int)Math.floor(amount * rate);
				//
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//
		return taxAmount;
	}

	public int getMSeqOfDate(String parmDate) {
		int mSeq = 0;
		if (!parmDate.equals("")) {
			int month, date;
			parmDate = parmDate.replaceAll("-", "").trim();
			parmDate = parmDate.replaceAll("/", "");
	        month = Integer.parseInt(parmDate.substring(4,6));
	        date = Integer.parseInt(parmDate.substring(6,8));
			//
			boolean isWithinMonth = false;
			int startMonth = 1;
			int lastDay = 31;
			//
			startMonth = getSystemVariantInteger("FIRST_MONTH");
			lastDay = getSystemVariantInteger("LAST_DAY");
			//
			if (lastDay >= 31) {
				if ((month == 1 && date <= 31) 
						|| (month == 2 && date <= 29)
						|| (month == 3 && date <= 31)
						|| (month == 4 && date <= 30)
						|| (month == 5 && date <= 31)
						|| (month == 6 && date <= 30)
						|| (month == 7 && date <= 31)
						|| (month == 8 && date <= 31)
						|| (month == 9 && date <= 30)
						|| (month == 10 && date <= 31)
						|| (month == 11 && date <= 30)
						|| (month == 12 && date <= 31)) {
					isWithinMonth = true;
				}
			}
			if (lastDay == 30) {
				if ((month == 1 && date <= 30) 
						|| (month == 2 && date <= 29)
						|| (month == 3 && date <= 30)
						|| (month == 4 && date <= 30)
						|| (month == 5 && date <= 30)
						|| (month == 6 && date <= 30)
						|| (month == 7 && date <= 30)
						|| (month == 8 && date <= 30)
						|| (month == 9 && date <= 30)
						|| (month == 10 && date <= 30)
						|| (month == 11 && date <= 30)
						|| (month == 12 && date <= 30)) {
					isWithinMonth = true;
				}
			}
			if (lastDay <= 29 && date <= lastDay) {
					isWithinMonth = true;
			}
			//
			if (isWithinMonth) {
				mSeq = month - startMonth + 1;
			} else {
				mSeq = month - startMonth + 2;
			}
			if (mSeq <= 0) {
				mSeq = mSeq + 12;
			}
		}
		return mSeq;
	}

	public int getFYearOfDate(String parmDate) {
		int fYear = 0;
		int mSeq = 0;
		if (!parmDate.equals("")) {
			int month, date;
			parmDate = parmDate.replaceAll("-", "").trim();
			parmDate = parmDate.replaceAll("/", "");
	        fYear = Integer.parseInt(parmDate.substring(0,4));
	        month = Integer.parseInt(parmDate.substring(4,6));
	        date = Integer.parseInt(parmDate.substring(6,8));
			//
			boolean isWithinMonth = false;
			int startMonth = 1;
			int lastDay = 31;
			//
			startMonth = getSystemVariantInteger("FIRST_MONTH");
			lastDay = (Integer)getSystemVariantInteger("LAST_DAY");
			//
			if (lastDay >= 31) {
				if ((month == 1 && date <= 31) 
						|| (month == 2 && date <= 29)
						|| (month == 3 && date <= 31)
						|| (month == 4 && date <= 30)
						|| (month == 5 && date <= 31)
						|| (month == 6 && date <= 30)
						|| (month == 7 && date <= 31)
						|| (month == 8 && date <= 31)
						|| (month == 9 && date <= 30)
						|| (month == 10 && date <= 31)
						|| (month == 11 && date <= 30)
						|| (month == 12 && date <= 31)) {
					isWithinMonth = true;
				}
			}
			if (lastDay == 30) {
				if ((month == 1 && date <= 30) 
						|| (month == 2 && date <= 29)
						|| (month == 3 && date <= 30)
						|| (month == 4 && date <= 30)
						|| (month == 5 && date <= 30)
						|| (month == 6 && date <= 30)
						|| (month == 7 && date <= 30)
						|| (month == 8 && date <= 30)
						|| (month == 9 && date <= 30)
						|| (month == 10 && date <= 30)
						|| (month == 11 && date <= 30)
						|| (month == 12 && date <= 30)) {
					isWithinMonth = true;
				}
			}
			if (lastDay <= 29 && date <= lastDay) {
					isWithinMonth = true;
			}
			//
			if (isWithinMonth) {
				mSeq = month - startMonth + 1;
			} else {
				mSeq = month - startMonth + 2;
			}
			if (mSeq <= 0) {
				fYear = fYear - 1;
			}
		}
		return fYear;
	}
	
	public String getYearMonthOfFYearMSeq(String fYearMSeq) {
		String resultYear = "";
		String resultMonth = "";
		int workInt;
		int startMonth = getSystemVariantInteger("FIRST_MONTH");
		int fYear = Integer.parseInt(fYearMSeq.substring(0, 4)); 
		int mSeq = Integer.parseInt(fYearMSeq.substring(4, 6)); 
		//
		workInt = startMonth + mSeq - 1;
		if (workInt > 12) {
			workInt = workInt - 12;
			resultMonth = Integer.toString(workInt);
			workInt = fYear + 1;
			resultYear = Integer.toString(workInt);
		} else {
			resultMonth = Integer.toString(workInt);
			workInt = fYear;
			resultYear = Integer.toString(workInt);
		}
		//
		return resultYear + resultMonth;
	}

	void closeSession() {
		try {
			//
			if (noErrorsOccured) {
				sessionStatus = "END";
			} else {
				sessionStatus = "ERR";
			}
			//
			Statement statement = connection.createStatement();
			String updateSQL = "update " + sessionTable
			+ " set DTLOGOUT=CURRENT_TIMESTAMP, KBSESSIONSTATUS='" + sessionStatus + "' where " + "NRSESSION='" + sessionID + "'";
			statement.executeUpdate(updateSQL);
			//
			connection.commit();
			connection.close();
			//
			if (!databaseDisconnect.equals("")) {
				DriverManager.getConnection(databaseDisconnect);
			}
			//
		} catch (SQLException ex1) {
			//
			// Check if disconnected successfully(SQLState=XJ015) //
			if (ex1.getSQLState() != null && !ex1.getSQLState().equals("XJ015")) {
				ex1.printStackTrace();
			}
		} catch (Exception ex2) {
			ex2.printStackTrace();
		}
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			super.processWindowEvent(e);
			closeSession();
			System.exit(0);
		} else {
			super.processWindowEvent(e);
		}
	}

	void jEditorPane_hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				desktop.browse(e.getURL().toURI());
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	void jButtonMenu_actionPerformed(ActionEvent e) {
		try {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			//
			Component com = (Component)e.getSource();
			for (int i = 0; i < 20; i++) {
				if (com.equals(jButtonMenuOptionArray[i])) {
					if (menuOptionArray[jTabbedPaneMenu.getSelectedIndex()][i].isLogoutOption) {
						this.closeSession();
						this.setVisible(false);
						System.exit(0);
					} else {
						HashMap<String, Object> returnMap = menuOptionArray[jTabbedPaneMenu.getSelectedIndex()][i].call();
						if (returnMap != null && returnMap.get("RETURN_CODE") != null) {
							if (returnMap.get("RETURN_MESSAGE") == null) {
								jTextAreaMessages.setText(XFUtility.getMessageOfReturnCode(returnMap.get("RETURN_CODE").toString()));
							} else {
								jTextAreaMessages.setText(returnMap.get("RETURN_MESSAGE").toString());
							}
						}
					}
					break;
				}
			}
		} finally {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	int writeLogOfFunctionStarted(String functionID, String functionName) {
		try {
			sqProgram++;
			Statement statement = connection.createStatement();
			String insertSQL = "insert into " + sessionDetailTable
			+ " (NRSESSION, SQPROGRAM, IDMENU, IDPROGRAM, TXPROGRAM, DTSTART, KBPROGRAMSTATUS) values ("
			+ "'" + this.getSessionID() + "'," + sqProgram + "," + "'" + this.getMenuID() + "'," + "'" + functionID + "'," + "'" + functionName + "'," + "CURRENT_TIMESTAMP,'')";
			statement.executeUpdate(insertSQL);
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sqProgram;
	}

	void writeLogOfFunctionClosed(int sqProgramOfFunction, String programStatus, String errorLog) {
		try {
			if (errorLog.length() > 20000) {
				errorLog = errorLog.substring(0, 20000) + "...";
			}
			if (programStatus.equals("99")) {
				noErrorsOccured = false;
			}
			Statement statement = connection.createStatement();
			String updateSQL = "update " + sessionDetailTable
			+ " set DTEND=CURRENT_TIMESTAMP, KBPROGRAMSTATUS='" + programStatus + "', TXERRORLOG='" + errorLog + "' where " + "NRSESSION='" + this.getSessionID() + "' and " + "SQPROGRAM=" + sqProgramOfFunction;
			statement.executeUpdate(updateSQL);
			connection.commit();
			//
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	void menu_keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F1) {
			browseHelp();
		}
		if (e.getKeyCode() == KeyEvent.VK_F12) {
			boolean modified = modifyPasswordDialog.passwordModified();
			if (modified) {
				jTextAreaMessages.setText(res.getString("PasswordModified"));
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			Component com = getFocusOwner();
			for (int i = 0; i < 20; i++) {
				if (com.equals(jButtonMenuOptionArray[i])) {
					setFocusOnNextVisibleButton(i, "RIGHT");
					break;
				}
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			Component com = getFocusOwner();
			for (int i = 0; i < 20; i++) {
				if (com.equals(jButtonMenuOptionArray[i])) {
					setFocusOnNextVisibleButton(i, "LEFT");
					break;
				}
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			Component com = getFocusOwner();
			if (com.equals(jTabbedPaneMenu)) {
				for (int i = 0; i < 20; i++) {
					if (jButtonMenuOptionArray[i].isVisible()) {
						jButtonMenuOptionArray[i].requestFocus();
						break;
					}
				}
			} else {
				for (int i = 0; i < 20; i++) {
					if (com.equals(jButtonMenuOptionArray[i])) {
						setFocusOnNextVisibleButton(i, "DOWN");
						break;
					}
				}
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			Component com = getFocusOwner();
			if (com.equals(jButtonMenuOptionArray[10])) {
				jTabbedPaneMenu.requestFocus();
			} else {
				if (com.equals(jButtonMenuOptionArray[0])) {
					jTabbedPaneMenu.requestFocus();
				} else {
					for (int i = 0; i < 20; i++) {
						if (com.equals(jButtonMenuOptionArray[i])) {
							setFocusOnNextVisibleButton(i, "UP");
							break;
						}
					}
				}
			}
		}
	}
	
	void setFocusOnNextVisibleButton(int index, String direction) {
		int i= index;
		//
		if (direction.equals("RIGHT")) {
			if (i >= 0 && i <= 9) {
				i = i + 10;
				if (jButtonMenuOptionArray[i].isVisible()) {
					jButtonMenuOptionArray[i].requestFocus();
				} else {
					while (i != 19) {
						i = i + 1;
						if (jButtonMenuOptionArray[i].isVisible()) {
							jButtonMenuOptionArray[i].requestFocus();
							break;
						}
					}
				}
			}
		}
		//
		if (direction.equals("LEFT")) {
			if (i >= 10 && i <= 19) {
				i = i - 10;
				if (jButtonMenuOptionArray[i].isVisible()) {
					jButtonMenuOptionArray[i].requestFocus();
				} else {
					while (i != 0) {
						i = i - 1;
						if (jButtonMenuOptionArray[i].isVisible()) {
							jButtonMenuOptionArray[i].requestFocus();
							break;
						}
					}
				}
			}
		}
		//
		if (direction.equals("DOWN")) {
			while (i != 9 && i != 19) {
				i = i + 1;
				if (jButtonMenuOptionArray[i].isVisible()) {
					jButtonMenuOptionArray[i].requestFocus();
					break;
				}
			}
		}
		//
		if (direction.equals("UP")) {
			while (i != 0 && i != 10) {
				i = i - 1;
				if (jButtonMenuOptionArray[i].isVisible()) {
					jButtonMenuOptionArray[i].requestFocus();
					break;
				} else {
					if (index > 10 && i ==10) {
						i = i - 1;
						if (jButtonMenuOptionArray[i].isVisible()) {
							jButtonMenuOptionArray[i].requestFocus();
							break;
						}
					}
					
				}
			}
		}
	}

	void jTabbedPaneMenu_stateChanged(ChangeEvent e) {
		setupOptionsOfMenuWithTabNo(jTabbedPaneMenu.getSelectedIndex());
	}

	class Function extends Object {
		private XF000[] xF000 = new XF000[10];
		private XF010[] xF010 = new XF010[10];
		private XF100[] xF100 = new XF100[10];
		private XF110[] xF110 = new XF110[10];
		private XF200[] xF200 = new XF200[10];
		private XF210[] xF210 = new XF210[10];
		private XF290[] xF290 = new XF290[10];
		private XF300[] xF300 = new XF300[10];
		private XF310[] xF310 = new XF310[10];
		private XF390[] xF390 = new XF390[10];
		private Session session_ = null;
		//private StringBuffer processLog_ = null;
		//
		public Function(Session session) {
			xF000[0] = new XF000(session, 0);
			xF010[0] = new XF010(session, 0);
			xF100[0] = new XF100(session, 0);
			xF110[0] = new XF110(session, 0);
			xF200[0] = new XF200(session, 0);
			xF210[0] = new XF210(session, 0);
			xF290[0] = new XF290(session, 0);
			xF300[0] = new XF300(session, 0);
			xF310[0] = new XF310(session, 0);
			xF390[0] = new XF390(session, 0);
			session_ = session;
		}
		//
		public HashMap<String, Object> execute(org.w3c.dom.Element functionElement, HashMap<String, Object> parmMap) {
			HashMap<String, Object> returnMap = new HashMap<String, Object>();
			//
			if (functionElement == null) {
				JOptionPane.showMessageDialog(null, res.getString("SessionError15"));
			} else {
				for (int i = 0; i < 10; i++) {
					if (functionElement.getAttribute("Type").equals("XF000")) {
						if (xF000[i] == null) {
							xF000[i] = new XF000(session_, i);
							//processLog_ = xF000[i].getProcessLog();
							returnMap = xF000[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF000[i].isAvailable()) {
								//processLog_ = xF000[i].getProcessLog();
								returnMap = xF000[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
					if (functionElement.getAttribute("Type").equals("XF010")) {
						if (xF010[i] == null) {
							xF010[i] = new XF010(session_, i);
							//processLog_ = xF010[i].getProcessLog();
							returnMap = xF010[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF010[i].isAvailable()) {
								//processLog_ = xF010[i].getProcessLog();
								returnMap = xF010[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
					if (functionElement.getAttribute("Type").equals("XF100")) {
						if (xF100[i] == null) {
							xF100[i] = new XF100(session_, i);
							//processLog_ = xF100[i].getProcessLog();
							returnMap = xF100[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF100[i].isAvailable()) {
								//processLog_ = xF100[i].getProcessLog();
								returnMap = xF100[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
					if (functionElement.getAttribute("Type").equals("XF110")) {
						if (xF110[i] == null) {
							xF110[i] = new XF110(session_, i);
							//processLog_ = xF110[i].getProcessLog();
							returnMap = xF110[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF110[i].isAvailable()) {
								//processLog_ = xF110[i].getProcessLog();
								returnMap = xF110[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
					if (functionElement.getAttribute("Type").equals("XF200")) {
						if (xF200[i] == null) {
							xF200[i] = new XF200(session_, i);
							//processLog_ = xF200[i].getProcessLog();
							returnMap = xF200[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF200[i].isAvailable()) {
								//processLog_ = xF200[i].getProcessLog();
								returnMap = xF200[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
					if (functionElement.getAttribute("Type").equals("XF210")) {
						if (xF210[i] == null) {
							xF210[i] = new XF210(session_, i);
							//processLog_ = xF210[i].getProcessLog();
							returnMap = xF210[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF210[i].isAvailable()) {
								//processLog_ = xF210[i].getProcessLog();
								returnMap = xF210[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
					if (functionElement.getAttribute("Type").equals("XF290")) {
						if (xF290[i] == null) {
							xF290[i] = new XF290(session_, i);
							//processLog_ = xF290[i].getProcessLog();
							returnMap = xF290[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF290[i].isAvailable()) {
								//processLog_ = xF290[i].getProcessLog();
								returnMap = xF290[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
					if (functionElement.getAttribute("Type").equals("XF300")) {
						if (xF300[i] == null) {
							xF300[i] = new XF300(session_, i);
							//processLog_ = xF300[i].getProcessLog();
							returnMap = xF300[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF300[i].isAvailable()) {
								//processLog_ = xF300[i].getProcessLog();
								returnMap = xF300[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
					if (functionElement.getAttribute("Type").equals("XF310")) {
						if (xF310[i] == null) {
							xF310[i] = new XF310(session_, i);
							//processLog_ = xF310[i].getProcessLog();
							returnMap = xF310[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF310[i].isAvailable()) {
								//processLog_ = xF310[i].getProcessLog();
								returnMap = xF310[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
					if (functionElement.getAttribute("Type").equals("XF390")) {
						if (xF390[i] == null) {
							xF390[i] = new XF390(session_, i);
							//processLog_ = xF390[i].getProcessLog();
							returnMap = xF390[i].execute(functionElement, parmMap);
							break;
						} else {
							if (xF390[i].isAvailable()) {
								//processLog_ = xF390[i].getProcessLog();
								returnMap = xF390[i].execute(functionElement, parmMap);
								break;
							}
						}
					}
				}
			}
			//
			return returnMap;
		}

		//public void setProcessLog(String text) {
		//	XFUtility.appendLog(text, processLog_);
		//}
	}

	class MenuOption extends Object {
		private org.w3c.dom.Element functionElement_ = null;
		private String optionName_;
		private boolean isLogoutOption = false;
		//private HashMap<String, Object> parmMap_;
		//
		//public MenuOption(org.w3c.dom.Element functionElement, String optionName, String parms) {
		public MenuOption(org.w3c.dom.Element functionElement, String optionName) {
			//
			functionElement_ = functionElement;
			//
			if (optionName.equals("")) {
				if (functionElement_ != null) {
					optionName_ = functionElement_.getAttribute("Name");
				}
			} else {
				if (optionName.equals("LOGOUT")) {
					optionName_ = res.getString("LogOut");
					isLogoutOption = true;
				} else {
					optionName_ = optionName;
				}
			}
			//
//			String wrkStr;
//			int pos1, pos2;
//			StringTokenizer workTokenizer = new StringTokenizer(parms, "," );
//			while (workTokenizer.hasMoreTokens()) {
//				wrkStr = workTokenizer.nextToken();
//				pos1 = wrkStr.indexOf("(");
//				pos2 = wrkStr.indexOf(")");
//				parmMap_.put(wrkStr.substring(0, pos1), wrkStr.substring(pos1, pos2));
//			}
		}
		//
		public String getMenuOptionName() {
			return optionName_;
		}
		//
//		public HashMap<String, Object> getParamMap() {
//			  return parmMap_;
//		}
		//
		public boolean isLogoutOption() {
			return isLogoutOption;
		}
		//
		public HashMap<String, Object> call() {
			if (isLogoutOption) {
				return null;
			} else {
				//parmMap_ = new HashMap<String, Object>();
				//return function.execute(functionElement_, parmMap_);
				return function.execute(functionElement_, new HashMap<String, Object>());
			}
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
	
	public void executeProgram(String pgmName) {
		try {
			//setProcessLog("execute " + pgmName);
			//
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(pgmName);
			if (p != null) {
				String result = "";
				StringBuffer buf = new StringBuffer();
				buf.append(pgmName);
				buf.append(" was executed.\n");
				InputStream is = p.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				while ((result = br.readLine()) != null) {
					buf.append(result);
					buf.append("\n");
				}
				JOptionPane.showMessageDialog(null, buf.toString());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}	
	}
	
	public void browseFile(String fileName) {
			File file = new File(fileName);
			browseFile(file.toURI());
	}
	
	public void browseFile(URI uri) {
		try {
			desktop.browse(uri);
			//
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}	
	}
	
	public void editFile(String fileName) {
		try {
			//setProcessLog("edit " + fileName);
			//
			File file = new File(fileName);
			desktop.edit(file);
			//
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}	
	}
	
	public String getImageFileFolder() {
		return imageFileFolder;
	}
	
	public File createTempFile(String functionID, String extension) throws IOException {
		File tempFile = File.createTempFile("XeadDriver_" + functionID + "_", extension, outputFolder);
		//
		if (outputFolder == null) {
			tempFile.deleteOnExit();
			//setProcessLog("generate and delete " + tempFile.toURI().toString());
		} else {
			//setProcessLog("generate " + tempFile.toURI().toString());
		}
		//
		return tempFile;
	}

	DatabaseMetaData getDatabaseMetaData() {
		return databaseMetaData;
	}

	org.w3c.dom.Document getDomDocument() {
		return domDocument;
	}

	public Function getFunction() {
		return function;
	}

	//public void setProcessLog(String text) {
	//	function.setProcessLog(text);
	//}

	Desktop getDesktop() {
		return desktop;
	}

	DigestAdapter getDigestAdapter() {
		return digestAdapter;
	}
	
	String getSystemName() {
		return systemName;
	}

	String getTableNameOfUser() {
		return userTable;
	}

	String getTableNameOfVariants() {
		return variantsTable;
	}

	String getTableNameOfUserVariants() {
		return userVariantsTable;
	}

	String getTableNameOfCalendar() {
		return calendarTable;
	}

	String getMenuID() {
		return menuIDUsing;
	}

	int getNextSQPROGRAM() {
		sqProgram++;
		return sqProgram;
	}

	public String getDatabaseUser() {
		return databaseUser;
	}
	
	public void compressTable(String tableID) {
		StringBuffer statementBuf;
		org.w3c.dom.Element element;
		//
		try {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			//
			for (int i = 0; i < tableList.getLength(); i++) {
				//
				element = (org.w3c.dom.Element)tableList.item(i);
				if (element.getAttribute("ID").startsWith(tableID) || tableID.equals("")) {
					//
					statementBuf = new StringBuffer();
					statementBuf.append("call SYSCS_UTIL.SYSCS_COMPRESS_TABLE('");
					statementBuf.append(databaseUser);
					statementBuf.append("', '") ;
					statementBuf.append(element.getAttribute("ID"));
					statementBuf.append("', 1)") ;
					//
					Statement statement = connection.createStatement();
					//setProcessLog(statementBuf.toString());
					statement.executeUpdate(statementBuf.toString());
					connection.commit();
				}
			}
		} catch (SQLException e1) {
		} finally {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public String getSessionID() {
		return sessionID;
	}

	public String getUserID() {
		return userID;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserEmployeeNo() {
		return userEmployeeNo;
	}
	
	public String getAttribute(String id) {
		return attributeMap.get(id);
	}
	
	public void setAttribute(String id, String value) {
		attributeMap.put(id, value);
	}

	public NodeList getFunctionList() {
		return functionList;
	}

	public String getFunctionName(String functionID) {
		org.w3c.dom.Element workElement;
		String functionName = "";
		//
		for (int k = 0; k < functionList.getLength(); k++) {
			workElement = (org.w3c.dom.Element)functionList.item(k);
			if (workElement.getAttribute("ID").equals(functionID)) {
				functionName = workElement.getAttribute("Name");
				break;
			}
		}
		//
		return functionName;
	}
	
	org.w3c.dom.Element getTablePKElement(String tableID) {
		org.w3c.dom.Element element1, element2;
		org.w3c.dom.Element element3 = null;
		NodeList nodeList;
		for (int i = 0; i < tableList.getLength(); i++) {
			element1 = (org.w3c.dom.Element)tableList.item(i);
			if (element1.getAttribute("ID").equals(tableID)) {
				nodeList = element1.getElementsByTagName("Key");
				for (int j = 0; j < nodeList.getLength(); j++) {
					element2 = (org.w3c.dom.Element)nodeList.item(j);
					if (element2.getAttribute("Type").equals("PK")) {
						element3 = element2;
						break;
					}
				}
				break;
			}
		}
		return element3;
	}
	
	org.w3c.dom.Element getTableElement(String tableID) {
		org.w3c.dom.Element element1;
		org.w3c.dom.Element element2 = null;
		for (int i = 0; i < tableList.getLength(); i++) {
			element1 = (org.w3c.dom.Element)tableList.item(i);
			if (element1.getAttribute("ID").equals(tableID)) {
				element2 = element1;
				break;
			}
		}
		return element2;
	}
	
	String getTableName(String tableID) {
		String tableName = "";
		org.w3c.dom.Element element1;
		org.w3c.dom.Element element2 = null;
		for (int i = 0; i < tableList.getLength(); i++) {
			element1 = (org.w3c.dom.Element)tableList.item(i);
			if (element1.getAttribute("ID").equals(tableID)) {
				element2 = element1;
				tableName = element2.getAttribute("Name");
				break;
			}
		}
		return tableName;
	}
	
	public org.w3c.dom.Element getFieldElement(String tableID, String fieldID) {
		org.w3c.dom.Element element1, element2;
		org.w3c.dom.Element element3 = null;
		NodeList nodeList;
		for (int i = 0; i < tableList.getLength(); i++) {
			element1 = (org.w3c.dom.Element)tableList.item(i);
			if (element1.getAttribute("ID").equals(tableID)) {
				nodeList = element1.getElementsByTagName("Field");
				for (int j = 0; j < nodeList.getLength(); j++) {
					element2 = (org.w3c.dom.Element)nodeList.item(j);
					if (element2.getAttribute("ID").equals(fieldID)) {
						element3 = element2;
						break;
					}
				}
				break;
			}
		}
		return element3;
	}
}

class DigestAdapter {
    private MessageDigest digest_;

    public DigestAdapter(String algorithm) throws NoSuchAlgorithmException {
        digest_ = MessageDigest.getInstance(algorithm);
    }

    public synchronized String digest(String str) {
        return toHexString(digestArray(str));
    }

    public synchronized byte[] digestArray(String str) {
        byte[] hash = digest_.digest(str.getBytes());
        digest_.reset();
        return hash;
    }

    private String toHexString(byte[] arr) {
        StringBuffer buff = new StringBuffer(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String b = Integer.toHexString(arr[i] & 0xff);
            if (b.length() == 1) {
                buff.append("0");
            }
            buff.append(b);
        }
        return buff.toString();
    }
}

class Session_jButton_actionAdapter implements java.awt.event.ActionListener {
	  Session adaptee;
	  Session_jButton_actionAdapter(Session adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void actionPerformed(ActionEvent e) {
	    adaptee.jButtonMenu_actionPerformed(e);
	  }
}

class Session_jEditorPane_actionAdapter implements javax.swing.event.HyperlinkListener {
	  Session adaptee;
	  Session_jEditorPane_actionAdapter(Session adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void hyperlinkUpdate(HyperlinkEvent e) {
	    adaptee.jEditorPane_hyperlinkUpdate(e);
	  }
}

class Session_keyAdapter extends java.awt.event.KeyAdapter {
	  Session adaptee;
	  Session_keyAdapter(Session adaptee) {
	    this.adaptee = adaptee;
	  }
	  public void keyPressed(KeyEvent e) {
	    adaptee.menu_keyPressed(e);
	  }
}

class Session_jTabbedPaneMenu_changeAdapter  implements ChangeListener {
	Session adaptee;
	Session_jTabbedPaneMenu_changeAdapter(Session adaptee) {
		this.adaptee = adaptee;
	}
	public void stateChanged(ChangeEvent e) {
		adaptee.jTabbedPaneMenu_stateChanged(e);
	}
}
