package motor_gráfico;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import chunks_NoCeldas.Chunks;
import efectos.Iluminación;
import gráficos.Pantalla;
import gráficos.PantallaMultihilo;
import motor_gráfico.menús.MenúPrincipal;
import niveles.Niveles;

import java.awt.Font;
import java.awt.Color;

import objetos.Cámara;
import objetos.Objetos;
import operaciones.MyMath;

public class MotorGráfico3D_Swing implements MotorGráfico {
	private double[] pos;
	private double teta_hori, teta_vert, teta_inclin, delta_teta = Math.PI/8;
	private boolean swBorde, swPrecisión;
	
	private JFrame frmMotorGráfico;
	private JTextField txtRender;
	private JTextField txtInstrucción;
	private JTextField txtCDV;
	private JLabel lblSalida;
	private Pantalla pantalla;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				long start = System.nanoTime();
				MotorGráfico3D_Swing window = new MotorGráfico3D_Swing();
				window.frmMotorGráfico.setVisible(true);
				System.out.println("Ha tardado " + (System.nanoTime() - start) + " nanosegundos en cargar");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MotorGráfico3D_Swing() {
		initialize();
	}

	@Override
	public Pantalla getPantalla() {
		return pantalla;
	}
	
	@Override
	public JLabel getJLabel() {
		return lblSalida;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Objetos.init();
		Niveles.añadirListener(this);
		react();
		Chunks.cargar(pos, 3);
		
		frmMotorGráfico = new JFrame();
		frmMotorGráfico.setTitle("exe.exe - Geoge🅱ruh");
		frmMotorGráfico.setResizable(false);
		frmMotorGráfico.setBounds(100, 1, 738, 723);
		frmMotorGráfico.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMotorGráfico.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setBounds(10, 69, 712, 614);
		frmMotorGráfico.add(panel);
		panel.setLayout(null);
		
		lblSalida = new JLabel("Introduce los datos necesarios");
		lblSalida.setForeground(Color.GREEN);
		lblSalida.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblSalida.setHorizontalAlignment(SwingConstants.CENTER);
		lblSalida.setBounds(0, 0, 712, 614);
		panel.add(lblSalida);
		
		JLabel lblPosición = new JLabel(String.format("Posición inicial (%+.2f,%+.2f,%+.2f)", pos[0], pos[1], pos[2]));
		lblPosición.setBounds(149, 6, 301, 20);
		frmMotorGráfico.add(lblPosición);
		
		txtRender = new JTextField();
		txtRender.setText("10");
		txtRender.setBounds(104, 6, 35, 20);
		frmMotorGráfico.add(txtRender);
		txtRender.setColumns(10);
		
		txtCDV = new JTextField();
		txtCDV.setText("N");
		txtCDV.setBounds(638, 6, 43, 20);
		frmMotorGráfico.add(txtCDV);
		txtCDV.setColumns(10);

		txtInstrucción = new JTextField();
		txtInstrucción.setHorizontalAlignment(SwingConstants.CENTER);
		txtInstrucción.setText("->");
		txtInstrucción.setBounds(104, 32, 35, 23);
		frmMotorGráfico.add(txtInstrucción);
		txtInstrucción.setColumns(10);
		
		MenúPrincipal menú = new MenúPrincipal(this);
		
		JButton btnEjecutar = new JButton("Ejecutar");
		btnEjecutar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean menú_ocultado = ejecutarInput(txtInstrucción.getText().toLowerCase().charAt(0));
				
				if (!menú.estáMostrado() && !menú_ocultado) {
					// Renderizar
					int render = Integer.parseInt(txtRender.getText()) * 5;
					Cámara cámara = new Cámara(pos, render, swPrecisión);
					cámara.setRotaciónYOrient(new double[] {teta_hori, teta_vert, teta_inclin});
					if (txtCDV.getText().trim().toLowerCase().charAt(0) != 'n') cámara.setTeta_CDV(MyMath.fix(Double.parseDouble(txtCDV.getText()) / 180 * Math.PI));
					
					//pantalla = new Pantalla(51, 71, swBorde, cámara); // Más lento, más ligero, 1 hilo
					pantalla = new PantallaMultihilo(51, 71, 4, swBorde, cámara);
					pantalla.setIluminada(Iluminación.hayIluminación());
					
					long start = System.nanoTime();
					pantalla.renderizar();
					lblSalida.setText(pantalla.textoRender());
					System.out.println("Tiempo de dibujado: " + (System.nanoTime() - start) + " nanosegundos");
				}
			}

