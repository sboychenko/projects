package ru.bserg.pricegen.ui;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ru.bserg.pricegen.Generator;
import ru.bserg.pricegen.commons.PropertiesReader;

import java.awt.Font;


public class ApplicationUI {

	private Logger logger = Logger.getLogger(ApplicationUI.class.getName());
	
	private JFrame frame;
	private JTextField txtInput;
	private JTextField txtOutput;
	private JTextField txtTemplate;
	private JTextField txtPassword;
	private JTextField txtResult;
	private JCheckBox chckbxLock;
	private JCheckBox chckbxAutoOpen;
	private boolean ziroCnt;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApplicationUI window = new ApplicationUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ApplicationUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Создание прайса");
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				PropertiesReader.set("input", txtInput.getText());
				PropertiesReader.set("output", txtOutput.getText());
				PropertiesReader.set("password", txtPassword.getText());
				PropertiesReader.set("lock", Boolean.toString(chckbxLock.isSelected()));
				PropertiesReader.set("open", Boolean.toString(chckbxAutoOpen.isSelected()));
				//PropertiesReader.set("ziro", Boolean.toString(ziroCnt));
				PropertiesReader.store();
			}
			@Override
			public void windowOpened(WindowEvent e) {
				txtInput.setText(PropertiesReader.get("input", "Tovar.txt"));
				txtOutput.setText(PropertiesReader.get("output", "Price.xls"));
				txtTemplate.setText(PropertiesReader.get("template", "template.xls"));
				txtPassword.setText(PropertiesReader.get("password", "password"));
				chckbxLock.setSelected(Boolean.parseBoolean(PropertiesReader.get("lock", "false")));
				chckbxAutoOpen.setSelected(Boolean.parseBoolean(PropertiesReader.get("open", "false")));
				ziroCnt = Boolean.parseBoolean(PropertiesReader.get("ziro", "false"));
			}
		});
		frame.setBounds(100, 100, 353, 148);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnRun = new JButton("Сформировать");
		btnRun.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				txtResult.setText("");
			}
		});
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Generator gen = new Generator(txtInput.getText());
				gen.setPASSWORD(txtPassword.getText());
				gen.setTemplateName(txtTemplate.getText());
				gen.setProtect(chckbxLock.isSelected());
				gen.setBOOLVAR(true);
				gen.setZIROCNT(ziroCnt);
				try {
					gen.process(txtOutput.getText());
					txtResult.setText("Успешно");
					logger.log(Level.INFO, "Успешно");
					if (chckbxAutoOpen.isSelected())
						Desktop.getDesktop().open(new File(txtOutput.getText()));
				} catch (Exception e1) {
					txtResult.setText("Ошибка");
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e1.printStackTrace(pw);
					logger.log(Level.WARNING, sw.toString());
					JOptionPane.showMessageDialog(frame, e1.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
				} 
			}
		});
		btnRun.setBounds(10, 88, 244, 23);
		frame.getContentPane().add(btnRun);
		
		JLabel lblTxt = new JLabel("Входной txt:");
		lblTxt.setBounds(10, 11, 99, 14);
		frame.getContentPane().add(lblTxt);
		
		JLabel lblXls = new JLabel("Выходной xls:");
		lblXls.setBounds(10, 36, 99, 14);
		frame.getContentPane().add(lblXls);
		
		JLabel label = new JLabel("Шаблон:");
		label.setBounds(10, 63, 99, 14);
		frame.getContentPane().add(label);
		
		txtInput = new JTextField();
		txtInput.setBounds(97, 8, 109, 20);
		frame.getContentPane().add(txtInput);
		txtInput.setColumns(10);
		
		txtOutput = new JTextField();
		txtOutput.setColumns(10);
		txtOutput.setBounds(97, 33, 109, 20);
		frame.getContentPane().add(txtOutput);
		
		txtTemplate = new JTextField();
		txtTemplate.setHorizontalAlignment(SwingConstants.LEFT);
		txtTemplate.setColumns(10);
		txtTemplate.setBounds(97, 60, 109, 20);
		frame.getContentPane().add(txtTemplate);
		
		chckbxLock = new JCheckBox("Защитить паролем");
		chckbxLock.setFont(new Font("Tahoma", Font.PLAIN, 10));
		chckbxLock.setBounds(212, 7, 121, 23);
		frame.getContentPane().add(chckbxLock);
		
		txtPassword = new JTextField();
		txtPassword.setBounds(216, 33, 117, 20);
		frame.getContentPane().add(txtPassword);
		txtPassword.setColumns(10);
		
		txtResult = new JTextField();
		txtResult.setEditable(false);
		txtResult.setBounds(216, 60, 117, 20);
		frame.getContentPane().add(txtResult);
		txtResult.setColumns(10);
		
		chckbxAutoOpen = new JCheckBox("открыть?");
		chckbxAutoOpen.setHorizontalAlignment(SwingConstants.RIGHT);
		chckbxAutoOpen.setFont(new Font("Tahoma", Font.PLAIN, 10));
		chckbxAutoOpen.setBounds(260, 88, 73, 23);
		frame.getContentPane().add(chckbxAutoOpen);
	}
}
