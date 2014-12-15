import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;




public class CRUD extends JFrame {

	private JFrame frame;
	private JPanel contentPane;
	private JButton btnLoadFile;
	SwingAction sw = new SwingAction();
	private final Action action = sw;
	private JLabel lblNewLabel;
	private JTable table;
	private JMenuBar menubar;
	private JMenu menu;

	public JTextField rulename; //ruleSelectで使用


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
		setBounds(100, 100, 850, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnLoadFile = new JButton("Load File");
		btnLoadFile.setAction(action);
		btnLoadFile.setBounds(325, 12, 107, 25);
		contentPane.add(btnLoadFile);

		lblNewLabel = new JLabel("Select File");
		lblNewLabel.setBounds(12, 17, 295, 15);
		contentPane.add(lblNewLabel);

		// テーブルとモデルを対応付ける
		JTable table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.setBounds(12, 46, 800, 417);
		contentPane.add(table);


		//元のフレームにメニューバーを作成
		menubar = new JMenuBar();
		menu = new JMenu("編集");
		menubar.add(menu);
		menu.setEnabled(false); //ファイルが選択されるまで無効
		JMenuItem menuitem1 = new JMenuItem("追加");
		JMenuItem menuitem2 = new JMenuItem("削除");
		menuitem1.addActionListener(new addListener());
		menuitem2.addActionListener(new deleteListener());
		menu.add(menuitem1);
		menu.add(menuitem2);
		setJMenuBar(menubar);
	}
	private class SwingAction extends AbstractAction {
		File file;
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			JFileChooser filechooser = new JFileChooser("..\\"); // ファイル選択用クラス、初期位置を相対パスで指定
			filechooser.setAcceptAllFileFilterUsed(false); //すべてのファイルから選択の項目を削除
			filechooser.addChoosableFileFilter(new DataFilter()); //txtかdataファイルのみを表示するフィルター

			int selected = filechooser.showOpenDialog(frame); //「開く」ダイアログ表示
			if (selected == JFileChooser.APPROVE_OPTION){ //ファイルが選択されたら
				file = filechooser.getSelectedFile();
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
					String head;

					for(int line=0; line<100; line++) {

						if(str.equals("")){                            // 空白行を無視
							str = br.readLine();
							continue;
						}

						//System.out.println(str);
						head = str.substring(0,2);                     // 読み込んだ行の先頭２文字を抜き出し

						int index = str.indexOf("\"");
						str = str.substring(index+1);                  // ""で囲まれた部分を抜き出し
						str = str.substring(0,str.length()-1);

						if (head.equals("ru")) {                       // rule行の場合
							data[count][0] = str;                      //  1列目に入力
						}
						else if (head.equals("if")){                   // if行の場合
							data[count][1] = str;                      //  2列目に入力
						}
						else if (head.equals("th")){                   // then行の場合
							data[count][2] = str;                      //  3行目に入力
							count++;                                   //  次のルールへ
						}
						else {                                         // if行の二個目以降
							data[count][1] = data[count][1] +','+str;  //  2列目の末尾に追加
						}

						str = br.readLine();                               // 次の行を読み込み
					}
					br.close();

				}catch(FileNotFoundException ee){
					System.out.println(ee);
				}catch(IOException ee){
					System.out.println(ee);
				}catch(NullPointerException eee){
				}

				menu.setEnabled(true); //メニューを有効にする

