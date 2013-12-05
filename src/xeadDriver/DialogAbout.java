package xeadDriver;

/*
 * Copyright (c) 2012 WATANABE kozo <qyf05466@nifty.com>,
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
import java.net.URI;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

public class DialogAbout extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	/**
	 * Application Information
	 */
	public static final String PRODUCT_NAME = "XEAD[zi:d] Driver";
	public static final String FULL_VERSION  = "V1.R1.M27";
	//27
	//�E�X�N���v�g�Ői���o�[�𐧌䂷�邽�߂̊֐�session.startProgress(...)��ǉ�����
	//�E���j���[�����u�v�����[�h�w��@�\�v�ɑΉ�����
	//�EXF310�̃L�[���̓_�C�A���O�̃N���[�Y����̃o�O���C��
	//�E�e�[�u���X�N���v�g�́u�ꎞ�ۗ��v�ɑΉ�����
	//�E�摜�t�@�C���t�B�[���h�ɂ��ăt�@�C�������݂��Ȃ��ꍇ�̕\�������P����
	//��摜�t�@�C���t�B�[���h���X�V�Ώۂ̖��׃t�B�[���h�Ƃ��Ēu�����ꍇ�̕\�������P����
	//�E�e��ǉ������ɂ����āA�����̔ԃt�B�[���h�������t�B�[���h�Ƃ��Ċ܂܂�Ă���ƍ̔ԃ��R�[�h�����Ӗ��ɃJ�E���g�A�b�v�����o�O���C��
	//��@�\���œ����select�������s���ꂽ�ꍇ�ɂ̓e�[�u�����샍�O�ɋL�^���Ȃ��悤�ɂ����i���O�̃T�C�Y�����炷���߁j
	//�XF110,310�̖��׍s��ɃJ�[�\��������Ƃ��ɍX�V�{�^���������������悤�ɂ����i���̂܂܍X�V����ƍŉ��s�̒l���������[��Ɏc���Ă��邱�Ƃ����邽�߁j
	//�VARCHAR�����̃e�L�X�g�h���͈�Ƀt�H�[�J�X������ꍇ�AEnter�L�[��Tab�L�[�ɂ��Ă����Ă������A�G���[�`�F�b�N�̃L�[�ɕύX����
	//
	//26
	//�EInputDialog�Ƀ_�C�A���O�̕����w�肷�邽�߂̃��\�b�hsetWidth(...)��g�ݍ���
	//�EInputDialog�̃t�B�[���h��setValue(...)���ꂽ�ꍇ�ɁA�l���t�B�[���h�T�C�Y�ɍ��킹��悤�ɂ���
	//�E�摜���t�B�[���h�̎w��T�C�Y�ɍ��킹�ďk���^�g�傷��悤�ɂ���ƂƂ��ɁA�摜���N���b�N����΃I���W�i���T�C�Y�ŕ\�������悤�ɂ���
	//�EXF390�̖��׍��ڂ����l�̏ꍇ�Ɉُ�I�����邱�Ƃ���������C������
	//�E�N���X�`�F�b�N�̑Ώۃe�[�u����ÓI�������ꂽ���̂Ɍ���悤�ɂ���
	//�E�N���X�`�F�b�N�̏��O�w��ɑΉ�����
	//�E�����L�[�������t�B�[���h�̏ꍇ�ɃN���X�`�F�b�N������ɍ쓮���Ȃ������C��
	//�EXF110�̑S�I��p�`�F�b�N�{�b�N�X���`�F�b�N����ƉE�ׂ̍��ڂł̕��ёւ����N�����Ă����o�O���C��
	//
	//25
	//�E���t�t�B�[���h�����p����J�����_�[�R���|�[�l���g���Z�b�V�������L�I�u�W�F�N�g�ɂ���
	//�E�J�����_�[�R���|�[�l���g�̕\���ʒu�̐��䃍�W�b�N�����P����
	//�E�J�����_�[�R���|�[�l���g���J�����_�[�敪���󂯎���悤�ɂ����i���t�t�B�[���h�̏C���ɂ��Ă͖����j
	//
	//24
	//�EReferChecker��FK�Ɋ܂܂�鐔���t�B�[���h��NULL�ł������ꍇ�ɑΉ�
	//�E�J�����_�[�R���|�[�l���g�ŁA�x���e�[�u���ƃ��[�U��`�敪�́u�J�����_�[�敪�v����������悤�ɂ���
	//�E���t�����n�̂R�̃Z�b�V�����֐��ŁA�x���e�[�u���́u�J�����_�[�敪�v����������悤�ɂ���
	//
	//23
	//�E�}�C�i�X�s�̐��l�t�B�[���h�Ń}�C�i�X���͂��\�ɂȂ邱�Ƃ�����o�O���C��
	//�EXF100,110,300�̈ꗗ���ɃG�C���A�X�w�肳�ꂽ�t�B�[���h���܂߂�ƈُ�I������o�O���C��
	//�EXF310�ŃJ�����T�C�Y��ύX���Ă��A�ҏW�I���s�̃J�����T�C�Y���ω����Ȃ��o�O���C��
	//�E�N���X�`�F�b�J�[�̃��O�C�������[�h�̎d�l��g�ݍ���
	//�E�@�\�̏I�����ɃN���X�`�F�b�J�[�̐����X���b�h���L�����Z������悤�ɂ���
	//�EXF100,110,300�̌��������̃v�����v�g�I�v�V�����u��⃊�X�g�v�ɑΉ�����
	//�E�����e�[�u���́u�Œ�Where�v�ɃZ�b�V���������̑��Ƀ��[�U�������w��ł���悤�ɂ���
	//�EXF390�ŁA�w��ɂ���č��v�l�⍇�v�s�̐ݒ肪���������Ȃ�o�O���C��
	//�EXF000�ŁA���s������LIST����REPEAT�����w��ł���悤�ɂ���
	//
	//22
	//�E���j���[�^�u�̑I�����ɉ����ɕ\������郁�b�Z�[�W���A���쉇���pURL���w�肳��Ă��邩�ǂ����Ő؂�ւ���悤�ɂ���
	//�EXFTextField�̃^�C�v��DATETIME�̏ꍇ�̃t�B�[���h�����L����
	//�EXF310�ł̃u�����N�s�ǉ��̃��W�b�N�����P����
	//�EXF300���疾�׍s��XF200�ŃR�s�[������ňꗗ�����t���b�V������悤�ɂ���
	//�ECheckListDialog�Ŗ߂�{�^�����g���ƃ��X�g���N���A����Ȃ��o�O���C��
	//
	//21
	//�EInputDialog��addField���\�b�h�ɂ����ĕ\����̃f�t�H���g��0�i�㕔�z�u�j�Ƃ���
	//�E�u�Œ�Where�v�ɃZ�b�V����������g�ݍ��߂�悤�ɂ���
	//�ESession�֐�setNextNumber(id, nextNumber)��g�ݍ���
	//�ESession�֐�isValidTime(time, format)��g�ݍ���
	//�ESession�֐�getMinutesBetweenTimes(timeFrom, timeThru)��g�ݍ���
	//�ESession�֐�getOffsetDateTime(date, time, minutes, countType)��g�ݍ���
	//�ESession�֐�getOffsetYearMonth(yearMonth, months)��g�ݍ���
	//�ESession�֐�setSystemVariant(id, value)��g�ݍ���
	//�EURL�^�C�v�̃t�B�[���h�Ƀ��[�J���t�@�C�������w��ł���悤�ɂ���
	//�EXF200��"EDIT"��INSTANCE_MODE��n���ΕҏW���[�h�ŋN�������悤�ɂ���
	//�EXF200,310�ŁA�v�����v�^�֐�����󂯎��t�B�[���h���܂܂�Ȃ��ꍇ�ɔ�\���t�B�[���h�Ƃ��đg�ݍ��ނ悤�ɂ���
	//�EXF310�̍s�ǉ����X�g�ɖ��׍��ڂ��w�肳��Ă��Ȃ��ꍇ�ɂ̓��b�Z�[�W�o�͂��ďI������悤�ɂ���
	//�EXF310�̍s�ǉ����X�g��Where�������w�肳��Ă��Ȃ��P�[�X�ɑΉ�����
	//�EXF310�Œǉ����ꂽ����ɂ̓G���[�\�����Ȃ��悤�ɂ���
	//�EXF100,110,300�ɂ��āA�����\���I�v�V�����ɑΉ�����
	//�EXF100����@�\���N��������̏I���R�[�h�̒l�ɂ��������Ė��׈ꗗ���X�V����悤�ɂ���
	//�EXF100,110,300,310�ɂ��āA�Z���̔z�F�ݒ�����P����
	//�ETableOperator�ŃV���O���N�H�[�e�[�V�������܂ރf�[�^��������悤�ɂ���
	//�ETableOperator�œ��t����=''��addKeyValue�����ꂽ�ꍇ�ɁA������'is NULL'�ɒu��������悤�ɂ���(!=''�̏ꍇ�ɂ�'is not NULL') 
	//�ETableOperator�œ��t����''��addValue�����ꂽ�ꍇ�ɁA������'NULL'�ɒu��������悤�ɂ���
	//�E���������t�B�[���h�ɕ�����OR������ݒ肷�邽�߂ɁAsession.getCheckListDialog()��ǉ�����
	//�ESession�̎����̔ԏ����̃��W�b�N�����P����
	//�EXF110,200,310�ŁA���X�g�{�b�N�X���v�����v�^���ݒ肳��Ă���t�B�[���h�ɂ��āA�����ɂ���Ēl�ݒ肳���t�B�[���h�̂����̈ꕔ���ҏW�s�ł���Ζ����ɂ��Ă������A�������߂��B�ҏW�s�ł����Ă��l���X�V�������P�[�X�����邽�߁B
	//���C�����ꂽ��聄
	//�ESession�̐Ŋz�v�Z�֐��̌������W�b�N�̃o�O���C������
	//�EXF100,110,300�ŁA�J�����ʂ̃\�[�g���w�肵���ꍇ�AEXCEL�o�͂�����ƌ��o����<u>���t������Ă��܂��o�O���C������
	//�E�����t�B�[���h�ɑ΂��Ď����̔Ԃ�ݒ肷���*AUTO���\������Ȃ��o�O���C������
	//�EVARCHAR���ڂ��P�s�\���ɂ����ꍇ�A�X�N���[���o�[���펞�\������Ă����o�O���C������
	//�EXF100,110,200,300,310�ɂ��āA�����t�B�[���h�Ƀv�����v�g�ݒ肵���ۂ̓����Ɋւ���o�O���C������
	//�EXF110,310�ɂ��āA���׍s���̕ҏW�^�s�ݒ肪���������f����Ă��Ȃ������o�O���C������
	//�EXF290,390�ŗ�O���b�Z�[�W���_�C�A���O�\������X�e�b�v�ׂ̍����o�O���C������
	//�EXF310�Łu�l���X�g�t�B�[���h�v�Ɋւ��鈵�������������Ă����o�O���C������
	//�EXF310��A�����s�����ꍇ�ADividerLocation�̈ʒu���Đݒ肳��Ȃ����Ƃ̂���o�O���C������
	//�EXF310�Ō��o����ɓ��͉\���ڂ����݂��Ȃ��ꍇ�A���׍s�̃G���[���ڂɃt�H�[�J�X��������Ȃ��o�O���C��
	//�EXF310�ŐV�K�ǉ����ꂽ���׍s�ɂ��Ẵ��j�[�N����`�F�b�N�Ɋւ���o�O���C������
	//�EXF110�ňꎟ�e�[�u���́u�����e�[�u���ǂݍ��ݑO�E�X�V�O�X�N���v�g�v�̎��s�X�e�b�v�������Ă����o�O���C��
	//�EXF110�Ńo�b�`�e�[�u�������@�\���u�����N�ł����s����Ă��܂��o�O���C��
	//�EXF110�Ō����������[���ɂ���ƕ\�������������Ȃ�o�O���C��
	//�EXF110�Ō��o����̃��X�g�{�b�N�X�ɂ��āA�I�������l���֘A����o�b�`�t�B�[���h�ɔ��f����Ȃ��o�O���C��
	//�EXF100,110�ŔN���^�t�B�[���h�Ō����������w�肷��ƈُ�I������o�O���C������
	public static final String FORMAT_VERSION  = "1.1";
	public static final String COPYRIGHT = "Copyright 2013 DBC,Ltd.";
	public static final String URL_DBC = "http://homepage2.nifty.com/dbc/";
	/**
	 * Components on dialog
	 */
	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	private JPanel insetsPanel1 = new JPanel();
	private JPanel insetsPanel2 = new JPanel();
	private JPanel insetsPanel3 = new JPanel();
	private JButton buttonOK = new JButton();
	private JLabel imageLabel = new JLabel();
	private JLabel labelName = new JLabel();
	private JLabel labelVersion = new JLabel();
	private JLabel labelCopyright = new JLabel();
	private JLabel labelURL = new JLabel();
	private ImageIcon imageXead = new ImageIcon();
	private HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
	private Desktop desktop = Desktop.getDesktop();
	private JDialog parent_;

	public DialogAbout(JDialog parent) {
		super(parent);
		parent_ = parent;
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception  {
	 	imageXead = new ImageIcon(Toolkit.getDefaultToolkit().createImage(xeadDriver.Session.class.getResource("title.png")));
		imageLabel.setIcon(imageXead);
		panel1.setLayout(new BorderLayout());
		panel1.setBorder(BorderFactory.createEtchedBorder());
		panel2.setLayout(new BorderLayout());
		insetsPanel2.setLayout(new BorderLayout());
		insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		insetsPanel2.setPreferredSize(new Dimension(75, 52));
		insetsPanel2.add(imageLabel, BorderLayout.EAST);
		//
		labelName.setFont(new java.awt.Font("Serif", 1, 16));
		labelName.setHorizontalAlignment(SwingConstants.CENTER);
		labelName.setText(PRODUCT_NAME);
		labelName.setBounds(new Rectangle(-5, 9, 190, 18));
		labelVersion.setFont(new java.awt.Font("Dialog", 0, 12));
		labelVersion.setHorizontalAlignment(SwingConstants.CENTER);
		labelVersion.setText(FULL_VERSION);
		labelVersion.setBounds(new Rectangle(-5, 32, 190, 15));
		labelCopyright.setFont(new java.awt.Font("Dialog", 0, 12));
		labelCopyright.setHorizontalAlignment(SwingConstants.CENTER);
		labelCopyright.setText(COPYRIGHT);
		labelCopyright.setBounds(new Rectangle(-5, 53, 190, 15));
		labelURL.setFont(new java.awt.Font("Dialog", 0, 12));
		labelURL.setHorizontalAlignment(SwingConstants.CENTER);
		labelURL.setText("<html><u><font color='blue'>" + URL_DBC);
		labelURL.setBounds(new Rectangle(-5, 73, 190, 15));
		labelURL.addMouseListener(new About_labelURL_mouseAdapter(this));
		insetsPanel3.setLayout(null);
		insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
		insetsPanel3.setPreferredSize(new Dimension(190, 80));
		insetsPanel3.add(labelName, null);
		insetsPanel3.add(labelVersion, null);
		insetsPanel3.add(labelCopyright, null);
		insetsPanel3.add(labelURL, null);
		//
		buttonOK.setText("OK");
		buttonOK.addActionListener(this);
		insetsPanel1.add(buttonOK, null);
		//
		panel1.add(insetsPanel1, BorderLayout.SOUTH);
		panel1.add(panel2, BorderLayout.NORTH);
		panel2.setPreferredSize(new Dimension(270, 90));
		panel2.add(insetsPanel2, BorderLayout.CENTER);
		panel2.add(insetsPanel3, BorderLayout.EAST);
		//
		this.setTitle("About XEAD Driver");
		this.getContentPane().add(panel1, null);
		this.setResizable(false);
	}

	public void request() {
		insetsPanel1.getRootPane().setDefaultButton(buttonOK);
		Dimension dlgSize = this.getPreferredSize();
		Dimension frmSize = parent_.getSize();
		Point loc = parent_.getLocation();
		this.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x + 30, (frmSize.height - dlgSize.height) / 2 + loc.y + 30);
		this.pack();
		super.setVisible(true);
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			cancel();
		}
		super.processWindowEvent(e);
	}

	void cancel() {
		dispose();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonOK) {
			cancel();
		}
	}

	void labelURL_mouseClicked(MouseEvent e) {
		try {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			desktop.browse(new URI(URL_DBC));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "The Site is inaccessible.");
		} finally {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	void labelURL_mouseEntered(MouseEvent e) {
		setCursor(htmlEditorKit.getLinkCursor());
	}

	void labelURL_mouseExited(MouseEvent e) {
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
}

class About_labelURL_mouseAdapter extends java.awt.event.MouseAdapter {
	DialogAbout adaptee;
	About_labelURL_mouseAdapter(DialogAbout adaptee) {
		this.adaptee = adaptee;
	}
	public void mouseClicked(MouseEvent e) {
		adaptee.labelURL_mouseClicked(e);
	}
	public void mouseEntered(MouseEvent e) {
		adaptee.labelURL_mouseEntered(e);
	}
	public void mouseExited(MouseEvent e) {
		adaptee.labelURL_mouseExited(e);
	}
}
