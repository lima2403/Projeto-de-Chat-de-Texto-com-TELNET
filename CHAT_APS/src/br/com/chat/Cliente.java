package br.com.chat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Cliente extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JTextArea areaTexto;
	private JTextField mensagem;
	private JButton botaoEnviar;
	private JButton botaoSair;
	private JLabel labelHistorico;
	private JLabel labelMensagem;
	private JPanel chatPanel;
	private Socket socket;
	private OutputStream outputStream;
	private Writer outputWriter;
	private BufferedWriter bufferedWriter;
	private JTextField ip;
	private JTextField porta;
	private JTextField nome;

	public Cliente() throws IOException {
		JLabel labelMessage = new JLabel("Dados de Conexão!");
		ip = new JTextField("127.0.0.1");
		porta = new JTextField("55555");
		nome = new JTextField("Nome");
		Object[] textos = { labelMessage, ip, porta, nome };
		JOptionPane.showMessageDialog(null, textos);
		chatPanel = new JPanel();
		areaTexto = new JTextArea(10, 20);
		areaTexto.setEditable(false);
		areaTexto.setBackground(new Color(240, 240, 240));
		mensagem = new JTextField(20);
		labelHistorico = new JLabel("Histórico");
		labelMensagem = new JLabel("Mensagem");
		botaoEnviar = new JButton("Enviar");
		botaoEnviar.setToolTipText("Enviar Mensagem");
		botaoSair = new JButton("Sair");
		botaoSair.setToolTipText("Sair do Chat");
		botaoEnviar.addActionListener(this);
		botaoSair.addActionListener(this);
		botaoEnviar.addKeyListener(this);
		mensagem.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(areaTexto);
		areaTexto.setLineWrap(true);
		chatPanel.add(labelHistorico);
		chatPanel.add(scroll);
		chatPanel.add(labelMensagem);
		chatPanel.add(mensagem);
		chatPanel.add(botaoSair);
		chatPanel.add(botaoEnviar);
		chatPanel.setBackground(Color.LIGHT_GRAY);
		areaTexto.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		mensagem.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		setTitle(nome.getText());
		setContentPane(chatPanel);
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(250, 300);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/***
	 * Método usado para conectar ao servidor
	 * 
	 * @throws IOException
	 */
	public void conectar() throws IOException {

		socket = new Socket(ip.getText(), Integer.parseInt(porta.getText()));
		outputStream = socket.getOutputStream();
		outputWriter = new OutputStreamWriter(outputStream);
		bufferedWriter = new BufferedWriter(outputWriter);
		bufferedWriter.write(nome.getText() + "\r\n");
		bufferedWriter.flush();
	}

	/***
	 * Método chamado para enviar mensagem para o servidor
	 * 
	 * @param msg do tipo String
	 * @throws IOException
	 */
	public void enviarMensagem(String msg) throws IOException {

		if (msg.equals("Sair")) {
			bufferedWriter.write("Desconectado \r\n");
			areaTexto.append("Desconectado \r\n");
		} else {
			bufferedWriter.write(msg + "\r\n");
			areaTexto.append(nome.getText() + " diz -> " + mensagem.getText() + "\r\n");
		}
		bufferedWriter.flush();
		mensagem.setText("");
	}

	/**
	 * Método chamado para escutar e receber as mensagens do servidor
	 * 
	 * @throws IOException
	 */
	public void escutar() throws IOException {

		InputStream in = socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";

		while (!"Sair".equalsIgnoreCase(msg))

			if (bfr.ready()) {
				msg = bfr.readLine();
				if (msg.equals("Sair"))
					areaTexto.append("Servidor caiu! \r\n");
				else
					areaTexto.append(msg + "\r\n");
			}
	}

	/***
	 * Método chamado quando o botão sair é clicado
	 * 
	 * @throws IOException
	 */
	public void sair() throws IOException {

		enviarMensagem("Sair");
		bufferedWriter.close();
		outputWriter.close();
		outputStream.close();
		socket.close();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getActionCommand().equals(botaoEnviar.getActionCommand()))
				enviarMensagem(mensagem.getText());
			else if (e.getActionCommand().equals(botaoSair.getActionCommand()))
				sair();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				enviarMensagem(mensagem.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public static void main(String[] args) throws IOException {

		Cliente app = new Cliente();
		app.conectar();
		app.escutar();
	}
}
