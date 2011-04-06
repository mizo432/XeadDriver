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

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class XF000 extends JDialog implements XFExecutable, XFScriptable {
	private static final long serialVersionUID = 1L;
	private static ResourceBundle res = ResourceBundle.getBundle("xeadDriver.Res");
	private org.w3c.dom.Element functionElement_ = null;
	private HashMap<String, Object> parmMap_ = null;
	private HashMap<String, Object> returnMap_ = new HashMap<String, Object>();
	private Session session_ = null;
	private boolean instanceIsAvailable_ = true;
	private int programSequence;
	private int instanceArrayIndex_ = -1;
	private StringBuffer processLog = new StringBuffer();
	private ScriptEngine scriptEngine;
	private Bindings engineScriptBindings;
	private ByteArrayOutputStream exceptionLog;
	private PrintStream exceptionStream;
	private String exceptionHeader = "";
	//
	private JPanel jPanelMain = new JPanel();
	private Dimension scrSize;
	private JPanel jPanelTimer = new JPanel();
	private JPanel jPanelTop = new JPanel();
	private JPanel jPanelBottom = new JPanel();
	private JPanel jPanelButtons = new JPanel();
	private JPanel jPanelInfo = new JPanel();
	private JLabel jLabelTime = new JLabel();
	private SpinnerNumberModel spinnerNumberModelHour = new SpinnerNumberModel(00, 00, 24, 1);
	private SpinnerNumberModel spinnerNumberModelMinuite = new SpinnerNumberModel(00, 00, 55, 5);
	private JSpinner jSpinnerHour = new JSpinner(spinnerNumberModelHour);
	private JSpinner jSpinnerMinuite = new JSpinner(spinnerNumberModelMinuite);
	private JCheckBox jCheckBoxRepeat = new JCheckBox();
	private JCheckBox jCheckBoxRunOffDay = new JCheckBox();
	private JCheckBox jCheckBoxRunNow = new JCheckBox();
	private Calendar calendar;
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-HH:mm:ss");
	private Timer timer;
	private TimerTaskScript task = null;
	private boolean alreadyRun = false;
	private GridLayout gridLayoutInfo = new GridLayout();
	private JLabel jLabelFunctionID = new JLabel();
	private JLabel jLabelSessionID = new JLabel();
	private JScrollPane jScrollPaneMessages = new JScrollPane();
	private JTextArea jTextAreaMessages = new JTextArea();
	private JButton jButtonExit = new JButton();
	private JButton jButtonStart = new JButton();
	private JButton jButtonStop = new JButton();
	private JButton jButtonIconify = new JButton();
	private final int FONT_SIZE = 14;
	
	public XF000(Session session, int instanceArrayIndex) {
		super(session, "", true);
		try {
			//
			session_ = session;
			instanceArrayIndex_ = instanceArrayIndex;
			//
			initTimerPanel();
			//
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	void initTimerPanel() {
		scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		jPanelMain.setLayout(new BorderLayout());
		jPanelTop.setLayout(new BorderLayout());
		jPanelTimer.setLayout(null);
		jPanelTimer.setFocusable(false);
		jPanelTimer.setBorder(BorderFactory.createEtchedBorder());
		jLabelTime.setFont(new java.awt.Font("SansSerif", 0, 12));
		jLabelTime.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabelTime.setHorizontalTextPosition(SwingConstants.LEADING);
		jLabelTime.setText(res.getString("TimerTime"));
		jLabelTime.setBounds(new Rectangle(10, 12, 80, 15));
		jSpinnerHour.setFont(new java.awt.Font("SansSerif", 0, 14));
		jSpinnerHour.setBounds(new Rectangle(100, 9, 40, 18));
	    JSpinner.NumberEditor editorHour = new JSpinner.NumberEditor(jSpinnerHour, "00");
	    jSpinnerHour.setEditor(editorHour);
	    JFormattedTextField ftextHour = editorHour.getTextField();
	    ftextHour.setEditable(false);
		ftextHour.setBackground(Color.white);
		jSpinnerMinuite.setFont(new java.awt.Font("SansSerif", 0, 14));
		jSpinnerMinuite.setBounds(new Rectangle(140, 9, 40, 18));
	    JSpinner.NumberEditor editorMin = new JSpinner.NumberEditor(jSpinnerMinuite, "00");
	    jSpinnerMinuite.setEditor(editorMin);
	    JFormattedTextField ftextMin = editorMin.getTextField();
	    ftextMin.setEditable(false);
		ftextMin.setBackground(Color.white);
		jCheckBoxRepeat.setFont(new java.awt.Font("SansSerif", 0, 12));
		jCheckBoxRepeat.setText(res.getString("TimerRepeat"));
		jCheckBoxRepeat.setBounds(new Rectangle(195, 10, 150, 15));
		jCheckBoxRunOffDay.setFont(new java.awt.Font("SansSerif", 0, 12));
		jCheckBoxRunOffDay.setText(res.getString("TimerRunOffDay"));
		jCheckBoxRunOffDay.setBounds(new Rectangle(350, 10, 150, 15));
		jCheckBoxRunNow.setFont(new java.awt.Font("SansSerif", 0, 12));
		jCheckBoxRunNow.setText(res.getString("TimerRunNow"));
		jCheckBoxRunNow.setBounds(new Rectangle(505, 10, 150, 15));
		jCheckBoxRunNow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRunNow();
			}
		});
		jPanelTimer.add(jLabelTime);
		jPanelTimer.add(jSpinnerHour);
		jPanelTimer.add(jSpinnerMinuite);
		jPanelTimer.add(jCheckBoxRepeat);
		jPanelTimer.add(jCheckBoxRunOffDay);
		jPanelTimer.add(jCheckBoxRunNow);
		jTextAreaMessages.setEditable(false);
		jTextAreaMessages.setBorder(BorderFactory.createEtchedBorder());
		jTextAreaMessages.setFont(new java.awt.Font("SansSerif", 0, FONT_SIZE));
		jTextAreaMessages.setFocusable(false);
		jTextAreaMessages.setLineWrap(true);
		jTextAreaMessages.setOpaque(false);
		jScrollPaneMessages.getViewport().add(jTextAreaMessages, null);
		jScrollPaneMessages.setPreferredSize(new Dimension(10, 295));
		jPanelBottom.setPreferredSize(new Dimension(10, 35));
		jPanelBottom.setLayout(new BorderLayout());
		jPanelBottom.setBorder(null);
		jLabelFunctionID.setFont(new java.awt.Font("Dialog", 0, FONT_SIZE));
		jLabelFunctionID.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabelFunctionID.setForeground(Color.gray);
		jLabelFunctionID.setFocusable(false);
		jLabelSessionID.setFont(new java.awt.Font("Dialog", 0, FONT_SIZE));
		jLabelSessionID.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabelSessionID.setForeground(Color.gray);
		jLabelSessionID.setFocusable(false);
		jPanelButtons.setBorder(null);
		jPanelButtons.setLayout(null);
		jPanelButtons.setFocusable(false);
		gridLayoutInfo.setColumns(1);
		gridLayoutInfo.setRows(2);
		gridLayoutInfo.setVgap(4);
		jPanelInfo.setLayout(gridLayoutInfo);
		jPanelInfo.add(jLabelSessionID);
		jPanelInfo.add(jLabelFunctionID);
		jPanelInfo.setFocusable(false);
		jPanelMain.add(jPanelTop, BorderLayout.CENTER);
		jPanelMain.add(jPanelBottom, BorderLayout.SOUTH);
		jPanelTop.add(jPanelTimer, BorderLayout.CENTER);
		jPanelTop.add(jScrollPaneMessages, BorderLayout.SOUTH);
		jPanelBottom.add(jPanelInfo, BorderLayout.EAST);
		jPanelBottom.add(jPanelButtons, BorderLayout.CENTER);
		jButtonExit.setFont(new java.awt.Font("Dialog", 0, FONT_SIZE));
		jButtonExit.setText(res.getString("ExitButton"));
		jButtonExit.setBounds(new Rectangle(5, 2, 100, 32));
		jButtonExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		jButtonStart.setFont(new java.awt.Font("Dialog", 0, FONT_SIZE));
		jButtonStart.setText(res.getString("TimerStart"));
		jButtonStart.setBounds(new Rectangle(150, 2, 100, 32));
		jButtonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startTimer();
			}
		});
		jButtonStop.setFont(new java.awt.Font("Dialog", 0, FONT_SIZE));
		jButtonStop.setText(res.getString("TimerStop"));
		jButtonStop.setBounds(new Rectangle(295, 2, 100, 32));
		jButtonStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTimer();
			}
		});
		jButtonIconify.setFont(new java.awt.Font("Dialog", 0, FONT_SIZE));
		jButtonIconify.setText(res.getString("Iconify"));
		jButtonIconify.setBounds(new Rectangle(440, 2, 100, 32));
		jButtonIconify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				session_.setExtendedState(JFrame.ICONIFIED | session_.getExtendedState());
			}
		});
		jPanelButtons.add(jButtonExit);
		jPanelButtons.add(jButtonStart);
		jPanelButtons.add(jButtonStop);
		jPanelButtons.add(jButtonIconify);
		this.getContentPane().add(jPanelMain, BorderLayout.CENTER);
		this.setSize(new Dimension(scrSize.width, scrSize.height));
		//
		int width = 665;
		int height = 400;
		this.setPreferredSize(new Dimension(width, height));
		int posX = (scrSize.width - width) / 2;
		int posY = (scrSize.height - height) / 2;
		this.setLocation(posX, posY);
		this.pack();
	}

	public boolean isAvailable() {
		return instanceIsAvailable_;
	}

	public HashMap<String, Object> execute(org.w3c.dom.Element functionElement, HashMap<String, Object> parmMap) {
		try {

			///////////////////
			// Process parms //
			///////////////////
			parmMap_ = parmMap;
			if (parmMap_ == null) {
				parmMap_ = new HashMap<String, Object>();
			}
			returnMap_.clear();
			returnMap_.putAll(parmMap_);
			returnMap_.put("RETURN_CODE", "00");

			/////////////////////////
			// Initialize variants //
			/////////////////////////
			instanceIsAvailable_ = false;
			exceptionLog = new ByteArrayOutputStream();
			exceptionStream = new PrintStream(exceptionLog);
			exceptionHeader = "";
			functionElement_ = functionElement;
			processLog.delete(0, processLog.length());
			programSequence = session_.writeLogOfFunctionStarted(functionElement_.getAttribute("ID"), functionElement_.getAttribute("Name"));
			scriptEngine = session_.getScriptEngineManager().getEngineByName("js");
			engineScriptBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
			
			////////////////////////////////////
			// Run Script or Show Timer Panel //
			////////////////////////////////////
			if (functionElement_.getAttribute("TimerOption").equals("")) {
				runScript();
				closeFunction();
			} else {
				setupTimerPanel();
				this.setVisible(true);
			}

		} catch(ScriptException e) {
			JOptionPane.showMessageDialog(null, res.getString("FunctionError12"));
			exceptionHeader = "'Script error in the function'\n";
			e.printStackTrace(exceptionStream);
			setErrorAndCloseFunction();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, res.getString("FunctionError5"));
			e.printStackTrace(exceptionStream);
			setErrorAndCloseFunction();
		} finally {
			instanceIsAvailable_ = true;
		}

		return returnMap_;
	}
	
	void setupTimerPanel() {
		StringTokenizer workTokenizer;
		int hour = 0;
		int minuite = 0;
		//
		this.setTitle(functionElement_.getAttribute("Name"));
		jLabelSessionID.setText(session_.getSessionID());
		jLabelFunctionID.setText("000" + "-" + instanceArrayIndex_ + "-" + functionElement_.getAttribute("ID"));
		FontMetrics metrics = jLabelFunctionID.getFontMetrics(new java.awt.Font("Dialog", 0, FONT_SIZE));
		jPanelInfo.setPreferredSize(new Dimension(metrics.stringWidth(jLabelFunctionID.getText()), 35));
		this.getRootPane().setDefaultButton(jButtonStart);
		//
		if (functionElement_.getAttribute("TimerOption").contains(":")) {
			workTokenizer = new StringTokenizer(functionElement_.getAttribute("TimerOption"), ":" );
			try {
				hour = Integer.parseInt(workTokenizer.nextToken());
				minuite = Integer.parseInt(workTokenizer.nextToken());
			} catch (NumberFormatException e) {
			}
		}
		jSpinnerHour.setValue(hour);
		jSpinnerMinuite.setValue(minuite);
		//
		jCheckBoxRepeat.setSelected(true);
		jCheckBoxRunOffDay.setSelected(false);
		jCheckBoxRunNow.setSelected(false);
		//
		if (functionElement_.getAttribute("TimerMessage").equals("")) {
			jTextAreaMessages.setText("> " + res.getString("FunctionMessage44") + "\n");
		} else {
			jTextAreaMessages.setText(functionElement_.getAttribute("TimerMessage") + "\n");
		}
		//
		jButtonStart.setEnabled(true);
		jButtonStop.setEnabled(false);
		jButtonIconify.setEnabled(false);
		timer = null;
	}

	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			stopTimer();
			closeFunction();
		}
	}

	void setErrorAndCloseFunction() {
		returnMap_.put("RETURN_CODE", "99");
		closeFunction();
	}

	void closeFunction() {
		instanceIsAvailable_ = true;
		//
		String wrkStr;
		if (exceptionLog.size() > 0 || !exceptionHeader.equals("")) {
			wrkStr = processLog.toString() + "\nERROR LOG:\n" + exceptionHeader + exceptionLog.toString();
		} else {
			wrkStr = processLog.toString();
		}
		if (!functionElement_.getAttribute("TimerOption").equals("")) {
			wrkStr = wrkStr + "\n\n<Timer Console Log>\n" + jTextAreaMessages.getText();
		}
		wrkStr = wrkStr.replace("'", "\"");
		session_.writeLogOfFunctionClosed(programSequence, returnMap_.get("RETURN_CODE").toString(), wrkStr);
	}
	
	public void cancelWithMessage(String message) {
		if (!message.equals("")) {
			JOptionPane.showMessageDialog(null, message);
			if (this.isVisible()) {
				jTextAreaMessages.setText(getNewMessage(message, ""));
			}
		}
		if (returnMap_.get("RETURN_CODE").equals("00")) {
			returnMap_.put("RETURN_CODE", "01");
		}
	}

	public void callFunction(String functionID) {
		try {
			//
			if (this.isVisible()) {
				jTextAreaMessages.setText(getNewMessage(session_.getFunctionName(functionID) + "(" + functionID + ")", res.getString("FunctionMessage38")));
				jScrollPaneMessages.validate();
				jScrollPaneMessages.paintImmediately(0,0,jScrollPaneMessages.getWidth(),jScrollPaneMessages.getHeight());
			}
			//
			returnMap_ = XFUtility.callFunction(session_, functionID, parmMap_);
			//
		} catch (Exception e) {
			String message = res.getString("FunctionError9") + functionID + res.getString("FunctionError10");
			JOptionPane.showMessageDialog(null,message);
			if (this.isVisible()) {
				jTextAreaMessages.setText(getNewMessage(message, ""));
			}
			exceptionHeader = message;
			setErrorAndCloseFunction();
		}
	}

	public String getFunctionID() {
		return functionElement_.getAttribute("ID");
	}

	public HashMap<String, Object> getParmMap() {
		return parmMap_;
	}

	public void setProcessLog(String text) {
		XFUtility.appendLog(text, processLog);
	}

	public HashMap<String, Object> getReturnMap() {
		return returnMap_;
	}

	//public StringBuffer getProcessLog() {
	//	return processLog;
	//}

	public Session getSession() {
		return session_;
	}

	public PrintStream getExceptionStream() {
		return exceptionStream;
	}

	public void runScript() throws ScriptException {
		engineScriptBindings.clear();
		engineScriptBindings.put("instance", (XFScriptable)this);
		//
		String scriptText = XFUtility.substringLinesWithTokenOfEOL(functionElement_.getAttribute("Script"), "\n");
		if (!scriptText.equals("")) {
			scriptEngine.eval(scriptText);
		}
	}

	private void runNow() {
		try {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			jTextAreaMessages.setText(getNewMessage(res.getString("FunctionMessage47"), ""));
			runScript();
			jTextAreaMessages.setText(getNewMessage(res.getString("FunctionMessage48"), "") + "\n");
			//
		} catch(ScriptException e) {
			JOptionPane.showMessageDialog(null, res.getString("FunctionError12"));
			exceptionHeader = "'Script error in the function'\n";
			e.printStackTrace(exceptionStream);
			jTextAreaMessages.setText(getNewMessage(exceptionHeader, res.getString("FunctionMessage46")));
			jButtonStart.setEnabled(true);
			jButtonStop.setEnabled(false);
			jButtonExit.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace(exceptionStream);
			jTextAreaMessages.setText(getNewMessage(res.getString("FunctionError5"), res.getString("FunctionMessage46")));
			jButtonStart.setEnabled(true);
			jButtonStop.setEnabled(false);
			jButtonExit.setEnabled(true);
		} finally {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void startTimer() {
		if (jCheckBoxRunNow.isSelected()) {
			runNow();
		} else {
			//
			jLabelTime.setEnabled(false);
			jSpinnerHour.setEnabled(false);
			jSpinnerMinuite.setEnabled(false);
			jCheckBoxRepeat.setEnabled(false);
			jCheckBoxRunOffDay.setEnabled(false);
			//
			jButtonStart.setEnabled(false);
			jButtonStop.setEnabled(true);
			jButtonIconify.setEnabled(true);
			this.getRootPane().setDefaultButton(jButtonStop);
			//
			Calendar date = getNextDateToRun();
			if (date != null) {
				alreadyRun = false;
				timer = new Timer(true);
				task = new TimerTaskScript();
				timer.schedule(task, date.getTime());
				jTextAreaMessages.setText(getNewMessage(formatter.format(date.getTime()), res.getString("FunctionMessage45") + "\n"));
				jScrollPaneMessages.validate();
				jScrollPaneMessages.paintImmediately(0,0,jScrollPaneMessages.getWidth(),jScrollPaneMessages.getHeight());
			}
		}
	}

	private void stopTimer() {
		jLabelTime.setEnabled(true);
		jSpinnerHour.setEnabled(true);
		jSpinnerMinuite.setEnabled(true);
		jCheckBoxRepeat.setEnabled(true);
		jCheckBoxRunOffDay.setEnabled(true);
		//
		//jButtonExit.setEnabled(true);
		jButtonStart.setEnabled(true);
		jButtonStop.setEnabled(false);
		jButtonIconify.setEnabled(false);
		this.getRootPane().setDefaultButton(jButtonStart);
		//
		if (timer != null) {
			timer.cancel();
			task = null;
		}
		//
		jTextAreaMessages.setText(getNewMessage(res.getString("FunctionMessage46"), ""));
	}
	
	private Calendar getNextDateToRun() {
		int diff;
		String wrkStr;
		Calendar date = null;
		java.util.Date wrkDate;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//
		if (jCheckBoxRepeat.isSelected() || !alreadyRun) {
			//
			Calendar currentDateTime = Calendar.getInstance();
			Calendar targetDateTime = Calendar.getInstance();
			targetDateTime.set(Calendar.HOUR_OF_DAY, (Integer)jSpinnerHour.getValue());
			targetDateTime.set(Calendar.MINUTE, (Integer)jSpinnerMinuite.getValue());
			targetDateTime.set(Calendar.SECOND, 0);
			targetDateTime.set(Calendar.MILLISECOND, 0);
			diff = targetDateTime.compareTo(currentDateTime);
			//
			if (diff <= 0) {
				targetDateTime.add(Calendar.DAY_OF_MONTH, 1);
			}
			//
			if (!jCheckBoxRunOffDay.isSelected()) {
				wrkDate = targetDateTime.getTime();
				wrkStr = df.format(wrkDate);
				while (session_.isOffDate(wrkStr)) {
					targetDateTime.add(Calendar.DAY_OF_MONTH, 1);
					wrkDate = targetDateTime.getTime();
					wrkStr = df.format(wrkDate);
				}
			}
			date = targetDateTime;
			//
			alreadyRun = true;
		}
		//
		return date;
	}

	class TimerTaskScript extends TimerTask {
		public void run() {
			try {
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				//
				jTextAreaMessages.setText(getNewMessage(res.getString("FunctionMessage47"), ""));
				//
				runScript();
				//
				jTextAreaMessages.setText(getNewMessage(res.getString("FunctionMessage48"), ""));
				//
				if (jCheckBoxRepeat.isSelected()) {
					startTimer();
				} else {
					stopTimer();
				}
				//
			} catch(ScriptException e) {
				JOptionPane.showMessageDialog(null, res.getString("FunctionError12"));
				exceptionHeader = "'Script error in the function'\n";
				e.printStackTrace(exceptionStream);
				jTextAreaMessages.setText(getNewMessage(exceptionHeader, res.getString("FunctionMessage46")));
				jButtonStart.setEnabled(true);
				jButtonStop.setEnabled(false);
				jButtonExit.setEnabled(true);
			} catch (Exception e) {
				e.printStackTrace(exceptionStream);
				jTextAreaMessages.setText(getNewMessage(res.getString("FunctionError5"), res.getString("FunctionMessage46")));
				jButtonStart.setEnabled(true);
				jButtonStop.setEnabled(false);
				jButtonExit.setEnabled(true);
			} finally {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	
	private void setRunNow() {
		if (jCheckBoxRunNow.isSelected()) {
			jSpinnerHour.setEnabled(false);
			jSpinnerMinuite.setEnabled(false);
			jCheckBoxRepeat.setEnabled(false);
			jCheckBoxRunOffDay.setEnabled(false);
		} else {
			jSpinnerHour.setEnabled(true);
			jSpinnerMinuite.setEnabled(true);
			jCheckBoxRepeat.setEnabled(true);
			jCheckBoxRunOffDay.setEnabled(true);
		}
	}
	
	private String getNewMessage(String newMessage1, String newMessage2) {
		StringBuffer bf = new StringBuffer();
		//
		if (!jTextAreaMessages.getText().equals("")) {
			bf.append(jTextAreaMessages.getText());
			bf.append("\n");
		}
		//
		bf.append("> ");
		bf.append(newMessage1);
		if (!newMessage2.equals("")) {
			bf.append(" ");
			bf.append(newMessage2);
		}
		//
		calendar = Calendar.getInstance();
		bf.append("(");
		bf.append(formatter.format(calendar.getTime()));
		bf.append(")");
		//
		return bf.toString();
	}
}