			private boolean ejecutarInput(char input) {
				if (menú.estáMostrado()) return menú.ejecutarInput(input);
				switch (input) {
				case 'm':
					menú.setMostrado(true);
					break;
				case 'w':
					pos[0] += Math.cos(teta_hori);
					pos[2] += Math.sin(teta_hori);
					break;
				case 'a':
					pos[0] -= Math.sin(teta_hori);
					pos[2] += Math.cos(teta_hori);
					break;
				case 's':
					pos[0] -= Math.cos(teta_hori);
					pos[2] -= Math.sin(teta_hori);
					break;
				case 'd':
					pos[0] += Math.sin(teta_hori);
					pos[2] -= Math.cos(teta_hori);
					break;
				case ' ':
					pos[1]++;
					break;
				case 'c':
					pos[1]--;
					break;
				case 'q':
					teta_hori += delta_teta;
					break;
				case 'e':
					teta_hori -= delta_teta;
					break;
				case 'r':
					teta_vert -= delta_teta;
					break;
				case 't':
					teta_vert += delta_teta;
					break;
				case 'v':
					teta_inclin -= delta_teta;
					break;
				case 'b':
					teta_inclin += delta_teta;
					break;
				default:
					System.out.println("Input no reconocido");
				}
				teta_hori = MyMath.fix(teta_hori);
				teta_vert = MyMath.fix(teta_vert);
				teta_inclin = MyMath.fix(teta_inclin);
				pos = MyMath.fix(pos);
				
				lblPosición.setText(String.format("Posición actual: (%+.2f,%+.2f,%+.2f)", pos[0], pos[1], pos[2]));
				return false;
			}
		});
		btnEjecutar.setBounds(10, 32, 84, 23);
		frmMotorGráfico.add(btnEjecutar);
		
		JLabel lblLeyenda = new JLabel("(WASDC y Espacio = movimiento|QERTVB = rotaciones)");
		lblLeyenda.setBounds(149, 33, 322, 20);
		frmMotorGráfico.add(lblLeyenda);
		
		JLabel lblRenderizado = new JLabel("Renderizado/5:");
		lblRenderizado.setBounds(10, 6, 90, 20);
		frmMotorGráfico.add(lblRenderizado);
		
		JLabel lblCampoDeVisión = new JLabel("Campo de visión (grados|N):");
		lblCampoDeVisión.setBounds(460, 6, 168, 20);
		frmMotorGráfico.add(lblCampoDeVisión);
		
		JButton btnBordes = new JButton("Bordes");
		btnBordes.addActionListener(e -> swBorde = !swBorde);
		btnBordes.setBounds(633, 32, 89, 23);
		frmMotorGráfico.add(btnBordes);
		
		JButton btnPrecisión = new JButton("Precisión: OFF");
		btnPrecisión.addActionListener(e -> {
			if (swPrecisión = !swPrecisión) btnPrecisión.setText("Precisión: ON");
			else btnPrecisión.setText("Precisión: OFF");
		});
		btnPrecisión.setBounds(508, 32, 115, 23);
		frmMotorGráfico.add(btnPrecisión);
	}
	
	@Override
	public void react() {
		pos = Niveles.cargado().getPos();
		double[] ángulos = Niveles.cargado().getRotación();
		teta_hori = ángulos[0];
		teta_vert = ángulos[1];
		teta_inclin = ángulos[2];
	}
}