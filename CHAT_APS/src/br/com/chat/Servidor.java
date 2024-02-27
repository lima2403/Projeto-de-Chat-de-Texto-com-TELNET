package br.com.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Servidor extends Thread {

	private static ArrayList<BufferedWriter> clientes;
	private static ServerSocket servidor;
	private String nome;
	private Socket conexao;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;

	/**
	 * Método construtor
	 * 
	 * @param conexao do tipo Socket
	 */
	public Servidor(Socket conexao) {
		this.conexao = conexao;
		try {
			inputStream = conexao.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método run para rodar o servidor
	 */
	public void run() {

		try {

			String mensagem;
			OutputStream outputStream = this.conexao.getOutputStream();
			Writer writer = new OutputStreamWriter(outputStream);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			clientes.add(bufferedWriter);
			nome = mensagem = bufferedReader.readLine();

			while (!"Sair".equalsIgnoreCase(mensagem) && mensagem != null) {
				mensagem = bufferedReader.readLine();
				sendToAll(bufferedWriter, mensagem);
				System.out.println(mensagem);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/***
	 * Método chamado para enviar mensagem para os clients conectados
	 * 
	 * @param msgSaida do tipo BufferedWriter
	 * @param msg     do tipo String
	 * @throws IOException
	 */
	public void sendToAll(BufferedWriter msgSaida, String msg) throws IOException {
		BufferedWriter bufferedWriterAuxiliar;

		for (BufferedWriter bufferedWriter : clientes) {
			bufferedWriterAuxiliar = (BufferedWriter) bufferedWriter;
			if (!(msgSaida == bufferedWriterAuxiliar)) {
				bufferedWriter.write(nome + " -> " + msg + "\r\n");
				bufferedWriter.flush();
			}
		}
	}

	/***
	 * Método main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// Cria os objetos necessário para instânciar o servidor
			JLabel lblMessage = new JLabel("Porta do Servidor:");
			JTextField porta = new JTextField("55555");
			Object[] texts = { lblMessage, porta };
			JOptionPane.showMessageDialog(null, texts);
			servidor = new ServerSocket(Integer.parseInt(porta.getText()));
			clientes = new ArrayList<BufferedWriter>();
			JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + porta.getText());

			while (true) {
				System.out.println("Aguardando conexão...");
				Socket con = servidor.accept();
				System.out.println("Cliente conectado...");
				Thread t = new Servidor(con);
				t.start();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

} 
