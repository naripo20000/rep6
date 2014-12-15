import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JButton;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class CRUD extends JFrame {

	private JFrame frame;
	private JPanel contentPane;
	private JButton btnLoadFile;
	private final Action action = new SwingAction();
	private JLabel lblNewLabel;
	private JTable table;

	
	String[] columnNames = { "rule", "if", "then"};

	String[][] tabledata;
	

	DefaultTableModel model =  new DefaultTableModel(tabledata, columnNames);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CRUD frame = new CRUD();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CRUD() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnLoadFile = new JButton("Load File");
		btnLoadFile.setAction(action);
		btnLoadFile.setBounds(325, 12, 107, 25);
		contentPane.add(btnLoadFile);

		lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(12, 17, 295, 15);
		contentPane.add(lblNewLabel);
		
		// テーブルとモデルを対応付ける
		JTable table = new JTable(model);

		table.setBounds(12, 46, 420, 217);
		contentPane.add(table);
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			JFileChooser filechooser = new JFileChooser(); // ファイル選択用クラス

			int selected = filechooser.showOpenDialog(frame); //「開く」ダイアログ表示
			if (selected == JFileChooser.APPROVE_OPTION){ //ファイルが選択されたら
				File file = filechooser.getSelectedFile(); 
				lblNewLabel.setText(file.getName()); //ラベルの文字をファイル名に

				String data[][] = new String[99][3];

				//ファイルの読み込み
				try{
					//ファイルのアドレスよりbrにファイルの情報を格納
					FileReader filereader = new FileReader(file);
					BufferedReader br = new BufferedReader(filereader);

					//一行ずつ読み込んで
					String str = br.readLine();
					int count = 0;
					
					for(int line=0; line<30; line++) {
					}

					for(int line=0; line<30; line++) {
						if(line%4 != 3) {
							if(str == null) break;
							data[count][line%4] = str;

							Matcher m = Pattern.compile("rule\b\t(.*?)").matcher(str);
							if (m.matches()) {
								data[count][line%4] = m.group(0);
								System.out.println(m.group(0));
							}
						} else {
							count++;
						}
						//次の行を読み込み
						str = br.readLine();
					}

					br.close();
				}catch(FileNotFoundException ee){
					System.out.println(ee);
				}catch(IOException ee){
					System.out.println(ee);
				}

				for(int i=0; i<data.length; i++) {
		        	System.out.println(data[i][0]);
		        	String[] huga = {data[i][0], data[i][1], data[i][2]};
		        	model.addRow(huga);
		        }
			}
		}
	}	
}