				model.setRowCount(0);
				for(int i=0; i<data.length; i++) {
					String[] huga = {data[i][0], data[i][1], data[i][2]};
					model.addRow(huga);
				}
			}
		}
		//ファイルの場所を返すメソッド
		String getpath(){
			return file.getAbsolutePath();
		}
	}

	//ファイル選択時のフィルターのクラス
	public class DataFilter extends FileFilter{
		public boolean accept(File f){
			//ディレクトリは無条件で表示する
			if(f.isDirectory()){
				return true;
			}

			//拡張子を取り出し、dataかtxtなら表示する
			String ext = getExtension(f);

			if(ext != null){
				if(ext.equals("data") || ext.equals("txt")){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}

		//項目名のラベル、ファイル選択画面で表示される
		public String getDescription(){
			return "data,txtファイル";
		}

		//拡張子を取り出す
		private String getExtension(File f){
			String ext = null;
			String filename = f.getName();
			int dotIndex = filename.lastIndexOf('.');

			if((dotIndex > 0) && (dotIndex < filename.length() - 1)){
				ext = filename.substring(dotIndex +1).toLowerCase();
			}
			return ext;
		}

	}

	//ルール追加用のダイアログを表示する
	public class addListener extends JDialog implements ActionListener{
		addListener(){
			super(frame,"ルールの追加",true);//閉じるまで元の画面を操作できなくし、ウィンドウ名を記述
		}
		public void actionPerformed(ActionEvent e){
			final JTextField rulename;
			final JTextField rule1;
			final JTextField rule2;
			final JLabel label1;
			final JLabel label2;
			final JLabel label3;
			final JLabel erlabel;
			JButton btnOK;
			JButton btnCANCEL;

			JPanel erpanel = new JPanel(); //エラー表示部分のパネル
			erlabel = new JLabel("追加するルールを入力してください");
			erpanel.add(erlabel);

			JPanel panel = new JPanel(new GridBagLayout()); //ルール書き込み用のパネル
			panel.setLayout(new GridBagLayout());

			GridBagConstraints cs = new GridBagConstraints();

			cs.fill = GridBagConstraints.HORIZONTAL;

			label1 = new JLabel("ルール名");
			cs.gridx = 0;
			cs.gridy = 0;
			cs.gridwidth = 1;
			panel.add(label1,cs);

			rulename = new JTextField(20);
			cs.gridx = 1;
			cs.gridy = 0;
			cs.gridwidth = 1;
			panel.add(rulename,cs);

			label2 = new JLabel("前件");
			cs.gridx = 0;
			cs.gridy = 1;
			cs.gridwidth = 1;
			panel.add(label2,cs);

			rule1 = new JTextField(20);
			cs.gridx = 1;
			cs.gridy = 1;
			cs.gridwidth = 1;
			panel.add(rule1,cs);

			label3 = new JLabel("後件");
			cs.gridx = 0;
			cs.gridy = 2;
			cs.gridwidth = 1;
			panel.add(label3,cs);

			rule2 = new JTextField(20);
			cs.gridx = 1;
			cs.gridy = 2;
			cs.gridwidth = 1;
			panel.add(rule2,cs);

			//ボタンの処理を実装する箇所
			btnOK = new JButton("Ok");
			btnOK.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//記入されていない箇所があるならダイアログ上の文章を変える
					if(rulename.getText().equals("") || rule1.getText().equals("") || rule2.getText().equals("")){
						erlabel.setText("全ての箇所を入力してください");
					}else{
						//ここに処理内容を書く
						String[] rule = new String[3];
						rule[0] = rulename.getText(); //それぞれの入力を取得
						rule[1] = rule1.getText();
						rule[2] = rule2.getText();
						if(adddata(rule) == true){ //ファイルに書き込みができたかでメッセージを変える
							JOptionPane.showMessageDialog(addListener.this,"ルールが追加されました","",JOptionPane.PLAIN_MESSAGE);
						}else{
							JOptionPane.showMessageDialog(addListener.this,"エラー:ファイルを開くことができませんでした","error",JOptionPane.ERROR_MESSAGE);
						}
						dispose(); //元の画面に処理を戻す
					}
				}
			});

			btnCANCEL = new JButton("Cancel");
			btnCANCEL.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//キャンセル側は何も記述しなくてよい
					dispose(); //元の画面に処理を戻す
				}
			});

			JPanel btn = new JPanel(); //ボタン用のパネル
			btn.add(btnOK);
			btn.add(btnCANCEL);

			//3つのパネルをくっつける
			getContentPane().add(erpanel,BorderLayout.PAGE_START);
			getContentPane().add(panel,BorderLayout.CENTER);
			getContentPane().add(btn,BorderLayout.PAGE_END);

			setSize(300,200);
			setVisible(true);
		}
	}

	public class deleteListener extends JDialog implements ActionListener{
		deleteListener(){
			super(frame,"ルールの削除",true);//閉じるまで元の画面を操作できなくし、ウィンドウ名を記述
		}
		public void actionPerformed(ActionEvent e){
			final JLabel label;
			final JLabel erlabel;
			JButton btnModeChange;
			JButton btnOK;
			JButton btnCANCEL;

			JPanel erpanel = new JPanel(); //エラー表示部分のパネル
			erlabel = new JLabel("ルール名を入力してください");
			erpanel.add(erlabel);



			JPanel panel = new JPanel(new GridBagLayout()); //ルール書き込み用のパネル
			panel.setLayout(new GridBagLayout());

			GridBagConstraints cs = new GridBagConstraints();

			cs.fill = GridBagConstraints.HORIZONTAL;

			label = new JLabel("ルール名");
			cs.gridx = 0;
			cs.gridy = 0;
			cs.gridwidth = 1;
			panel.add(label,cs);

			rulename = new JTextField(20);
			cs.gridx = 1;
			cs.gridy = 0;
			cs.gridwidth = 1;
			panel.add(rulename,cs);

			//記入モード変更用のボタン
			btnModeChange = new JButton("リストから選択");
			btnModeChange.addActionListener(new ruleSelect(deleteListener.this));
			cs.gridx = 0;
			cs.gridy = 1;
			cs.gridwidth = 2;
			cs.insets = new Insets(0,120,0,0);
			panel.add(btnModeChange,cs);

			//ボタンの処理を実装する箇所
			btnOK = new JButton("Ok");
			btnOK.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(rulename.getText().equals("")){ //記入されていない箇所があるなら
						erlabel.setText("ルール名を入力してください"); //ダイアログ上の文章を変える
					}else{
						//ここに処理内容を書く
						String[] rule = new String[1];
						rule[0] = rulename.getText(); //ルール名を取得
						switch(deletedata(rule)){ //ファイルから削除できたかでメッセージを変える
						case 0:
							JOptionPane.showMessageDialog(deleteListener.this,"ルールが削除されました","",JOptionPane.PLAIN_MESSAGE);
							rulename.setText("");
							dispose(); //元の画面に処理を戻す
							break;

						case 1:
							JOptionPane.showMessageDialog(deleteListener.this,"エラー:ファイルを開くことができませんでした","error",JOptionPane.ERROR_MESSAGE);
							rulename.setText("");
							dispose(); //元の画面に処理を戻す
							break;
						case 2:
							JOptionPane.showMessageDialog(deleteListener.this,"エラー:削除するルールが見つかりません","error",JOptionPane.ERROR_MESSAGE);
							rulename.setText("");
							break;
						}
					}
				}
			});

			btnCANCEL = new JButton("Cancel");
			btnCANCEL.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//キャンセル側は何も記述しなくてよい
					dispose(); //元の画面に処理を戻す
				}
			});

			JPanel btn = new JPanel(); //ボタン用のパネル
			btn.add(btnOK);
			btn.add(btnCANCEL);

			//3つのパネルをくっつける
			getContentPane().add(erpanel,BorderLayout.PAGE_START);
			getContentPane().add(panel,BorderLayout.CENTER);
			getContentPane().add(btn,BorderLayout.PAGE_END);

			setSize(300,200);
			setVisible(true);
		}
	}

	//ルールをリスト表示する
	public class ruleSelect extends JDialog implements ActionListener{
		ruleSelect(deleteListener dL){
			super(dL,"ルール選択",true);//閉じるまで元の画面を操作できなくし、ウィンドウ名を記述
		}
		public void actionPerformed(ActionEvent e){
			File file = new File(sw.getpath());
			String fileName = file.getName(); //ファイル名指定
			JButton btnOK;
			JButton btnCANCEL;
			int count = 0;
			int rules =countRule(file);
			if(rules <= 0){
				JOptionPane.showMessageDialog(ruleSelect.this,"エラー:ファイルを開くことができませんでした","error",JOptionPane.ERROR_MESSAGE);
				return;
			}
			final String[][] tabledata = new String[rules][1];    
			try { // ファイル読み込みに失敗した時の例外処理のためのtry-catch構文

				// 文字コードを指定してBufferedReaderオブジェクトを作る
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

				// 変数lineに1行ずつ読み込むfor文
				for (String line = in.readLine(); count < rules && line !=null; line = in.readLine()) {
					Matcher m = Pattern.compile("(rule.*?\")(.*?)(\")").matcher(line);
					if (m.find()) {
						tabledata[count][0] = m.group(2);
						count++;
					}
				}
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace(); // 例外が発生した所までのスタックトレースを表示
			}  
			String[] columnName = new String[1];
			columnName[0] ="ルール名";

			final JTable table = new JTable(tabledata, columnName);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			JScrollPane sp = new JScrollPane(table);
			sp.setPreferredSize(new Dimension(400, 200));

			//ボタンの処理を実装する箇所
			btnOK = new JButton("Ok");
			btnOK.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//ここに処理内容を書く
					if(table.getSelectedRow() == -1){
						JOptionPane.showMessageDialog(ruleSelect.this,"削除するルールを選択してください","",JOptionPane.PLAIN_MESSAGE);
					}
					rulename.setText(tabledata[table.getSelectedRow()][0]);
					dispose(); //元の画面に処理を戻す
				}
			});

			btnCANCEL = new JButton("Cancel");
			btnCANCEL.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//キャンセル側は何も記述しなくてよい
					dispose(); //元の画面に処理を戻す
				}
			});

			JPanel btn = new JPanel(); //ボタン用のパネル
			btn.add(btnOK);
			btn.add(btnCANCEL);

			JPanel panel = new JPanel();
			panel.add(sp);
			setBounds(100, 100, 450, 300);

			//2つのパネルをくっつける
			getContentPane().add(panel, BorderLayout.CENTER);
			getContentPane().add(btn,BorderLayout.PAGE_END);
			setVisible(true);         
		}

		int countRule(File file){
			String fileName = file.getName(); //ファイル名指定
			int count=0;

			try { // ファイル読み込みに失敗した時の例外処理のためのtry-catch構文

				// 文字コードを指定してBufferedReaderオブジェクトを作る
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

				// 変数lineに1行ずつ読み込むfor文
				for (String line = in.readLine(); line != null; line = in.readLine()) {
					if(line.matches(".*rule.*")){
						count++;
					}
				}
				in.close();
				return count;
			} catch (IOException e) {
				e.printStackTrace(); // 例外が発生した所までのスタックトレースを表示
				return -1;
			}
		}
	}

	//ルール追加用のメソッド
	boolean adddata(String args[]){
		if(args.length != 3){
			System.out.println("例　Adddata \"CarRule20\" \"?x is inexpensive,?x is red\" \"?x is made_in_JP\"");
		}
		else{
			try{
				File file = new File(sw.getpath());                    //書き込み先を取得する

				if (checkBeforeWritefile(file)){
					FileWriter filewriter = new FileWriter(file, true); //現在ある文書に追加書き込み

					filewriter.write("\n");
					filewriter.write("rule  \""+args[0]+"\"\n");      //ルール番号を書き込む

					String[] str = args[1].split(",",0);                //ルールの前件集合を分割

					filewriter.write("if        \""+str[0]+"\"\n");       //前件を一行ずつ書き込む

					for(int i=1;i<str.length;i++){
						filewriter.write("      \""+str[i]+"\"\n");
						
						//テーブル更新
						String[] huga = {args[0], str[i], args[2]};
						model.addRow(args);
					}
 
					filewriter.write("then  \""+args[2]+"\"\n");      //ルールの後件を書き込む

					filewriter.close();
					
					
					
//					String[] rule = new String[3];
//					rule[0] = rulename.getText(); //それぞれの入力を取得
//					rule[1] = rule1.getText();
//					rule[2] = rule2.getText();
					
//					model.setRowCount(0);
//					for(int i=0; i<data.length; i++) {
//						String[] huga = {data[i][0], data[i][1], data[i][2]};
//						model.addRow(huga);
//					}

					return true;
				}else{
					return false;      //書き込みができない場合のエラー
				}

			}catch(IOException e){
				System.out.println(e);    //例外処理
			}
		}
		return false;
	}

	//ルール削除用のメソッド
	int deletedata(String arg[]){

		boolean delete = false;
		boolean finish = false;
		String[] buffer = new String[200];
		int i=0;


		if(arg.length != 1){
			return 1;
		}
		else{
			File file = new File(sw.getpath());
			String fileName = file.getName(); //ファイル名指定

			try { // ファイル読み込みに失敗した時の例外処理のためのtry-catch構文

				// 文字コードを指定してBufferedReaderオブジェクトを作る
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

				// 変数lineに1行ずつ読み込むfor文
				for (String line = in.readLine(); line != null; line = in.readLine()) {

					if(finish == true){ // 削除後の処理
						buffer[i++] = line;
					}
					else if((delete == true)&&(line.matches(".*rule.*"))){ //削除部分終了時の処理
						finish = true;
						buffer[i++] = line; //bufferに書き込み
					}
					else if(delete == true){ //削除中
						continue; //削除部分はbufferに書き込まない
					}
					else if((line.matches(".*rule.*"))&&(line.matches(".*"+arg[0]+".*"))){
						delete = true; //削除部分発見
					}
					else{
						buffer[i++] = line; //bufferに書き込み
					}
				}

				in.close();
				if(delete == true){ //削除部分がある場合               
					if (checkBeforeWritefile(file)){
						FileWriter filewriter = new FileWriter(file);

						for(int j=0;j<i;j++){
							filewriter.write(buffer[j]+'\n'); //bufferに保存したデータに書き換える
						}
						filewriter.close();
						return 0;

					}else{
						return 1;
					}

				}else{
					return 2; //削除部分がない場合
				}

			} catch (IOException e) {
				e.printStackTrace(); // 例外が発生した所までのスタックトレースを表示
			}  
		}
		return 1;
	}

	//ファイルに書き込み可能かどうかを調べる
	private static boolean checkBeforeWritefile(File file){
		if (file.exists()){ //ファイルが存在し
			if (file.isFile() && file.canWrite()){ // ディレクトリではなく、かつ書き込み可能
				return true;
			}
		}

		return false;
	}
}